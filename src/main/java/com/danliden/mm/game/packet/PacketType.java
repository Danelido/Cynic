package com.danliden.mm.game.packet;

public abstract class PacketType {

    public static final int JOIN_SESSION = 0;       // Incoming / Outgoing
    public static final int LEAVE_SESSION = 1;      // Incoming
    public static final int HEARTBEAT = 2;          // Incoming / Outgoing
    public static final int DECLINED = 3;           // Outgoing
    public static final int NEW_PLAYER = 4;         // Outgoing
    public static final int UPDATE_PLAYER = 5;      // Incoming / Outgoing


    public static final int TEST_PACKET = -1;      // Incoming / Outgoing    Used for testing purposes

}
