package com.softwinner.Workerman.SocketApi;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.softwinner.log.log;

public class ConnectApi {
	public static int ROLE_DEVICE = 1;
	public static int ROLE_WEBSEVER = 0;
	
	public static int TYPE_CONNCET = 3000;
	public static int TYPE_UPDATE = 3001;
	public static int TYPE_PUSH = 3002;

	public static int MSG_AUTH_REQ = 4000;
	public static int MSG_AUTH_RESP = 4001;
	public static int MSG_AUTH_VEFY = 4002;
	public static int MSG_AUTH_FIAL = 4003;
	
	public static int MSG_PUSH_ALL = 5000;
	public static int MSG_PUSH_DEVICE = 5001;
	
	public static String getWebServerAuthRespString(){
		JSONObject json = new JSONObject();
		json.put("role", ROLE_WEBSEVER);
		json.put("type", TYPE_CONNCET);
		json.put("msg", MSG_AUTH_RESP);
		return json.toString();
	}
	
	public static String getPushMsgString(String data,int msg,String device_id){
		JSONObject json = new JSONObject();
		json.put("type", TYPE_PUSH);
		json.put("msg", msg);
		if(device_id != null)
			json.put("id", device_id);
		json.put("data", data);
		return json.toString();		
	}
}
