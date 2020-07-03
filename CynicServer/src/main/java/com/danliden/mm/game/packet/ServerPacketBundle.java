package com.danliden.mm.game.packet;

import org.json.JSONObject;

import java.net.DatagramPacket;

public class ServerPacketBundle {
    private final DatagramPacket datagramPacket;
    private JSONObject packetJsonData;
    private int sessionId = -1;

    public ServerPacketBundle(DatagramPacket dgPacket) {
        this.datagramPacket = dgPacket;
    }

    public ServerPacketBundle build() {
        String data = new String(datagramPacket.getData());
        packetJsonData = new JSONObject(data);
        sessionId = packetJsonData.getInt(PacketKeys.SessionID);

        return this;
    }

    public int getSessionId() {
        return sessionId;
    }

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public JSONObject getPacketJsonData() {
        return packetJsonData;
    }
}
