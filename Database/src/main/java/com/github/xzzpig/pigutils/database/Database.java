package com.github.xzzpig.pigutils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.data.DataUtils;
import com.github.xzzpig.pigutils.data.DataUtils.EachResult;
import com.github.xzzpig.pigutils.reflect.ClassUtils;
import com.github.xzzpig.pigutils.reflect.MethodUtils;

/**
 * 对应数据库实例
 * 
 * @author xzzpig
 *
 */
public class Database {

	private Connection connection;

	public Database(@NotNull Connection connection) {
		new ClassUtils<>(Database.class).checkConstructorArgs(new Class[] { Connection.class }, connection);
		try {
			if (connection.isClosed()) {
				throw new IllegalArgumentException("connection is closed");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
		this.connection = connection;
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Table createTable(@NotNull String name, @NotNull TableConstruct construct) throws SQLException {
		MethodUtils.checkArgs(Database.class, "createTable", name, construct);
		Table table = new Table(name, this);
		table.setConstruct(construct);
		String sql = "CREATE TABLE " + name + " " + construct.toString() + ";";
		connection.prepareStatement(sql).execute();
		return table;
	}

	public void execSql(String sql, List<Object> perpareLists) throws SQLException {
		if (perpareLists == null)
			perpareLists = new ArrayList<Object>();
		SQLException[] exceptions = new SQLException[1];
		if (perpareLists.size() == 0)
			this.withStatment(statment -> {
				try {
					statment.execute(sql);
				} catch (SQLException e) {
					exceptions[0] = e;
				}
			});
		else {
			PreparedStatement ps = this.getConnection().prepareStatement(sql);
			DataUtils.forEachWithIndex(perpareLists, (o, i) -> {
				try {
					ps.setBytes(i + 1, (byte[]) o);
				} catch (SQLException e) {
					exceptions[0] = e;
					return EachResult.BREAK;
				}
				return null;
			});
			ps.execute();
			ps.close();
		}
		if (exceptions[0] != null)
			throw exceptions[0];
	}

	public Connection getConnection() {
		return connection;
	}

	public Table getTable(@NotNull String name) {
		MethodUtils.checkArgs(Database.class, "getTable", name);
		return new Table(name, this);
	}

	public void withStatment(Consumer<Statement> consumer) throws SQLException {
		Statement statement = connection.createStatement();
		consumer.accept(statement);
		statement.close();
	}
}
