package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.racing.StartingPoint;
import com.danliden.mm.game.racing.TrackManager;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.Vector2;
import com.danliden.mm.utils.Vector3;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
    private final TrackManager trackManagerMock = mock(TrackManager.class);
    private final String VALID_SHIP_NAME = "ValidName";
    private final Vector3 COLOR = new Vector3(1.0f, 0.5f, 1.0f);

    @Before
    public void Before() {
        List<StartingPoint> startingPoints = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            startingPoints.add(new StartingPoint(new Vector2(i, i)));
        }

        Mockito.when(trackManagerMock.getAllStartingPoints()).thenReturn(startingPoints);
    }

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

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id, VALID_SHIP_NAME, COLOR);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

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

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id, VALID_SHIP_NAME, COLOR);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

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

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        // Mark all existing players as ready
        for (int i = 0; i < sessionPlayers.getPlayers().size(); i++) {
            PlayerClient client = sessionPlayers.getPlayers().get(i);
            if (client.id != player.id) {
                client.setIsReady(true);
            }
        }

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id, VALID_SHIP_NAME, COLOR);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

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

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

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

        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id + 1, VALID_SHIP_NAME, COLOR);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(1)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    @Test
    public void testAddingVoteFromPlayerWithEmptyShipName() {
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

        String INVALID_SHIP_NAME = "";
        JSONObject playerAddVotePacket = createPlayerAddVotePacket(player.id, INVALID_SHIP_NAME, COLOR);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerAddVotePacket);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state);
        voteToStartSession.execute(properties);

        assert (!player.isReady());
        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
    }

    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createPlayerAddVotePacket(int playerId, String shipName, Vector3 color) {
        return new JSONObject()
                .put(PacketKeys.PlayerId, playerId)
                .put(PacketKeys.ShipPrefabName, shipName)
                .put(PacketKeys.ShipRedComponent, color.x)
                .put(PacketKeys.ShipGreenComponent, color.y)
                .put(PacketKeys.ShipBlueComponent, color.z);
    }

    private Properties createProperties(ServerPacketBundle bundle, PacketSender senderMock, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState state) {
        Properties properties = new Properties();
        return properties.setBundle(bundle)
                .setPacketSender(senderMock)
                .setSessionAckHandler(ackHandler)
                .setSessionPlayers(sessionPlayers)
                .setGameState(state)
                .setTrackManager(trackManagerMock);
    }
}
