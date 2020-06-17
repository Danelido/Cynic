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
                return false;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

}
