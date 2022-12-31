package com.groupunix.drivewireserver.dwexceptions;

public class DWDriveWriteProtectedException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWDriveWriteProtectedException(String msg)
	{
		super(msg);
	}
}
