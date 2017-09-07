package com.github.xzzpig.pigutils.logger;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.json.JSONObject;

public abstract class LogFormater {

	private static Map<String, LogFormater> map = new HashMap<>();

	public static void addFormater(@NotNull LogFormater formater) {
		map.put(formater.getName(), formater);
	}

	public static LogFormater getFormater(String str) {
		if (map.containsKey(str))
			return map.get(str);
		try {
			Class<?> clazz = Class.forName(str);
			if (LogFormater.class.isAssignableFrom(clazz)) {
				return (LogFormater) clazz.newInstance();
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		}
		return null;
	}

	public LogFormater() {
	}

	public abstract String format(AnnotatedElement element, LogLevel level, JSONObject config, Object... objs);

	public abstract String getName();

	public abstract boolean march(AnnotatedElement element, Object... objs);

	@Override
	public boolean equals(Object obj) {
        return obj instanceof LogFormater && this.getName().equals(((LogFormater) obj).getName());
    }

	/**
	 * @return 是否替换之后
	 *         {@link LogFormater#format(AnnotatedElement, LogLevel, JSONObject, Object...)}中的AnnotatedElement
	 */
	public boolean accept(AnnotatedElement ele) {
		return true;
	}

	/**
	 * @return 是否替换之后
	 *         {@link LogFormater#format(AnnotatedElement, LogLevel, JSONObject, Object...)}中的
	 *         {@link JSONObject}
	 */
	public boolean accept(JSONObject ele) {
		return true;
	}
}
