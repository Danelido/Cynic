package com.danliden.mm.game.session;

public class AckEntity {

    private final int ackId;
    private final PlayerClient client;
    private final byte[] outgoingData;
    private final int intervalMS;
    private final int maxTries;

    private int timeSinceLastSend = 0;
    private int currentTry = 0;

    public AckEntity(final int ackId, final PlayerClient client, byte[] outgoingData,
                     final int intervalMS, final int maxTries) {
        this.ackId = ackId;
        this.client = client;
        this.outgoingData = outgoingData;
        this.intervalMS = intervalMS;
        this.maxTries = maxTries;
    }

    public void addTimeSinceLastSend(float timeMS) {
        timeSinceLastSend += timeMS;
    }

    public void resetTimeSinceLastSend() {
        timeSinceLastSend = 0;
    }

    public void incrementTry() {
        currentTry++;
    }

    public int getAckId() {
        return ackId;
    }

    public PlayerClient getClient() {
        return client;
    }

    public byte[] getOutgoingData() {
        return outgoingData;
    }

    public int getIntervalMS() {
        return intervalMS;
    }

    public int getMaxTries() {
        return maxTries;
    }

    public int getTimeSinceLastSend() {
        return timeSinceLastSend;
    }

    public int getCurrentTry() {
        return currentTry;
    }
}
