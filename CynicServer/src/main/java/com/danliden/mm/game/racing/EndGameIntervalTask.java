package com.danliden.mm.game.racing;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.utils.Execution;
import org.json.JSONObject;

public class EndGameIntervalTask extends Execution {
    private Properties properties;
    private int currentTime;

    public EndGameIntervalTask(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void setCurrentTime(int currentTimeMs) {
        currentTime = currentTimeMs;
    }

    @Override
    public void run() {
        sendCountdownPacket();
    }

    private void sendCountdownPacket() {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.END_OF_LOOKING_AT_PLACEMENTS_TIMER);
        packet.put(PacketKeys.EndOfRaceTimer, currentTime);
        properties.sender.sendToMultipleWithAck(
                properties.ackHandler,
                packet,
                properties.sessionPlayers.getPlayers(),
                5,
                150);
    }

}
