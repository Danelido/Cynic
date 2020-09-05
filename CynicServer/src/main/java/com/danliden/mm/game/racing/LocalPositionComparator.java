package com.danliden.mm.game.racing;

import com.danliden.mm.game.session.PlayerClient;

import java.util.Comparator;

public class LocalPositionComparator implements Comparator<PlayerClient> {
    @Override
    public int compare(PlayerClient client1, PlayerClient client2) {
        return Integer.compare(client1.getLocalPlacement(), client2.getLocalPlacement());
    }
}
