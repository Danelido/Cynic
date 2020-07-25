package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

public class RemoveVoteToStartSession implements IPacketLogic {

    @Override
    public void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState gameState) {
        final int id = bundle
                .getPacketJsonData()
                .getInt(PacketKeys.PlayerId);

        PlayerClient client = sessionPlayers.findById(id);
        if(setToNotReadyIfNotNull(client, bundle, sender) && gameState.getGameState() == GameState.GameStateEnum.LOBBY){
            informAllPlayers(client, ackHandler, sessionPlayers, sender);
        }

    }

    private boolean setToNotReadyIfNotNull(PlayerClient client, ServerPacketBundle bundle, PacketSender sender){
        if (client != null) {
            client.setIsReady(false);
            return true;
        }

        sender.sendNotConnectedPacketToSender(bundle);
        return false;
    }

    private void informAllPlayers(PlayerClient client, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender){
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.PLAYER_REMOVE_VOTE_TO_START);
        packet.put(PacketKeys.PlayerId, client.id);
        sender.sendToMultipleWithAck(ackHandler, packet, sessionPlayers.getPlayers(), 10, 250);
    }

}
