package com.github.xzzpig.pigutils.websocket.handshake;

public interface HandshakeBuilder extends Handshakedata {
	public abstract void put(String name, String value);

	public abstract void setContent(byte[] content);
}
