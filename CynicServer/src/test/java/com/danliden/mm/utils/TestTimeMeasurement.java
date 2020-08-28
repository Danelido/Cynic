package com.danliden.mm.utils;

import org.junit.Test;

public class TestTimeMeasurement {

    @Test
    public void testSecondsToMillis(){
        int seconds = 10;
        int expected = 10 * 1000;

        assert expected == TimeMeasurement.of(seconds, TimeUnits.SECONDS);
    }


    @Test
    public void testMillisToMillis(){
        int millis = 1500;
        int expected = 1500;

        assert expected == TimeMeasurement.of(millis, TimeUnits.MILLISECONDS);
    }
}
