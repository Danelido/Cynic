package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.packet.logic.*;
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
    private final GameState currentState;
    private final SessionAckHandler ackHandler;
    private final PacketSender sender;

    public GameSession(PacketSender sender, int sessionID) {
        this.sender = sender;
        SESSION_ID = sessionID;
        sessionPlayers = new SessionPlayers(MAX_PLAYERS);
        currentState = GameState.LOBBY;
        ackHandler = new SessionAckHandler(sender);
        mapPacketLogic();
    }

    public void onServerUpdate(final int updateInterval) {
        checkClientsHeartbeat();
        ackHandler.update(updateInterval);
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
                    .getInt(ValidPacketDataKeys.PacketId);

            IPacketLogic logic = packetLogicMapping.getOrDefault(pid, null);
            if (logic != null) {
                logic.execute(bundle, sender, ackHandler, sessionPlayers, currentState);
            }
        }

    }

    private void checkClientsHeartbeat() {
        for (PlayerClient client : sessionPlayers.getPlayers()) {
            if (client.nrOfFlatLines >= MAX_FLAT_LINES) {
                disconnectPlayer(client);
            }
        }
    }

    private void disconnectPlayer(PlayerClient client) {
        JSONObject disconnectPackage = new JSONObject()
                .put(ValidPacketDataKeys.PacketId, PacketType.Outgoing.LOST_CLIENT)
                .put(ValidPacketDataKeys.PlayerId, client.id);
        sessionPlayers.removePlayer(client.id);
        sender.sendToMultipleWithAck(ackHandler, disconnectPackage, sessionPlayers.getPlayers(), 30, 2000);
    }

    private void mapPacketLogic() {
        packetLogicMapping.put(PacketType.Incoming.JOIN_REQUEST, new JoinSession());
        packetLogicMapping.put(PacketType.Incoming.LEFT_SESSION, new LeaveSession());
        packetLogicMapping.put(PacketType.Incoming.HEARTBEAT, new Heartbeat());
        packetLogicMapping.put(PacketType.Incoming.CLIENT_UPDATE, new UpdatePlayer());
    }

    private void sendHeartbeats() {
        JSONObject obj = new JSONObject()
                .put(ValidPacketDataKeys.PacketId, PacketType.Outgoing.HEARTBEAT_REQUEST);
        sender.sendToMultiple(obj, sessionPlayers.getPlayers());
    }

    public boolean isFull() {
        return sessionPlayers.isFull();
    }

    public boolean isJoinAble() {
        return currentState == GameState.LOBBY && !isFull();
    }

    public final int getSessionId() {
        return SESSION_ID;
    }

}
