package com.github.xzzpig.pigutils.eddata;

import java.util.Map;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.event.Event;

public class EventDriveData {

	private Object bindObj;

	public EventDriveData(@NotNull Object bindObj) {
		if (bindObj == null) {
			throw new NullPointerException("bindObj can not be Null");
		}
		this.bindObj = bindObj;
	}

	@Override
	public EventDriveData clone() throws CloneNotSupportedException {
		return new EventDriveData(bindObj);
	}

	@Override
	public boolean equals(Object obj) {
        return obj instanceof EventDriveData && this.bindObj.equals(((EventDriveData) obj).bindObj);
    }

	public <T> T get(Object key, Class<T> valueClazz) {
		EventDriveDataGetEvent<T> event = new EventDriveDataGetEvent<>(this, key, valueClazz);
		Event.callEvent(event);
		return event.getValue();
	}

	public <T> T get(Object key, Map<String, Object> extras, Class<T> valueClazz) {
		EventDriveDataGetEvent<T> event = new EventDriveDataGetEvent<>(this, key, valueClazz);
		if (extras != null)
			event.getExtraMap().putAll(extras);
		Event.callEvent(event);
		return event.getValue();
	}

	public @NotNull Object getBind() {
		return bindObj;
	}

	@Override
	public int hashCode() {
		return bindObj.hashCode();
	}

	public boolean remove(Object key) {
		EventDriveDataRemoveEvent event = new EventDriveDataRemoveEvent(this, key);
		Event.callEvent(event);
		return event.isSuccess();
	}

	public boolean remove(Object key, Map<String, Object> extras) {
		EventDriveDataRemoveEvent event = new EventDriveDataRemoveEvent(this, key);
		if (extras != null)
			event.getExtraMap().putAll(extras);
		Event.callEvent(event);
		return event.isSuccess();
	}

	public boolean set(Object key, Object value) {
		EventDriveDataSetEvent event = new EventDriveDataSetEvent(this, key, value);
		Event.callEvent(event);
		return event.isSuccess();
	}

	public boolean set(Object key, Object value, Map<String, Object> extras) {
		EventDriveDataSetEvent event = new EventDriveDataSetEvent(this, key, value);
		if (extras != null)
			event.getExtraMap().putAll(extras);
		Event.callEvent(event);
		return event.isSuccess();
	}
}
