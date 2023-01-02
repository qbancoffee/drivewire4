package com.groupunix.drivewireserver.dwprotocolhandler;

import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
import java.io.IOException;

public interface DWProtocolDevice {
  boolean connected();
  
  void close();
  
  void shutdown();
  
  void comWrite(byte[] paramArrayOfbyte, int paramInt, boolean paramBoolean);
  
  void comWrite1(int paramInt, boolean paramBoolean);
  
  byte[] comRead(int paramInt) throws IOException, DWCommTimeOutException;
  
  int comRead1(boolean paramBoolean) throws IOException, DWCommTimeOutException;
  
  int getRate();
  
  String getDeviceType();
  
  String getDeviceName();
}


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWProtocolDevice.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */