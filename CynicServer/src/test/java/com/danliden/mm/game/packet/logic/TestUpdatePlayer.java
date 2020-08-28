package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.racing.DoomTimer;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.Vector2;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.*;

public class TestUpdatePlayer {
    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);
    private final DoomTimer doomTimer = mock(DoomTimer.class);

    @Test
    public void testUpdatingValidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, rotation, throttling, newPosition, 0);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);

        assert (player.getRotationDegrees() == rotation);
        assert (player.getPosition().equalsTo(newPosition));
        assert (player.isThrottling());

        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
    }

    @Test
    public void testUpdatingValidPlayerInLobby() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, rotation, throttling, newPosition, 0);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);

        assert (player.getRotationDegrees() != rotation);
        assert (!player.getPosition().equalsTo(newPosition));
        assert (!player.isThrottling());

        // Build the expected outgoing packet
        verify(senderMock, times(0)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
    }

    @Test
    public void testUpdatingInvalidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(-1, rotation, throttling, newPosition, 0);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);

        assert (sessionPlayers.findById(player.id) != null);
        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendNotConnectedPacketToSender(bundle);
    }

    @Test
    public void testUpdatingValidPlayerCrossesFinishLineShouldActivateDoomTimer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, rotation, throttling, newPosition, 4);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);

        assert (player.getRotationDegrees() == rotation);
        assert (player.getPosition().equalsTo(newPosition));
        assert (player.isThrottling());
        assert (player.isHasFinishedRace());

        verify(senderMock, times(1)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
        verify(doomTimer, times(1)).startCountdown(anyInt());
    }

    @Test
    public void testUpdatingValidPlayerCrossesFinishLineShouldActivateDoomTimerWithMultiplePlayers() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        PlayerClient player2 = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player2 != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;

        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, rotation, throttling, newPosition, 4);
        JSONObject playerJsonUpdatedData2 = createPlayerUpdateData(player2.id, rotation, throttling, newPosition, 2);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData2);
        updatePlayerLogic.execute(properties);

        assert (player.getRotationDegrees() == rotation);
        assert (player.getPosition().equalsTo(newPosition));
        assert (player.isThrottling());
        assert (player.isHasFinishedRace());

        assert (player2.getRotationDegrees() == rotation);
        assert (player2.getPosition().equalsTo(newPosition));
        assert (player2.isThrottling());
        assert (!player2.isHasFinishedRace());

        verify(senderMock, times(2)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
        verify(doomTimer, times(1)).startCountdown(anyInt());
    }

    @Test
    public void testUpdatingValidPlayersCrossesFinishLineShouldActivateDoomTimer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);

        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        PlayerClient player2 = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player2 != null);

        Vector2 newPosition = new Vector2(400, -200);
        float rotation = 45.0f;
        boolean throttling = true;

        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, rotation, throttling, newPosition, 4);
        JSONObject playerJsonUpdatedData2 = createPlayerUpdateData(player2.id, rotation, throttling, newPosition, 4);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        Properties properties = createProperties(bundle, senderMock, ackHandler, sessionPlayers, state, doomTimer);
        updatePlayerLogic.execute(properties);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData2);
        updatePlayerLogic.execute(properties);

        assert (player.getRotationDegrees() == rotation);
        assert (player.getPosition().equalsTo(newPosition));
        assert (player.isThrottling());
        assert (player.isHasFinishedRace());

        assert (player2.getRotationDegrees() == rotation);
        assert (player2.getPosition().equalsTo(newPosition));
        assert (player2.isThrottling());
        assert (player2.isHasFinishedRace());

        verify(senderMock, times(2)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
        verify(doomTimer, times(1)).startCountdown(anyInt());
    }


    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket, final int hostAddressMock) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(hostAddressMock));
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createPlayerUpdateData(int playerId, float rotation, boolean throttling, Vector2 position, int lap) {
        return new JSONObject()
                .put(PacketKeys.PlayerId, playerId)
                .put(PacketKeys.PlayerRotation, rotation)
                .put(PacketKeys.Throttling, throttling)
                .put(PacketKeys.PlayerXPos, position.x)
                .put(PacketKeys.PlayerYPos, position.y)
                .put(PacketKeys.NextCheckpointIndex, 0)
                .put(PacketKeys.PlayerLap, lap);
    }

    private Properties createProperties(ServerPacketBundle bundle, PacketSender senderMock, SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState state, DoomTimer doomTimer) {
        Properties properties = new Properties();
        return properties.setBundle(bundle)
                .setPacketSender(senderMock)
                .setSessionAckHandler(ackHandler)
                .setSessionPlayers(sessionPlayers)
                .setGameState(state)
                .setDoomTimer(doomTimer);
    }
}
