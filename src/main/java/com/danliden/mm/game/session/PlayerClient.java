package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.utils.Vector2;
import org.json.JSONObject;

import java.net.InetAddress;

public class PlayerClient {

    public final String name;
    public final InetAddress address;
    public final int port;
    public final int id;
    public final int sessionId;
    public int health;
    public int nrOfFlatLines = 0; // Used with session heartbeats.
    public Vector2 position = new Vector2();

    public PlayerClient(String name, InetAddress address, int port, int id, int sessionId, int maxHealth) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.id = id;
        this.sessionId = sessionId;
        this.health = maxHealth;
    }

    public JSONObject getAsJson() {
        JSONObject obj = new JSONObject();
        obj.put(ValidPacketDataKeys.PlayerId, id);
        obj.put(ValidPacketDataKeys.SessionID, sessionId);
        obj.put(ValidPacketDataKeys.PlayerName, name);
        obj.put(ValidPacketDataKeys.PlayerXPos, position.x);
        obj.put(ValidPacketDataKeys.PlayerYPos, position.y);
        obj.put(ValidPacketDataKeys.PlayerHealth, health);
        return obj;
    }

    public void updatePlayer(JSONObject obj) {
        health = obj.getInt(ValidPacketDataKeys.PlayerHealth);
        position.set(obj.getFloat(ValidPacketDataKeys.PlayerXPos),
                obj.getFloat(ValidPacketDataKeys.PlayerYPos));
    }


    public synchronized void addFlatline() {
        nrOfFlatLines++;
    }

    public synchronized void resetFlatline() {
        nrOfFlatLines = 0;
    }

}
