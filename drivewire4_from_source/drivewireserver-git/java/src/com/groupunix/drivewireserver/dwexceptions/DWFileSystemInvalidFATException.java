package com.groupunix.drivewireserver.dwexceptions;

public class DWFileSystemInvalidFATException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWFileSystemInvalidFATException(String msg)
	{
		super(msg);
	}
}
