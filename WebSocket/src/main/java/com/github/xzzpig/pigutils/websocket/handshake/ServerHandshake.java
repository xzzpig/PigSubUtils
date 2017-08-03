package com.github.xzzpig.pigutils.websocket.handshake;

public interface ServerHandshake extends Handshakedata {
	public short getHttpStatus();

	public String getHttpStatusMessage();
}
