package com.danliden.mm.tests;

import com.danliden.mm.utils.HttpClient;
import com.danliden.mm.utils.PacketSender;

public interface ICynicTest {
    boolean execute(HttpClient httpSender, PacketSender packetSender) throws Exception;
}
