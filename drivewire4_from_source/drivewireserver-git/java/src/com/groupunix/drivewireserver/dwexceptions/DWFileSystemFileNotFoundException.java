package com.groupunix.drivewireserver.dwexceptions;

public class DWFileSystemFileNotFoundException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWFileSystemFileNotFoundException(String msg)
	{
		super(msg);
	}
}
