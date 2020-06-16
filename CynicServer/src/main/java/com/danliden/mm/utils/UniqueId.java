package com.danliden.mm.utils;

import java.util.ArrayList;
import java.util.List;

public class UniqueId {

    private final List<Integer> ids = new ArrayList<>();
    private int range;
    private int expandSize = 100;

    public UniqueId(int startRange) {
        this.range = startRange;

    }

    public void giveBackID(int id) {
        ids.add(id);
    }

    public int getId() {
        if (ids.size() == 0) {
            expand();
        }

        int id = ids.get(ids.size() - 1);
        ids.remove(ids.size() - 1);
        return id;
    }

    private void setExpandSize(int newExpandSize) {
        expandSize = newExpandSize;
    }

    private void expand() {
        int startIndexOfNewValues = range;
        range += expandSize;
        fillList(startIndexOfNewValues);
    }

    private void fillList(int startingIndex) {
        for (int i = startingIndex; i < range; i++) {
            ids.add(i);
        }
    }

    public int getRange() {
        return range;
    }

    public int getExpandSize() {
        return expandSize;
    }
}
