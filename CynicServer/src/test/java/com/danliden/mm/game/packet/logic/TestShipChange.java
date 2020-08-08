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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestShipChange {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void changeShipWhenLobbyAndPlayerNotReady() {
        // Setup
        IPacketLogic ShipChange = new ShipChange();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.LOBBY);
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);

        String newShipName = "ValidName";
        JSONObject shipChangePacket = createShipChangePacket(player.id, newShipName);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(shipChangePacket);

        // Execute logic
        ShipChange.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);
        assert (player.getChoosenShip().contentEquals(newShipName));

        verify(senderMock, times(0)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(1)).sendToMultipleWithAckAndExclude(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt(), any(PlayerClient.class));
    }

    @Test
    public void NotChangeShipWhenLobbyAndPlayerReady() {
        // Setup
        IPacketLogic ShipChange = new ShipChange();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.LOBBY);
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);
        player.setChoosenShip("SomethingElse");
        player.setIsReady(true);

        String newShipName = "ValidName";
        JSONObject shipChangePacket = createShipChangePacket(player.id, newShipName);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(shipChangePacket);

        // Execute logic
        ShipChange.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);
        assert (!player.getChoosenShip().contentEquals(newShipName));

        verify(senderMock, times(0)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAckAndExclude(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt(), any(PlayerClient.class));
    }

    @Test
    public void NotChangeShipWhenInSessionState() {
        // Setup
        IPacketLogic ShipChange = new ShipChange();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);
        player.setChoosenShip("SomethingElse");

        String newShipName = "ValidName";
        JSONObject shipChangePacket = createShipChangePacket(player.id, newShipName);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(shipChangePacket);

        // Execute logic
        ShipChange.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.IN_SESSION);

        verify(senderMock, times(0)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAckAndExclude(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt(), any(PlayerClient.class));
    }

    @Test
    public void NotChangeShipWhenInSessionStateAndPlayerReady() {
        // Setup
        IPacketLogic ShipChange = new ShipChange();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.IN_SESSION);
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket);
        addPlayer(sessionPlayers, dgPacket);
        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);
        player.setChoosenShip("SomethingElse");
        player.setIsReady(true);

        String newShipName = "ValidName";
        JSONObject shipChangePacket = createShipChangePacket(player.id, newShipName);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(shipChangePacket);

        // Execute logic
        ShipChange.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.IN_SESSION);

        verify(senderMock, times(0)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAckAndExclude(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt(), any(PlayerClient.class));
    }

    @Test
    public void NotChangeShipFromNonValidPlayer() {
        // Setup
        IPacketLogic ShipChange = new ShipChange();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = new GameState();
        state.setGameState(GameState.GameStateEnum.LOBBY);
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket);
        assert (player != null);
        player.setChoosenShip("SomethingElse");
        player.setIsReady(true);

        String newShipName = "ValidName";
        JSONObject shipChangePacket = createShipChangePacket(player.id + 1, newShipName);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(shipChangePacket);

        // Execute logic
        ShipChange.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (state.getGameState() == GameState.GameStateEnum.LOBBY);

        verify(senderMock, times(1)).sendNotConnectedPacketToSender(any(ServerPacketBundle.class));
        verify(senderMock, times(0)).sendToMultipleWithAckAndExclude(any(SessionAckHandler.class), any(JSONObject.class), anyList(), anyInt(), anyInt(), any(PlayerClient.class));
    }

    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createShipChangePacket(int playerId, String shipName) {
        return new JSONObject()
                .put(PacketKeys.PlayerId, playerId)
                .put(PacketKeys.ShipPrefabName, shipName);
    }

}
