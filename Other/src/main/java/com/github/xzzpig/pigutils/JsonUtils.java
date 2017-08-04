package com.github.xzzpig.pigutils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import com.github.xzzpig.pigutils.event.TransformEvent;
import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;

public class JsonUtils {
	public static JSONObject bean2Json(Object bean) {
		JSONObject json = new JSONObject(bean);
		return json;
	}

	public static <T> T json2Bean(JSONObject json, Class<T> clazz) {
		T bean = null;
		try {
			bean = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
		for (Method method : clazz.getDeclaredMethods()) {
			if (!method.getName().startsWith("set"))
				continue;
			boolean access = method.isAccessible();
			char[] key = method.getName().replaceAll("set", "").toCharArray();
			key[0] = Character.toLowerCase(key[0]);
			Object value = json.opt(new String(key));
			if (value == null)
				continue;

			Parameter[] parameters = method.getParameters();
			if (parameters == null || parameters.length == 0)
				continue;

			method.setAccessible(true);
			Parameter parameter = parameters[0];
			if (value instanceof JSONArray) {
				JSONArray values = (JSONArray) value;
				if (parameter.getType().isArray()) {
					value = Array.newInstance(parameter.getType().getComponentType(), values.length());
					for (int i = 0; i < values.length(); i++) {
						Array.set(value, i, values.get(i));
					}
				}
				if (List.class.isAssignableFrom(parameter.getClass())) {
					value = values.toList();
				}
			} else if (value instanceof JSONObject) {
				JSONObject values = (JSONObject) value;
				if (Map.class.isAssignableFrom(parameter.getClass())) {
					value = values.toMap();
				} else {
					Object obj = json2Bean(values, parameter.getType());
					if (obj != null) {
						value = obj;
					} else {
						obj = TransformEvent.transform(values, parameter.getType());
						if (obj != null) {
							value = obj;
						}
					}
				}
			}
			try {
				method.invoke(bean, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			} finally {
				method.setAccessible(access);
			}

		}
		return bean;
	}

	private JsonUtils() {
	}
}
