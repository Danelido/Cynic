package com.danliden.mm.execution;

public class Main {

    public static void main(String[] args) throws Exception {
        TestExecutor testExecutor = new TestExecutor();
        boolean result = testExecutor.executeAll();

        if (!result) {
            System.exit(-1);
        }
    }
}
