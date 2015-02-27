package com.softwinner.Workerman.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class SocketConnect {
	private String mSocketServerIP = "127.0.0.1";
	private int mScoketPort = 6666;
	private Socket mServerSocket = null;
	private boolean mConnectStatus = false;

	public SocketConnect(){
		
	}
	public SocketConnect(String ipaddr,int port){		
		mSocketServerIP = ipaddr;
		mScoketPort = port;
	}
	public boolean openSocket(){
		mServerSocket = new Socket();
		SocketAddress socketaddress = new InetSocketAddress(mSocketServerIP,mScoketPort);
		try {
			mServerSocket.connect(socketaddress,1000);
			mServerSocket.setKeepAlive(true);
			//mConnectStatus = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	public boolean closeSocket(){
		try {
			mServerSocket.close();
			//mConnectStatus = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean isSocketServerConnected(){
		try {
			mServerSocket.sendUrgentData(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	public Socket getSocketServer(){
		return mServerSocket;
	}
	public boolean getConnectStatus(){
		mConnectStatus = isSocketServerConnected();
		return mConnectStatus;
	}
	
}
