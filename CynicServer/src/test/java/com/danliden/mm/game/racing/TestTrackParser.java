package com.danliden.mm.game.racing;

import org.junit.Test;

import java.io.IOException;

public class TestTrackParser {

    @Test
    public void testParsingValidFile() throws IOException {
        TrackParser parser = new TrackParser();
        TrackConfig trackConfig = parser.parseTrack(Tracks.SPACE_YARD);
        assert trackConfig.getCheckpoints().size() != 0;
        assert trackConfig.getStartingPoints().size() != 0;
    }
}
