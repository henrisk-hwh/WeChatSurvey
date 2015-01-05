<%@ page language="java" pageEncoding="utf-8"%>
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
</head>
<body>
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
			<a href="javascript:void(0)" class="button" id="hiden">隐藏</a>

		</div>
	</div>

	<script src="debug.js" type="text/javascript"></script>
	<script>

	var deviceId = "6789";

	var readyFunc = function onBridgeReady() {
		document.querySelector('#hiden').addEventListener('touchstart', function(e){
			WeixinJSBridge.call('hideOptionMenu');
			console.log('hiden',"1");
		});
		
		document.querySelector('#openWXDeviceLib').addEventListener('touchstart', function(e){
			WeixinJSBridge.invoke('openWXDeviceLib', {}, function(res){
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