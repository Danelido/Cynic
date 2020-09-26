package com.danliden.mm.game.racing;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 *
 *  Handles the checkpoints that exists within a current track.
 */
public class TrackManager {

    private TrackConfig trackConfig = new TrackConfig(0,0, new ArrayList<>(), new ArrayList<>());

    public void loadTrack(String filename) throws IOException {
        TrackParser parser = new TrackParser();
        trackConfig = parser.parseTrack(filename);
    }

    public Checkpoint getCheckpointByIndex(int index){
        List<Checkpoint> checkpoints = trackConfig.getCheckpoints();
        if(index >= checkpoints.size() || index < 0){
            throw new IndexOutOfBoundsException(String.format("Checkpoint Index out of bounds. Index: {} and number of checkpoints {}",
                    index,
                    checkpoints.size()));
        }
        return checkpoints.get(index);
    }

    public StartingPoint getEmptyStartingPoint(){
        for (StartingPoint startingPoint: trackConfig.getStartingPoints()) {
            if(!startingPoint.isBusy()) {
                startingPoint.setBusy(true);
                return startingPoint;
            }
        }

        return null;
    }

    // Used for tests
    public void setCheckpointList(List<Checkpoint> checkpointList){
        this.trackConfig.setCheckpoints(checkpointList);;
    }

    public List<Checkpoint> getAllCheckpoints(){
        return this.trackConfig.getCheckpoints();
    }
    public List<StartingPoint> getAllStartingPoints() { return this.trackConfig.getStartingPoints(); }

    public TrackConfig getCurrentTrackConfig() {
        return this.trackConfig;
    }
}
