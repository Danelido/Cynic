package com.danliden.mm.utils;

import com.danliden.mm.execution.Main;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UdpClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DatagramSocket socket;
    private InetAddress ipAddress;
    private final int maxPacketSize = 4096;
    private Thread listenerThread;
    private final List<JSONObject> receivedPackets = new ArrayList<>();

    public UdpClient() {

    }

    public void connect() throws Exception {
        socket = new DatagramSocket();
        ipAddress = InetAddress.getByName(Main.SERVER_ADDRESS);
        packetListener();
    }

    public void close() {
        socket.close();
        listenerThread.interrupt();
    }

    public void send(JSONObject jsonPacket, int port) throws IOException {
        String stringFromJson = jsonPacket.toString();
        DatagramPacket packet = new DatagramPacket(
                stringFromJson.getBytes(),
                stringFromJson.length(),
                ipAddress,
                port);

        socket.send(packet);
    }

    public JSONObject popPacket() {
        synchronized (receivedPackets) {
            int nrOfPackets = receivedPackets.size();

            if (nrOfPackets > 0) {
                return receivedPackets.remove(nrOfPackets - 1);
            }

            return null;
        }
    }

    private void packetListener() {
        listenerThread = new Thread("listener") {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    try {
                        byte[] byteData = new byte[maxPacketSize];
                        DatagramPacket dataPacket = new DatagramPacket(byteData, byteData.length);
                        socket.receive(dataPacket);
                        JSONObject jsonPacket = fromBytesToJson(dataPacket.getData());

                        synchronized (receivedPackets) {
                            receivedPackets.add(jsonPacket);
                        }

                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    if (Thread.interrupted()) {
                        return;
                    }
                }
            }
        };

        listenerThread.start();
    }

    private JSONObject fromBytesToJson(byte[] packetBytes) {
        String data = new String(packetBytes);
        return new JSONObject(data);
    }


}
