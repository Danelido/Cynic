package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.utils.UniqueId;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionAckHandler {
    private final List<AckEntity> ackList = new ArrayList<>();
    private final UniqueId ackIDGenerator;
    private final PacketSender sender;

    public SessionAckHandler(PacketSender sender) {
        ackIDGenerator = new UniqueId(500000);
        this.sender = sender;
    }

    public void update(float updateInterval) {
        synchronized (ackList) {
            final int startIndex = ackList.size() - 1;
            for (int i = startIndex; i >= 0; i--) {
                AckEntity ackEntity = ackList.get(i);
                ackEntity.addTimeSinceLastSend(updateInterval);

                if (ackEntity.getIntervalMS() <= ackEntity.getTimeSinceLastSend()) {
                    sender.send(ackEntity.getOutgoingData(), ackEntity.getClient().address, ackEntity.getClient().port);
                    ackEntity.resetTimeSinceLastSend();
                    ackEntity.incrementTry();

                    if (ackEntity.getCurrentTry() >= ackEntity.getMaxTries()) {
                        // Give up
                        ackIDGenerator.giveBackID(ackEntity.getAckId());
                        ackList.remove(i);
                    }
                }

            }
        }
    }

    public boolean handleIfPacketIsAck(JSONObject packetData) {
        boolean isPacketAck;

        try {
            final int ackId = packetData.getInt(ValidPacketDataKeys.AckId);
            isPacketAck = true;

            synchronized (ackList) {
                for (int i = 0; i < ackList.size(); i++) {
                    AckEntity ack = ackList.get(i);
                    if (ack.getAckId() == ackId) {
                        ackIDGenerator.giveBackID(ack.getAckId());
                        ackList.remove(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            isPacketAck = false;
        }

        return isPacketAck;
    }

    public AckEntity buildAckEntity(JSONObject jsonData, PlayerClient receiver, int maxTries, int intervalMS) {
        final int ackId = ackIDGenerator.getId();
        jsonData.put(ValidPacketDataKeys.AckId, ackId);

        final byte[] data = jsonData.toString().getBytes();

        return new AckEntity(
                ackId,
                receiver,
                data,
                intervalMS,
                maxTries);
    }

    public void registerAckEntity(AckEntity ackEntity) {
        if (!ackList.contains(ackEntity)) {
            ackList.add(ackEntity);
        }
    }

    public int getNumAcksInProcess() {
        return ackList.size();
    }
}
