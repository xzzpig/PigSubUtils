package com.github.xzzpig.pigutils.database;

import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigutils.data.DataUtils;

public class TableConstruct {

	private List<DBField> fields = new ArrayList<>();

	private Table table;

	public TableConstruct() {
	}

	TableConstruct(Table table) {
		this.table = table;
	}

	public TableConstruct addDBField(DBField field) {
		fields.add(field);
		return this;
	}

	public Table getTable() {
		return table;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		DataUtils.forEachWithIndex(fields, (field, i) -> {
			if (i == 0)
				sb.append(field.toString());
			else
				sb.append(',').append(field.toString());
			return null;
		});
		sb.append(')');
		return sb.toString();
	}
}
