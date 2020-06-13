package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

public class UpdatePlayer implements IPacketLogic {
    @Override
    public void execute(ServerPacketBundle bundle, PacketSender sender, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState gameState) {
        final int id = bundle
                .getPacketJsonData()
                .getInt(ValidPacketDataKeys.PlayerId);

        PlayerClient client = sessionPlayers.findById(id);

        if (client != null) {
            client.updatePlayer(bundle.getPacketJsonData());
            notifyOtherClients(sender, sessionPlayers, client);
        }
    }

    private void notifyOtherClients(PacketSender sender, SessionPlayers sessionPlayers, PlayerClient client) {
        JSONObject updatePlayerJsonData = client.getAsJson();
        updatePlayerJsonData.put(ValidPacketDataKeys.PacketId, PacketType.Outgoing.UPDATED_CLIENT);
        sender.sendToMultipleWithExclude(updatePlayerJsonData, sessionPlayers.getPlayers(), client);
    }
}
