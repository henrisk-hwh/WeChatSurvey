<%@page import="java.io.FileInputStream"%>
<%@ page language="java" import="java.util.*" 
                         import="java.security.*"
                         import="java.io.*" 
                         import="java.io.UnsupportedEncodingException" 
						 import="com.softwinner.WeChatSurvey.WeChatHandler" 
pageEncoding="UTF-8"%>
<%

class Sign {

    public  Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        System.out.println(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private  String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private  String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private  String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    public String getJsApiTicket(){
    	String path = "/tmp/JsApiTicket.wechat";
    	//File file = new File(path);
    	try{
	    	FileInputStream fis = new FileInputStream(path);
	    	int length = fis.available();
	    	byte[] bytes = new byte[length];
	    	fis.read(bytes,0,length);
	    	fis.close();
	    	return new String(bytes,"UTF-8");
    	}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
}

String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/device.jsp";
String basePath = request.getScheme()+"://"+request.getServerName()+path+"/device.jsp";
String url = "http://example.com";
Sign mysign = new Sign();

String jsapi_ticket = mysign.getJsApiTicket();
Map<String, String> ret = mysign.sign(jsapi_ticket, basePath);

for (Map.Entry entry : ret.entrySet()) {
          System.out.println(entry.getKey() + ", " + entry.getValue());
}



String timestamp = ret.get("timestamp");
String nonceStr = ret.get("nonceStr"); 
String signature = ret.get("signature"); 
String appId = "wxed078c433980b3f6";
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
	<meta charset="utf-8" />
	<meta name="viewport" content="initial-scale=1, width=device-width, maximum-scale=1, user-scalable=no"  />
	<meta name="viewport" content="initial-scale=1.0,user-scalable=no,maximum-scale=1" media="(device-height: 568px)" />
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name='apple-touch-fullscreen' content='yes'>
	<meta name="full-screen" content="yes">
	<title>硬件JSAPI测试</title>
	<link rel="stylesheet" type="text/css" href="device.css">

<style>
		*{margin:0;padding:0;}
		ul li{list-style:none;}
		html,body{height:100%;}
		img{border:none;}
		body{font-family:"微软雅黑";}
		.dendai_body{width:100%;height:100%;text-align:center;}
		.dedai_img{width:100%;padding-top:16%;padding-bottom:5%;}
		.dedai_img img{width:46%;height:auto;border-radius:6px;}
		.jiazai{width:100%;font-size:29px;color:#a6a6a6;}
		.dedai_body{width:100%;text-align:center;color:#a6a6a6;font-size:28px;font-weight:bold;margin-top:10%;}
		.text2_dedai{font-size:28px;margin-top:6px;}
		.dedai_foot{width:100%;margin-top:16%;}
		.Liwszhi_but{display:inline-block;width:76%;height:80px;line-height:80px;color:#fff;font-size:30px;font-weight:bold;border-radius:3px;background-color:#07be04;}
	
		
		.body_lianwan{width:100%;height:100%;background-color:#fff;position:fixed;top:0px;}
		/*联网*/
		.int_header{width:100%;height:300px;text-align:center;padding-top:12%;}
		.int_wang_luo{width:100%;margin-top:20px;font-size:29px;}
		.int_lianwanbutton{width:100%;height:300px;margin-top:5%;}
		.set_button{width:95%;margin:0 auto;height:100px;margin-top:36px;}
		.set_button a{display:block;width:100%;height:100%;text-align:center;line-height:100px;font-size:30px;}
		.set_button:first-child a{background:url(./images/erwem_img.png) no-repeat 30px;color:#000;border-radius:5px;border:1px solid #cfcfcf;}
		.set_button:last-child a{background-color:#09b700;border-radius:5px;color:#fff;}
		a {text-decoration:none;}
		/*联网*/
</style>

</head>

<body>
	<article class="body_lianwan" style="display:none;" >
		<article class="int_header">
			<section><img src="./images/duil_ico.png" /></section>
			<section class="int_wang_luo">网络连接成功</section>
		</article>
		<article class="int_lianwanbutton">
			<section class="set_button"><a href="javascript:scanQrcode();">扫描设备</a></section>
			<script src="http://libs.baidu.com/jquery/1.8.2/jquery.min.js" ></script>
		</article>
	</article>
	
	<div id="debug"></div>
	<div id="buttons">
		<div>
			<a href="javascript:void(0)" class="button" id="openWXDeviceLib">API初始化</a>
			<a href="javascript:void(0)" class="button" id="closeWXDeviceLib">API释放</a>
		</div>
		<div>
			<a href="javascript:void(0)" class="button" id="getWXDeviceInfos">设备状态</a>
			<a href="javascript:void(0)" class="button" id="getWXDeviceBindTicket">绑定票据</a>
			<a href="javascript:void(0)" class="button" id="getWXDeviceUnbindTicket">解绑票据</a>
			<a href="javascript:void(0)" class="button" id="setSendDataDirection">设置SendData目标</a>
		</div>
		<div>
			<a href="javascript:void(0)" class="button" id="startScanWXDevice">开始扫描</a>
			<a href="javascript:void(0)" class="button" id="stopScanWXDevice">停止扫描</a>
		</div>
		<div>
			<a href="javascript:void(0)" class="button" id="connectWXDevice">连接设备</a>
			<a href="javascript:void(0)" class="button" id="disconnectWXDevice">断开设备</a>
			<a href="javascript:void(0)" class="button" id="sendDataToWXDevice">发送数据</a>
		</div>
		<div>
			<a href="javascript:void(0)" class="button" id="startAirkiss">Airkiss</a>
		</div>
	</div>
	<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>

	<script src="debug.js" type="text/javascript"></script>
	<script>
	  /*
	   * 注意：
	   * 1. 所有的JS接口只能在公众号绑定的域名下调用，公众号开发者需要先登录微信公众平台进入“公众号设置”的“功能设置”里填写“JS接口安全域名”。
	   * 2. 如果发现在 Android 不能分享自定义内容，请到官网下载最新的包覆盖安装，Android 自定义分享接口需升级至 6.0.2.58 版本及以上。
	   * 3. 完整 JS-SDK 文档地址：http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html
	   *
	   * 如有问题请通过以下渠道反馈：
	   * 邮箱地址：weixin-open@qq.com
	   * 邮件主题：【微信JS-SDK反馈】具体问题
	   * 邮件内容说明：用简明的语言描述问题所在，并交代清楚遇到该问题的场景，可附上截屏图片，微信团队会尽快处理你的反馈。

	   */
  wx.config({
      debug: true,
      beta: true,
      appId: '<%=appId%>',
      timestamp: <%=timestamp%>,
      nonceStr: '<%=nonceStr%>',
      signature: '<%=signature%>',
      jsApiList: [
        'checkJsApi',
        'getNetworkType',
        'scanQRCode',
        'configWXDeviceWiFi',
        'openWXDeviceLib',
        'closeWXDeviceLib',
        'getWXDeviceInfos',       
        
      ]
  });
	
	 wx.ready(function (){
	  //alert("OK");	  
	 });
	 
	 wx.error(function (res){
	  alert(ret.errMsg);
	 });
	
	 
	 var CANCEL = "configWXDeviceWiFi:cancel";
	 var FAIL = "configWXDeviceWiFi:fail";
	 var WIFI_OK = "configWXDeviceWiFi:ok";
  
	function scanQrcode(){
		 wx.scanQRCode({
		      desc: '扫描二维码添加插座设备'
		 });
	}
	var deviceId = "0123";

	var readyFunc = function onBridgeReady() {
		document.querySelector('#startAirkiss').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('configWXDeviceWiFi',{},function(res){
				
				if(res && ('err_msg' in res) ){
				  	if( res['err_msg'] == CANCEL )
				  		alert("微信联网失败2，请重新联网！");	
				  	else if( res['err_msg'] == FAIL )
				  		alert("微信联网失败，请重新联网！");
				  	else if( res['err_msg'] ==  WIFI_OK)
						try{  
							$(".body_lianwan").show();	
						} catch(e){
							alert("微信联网成功，请返回！");
						}
				 }			

			});
		});
		
		document.querySelector('#openWXDeviceLib').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('openWXDeviceLib', {}, function(res){
				alert("open");
				console.log('openWXDeviceLib22', res);
			});
		});
		document.querySelector('#closeWXDeviceLib').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('closeWXDeviceLib', {}, function(res){
				console.log('closeWXDeviceLib', res);
			});
		});

		document.querySelector('#getWXDeviceInfos').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('getWXDeviceInfos', {}, function(res){
				console.log('getWXDeviceInfos', res);
			});
		});
		document.querySelector('#getWXDeviceBindTicket').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('getWXDeviceTicket', {"deviceId":deviceId, "type":"1"}, function(res){
				console.log('getWXDeviceTicket', res);
			});
		});
		document.querySelector('#getWXDeviceUnbindTicket').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('getWXDeviceTicket', {"deviceId":deviceId, "type":"2"}, function(res){
				console.log('getWXDeviceTicket', res);
			});
		});
		document.querySelector('#setSendDataDirection').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('setSendDataDirection', {"deviceId":deviceId, "direction":1 }, function(res){
				console.log('setSendDataDirection', res);
			});
		});

		document.querySelector('#startScanWXDevice').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('startScanWXDevice', {}, function(res){
				console.log('startScanWXDevice', res);
			});
		});
		document.querySelector('#stopScanWXDevice').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('stopScanWXDevice', {}, function(res){
				console.log('stopScanWXDevice', res);
			});
		});

		document.querySelector('#connectWXDevice').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('connectWXDevice', {"deviceId":deviceId}, function(res){
				console.log('connectWXDevice', res);
			});
		});
		document.querySelector('#disconnectWXDevice').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('disconnectWXDevice', {"deviceId":deviceId}, function(res){
				console.log('disconnectWXDevice', res);
			});
		});
		document.querySelector('#sendDataToWXDevice').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('sendDataToWXDevice', {"deviceId":deviceId, "base64Data":"Ejl="}, function(res){
				console.log('sendDataToWXDevice', res);
			});
		});

		WeixinJSBridge.on('onWXDeviceBindStateChange', function(argv) {
			console.log('onWXDeviceBindStateChange', argv);
		});
		WeixinJSBridge.on('onWXDeviceStateChange', function(argv) {
			console.log('onWXDeviceStateChange', argv);
		});
		WeixinJSBridge.on('onScanWXDeviceResult', function(argv) {
			console.log('onScanWXDeviceResult', argv);
		});
		WeixinJSBridge.on('onReceiveDataFromWXDevice', function(argv) {
			console.log('onReceiveDataFromWXDevice', argv);
		});
		WeixinJSBridge.on('onWXDeviceBluetoothStateChange', function(argv) {
			console.log('onWXDeviceBluetoothStateChange', argv);
		});
	};

	if (typeof WeixinJSBridge === "undefined") {
		document.addEventListener('WeixinJSBridgeReady', readyFunc, false);
	} else {
		readyFunc();
	}

	</script>
</body>
</html>