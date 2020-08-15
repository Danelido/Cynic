package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;
import org.junit.Test;

public class TestCheckpoint {

    @Test
    public void TestCheckpointReturnValues(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        Vector2 size = new Vector2(10.12f, 98.82f);
        float rotation = 0.0f;
        int index = 0;
        boolean startFinish = false;
        Checkpoint checkpoint = new Checkpoint(pivot, index, startFinish, size, rotation);

        assert checkpoint.getPivot().equalsTo(pivot);
        assert checkpoint.getIndex() == index;
        assert checkpoint.isStartFinish() == startFinish;
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestNegativeIndex(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        int index = -1;
        boolean startFinish = false;
        Vector2 size = new Vector2(10.12f, 98.82f);
        float rotation = 0.0f;
        new Checkpoint(pivot, index, startFinish, size, rotation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestTooLargeIndex(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        int index = Integer.MAX_VALUE;
        boolean startFinish = false;
        Vector2 size = new Vector2(10.12f, 98.82f);
        float rotation = 0.0f;
        new Checkpoint(pivot, index, startFinish, size, rotation);
    }

}
