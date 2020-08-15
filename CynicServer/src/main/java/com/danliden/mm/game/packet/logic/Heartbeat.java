package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.session.PlayerClient;

public class Heartbeat implements IPacketLogic {
    @Override
    public void execute(Properties props) {
        final int id = props.bundle
                .getPacketJsonData()
                .getInt(PacketKeys.PlayerId);

        PlayerClient client = props.sessionPlayers.findById(id);
        if (client != null) {
            client.resetFlatline();
        } else {
            props.sender.sendNotConnectedPacketToSender(props.bundle);
        }
    }
}
