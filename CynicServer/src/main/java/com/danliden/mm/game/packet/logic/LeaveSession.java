package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

public class LeaveSession implements IPacketLogic {
    @Override
    public void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState gameState) {
        final int playerId = bundle
                .getPacketJsonData()
                .getInt(ValidPacketDataKeys.PlayerId);


        PlayerClient client = sessionPlayers.findById(playerId);
        if (client != null) {
            JSONObject disconnectPackage = buildDisconnectPackageAsJson(client);
            sessionPlayers.removePlayer(client.id);
            sender.sendToMultipleWithAck(ackHandler, disconnectPackage, sessionPlayers.getPlayers(), 30, 2000);
        } else {
            sender.sendNotConnectedPacketToSender(bundle);
        }
    }

    private JSONObject buildDisconnectPackageAsJson(PlayerClient client) {
        return new JSONObject()
                .put(ValidPacketDataKeys.PacketId, PacketType.Outgoing.LOST_CLIENT)
                .put(ValidPacketDataKeys.PlayerId, client.id);
    }

}
