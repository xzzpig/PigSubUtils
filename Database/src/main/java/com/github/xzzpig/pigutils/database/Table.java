package com.github.xzzpig.pigutils.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.xzzpig.pigutils.annoiation.BaseOnClass;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.data.DataUtils;
import com.github.xzzpig.pigutils.data.DataUtils.EachResult;

@BaseOnClass(DataUtils.class)
public class Table {
	private TableConstruct construct;
	private Database db;
	private String name;
	private int updateNum;

	public Table(String name, Database db) {
		this.name = name;
		this.db = db;
	}

	public Table delete() throws SQLException {
		db.execSql("DELETE FROM " + name, null);
		return this;
	}

	public Table delete(@NotNull Map<String, Object> where) throws SQLException {
		StringBuffer wheresb = new StringBuffer();
		int j = 0;
		List<Object> objs = new ArrayList<>();
		for (Entry<String, Object> entry : where.entrySet()) {
			Object obj = entry.getValue();
			String str;
			if (obj.getClass() == DBFieldType.Blob.targetClazz) {
				objs.add(obj);
				str = "?";
			} else if (obj.getClass() == DBFieldType.Int.targetClazz) {
				str = "" + obj;
			} else if (obj.getClass() == DBFieldType.Double.targetClazz) {
				str = "" + obj;
			} else {
				str = "\"" + obj + "\"";
			}
			if (j == 0)
				wheresb.append(entry.getKey()).append(" = ").append(str);
			else
				wheresb.append(',').append(entry.getKey()).append(" = ").append(str);
		}
		db.execSql("DELETE FROM " + name + " WHERE " + wheresb, objs);
		return this;
	}

	public Table delete(@NotNull String where) throws SQLException {
		db.execSql("DELETE FROM " + name + " WHERE " + where, null);
		return this;
	}

	public Table drop() throws SQLException {
		System.out.println("DROP TABLE " + name);
		db.getConnection().prepareStatement("DROP TABLE " + name).execute();
		return this;
	}

	public TableConstruct getConstruct() {
		return construct;
	}

	public Database getDatabase() {
		return db;
	}

	public int getLastUpdateNum() {
		return updateNum;
	}

	public String getName() {
		return name;
	}

	public Table insert(Map<String, Object> map) throws SQLException {
		StringBuffer sb = new StringBuffer("INSERT INTO ").append(name);
		StringBuffer key = new StringBuffer();
		StringBuffer value = new StringBuffer();
		int j = 0;
		List<Object> objs = new ArrayList<>();
		for (Entry<String, Object> entry : map.entrySet()) {
			if (j == 0)
				key.append(entry.getKey());
			else
				key.append(',').append(entry.getKey());
			Object obj = entry.getValue();
			String str;
			if (obj.getClass() == DBFieldType.Blob.targetClazz) {
				objs.add(obj);
				str = "?";
			} else if (obj.getClass() == DBFieldType.Int.targetClazz) {
				str = "" + obj;
			} else if (obj.getClass() == DBFieldType.Double.targetClazz) {
				str = "" + obj;
			} else {
				str = "\"" + obj + "\"";
			}
			if (j == 0)
				value.append(str);
			else
				value.append("," + str);
		}
		sb.append('(').append(key).append(')').append(" VALUES (").append(value).append(");");
		db.execSql(sb.toString(), objs);
		return this;
	}

	public Table insert(Object... values) throws SQLException {
		StringBuffer sb = new StringBuffer("INSERT INTO ");
		sb.append(name).append(" VALUES (");
		List<Object> objs = new ArrayList<>();
		DataUtils.forEachWithIndex(values, (obj, i) -> {
			String str;
			if (obj.getClass() == DBFieldType.Blob.targetClazz) {
				objs.add(obj);
				str = "?";
			} else if (obj.getClass() == DBFieldType.Int.targetClazz) {
				str = "" + obj;
			} else if (obj.getClass() == DBFieldType.Double.targetClazz) {
				str = "" + obj;
			} else {
				str = "\"" + obj + "\"";
			}
			if (i == 0)
				sb.append(str);
			else
				sb.append("," + str);
			return null;
		});
		sb.append(')').append(';');
		db.execSql(sb.toString(), objs);
		return this;
	}

	public DBSelecter select(){
		return new DBSelecter(this);
	}

	public Table setConstruct(TableConstruct construct) {
		this.construct = construct;
		return this;
	}
	
	public Table update(Map<String, Object> map, String where) throws SQLException {
		StringBuffer sb = new StringBuffer("UPDATE " + name + " SET ");
		StringBuffer sets = new StringBuffer();

		int j = 0;
		List<Object> objs = new ArrayList<>();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object obj = entry.getValue();
			String str;
			if (obj.getClass() == DBFieldType.Blob.targetClazz) {
				objs.add(obj);
				str = "?";
			} else if (obj.getClass() == DBFieldType.Int.targetClazz) {
				str = "" + obj;
			} else if (obj.getClass() == DBFieldType.Double.targetClazz) {
				str = "" + obj;
			} else {
				str = "\"" + obj + "\"";
			}
			if (j == 0)
				sets.append(entry.getKey()).append(" = ").append(str);
			else
				sets.append(',').append(entry.getKey()).append(" = ").append(str);
		}
		if (where != null)
			sb.append(sets).append("WHERE ").append(where);
		SQLException[] exceptions = new SQLException[1];
		if (objs.size() == 0)
			db.withStatment(statment -> {
				try {
					updateNum = statment.executeUpdate(sb.toString());
				} catch (SQLException e) {
					exceptions[0] = e;
				}
			});
		else {
			PreparedStatement ps = db.getConnection().prepareStatement(sb.toString());
			DataUtils.forEachWithIndex(objs, (o, i) -> {
				try {
					ps.setBytes(i + 1, (byte[]) o);
				} catch (SQLException e) {
					exceptions[0] = e;
					return EachResult.BREAK;
				}
				return null;
			});
			updateNum = ps.executeUpdate();
			ps.close();
		}
		if (exceptions[0] != null)
			throw exceptions[0];
		return this;
	}
}
