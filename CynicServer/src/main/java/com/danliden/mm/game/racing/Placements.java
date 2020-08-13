package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Placements {

    private static class PlacementComparator implements Comparator<PlayerClient> {

        @Override
        public int compare(PlayerClient o1, PlayerClient o2) {

            if (o1.getNextCheckpoint().getIndex() < o2.getNextCheckpoint().getIndex()) {
                return 1;
            } else if (o1.getNextCheckpoint().getIndex() > o2.getNextCheckpoint().getIndex()) {
                return -1;
            } else {
                // Chasing the same checkpoint
                Vector2 checkpointPosition = o1.getNextCheckpoint().getPivot();
                float playerOneDist = o1.getPosition().distance(checkpointPosition);
                float playerTwoDist = o2.getPosition().distance(checkpointPosition);

                return Float.compare(playerOneDist, playerTwoDist);
            }
        }
    }

    public List<PlayerClient> getPlacements(List<PlayerClient> players) {
        List<PlayerClient> placementList = new ArrayList<>(players);
        placementList.sort(new PlacementComparator());
        return placementList;
    }
}
