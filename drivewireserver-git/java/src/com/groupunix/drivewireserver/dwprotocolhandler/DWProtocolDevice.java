package com.groupunix.drivewireserver.dwprotocolhandler;

import java.io.IOException;
import java.io.InputStream;

import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;



public interface DWProtocolDevice 
{

	public boolean connected();
	public void close();
	public void shutdown();
	public void comWrite(byte[] data, int len, boolean prefix);
	public void comWrite1(int data, boolean prefix);
	public byte[] comRead(int len) throws IOException, DWCommTimeOutException; 
	public int comRead1(boolean timeout) throws IOException, DWCommTimeOutException; 
	public int getRate();
	public String getDeviceType();
	public String getDeviceName();
	public String getClient();
	public InputStream getInputStream();

}
