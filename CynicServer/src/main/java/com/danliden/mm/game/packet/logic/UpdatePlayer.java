package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.racing.CheckpointManager;
import com.danliden.mm.game.racing.Placements;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.TimeMeasurement;
import com.danliden.mm.utils.TimeUnits;
import org.json.JSONObject;

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
                // Check if a client has won
                checkForWinnings(props, client);
            } else {
                props.sender.sendNotConnectedPacketToSender(props.bundle);
            }
        }
    }

    private void checkForWinnings(Properties props, PlayerClient client) {
        if(client.getLap() > 3){
            client.setHasFinishedRace(true);
            if(firstPlayerToFinish(client, props.sessionPlayers)){
                // if so then start the 30 seconds count down
                props.doomTimer.startCountdown(TimeMeasurement.of(30, TimeUnits.SECONDS));
                // Notify clients

            }

            if(client.getLocalPlacement() <= props.sessionPlayers.getPlayers().size() -1 && client.getLocalPlacement() <= 3){
                // Reward ( future )
            }

            // Send a packet to the player telling him/her about this

        }
    }

    private boolean firstPlayerToFinish(PlayerClient client, SessionPlayers sessionPlayers){
        for (PlayerClient player: sessionPlayers.getPlayers()) {
            if(player.id == client.id){
                continue;
            }
            if(player.isHasFinishedRace()){
                return false;
            }
        }
        return true;
    }

    private void updatePlayerPlacements(PacketSender sender, SessionPlayers sessionPlayers, CheckpointManager checkpointManager) {
        if (checkpointManager != null) {
            List<PlayerClient> placementList = placements.getPlacements(sessionPlayers.getPlayers(), checkpointManager);
            updateLocalPlacements(sessionPlayers, placementList);
            notifyOtherClientsPlacements(sender, sessionPlayers, placementList);
        }
    }

    private void updateLocalPlacements(SessionPlayers sessionPlayers, List<PlayerClient> placementList) {
        sessionPlayers.getPlayers().forEach(playerClient -> {
            for (int i = 0; i < placementList.size(); i++) {
                if(playerClient.id == placementList.get(i).id){
                    playerClient.setLocalPlacement(i);
                    break;
                }
            }
        });
    }

    private void notifyOtherClientsPlacements(PacketSender sender, SessionPlayers sessionPlayers, List<PlayerClient> placementList) {
        JSONObject placementPacket = buildPlacementPacket(placementList);
        sender.sendToMultiple(placementPacket, sessionPlayers.getPlayers());
    }

    private void notifyOtherClientsUpdate(PacketSender sender, SessionPlayers sessionPlayers, PlayerClient client) {
        JSONObject updatePlayerJsonData = client.getAsJsonForInSession();
        updatePlayerJsonData.put(PacketKeys.PacketId, PacketType.Outgoing.UPDATED_CLIENT);
        sender.sendToMultipleWithExclude(updatePlayerJsonData, sessionPlayers.getPlayers(), client);
    }

    private JSONObject buildPlacementPacket(List<PlayerClient> placementList) {
        JSONObject placementPacket = new JSONObject();
        StringBuilder placementsString = buildPlacementString(placementList);
        placementPacket.put(PacketKeys.PacketId, PacketType.Outgoing.PLACEMENT_UPDATE);
        placementPacket.put(PacketKeys.PlacementUpdate, placementsString.toString());

        return placementPacket;
    }

    private StringBuilder buildPlacementString(List<PlayerClient> placementList) {
        StringBuilder orderedPlayerIds = new StringBuilder();
        for (PlayerClient playerClient : placementList) {
            orderedPlayerIds.append(playerClient.id).append(",");
        }
        return orderedPlayerIds;
    }

}
