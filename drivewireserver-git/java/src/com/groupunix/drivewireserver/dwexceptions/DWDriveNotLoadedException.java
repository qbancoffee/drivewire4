package com.groupunix.drivewireserver.dwexceptions;

public class DWDriveNotLoadedException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public DWDriveNotLoadedException(String msg)
	{
		super(msg);
	}
}