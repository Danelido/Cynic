package com.danliden.mm.utils;

public class Encryption {

    public static String encryptOrDecrypt(String input, String key) {
        StringBuilder builder = new StringBuilder();
        while (key.length() < input.length()) {
            key += key;
        }

        for (int i = 0; i < input.length(); i++) {
            int inputCharValue = input.charAt(i);
            int keyCharValue = key.charAt(i);
            int xorValue = inputCharValue ^ keyCharValue;

            builder.append((char) xorValue);
        }

        return builder.toString();
    }

}
