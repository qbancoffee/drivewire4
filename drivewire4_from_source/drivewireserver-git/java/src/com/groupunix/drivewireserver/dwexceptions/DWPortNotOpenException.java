package com.groupunix.drivewireserver.dwexceptions;

public class DWPortNotOpenException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWPortNotOpenException(String msg)
	{
		super(msg);
	}
	
}
