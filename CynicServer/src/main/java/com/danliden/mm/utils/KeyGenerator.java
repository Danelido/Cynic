package com.danliden.mm.utils;

import java.util.SplittableRandom;

public class KeyGenerator {

    public static final int LENGTH = 128;
    private static final String TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SplittableRandom random = new SplittableRandom();

    /**
     * @return 128 bytes encryption key
     */
    public static String generateKey() {
        StringBuilder keyStrBuffer = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int randIndex = random.nextInt(0, TABLE.length());
            keyStrBuffer.append(TABLE.charAt(randIndex));
        }

        return keyStrBuffer.toString();

    }
}
