package com.github.xzzpig.pigutils.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

public interface IData {
	void clear();

	default Object get(String key) {
		return get(key, Object.class);
	}

	<T> T get(String key, Class<T> value);

	default boolean getBoolean(String key) {
		return get(key, Boolean.class);
	}

	default double getDouble(String key) {
		return get(key, Double.class);
	}

	default int getInt(String key) {
		return get(key, Integer.class);
	}

	default long getLong(String key) {
		return get(key, Long.class);
	}

	default String getString(String key) {
		return get(key, String.class);
	}

	Set<String> keySet();

	default IData load(InputStream in) {
		throw new UnsupportedOperationException();
	}

	Object remove(String key);

	default IData save(OutputStream out) {
		throw new UnsupportedOperationException();
	}

	IData set(String key, Object value);

	int size();

	Collection<Object> values();
}
