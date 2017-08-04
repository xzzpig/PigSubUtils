package com.github.xzzpig.pigutils.pigcommandservice.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.pigcommandservice.Command;
import com.github.xzzpig.pigutils.websocket.WebSocket;
import com.github.xzzpig.pigutils.websocket.handshake.ClientHandshake;
import com.github.xzzpig.pigutils.websocket.server.WebSocketServer;

/**
 * @author xzzpig 命令服務器，用于处理命令
 */
public class CommandServer {
	private static CommandServer instance;

	public static CommandServer getServerInstance() {
		return instance == null ? instance = new CommandServer() : instance;
	}

	private boolean open;

	private int port;

	private WebSocketServer server;

	/**
	 * @return 返回内置的WebSocketServer实例
	 */
	public WebSocketServer getServer() {
		return server;
	}

	public boolean isOpened() {
		return open;
	}

	/**
	 * 打开server
	 * 
	 * @param port
	 *            绑定的端口
	 * @return this
	 * @throws IOException
	 */
	public CommandServer open(int port) throws IOException {
		if (isOpened())
			return this;
		server = new WebSocketServer(new InetSocketAddress(port)) {
			@Override
			public void onClose(WebSocket conn, int code, String reason, boolean remote) {
			}

			@Override
			public void onError(WebSocket conn, Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onMessage(WebSocket conn, String message) {
				JSONObject msg = new JSONObject(message);
				String cmd = msg.optString("command", null);
				JSONObject res = new JSONObject();
				if (cmd == null) {
					res.put("command", "print");
					res.put("l", "Error");
					res.put("m", "命令不可未空");
				}
				ServerCommand[] ss = Command.getCommands().filter(c -> c instanceof ServerCommand)
						.map(c -> (ServerCommand) c).filter(c -> c.getCmd().equalsIgnoreCase(cmd))
						.filter(c -> c.getType().toString().equalsIgnoreCase("Server")).toArray(ServerCommand[]::new);
				if (ss.length == 0) {
					res.put("command", "print");
					res.put("l", "Error");
					res.put("m", cmd + "命令未找到");
				} else {
					msg.put("client", conn);
					for (ServerCommand sc : ss) {
						JSONObject result = sc.runCommand(msg);
						if (result != null) {
							if (result.optString("side", "Client").equalsIgnoreCase("Client")) {
								res.put("command", "print");
								res.put("l", "Info");
								res.put("m", res.optString("m", "") + result.getString("m"));
							} else {
								System.out.println(result.getString("m"));
							}
						}
					}
				}
				if (res.has("command"))
					conn.send(res.toString());
			}

			@Override
			public void onOpen(WebSocket conn, ClientHandshake handshake) {
				Logger.getAnonymousLogger().info(conn.getRemoteSocketAddress().getHostString() + "连接");
			}
		};
		open = true;
		this.port = port;
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
		if (cmd == null) {
			res.put("command", "print");
			res.put("l", "Error");
			res.put("m", "命令不可未空");
		}
		ServerCommand[] ss = Command.getCommands().filter(c -> c instanceof ServerCommand).map(c -> (ServerCommand) c)
				.filter(c -> c.getCmd().equalsIgnoreCase(cmd))
				.filter(c -> c.getType().toString().equalsIgnoreCase("Client")).toArray(ServerCommand[]::new);
		if (ss.length == 0) {
			res.put("command", "print");
			res.put("l", "Error");
			res.put("m", cmd + "命令未找到");
		} else
			for (ServerCommand sc : ss) {
				JSONObject result = sc.runCommand(args);
				if (result != null) {
					if (result.optString("side", "Client").equalsIgnoreCase("Server")) {
						res.put("command", "print");
						res.put("l", "Info");
						res.put("m", res.optString("m", "") + result.getString("m"));
					} else {
						System.out.println(result.getString("m"));
					}
				}
			}
		return res.length() == 0 ? null : res;
	}

	public CommandServer start() {
		server.start();
		Logger.getAnonymousLogger().info("CommandServer已启动于0.0.0.0:" + port);
		return this;
	}

	public void stop() throws IOException, InterruptedException {
		server.stop();
	}
}
