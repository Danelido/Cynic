package com.danliden.mm.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUniqueId {

    @Test
    public void testAllUniqueNumbers() {
        UniqueId idGen = new UniqueId(100);
        Set<Integer> cache = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            cache.add(idGen.getId());
        }

        assert (cache.size() == 100);

    }

    @Test
    public void testAllUniqueNumbersWhenOverflowing() {
        UniqueId idGen = new UniqueId(100);
        Set<Integer> cache = new HashSet<>();

        for (int i = 0; i < 250; i++) {
            cache.add(idGen.getId());
        }

        assert (cache.size() == 250);

    }

    @Test
    public void testGivingBackNumbers() {
        UniqueId idGen = new UniqueId(100);
        List<Integer> idHolder = new ArrayList<>();
        Set<Integer> cache = new HashSet<>();

        for (int i = 0; i < 120; i++) {
            idHolder.add(idGen.getId());
        }

        for (Integer id : idHolder) {
            idGen.giveBackID(id);
        }

        int range = idGen.getRange();

        for (int i = 0; i < range; i++) {
            cache.add(idGen.getId());
        }

        assert (cache.size() == range);
    }


}

