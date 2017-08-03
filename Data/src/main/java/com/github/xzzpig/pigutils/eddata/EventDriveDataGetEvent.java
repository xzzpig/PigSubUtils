package com.github.xzzpig.pigutils.eddata;

public class EventDriveDataGetEvent<T> extends EventDriveDataEvent {

	private Object key;
	private Class<T> clazz;
	T value;

	EventDriveDataGetEvent(EventDriveData data, Object key, Class<T> clazz) {
		super(data);
		this.key = key;
		this.clazz = clazz;
	}

	public Object getKey() {
		return key;
	}

	public T getValue() {
		return value;
	}

	public Class<T> getValueClass() {
		return clazz;
	}

	public EventDriveDataEvent setValue(T value) {
		this.value = value;
		return success();
	}
}
