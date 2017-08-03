package com.github.xzzpig.pigutils.eddata;

public class EventDriveDataRemoveEvent extends EventDriveDataEvent {

	private Object key;

	EventDriveDataRemoveEvent(EventDriveData data, Object key) {
		super(data);
		this.key = key;
	}

	public Object getKey() {
		return key;
	}
}
