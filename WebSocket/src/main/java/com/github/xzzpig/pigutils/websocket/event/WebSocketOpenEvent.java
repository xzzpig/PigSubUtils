package com.github.xzzpig.pigutils.websocket.event;

import com.github.xzzpig.pigutils.websocket.WebSocket;
import com.github.xzzpig.pigutils.websocket.handshake.Handshakedata;

public class WebSocketOpenEvent extends WebSocketEvent {

	Handshakedata handshakedata;

	public WebSocketOpenEvent(WebSocket ws, Handshakedata data) {
		super(ws);
		this.handshakedata = data;
	}

	public Handshakedata getHandshakedata() {
		return handshakedata;
	}
}
