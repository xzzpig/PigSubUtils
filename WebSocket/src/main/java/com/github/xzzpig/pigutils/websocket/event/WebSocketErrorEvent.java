package com.github.xzzpig.pigutils.websocket.event;

import com.github.xzzpig.pigutils.websocket.WebSocket;

public class WebSocketErrorEvent extends WebSocketEvent {

	Exception exception;

	public WebSocketErrorEvent(WebSocket ws, Exception err) {
		super(ws);
		this.exception = err;
	}

	public Exception getException() {
		return exception;
	}
}
