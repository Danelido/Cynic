package com.danliden.mm.utils;

public class Randomizer {
    public static float getRandom(float min, float max){
        return(float)(min + Math.random() * (max - min));
    }

}
