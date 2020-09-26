package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;

public class StartingPoint {

    private Vector2 position;
    private boolean busy;

    public StartingPoint(Vector2 position) {
        this.position = position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
        return busy;
    }
}
