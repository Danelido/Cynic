package com.danliden.mm.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Configuration {
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static JSONObject configurationAsJson;

    static {
        InputStream inputStream = Configuration.class.getResourceAsStream("/settings/Configurations.json");
        if (inputStream == null) {
            logger.info("Could not load configuration resource");
            throw new NullPointerException();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String jsonString = reader.lines().collect(Collectors.joining());
            System.out.println(jsonString);
            configurationAsJson = new JSONObject(jsonString);
        } finally {
            try {
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int MissedHeartbeatsBeforeDisconnect = configurationAsJson.getInt("MissedHeartbeatsBeforeDisconnect");
    private static final int HeartbeatFrequency = configurationAsJson.getInt("HeartbeatFrequency");
    private static final int UpdateFrequency = configurationAsJson.getInt("UpdateFrequency");
    private static final int DoomTimerSeconds = configurationAsJson.getInt("DoomTimerSeconds");
    private static final int EndOfRaceTime = configurationAsJson.getInt("EndOfRaceTime");
    private static final int MaxGameSessions = configurationAsJson.getInt("MaxGameSessions");
    private static final int MaxIncomingPacketSizeBytes = configurationAsJson.getInt("MaxIncomingPacketSizeBytes");
    private static final int RaceCountdownTime = configurationAsJson.getInt("RaceCountdownTime");

    public static int getMissedHeartbeatsBeforeDisconnect() {
        return MissedHeartbeatsBeforeDisconnect;
    }

    public static int getHeartbeatFrequency() {
        return HeartbeatFrequency;
    }

    public static int getUpdateFrequency() {
        return UpdateFrequency;
    }

    public static int getDoomTimerSeconds() {
        return DoomTimerSeconds;
    }

    public static int getEndOfRaceTime() {
        return EndOfRaceTime;
    }

    public static int getMaxGameSessions() {
        return MaxGameSessions;
    }

    public static int getMaxIncomingPacketSizeBytes() {
        return MaxIncomingPacketSizeBytes;
    }

    public static int getRaceCountdownTime() {
        return RaceCountdownTime;
    }
}
