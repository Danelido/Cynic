package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.racing.Tracks;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VoteToStartSession implements IPacketLogic {

    private static final Logger logger = LoggerFactory.getLogger(VoteToStartSession.class);

    @Override
    public void execute(Properties props) {
        final int id = props.bundle
                .getPacketJsonData()
                .getInt(PacketKeys.PlayerId);

        PlayerClient client = props.sessionPlayers.findById(id);

        if (!doesPlayerExist(client)) {
            props.sender.sendNotConnectedPacketToSender(props.bundle);
            return;
        }

        if (!enoughPlayersToStartSession(props.sessionPlayers)) {
            return;
        }

        if (props.gameState.getGameState() == GameState.GameStateEnum.LOBBY) {
            String chosenShip = extractChosenShipName(props.bundle);
            if (validShipName(chosenShip)) {
                client.setChosenShip(chosenShip);
                client.setShipColor(
                        props.bundle.getPacketJsonData().getFloat(PacketKeys.ShipRedComponent),
                        props.bundle.getPacketJsonData().getFloat(PacketKeys.ShipGreenComponent),
                        props.bundle.getPacketJsonData().getFloat(PacketKeys.ShipBlueComponent));
                props.sessionPlayers.setClientReady(client, true);
                sendPlayerReadyPacketToAllPlayers(client, props.ackHandler, props.sessionPlayers, props.sender);
                if (isAllPlayersReady(props.sessionPlayers)) {
                    if(loadSelectedTrackConfigurations(props)) {
                        sendStartingPacketToPlayers(props.ackHandler, props.sessionPlayers, props.sender);
                        props.gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
                    }
                }
            }
        }
    }

    private boolean loadSelectedTrackConfigurations(Properties props) {
        try {
            // TODO Do not hard code the map, it should be voted by the clients
            props.checkpointManager.loadNewCheckpoints(Tracks.SPACE_YARD);
            return true;
        } catch (IOException e) {
            logger.debug(e.getMessage());
            props.sender.sendSomethingWentWrongServerError(props.ackHandler, props.sessionPlayers.getPlayers(), "Could not load config for selected track");
            return false;
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

    private boolean sendStartSignalToAllPlayers(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PacketSender sender) {

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
