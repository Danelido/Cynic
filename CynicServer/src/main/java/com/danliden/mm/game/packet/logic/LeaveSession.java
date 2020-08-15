package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.session.PlayerClient;
import org.json.JSONObject;

public class LeaveSession implements IPacketLogic {
    @Override
    public void execute(Properties props) {
        final int playerId = props.bundle
                .getPacketJsonData()
                .getInt(PacketKeys.PlayerId);


        PlayerClient client = props.sessionPlayers.findById(playerId);
        if (client != null) {
            JSONObject disconnectPackage = buildDisconnectPackageAsJson(client);
            props.sessionPlayers.removePlayer(client.id);
            props.sender.sendToMultipleWithAck(props.ackHandler, disconnectPackage, props.sessionPlayers.getPlayers(), 30, 2000);
        } else {
            props.sender.sendNotConnectedPacketToSender(props.bundle);
        }
    }

    private JSONObject buildDisconnectPackageAsJson(PlayerClient client) {
        return new JSONObject()
                .put(PacketKeys.PacketId, PacketType.Outgoing.LOST_CLIENT)
                .put(PacketKeys.PlayerId, client.id);
    }

}
