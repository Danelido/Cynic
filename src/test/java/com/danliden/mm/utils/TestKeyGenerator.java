package com.danliden.mm.utils;

import org.junit.Test;

public class TestKeyGenerator {

    @Test
    public void testKeyLength() {
        final String key = KeyGenerator.generateKey();
        assert (key.length() == KeyGenerator.LENGTH);
    }

}
