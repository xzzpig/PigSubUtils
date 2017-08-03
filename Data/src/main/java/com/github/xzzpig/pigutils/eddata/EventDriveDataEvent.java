package com.github.xzzpig.pigutils.eddata;

import com.github.xzzpig.pigutils.event.Event;

public class EventDriveDataEvent extends Event {

	private EventDriveData data;
	private boolean success;

	EventDriveDataEvent(EventDriveData data) {
		this.data = data;
	}

	public EventDriveData getEventDriveData() {
		return data;
	}

	public boolean isSuccess() {
		return success;
	}

	public EventDriveDataEvent setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public EventDriveDataEvent success() {
		setSuccess(true);
		return this;
	}

}
