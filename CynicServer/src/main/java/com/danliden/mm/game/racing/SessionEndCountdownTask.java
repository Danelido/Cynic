package com.danliden.mm.game.racing;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.utils.Execution;
import org.json.JSONObject;

public class SessionEndCountdownTask extends Execution {

    private final Properties properties;

    public SessionEndCountdownTask(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void run() {
        startRace();
    }

    private void startRace() {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.START_RACE);
        properties.sender.sendToMultipleWithAck(
                properties.ackHandler,
                packet,
                properties.sessionPlayers.getPlayers(),
                5,
                150);
    }

}
