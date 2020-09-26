package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.logic.*;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.game.racing.*;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.TimeMeasurement;
import com.danliden.mm.utils.TimeUnits;
import com.danliden.mm.utils.TimedExecution;
import org.json.JSONObject;

import java.util.*;

public class GameSession {

    private final Map<Integer, IPacketLogic> packetLogicMapping = new HashMap<>();

    static final int MAX_PLAYERS = 4;
    static final int MAX_FLAT_LINES = 8;
    int TIME_UNTIL_LOBBY_FROM_SCOREBOARD_MS = 10000;
    final int SESSION_ID;

    private final SessionPlayers sessionPlayers;
    private final GameState gameState;
    private final SessionAckHandler ackHandler;
    private final PacketSender sender;
    private final TrackManager trackManager;
    private final DoomTimer doomTimer;
    final Properties properties;

    public GameSession(PacketSender sender, int sessionID) {
        this.sender = sender;
        SESSION_ID = sessionID;
        sessionPlayers = new SessionPlayers(MAX_PLAYERS);
        gameState = new GameState();
        ackHandler = new SessionAckHandler(sender);
        trackManager = new TrackManager();
        doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, sender);
        properties = new Properties();
        mapPacketLogic();
    }

    public void onServerUpdate(final int updateIntervalMs) {
        checkIfShouldExitToLobby();
        checkClientsHeartbeat();
        ackHandler.update(updateIntervalMs);
        doomTimer.update(updateIntervalMs);

        if (doomTimer.getCurrentState() == DoomTimer.State.FINISHED) {
            doomTimer.stop();
            gameState.setGameState(GameState.GameStateEnum.IN_SESSION_END);
            sendEndOfRacePacketWithPlacements();
            startEndGameTasks(TIME_UNTIL_LOBBY_FROM_SCOREBOARD_MS);
        }
    }

    private void checkIfShouldExitToLobby() {
        if(gameState.getGameState() != GameState.GameStateEnum.LOBBY && sessionPlayers.getNumberOfPlayers() == 0){
            // Maybe clear ackhandler and all that as well?
            gameState.setGameState(GameState.GameStateEnum.LOBBY);
        }
    }

    private void sendEndOfRacePacketWithPlacements() {
        Placements placements = new Placements();
        List<PlayerClient> placementList = placements.getPlacementsFromLocalPositions(sessionPlayers.getPlayers());
        JSONObject doomTimerEndPacket = new JSONObject();
        StringBuilder placementsString = buildPlacementString(placementList);
        doomTimerEndPacket.put(PacketKeys.PacketId, PacketType.Outgoing.END_OF_RACE);
        doomTimerEndPacket.put(PacketKeys.PlacementUpdate, placementsString.toString());
        sender.sendToMultipleWithAck(ackHandler, doomTimerEndPacket, sessionPlayers.getPlayers(), 10, 500);
    }

    private StringBuilder buildPlacementString(List<PlayerClient> placementList) {
        StringBuilder orderedPlayerIds = new StringBuilder();
        for (PlayerClient playerClient : placementList) {
            orderedPlayerIds.append(playerClient.id).append(",");
        }
        return orderedPlayerIds;
    }

    private void startEndGameTasks(int time) {
        new TimedExecution()
                .setTime(time)
                .setIntervalExecution(new EndGameIntervalTask(properties), TimeMeasurement.of(500, TimeUnits.MILLISECONDS))
                .setPostTimerExecution(new EndGameTask(properties))
                .start();
    }

    public void onServerHeartbeat() {
        for (PlayerClient client : sessionPlayers.getPlayers()) {
            client.addFlatline();
        }
        sendHeartbeats();
    }

    public void handleData(ServerPacketBundle bundle) {
        // If this packet is not an ack then execute packet logic
        if (!ackHandler.handleIfPacketIsAck(bundle.getPacketJsonData())) {
            int pid = bundle.getPacketJsonData()
                    .getInt(PacketKeys.PacketId);

            IPacketLogic logic = packetLogicMapping.getOrDefault(pid, null);
            if (logic != null) {
                updateProperties(bundle);
                logic.execute(properties);
            }
        }

    }

    void updateProperties(ServerPacketBundle bundle) {
        properties.setBundle(bundle)
                .setPacketSender(sender)
                .setSessionAckHandler(ackHandler)
                .setSessionPlayers(sessionPlayers)
                .setGameState(gameState)
                .setCheckpointsManager(trackManager)
                .setDoomTimer(doomTimer);
    }

    private void checkClientsHeartbeat() {
        for (int i = sessionPlayers.getNumberOfPlayers() - 1; i >= 0; i--) {
            PlayerClient client = sessionPlayers.getPlayers().get(i);
            if (client.getNrOfFlatLines() >= MAX_FLAT_LINES) {
                disconnectPlayer(client);
            }
        }
    }

    private void disconnectPlayer(PlayerClient client) {
        JSONObject disconnectPackage = new JSONObject()
                .put(PacketKeys.PacketId, PacketType.Outgoing.LOST_CLIENT)
                .put(PacketKeys.PlayerId, client.id);
        sessionPlayers.removePlayer(client.id);
        sender.sendToMultipleWithAck(ackHandler, disconnectPackage, sessionPlayers.getPlayers(), 30, 2000);
    }

    private void mapPacketLogic() {
        packetLogicMapping.put(PacketType.Incoming.JOIN_REQUEST, new JoinSession());
        packetLogicMapping.put(PacketType.Incoming.LEFT_SESSION, new LeaveSession());
        packetLogicMapping.put(PacketType.Incoming.HEARTBEAT, new Heartbeat());
        packetLogicMapping.put(PacketType.Incoming.CLIENT_UPDATE, new UpdatePlayer());
        packetLogicMapping.put(PacketType.Incoming.VOTE_TO_START_SESSION, new VoteToStartSession());
        packetLogicMapping.put(PacketType.Incoming.REMOVE_VOTE_TO_START_SESSION, new RemoveVoteToStartSession());
        packetLogicMapping.put(PacketType.Incoming.PLAYER_SHIP_CHANGE, new ShipChange());
    }

    private void sendHeartbeats() {
        JSONObject obj = new JSONObject()
                .put(PacketKeys.PacketId, PacketType.Outgoing.HEARTBEAT_REQUEST);
        sender.sendToMultiple(obj, sessionPlayers.getPlayers());
    }

    public boolean isFull() {
        return sessionPlayers.isFull();
    }

    public boolean isJoinAble() {
        return gameState.getGameState() == GameState.GameStateEnum.LOBBY && !isFull();
    }

    public final int getSessionId() {
        return SESSION_ID;
    }

}
