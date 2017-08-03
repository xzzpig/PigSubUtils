package com.github.xzzpig.pigutils.websocket.event;

import com.github.xzzpig.pigutils.event.Event;
import com.github.xzzpig.pigutils.websocket.WebSocket;

public class WebSocketEvent extends Event {

	WebSocket webSocket;

	public WebSocketEvent(WebSocket ws) {
		webSocket = ws;
	}

	public WebSocket getWebSocket() {
		return webSocket;
	}

}
