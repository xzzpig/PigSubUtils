package com.github.xzzpig.pigutils.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashData implements IData{

	Map<String,Object> map = new HashMap<>();
	
	@Override
	public void clear() {
		map.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> value) {
		return (T) map.get(key);
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Object remove(String key) {
		return map.remove(key);
	}

	@Override
	public IData set(String key, Object value) {
		map.put(key, value);
		return this;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}
}
