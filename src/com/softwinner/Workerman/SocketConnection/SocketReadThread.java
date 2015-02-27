package com.softwinner.Workerman.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.softwinner.log.log;

public class SocketReadThread extends ThreadBase {
	private Socket mSocketServer = null;
	private SocketConnect mConnect = null;
	private ReadThreadListener mListener = null;
	public SocketReadThread(SocketConnect connect){
		mConnect = connect;
		mSocketServer = mConnect.getSocketServer();		
	}
	public interface ReadThreadListener{
		public void onRead(String msg);
		public void onReadThreadBreaked();
	}
	public void setListener(ReadThreadListener listener){
		mListener = listener;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
			String msg;
			log.d("start read thread!");
			while(true){
				if(stop){
					log.d("stop read thread!");
					break;
				}

				if((msg = br.readLine()) != null){
					log.d(msg);
					if(mListener != null)
						mListener.onRead(msg);
				}else{
					log.e("read thread read null!!");
					if(mListener != null) 
						mListener.onReadThreadBreaked();
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.e("read thread break!!");
			if(mListener != null) 
				mListener.onReadThreadBreaked();
		}
	}
	
	
}
