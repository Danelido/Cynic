package com.danliden.mm.utils;

public enum TimeUnits {
    SECONDS(1000),
    MILLISECONDS(1);

    private int inMillis;
    TimeUnits(int inMillis){
        this.inMillis = inMillis;
    }
    public int convertToMs(int timeValue){
        return timeValue * inMillis;
    }
}
