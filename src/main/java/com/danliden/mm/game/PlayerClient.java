package com.danliden.mm.game;

import com.danliden.mm.utils.Vector2;
import com.sun.javafx.geom.Vec2f;
import org.json.JSONObject;

import java.net.InetAddress;

public class PlayerClient {

    public final String name;
    public final InetAddress address;
    public final int port;
    public final int id;
    public final int sessionId;
    public int health;
    public int nrOfFlatlines = 0; // Used with session heartbeats.
    public Vector2 position = new Vector2();

    public PlayerClient(String name, InetAddress address, int port, int id, int sessionId, int maxHealth){
        this.name = name;
        this.address = address;
        this.port = port;
        this.id = id;
        this.sessionId = sessionId;
        this.health = maxHealth;
    }

    public JSONObject getAsJson(){
        JSONObject obj = new JSONObject();
        obj.put(PacketDataKey.PlayerId, id);
        obj.put(PacketDataKey.SessionID, sessionId);
        obj.put(PacketDataKey.PlayerName, name);
        obj.put(PacketDataKey.PlayerXPos, position.x);
        obj.put(PacketDataKey.PlayerYPos, position.y);
        obj.put(PacketDataKey.PlayerHealth, health);
        return obj;
    }

    public void updatePlayer(JSONObject obj){
        health = obj.getInt(PacketDataKey.PlayerHealth);
        position.set(obj.getFloat(PacketDataKey.PlayerXPos),
                obj.getFloat(PacketDataKey.PlayerYPos));
    }


    public synchronized void addFlatline() {
        nrOfFlatlines ++;
    }

    public synchronized void resetFlatline(){
        nrOfFlatlines = 0;
    }

}
