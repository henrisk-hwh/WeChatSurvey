package com.softwinner.MicroMSGSurvey;

import java.text.SimpleDateFormat;
import java.util.Date;

public class log {
	public static void e(String s){
		System.out.println(getSysTimeNow()+" E " + s);
	}
	public static void e(double s){
		System.out.println(getSysTimeNow()+" E " + s);
	}
	public static void e(int a){
		System.out.println(getSysTimeNow()+" E " + a);
	}
	public static void d(String s){		
		System.out.println(getSysTimeNow()+" D " + s);
	}
	
	public static void d(double s){		
		System.out.println(getSysTimeNow()+" D " + s);
	}
	public static void d(int a){
		System.out.println(getSysTimeNow()+" D " + a);
	}
    public static String getSysTimeNow() {  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");  
        Date date = new Date();  
        return sdf.format(date);  
    }   
}
