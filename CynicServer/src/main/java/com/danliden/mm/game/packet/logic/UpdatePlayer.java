package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.racing.CheckpointManager;
import com.danliden.mm.game.racing.Placements;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdatePlayer implements IPacketLogic {

    private Placements placements = new Placements();

    @Override
    public void execute(Properties props) {
        if (props.gameState.getGameState() == GameState.GameStateEnum.IN_SESSION) {
            final int id = props.bundle
                    .getPacketJsonData()
                    .getInt(PacketKeys.PlayerId);

            PlayerClient client = props.sessionPlayers.findById(id);

            if (client != null) {
                client.updatePlayer(props.bundle.getPacketJsonData());
                updatePlayerPlacements(props.sender, props.sessionPlayers, props.checkpointManager);
                notifyOtherClientsUpdate(props.sender, props.sessionPlayers, client);
            } else {
                props.sender.sendNotConnectedPacketToSender(props.bundle);
            }
        }
    }

    private void updatePlayerPlacements(PacketSender sender, SessionPlayers sessionPlayers, CheckpointManager checkpointManager) {
        if(checkpointManager != null) {
            List<PlayerClient> placementList = placements.getPlacements(sessionPlayers.getPlayers(), checkpointManager);
            notifyOtherClientsPlacements(sender, sessionPlayers, placementList);
        }
    }

    private void notifyOtherClientsPlacements(PacketSender sender, SessionPlayers sessionPlayers, List<PlayerClient> placementList) {
        JSONObject placementPacket = buildPlacementPacket(placementList);
        sender.sendToMultiple(placementPacket, sessionPlayers.getPlayers());
    }

    private JSONObject buildPlacementPacket(List<PlayerClient> placementList) {
        List<Integer> orderedPlayerIds = new ArrayList<>();
        placementList.forEach(player -> orderedPlayerIds.add(player.id));

        JSONObject placementPacket = new JSONObject();
        placementPacket.put(PacketKeys.PacketId, PacketType.Outgoing.PLACEMENT_UPDATE);
        placementPacket.put(PacketKeys.PlacementUpdate, orderedPlayerIds);

        return placementPacket;
    }

    private void notifyOtherClientsUpdate(PacketSender sender, SessionPlayers sessionPlayers, PlayerClient client) {
        JSONObject updatePlayerJsonData = client.getAsJsonForInSession();
        updatePlayerJsonData.put(PacketKeys.PacketId, PacketType.Outgoing.UPDATED_CLIENT);
        sender.sendToMultipleWithExclude(updatePlayerJsonData, sessionPlayers.getPlayers(), client);
    }
}
