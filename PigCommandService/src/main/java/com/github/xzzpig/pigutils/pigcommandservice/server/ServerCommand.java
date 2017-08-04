package com.github.xzzpig.pigutils.pigcommandservice.server;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.pigcommandservice.Command;
import com.github.xzzpig.pigutils.websocket.WebSocket;

public abstract class ServerCommand extends Command {
	public WebSocket getClient(JSONObject msg) {
		return (WebSocket) msg.opt("client");
	}

	public CommandServer getServer() {
		return CommandServer.getServerInstance();
	}
}
