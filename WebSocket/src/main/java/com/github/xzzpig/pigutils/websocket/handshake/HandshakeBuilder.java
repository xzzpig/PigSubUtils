package com.github.xzzpig.pigutils.websocket.handshake;

public interface HandshakeBuilder extends Handshakedata {
    void put(String name, String value);

    void setContent(byte[] content);
}
