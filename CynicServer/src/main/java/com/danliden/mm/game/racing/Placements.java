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

            int lapCompareVal = Integer.compare(o2.getLap(), o1.getLap());
            if(lapCompareVal == 0){
                int checkpointCompareVal = Integer.compare(p2CheckPoint.getIndex(), p1CheckPoint.getIndex());
                if(checkpointCompareVal == 0){
                    Vector2 checkpointPosition = p1CheckPoint.getPivot();
                    float playerOneDist = o1.getPosition().distance(checkpointPosition);
                    float playerTwoDist = o2.getPosition().distance(checkpointPosition);

                    return Float.compare(playerOneDist, playerTwoDist);
                }

                return checkpointCompareVal;
            }

            return lapCompareVal;
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
