package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Vector2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPlacements {

    private Placements placements = new Placements();

    @Test
    public void TestPlacementsTargetingSameCheckpoint() {
        List<PlayerClient> orderedList = new ArrayList<>();


        List<PlayerClient> shuffledList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            PlayerClient player = AddPlayer(i, 0, new Vector2(100.0f, 100.0f - i),
                    new Vector2(110.0f, 110.f), false);
            orderedList.add(player);
            shuffledList.add(player);
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);

        List<PlayerClient> placementList = placements.getPlacements(shuffledList);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    @Test
    public void TestPlacementsTargetingDifferentCheckpoints() {
        List<PlayerClient> orderedList = new ArrayList<>();


        List<PlayerClient> shuffledList = new ArrayList<>();
        int checkpointIndex = 25;
        for (int i = 0; i < 50; i++) {
            PlayerClient player = AddPlayer(i, checkpointIndex, new Vector2(100.0f, 100.0f - i),
                    new Vector2(110.0f, 110.f), false);
            orderedList.add(player);
            shuffledList.add(player);

            if (i % 5 == 0) {
                checkpointIndex--;
            }
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);

        List<PlayerClient> placementList = placements.getPlacements(shuffledList);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    private PlayerClient AddPlayer(int playerId, int checkpointIndex, Vector2 playerPos, Vector2 checkpointPos, boolean startFinish) {
        String name = "John Doe";
        int sessionId = 0;
        int port = 1234;
        PlayerClient player = new PlayerClient(name, null, port, playerId, sessionId);
        player.setPosition(playerPos);

        Checkpoint checkpoint = new Checkpoint(checkpointPos, checkpointIndex, startFinish);
        player.setNextCheckpoint(checkpoint);

        return player;
    }

}
