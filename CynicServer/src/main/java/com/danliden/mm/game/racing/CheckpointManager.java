package com.danliden.mm.game.racing;


import java.util.ArrayList;
import java.util.List;

/*
 *
 *  Handles the checkpoints that exists within a current track.
 */
public class CheckpointManager {

    private List<Checkpoint> checkpointList;

    public void loadNewCheckpoints(String filename){
        TrackParser parser = new TrackParser();
        //checkpointList = parser.getCheckpointsFromFile("SpaceYard");
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



}
