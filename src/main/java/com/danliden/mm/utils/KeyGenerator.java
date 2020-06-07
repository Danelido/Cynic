package com.danliden.mm.utils;

import java.util.SplittableRandom;

public class KeyGenerator {

    private static final String TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static SplittableRandom random = new SplittableRandom();
    private static final int LENGTH = 128;

    /**
     *
     * @return 128 bytes encryption key
     */
    public static String generateKey(){
        StringBuffer keyStrBuffer = new StringBuffer();
        for(int i = 0; i < LENGTH; i++){
            int randIndex = random.nextInt(0, TABLE.length());
            keyStrBuffer.append(TABLE.charAt(randIndex));
        }

        return keyStrBuffer.toString();

    }

}
