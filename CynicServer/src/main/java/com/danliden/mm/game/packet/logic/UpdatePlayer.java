package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.racing.Placements;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.Configuration;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.TimeMeasurement;
import com.danliden.mm.utils.TimeUnits;

import java.util.List;

public class UpdatePlayer implements IPacketLogic {

    private final Placements placements = new Placements();

    @Override
    public void execute(Properties props) {
        if (props.gameState.getGameState() == GameState.GameStateEnum.IN_SESSION ||
                props.gameState.getGameState() == GameState.GameStateEnum.IN_SESSION_DOOM_TIMER) {
            final int id = props.bundle
                    .getPacketJsonData()
                    .getInt(PacketKeys.PlayerId);

            PlayerClient client = props.sessionPlayers.findById(id);

            if (client != null) {
                client.updatePlayer(props.bundle.getPacketJsonData());
                updatePlayerPlacements(props, client);
            } else {
                props.sender.sendNotConnectedPacketToSender(props.bundle);
            }
        }
    }

    private void updatePlayerPlacements(Properties props, PlayerClient client) {
        if (props.trackManager != null) {
            List<PlayerClient> placementList = placements.getPlacements(props.sessionPlayers.getPlayers(), props.trackManager);
            updateLocalPlacements(props.sessionPlayers, placementList);
            checkForWinnings(props, client);
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

    private void checkForWinnings(Properties props, PlayerClient client) {
        if(client.getLap() > 3){
            client.setHasFinishedRace(true);
            if(firstPlayerToFinish(client, props.sessionPlayers)){
                props.doomTimer.startCountdown(TimeMeasurement.of(Configuration.getDoomTimerSeconds(), TimeUnits.SECONDS));
            }

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
}
