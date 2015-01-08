package com.softwinner.WeChatSurvey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;





public class MYSQLDB {
	public Connection conn;
	public static final String DRIVER = "org.gjt.mm.mysql.Driver";
	public static final String SQL_TEST = "call test.new_procedure()";
	public Connection getConn(){
		try{
			Class.forName(DRIVER);
			String url = "jdbc:mysql://localhost:3306/test?user=root&password=henrisk";
			conn = DriverManager.getConnection(url);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public void closeConn(){
		try{
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
