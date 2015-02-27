package com.softwinner.WeChatSurvey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.softwinner.log.log;



public class MYSQLDB {
	public Connection conn = null;
	public static final String DRIVER = "org.gjt.mm.mysql.Driver";
	public static final String SQL_TEST = "call test.new_procedure()";
	
	public static final String TABLE_USER = "user";
	public static final String TABLE_DEVICE="deivce";
	public static final String TABLE_DATA="data";
	
	public static final String USER_ID = "open_id";	
	
	public static final String DEVICE_ID = "device_id";
	public static final String DEVICE_MAC = "mac";
	
	public static final String DATA_TIME = "time";
	public static final String DATA = "data";
	
	public static final String INSERT_SQL = "INSERT INTO %1$s (%2$s) VALUES (%3$s);";
	public static final String QUERY_SQL = "SELECT %1$s FROM %2$s WHERE %3$s=%4$s;";
	public static final String DELETE_SQL = "DELETE FROM %1$s WHERE %2$s=%3$s;";
		
	public Connection getConn(){
		if(conn != null) return conn;
		try{
			Class.forName(DRIVER);
			String url = "jdbc:mysql://localhost:3306/wechatdb?user=root&password=henrisk";
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
	public boolean queryUser(String openid){
		String sql = String.format(QUERY_SQL,USER_ID,TABLE_USER,USER_ID,make(openid));
		log.d(sql);
		ResultSet resSet = executeQuery(sql);
		//printfResultSet(resSet);
		//
		try {
			while(resSet.next()){
				if(openid.equals(resSet.getString(1)))
					return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public int insertUser(String openid){
		//查询用户是否存在
		if(queryUser(openid)){
			log.d("user:"+openid+",had existed in database!");
			return 1;
		};
		String sql = String.format(INSERT_SQL,TABLE_USER,USER_ID,make(openid));
		log.d(sql);

		return executeUpdate(sql);

	}
	public int deleteUser(String openid){
		//查询用户是否存在
		if(!queryUser(openid)){
			log.d("user: "+openid+",had no existed in database!");
			return 1;
		};
		String sql = String.format(DELETE_SQL,TABLE_USER,USER_ID,make(openid));
		log.d(sql);

		return executeUpdate(sql);
	}

	public boolean queryDevice(String deviceid){
		String sql = String.format(QUERY_SQL,DEVICE_ID,TABLE_DEVICE,DEVICE_ID,make(deviceid));
		log.d(sql);
		ResultSet resSet = executeQuery(sql);
		//printfResultSet(resSet);
		try {
			while(resSet.next()){
				if(deviceid.equals(resSet.getString(1)))
					return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public int insertDevice(String deviceid,String mac,String devicetype,String openid){
		//查询用户是否存在
		if(queryDevice(deviceid)){
			log.d("device:"+deviceid+",had existed in database!");
			return 1;
		};
		String s = make(deviceid);
		String col = make(DEVICE_ID);

		if(mac != null){
			s = s + ","+make(mac);
			col = make(DEVICE_ID)+","+make(DEVICE_MAC);
		}
		String sql = String.format(INSERT_SQL,TABLE_DEVICE,col,s);
		log.d(sql);

		return executeUpdate(sql);

	}
	public int deleteDevice(String deviceid){
		//查询用户是否存在
		if(!queryDevice(deviceid)){
			log.d("device: "+deviceid+",had no existed in database!");
			return 1;
		};
		String sql = String.format(DELETE_SQL,TABLE_DEVICE,DEVICE_ID,make(deviceid));
		log.d(sql);

		return executeUpdate(sql);
	}	
	
	
	//执行SELECT语句
	public ResultSet executeQuery(String sql){
		try {
			return conn.prepareStatement(sql).executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	//执行INSERT，UPDATE or DELETE; 或者 一个 不许要返回的SQL 操作
	public int executeUpdate(String sql){
		try {
			return conn.prepareStatement(sql).executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	public static void printfResultSet(ResultSet rs){
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();//获取列数
			log.d("colCount: "+colCount);
			while(rs.next()){
				String s = null;
				for(int i = 1;i <= colCount;i++){
					String tpl = rsmd.getColumnName(i) + " = " + rs.getString(i) + "  ";
					if(s == null) s = tpl;
					else s = s + tpl;
				}
				log.d(s);
			}
			rs.first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static String make(String s){
		return "\'"+s+"\'";
	}
}
