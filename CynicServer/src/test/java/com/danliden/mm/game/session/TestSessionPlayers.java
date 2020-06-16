package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ServerPacketBundle;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.mock;

public class TestSessionPlayers {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);

    @Test
    public void testCreatingPlayer(){
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getSessionId()).thenReturn(10);

        prepareNewPlayer(dgPacket, 0);
        prepareNewPlayer(dgPacket, 1);

        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);

        assert (sessionPlayers.getNumberOfPlayers() == 2);

    }


    @Test
    public void testAddingPlayerToFullSession(){
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getSessionId()).thenReturn(10);

        prepareNewPlayer(dgPacket, 0);
        prepareNewPlayer(dgPacket, 1);
        prepareNewPlayer(dgPacket, 2);
        prepareNewPlayer(dgPacket, 3);
        prepareNewPlayer(dgPacket, 4);

        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);

        assert (sessionPlayers.getNumberOfPlayers() == 4);

    }

    @Test
    public void testRemovingPlayerFromSession(){
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getSessionId()).thenReturn(10);

        prepareNewPlayer(dgPacket, 0);
        prepareNewPlayer(dgPacket, 1);
        prepareNewPlayer(dgPacket, 2);
        prepareNewPlayer(dgPacket, 3);

        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);
        sessionPlayers.createPlayer(bundle);
        PlayerClient clientToRemove = sessionPlayers.createPlayer(bundle);

        sessionPlayers.removePlayer(clientToRemove.id);

        assert (sessionPlayers.getNumberOfPlayers() == 3);

    }



    private void prepareNewPlayer(final DatagramPacket dgPacket, final int hostAddress) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(hostAddress));
    }
}
