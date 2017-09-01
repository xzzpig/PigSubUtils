package com.github.xzzpig.pigutils.task;

import java.util.HashMap;
import java.util.Map;

import com.github.xzzpig.pigutils.task.base.BaseTaskStreamManager;

public abstract class TaskStreamManager {

	public static String defaultTaskStreamManagerName = "base";

	static {
		register("base", new BaseTaskStreamManager());
	}

	protected TaskStreamManager() {
	}

	private static Map<String, TaskStreamManager> map;

	public static void register(String name, TaskStreamManager manager) {
		if (map == null)
			map = new HashMap<>();
		map.put(name, manager);
	}

	public static TaskStreamManager getTaskStreamManager(String name) {
		if (map == null)
			return null;
		return map.get(name);
	}

	public static TaskStreamManager getTaskStreamManager() {
		return getTaskStreamManager(defaultTaskStreamManagerName);
	}

	public abstract TaskStreamBuilder getBuilder(String name);

	public abstract TaskStreamBuilder getBuilder();
}
