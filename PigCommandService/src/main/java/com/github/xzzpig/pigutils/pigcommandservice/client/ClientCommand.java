package com.github.xzzpig.pigutils.pigcommandservice.client;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.pigcommandservice.Command;

public abstract class ClientCommand extends Command {
	public CommandClient getClient(JSONObject args) {
		return (CommandClient) args.opt("client");
	}
}
