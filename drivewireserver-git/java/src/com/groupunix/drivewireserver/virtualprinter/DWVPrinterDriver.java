package com.groupunix.drivewireserver.virtualprinter;

import java.io.IOException;

import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;

public interface DWVPrinterDriver 
{
	public void flush() throws IOException, DWPrinterFileError, DWPrinterNotDefinedException;
	public void addByte(byte data) throws IOException;
	public String getDriverName();
	public String getPrinterName();
}
