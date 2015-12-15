package com.edu.jnu.util;

import java.util.List;

public class DBHelper {
	
	private static final String CLOTHBASE_TB = Config.dbTable;
	
	private static DBConnection dbcon = new DBConnection();

	
	public static List<Object> fetchALLCloth() {

		String sql = "select * from " + CLOTHBASE_TB;

		return dbcon.excuteQuery(sql, null);
	}
	
	
	public static int updateData (String sql, Object[] data) {
		
		return dbcon.executeUpdate(sql, data);
	}
	
	
	public static List<Object> fetchCloth(int N) {

		String sql = "select * from " + CLOTHBASE_TB  + " limit " + N;

		return dbcon.excuteQuery(sql, null);
	}
	
}
