package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestJoinSession {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testAddingNewPlayer() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        prepareNewPlayer(dgPacket, 0);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 1);

        Mockito.verify(senderMock, Mockito.times(1))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(1))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

    }

    @Test
    public void testAddingNewPlayerWithExistingPlayers() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, 1);
        addPlayer(sessionPlayers, dgPacket, 2);
        addPlayer(sessionPlayers, dgPacket, 3);

        prepareNewPlayer(dgPacket, 4);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 4);

        Mockito.verify(senderMock, Mockito.times(4))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(1))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

    }

    @Test
    public void testAddingNewPlayerWhenFull() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, 1);
        addPlayer(sessionPlayers, dgPacket, 2);
        addPlayer(sessionPlayers, dgPacket, 3);
        addPlayer(sessionPlayers, dgPacket, 4);

        prepareNewPlayer(dgPacket, 5);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 4);

        Mockito.verify(senderMock, Mockito.times(0))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(0))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

        Mockito.verify(senderMock, Mockito.times(1)).sendToAddress(Mockito.any(), Mockito.any(), Mockito.anyInt());
    }

    @Test
    public void testAddingNewPlayerWhenStateIsNotLobby() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        // Unique INetAddresses for the players
        addPlayer(sessionPlayers, dgPacket, 1);
        addPlayer(sessionPlayers, dgPacket, 2);
        prepareNewPlayer(dgPacket, 3);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 2);

        Mockito.verify(senderMock, Mockito.times(0))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(0))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

        Mockito.verify(senderMock, Mockito.times(1)).sendToAddress(Mockito.any(), Mockito.any(), Mockito.anyInt());
    }

    @Test
    public void testAddingExistingPlayer() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, 1);
        addPlayer(sessionPlayers, dgPacket, 2);
        addPlayer(sessionPlayers, dgPacket, 3);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 3);

        Mockito.verify(senderMock, Mockito.times(0))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(0))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

    }

    @Test
    public void testAddingNewPlayerWithExistingPlayersBeingPartiallyReady() {
        // Setup
        IPacketLogic joinSessionLogic = new JoinSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, 1);
        addPlayer(sessionPlayers, dgPacket, 2);
        addPlayer(sessionPlayers, dgPacket, 3);

        prepareNewPlayer(dgPacket, 4);

        // Execute logic
        joinSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Verify
        assert (sessionPlayers.getNumberOfPlayers() == 4);

        Mockito.verify(senderMock, Mockito.times(4))
                .sendWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        Mockito.verify(senderMock, Mockito.times(1))
                .sendToMultipleWithAckAndExclude(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());

    }

    private void addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket, final int id) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(id));
        sessionPlayers.createPlayer(bundle);
    }

    private void prepareNewPlayer(final DatagramPacket dgPacket, final int id) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(id));
    }
}
