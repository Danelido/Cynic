package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.utils.UniqueId;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SessionPlayers {

    private final List<PlayerClient> players;
    private final UniqueId IDGenerator;
    private final int MAX_PLAYERS;

    public SessionPlayers(final int MAX_PLAYERS) {
        this.MAX_PLAYERS = MAX_PLAYERS;
        players = new ArrayList<>();
        IDGenerator = new UniqueId(MAX_PLAYERS);
    }

    public PlayerClient createPlayer(final ServerPacketBundle bundle) {
        if(!isFull()) {
            PlayerClient client = new PlayerClient(
                    "John Doe",
                    bundle.getDatagramPacket().getAddress(),
                    bundle.getDatagramPacket().getPort(),
                    IDGenerator.getId(),
                    bundle.getSessionId()
            );

            players.add(client);
            return client;
        }

        return null;
    }

    public void removePlayer(final int ID) {
        for (int i = 0; i < players.size(); i++) {
            PlayerClient player = players.get(i);

            if (player.id == ID) {
                IDGenerator.giveBackID(player.id);
                players.remove(i);
                return;
            }
        }

    }

    public PlayerClient findById(final int ID) {
        for (PlayerClient player : players) {
            if (player.id == ID) {
                return player;
            }
        }
        return null;
    }

    public PlayerClient findByAddressAndPort(final InetAddress address, final int port) {
        for (PlayerClient player : players) {
            if (player.address.getHostAddress().equals(address.getHostAddress()) && player.port == port) {
                return player;
            }
        }
        return null;
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public boolean isFull() {
        return getNumberOfPlayers() == MAX_PLAYERS;
    }

    public List<PlayerClient> getPlayers() {
        return players;
    }
}
