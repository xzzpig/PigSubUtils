package com.github.xzzpig.pigutils.pigcommandservice.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.pigcommandservice.Command;
import com.github.xzzpig.pigutils.websocket.client.WebSocketClient;
import com.github.xzzpig.pigutils.websocket.handshake.ServerHandshake;

/**
 * @author xzzpig 命令服務器，用于处理命令
 */
public class CommandClient {

	private WebSocketClient client;

	public CommandClient connect() {
		client.connect();
		return this;
	}

	/**
	 * @return 返回内置的WebSocketClient实例
	 */
	public WebSocketClient getClient() {
		return client;
	}

	/**
	 * 打开server
	 * 
	 * @param port
	 *            绑定的端口
	 * @return this
	 * @throws IOException
	 * @throws URISyntaxException
	 */

	public boolean isConnected() {
		return client.isOpen();
	}

	public CommandClient open(String ip, int port) throws IOException, URISyntaxException {
		client = new WebSocketClient(new URI("ws://" + ip + ":" + port)) {
			@Override
			public void onClose(int code, String reason, boolean remote) {
			}

			@Override
			public void onError(Exception ex) {
			}

			@Override
			public void onMessage(String message) {
				JSONObject msg = new JSONObject(message);
				String cmd = msg.optString("command", null);
				JSONObject res = new JSONObject();
				if (cmd == null) {
					res.put("command", "print");
					res.put("l", "Error");
					res.put("m", "命令不可未空");
				}
				ClientCommand[] ss = Command.getCommands().filter(c -> c instanceof ClientCommand)
						.map(c -> (ClientCommand) c).filter(c -> c.getCmd().equalsIgnoreCase(cmd))
						.filter(c -> c.getType().toString().equalsIgnoreCase("Server")).toArray(ClientCommand[]::new);
				if (ss.length == 0) {
					res.put("command", "print");
					res.put("l", "Error");
					res.put("m", cmd + "命令未找到");
				} else
					for (ClientCommand sc : ss) {
						JSONObject result = sc.runCommand(msg);
						if (result != null) {
							System.out.println(result.getString("m"));
						}
					}
				if (res.has("command"))
					this.send(res.toString());
			}

			@Override
			public void onOpen(ServerHandshake handshakedata) {
				Logger.getAnonymousLogger().info("连接到" + this.getRemoteSocketAddress().getHostString() + "成功");
			}
		};
		return this;
	}

	public JSONObject runCommand(String str) {
		String[] strs = str.split(" ");
		String cmd = strs[0];
		JSONObject args = new JSONObject();
		args.put("command", cmd);
		for (String s : strs) {
			if (s.startsWith("-")) {
				s = s.replaceFirst("-", "");
				if (!s.contains(":"))
					continue;
				String[] ss = s.split(":", 2);
				args.put(ss[0], ss[1]);
			}
		}
		return runCommand(cmd, args);
	}

	public JSONObject runCommand(String cmd, JSONObject args) {
		JSONObject res = new JSONObject();
		args.put("command", cmd);
		args.put("client", this);
		if (cmd == null) {
			res.put("command", "print");
			res.put("l", "Error");
			res.put("m", "命令不可未空");
		}
		ClientCommand[] ss = Command.getCommands().filter(c -> c instanceof ClientCommand).map(c -> (ClientCommand) c)
				.filter(c -> c.getCmd().equalsIgnoreCase(cmd))
				.filter(c -> c.getType().toString().equalsIgnoreCase("Client")).toArray(ClientCommand[]::new);
		if (ss.length == 0) {
			res.put("command", "print");
			res.put("l", "Error");
			res.put("m", cmd + "命令未找到");
		} else
			for (ClientCommand sc : ss) {
				JSONObject result = sc.runCommand(args);
				if (result != null) {
					System.out.println(result.getString("m"));
				}
			}
		return res.length() == 0 ? null : res;
	}

	public void stop() throws IOException, InterruptedException {
		client.close();
	}
}
