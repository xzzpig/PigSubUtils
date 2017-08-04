package com.github.xzzpig.pigutils.database;

public class DBField {

	public static String autoIncrementString = "AUTOINCREMENT";

	private Object defaultValue;
	private String name;
	private boolean primaryKey, unique, notNull, autoIncrement;
	private DBFieldType type;

	public DBField() {
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
		return name;
	}

	public DBFieldType getType() {
		return type;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public DBField setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public DBField setName(String name) {
		this.name = name;
		return this;
	}

	public DBField setNotNull(boolean notNull) {
		this.notNull = notNull;
		return this;
	}

	public DBField setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}

	public DBField setType(DBFieldType type) {
		this.type = type;
		return this;
	}

	public DBField setUnique(boolean unique) {
		this.unique = unique;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(name);
		sb.append(' ').append(type.nameInDB);
		if (primaryKey)
			sb.append(' ').append("PRIMARY KEY");
		if (unique)
			sb.append(' ').append("UNIQUE");
		if (notNull)
			sb.append(' ').append("NOT NULL");
		if (defaultValue != null)
			sb.append(' ').append("DEFAULT").append(' ').append('(').append(defaultValue).append(')');
		if (autoIncrement)
			sb.append(' ').append(autoIncrementString);
		return sb.toString();
	}

}
