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
    public int nrOfFlatLines = 0; // Used with session heartbeats.
    public Vector2 position = new Vector2();
    public float rotationDegrees = 0.f;
    public boolean throttling = false;

    public PlayerClient(String name, InetAddress address, int port, int id, int sessionId) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.id = id;
        this.sessionId = sessionId;
    }

    public JSONObject getAsJson() {
        JSONObject obj = new JSONObject();
        obj.put(PacketKeys.PlayerId, id);
        obj.put(PacketKeys.SessionID, sessionId);
        obj.put(PacketKeys.PlayerName, name);
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

}
