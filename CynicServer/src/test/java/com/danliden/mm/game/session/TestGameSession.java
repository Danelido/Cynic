package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.server.PacketSender;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.mock;

public class TestGameSession {
    private final ServerPacketBundle bundleMock = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacketMock = mock(DatagramPacket.class);

    @Before
    public void before() {
        Mockito.when(bundleMock.getSessionId()).thenReturn(10100);
        Mockito.when(bundleMock.getDatagramPacket()).thenReturn(dgPacketMock);
        Mockito.when(dgPacketMock.getPort()).thenReturn(2020);
    }

    @Test
    @Ignore
    public void testOnServerHeartbeat(){
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        addPlayer(gameSession, dgPacketMock, 0);
        addPlayer(gameSession, dgPacketMock, 1);
    }

    private PlayerClient addPlayer(GameSession session, final DatagramPacket dgPacket, final int id) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(id));

        return session.properties.sessionPlayers.createPlayer(bundleMock);
    }



}
