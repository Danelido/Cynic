package com.danliden.mm.utils;

public abstract class PacketType {

    public abstract static class Outgoing {
        public static final int JOIN_REQUEST = 0;
        public static final int LEFT_SESSION = 1;
        public static final int HEARTBEAT = 2;
        public static final int CLIENT_UPDATE = 3;
    }

    public abstract static class Incoming {
        public static final int JOIN_ACCEPTED = 0;
        public static final int LOST_CLIENT = 1;
        public static final int HEARTBEAT_REQUEST = 2;
        public static final int UPDATED_CLIENT = 3;
        public static final int DECLINED_JOIN_REQUEST = 4;
        public static final int NEW_PLAYER_JOINED = 5;
    }

}
