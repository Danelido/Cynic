package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.utils.Vector2;
import com.danliden.mm.utils.Vector3;
import org.json.JSONObject;

import java.net.InetAddress;

public class PlayerClient {

    public final String name;
    public final InetAddress address;
    public final int port;
    public final int id;
    public final int sessionId;
    private int nrOfFlatLines = 0; // Used with session heartbeats.
    private String shipPrefabName;
    private Vector2 position = new Vector2();
    private Vector3 color = new Vector3();
    private float rotationDegrees = 0.f;
    private boolean throttling = false;
    private boolean ready;

    public PlayerClient(String name, InetAddress address, int port, int id, int sessionId) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.id = id;
        this.sessionId = sessionId;
        this.ready = false;
        this.shipPrefabName = "";
    }

    public JSONObject getAsJsonForLobby() {
        JSONObject obj = new JSONObject();
        obj.put(PacketKeys.PlayerId, id);
        obj.put(PacketKeys.PlayerReady, ready);
        obj.put(PacketKeys.PlayerName, name);
        obj.put(PacketKeys.ShipPrefabName, shipPrefabName);
        return obj;
    }

    public JSONObject getAsJsonForInSession() {
        JSONObject obj = new JSONObject();
        obj.put(PacketKeys.PlayerId, id);
        obj.put(PacketKeys.PlayerXPos, position.x);
        obj.put(PacketKeys.PlayerYPos, position.y);
        obj.put(PacketKeys.PlayerRotation, rotationDegrees);
        obj.put(PacketKeys.Throttling, throttling);
        return obj;
    }

    public void updatePlayer(JSONObject obj) {
        throttling = obj.getBoolean(PacketKeys.Throttling);
        rotationDegrees = obj.getInt(PacketKeys.PlayerRotation);
        position.set(obj.getFloat(PacketKeys.PlayerXPos),
                obj.getFloat(PacketKeys.PlayerYPos));
    }

    public synchronized void addFlatline() {
        nrOfFlatLines++;
    }

    public synchronized void resetFlatline() {
        nrOfFlatLines = 0;
    }

    public synchronized void setIsReady(boolean isReady) {
        ready = isReady;
    }

    public int getNrOfFlatLines() {
        return nrOfFlatLines;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public boolean isThrottling() {
        return throttling;
    }

    public boolean isReady() {
        return ready;
    }

    public void setChosenShip(String prefabName) {
        this.shipPrefabName = prefabName;
    }

    public void setShipColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public String getChosenShip() {
        return this.shipPrefabName;
    }

    public Vector3 getColor(){
        return this.color;
    }
}
