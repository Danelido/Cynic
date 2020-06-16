package com.danliden.mm.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEncryption {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testSmallEncryption() {
        String payload = "this is a dummy text";
        String key = KeyGenerator.generateKey();

        try {
            String encryptedPayload = Encryption.encryptOrDecrypt(payload, key);
            String decryptedPayload = Encryption.encryptOrDecrypt(encryptedPayload, key);

            if (encryptedPayload.equals(payload)) {
                logger.debug(String.format("Encrypted payload \"%s\" equals payload \"%s\"", encryptedPayload, payload));
                assert (false);
            }

            if (!decryptedPayload.equals(payload)) {
                logger.debug(String.format("Decrypted payload \"%s\" does not equal payload \"%s\"", decryptedPayload, payload));
                assert (false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(String.format("Could not encrypt %s with key %s", payload, key));
            assert (false);
        }
    }

    @Test
    public void testBigEncryption() {
        StringBuilder payload = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            payload.append("a");
        }

        String key = KeyGenerator.generateKey();

        try {
            String encryptedPayload = Encryption.encryptOrDecrypt(payload.toString(), key);
            String decryptedPayload = Encryption.encryptOrDecrypt(encryptedPayload, key);

            if (encryptedPayload.equals(payload.toString())) {
                logger.debug(String.format("Encrypted payload \"%s\" equals payload \"%s\"", encryptedPayload, payload.toString()));
                assert (false);
            }

            if (!decryptedPayload.equals(payload.toString())) {
                logger.debug(String.format("Decrypted payload \"%s\" does not equal payload \"%s\"", decryptedPayload, payload.toString()));
                assert (false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(String.format("Could not encrypt %s with key %s", payload.toString(), key));
            assert (false);
        }
    }

    @Test
    public void testEncryptionWithSpecialCharacters() {
        String payload = "HELLo=?/6&23DF89324_.,!!2#¤%&/()=?`*^-<>§";
        String key = KeyGenerator.generateKey();

        try {
            String encryptedPayload = Encryption.encryptOrDecrypt(payload, key);
            String decryptedPayload = Encryption.encryptOrDecrypt(encryptedPayload, key);

            if (encryptedPayload.equals(payload)) {
                logger.debug(String.format("Encrypted payload \"%s\" equals payload \"%s\"", encryptedPayload, payload));
                assert (false);
            }

            if (!decryptedPayload.equals(payload)) {
                logger.debug(String.format("Decrypted payload \"%s\" does not equal payload \"%s\"", decryptedPayload, payload));
                assert (false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(String.format("Could not encrypt %s with key %s", payload, key));
            assert (false);
        }
    }

}
