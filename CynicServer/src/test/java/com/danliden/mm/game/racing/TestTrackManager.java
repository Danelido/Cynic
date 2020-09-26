package com.danliden.mm.game.racing;

import org.junit.Test;

import java.io.IOException;

public class TestTrackManager {

    @Test
    public void testLoadingSpaceYardCheckpoints() throws IOException {
        TrackManager trackManager = new TrackManager();
        trackManager.loadTrack(Tracks.SPACE_YARD);
        assert trackManager.getAllCheckpoints().size() == trackManager.getCurrentTrackConfig().getNrOfCheckpoints();
        assert trackManager.getAllStartingPoints().size() == trackManager.getCurrentTrackConfig().getNrOfStartingPoints();
    }
}
