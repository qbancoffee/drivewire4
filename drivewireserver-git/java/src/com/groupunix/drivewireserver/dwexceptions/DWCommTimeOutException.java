package com.groupunix.drivewireserver.dwexceptions;

public class DWCommTimeOutException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWCommTimeOutException(String msg)
	{
		super(msg);
	}
}
