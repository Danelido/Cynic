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

public class VoteToStartSession implements IPacketLogic {

    @Override
    public void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState gameState) {
        final int id = bundle
                .getPacketJsonData()
                .getInt(PacketKeys.PlayerId);

        PlayerClient client = sessionPlayers.findById(id);
        if(setToReadyIfNotNull(client, bundle, sender) && gameState.getGameState() == GameState.GameStateEnum.LOBBY){
            sendPlayerReadyPacketToAllPlayers(client, ackHandler, sessionPlayers, sender);
            if(startSessionIfAllPlayersReady(ackHandler, sessionPlayers, sender)){
                gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
            }
        }

    }

    private boolean setToReadyIfNotNull(PlayerClient client, ServerPacketBundle bundle, PacketSender sender){
        if (client != null) {
            client.setIsReady(true);
            return true;
        }
        sender.sendNotConnectedPacketToSender(bundle);
        return false;
    }

    private void sendPlayerReadyPacketToAllPlayers(PlayerClient client, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender){
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.PLAYER_VOTE_TO_START);
        packet.put(PacketKeys.PlayerId, client.id);
        sender.sendToMultipleWithAckAndExclude(ackHandler, packet, sessionPlayers.getPlayers(), 10, 250, client);
    }

    private boolean startSessionIfAllPlayersReady(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender){
        if(isAllPlayersReady(sessionPlayers)){
            sendStartingPacketToPlayers(ackHandler, sessionPlayers, sender);
            return true;
        }

        return false;
    }

    private boolean isAllPlayersReady(SessionPlayers sessionPlayers){
        for (PlayerClient player : sessionPlayers.getPlayers()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    private void sendStartingPacketToPlayers(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender){
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.STARTING_GAME);
        sender.sendToMultipleWithAck(ackHandler, packet, sessionPlayers.getPlayers(), 10, 500);
    }


}
