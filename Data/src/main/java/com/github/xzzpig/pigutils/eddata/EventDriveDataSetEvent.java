package com.github.xzzpig.pigutils.eddata;

public class EventDriveDataSetEvent extends EventDriveDataEvent {

	private Object key;
	private Object value;

	EventDriveDataSetEvent(EventDriveData data, Object key, Object value) {
		super(data);
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}
}
