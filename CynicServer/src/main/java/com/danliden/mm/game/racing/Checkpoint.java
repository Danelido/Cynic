package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;

public class Checkpoint {

    private Vector2 pivot;
    private int index;
    private boolean startFinish;

    public Checkpoint(Vector2 pivot, int index, boolean startFinish) {
        validateIndex(index);
        this.pivot = pivot;
        this.index = index;
        this.startFinish = startFinish;
    }

    private void validateIndex(int index){
        if(index < 0 || index == Integer.MAX_VALUE){
            throw new IllegalArgumentException("index must be above 0 and below Integer.MAX_VALUE");
        }
    }

    public Vector2 getPivot() {
        return pivot;
    }

    public int getIndex() {
        return index;
    }

    public boolean isStartFinish() {
        return startFinish;
    }

}
