package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Placements {

    private static class PlacementComparator implements Comparator<PlayerClient> {
        private CheckpointManager checkpointManager;

        public PlacementComparator(CheckpointManager checkpointManager){
            this.checkpointManager = checkpointManager;
        }

        @Override
        public int compare(PlayerClient o1, PlayerClient o2) {

            Checkpoint p1CheckPoint = checkpointManager.getCheckpointByIndex(o1.getNextCheckpointIndex());
            Checkpoint p2CheckPoint = checkpointManager.getCheckpointByIndex(o2.getNextCheckpointIndex());

            if (p1CheckPoint.getIndex() < p2CheckPoint.getIndex()) {
                return 1;
            } else if (p1CheckPoint.getIndex() > p2CheckPoint.getIndex()) {
                return -1;
            } else {
                // Chasing the same checkpoint
                Vector2 checkpointPosition = p1CheckPoint.getPivot();
                float playerOneDist = o1.getPosition().distance(checkpointPosition);
                float playerTwoDist = o2.getPosition().distance(checkpointPosition);

                return Float.compare(playerOneDist, playerTwoDist);
            }
        }
    }

    public List<PlayerClient> getPlacements(List<PlayerClient> players, CheckpointManager checkpointManager) {
        if(!hasValidIndexes(players)){
            return new ArrayList<>();
        }
        List<PlayerClient> placementList = new ArrayList<>(players);
        placementList.sort(new PlacementComparator(checkpointManager));
        return placementList;
    }

    public boolean hasValidIndexes(List<PlayerClient> players){
        for (PlayerClient player : players) {
            if (player.getNextCheckpointIndex() == -1) {
                return false;
            }
        }
        return true;
    }
}
