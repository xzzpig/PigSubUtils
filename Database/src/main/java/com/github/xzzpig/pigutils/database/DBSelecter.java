package com.github.xzzpig.pigutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.data.DataUtils;

public class DBSelecter {

	private String[] colums = { "*" };
	private boolean distinct;

	private Table mainTable;
	private List<Table> tables = new ArrayList<>();
	private String where, other;

	public DBSelecter(@NotNull Table table) {
		mainTable = table;
		this.tables.add(table);
	}

	public DBSelecter addTable(Table table) {
		if (table.getDatabase() != mainTable.getDatabase()) {
			throw new IllegalArgumentException("arg0 is not from same DB of MainTable");
		}
		tables.add(table);
		return this;
	}

	public ResultSet select() throws SQLException {
		Statement statement = mainTable.getDatabase().getConnection().createStatement();
		ResultSet resultSet = statement.executeQuery(this.toString());
//		statement.close();
		return resultSet;
	}

	public DBSelecter setColums(@Nullable String... colums) {
		if (colums == null)
			return this;
		this.colums = colums;
		return this;
	}

	public DBSelecter setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public DBSelecter setOther(String other) {
		this.other = other;
		return this;
	}

	public DBSelecter setWhere(String where) {
		this.where = where;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("SELECT ");
		if (distinct)
			sb.append("DISTINCT ");
		DataUtils.forEachWithIndex(colums, (str, i) -> {
			if (i == 0)
				sb.append(str);
			else
				sb.append(',').append(str);
			return null;
		});
		sb.append(" FROM ");
		DataUtils.forEachWithIndex(tables, (table, i) -> {
			if (i == 0)
				sb.append(table.getName());
			else
				sb.append(',').append(table.getName());
			return null;
		});
		if (other != null)
			sb.append(' ').append(other);
		if (where != null)
			sb.append(" WHERE ").append(where);
		return sb.toString();
	}
}
