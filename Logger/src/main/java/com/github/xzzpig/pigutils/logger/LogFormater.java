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
			} else {
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		}
		return null;
	}

	AnnotatedElement element;
	JSONObject config;
	
	public LogFormater() {
	}

	public abstract String getName();

	public abstract boolean march(AnnotatedElement element, Object... objs);

	public abstract String format(AnnotatedElement element, LogLevel level, JSONObject config, Object... objs);

}
