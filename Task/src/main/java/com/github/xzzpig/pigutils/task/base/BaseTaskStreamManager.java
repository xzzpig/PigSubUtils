package com.github.xzzpig.pigutils.task.base;

import com.github.xzzpig.pigutils.task.TaskStreamBuilder;
import com.github.xzzpig.pigutils.task.TaskStreamManager;

public class BaseTaskStreamManager extends TaskStreamManager {

	public BaseTaskStreamManager() {
	}

	@Override
	public TaskStreamBuilder getBuilder(String name) {
		if (name.equalsIgnoreCase("base"))
			return new BaseTaskStreamBuilder();
		return null;
	}

	@Override
	public TaskStreamBuilder getBuilder() {
		return getBuilder("base");
	}

}
