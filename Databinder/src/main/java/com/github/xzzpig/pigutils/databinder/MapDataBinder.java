package com.github.xzzpig.pigutils.databinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.reflect.ClassUtils;

public class MapDataBinder extends DataBinder {

	private class MapBinder {
		Field valueField;
		Method valueMethod;
		String sourceKey;
		Method transferMethod;
		Object transferObj;
		Field field;

		MapBinder(Field field, Field valueField, Method valueMethod, String sourceKey, Method transferMethod,
				Object transferObj) {
			this.field = field;
			this.valueField = valueField;
			this.valueMethod = valueMethod;
			this.sourceKey = sourceKey;
			this.transferMethod = transferMethod;
			this.transferObj = transferObj;
		}

		public synchronized void update()
				throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			Object sourceValue = source.get(sourceKey);
			boolean access;
			if (transferMethod != null) {
				access = transferMethod.isAccessible();
				try {
					transferMethod.setAccessible(true);
					sourceValue = transferMethod.invoke(transferObj, sourceValue);
				} finally {
					transferMethod.setAccessible(access);
				}
			}
			if (valueField != null) {
				access = valueField.isAccessible();
				boolean access2 = field.isAccessible();
				try {
					valueField.setAccessible(true);
					field.setAccessible(true);
					valueField.set(field.get(target), sourceValue);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					valueField.setAccessible(access);
					field.setAccessible(access2);
				}
			} else if (valueMethod != null) {
				valueMethod.invoke(field.get(target), sourceValue);
			}
		}
	}

	private Object target;
	private Map<String, Object> source;

	private HashMap<String, MapBinder> map = new HashMap<>();
	private List<String> updateList = new ArrayList<>();

	MapDataBinder(@NotNull Object target, @NotNull Map<String, Object> source, @Nullable Object controler) {
		if (target == null || source == null) {
			throw new IllegalArgumentException("target or source can not be null");
		}
		this.source = source;
		this.target = target;
		for (Field f : target.getClass().getDeclaredFields()) {
			if (!f.isAnnotationPresent(BindData.class))
				continue;
			BindData bindData = f.getAnnotation(BindData.class);
			ClassUtils<?> valueCU = new ClassUtils<>(f.getType()), targetCU = new ClassUtils<>(target.getClass());
			Field valueFiled = valueCU.getField(bindData.value());
			Method valueMethod = valueCU.getMethod(bindData.value());
			Method transferMethod = targetCU.getMethod(bindData.transformer());
			Object transferObj = target;
			if (transferMethod == null && controler != null) {
				ClassUtils<?> ctlrCU = new ClassUtils<>(controler.getClass());
				transferMethod = ctlrCU.getMethod(bindData.transformer());
				transferObj = controler;
			}
			MapBinder fieldBinder = new MapBinder(f, valueFiled, valueMethod, bindData.source(), transferMethod,
					transferObj);
			map.put(bindData.source(), fieldBinder);
		}
	}

	public synchronized MapDataBinder set(String fieldName, Object value) {
		source.put(fieldName, value);
		if (map.containsKey(fieldName)) {
			synchronized (updateList) {
				updateList.add(fieldName);
			}
		}
		return this;
	}

	public synchronized MapDataBinder update() {
		synchronized (updateList) {
			map.entrySet().stream().filter(e -> updateList.contains(e.getKey())).forEach(e -> {
				try {
					e.getValue().update();
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
					e1.printStackTrace();
				}
			});
			updateList.clear();
		}
		return this;
	}

	public synchronized MapDataBinder updateAll() {
		map.values().forEach(t -> {
			try {
				t.update();
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
		synchronized (updateList) {
			updateList.clear();
		}
		return this;
	}
}
