package com.danliden.mm.utils;

/*
    Value returned is in millis.
 */
public class TimeMeasurement {

    public static int of(int timeValue, TimeUnits unit){
        return unit.convertToMs(timeValue);
    }


}
