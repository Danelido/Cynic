package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;
import org.junit.Test;

public class TestCheckpoint {

    @Test
    public void TestCheckpointReturnValues(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        int index = 0;
        boolean startFinish = false;
        Checkpoint checkpoint = new Checkpoint(pivot, index, startFinish);

        assert checkpoint.getPivot().equalsTo(pivot);
        assert checkpoint.getIndex() == index;
        assert checkpoint.isStartFinish() == startFinish;
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestNegativeIndex(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        int index = -1;
        boolean startFinish = false;
        new Checkpoint(pivot, index, startFinish);
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestTooLargeIndex(){
        Vector2 pivot = new Vector2(102.12f, -98.82f);
        int index = Integer.MAX_VALUE;
        boolean startFinish = false;
        new Checkpoint(pivot, index, startFinish);
    }

}
