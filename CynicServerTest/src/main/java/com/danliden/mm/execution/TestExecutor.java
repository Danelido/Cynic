package com.danliden.mm.execution;

import com.danliden.mm.tests.ICynicTest;
import com.danliden.mm.tests.ServerFetchTest;
import com.danliden.mm.tests.ServerJoinTest;
import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.UdpClient;

import java.util.ArrayList;
import java.util.List;

public class TestExecutor {

    private final List<ICynicTest> cynicTests = new ArrayList<>();
    private final HttpClient httpClient = new HttpClient();
    private final UdpClient udpClient = new UdpClient();

    public TestExecutor() {
        initializeTests();
    }

    public boolean executeAll() throws Exception {
        udpClient.connect();

        for (ICynicTest test : cynicTests) {
            if (!test.execute(httpClient, udpClient)) {
                return false;
            }
        }

        udpClient.close();
        httpClient.close();
        return true;
    }

    private void initializeTests() {
        cynicTests.add(new ServerFetchTest());
        cynicTests.add(new ServerJoinTest());
    }
}
