package com.github.xzzpig.pigutils.websocket.handshake;

public interface ServerHandshakeBuilder extends HandshakeBuilder, ServerHandshake {
    void setHttpStatus(short status);

    void setHttpStatusMessage(String message);
}
