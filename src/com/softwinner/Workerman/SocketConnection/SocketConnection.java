package com.softwinner.Workerman.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.softwinner.Workerman.SocketApi.ConnectApi;
import com.softwinner.log.log;

public class SocketConnection implements SocketReadThread.ReadThreadListener{

	public static String SocketServerIP = "127.0.0.1";
	public static int ScoketPort = 2345;
	private Socket mServerSocket = null;

	private SocketReadThread mReadThread = null;
	private SocketWriteThread mWriteThread = null;
	private boolean mConnectStatus = false;
	private SocketConnect mConnect = new SocketConnect(SocketServerIP,ScoketPort);
	private Timer mTimer = null;
	public interface ConnectionListener{
		public void onPushIamge(String path,String device_id);
		
	}
	private ConnectionListener mListener;
	public void setListener(ConnectionListener listener){
		mListener = listener;
	}
	
	
	public boolean init(){
		if(initSocket()){
			initThread();
			return true;
		}
		return false;
	}
	public boolean initSocket(){
		return mConnect.openSocket();
	}
	public void initThread(){
		mConnectStatus = true;
		mWriteThread = new SocketWriteThread(mConnect);
		mReadThread = new SocketReadThread(mConnect);
		mReadThread.setListener(this);		
	}
	public boolean reinit(){
		finishAll();
		if(init()){
			startAll();
			return true;
		}
		return false;
	}
	public void destroy() {
		finishAll();
	}
	public void startAll(){
		if(mConnectStatus && mWriteThread != null && mReadThread != null){
			mWriteThread.start();
			mReadThread.start();
		}
	}
	public void finishAll(){
		if(mWriteThread != null && mReadThread != null){
			mWriteThread.finish();
			mReadThread.finish();
		}
	}

	public void testWrite(String s){
		pushDatatoAll(s);
	}

	public void pushDatatoAll(String data){
		Data senddata = new Data();
		senddata.string = ConnectApi.getWebServerPushMsgString(data,ConnectApi.MSG_PUSH_ALL,(String)null);
		mWriteThread.addItem(senddata);
	}

	public void pushDatatoOne(String data,String device_id){
		Data senddata = new Data();
		senddata.string = ConnectApi.getWebServerPushMsgString(data,ConnectApi.MSG_PUSH_DEVICE,device_id);
		mWriteThread.addItem(senddata);
	}
	public void pushDatatoGroup(String data,ArrayList<String> device_id_list){
		Data senddata = new Data();
		senddata.string = ConnectApi.getWebServerPushMsgString(data,ConnectApi.MSG_PUSH_DEVICE,device_id_list);
		mWriteThread.addItem(senddata);
	}
	
	@Override
	public void onRead(String msg) {
		// TODO Auto-generated method stub		

		log.d("onRead: "+msg);
		JSONObject jp = JSON.parseObject(msg);
		
		int type = jp.getIntValue("type");
		if(type == ConnectApi.TYPE_CONNCET){
			Data data = new Data();
			data.string = ConnectApi.getWebServerAuthRespString();
			mWriteThread.addItem(data);			
		}
		
		if(type == ConnectApi.TYPE_UPDATE){			
			if((ConnectApi.MSG_UPDATE_IMAGE == jp.getIntValue("msg"))){
				String image_path = jp.getString("path");
				String device_id = jp.getString("id");
				if(device_id != null && image_path!= null && mListener != null)
					mListener.onPushIamge(image_path,device_id);
			}
		}
		
		//测试分支
		if(type == 100){//test
			testdevicenum++;
		}
	}
	private int testdevicenum = 0;
	public int getTestDeviceNum(){
		return testdevicenum;
	}
	public void clearTestDeivceNum(){
		testdevicenum = 0;
	}
	//读线程异常时回调
	@Override
	public void onReadThreadBreaked() {
		// TODO Auto-generated method stub
		mTimer = new Timer();
		keepConnectTimer();
	}

	//检测是否断线以及掉线重新连接
	private void keepConnectTimer() {    	
    	mTimer.schedule(new TimerTask() {  
            public void run() {
            	log.d("-------reinit the socket and thread--------");
            	if(reinit()){
            		if(mConnect.getConnectStatus())
            			log.d("-------stop timer--------");
            			mTimer.cancel();
            	}

            }  
        }, 1000, 5000); 
    }

}
