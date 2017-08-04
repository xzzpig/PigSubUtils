package com.github.xzzpig.pigutils.databinder;

import java.util.Map;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

public abstract class DataBinder {
	@SuppressWarnings("unchecked")
	public static DataBinder bind(@NotNull Object target, @Nullable Object source, @Nullable Object contorler) {
		if (target == null) {
			throw new IllegalArgumentException(new NullPointerException("target can not be null"));
		}
		if (source == null) {
			source = target;
		}
		if (source instanceof Map) {
			return new MapDataBinder(target, (Map<String, Object>) source, contorler);
		} else {
			return new FieldDataBinder(target, source, contorler);
		}
	}

	public abstract DataBinder set(String fieldName, Object value);

	public abstract DataBinder update();

	public abstract DataBinder updateAll();
}
