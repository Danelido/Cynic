package com.danliden.mm.game.racing;


import java.io.IOException;
import java.util.List;

/*
 *
 *  Handles the checkpoints that exists within a current track.
 */
public class CheckpointManager {

    private List<Checkpoint> checkpointList;

    public void loadNewCheckpoints(String filename) throws IOException {
        TrackParser parser = new TrackParser();
        checkpointList = parser.getCheckpointsFromFile(filename);
    }

    public Checkpoint getCheckpointByIndex(int index){
        if(index >= checkpointList.size() || index < 0){
            throw new IndexOutOfBoundsException(String.format("Checkpoint Index out of bounds. Index: {} and number of checkpoints {}",
                    index,
                    checkpointList.size()));
        }
        return checkpointList.get(index);
    }

    // Used for tests
    void setCheckpointList(List<Checkpoint> checkpointList){
        this.checkpointList = checkpointList;
    }

    List<Checkpoint> getAllCheckpoints(){
        return this.checkpointList;
    }


}
