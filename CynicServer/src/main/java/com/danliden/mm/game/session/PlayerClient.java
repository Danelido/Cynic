package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.PacketKeys;
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
        obj.put(PacketKeys.PlayerId, id);
        obj.put(PacketKeys.SessionID, sessionId);
        obj.put(PacketKeys.PlayerName, name);
        obj.put(PacketKeys.PlayerXPos, position.x);
        obj.put(PacketKeys.PlayerYPos, position.y);
        obj.put(PacketKeys.PlayerHealth, health);
        return obj;
    }

    public void updatePlayer(JSONObject obj) {
        health = obj.getInt(PacketKeys.PlayerHealth);
        position.set(obj.getFloat(PacketKeys.PlayerXPos),
                obj.getFloat(PacketKeys.PlayerYPos));
    }


    public synchronized void addFlatline() {
        nrOfFlatLines++;
    }

    public synchronized void resetFlatline() {
        nrOfFlatLines = 0;
    }

}
