package com.github.xzzpig.pigutils.websocket.handshake;

public interface ClientHandshakeBuilder extends HandshakeBuilder, ClientHandshake {
	public void setResourceDescriptor(String resourceDescriptor);
}
