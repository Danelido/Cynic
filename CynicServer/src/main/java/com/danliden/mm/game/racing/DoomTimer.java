package com.danliden.mm.game.racing;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoomTimer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public enum State {
        NOT_STARTED,
        STARTED,
        FINISHED
    }

    private final SessionAckHandler ackHandler;
    private final SessionPlayers sessionPlayers;
    private final GameState gameState;
    private final PacketSender sender;

    private int currentTimeMs;
    private int lastTimeMs;
    private State currentState;

    public DoomTimer(SessionAckHandler ackHandler, SessionPlayers sessionPlayers, GameState currentState, PacketSender sender) {
        this.ackHandler = ackHandler;
        this.sessionPlayers = sessionPlayers;
        this.gameState = currentState;
        this.sender = sender;
        this.currentState = State.NOT_STARTED;
    }

    public void update(int updateIntervalMs) {
        if (currentState == State.STARTED) {
            decreaseTime(updateIntervalMs);
            notifyPlayersOnTimeChange();
            handleIfDone();
        }
    }

    public void startCountdown(int startTimeMs) {
        if (currentState == State.NOT_STARTED) {
            logger.debug("Starting doom counter countdown");
            gameState.setGameState(GameState.GameStateEnum.IN_SESSION_DOOM_TIMER);
            currentState = State.STARTED;
            currentTimeMs = startTimeMs;
            lastTimeMs = startTimeMs;
        }
    }

    public void stop() {
        currentState = State.NOT_STARTED;
        logger.debug("Stopping doom counter countdown");
    }

    private void decreaseTime(int amount) {
        currentTimeMs -= amount;
        if (currentTimeMs < 0) {
            currentTimeMs = 0;
        }
    }

    private void notifyPlayersOnTimeChange() {
        if (timeToSend()) {
            lastTimeMs = currentTimeMs;
            sendTimerPacket();
        }

    }

    private void sendTimerPacket() {
        logger.debug("Sending doom timer packet to clients");
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.DOOM_TIMER);
        packet.put(PacketKeys.DoomTimer, currentTimeMs);
        sender.sendToMultipleWithAck(ackHandler, packet, sessionPlayers.getPlayers(), 5, 150);
    }

    private boolean timeToSend() {
        int currentInSeconds = currentTimeMs / 1000;
        int lastTimeInSeconds = lastTimeMs / 1000;
        logger.debug("CurrentTime(seconds): {}, LastTime(seconds): {}", currentInSeconds, lastTimeInSeconds);
        return currentInSeconds != lastTimeInSeconds;
    }

    private void handleIfDone() {
        if (currentState == State.STARTED && currentTimeMs <= 0) {
            logger.debug("Doom timer is done, setting state to FINISHED");
            currentState = State.FINISHED;
        }
    }

    public void overrideState(State state){
        currentState = state;
    }

    public int getCurrentTimeMs() {
        return currentTimeMs;
    }

    public State getCurrentState() {
        return this.currentState;
    }
}
