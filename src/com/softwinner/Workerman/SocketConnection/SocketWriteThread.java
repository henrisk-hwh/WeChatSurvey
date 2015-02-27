package com.softwinner.Workerman.SocketConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.softwinner.log.log;

public class SocketWriteThread extends ThreadBase {
	private Socket mSocketServer;
	private ArrayList<Data> mQueue = new ArrayList<Data>();
	private static final int QUEUE_LIMIT = 100;
	private SocketConnect mConnect = null;
	public SocketWriteThread(SocketConnect connect){
		mConnect = connect;
		mSocketServer = mConnect.getSocketServer();
	}
	
	public void addItem(Data data){
		log.d("add item: "+data.string);
		synchronized (this) {
			while(mQueue.size() >= QUEUE_LIMIT){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mQueue.add(data);
			notifyAll();
		}
	}
	public void run(){
		log.d("start write thread");
		while(true){
			Data data;
			synchronized (this) {
				if(mQueue.isEmpty()){
					notifyAll();
					if(stop){
						log.d("stop write thread!");
						break;
					}
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				data = mQueue.get(0);
			}
			if(mConnect.getConnectStatus())
				sendData(data);
			synchronized (this) {
				mQueue.remove(0);
				notifyAll();
			}
		}
	}
	private void sendData(Data data){
		try {
			OutputStream scoketout = mSocketServer.getOutputStream();
			scoketout.write((data.string+'\n').getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
