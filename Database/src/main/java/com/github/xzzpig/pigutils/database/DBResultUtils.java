package com.github.xzzpig.pigutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.xzzpig.pigutils.data.DataUtils;

public class DBResultUtils {

	private ResultSet result;
	
	public DBResultUtils(ResultSet result) {
		this.result = result;
	}
	
	public String[] getColumNames() throws SQLException{
		String[] names = new String[result.getMetaData().getColumnCount()];
		for(int i:DataUtils.range(result.getMetaData().getColumnCount())){
			names[i] = result.getMetaData().getColumnName(i+1);
		}
		return names;
	}

	public ResultSet getResultSet(){
		return result;
	}
	
}
