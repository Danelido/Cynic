package com.danliden.mm.tests;
import com.danliden.mm.utils.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerJoinTest implements ICynicTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean execute(HttpClient httpSender, UdpClient udpClient) throws Exception {
        JSONObject payload = httpSender.get("FSS", 10, 6000);

        if(!Validator.validateFSSResponse(payload)){
            return false;
        }
        int sessionId = payload.getInt(ValidPacketDataKeys.SessionID);
        int sessionPort = payload.getInt(ValidPacketDataKeys.ServerPort);
        logger.info("Sending Join request to session on port " + sessionPort);

        JSONObject outgoingPacket = constructPacket(sessionId);
        JSONObject receivedPacket = sendAndWaitForResponse(outgoingPacket, udpClient, sessionPort);

        logger.info("Validating received package...");
        return Validator.validateJoinResponse(receivedPacket);
    }

    private JSONObject constructPacket(int sessionId){
        return new JSONObject()
                .put(ValidPacketDataKeys.PacketId, PacketType.Outgoing.JOIN_REQUEST)
                .put(ValidPacketDataKeys.SessionID, sessionId);
    }

    private JSONObject sendAndWaitForResponse(JSONObject outgoingPacket, UdpClient udpClient, int sessionPort) throws InterruptedException, IOException {
        int maximumRetries = 10;
        for(int currentTry = 0; currentTry < maximumRetries; currentTry++){
            udpClient.send(outgoingPacket, sessionPort);
            JSONObject packet = udpClient.popPacket();

            if(packet != null){
                if(!isHeartbeat(packet)) {
                    logger.info("Popping recent package");
                    return packet;
                }else{
                    logger.info("Received heartbeat packet");
                }
            }
            logger.info("No package received, re-sending...");
            int timeBetweenTriesMillis = 1000;
            Thread.sleep(timeBetweenTriesMillis);
        }

        return null;
    }

    private boolean isHeartbeat(JSONObject packet){
        try{
            int packetId = packet.getInt(ValidPacketDataKeys.PacketId);
            return (packetId == PacketType.Incoming.HEARTBEAT_REQUEST);
        }catch(Exception e){
            logger.warn(e.getMessage());
        }
        return false;
    }

}
