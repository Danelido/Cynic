package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;

public class Checkpoint {

    private final Vector2 pivot;
    private final int index;
    private final boolean startFinish;
    private final Vector2 size;
    private final float rotation;

    public Checkpoint(Vector2 pivot, int index, boolean startFinish, Vector2 size, float rotation) {
        this.size = size;
        this.rotation = rotation;
        validateIndex(index);
        this.pivot = pivot;
        this.index = index;
        this.startFinish = startFinish;
    }

    private void validateIndex(int index) {
        if (index < 0 || index == Integer.MAX_VALUE) {
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

    public Vector2 getSize() {
        return size;
    }

    public float getRotation() {
        return rotation;
    }
}
