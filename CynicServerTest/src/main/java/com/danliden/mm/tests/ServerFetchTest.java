package com.danliden.mm.tests;

import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.PacketSender;
import com.danliden.mm.utils.Validator;
import org.json.JSONObject;

public class ServerFetchTest implements ICynicTest {

    @Override
    public boolean execute(HttpClient httpSender, PacketSender packetSender) throws Exception {
        JSONObject payload = httpSender.get("FSS", 10, 6000);
        return Validator.validateFSSResponse(payload);
    }
}
