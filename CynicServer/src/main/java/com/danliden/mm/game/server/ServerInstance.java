package com.danliden.mm.game.server;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.session.GameSession;
import com.danliden.mm.rest.HTTPResponse;
import com.danliden.mm.utils.UniqueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class ServerInstance implements Runnable {

    private static final int MAX_GAME_SESSIONS = 150;
    private static final int MAX_PACKET_SIZE = 1024;

    private static final int UPDATE_INTERVAL_MS = 100;
    private static final int HEARTBEAT_INTERVAL_MS = 1500;

    private final DatagramSocket socket;
    private final Map<Integer, GameSession> gameSessionMap = new HashMap<>();
    private final UniqueId sessionIDCreator;
    private final PacketSender packetSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerInstance() throws Exception {
        socket = new DatagramSocket();
        logger.info(String.format("Creating server instance on port %d", socket.getLocalPort()));
        sessionIDCreator = new UniqueId(MAX_GAME_SESSIONS);
        packetSender = new PacketSender(socket);
    }

    public void run() {
        runUpdateWorker();
        runHeartbeatWorker();
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                handleIncomingPackages();
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
    }

    public HTTPResponse getAvailableGameSession() {
        HTTPResponse response = new HTTPResponse();
        GameSession session = findAvailableSession();
        if (session != null) {
            logger.info("Found an available game session");
            response.setStatusCode(HttpStatus.OK.value())
                    .append(ValidPacketDataKeys.ServerPort, socket.getLocalPort())
                    .append(ValidPacketDataKeys.SessionID, session.getSessionId());

            return response;
        }
        logger.info("Did not find any available sessions");
        return response.setStatusCode(HttpStatus.NOT_FOUND.value());
    }

    private GameSession findAvailableSession() {
        for (Map.Entry<Integer, GameSession> entry : gameSessionMap.entrySet()) {
            GameSession session = entry.getValue();
            if (session.isJoinAble()) {
                return session;
            }
        }
        // If it gets here then there is no available sessions
        // Check if it's possible to create a new one
        if (gameSessionMap.size() < MAX_GAME_SESSIONS) {
            return createNewGameSession();
        }

        return null;
    }

    private GameSession createNewGameSession() {
        int sessionID = sessionIDCreator.getId();
        GameSession session = new GameSession(packetSender, sessionID);
        gameSessionMap.put(sessionID, session);

        return session;
    }

    private GameSession findSessionFromId(int sessionId) {
        return gameSessionMap.get(sessionId);
    }

    private void runUpdateWorker() {
        Thread updateWorker = new Thread("Updater") {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        handleGeneralUpdate();
                    } catch (Exception e) {
                        /* Empty */
                    }
                }
            }
        };
        updateWorker.start();
        logger.info("Update worker started");
    }

    private void handleGeneralUpdate() throws Exception {
        for (Map.Entry<Integer, GameSession> entry : gameSessionMap.entrySet()) {
            GameSession session = entry.getValue();
            session.onServerUpdate(UPDATE_INTERVAL_MS);
        }
        Thread.sleep(UPDATE_INTERVAL_MS);
    }

    private void runHeartbeatWorker() {
        Thread heartbeatWorker = new Thread("Heartbeat") {
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        handleHeartbeat();
                    } catch (Exception e) {
                        /* Empty */
                    }
                }
            }
        };
        heartbeatWorker.start();
        logger.info("Heartbeat worker started");
    }

    private void handleHeartbeat() throws Exception {
        for (Map.Entry<Integer, GameSession> entry : gameSessionMap.entrySet()) {
            GameSession session = entry.getValue();
            session.onServerHeartbeat();
        }
        Thread.sleep(HEARTBEAT_INTERVAL_MS);
    }

    private void handleIncomingPackages() throws Exception {
        byte[] byteData = new byte[MAX_PACKET_SIZE];
        DatagramPacket dataPacket = new DatagramPacket(byteData, byteData.length);
        socket.receive(dataPacket);

        ServerPacketBundle packetBundle = new ServerPacketBundle(dataPacket).build();

        GameSession session = findSessionFromId(packetBundle.getSessionId());
        if (session != null) {
            session.handleData(packetBundle);
        }
    }

}
