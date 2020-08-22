package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.racing.CheckpointManager;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;

public class Properties {
    ServerPacketBundle bundle;
    PacketSender sender;
    SessionAckHandler ackHandler;
    SessionPlayers sessionPlayers;
    GameState gameState;
    CheckpointManager checkpointManager;

    public Properties setBundle(ServerPacketBundle bundle){
        this.bundle = bundle;
        return this;
    }

    public Properties setPacketSender(PacketSender sender){
        this.sender = sender;
        return this;
    }

    public Properties setSessionAckHandler(SessionAckHandler ackHandler){
        this.ackHandler = ackHandler;
        return this;
    }

    public Properties setSessionPlayers(SessionPlayers sessionPlayers){
        this.sessionPlayers = sessionPlayers;
        return this;
    }

    public Properties setGameState(GameState gameState){
        this.gameState = gameState;
        return this;
    }

    public Properties setCheckpointsManager(CheckpointManager checkpointsManager){
        this.checkpointManager = checkpointsManager;
        return this;
    }

}