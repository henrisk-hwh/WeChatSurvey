package com.softwinner.WeChatSurvey;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WeChatHandler {
	public static final String TOKEN = "henrisktest";
	
	public static final String TO_USER_NAME 		= "ToUserName";
	public static final String FROM_USER_NAME 		= "FromUserName";
	public static final String CREATE_TIME			= "CreateTime";
	public static final String MSG_TYPE				= "MsgType";
	public static final String CONTENT				= "Content";
	public static final String MSG_ID				= "MsgId";
	public static final String EVENT				= "Event";
	public static final String EVENT_KEY			= "EventKey";
	
	//msgtpye
	public static final String MSG_TYPE_TEXT		= "text";
	public static final String MSG_TYPE_EVENT		= "event";
	public static final String MSG_TYPE_DEVICE		= "device_event";
	
	//menu type
	public static final String MENU_TYPE_CLICK				= "CLICK";
	public static final String MENU_TYPE_VIEW				= "VIEW";
	public static final String MENU_TYPE_SCAN_CODE_PUSH		= "scancode_push";
	public static final String MENU_TYPE_SCAN_CODE_WAITMSG	= "scancode_waitmsg";
	
	//device
	public static final String DEVICE_TYPE			= "DeviceType";
	public static final String DEVICE_ID			= "DeviceID";
	public static final String OP_TYPE				= "OpType";
	public static final String OPEN_ID				= "OpenID";
	
	
	private AccessToken mAccessToken = null;
	
	public WeChatHandler(){
		mAccessToken = new AccessToken();		
	}
	
	//检查消息真实性
    public static boolean checkSignature(HttpServletRequest request){
		//验证消息真实性
		/*	在开发者首次提交验证申请时，微信服务器将发送GET请求到填写的URL上，
		 *	并且带上四个参数（signature、timestamp、nonce、echostr），
		 *	开发者通过对签名（即signature）的效验，来判断此条消息的真实性。
		 *	此后，每次开发者接收用户消息的时候，微信也都会带上前面三个参数
		 *	（signature、timestamp、nonce）访问开发者设置的URL，
		 *	开发者依然通过对签名的效验判断此条消息的真实性。效验方式与首次提交验证申请一致。
		 *	
		 *	signature:	微信加密签名，signature结合了开发者填写的token参数和请求中的
		 *				timestamp参数、nonce参数。
		 *	timestamp：	时间戳 
		 *	nonce ：		随机数 
		 *	echostr：	随机字符串 
		 *
		 * 	开发者通过检验signature对请求进行校验（下面有校验方式）。
		 * 	若确认此次GET请求来自微信服务器，请原样返回echostr参数内容，
		 * 	则接入生效，成为开发者成功，否则接入失败。
		 * 	加密/校验流程如下：
		 * 		1. 将token、timestamp、nonce三个参数进行字典序排序
		 * 		2. 将三个参数字符串拼接成一个字符串进行sha1加密
		 * 		3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信 
		 **/
    	
        String signature = request.getParameter("signature");  
        String timestamp = request.getParameter("timestamp");  
        String nonce = request.getParameter("nonce");
		if(signature == null || timestamp ==null || nonce == null){
			log.e("signature=" + signature + ", timestamp=" + timestamp + ", nonce="+ nonce+". error check parameter!!");
			return false;
		}
  
		//字典排序
        String[] tmpArr={TOKEN,timestamp,nonce};  
        Arrays.sort(tmpArr);  
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tmpArr.length; i++) {
            sb.append(tmpArr[i]);
        } 
        
        //sha1加密
        String tmpStr = SHA1Encode(sb.toString());  
        if(tmpStr.equalsIgnoreCase(signature)){
        	log.d("checkSignature successfully!");
            return true;  
        }else{
        	log.d("TOKEN: "+TOKEN);
        	log.d("signature: "+signature);
        	log.d("timestamp: "+timestamp);
        	log.d("nonce: "+nonce);
            return false;  
        }  
    } 
    //sha1加密算法
    private static String SHA1Encode(String sourceString) {  
        String resultString = null;  
        try {  
           resultString = new String(sourceString);  
           MessageDigest md = MessageDigest.getInstance("SHA-1");  
           resultString = byte2hexString(md.digest(resultString.getBytes()));  
        } catch (Exception ex) {  
        }  
        return resultString;  
    }  
    private static final String byte2hexString(byte[] bytes) {  
        StringBuffer buf = new StringBuffer(bytes.length * 2);  
        for (int i = 0; i < bytes.length; i++) {  
            if (((int) bytes[i] & 0xff) < 0x10) {  
                buf.append("0");  
            }  
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));  
        }  
        return buf.toString().toUpperCase();  
    }

    public static String getPostMSGParameterKey(Map<?, ?> m,String key)
    {
		Object obj = new Object();
		if(m != null){
			obj = m.get(key);
			if(obj != null)
				return obj.toString();
		}
		return null;
    }
    //获取POST消息xml内容 
	public static Map<String, Object> getPostMSGParameterMap(HttpServletRequest request)
    {
		Map<String, Object> m = null;
    	
    	//获取post流中的xml字符串
    	String postStr = null ;
		try {
			postStr = readStreamXMLParameter(request.getReader());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
    	
		//test string
    	//postStr = "<xml><ToUserName><![CDATA[gh_206febbb4f15]]></ToUserName><FromUserName><![CDATA[o4mxXty_6U0zv5_KBmcgp9nLgGo0]]></FromUserName><CreateTime>1411641083</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[Gfhfgg]]></Content><MsgId>6062952285375556026</MsgId></xml>";
    	log.d("post xml string: " + postStr);
    	
    	if (null != postStr && !postStr.isEmpty()){  
            Document document=null;  
            try{  
                document = DocumentHelper.parseText(postStr);  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
            if(null==document){  
                log.e("document is null!!");
                return null;
            }  
            Element root=document.getRootElement();

            m = Dom2Map(root);
            /*
            Iterator i = m.entrySet().iterator();
            while(i.hasNext()){
            	Map.Entry e  = (Map.Entry)i.next();
            	log.d(e.getKey()+ "=" + e.getValue());            	
            }
            */

        }else {  
            log.e("post string is null, do nothing!");
            m = null;
        }  
    	
    	return m;
    	
    }
    
	//构建文本回复格式字符串
    public static String makeTextRespondString(String toUsername,String fromUsername,String contentStr)
    {
    	String resultStr = null;
    	String msgType = "text";
    	String time = new Date().getTime()/1000+"";
        String textTpl = "<xml>"+
                		 "<ToUserName><![CDATA[%1$s]]></ToUserName>"+
                		 "<FromUserName><![CDATA[%2$s]]></FromUserName>"+
                		 "<CreateTime>%3$s</CreateTime>"+
                		 "<MsgType><![CDATA[%4$s]]></MsgType>"+
                		 "<Content><![CDATA[%5$s]]></Content>"+
                		 "<FuncFlag>0</FuncFlag>"+
                		 "</xml>";
        if(null != contentStr && !contentStr.equals(""))
        {
            //String contentStr = "Welcome to wechat world!";
            resultStr = String.format(textTpl, toUsername, fromUsername, time, msgType, contentStr);
            log.d(resultStr);
        }else{
        	resultStr = null;
        	log.d("Input something...");
        }
    	return resultStr;
    }
	//从输入流读取post的xml参数  
    private static String readStreamXMLParameter(BufferedReader br){ 

		String s;
		StringBuffer sb = new StringBuffer();
		try {
			while((s = br.readLine())!=null){	
				//log.d(s);
				sb.append(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return sb.toString();		
    }
    private static Map<String, Object> Dom2Map(Element e){ 
        Map<String, Object> map = new HashMap<String, Object>(); 
        List<?> list = e.elements(); 
        if(list.size() > 0){ 
            for (int i = 0;i < list.size(); i++) { 
                Element iter = (Element) list.get(i); 
                List<Object> mapList = new ArrayList<Object>(); 
                 
                if(iter.elements().size() > 0){ 
                    Map<String, Object> m = Dom2Map(iter); 
                    if(map.get(iter.getName()) != null){ 
                        Object obj = map.get(iter.getName()); 
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = new ArrayList<Object>(); 
                            mapList.add(obj); 
                            mapList.add(m); 
                        } 
                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = (List<Object>) obj; 
                            mapList.add(m); 
                        } 
                        map.put(iter.getName(), mapList); 
                    }else 
                        map.put(iter.getName(), m); 
                } 
                else{ 
                    if(map.get(iter.getName()) != null){ 
                        Object obj = map.get(iter.getName()); 
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = new ArrayList<Object>(); 
                            mapList.add(obj); 
                            mapList.add(iter.getText()); 
                        } 
                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = (List<Object>) obj; 
                            mapList.add(iter.getText()); 
                        } 
                        map.put(iter.getName(), mapList); 
                    }else 
                        map.put(iter.getName(), iter.getText()); 
                } 
            } 
        }else 
            map.put(e.getName(), e.getText()); 
        return map; 
    } 

    //群发接口
    public Map<?, ?> sendPublicMSG2Group(String group,String access_token){
    /*
     * 1、该接口暂时仅提供给已微信认证的服务号
     * 2、虽然开发者使用高级群发接口的每日调用限制为100次，但是用户每月只能接收4条，请小心测试
     * 3、无论在公众平台网站上，还是使用接口群发，用户每月只能接收4条群发消息，多于4条的群发将对该用户发送失败。
     * 4、具备微信支付权限的公众号，在使用高级群发接口上传、群发图文消息类型时，可使用<a>标签加入外链
     */ 	
    	return null;
    }
    
    //发送客服信息接口
    public Map<?, ?> sendCustomerMSG2User(String username,String access_token)
    {
    	/*
    	 * 当用户主动发消息给公众号的时候（包括发送信息、点击自定义菜单、订阅事件、扫描二维码事件、支付成功事件、用户维权），
    	 * 微信将会把消息数据推送给开发者，开发者在一段时间内（目前修改为48小时）可以调用客服消息接口，通过POST一个JSON数
    	 * 据包来发送消息给普通用户，在48小时内不限制发送次数。此接口主要用于客服等有人工消息处理环节的功能，方便开发者为用
    	 * 户提供更加优质的服务。 
    	 */
    	return null;
    }
    
    //获取关注者列表
    public String getUserOpenIDList()
    {
    	String url =  "https://api.weixin.qq.com/cgi-bin/user/get";
    	String param = "access_token="+ mAccessToken.getAccessToken();
    	String s = HttpRequest.sendGet(url,param);
    	return s;
    }

    //自定义菜单创建接口
    public boolean creatMenu()
    {
    	String url =  "https://api.weixin.qq.com/cgi-bin/menu/create";
    	String param = "access_token="+ mAccessToken.getAccessToken();
    	url = url + "?" + param ;

    	//json string
    	String ss = "{\"button\":[{\"type\":\"click\",\"name\":\"AWtech\",\"key\":\"全志科技\"},{\"type\":\"click\",\"name\":\"芯片查询\",\"key\":\"芯片查询\"},{\"name\":\"日常工作\",\"sub_button\":[{\"type\":\"click\",\"name\":\"A80\",\"key\":\"A80 SDK\"},{\"type\":\"click\",\"name\":\"A83\",\"key\":\"A83 SDK\"},{\"type\":\"click\",\"name\":\"A33\",\"key\":\"A33 SDK\"},{\"type\":\"click\",\"name\":\"A23\",\"key\":\"A23 SDK\"},{\"type\":\"view\",\"name\":\"联系我们\",\"url\":\"http://www.allwinnertech.com/\"}]}]}";
    	String menu = "{"
    					+ "\"button\":["
    						+ "{"
    							+ "\"name\":\"设备查询\","
    							+ "\"sub_button\":["
									+ "{"
										+ "\"type\":\"click\","
										+ "\"name\":\"查询设备状态\","
										+ "\"key\":\"getstatus\""
									+ "},"
									+ "{"
										+ "\"type\":\"click\","
										+ "\"name\":\"查询所有关注者ID\","
										+ "\"key\":\"getopenid\""
									+ "},"
									+ "{"
										+ "\"type\":\"click\","
										+ "\"name\":\"查询设备ID\","
										+ "\"key\":\"getdeviceid\""
									+ "},"
									+ "{"
										+ "\"type\":\"click\","
										+ "\"name\":\"查询动作时间\","
										+ "\"key\":\"getactiontime\""
									+ "}"		
							+ "]},"   
    						+ "{"
    							+ "\"type\":\"click\","
    							+ "\"name\":\"芯片查询\","
    							+ "\"key\":\"芯片查询\""
    						+ "},"
    						+ "{"
    							+ "\"name\":\"日常工作\","
    							+ "\"sub_button\":["
    								+ "{"
    									+ "\"type\":\"click\","
    									+ "\"name\":\"A80\","
    									+ "\"key\":\"A80 SDK\""
    								+ "},"
    								+ "{"
    									+ "\"type\":\"click\","
    									+ "\"name\":\"A83\","
    									+ "\"key\":\"A83 SDK\""
    								+ "},"
    								+ "{"
    									+ "\"type\":\"click\","
    									+ "\"name\":\"A33\","
    									+ "\"key\":\"A33 SDK\""
    								+ "},"
    								+ "{"
    									+ "\"type\":\"click\","
    									+ "\"name\":\"A23\","
    									+ "\"key\":\"A23 SDK\""
    								+ "},"
    								+ "{"
    									+ "\"type\":\"view\","
    									+ "\"name\":\"联系我们\","
    									+ "\"url\":\"http://www.allwinnertech.com/\""
    								+ "}"
    							+ "]}"
    						+ "]"
    					+ "}";
    	
    	log.d(menu);

    	String s = HttpRequest.sendPost(url,menu);
 
		JSONObject jp = JSON.parseObject(s);
		String errcode = jp.getString("errcode");
		String errmsg = jp.getString("errmsg");
		log.d("errcode: "+errcode+", errmsg: "+ errmsg);
    	return true;
    }
    
    //自定义菜单类
    public class Menu
    {
    	/* 目前自定义菜单最多包括3个一级菜单，每个一级菜单最多包含5个二级菜单。一级菜单最多4个汉字，
    	 * 二级菜单最多7个汉字，多出来的部分将会以“...”代替。请注意，创建自定义菜单后，由于微信客户
    	 * 端缓存，需要24小时微信客户端才会展现出来。建议测试时可以尝试取消关注公众账号后再次关注，
    	 * 则可以看到创建后的效果。
    	 * 
    	 *  自定义菜单接口可实现多种类型按钮，如下：
    	 *  1、click：点击推事件用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event
    	 *     的结构给开发者（参考消息接口指南），并且带上按钮中开发者填写的key值，开发者可以通过自定义
    	 *     的key值与用户进行交互；
    	 *  
    	 *  2、view：跳转URL用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL，可与
    	 *     网页授权获取用户基本信息接口结合，获得用户基本信息。
    	 *  
    	 *  3、scancode_push：扫码推事件用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后显示扫
    	 *     描结果（如果是URL，将进入URL），且会将扫码的结果传给开发者，开发者可以下发消息。
    	 *     
    	 *  4、scancode_waitmsg：扫码推事件且弹出“消息接收中”提示框用户点击按钮后，微信客户端将调起扫一
    	 *     扫工具，完成扫码操作后，将扫码的结果传给开发者，同时收起扫一扫工具，然后弹出“消息接收中”提示
    	 *     框，随后可能会收到开发者下发的消息。
    	 *     
    	 *  5、pic_sysphoto：弹出系统拍照发图用户点击按钮后，微信客户端将调起系统相机，完成拍照操作后，会将
    	 *     拍摄的相片发送给开发者，并推送事件给开发者，同时收起系统相机，随后可能会收到开发者下发的消息。
    	 *     
    	 *  6、pic_photo_or_album：弹出拍照或者相册发图用户点击按钮后，微信客户端将弹出选择器供用户选择“拍照”
    	 *     或者“从手机相册选择”。用户选择后即走其他两种流程。
    	 *     
    	 *  7、pic_weixin：弹出微信相册发图器用户点击按钮后，微信客户端将调起微信相册，完成选择操作后，将选择的
    	 *     相片发送给开发者的服务器，并推送事件给开发者，同时收起相册，随后可能会收到开发者下发的消息。
    	 *     
    	 *  8、location_select：弹出地理位置选择器用户点击按钮后，微信客户端将调起地理位置选择工具，完成选择
    	 *     操作后，将选择的地理位置发送给开发者的服务器，同时收起位置选择工具，随后可能会收到开发者下发的消息。
    	 */
    	public static final String BOTTON = "\"botion\"";
    	public static final String TYPE = "\"type\"";
    	public static final String NAME = "\"name\"";
    	public static final String KEY = "\"key\"";
    	public static final String CLICK = "\"click\"";
    	public String mMenuString = null;
    	
    	public String getDemoMenuString()
    	{    		
    		String a = makeString(TYPE,CLICK);
    		String b = makeString(NAME,"\"今日歌曲\"");
    		String c = makeString(KEY,"\"henrisk\"");
    		mMenuString = a + "," + b + "," + c;
    		mMenuString = addBrace(mMenuString);
    		mMenuString = addBracket(mMenuString);
    		mMenuString = makeString(BOTTON,mMenuString);
    		mMenuString = addBrace(mMenuString);
    		return mMenuString;
    	}
    	
    	private String makeString(String key,String value){
    		return key + ":" + value;
    	}
    	private String addBrace(String s){
    		return "{" + s+ "}";
    	}
    	private String addBracket(String s){
    		return "[" + s + "]";
    	}
    	
    }   
    
    
	//管理access-token 内部类
    //主要维护当前access-token是否有效，有效返回当前access-token，过期重新申请新的access-token
	public class AccessToken
	{
				
		//芯智慧服务号相关id
		//private static final String AppId = "wx96af4e9cb2a6ab51";
		//private static final String Appsecret = "76dcf6b99c4ef85683ef90787ee915af";
		
		//测试号相关id
		private static final String AppId = "wx344a43b36f5ef1c6";
		private static final String Appsecret = "b6108a59b5e413c48080892d5561fcdd";
		
		private static final String GrantType = "client_credential";		
		
		private String mAppId = null;
		private String mAppsecret = null;
		
		private long mAccessTokenValidTime = 7200; //Token 有效时间为7200s
		
		private String mAccessToken = null;
		private long mCurAccessTokenStartTime = System.currentTimeMillis();;
		
	
		public AccessToken()
		{
			mAppId = AppId;
			mAppsecret = Appsecret;
			//getNewAccessToken();		
		}
		public AccessToken(String appid,String appsecret)
		{
			mAppId = appid;
			mAppsecret = appsecret;
			//getNewAccessToken();		
		}
		public String getAccessToken()
		{
			//sent a GET request to get the new Token
			/*https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
			 *	grant_type: 获取Token 必须填写"client_credential"，即GrantType
			 *  appid：		第三用户唯一凭证，即AppId
			 *  secret：		第三方用户唯一凭证密钥，即Appsecret
			 */
			long curtime = System.currentTimeMillis();
			log.d("Current token: "+mAccessToken+", exist time: "+(curtime-mCurAccessTokenStartTime)+"ms");
			
			//如果Token超时或者第一次为空时，重新申请Token
			if(mAccessToken == null || (curtime - mCurAccessTokenStartTime) > mAccessTokenValidTime*1000){			
				getNewAccessToken();			
			}
			//TODO: 是否需要加入失败重新申请的机制
			return mAccessToken;
		}
		
		public String getNewAccessToken()
		{
			String url = "https://api.weixin.qq.com/cgi-bin/token";
			String param = "grant_type="+ GrantType + "&appid=" + mAppId + "&secret=" + mAppsecret;

			String token = HttpRequest.sendGet(url,param);
			mAccessToken = fetchTokenformJSONObject(token);
			mCurAccessTokenStartTime = System.currentTimeMillis();
			return mAccessToken;
			
		}
		private String fetchTokenformJSONObject(String json)
		{
			String access_token = null;
			String expires_in = null;
			
			System.out.println("json data: "+json);		
			JSONObject jp = JSON.parseObject(json);
			access_token = jp.getString("access_token");
			expires_in = jp.getString("expires_in");
			
			if(access_token == null || expires_in ==null){
				String errcode = jp.getString("errcode");
				String errmsg = jp.getString("errmsg");
				log.e("get token failed, errmsg: "+errmsg+", errmsg: "+ errmsg);
			}
			mAccessTokenValidTime = Integer.valueOf(expires_in).intValue();
			log.d("access_token: "+access_token);
			log.d("expires_in: "+expires_in);
			
			return access_token;
		}		
	}

	//TODO：   必须要加上网络返回值检测
	//获取设备二维码
	public String getDevice()
	{

    	return null;
		
	}
	//获取device id 对应的opend id
	public String getOpenID(String device_type,String device_id)
	{
    	String url =  "https://api.weixin.qq.com/device/get_openid";
    	String param = "access_token="+ mAccessToken.getAccessToken()+"&device_type="+device_type+"&device_id="+device_id;
    			
    	return HttpRequest.sendGet(url,param);
	}
	public ArrayList<String> getOpenIDList(String device_type,String device_id)
	{
    	String url =  "https://api.weixin.qq.com/device/get_openid";
    	String param = "access_token="+ mAccessToken.getAccessToken()+"&device_type="+device_type+"&device_id="+device_id;
    	
    	String s = HttpRequest.sendGet(url,param);
    	
    	ArrayList<String> list = new ArrayList<String>();
    	JSONObject jp = JSON.parseObject(s);
    	Integer ret_code = jp.getJSONObject("resp_msg").getInteger("ret_code");

    	if(ret_code == 0){	//成功获取   		
    		JSONArray array = jp.getJSONArray("open_id");
    		for(int i = 0; i < array.size(); i++){
    			String openid = array.get(i).toString();
    			list.add(openid);
    		}
    	}    		
    	return list;
	}
	
	//查询设备状态
	public Integer queryDeviceStatus(String device_id)
	{			
    	String url =  "https://api.weixin.qq.com/device/get_stat";
    	String param = "access_token="+ mAccessToken.getAccessToken()+"&device_id="+device_id;
    	String s =HttpRequest.sendGet(url,param);
    	JSONObject jp = JSON.parseObject(s);
		Integer status = jp.getInteger("status");
    	return status;
	}
	//获取二维码
	//重复调用会生成多个二维码，不知道各个二维码的有效性，慎用！！
	public String getQrcode(String device_id)
	{
		String url =  "https://api.weixin.qq.com/device/create_qrcode";
		String param = "access_token="+ mAccessToken.getAccessToken();
		url = url + "?" + param;
		param = "{\"device_num\":\"1\",\"device_id_list\":\"" + device_id + "\"}";
		
		String s =HttpRequest.sendPost(url,param);
		String code_list = JSON.parseObject(s).getString("code_list");
		JSONObject jp = (JSONObject)JSONArray.parseArray(code_list).get(0);
		
		return jp.getString("ticket");
	}
	//验证二维码
	public String verifyQrcode(String ticket)
	{
    	String url =  "https://api.weixin.qq.com/device/verify_qrcode";
    	String param = "access_token="+ mAccessToken.getAccessToken();		
		url = url + "?" + param;
		
		param = "{\"ticket\":\"" + ticket + "\"}";
		return HttpRequest.sendPost(url,param);
	}
	//从二维码中查询MAC地址
	public String getMac(String ticket)
	{
		String s = verifyQrcode(ticket);
		JSONObject jp = JSON.parseObject(s);
		return  jp.getString("mac");
	}
	
	public boolean syncDeviceInfo(WeChatDevice device,String device_type)
	{
		String deviceid = device.getDeviceID();
		//device.setQrcodeTicket(getQrcode(deviceid));	//获取新的二维码，是否合理？？
		device.setStatus(queryDeviceStatus(deviceid));	//获取状态
		device.setMac(getMac(device.getQrcodeTicket()));
		device.setOpenList(getOpenIDList(device_type, deviceid));
		device.setSynchronize(true);
		return true;
	}
}

