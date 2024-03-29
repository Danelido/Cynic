package com.danliden.mm.game.packet;

public abstract class PacketType {

    public abstract static class Incoming {
        public static final int JOIN_REQUEST = 0;
        public static final int LEFT_SESSION = 1;
        public static final int HEARTBEAT = 2;
        public static final int CLIENT_UPDATE = 3;
        public static final int VOTE_TO_START_SESSION = 4;
        public static final int REMOVE_VOTE_TO_START_SESSION = 5;
        public static final int PLAYER_SHIP_CHANGE = 6;
    }

    public abstract static class Outgoing {
        public static final int NOT_CONNECTED = -1;
        public static final int JOIN_ACCEPTED = 0;
        public static final int LOST_CLIENT = 1;
        public static final int HEARTBEAT_REQUEST = 2;
        public static final int UPDATED_CLIENT = 3;
        public static final int DECLINED_JOIN_REQUEST = 4;
        public static final int NEW_PLAYER_JOINED = 5;
        public static final int PLAYER_VOTE_TO_START = 6;
        public static final int PLAYER_REMOVE_VOTE_TO_START = 7;
        public static final int PLAYER_SHIP_CHANGE = 8;
        public static final int STARTING_GAME = 9;
        public static final int PLACEMENT_UPDATE = 10;
        public static final int FATAL_SERVER_ERROR = 11;
        public static final int DOOM_TIMER = 12;
        public static final int END_OF_RACE = 13;
        public static final int LOOKING_AT_PLACEMENTS_TIMER = 14;
        public static final int BACK_TO_LOBBY = 15;
        public static final int SESSION_COUNTDOWN = 16;
        public static final int START_RACE = 17;
    }

}
