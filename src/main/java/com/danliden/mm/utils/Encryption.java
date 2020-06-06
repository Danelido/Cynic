package com.danliden.mm.utils;

public class Encryption {

    public static String encryptOrDecrypt(String input, String key) {
        StringBuilder builder = new StringBuilder();
        while (key.length() < input.length() / 2) {
            key += key;
        }

        for (int i = 0; i < input.length(); i++) {
            String hexValueString = input.substring(i, i + 2);
            int hexToInt = Integer.parseInt(hexValueString, 16);
            int keyCharToInt = key.charAt(i / 2);
            int xorValue = hexToInt ^ keyCharToInt;
            builder.append((char) xorValue);
        }

        return builder.toString();
    }

}
