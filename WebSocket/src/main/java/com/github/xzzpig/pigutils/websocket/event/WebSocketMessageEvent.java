package com.github.xzzpig.pigutils.websocket.event;

import com.github.xzzpig.pigutils.websocket.WebSocket;

public class WebSocketMessageEvent extends WebSocketEvent {

	String message;

	public WebSocketMessageEvent(WebSocket ws, String message) {
		super(ws);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
