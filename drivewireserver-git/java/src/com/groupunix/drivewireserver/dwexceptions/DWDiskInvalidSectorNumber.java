package com.groupunix.drivewireserver.dwexceptions;

public class DWDiskInvalidSectorNumber extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWDiskInvalidSectorNumber(String msg)
	{
		super(msg);
	}
}
