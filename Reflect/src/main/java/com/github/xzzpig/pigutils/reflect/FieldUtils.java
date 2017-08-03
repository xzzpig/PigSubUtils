package com.github.xzzpig.pigutils.reflect;

import java.lang.reflect.Field;

public class FieldUtils {

	private Field field;

	public FieldUtils(Field field) {
		this.field = field;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object obj, Class<T> clazz) {
		boolean access = false;
		try {
			access = field.isAccessible();
			field.setAccessible(true);
			return (T) field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		} finally {
			field.setAccessible(access);
		}
	}

	public Field getField() {
		return field;
	}

	public boolean set(Object obj, Object value) {
		if (field == null)
			return false;
		boolean access = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return false;
		} finally {
			field.setAccessible(access);
		}
		return true;
	}
}
