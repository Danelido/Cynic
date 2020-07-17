package com.danliden.mm.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static boolean validateFSSResponse(JSONObject response) {
        try {
            int statusCode = response.getInt("StatusCode");
            response.getInt("Port");
            response.getInt("SessionId");

            if (statusCode != HttpStatus.OK.value()) {
                logger.info("FSS Response NOT OK! Status code: " + statusCode);
                return false;
            }
            logger.info("FSS Response OK");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }


    public static boolean validateJoinResponse(JSONObject response) {
        try {
            logger.info("Full response: " + response.toString());

            logger.info("Extracting received packetId");
            int packetId = response.getInt(ValidPacketDataKeys.PacketId);

            if (packetId != PacketType.Incoming.JOIN_ACCEPTED) {
                logger.info("PacketID mismatch");
                return false;
            }

            response.getInt(ValidPacketDataKeys.PlayerId);
            response.getInt(ValidPacketDataKeys.AckId);
            response.getString(ValidPacketDataKeys.PlayerName);
            response.getBoolean(ValidPacketDataKeys.PlayerReady);
            logger.info("Join Response OK");

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

}
