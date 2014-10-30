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
import java.util.Map;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class WeChatSurvey extends HttpServlet{
	
	public static final String TOKEN = "henrisktest";
	public static final String CONTENT_TYPE="text/html;charset=utf-8";
	public static final String DEVICE_TYPE="gh_206febbb4f15";
	
	public int mCount = 0;
	public WeChatHandler mWeChat;
	public ArrayList<WeChatDevice> mDeviceList;
	//private Object mDeivceListLock = new Object();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}

	private boolean mMicroMSGVerify = false;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
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
		mWeChat.getDevice();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("===================== doGet =====================");
		
		//设置URL时，验证消息真实性	
		mMicroMSGVerify = WeChatHandler.checkSignature(req);
		if(mMicroMSGVerify){
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
				break;
			
			case "getopenid":
				respondStr = mWeChat.getOpenID(DEVICE_TYPE, device_id);
				cmd = "获取设备 ";
				msg = "device_type: "+ DEVICE_TYPE +" device_id: "+ device_id;
				log.d(respondStr);
				log.d(mWeChat.getOpenIDList(DEVICE_TYPE,device_id).toString());
				break;
				
			case "getusers":
				respondStr = mWeChat.getUserOpenIDList();
				cmd = "获取关注者列表";
				log.d(respondStr);
				break;
				
			case "verifyqrcode":
				String ticket = req.getParameter("ticket");
				respondStr = mWeChat.verifyQrcode(ticket);
				msg = "ticket: " + ticket;
				cmd = "查询二维码";
				break;
				
			case "getqrcode":
				respondStr = mWeChat.getQrcode(device_id);
				msg = "device_id: "+ device_id;
				cmd = "获取二维码";
				break;
			case "setactiontime":
				String second = req.getParameter("actiontime");
				log.d("second: "+ second+" device_id "+device_id);
				WeChatDevice device = getDeivcebyDeviceID(device_id);
				if(device != null)
					device.setActionSecond(Integer.parseInt(second));
				break;
				
			}
			out.println("<html>");
			out.println("<body>");
			out.println(cmd+"  "+ msg);
			out.println("<br>");
			out.println(respondStr);
			out.println("</body></html>");
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
					else
						contentStr = "设备当前没有动作";
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
	
}

