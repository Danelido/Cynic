package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

public class JoinSession implements IPacketLogic {

    @Override
    public void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState gameState) {

        if (clientExists(bundle, sessionPlayers)) {
            return;
        }

        if (isSessionJoinAble(gameState, sessionPlayers)) {
            PlayerClient newClient = sessionPlayers.createPlayer(bundle);
            sendConfirmationToClient(sender, ackHandler, newClient);
            tellExistingClientsAboutNewClient(sender, ackHandler, newClient, sessionPlayers);
            tellNewClientAboutExistingClients(sender, ackHandler, newClient, sessionPlayers);
        } else {
            declineJoinRequest(bundle, sender);
        }
    }

    private void sendConfirmationToClient(PacketSender sender, SessionAckHandler ackHandler, PlayerClient newClient) {
        JSONObject newClientJsonData = newClient.getAsJsonForLobby();
        newClientJsonData.put(PacketKeys.PacketId, PacketType.Outgoing.JOIN_ACCEPTED);
        sender.sendWithAck(ackHandler, newClientJsonData, newClient, 30, 2000);
    }

    private void tellExistingClientsAboutNewClient(PacketSender sender, SessionAckHandler ackHandler, PlayerClient newClient, SessionPlayers sessionPlayers) {
        JSONObject newClientJsonData = newClient.getAsJsonForLobby();
        newClientJsonData.put(PacketKeys.PacketId, PacketType.Outgoing.NEW_PLAYER_JOINED);
        sender.sendToMultipleWithAckAndExclude(ackHandler, newClientJsonData, sessionPlayers.getPlayers(), 30, 2000, newClient);
    }

    private void tellNewClientAboutExistingClients(PacketSender sender, SessionAckHandler ackHandler, PlayerClient newClient, SessionPlayers sessionPlayers) {
        for (PlayerClient client : sessionPlayers.getPlayers()) {
            if (client.id != newClient.id) {
                JSONObject clientAsJson = client.getAsJsonForLobby();
                clientAsJson.put(PacketKeys.PacketId, PacketType.Outgoing.NEW_PLAYER_JOINED);
                sender.sendWithAck(ackHandler, clientAsJson, newClient, 30, 2000);
            }
        }
    }

    private void declineJoinRequest(ServerPacketBundle bundle, PacketSender sender) {
        JSONObject declineJsonData = new JSONObject();
        declineJsonData.put(PacketKeys.PacketId, PacketType.Outgoing.DECLINED_JOIN_REQUEST);
        sender.sendToAddress(declineJsonData, bundle.getDatagramPacket().getAddress(), bundle.getDatagramPacket().getPort());
    }

    private boolean isSessionJoinAble(GameState gameState, SessionPlayers sessionPlayers) {
        return gameState.getGameState() == GameState.GameStateEnum.LOBBY && !sessionPlayers.isFull();
    }

    private boolean clientExists(ServerPacketBundle bundle, SessionPlayers sessionPlayers) {
        return sessionPlayers.findByAddressAndPort(bundle.getDatagramPacket().getAddress(), bundle.getDatagramPacket().getPort()) != null;
    }


}
