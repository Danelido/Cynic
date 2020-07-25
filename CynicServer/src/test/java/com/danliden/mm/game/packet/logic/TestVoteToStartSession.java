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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestVoteToStartSession {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testAddingVoteFromPlayerWithNoOtherPlayers() {
        // Setup
        IPacketLogic voteToStartSession = new VoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        voteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (!player.isReady());
        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testAddingVoteFromPlayerAndNotAllIsReady() {
        // Setup
        IPacketLogic voteToStartSession = new VoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket).setIsReady(true);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);

        assert (player != null);

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        voteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (player.isReady());
        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(1)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testAddingVoteFromPlayerAndAllIsReady() {
        // Setup
        IPacketLogic voteToStartSession = new VoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket).setIsReady(true);
        addPlayer(sessionPlayers, dgPacket).setIsReady(true);
        addPlayer(sessionPlayers, dgPacket).setIsReady(true);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);

        assert (player != null);

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        voteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (player.isReady());
        assert (state.getGameState() == GameState.GameStateEnum.IN_SESSION);

        verify(senderMock, times(2)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testAddingVoteFromPlayerWhenInSession() {
        // Setup
        IPacketLogic voteToStartSession = new VoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        voteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testAddingVoteFromNonExistingPlayer() {
        // Setup
        IPacketLogic voteToStartSession = new VoteToStartSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id + 1);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        voteToStartSession.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(1)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }


    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createPlayerAddVotePacket(int playerId) {
        return new JSONObject().put(PacketKeys.PlayerId, playerId);
    }

}
