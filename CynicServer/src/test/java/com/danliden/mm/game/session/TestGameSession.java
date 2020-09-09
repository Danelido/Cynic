package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.game.racing.DoomTimer;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.utils.GameState;
import com.danliden.mm.utils.TimeMeasurement;
import com.danliden.mm.utils.TimeUnits;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.*;

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
    public void testOnServerHeartbeat(){
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        addPlayer(gameSession, dgPacketMock, 0);
        addPlayer(gameSession, dgPacketMock, 1);

        gameSession.onServerHeartbeat();

        for(PlayerClient client : gameSession.properties.sessionPlayers.getPlayers()){
            assert client.getNrOfFlatLines() == 1;
        }

        verify(senderMock, times(1))
                .sendToMultiple(any(JSONObject.class), anyList());
    }

    @Test
    public void testDisconnectPlayerOnNoHeartbeatResponse(){
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        addPlayer(gameSession, dgPacketMock, 0);
        addPlayer(gameSession, dgPacketMock, 1);

        for(int i = 0; i < GameSession.MAX_FLAT_LINES; i++){
            gameSession.onServerHeartbeat();
        }

        gameSession.onServerUpdate(0);

        assert gameSession.properties.sessionPlayers.getPlayers().size() == 0;
        verify(senderMock, times(GameSession.MAX_FLAT_LINES))
                .sendToMultiple(any(JSONObject.class), anyList());
        verify(senderMock, times(2))
                .sendToMultipleWithAck(any(SessionAckHandler.class),
                        any(JSONObject.class),
                        anyList(),
                        anyInt(),
                        anyInt());
    }

    @Test
    public void testStateChangeFromDoomTimer(){
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        addPlayer(gameSession, dgPacketMock, 0);
        addPlayer(gameSession, dgPacketMock, 1);

        gameSession.properties.gameState
                .setGameState(GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);

        gameSession.properties.doomTimer.startCountdown(TimeMeasurement.of(2, TimeUnits.SECONDS));
        gameSession.onServerUpdate(TimeMeasurement.of(2, TimeUnits.SECONDS));

        assert gameSession.properties.gameState.getGameState() == GameState.GameStateEnum.IN_SESSION_END;
    }

    @Test(timeout = 15000)
    public void testStateChangeFromEndGameTask() throws InterruptedException {
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        addPlayer(gameSession, dgPacketMock, 0);
        addPlayer(gameSession, dgPacketMock, 1);

        gameSession.properties.gameState
                .setGameState(GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);

        gameSession.TIME_UNTIL_LOBBY_FROM_SCOREBOARD_MS =TimeMeasurement.of(3, TimeUnits.SECONDS);
        gameSession.properties.doomTimer.startCountdown(TimeMeasurement.of(2, TimeUnits.SECONDS));
        gameSession.onServerUpdate(TimeMeasurement.of(2, TimeUnits.SECONDS));

        assert gameSession.properties.gameState.getGameState() == GameState.GameStateEnum.IN_SESSION_END;
        waitForStateChangeFromTimedExecution(gameSession.properties);

        verify(senderMock, times(9))
                .sendToMultipleWithAck(any(SessionAckHandler.class),
                        any(JSONObject.class),
                        anyList(),
                        anyInt(),
                        anyInt());

        assert gameSession.properties.gameState.getGameState() == GameState.GameStateEnum.LOBBY;
    }

    @Test
    public void testExitingToLobbyIfNoPlayerExists(){
        GameSession gameSession = new GameSession(senderMock, 0);
        gameSession.updateProperties(bundleMock);

        gameSession.properties.gameState
                .setGameState(GameState.GameStateEnum.LOBBY);
        gameSession.onServerUpdate(0);
        assert gameSession.properties.gameState.getGameState() == GameState.GameStateEnum.LOBBY;
    }

    private PlayerClient addPlayer(GameSession session, final DatagramPacket dgPacket, final int id) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(id));

        return session.properties.sessionPlayers.createPlayer(bundleMock);
    }

    private void waitForStateChangeFromTimedExecution(Properties properties) throws InterruptedException {
        while(properties.gameState.getGameState() != GameState.GameStateEnum.LOBBY){
            Thread.sleep(1000);
        }
    }




}
