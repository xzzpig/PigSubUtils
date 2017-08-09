package com.github.xzzpig.pigutils.database;

import java.util.HashMap;
import java.util.Map;

public class DBFieldType {
	// Number,Text,Blob

	public static final Map<String, DBFieldType> MAP = new HashMap<>();
	public static final DBFieldType Blob = valueOf("Blob", byte[].class);
	public static final DBFieldType Double = valueOf("DOUBLE", Double.class);
	public static final DBFieldType Int = valueOf("INTEGER", Integer.class);
	public static final DBFieldType Text = valueOf("TEXT", String.class);

	public String nameInDB;
	public Class<?> targetClazz;

	public static DBFieldType valueOf(String name) {
		if (MAP.containsKey(name))
			return MAP.get(name);
		MAP.put(name, new DBFieldType(name, Object.class));
		return MAP.get(name);
	}

	public static DBFieldType valueOf(String name, Class<?> clazz) {
		return valueOf(name).setClass(clazz);
	}

	private DBFieldType setClass(Class<?> clazz) {
		this.targetClazz = clazz;
		return this;
	}

	private DBFieldType(String name, Class<?> clazz) {
		this.nameInDB = name;
		this.targetClazz = clazz;
	}

}
