package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.server.PacketSender;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionAckHandler;
import com.danliden.mm.game.session.SessionPlayers;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.mockito.Mockito.*;

public class TestHeartbeat {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testExecutingHeartbeat() {
        // Setup
        final int hostAddressMock = 0;
        JSONObject mockJson = mock(JSONObject.class);
        IPacketLogic heartbeatLogic = new Heartbeat();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(mockJson);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Mockito.when(mockJson.getInt(PacketKeys.PlayerId)).thenReturn(player.id);

        player.addFlatline();
        player.addFlatline();
        assert (player.nrOfFlatLines == 2);

        // Execute logic
        heartbeatLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (player.nrOfFlatLines == 0);

    }

    @Test
    public void testExecutingHeartbeatOnNotConnectedPlayer() {
        // Setup
        final int hostAddressMock = 0;
        JSONObject mockJson = mock(JSONObject.class);
        IPacketLogic heartbeatLogic = new Heartbeat();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(mockJson);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        // Return invalid id
        Mockito.when(mockJson.getInt(PacketKeys.PlayerId)).thenReturn(player.id+1);

        // Execute logic
        heartbeatLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

       verify(senderMock, times(1)).sendNotConnectedPacketToSender(bundle);

    }

    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket, final int hostAddressMock) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(hostAddressMock));
        return sessionPlayers.createPlayer(bundle);
    }

}
