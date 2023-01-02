package com.groupunix.drivewireserver.virtualprinter;

import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
import java.io.IOException;

public interface DWVPrinterDriver {
  void flush() throws IOException, DWPrinterFileError, DWPrinterNotDefinedException;
  
  void addByte(byte paramByte) throws IOException;
  
  String getDriverName();
  
  String getPrinterName();
}


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinterDriver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */