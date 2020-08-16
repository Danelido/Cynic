package com.danliden.mm.game.racing;

import org.junit.Test;

import java.io.IOException;

public class TestCheckpointManager {

    @Test
    public void testLoadingSpaceYardCheckpoints() throws IOException {
        CheckpointManager checkpointManager = new CheckpointManager();
        checkpointManager.loadNewCheckpoints("SpaceYard.scfg");
        assert checkpointManager.getAllCheckpoints().size() == 7;
    }
}
