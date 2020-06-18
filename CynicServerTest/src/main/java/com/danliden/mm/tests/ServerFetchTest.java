package com.danliden.mm.tests;

import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.UdpClient;
import com.danliden.mm.utils.Validator;
import org.json.JSONObject;

public class ServerFetchTest implements ICynicTest {

    @Override
    public boolean execute(HttpClient httpSender, UdpClient udpClient) throws Exception {
        JSONObject payload = httpSender.get("FSS", 10, 6000);
        return Validator.validateFSSResponse(payload);
    }
}
