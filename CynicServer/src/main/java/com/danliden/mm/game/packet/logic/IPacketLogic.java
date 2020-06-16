package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;

public interface IPacketLogic {

    void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler,
                 SessionPlayers sessionPlayers, GameState gameState);
}
