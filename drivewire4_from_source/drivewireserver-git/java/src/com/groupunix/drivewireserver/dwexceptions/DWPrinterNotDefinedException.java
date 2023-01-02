package com.groupunix.drivewireserver.dwexceptions;

public class DWPrinterNotDefinedException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWPrinterNotDefinedException(String msg)
	{
		super(msg);
	}
}
