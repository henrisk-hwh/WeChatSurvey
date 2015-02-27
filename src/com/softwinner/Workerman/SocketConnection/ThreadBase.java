package com.softwinner.Workerman.SocketConnection;

public class ThreadBase extends Thread {
	public boolean stop = false;
	public synchronized void start(){
		super.start();
	}
	public void finish(){
		synchronized (this) {
			stop = true;
			notifyAll();
		}
	}
}
