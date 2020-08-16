package com.danliden.mm.game.racing;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestTrackParser {

    @Test
    public void testParsingValidFile() throws IOException {
        TrackParser parser = new TrackParser();
        List<Checkpoint> checkpointsFromFile = parser.getCheckpointsFromFile(Tracks.SPACE_YARD);
        assert checkpointsFromFile.size() == 7;
    }
}
