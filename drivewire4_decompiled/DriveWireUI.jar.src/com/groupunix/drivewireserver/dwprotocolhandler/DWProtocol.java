package com.groupunix.drivewireserver.dwprotocolhandler;

import com.groupunix.drivewireserver.dwhelp.DWHelp;
import java.util.GregorianCalendar;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

public interface DWProtocol extends Runnable {
  void shutdown();
  
  boolean isDying();
  
  HierarchicalConfiguration getConfig();
  
  DWProtocolDevice getProtoDev();
  
  GregorianCalendar getInitTime();
  
  String getStatusText();
  
  void resetProtocolDevice();
  
  void syncStorage();
  
  int getHandlerNo();
  
  Logger getLogger();
  
  int getCMDCols();
  
  DWHelp getHelp();
  
  boolean isReady();
  
  void submitConfigEvent(String paramString1, String paramString2);
  
  long getNumOps();
  
  long getNumDiskOps();
  
  long getNumVSerialOps();
}


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWProtocol.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */