package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.logic.*;
import com.danliden.mm.game.racing.CheckpointManager;
import com.danliden.mm.game.racing.DoomTimer;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameSession {

    private final Map<Integer, IPacketLogic> packetLogicMapping = new HashMap<>();

    private static final int MAX_PLAYERS = 4;
    private static final int MAX_FLAT_LINES = 20;
    private final int SESSION_ID;

    private final SessionPlayers sessionPlayers;
    private final GameState gameState;
    private final SessionAckHandler ackHandler;
    private final PacketSender sender;
    private final Properties properties;
    private final CheckpointManager checkpointManager;
    private final DoomTimer doomTimer;

    public GameSession(PacketSender sender, int sessionID) {
        this.sender = sender;
        SESSION_ID = sessionID;
        sessionPlayers = new SessionPlayers(MAX_PLAYERS);
        gameState = new GameState();
        ackHandler = new SessionAckHandler(sender);
        checkpointManager = new CheckpointManager();
        doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, sender);
        properties = new Properties();
        mapPacketLogic();
    }

    public void onServerUpdate(final int updateIntervalMs) {
        checkClientsHeartbeat();
        ackHandler.update(updateIntervalMs);
        doomTimer.update(updateIntervalMs);

        if(doomTimer.getCurrentState() == DoomTimer.State.FINISHED){
            doomTimer.stop();
            gameState.setGameState(GameState.GameStateEnum.IN_SESSION_END);
            /* Todo:
                - Send packet to clients indicating that it is over with all the placements
                - Start a timer task of 10(?) seconds
                    - When time is up, set state to IN_LOBBY
                    - Reset everything worth resetting.
                    - Write unit tests!
            */
        }
    }

    public void onServerHeartbeat() {
        // Increase flat lines
        for (PlayerClient client : sessionPlayers.getPlayers()) {
            client.addFlatline();
        }
        // Send out heartbeats
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

    private void updateProperties(ServerPacketBundle bundle){
        properties.setBundle(bundle)
                .setPacketSender(sender)
                .setSessionAckHandler(ackHandler)
                .setSessionPlayers(sessionPlayers)
                .setGameState(gameState)
                .setCheckpointsManager(checkpointManager)
                .setDoomTimer(doomTimer);
    }

    private void checkClientsHeartbeat() {
        for (PlayerClient client : sessionPlayers.getPlayers()) {
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
