package com.softwinner.WeChatSurvey;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.net.ServerSocket;
import java.net.Socket;
















import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.io.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.softwinner.Workerman.SocketApi.ConnectApi;
import com.softwinner.Workerman.SocketConnection.SocketConnection;
import com.softwinner.log.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class WeChatSurvey extends HttpServlet implements SocketConnection.ConnectionListener{
	
	public static final String TOKEN = "henrisktest";
	public static final String CONTENT_TYPE="text/html;charset=utf-8";
	public static final String DEVICE_TYPE="gh_270bbe8a5791";
	public MYSQLDB mSQL = null;
	public SocketConnection mSocketConnection = null;
	public int mCount = 0;
	public WeChatHandler mWeChat;
	public ArrayList<WeChatDevice> mDeviceList;
	//private Object mDeivceListLock = new Object();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}

	private boolean mWeChatVerify = false;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		if(mSQL != null)
			mSQL.closeConn();
		if(mSocketConnection != null){
			mSocketConnection.finishAll();
			//mSocketConnection.closeSocket();
		}
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		System.out.println(" ");
		System.out.println(" ");
		log.d("----init----");
		mWeChat = new WeChatHandler();
		//mDeviceList =  (ArrayList<WechatDevice>) Collections.synchronizedList(new ArrayList<WechatDevice>());
		mDeviceList = new ArrayList<WeChatDevice>();
		mWeChat.creatMenu();
		//mWeChat.getDevice();
		//mSQL = new MYSQLDB();
		//Connection conn = mSQL.getConn();
		/*
		try {
			PreparedStatement preStat = conn.prepareStatement(MYSQLDB.SQL_TEST);

			ResultSet resSet = preStat.executeQuery();
			

			while(resSet.next()){
				log.d(resSet.getObject(1).toString());
				String s = "student_id="+resSet.getInt("student_id")+
						   ",name="+resSet.getString("name")+
						   ",scoure_id="+resSet.getInt("scoure_id")+
						   ",score="+resSet.getInt("score");
				log.d(s);
			}

			resSet.close();
			preStat.close();

		}catch(SQLException e){
			e.printStackTrace();
		}*/

		//mSQL.queryUser("test1");
		
		mSocketConnection = new SocketConnection();
		mSocketConnection.init();
		mSocketConnection.setListener(this);
		mSocketConnection.startAll();
		
		//test json string
		//ArrayList<String> device_id_list = new ArrayList<String>();
		//device_id_list.add("111");device_id_list.add("333");device_id_list.add("555");
		//log.d(ConnectApi.getWebServerPushMsgString("aa", 11, device_id_list));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("===================== doGet =====================");
		//mSocketConnection.testWrite(new Date().toString());
		//设置URL时，验证消息真实性	
		mWeChatVerify = WeChatHandler.checkSignature(req);
		if(mWeChatVerify){
			//校验通过，返回echostr
			String echostr = req.getParameter("echostr");
			log.d("the echostr is: " + echostr);
			if(null != echostr || !echostr.isEmpty()){
				resp.setContentType(CONTENT_TYPE);//设置响应类型
				PrintWriter out=resp.getWriter();//得到输出流对象
				out.println(echostr);
			}
			else{
				//echostr为空，普通的请求
				
			}
			System.out.println("==================== doGet end ====================");
			return;	
		}
		String cmd = req.getParameter("cmd");
		log.d("cmd: "+cmd);
		if(cmd != null){
			resp.setContentType(CONTENT_TYPE);//设置响应类型
			PrintWriter out=resp.getWriter();//得到输出流对象
			String respondStr = null;
			String device_id = req.getParameter("device_id");
			String msg = null;
			switch(cmd){
			case "getstatus":
				respondStr = mWeChat.queryDeviceStatus(device_id) + "";
				cmd = "获取设备状态";
				msg = "device_id: "+ device_id;
				log.d(respondStr);
				out.println("<html>");
				out.println("<body>");
				out.println(cmd+"  "+ msg);
				out.println("<br>");
				out.println(respondStr);
				out.println("</body></html>");
				break;
			
			case "getopenid":
				respondStr = mWeChat.getOpenID(DEVICE_TYPE, device_id);
				cmd = "获取设备 ";
				msg = "device_type: "+ DEVICE_TYPE +" device_id: "+ device_id;
				log.d(respondStr);
				log.d(mWeChat.getOpenIDList(DEVICE_TYPE,device_id).toString());
				out.println("<html>");
				out.println("<body>");
				out.println(cmd+"  "+ msg);
				out.println("<br>");
				out.println(respondStr);
				out.println("</body></html>");
				break;
				
			case "getusers":
				respondStr = mWeChat.getUserOpenIDList();
				cmd = "获取关注者列表";
				log.d(respondStr);
				out.println("<html>");
				out.println("<body>");
				out.println(cmd+"  "+ msg);
				out.println("<br>");
				out.println(respondStr);
				out.println("</body></html>");
				break;
				
			case "verifyqrcode":
				String ticket = req.getParameter("ticket");
				respondStr = mWeChat.verifyQrcode(ticket);
				msg = "ticket: " + ticket;
				cmd = "查询二维码";
				out.println("<html>");
				out.println("<body>");
				out.println(cmd+"  "+ msg);
				out.println("<br>");
				out.println(respondStr);
				out.println("</body></html>");
				break;
				
			case "getqrcode":
				respondStr = mWeChat.getQrcode(device_id);
				msg = "device_id: "+ device_id;
				cmd = "获取二维码";
				out.println("<html>");
				out.println("<body>");
				out.println(cmd+"  "+ msg);
				out.println("<br>");
				out.println(respondStr);
				out.println("</body></html>");
				break;
			case "setactiontime":{
				String second = req.getParameter("actiontime");
				log.d("second: "+ second+" device_id "+device_id);
				WeChatDevice device = getDeivcebyDeviceID(device_id);
				if(device != null){
					device.setActionSecond(Integer.parseInt(second));
					if(device.getActionSecond() == 0)
						device.setAction(false);
				}
				break;
				}
			case "requestaction":{
				WeChatDevice device = getDeivcebyDeviceID(device_id);

				if(device != null && device.getAction())
					respondStr = "do";				
								
			}
			case "sendmsg":{
				//String s = mWeChat.sendCustomMessage(fromUsername,context);
				//log.d(s);			
			}
			//out.println("<html>");
			//out.println("<body>");
			//out.println(cmd+"  "+ msg);
			//out.println("<br>");
			out.println(respondStr);
			//out.println("</body></html>");
			}
		}	
		System.out.println("==================== doGet end ====================");
	}

	@Override
	//多线程，多个实例，需要保证同步
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("===================== doPost =====================");

		//log.d(req.getCharacterEncoding());
		req.setCharacterEncoding("utf-8");

		//验证消息真实性
		if(!WeChatHandler.checkSignature(req)) return;
		
		//刷新下AccessToken
		mWeChat.syncAccessToken();
		
		Map<String, Object> m = WeChatHandler.getPostMSGParameterMap(req);		
		if(m == null) return;
		
		String fromUsername = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.FROM_USER_NAME);
		String toUsername   = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.TO_USER_NAME);
		String msgtype      = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.MSG_TYPE);		
		String createtime   = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.CREATE_TIME);
		
		log.d("FromUserName: " + fromUsername);
		log.d("ToUserName:   " + toUsername);
		log.d("MsgType:      " + msgtype);
		log.d("CreateTime:   " + createtime);
		
		String respondStr = null;
		String contentStr = null;
		switch(msgtype){
		case WeChatHandler.MSG_TYPE_TEXT:{
			//普通text请求
			String content = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.CONTENT);//获取请求中内容
			contentStr = "测试字符串: "+ content + ".      hello world!  世界，你好!";
			
			mSocketConnection.testWrite(content);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contentStr = contentStr + " 当前在线设备为： "+mSocketConnection.getTestDeviceNum();
			mSocketConnection.clearTestDeivceNum();
			respondStr = WeChatHandler.makeTextRespondString(fromUsername, toUsername, contentStr);

			break;
		}
		case WeChatHandler.MSG_TYPE_EVENT:{
			//事件请求
			String eventkey = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.EVENT_KEY);
			String event = WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.EVENT);
			//菜单点击事件
			if(event.equals(WeChatHandler.MENU_TYPE_CLICK)){
				switch(eventkey){
				case "getstatus":{
					//查询这个user id 是否在设备的open id 列表中
					WeChatDevice device = getDeivcebyOpenID(fromUsername);
					String status = null;
					if(device != null)
						status = WeChatDevice.STATUS[device.getStatus()];
					contentStr = "当前设备状态：" + status;
					break;
				}
				case "getopenid":{
					//查询关注这个设备的所有id
					String list = null;
					WeChatDevice device = getDeivcebyOpenID(fromUsername);
					if(device != null)
						list = device.getOpenIDList().toString();
					contentStr = "关注这个设备的所有用户：" + list;
					break;					
				}
				case "getdeviceid":{
					WeChatDevice device = getDeivcebyOpenID(fromUsername);
					String id = null;
					if(device != null)
						id = device.getDeviceID();
					contentStr = "当前关注设备id：" + id;
					break;						
				}
				case "getactiontime":{
					WeChatDevice device = getDeivcebyOpenID(fromUsername);
					log.d("device id: "+device.getDeviceID());
					int second = device.getActionSecond();
					if(second > 0)
						contentStr = "设备还有 "+ second + "秒 完成动作";
					else{
						contentStr = "设备当前没有动作";						
					}
					break;						
				}
				case "doaction":{
					WeChatDevice device = getDeivcebyOpenID(fromUsername);
					if(device != null)
						device.setAction(true);
					break;
				}
				case "getBLEsteps":{
					contentStr = "目前步数为："+ mCount;
					break;
				}
				case "start":{
					byte[] a = new byte[2];
					a[0] = 1; a[1] = 1;
					log.d("getBASE64: " + getBASE64(a));				
					String s = mWeChat.transmsgtoDevice(DEVICE_TYPE,"6789",fromUsername,getBASE64(a));
					log.d(s);
					contentStr = "开始记步";
					break;
				}
				case "stop":{
					byte[] a = new byte[2];
					a[0] = 0; a[1] = 1;
					log.d("getBASE64: " + getBASE64(a));				
					String s = mWeChat.transmsgtoDevice(DEVICE_TYPE,"6789",fromUsername,getBASE64(a));
					log.d(s);
					contentStr = "停止记步";
					break;
				}
				default: 
					contentStr = "菜单查询："+ WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.EVENT_KEY);
				}
			}	
			respondStr = WeChatHandler.makeTextRespondString(fromUsername, toUsername, contentStr);
			break;
		}
		case WeChatHandler.MSG_TYPE_DEVICE:{
			//设备事件请求
			String event 		= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.EVENT);			
			String devicetype 	= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.DEVICE_TYPE);
			String deviceid 	= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.DEVICE_ID);
			String optype 		= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.OP_TYPE);
			String openid 		= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.OPEN_ID);
			
			log.d("event:        " + event);
			log.d("devicetype:   " + devicetype);
			log.d("deviceid:     " + deviceid);
			log.d("optype:       " + optype);
			log.d("openid:       " + openid);
			
			handleDeivceEvent(event,deviceid,openid);
			if(WeChatDevice.EVENT_SUBSCRIBE.equals(event)){
				respondStr = WeChatHandler.makeWifiDeviceStatusString(fromUsername, toUsername, devicetype, deviceid, 1);
				log.d(respondStr);
				//mSocketConnection.testWrite(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
			}
			break;
		}
		case WeChatHandler.MSG_TYPE_DEVICE_TEXT:{
			//设备数据请求
			String content 		= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.DEVICE_CONTENT);
			String sessionid 	= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.SESSION_ID);
			String devicetype 	= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.DEVICE_TYPE);
			String deviceid 	= WeChatHandler.getPostMSGParameterKey(m,WeChatHandler.DEVICE_ID);
			log.d("content:      " + content);
			log.d("sessionid:    " + sessionid);
			byte[] b = getFromBASE64(content);
			final StringBuilder stringbuilder = new StringBuilder(b.length);
			for(byte bytechar : b)
				stringbuilder.append(String.format("0x%02X ",bytechar));
			log.d("stringbuilder:   " + stringbuilder.toString());
			log.d(b[0]+""+b[1]+""+b[2]+""+b[3]+""+b[4]+"");
			mCount = b[4] + b[3]*10 + b[2]*100 + b[1]*1000 + b[0]*10000;
			log.d("count: "+mCount);
			byte[] a = new byte[2];
			a[0] = 0; a[1] = 1;
			log.d("getBASE64: "+getBASE64(a));
			respondStr = WeChatHandler.makeDeviceContentRespondString(fromUsername, toUsername, devicetype, deviceid, sessionid, getBASE64(a));
					//TextRespondString(fromUsername, toUsername, contentStr);
			//if(mCount%10 == 0){
				String context = "当前步数为："+mCount;
				String s = mWeChat.sendCustomTextMessage(fromUsername,context);
				log.d(s);
			//}
			break;
		}
		default:
			log.d("the msgtype is " + msgtype + ", invaliad WeChat post request,do nothing!");
		}		
		
		resp.setContentType(CONTENT_TYPE);//设置响应类型
		resp.getWriter().println(respondStr);  
		resp.getWriter().flush();  
		resp.getWriter().close();  		
	
		System.out.println("==================== doPost end ====================");
	}

	private boolean isInDeviceList(String device_id)
	{
		for(int i = 0; i < mDeviceList.size(); i++){
			if(mDeviceList.get(i).getDeviceID().equals(device_id))
				return true;			
		}
		return false;
	}
	private void dumpDeviceList()
	{
		for(int i = 0; i < mDeviceList.size(); i++){
			mDeviceList.get(i).dump("dumpDeviceList index: " + i);
		}	
	}
	private WeChatDevice getDeivcebyOpenID(String open_id)
	{
		for(int i = 0; i < mDeviceList.size();i++){
			WeChatDevice device = mDeviceList.get(i);
			if(device.isOpenID(open_id)) return device;
		}
		return null;
	}
	private WeChatDevice getDeivcebyDeviceID(String device_id)
	{
		for(int i = 0; i < mDeviceList.size();i++){
			WeChatDevice device = mDeviceList.get(i);
			if(device.getDeviceID().equals(device_id))
				return device;
		}
		return null;
	}	
	private void removeDeivcebyDeivceID(String device_id,String open_id)
	{
		synchronized (mDeviceList) {
			//如果device id 对应的只有一个openid  则删除device
			//如果device id 对应的只有多个openid  则删除open id list中相应的open id
			log.d("removeDeivcebyDeivceID device_id: "+ device_id+" open_id: "+open_id);
			for(int i = 0; i < mDeviceList.size();i++){
				WeChatDevice device = mDeviceList.get(i);
				if(device.getDeviceID().equals(device_id)){
					ArrayList<String> list = device.getOpenIDList();
					device.dump("remove device1");
					if(list.size() > 1){
						//删除相应的open id
						device.removeOpenID(open_id);
						device.dump("remove device2");
					}else mDeviceList.remove(i);				
				}
			}
			log.d("removeDeivcebyDeivceID end device_id: "+ device_id+" open_id: "+open_id);
		}
	}		
	
	private void addDeivcebyDeivceID(String deviceid)
	{
		synchronized (mDeviceList) {
			//如果是新的device，加入device list
			log.d("addDeivcebyDeivceID device_id:"+ deviceid);
			if(!isInDeviceList(deviceid)){
				WeChatDevice device = new WeChatDevice(deviceid,DEVICE_TYPE);
				mWeChat.syncDeviceInfo(device, DEVICE_TYPE);
				device.dump("addDeivcebyDeivceID");
				mDeviceList.add(device);
				log.d("DeviceList num: "+ mDeviceList.size());
			}else{
				//如果是旧device，则同步一下信息
				WeChatDevice device = getDeivcebyDeviceID(deviceid);
				device.dump("sync before");
				mWeChat.syncDeviceInfo(device, DEVICE_TYPE);
				device.dump("sync after");
			}
			log.d("addDeivcebyDeivceID end device_id:"+ deviceid);
		}
	}
	private void handleDeivceEvent(String event,String deviceid,String openid)
	{
		
		switch(event){
			case WeChatDevice.EVENT_SUBSCRIBE:{
				//进入公众号界面请求
				WeChatDevice device = getDeivcebyDeviceID(deviceid);
				 log.d("device "+ device);
				 if(device == null){
					 //如果list中没有device则加入
					 addDeivcebyDeivceID(deviceid);
				 }
				 else if(!device.getSynchronize()){
					 //如果需要同步则同步
					 synchronized (device) {
						 //涉及到里面设备对象，必须要加锁操作
						 mWeChat.syncDeviceInfo(device, DEVICE_TYPE);
						 device.dump("SUBSCRIBE_STATUS");							
					 }
				 }
				 //应该请求和设备保持连接
				break;				
			}
			case WeChatDevice.EVENT_UNSUBSCRIBE:{
				//退出公众号界面请求
				WeChatDevice device = getDeivcebyDeviceID(deviceid);
				 if(device != null){
					synchronized (device) {
					//关于一些设备操作	
					}
				 }
				 //断开连接
				break;
			}
			case WeChatDevice.EVENT_BIND:{
				//绑定
				addDeivcebyDeivceID(deviceid);
				break;
			}
			case  WeChatDevice.EVENT_UNBIND:{
				//解除绑定
				removeDeivcebyDeivceID(deviceid,openid);
				break;
			}
		
		}
		dumpDeviceList();		
	}	
	// 将 BASE64 编码的字符串 s 进行解码
	public static byte[] getFromBASE64(String s) {
		if (s == null) return null;
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				byte[] b = decoder.decodeBuffer(s);
				return b;
			} catch (Exception e) {
			return null;
		}
	}
	// 将 s 进行 BASE64 编码
	public static String getBASE64(byte[] s) {
	if (s == null) return null;
		return (new sun.misc.BASE64Encoder()).encode( s );
	}

	@Override
	public void onPushIamge(String path,String device_id) {
		// TODO Auto-generated method stub

		String media_id = mWeChat.uploadImageMedia(path);
		if(media_id != null){			
			WeChatDevice device = getDeivcebyDeviceID(device_id);
			if(device == null) return;
			for(String open_id: device.getOpenIDList()){
				log.d(mWeChat.sendCustomImageMessage(open_id,media_id));
			}
		}
		else
			log.e("media id is null");			

	}
	
}

