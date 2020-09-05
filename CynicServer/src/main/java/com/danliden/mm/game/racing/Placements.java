package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Placements {


    public List<PlayerClient> getPlacements(List<PlayerClient> players, CheckpointManager checkpointManager) {
        if (!hasValidIndexes(players)) {
            return new ArrayList<>();
        }
        List<PlayerClient> placementList = new ArrayList<>(players);
        placementList.sort(new PlacementsCheckpointComparator(checkpointManager));
        return placementList;
    }

    public List<PlayerClient> getPlacementsFromLocalPositions(List<PlayerClient> players){
        List<PlayerClient> placementList = new ArrayList<>(players);
        placementList.sort(new LocalPositionComparator());
        return placementList;
    }

    public boolean hasValidIndexes(List<PlayerClient> players) {
        for (PlayerClient player : players) {
            if (player.getNextCheckpointIndex() == -1) {
                return false;
            }
        }
        return true;
    }
}
