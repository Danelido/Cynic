package com.danliden.mm.game.server;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.session.AckEntity;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class PacketSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DatagramSocket socket;

    public PacketSender(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendWithAck(SessionAckHandler ackHandler, JSONObject jsonData, PlayerClient client, int maxTries, int intervalMS) {
        AckEntity ackEntity = ackHandler.buildAckEntity(jsonData, client, maxTries, intervalMS);
        ackHandler.registerAckEntity(ackEntity);
        send(ackEntity.getOutgoingData(), ackEntity.getClient().address, ackEntity.getClient().port);
    }

    public void sendToMultipleWithAck(SessionAckHandler ackHandler, JSONObject jsonData, List<PlayerClient> clients, int maxTries, int intervalMS) {
        for (PlayerClient client : clients) {
            AckEntity ackEntity = ackHandler.buildAckEntity(jsonData, client, maxTries, intervalMS);
            ackHandler.registerAckEntity(ackEntity);
            send(ackEntity.getOutgoingData(), ackEntity.getClient().address, ackEntity.getClient().port);
        }
    }

    public void sendToMultipleWithAckAndExclude(SessionAckHandler ackHandler, JSONObject jsonData, List<PlayerClient> clients, int maxTries, int intervalMS, PlayerClient excludedClient) {
        for (PlayerClient client : clients) {
            if (client.id != excludedClient.id) {
                AckEntity ackEntity = ackHandler.buildAckEntity(jsonData, client, maxTries, intervalMS);
                ackHandler.registerAckEntity(ackEntity);
                send(ackEntity.getOutgoingData(), ackEntity.getClient().address, ackEntity.getClient().port);
            }
        }
    }

    public void sendToMultiple(JSONObject data, List<PlayerClient> clients) {
        for (PlayerClient client : clients) {
            send(data.toString().getBytes(), client.address, client.port);
        }
    }

    public void sendToMultipleWithExclude(JSONObject data, List<PlayerClient> clients, PlayerClient excludedClient) {
        for (PlayerClient client : clients) {
            if (client.id != excludedClient.id) {
                send(data.toString().getBytes(), client.address, client.port);
            }
        }
    }

    public void sendNotConnectedPacketToSender(ServerPacketBundle bundle) {
        JSONObject jsonData = new JSONObject().put(PacketKeys.PacketId, PacketType.Outgoing.NOT_CONNECTED);
        sendToAddress(jsonData, bundle.getDatagramPacket().getAddress(), bundle.getDatagramPacket().getPort());
    }

    public void sendSomethingWentWrongServerError(SessionAckHandler ackHandler, List<PlayerClient> clients, String fault){
        JSONObject jsonData = new JSONObject();
        jsonData.put(PacketKeys.PacketId, PacketType.Outgoing.SERVER_ERROR);
        jsonData.put(PacketKeys.ServerError, fault);
        sendToMultipleWithAck(ackHandler, jsonData, clients, 10, 500);
    }

    public void sendToAddress(JSONObject data, InetAddress address, int port) {
        send(data.toString().getBytes(), address, port);
    }

    public void send(final byte[] data, final InetAddress address, final int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        try {
            socket.send(packet);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }


}
