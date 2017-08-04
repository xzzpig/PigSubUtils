package com.github.xzzpig.pigutils.database;

public class DBFieldType {
	// Number,Text,Blob

	public static final DBFieldType Blob = new DBFieldType("Blob", byte[].class);
	public static final DBFieldType Double = new DBFieldType("DOUBLE", Double.class);
	public static final DBFieldType Int = new DBFieldType("INTEGER", Integer.class);
	public static final DBFieldType Text = new DBFieldType("TEXT", String.class);

	public String nameInDB;
	public Class<?> targetClazz;

	private DBFieldType(String name, Class<?> clazz) {
		this.nameInDB = name;
		this.targetClazz = clazz;
	}

}
