package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.utils.Randomizer;
import com.danliden.mm.utils.Vector2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestPlacements {

    private Placements placements = new Placements();
    private CheckpointManager checkpointManager = new CheckpointManager();

    @Test
    public void testPlacementsTargetingSameCheckpoint() {
        List<PlayerClient> orderedList = new ArrayList<>();
        List<PlayerClient> shuffledList = new ArrayList<>();
        // fix placement algorithm
        for (int i = 0; i < 25; i++) {
            PlayerClient player = AddPlayer(i, 0, 0, new Vector2(100.0f, 100.0f - i));
            orderedList.add(player);
            shuffledList.add(player);
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);
        checkpointManager.setCheckpointList(generateDummyCheckpoints(25));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    @Test
    public void testPlacementsTargetingDifferentCheckpoints() {
        List<PlayerClient> orderedList = new ArrayList<>();


        List<PlayerClient> shuffledList = new ArrayList<>();
        int checkpointIndex = 25;
        for (int i = 0; i < 50; i++) {
            PlayerClient player = AddPlayer(i, checkpointIndex,0, new Vector2(100.0f, 100.0f - i));
            orderedList.add(player);
            shuffledList.add(player);

            if (i % 5 == 0) {
                checkpointIndex--;
            }
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);

        checkpointManager.setCheckpointList(generateDummyCheckpoints(50));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    @Test
    public void testPlacementsOnInvalidIndexes() {
        List<PlayerClient> orderedList = new ArrayList<>();
        List<PlayerClient> shuffledList = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            PlayerClient player = AddPlayer(i, -1, 0, new Vector2(100.0f, 100.0f - i));
            orderedList.add(player);
            shuffledList.add(player);
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);
        checkpointManager.setCheckpointList(generateDummyCheckpoints(25));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == 0;
    }

    @Test
    public void testPlacementsTargetingDifferentCheckpointsAndOnDifferentLaps() {
        List<PlayerClient> orderedList = new ArrayList<>();


        List<PlayerClient> shuffledList = new ArrayList<>();
        int checkpointIndex = 25;
        int currentLap = 25;
        for (int i = 0; i < 50; i++) {
            PlayerClient player = AddPlayer(i, checkpointIndex, currentLap, new Vector2(100.0f, 100.0f - i));
            orderedList.add(player);
            shuffledList.add(player);

            if (i % 10 == 0) {
                currentLap--;
            }


            if (i % 5 == 0) {
                checkpointIndex--;
            }
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);

        checkpointManager.setCheckpointList(generateDummyCheckpoints(50));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    @Test
    public void testDifferentCheckpointsButOnePlayerHasFinished(){
        List<PlayerClient> orderedList = new ArrayList<>();
        List<PlayerClient> shuffledList = new ArrayList<>();

        int checkpointIndex = 25;
        for (int i = 0; i < 50; i++) {
            PlayerClient player = AddPlayer(i, checkpointIndex,0, new Vector2(100.0f, 100.0f - i));
            orderedList.add(player);
            shuffledList.add(player);

            if (i % 5 == 0) {
                checkpointIndex--;
            }
        }

        // Shuffle the shuffledList
        Collections.shuffle(shuffledList);

        // Add a player that has finished, checkpoint index and such should not matter
        PlayerClient player = AddPlayer(99, 0, 4, new Vector2(0.0f, 0.0f));
        player.setHasFinishedRace(true);
        shuffledList.add(0, player);
        orderedList.add(0, player);

        checkpointManager.setCheckpointList(generateDummyCheckpoints(50));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    @Test
    public void TestSortingPlayersInEndGameScenario(){
        List<PlayerClient> orderedList = new ArrayList<>();
        List<PlayerClient> shuffledList = new ArrayList<>();

        PlayerClient dummy1 = AddPlayer(10, 3, 2, new Vector2(100.0f, 50.0f));
        dummy1.setLocalPlacement(1);
        dummy1.setHasFinishedRace(true);

        PlayerClient dummy2 = AddPlayer(12, 2, 2, new Vector2(100.0f, 90.0f));
        dummy2.setLocalPlacement(2);
        dummy2.setHasFinishedRace(true);

        PlayerClient dummy3 = AddPlayer(11, 2, 1, new Vector2(100.0f, 80.0f));
        PlayerClient dummy4 = AddPlayer(13, 2, 1, new Vector2(100.0f, 50.0f));

        orderedList.add(dummy1);
        orderedList.add(dummy2);
        orderedList.add(dummy3);
        orderedList.add(dummy4);

        shuffledList.addAll(orderedList);
        Collections.shuffle(shuffledList);

        checkpointManager.setCheckpointList(generateDummyCheckpoints(5));
        List<PlayerClient> placementList = placements.getPlacements(shuffledList, checkpointManager);

        assert placementList.size() == orderedList.size();

        for (int i = 0; i < orderedList.size(); i++) {
            assert orderedList.get(i).id == placementList.get(i).id;
        }
    }

    private PlayerClient AddPlayer(int playerId, int checkpointIndex, int lap, Vector2 playerPos) {
        String name = "John Doe";
        int sessionId = 0;
        int port = 1234;
        PlayerClient player = new PlayerClient(name, null, port, playerId, sessionId);
        player.setPosition(playerPos);
        player.setLap(lap);
        player.setNextCheckpointIndex(checkpointIndex);

        return player;
    }

    private List<Checkpoint> generateDummyCheckpoints(int size){
        List<Checkpoint> checkpoints= new ArrayList<>();

        for(int i = 0; i < size; i ++){
            Vector2 pivot = new Vector2(200, 200 + (i * 2) + 15);
            boolean startFinish = i == 0;
            Checkpoint cp = new Checkpoint(pivot, i, startFinish, Vector2.Zero(), 0.0f);
            checkpoints.add(i, cp);
        }

        return checkpoints;
    }

}
