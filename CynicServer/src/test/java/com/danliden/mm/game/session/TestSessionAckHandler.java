package com.danliden.mm.game.session;

import com.danliden.mm.game.packet.ValidPacketDataKeys;
import com.danliden.mm.game.server.PacketSender;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class TestSessionAckHandler {

    private final PacketSender senderMock = mock(PacketSender.class);

    @Test
    public void testAddingAckEntity(){
        SessionAckHandler ackHandler = new SessionAckHandler(senderMock);
        JSONObject dataJson = new JSONObject();
        PlayerClient playerMock = mock(PlayerClient.class);

        AckEntity ackEntity = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        AckEntity ackEntity2 = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        ackHandler.registerAckEntity(ackEntity);
        ackHandler.registerAckEntity(ackEntity2);

        assert(ackHandler.getNumAcksInProcess() == 2);

    }

    @Test
    public void testRemoveAckEntity(){
        SessionAckHandler ackHandler = new SessionAckHandler(senderMock);
        JSONObject dataJson = new JSONObject();
        PlayerClient playerMock = mock(PlayerClient.class);

        AckEntity ackEntity = ackHandler.buildAckEntity(dataJson, playerMock, 2, 100);
        AckEntity ackEntity2 = ackHandler.buildAckEntity(dataJson, playerMock, 2, 200);
        ackHandler.registerAckEntity(ackEntity);
        ackHandler.registerAckEntity(ackEntity2);

        ackHandler.update(100);
        assert (ackHandler.getNumAcksInProcess() == 2);
        verify(senderMock, times(1)).send(Mockito.any(), Mockito.any(), Mockito.anyInt());

        ackHandler.update(100);
        assert (ackHandler.getNumAcksInProcess() == 1);
        verify(senderMock, times(3)).send(Mockito.any(), Mockito.any(), Mockito.anyInt());


        ackHandler.update(200);
        assert (ackHandler.getNumAcksInProcess() == 0);
        verify(senderMock, times(4)).send(Mockito.any(), Mockito.any(), Mockito.anyInt());

        ackHandler.update(200);
        assert (ackHandler.getNumAcksInProcess() == 0);
        verify(senderMock, times(4)).send(Mockito.any(), Mockito.any(), Mockito.anyInt());

    }

    @Test
    public void testHandlingAckPackets(){
        SessionAckHandler ackHandler = new SessionAckHandler(senderMock);
        JSONObject dataJson = new JSONObject();
        PlayerClient playerMock = mock(PlayerClient.class);

        AckEntity ackEntity = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        AckEntity ackEntity2 = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        ackHandler.registerAckEntity(ackEntity);
        ackHandler.registerAckEntity(ackEntity2);

        //Create a ack packet with a valid ack id
        JSONObject ackPack1 = new JSONObject().put(ValidPacketDataKeys.AckId, ackEntity.getAckId());
        JSONObject ackPack2 = new JSONObject().put(ValidPacketDataKeys.AckId, ackEntity2.getAckId());

        boolean result;

        result = ackHandler.handleIfPacketIsAck(ackPack1);
        assert ( result );
        assert (ackHandler.getNumAcksInProcess() == 1);


        result = ackHandler.handleIfPacketIsAck(ackPack2);
        assert ( result );
        assert (ackHandler.getNumAcksInProcess() == 0);

    }

    @Test
    public void testHandlingNonAckPackets(){
        SessionAckHandler ackHandler = new SessionAckHandler(senderMock);
        JSONObject dataJson = new JSONObject();
        PlayerClient playerMock = mock(PlayerClient.class);

        AckEntity ackEntity = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        AckEntity ackEntity2 = ackHandler.buildAckEntity(dataJson, playerMock, 10, 100);
        ackHandler.registerAckEntity(ackEntity);
        ackHandler.registerAckEntity(ackEntity2);

        //Create a ack packet with a valid ack id
        JSONObject ackPack1 = new JSONObject().put(ValidPacketDataKeys.PlayerName, "John Doe");
        JSONObject ackPack2 = new JSONObject().put(ValidPacketDataKeys.PlayerId, "s");

        boolean result;

        result = ackHandler.handleIfPacketIsAck(ackPack1);
        assert ( !result );
        assert (ackHandler.getNumAcksInProcess() == 2);

        result = ackHandler.handleIfPacketIsAck(ackPack2);
        assert ( !result );
        assert (ackHandler.getNumAcksInProcess() == 2);

    }

}
