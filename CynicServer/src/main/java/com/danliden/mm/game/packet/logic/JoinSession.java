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
    public void execute(Properties props) {

        if (clientExists(props.bundle, props.sessionPlayers)) {
            return;
        }

        if (isSessionJoinAble(props.gameState, props.sessionPlayers)) {
            PlayerClient newClient = props.sessionPlayers.createPlayer(props.bundle);
            sendConfirmationToClient(props.sender, props.ackHandler, newClient);
            tellExistingClientsAboutNewClient(props.sender, props.ackHandler, newClient, props.sessionPlayers);
            tellNewClientAboutExistingClients(props.sender, props.ackHandler, newClient, props.sessionPlayers);
        } else {
            declineJoinRequest(props.bundle, props.sender);
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
