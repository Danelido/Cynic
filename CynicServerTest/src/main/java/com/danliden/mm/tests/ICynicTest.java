package com.danliden.mm.tests;

import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.UdpClient;

public interface ICynicTest {
    boolean execute(HttpClient httpSender, UdpClient udpClient) throws Exception;
}
