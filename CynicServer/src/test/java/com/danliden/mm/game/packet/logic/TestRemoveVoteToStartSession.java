package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestRemoveVoteToStartSession {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testRemovingVoteFromPlayer() {
        // Setup
        IPacketLogic removeVoteToStartSession = new RemoveVoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerRemoveVotePacket = createPlayerRemoveVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerRemoveVotePacket);

        // Execute logic
        removeVoteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (!player.isReady() );

        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testRemovingVoteFromPlayerWhenInSession() {
        // Setup
        IPacketLogic removeVoteToStartSession = new RemoveVoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerRemoveVotePacket = createPlayerRemoveVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerRemoveVotePacket);

        // Execute logic
        removeVoteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Build the expected outgoing packet
        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testRemovingVoteFromNonExistingPlayer() {
        // Setup
        IPacketLogic removeVoteToStartSession = new RemoveVoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerRemoveVotePacket = createPlayerRemoveVotePacket(player.id + 1);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerRemoveVotePacket);

        // Execute logic
        removeVoteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createPlayerRemoveVotePacket(int playerId) {
        return new JSONObject().put(PacketKeys.PlayerId, playerId);
    }
}
