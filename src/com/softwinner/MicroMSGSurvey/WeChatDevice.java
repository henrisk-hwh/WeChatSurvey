package com.softwinner.MicroMSGSurvey;

import java.util.ArrayList;


public class WeChatDevice {
	
	public static final String SUBSCRIBE_STATUS = "subscribe_status";
	public static final String UNSUBSCRIBE_STATUS = "unsubscribe_status";
	
	public static final String EVENT_BIND = "bind";
	public static final String EVENT_UNBIND = "unbind";
	
	public static final int STATUS_BIND = 2;
	public static final int STATUS_AUTHORIZED = 1;
	public static final int STATUS_UNAUTHORIZE = 0;
	public static final String STATUS[] = {"unauthorize","authorized","bind"};
	private int mStatus = STATUS_UNAUTHORIZE;
	
	private boolean mSynchronized	= false;	//是否需要从服务器同步数据标志
	
	private String mDeviceID		= null;		//设备id	
	private String mDeviceType		= null;		//设备type，为公众号id	
	private String mMac				= null;		//设备MAC地址
	private String mQrcodeTicket	= null;		//设备二维码ticket	

	//openid 列表，device绑定者id列表
	private ArrayList<String> mOpenIDList = new ArrayList<String>();
	
	public WeChatDevice(String device_id)
	{
		mDeviceID = device_id;
	}
	public WeChatDevice(String device_id,String device_type)
	{
		mDeviceID = device_id;
		mDeviceType = device_type;
	}
	public void dump(String s)
	{
		log.d(s+" DeviceID:     " + mDeviceID);
		log.d(s+" ---> Synchronized: " + mSynchronized);
		log.d(s+" ---> DeviceType:   " + mDeviceType);
		log.d(s+" ---> Mac:          " + mMac);
		log.d(s+" ---> QrcodeTicket: " + mQrcodeTicket);
		log.d(s+" ---> Status:       " + mStatus);
		log.d(s+" ---> OpenIDList:   " + mOpenIDList.toString());		
	}
	public void setSynchronize(boolean sync)
	{
		mSynchronized = sync;		
	}	

	public boolean getSynchronize()
	{
		return mSynchronized;		
	}
	public void setDeviceID(String device_id)
	{
		mDeviceID = device_id;		
	}	

	public String getDeviceID()
	{
		return mDeviceID;		
	}
	public void setDeviceType(String device_type)
	{
		mDeviceType = device_type;
	}
	public String getDeviceType()
	{
		return mDeviceType;
	}
	public void setMac(String mac)
	{
		mMac = mac;
	}
	public String getMac()
	{
		return mMac;
	}
	public void setQrcodeTicket(String ticket)
	{
		mQrcodeTicket = ticket;
	}
	public String getQrcodeTicket()
	{
		return mQrcodeTicket;
	}
	public void setStatus(int status)
	{
		mStatus = status;
	}
	public int getStatus()
	{
		return mStatus;
	}
	public void setOpenList(ArrayList<String> list)
	{
		mOpenIDList = list;
	}
	
	public ArrayList<String> getOpenIDList()
	{
		return mOpenIDList;
	}
	public boolean isOpenID(String open_id)
	{
		for(int i = 0; i < mOpenIDList.size(); i++){
			log.d(mOpenIDList.get(i));
			if(mOpenIDList.get(i).equals(open_id))
				return true;
		}
		return false;
	}
	public boolean addOpenID(String open_id)
	{
		return mOpenIDList.add(open_id);
	}
	
	public boolean removeOpenID(String open_id)
	{
		for(int i = 0; i < mOpenIDList.size(); i++){
			if(open_id.equals((mOpenIDList.get(i)))){
				mOpenIDList.remove(i);
				return true;
			}
		}
		return false;
	}
}
