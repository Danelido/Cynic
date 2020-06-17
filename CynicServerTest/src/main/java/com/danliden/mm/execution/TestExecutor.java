package com.danliden.mm.execution;

import com.danliden.mm.tests.ICynicTest;
import com.danliden.mm.tests.ServerFetchTest;
import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.PacketSender;

import java.util.ArrayList;
import java.util.List;

public class TestExecutor {

    private final List<ICynicTest> cynicTests = new ArrayList<>();
    private final HttpClient httpClient = new HttpClient();
    private final PacketSender packetSender = new PacketSender();

    public TestExecutor() {
        initializeTests();
    }

    public boolean executeAll() throws Exception {
        for (ICynicTest test : cynicTests) {
            if (!test.execute(httpClient, packetSender)) {
                return false;
            }
        }
        httpClient.close();
        return true;
    }

    private void initializeTests() {
        cynicTests.add(new ServerFetchTest());
    }
}
