package com.groupunix.drivewireserver.dwexceptions;

public class DWDriveAlreadyLoadedException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public DWDriveAlreadyLoadedException(String msg)
	{
		super(msg);
	}
}
