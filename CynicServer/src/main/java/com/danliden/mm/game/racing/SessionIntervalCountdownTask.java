package com.danliden.mm.game.racing;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.utils.Execution;
import org.json.JSONObject;

public class SessionIntervalCountdownTask extends Execution {
    private Properties properties;
    private long currentTime;

    public SessionIntervalCountdownTask(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void setCurrentTime(long currentTimeMs) {
        currentTime = currentTimeMs;
    }

    @Override
    public void run() {
        sendCountdownPacket();
    }

    private void sendCountdownPacket() {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.SESSION_COUNTDOWN);
        packet.put(PacketKeys.SessionCountdown, currentTime);
        properties.sender.sendToMultipleWithAck(
                properties.ackHandler,
                packet,
                properties.sessionPlayers.getPlayers(),
                5,
                200);
    }


}
