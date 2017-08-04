package com.github.xzzpig.pigutils.pigcommandservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.github.xzzpig.pigutils.json.JSONObject;

public abstract class Command {
	public enum CommandTarget {
		Server, Client
	}

	private static List<Command> cmds = new ArrayList<>();

	static {
		regCommand(new com.github.xzzpig.pigutils.pigcommandservice.command.cs.Command_Print());
		regCommand(new com.github.xzzpig.pigutils.pigcommandservice.command.cc.Command_Print());
		regCommand(new com.github.xzzpig.pigutils.pigcommandservice.command.cc.Command_Help());
		regCommand(new com.github.xzzpig.pigutils.pigcommandservice.command.sc.Command_Help());
	}

	public static Stream<Command> getCommands() {
		return cmds.stream();
	}

	public static void regCommand(Command c) {
		cmds.add(c);
	}

	public Map<String, String> args = new HashMap<>();

	/**
	 * 必填: -key:[value] 非必填: -key:<value>
	 * 
	 * @param key
	 * @param value
	 * @param needed
	 *            是否必填
	 */
	public void addArgs(String key, String value, boolean needed) {
		// args.put(key, value)
		if (needed)
			args.put(key, "[" + value + "]");
		else
			args.put(key, "<" + value + ">");
	}

	int compare(Entry<String, String> arg0, Entry<String, String> arg1) {
		if (arg0.getValue().endsWith("]") && arg1.getValue().endsWith("]"))
			return 0;
		if (arg0.getValue().endsWith(">") && arg1.getValue().endsWith("]"))
			return 1;
		if (arg0.getValue().endsWith("]") && arg1.getValue().endsWith(">"))
			return -1;
		if (arg0.getValue().endsWith(">") && arg1.getValue().endsWith(">"))
			return 0;
		return 0;
	}

	protected Map<String, String> getArgs() {
		return args;
	}

	public abstract String getCmd();

	public abstract CommandRunner getCommandRunner();

	public abstract String getDescribe();

	public abstract CommandTarget getType();

	public JSONObject runCommand(JSONObject args) {
		if (getArgs().entrySet().stream().anyMatch(e -> {
			if (e.getValue().startsWith("[")) {
				if (!args.has(e.getKey())) {
					return true;
				}
			}
			return false;
		}))
			return new JSONObject().put("m", "命令用法错误:" + this.toString());
		return getCommandRunner().run(getCmd(), args);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getCmd());
		getArgs().entrySet().stream().sorted(this::compare).forEach(e -> {
			sb.append(' ').append('-').append(e.getKey()).append(':').append(e.getValue());
		});
		sb.append('|').append(getDescribe());
		return sb.toString();
	}
}
