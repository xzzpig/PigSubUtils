package com.github.xzzpig.pigutils.websocket.handshake;

public interface ClientHandshakeBuilder extends HandshakeBuilder, ClientHandshake {
    void setResourceDescriptor(String resourceDescriptor);
}
