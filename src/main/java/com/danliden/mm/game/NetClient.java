package com.danliden.mm.game;

public class NetClient {

    public final String addr;
    public final int port;
    public final String encryptionKey;
    public final long createdTimestamp;
    public NetClient(String addr, int port, String encryptionKey){
        this.addr = addr;
        this.port = port;
        this.encryptionKey = encryptionKey;

        createdTimestamp = System.currentTimeMillis();
    }

    public boolean isEqual(String addr, int port){
        return (this.addr.equals(addr) && this.port == port);
    }

}
