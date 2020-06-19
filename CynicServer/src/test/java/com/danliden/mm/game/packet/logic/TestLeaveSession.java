package com.danliden.mm.game.packet.logic;

import com.danliden.mm.game.packet.ServerPacketBundle;
import com.danliden.mm.game.packet.ValidPacketDataKeys;
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

import static org.mockito.Mockito.mock;

public class TestLeaveSession {

    private final ServerPacketBundle bundle = mock(ServerPacketBundle.class);
    private final PacketSender senderMock = mock(PacketSender.class);
    private final DatagramPacket dgPacket = mock(DatagramPacket.class);
    private final SessionAckHandler ackHandler = mock(SessionAckHandler.class);

    @Test
    public void testDisconnectingValidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        JSONObject mockJson = mock(JSONObject.class);
        IPacketLogic leaveSessionLogic = new LeaveSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(mockJson);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        addPlayer(sessionPlayers, dgPacket, hostAddressMock);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Mockito.when(mockJson.getInt(ValidPacketDataKeys.PlayerId)).thenReturn(player.id);

        // Execute logic
        leaveSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (sessionPlayers.findById(player.id) == null);
        assert (sessionPlayers.getNumberOfPlayers() == 3);

        Mockito.verify(senderMock, Mockito.times(1))
                .sendToMultipleWithAck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void testDisconnectingInvalidPlayer() {
        // Setup
        final int hostAddressMock = 0;
        JSONObject mockJson = mock(JSONObject.class);
        IPacketLogic leaveSessionLogic = new LeaveSession();
        SessionPlayers sessionPlayers = new SessionPlayers(4);

        GameState state = GameState.IN_SESSION;
        Mockito.when(bundle.getSessionId()).thenReturn(10100);
        Mockito.when(bundle.getDatagramPacket()).thenReturn(dgPacket);
        Mockito.when(bundle.getPacketJsonData()).thenReturn(mockJson);
        Mockito.when(dgPacket.getPort()).thenReturn(2020);

        addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        addPlayer(sessionPlayers, dgPacket, hostAddressMock);

        PlayerClient player = addPlayer(sessionPlayers, dgPacket, hostAddressMock);
        assert (player != null);

        Mockito.when(mockJson.getInt(ValidPacketDataKeys.PlayerId)).thenReturn(-1);

        // Execute logic
        leaveSessionLogic.execute(bundle, senderMock, ackHandler, sessionPlayers, state);

        assert (sessionPlayers.findById(player.id) != null);
        assert (sessionPlayers.getNumberOfPlayers() == 4);

        Mockito.verify(senderMock, Mockito.times(1))
                .sendNotConnectedPacketToSender(bundle);

    }



    private PlayerClient addPlayer(SessionPlayers sessionPlayers, final DatagramPacket dgPacket, final int hostAddressMock) {
        InetAddress playerAddress = mock(InetAddress.class);
        Mockito.when(dgPacket.getAddress()).thenReturn(playerAddress);
        Mockito.when(playerAddress.getHostAddress()).thenReturn(Integer.toString(hostAddressMock));
        return sessionPlayers.createPlayer(bundle);
    }
}
