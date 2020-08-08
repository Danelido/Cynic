package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.utils.UniqueId;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SessionPlayers {

    private final List<PlayerClient> players;
    private final UniqueId idGenerator;
    private final int maxPlayers;

    public SessionPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
        players = new ArrayList<>();
        idGenerator = new UniqueId(maxPlayers);
    }

    public PlayerClient createPlayer(final ServerPacketBundle bundle) {
        synchronized (players) {
            if (!isFull()) {
                undoAllVotesToStartSession();
                PlayerClient client = new PlayerClient(
                        "John Doe",
                        bundle.getDatagramPacket().getAddress(),
                        bundle.getDatagramPacket().getPort(),
                        idGenerator.getId(),
                        bundle.getSessionId()
                );

                players.add(client);
                return client;
            }
        }
        return null;
    }

    public void removePlayer(final int ID) {
        synchronized (players) {
            for (int i = 0; i < players.size(); i++) {
                PlayerClient player = players.get(i);
                if (player.id == ID) {
                    idGenerator.giveBackID(player.id);
                    players.remove(i);
                    break;
                }
            }
            undoAllVotesToStartSession();
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

    private void undoAllVotesToStartSession() {
        players.forEach(playerClient -> playerClient.setIsReady(false));
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public boolean isFull() {
        return getNumberOfPlayers() == maxPlayers;
    }

    public List<PlayerClient> getPlayers() {
        return players;
    }

    public void setClientReady(PlayerClient client, boolean ready) {
        synchronized (players) {
            client.setIsReady(ready);
        }
    }
}
