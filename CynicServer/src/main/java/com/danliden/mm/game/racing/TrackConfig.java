package com.danliden.mm.game.racing;

import java.util.List;

public class TrackConfig {

    private int nrOfCheckpoints;
    private int nrOfStartingPoints;
    private List<Checkpoint> checkpoints;
    private List<StartingPoint> startingPoints;

    public TrackConfig(int nrOfCheckpoints, int nrOfStartingPoints, List<Checkpoint> checkpoints, List<StartingPoint> startingPoints) {
        this.nrOfCheckpoints = nrOfCheckpoints;
        this.nrOfStartingPoints = nrOfStartingPoints;
        this.checkpoints = checkpoints;
        this.startingPoints = startingPoints;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public List<StartingPoint> getStartingPoints() {
        return startingPoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void setStartingPoints(List<StartingPoint> startingPoints) {
        this.startingPoints = startingPoints;
    }

    public int getNrOfCheckpoints() {
        return nrOfCheckpoints;
    }

    public int getNrOfStartingPoints() {
        return nrOfStartingPoints;
    }
}
