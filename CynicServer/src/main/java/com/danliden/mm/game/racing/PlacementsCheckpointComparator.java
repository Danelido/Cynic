package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Vector2;

import java.util.Comparator;

public class PlacementsCheckpointComparator implements Comparator<PlayerClient> {
    private final CheckpointManager checkpointManager;

        public PlacementsCheckpointComparator(CheckpointManager checkpointManager) {
            this.checkpointManager = checkpointManager;
        }

        @Override
        public int compare(PlayerClient o1, PlayerClient o2) {
            if(o1.isHasFinishedRace() || o2.isHasFinishedRace())
                return 0;

            Checkpoint p1CheckPoint = checkpointManager.getCheckpointByIndex(o1.getNextCheckpointIndex());
            Checkpoint p2CheckPoint = checkpointManager.getCheckpointByIndex(o2.getNextCheckpointIndex());

            int lapCompareVal = Integer.compare(o2.getLap(), o1.getLap());
            if (lapCompareVal == 0) {
                int checkpointCompareVal = Integer.compare(p2CheckPoint.getIndex(), p1CheckPoint.getIndex());
                if (checkpointCompareVal == 0) {
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


