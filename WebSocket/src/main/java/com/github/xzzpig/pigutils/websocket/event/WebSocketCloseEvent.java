package com.github.xzzpig.pigutils.websocket.event;

import com.github.xzzpig.pigutils.websocket.WebSocket;

public class WebSocketCloseEvent extends WebSocketEvent {

	int code;
	String reason;
	boolean remote;

	public WebSocketCloseEvent(WebSocket ws, int code, String reason, boolean remote) {
		super(ws);
		this.code = code;
		this.reason = reason;
		this.remote = remote;
	}

	public int getCode() {
		return code;
	}

	public String getReason() {
		return reason;
	}

	public boolean isRemote() {
		return remote;
	}

}
