package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
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

    @Test
    public void testUpdatingValidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        int newHealth = 1;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(player.id, newHealth, newPosition);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        updatePlayerLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (player.health == newHealth);
        assert (player.position.equalsTo(newPosition));

        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendToMultipleWithExclude(any(JSONObject.class), anyList(), any(PlayerClient.class));
    }

    @Test
    public void testUpdatingInvalidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        IPacketLogic updatePlayerLogic = new UpdatePlayer();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Vector2 newPosition = new Vector2(400, -200);
        int newHealth = 1;
        JSONObject playerJsonUpdatedData = createPlayerUpdateData(-1, newHealth, newPosition);

        Mockito.when(bundle.getPacketJsonData()).thenReturn(playerJsonUpdatedData);

        // Execute logic
        updatePlayerLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (sessionPlayers.findById(player.id) != null);
        // Build the expected outgoing packet
        verify(senderMock, times(1)).sendNotConnectedPacketToSender(bundle);
    }

    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket, final int hostAddressMock) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(hostAddressMock));
        return sessionPlayers.createPlayer(bundle);
    }

    private JSONObject createPlayerUpdateData(int playerId, int health, Vector2 position) {
        return new JSONObject()
                .put(ValidPacketDataKeys.PlayerId, playerId)
                .put(ValidPacketDataKeys.PlayerHealth, health)
                .put(ValidPacketDataKeys.PlayerXPos, position.x)
                .put(ValidPacketDataKeys.PlayerYPos, position.y);
    }


}
