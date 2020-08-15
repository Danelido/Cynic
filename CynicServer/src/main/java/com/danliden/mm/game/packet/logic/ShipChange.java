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

public class ShipChange implements IPacketLogic {

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

        if (!inLobby(props.gameState)) return;
        if (client.isReady()) return;

        changeShip(props.bundle, client);
        notifyAllClients(props.sender, props.ackHandler, props.sessionPlayers, client);
    }

    private boolean doesPlayerExist(PlayerClient client) {
        return client != null;
    }

    private boolean inLobby(GameState gameState) {
        return gameState.getGameState() == GameState.GameStateEnum.LOBBY;
    }

    private void changeShip(ServerPacketBundle bundle, PlayerClient client) {
        String shipName = bundle
                .getPacketJsonData()
                .getString(PacketKeys.ShipPrefabName);
        client.setChosenShip(shipName);
    }

    private void notifyAllClients(PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, PlayerClient client) {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.PLAYER_SHIP_CHANGE);
        packet.put(PacketKeys.PlayerId, client.id);
        packet.put(PacketKeys.ShipPrefabName, client.getChosenShip());
        sender.sendToMultipleWithAckAndExclude(ackHandler, packet, sessionPlayers.getPlayers(), 10, 250, client);
    }
}
