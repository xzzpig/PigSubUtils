package com.github.xzzpig.pigutils.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.core.IData;

public class TaskObject implements IData {

	Map<String, Object> data;

	public TaskObject() {
		this(null);
	}

	public TaskObject(@Nullable Map<String, Object> data) {
		if (data == null)
			this.data = new HashMap<>();
		else
			this.data = data;
	}

	@Override
	public void clear() {
		data.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T) data.get(key);
	}

	@Override
	public Set<String> keySet() {
		return data.keySet();
	}

	@Override
	public Object remove(String key) {
		return data.remove(key);
	}

	@Override
	public TaskObject set(String key, Object value) {
		data.put(key, value);
		return this;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Collection<Object> values() {
		return data.values();
	}

	TaskState state;

	public @NotNull TaskObject setState(@Nullable TaskState state) {
		this.state = state;
		return this;
	}

	public @NotNull TaskState getState() {
		return state != null ? state : TaskState.DEFAULT;
	}

	public Object getInput() {
		return get("input");
	}

	public <T> T getInput(Class<T> clazz) {
		return get("input", clazz);
	}
}
