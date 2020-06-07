package com.danliden.mm.game;

import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.UniqueId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *  Must haves in a package
 *  "pid" -> Package ID
 */

public class GameSession {

    private static class Ack{
        public final int ackId;
        public final PlayerClient client;
        public final byte[] outgoingData;
        public final int outgoingPid;
        public final int intervalMS;
        public final int maxTries;

        public int currentTime = 0;
        public int currentTry = 0;

        public Ack(final int ackId, final PlayerClient client, byte[] outgoingData,
                   final int outgoingPid, final int intervalMS, final int maxTries){
            this.ackId = ackId;
            this.client = client;
            this.outgoingData = outgoingData;
            this.outgoingPid = outgoingPid;
            this.intervalMS = intervalMS;
            this.maxTries = maxTries;
        }
    }

    private static final int MAX_PLAYERS = 4;
    private static final int MAX_FLATLINES = 20;
    private final int SESSION_ID;
    private GameState currentState;
    private List<PlayerClient> clients = new ArrayList<PlayerClient>();
    private List<Ack> ackList = new ArrayList<Ack>();
    private UniqueId clientIdHandler;
    private UniqueId ackIdHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DatagramSocket parentSocket;

    public GameSession(DatagramSocket parentSocket, int sessionID){
        this.parentSocket = parentSocket;
        SESSION_ID = sessionID;
        currentState = GameState.LOBBY;
        ackIdHandler = new UniqueId(500000);
        clientIdHandler = new UniqueId(MAX_PLAYERS * 2);
    }
    /* This will be called multiple times during a second*/
    public void update(final int updateInterval){
        for (PlayerClient client: clients) {
            if(client.nrOfFlatlines >= MAX_FLATLINES){
                disconnect(client);
            }
        }

        synchronized (ackList) {
            final int startIndex = ackList.size() - 1;
            for(int i = startIndex; i >= 0; i--){
                Ack ack = ackList.get(i);
                ack.currentTime += updateInterval;

                if(ack.intervalMS >= ack.currentTime){
                    ack.currentTime = 0;
                    ack.currentTry++;

                    if(ack.currentTry >= ack.maxTries){
                        // Give up
                        ackIdHandler.giveBackID(ack.ackId);
                        ackList.remove(i);
                    }else {
                        send(ack.outgoingData, ack.client.address, ack.client.port);
                    }
                }

            }
        }

    }

    public void heartbeat(){
        // Increase flat lines
        for (PlayerClient client: clients) {
            client.addFlatline();
        }
        // Send out heartbeats
        sendHeartbeats();

    }

    /* This will only be called when there is a packet for this session */
    public void handleData(DatagramPacket packet, JSONObject data){
        // if it was an ack then there is no need for processing it further.
        if(handleAcks(data)){
            return;
        }

        int pid = data.getInt(PacketDataKey.PacketId);
        switch (pid){
            case PacketType.JOIN_SESSION: {
                onJoinSession(packet);
                break;
            }
            case PacketType.LEAVE_SESSION: {
                onLeaveSession(data.getInt(PacketDataKey.PlayerId));
                break;
            }
            case PacketType.HEARTBEAT: {
                onHeartbeat(data.getInt(PacketDataKey.PlayerId));
                break;
            }
            case PacketType.UPDATE_PLAYER:
                onUpdatePlayer(data);
                break;

            default:
                logger.info(String.format("Invalid pid %d", pid));
        }
    }

    /** Packet functions **/
    private boolean handleAcks(JSONObject data){
        try{
            final int ackId = data.getInt(PacketDataKey.AckId);
            synchronized(ackList) {
                for(int i = 0; i < ackList.size(); i++){
                    Ack ack = ackList.get(i);
                    if(ack.ackId == ackId){
                        ackIdHandler.giveBackID(ack.ackId);
                        ackList.remove(i);
                        return true;
                    }
                }
            }
        }catch(Exception e){
            return false;
        }

        return false;
    }

    private void onJoinSession(@NotNull DatagramPacket packet){
        // Check if client already exists
        PlayerClient client = findPlayerByAddr(packet.getAddress(), packet.getPort());
        if(client != null){
            // Resend info to the confused client
            sendPlayerInfo(PacketType.JOIN_SESSION, client, client);
            return;
        }

        if (isJoinable()) {
            PlayerClient newClient = join(packet.getAddress(), packet.getPort(), "Player " + (clients.size() + 1));

            // Send the player information to the new client
            sendPlayerInfo(PacketType.JOIN_SESSION, newClient, newClient);

            // Send the player information to all other clients as
            // well as sending the other clients to the new player
            for (PlayerClient currClient : clients) {
                if (currClient.id != newClient.id) {
                    // Serialize the new client and send to all other clients
                    JSONObject newClientJson = newClient.getAsJson();
                    newClientJson.put(PacketDataKey.PacketId, PacketType.NEW_PLAYER);
                    sendWithAck(newClientJson, currClient, 500, 100);

                    // Serialize the current client and send to the new client
                    JSONObject currClientJson = newClient.getAsJson();
                    currClientJson.put(PacketDataKey.PacketId, PacketType.NEW_PLAYER);
                    sendWithAck(currClientJson, newClient, 500, 100);
                }
            }
        }else{
            declineClient(packet.getAddress(), packet.getPort());
        }
    }

    private void onLeaveSession(final int id){
        PlayerClient client = findPlayerById(id);
        if (client != null) {
            disconnect(client);
        }
    }

    private void onHeartbeat(final int id){
        PlayerClient client = findPlayerById(id);
        if(client != null) {
            client.resetFlatline();
        }
    }

    private void onUpdatePlayer(@NotNull JSONObject data){
        final int id = data.getInt(PacketDataKey.PlayerId);
        PlayerClient client = findPlayerById(id);
        if(client != null){
            client.updatePlayer(data);
            sendUpdatedPlayer(client);
        }
    }

    /** Utility functions **/
    private void sendWithAck(@NotNull JSONObject obj, PlayerClient client, int maxTries, int intervalMS){
        final int ackId = ackIdHandler.getId();
        obj.put(PacketDataKey.AckId, ackId);

        final byte[] data =  obj.toString().getBytes();
        Ack ack = new Ack(ackId, client, data,
                obj.getInt(PacketDataKey.PacketId),intervalMS, maxTries);

        ackList.add(ack);
        send(data, client.address, client.port);
    }

    private void sendPlayerInfo(final int pid, @NotNull PlayerClient clientToSerialize, @NotNull PlayerClient receivingClient){
        JSONObject clientJson = clientToSerialize.getAsJson();
        clientJson.put(PacketDataKey.PacketId, pid);
        send(clientJson.toString().getBytes(), receivingClient.address, receivingClient.port);
    }

    private void sendUpdatedPlayer(PlayerClient clientToSerialize){
        for (PlayerClient client: clients) {
          if(clientToSerialize.id != client.id){
              sendPlayerInfo(PacketType.UPDATE_PLAYER, clientToSerialize, client);
          }
        }
    }

    private void declineClient(InetAddress address, int port) {
        JSONObject obj = new JSONObject();
        obj.put(PacketDataKey.PacketId, PacketType.DECLINED);
        send(obj.toString().getBytes(), address, port);
    }

    private void send(final byte[] data, final InetAddress address, final int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

        try {
            parentSocket.send(packet);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void sendHeartbeats(){
        JSONObject obj = new JSONObject();
        obj.put(PacketDataKey.PacketId, PacketType.HEARTBEAT);
        for (PlayerClient client: clients) {
            send(obj.toString().getBytes(), client.address, client.port);
        }
    }

    private void disconnect(@NotNull PlayerClient client){
        logger.info(String.format("disconnecting player %s from %s:%d", client.name, client.address, client.port));
        clients.remove(client);
        clientIdHandler.giveBackID(client.id);
        // Broadcast
    }

    public boolean isFull() {
        return clients.size() == MAX_PLAYERS;
    }

    public boolean isJoinable() {
        return currentState == GameState.LOBBY && !isFull();
    }

    public final int getSessionId() {
        return SESSION_ID;
    }

    @NotNull
    private PlayerClient join(InetAddress address, int port, String name){
        logger.info(String.format("New player \"%s\" joined from %s:%d", name, address, port));
        PlayerClient client = new PlayerClient(
            name, address, port,
            clientIdHandler.getId(), SESSION_ID, 3
        );

        clients.add(client);
        return client;
    }

    @Nullable
    @Contract(pure = true)
    private PlayerClient findPlayerByAddr(final InetAddress address, final int port){
        for (PlayerClient client: clients) {
            if(client.address == address && client.port == port){
                return client;
            }
        }

        return null;
    }

    @Nullable
    @Contract(pure = true)
    private PlayerClient findPlayerById(final int id) {
        for (PlayerClient client: clients) {
            if(client.id == id){
                return client;
            }
        }

        return null;
    }

}
