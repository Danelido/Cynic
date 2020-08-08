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

        if (!doesPlayerExist(client)) {
            sender.sendNotConnectedPacketToSender(bundle);
            return;
        }

        if (!enoughPlayersToStartSession(sessionPlayers)) {
            return;
        }

        if (gameState.getGameState() == GameState.GameStateEnum.LOBBY) {
            String chosenShip = extractChosenShipName(bundle);
            if (validShipName(chosenShip)) {
                client.setChosenShip(chosenShip);
                client.setShipColor(
                        bundle.getPacketJsonData().getFloat(PacketKeys.ShipRedComponent),
                        bundle.getPacketJsonData().getFloat(PacketKeys.ShipGreenComponent),
                        bundle.getPacketJsonData().getFloat(PacketKeys.ShipBlueComponent));
                sessionPlayers.setClientReady(client, true);
                sendPlayerReadyPacketToAllPlayers(client, ackHandler, sessionPlayers, sender);
                if (startSessionIfAllPlayersReady(ackHandler, sessionPlayers, sender)) {
                    gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
                }
            }
        }
    }

    private boolean doesPlayerExist(PlayerClient client) {
        return client != null;
    }

    private void sendPlayerReadyPacketToAllPlayers(PlayerClient client, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender) {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.PLAYER_VOTE_TO_START);
        packet.put(PacketKeys.PlayerId, client.id);
        packet.put(PacketKeys.ShipPrefabName, client.getChosenShip());
        packet.put(PacketKeys.ShipRedComponent, client.getColor().x);
        packet.put(PacketKeys.ShipGreenComponent, client.getColor().y);
        packet.put(PacketKeys.ShipBlueComponent, client.getColor().z);
        sender.sendToMultipleWithAck(ackHandler, packet, sessionPlayers.getPlayers(), 10, 250);
    }

    private String extractChosenShipName(ServerPacketBundle bundle) {
        return bundle.getPacketJsonData().getString(PacketKeys.ShipPrefabName);
    }

    private boolean validShipName(String shipName) {
        return shipName != null && !shipName.isEmpty();
    }

    private boolean startSessionIfAllPlayersReady(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender) {
        if (isAllPlayersReady(sessionPlayers)) {
            sendStartingPacketToPlayers(ackHandler, sessionPlayers, sender);
            return true;
        }
        return false;
    }

    private boolean isAllPlayersReady(SessionPlayers sessionPlayers) {
        for (PlayerClient player : sessionPlayers.getPlayers()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    private boolean enoughPlayersToStartSession(SessionPlayers sessionPlayers) {
        return sessionPlayers.getPlayers().size() > 1;
    }

    private void sendStartingPacketToPlayers(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender) {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.STARTING_GAME);
        sender.sendToMultipleWithAck(ackHandler, packet, sessionPlayers.getPlayers(), 10, 500);
    }


}