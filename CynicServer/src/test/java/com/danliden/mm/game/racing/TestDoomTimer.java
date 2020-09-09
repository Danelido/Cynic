package com.danliden.mm.game.racing;

import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class TestDoomTimer {

    private final PacketSender senderMock = mock(PacketSender.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testStateStartsInNotStarted() {
        GameState gameState = new GameState();
        gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        DoomTimer doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, senderMock);
        verifyState(doomTimer.getCurrentState(), DoomTimer.State.NOT_STARTED);
        verifyGameState(gameState.getGameState(), GameState.GameStateEnum.IN_SESSION);
    }

    @Test
    public void testStateStartsUpdateDoesNotDoAnythingInNotStartedState() {
        GameState gameState = new GameState();
        gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        DoomTimer doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, senderMock);
        final int intervalMs = 1000;
        doomTimer.update(intervalMs);

        verifyState(doomTimer.getCurrentState(), DoomTimer.State.NOT_STARTED);
        verifyTime(doomTimer.getCurrentTimeMs(), 0);
        verify(senderMock, times(0)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
        verifyGameState(gameState.getGameState(), GameState.GameStateEnum.IN_SESSION);
    }

    @Test
    public void testStateUpdateTimeWhenStarted() {
        GameState gameState = new GameState();
        gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        DoomTimer doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, senderMock);
        final int startTimeMs = 30000;
        final int intervalMs = 1000;

        doomTimer.startCountdown(startTimeMs);
        doomTimer.update(intervalMs);

        verifyState(doomTimer.getCurrentState(), DoomTimer.State.STARTED);
        verifyTime(doomTimer.getCurrentTimeMs(), startTimeMs - intervalMs);
        verify(senderMock, times(1)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
        verifyGameState(gameState.getGameState(), GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);
    }


    @Test
    public void testStateUpdateTimeWhenStartedShouldNotSendIfLessThanASecondDifference() {
        GameState gameState = new GameState();
        gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        DoomTimer doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, senderMock);
        final int startTimeMs = 30000;
        final int intervalMs = 200;

        doomTimer.startCountdown(startTimeMs);
        doomTimer.update(intervalMs); // First update always trigger
        doomTimer.update(intervalMs); // Should not trigger packet sending

        verifyState(doomTimer.getCurrentState(), DoomTimer.State.STARTED);
        verifyTime(doomTimer.getCurrentTimeMs(), startTimeMs - intervalMs - intervalMs);
        verify(senderMock, times(1)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
        verifyGameState(gameState.getGameState(), GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);
    }

    @Test
    public void testDoomTimerWhenDone() {
        GameState gameState = new GameState();
        gameState.setGameState(GameState.GameStateEnum.IN_SESSION);
        SessionPlayers sessionPlayers = new SessionPlayers(4);
        DoomTimer doomTimer = new DoomTimer(ackHandler, sessionPlayers, gameState, senderMock);
        final int startTimeMs = 30000;
        final int intervalMs = 31000;

        doomTimer.startCountdown(startTimeMs);

        doomTimer.update(intervalMs);

        verifyState(doomTimer.getCurrentState(), DoomTimer.State.FINISHED);
        verifyTime(doomTimer.getCurrentTimeMs(), 0);
        verify(senderMock, times(1)).sendToMultipleWithAck(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt());
        verifyGameState(gameState.getGameState(), GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);
    }


    private void verifyState(DoomTimer.State currentState, DoomTimer.State expectedState) {
        assert currentState == expectedState;
    }

    private void verifyTime(int currentTimeMs, int expectedTimeMs) {
        assert currentTimeMs == expectedTimeMs;
    }

    private void verifyGameState(GameState.GameStateEnum currentState, GameState.GameStateEnum expectedState) {
        assert currentState == expectedState;
    }

}
