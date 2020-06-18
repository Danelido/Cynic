package com.danliden.mm.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger("Main");
    public static String SERVER_ADDRESS;
    public static int PORT;

    public static void main(String[] args) {
        try {
            if (!extractServerValues(args[0], args[1])) {
                exitWithError();
            }

            TestExecutor testExecutor = new TestExecutor();
            boolean result = testExecutor.executeAll();

            if (!result) {
                exitWithError();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            exitWithError();
        }

    }

    private static void exitWithError() {
        System.exit(-1);
    }

    private static boolean extractServerValues(String address, String port) {
        try {
            logger.info("Server address: " + address);
            logger.info("Server port: " + port);
            SERVER_ADDRESS = address.trim();
            PORT = Integer.parseInt(port.trim());
            logger.info("Input OK");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

}
