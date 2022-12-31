package com.groupunix.drivewireserver.dwexceptions;


public class DWSeekPastEndOfDeviceException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public DWSeekPastEndOfDeviceException(String msg)
	{
		super(msg);
	}
}