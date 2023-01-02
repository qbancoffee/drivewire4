package com.groupunix.drivewireserver.dwexceptions;

public class DWFileSystemFullException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWFileSystemFullException(String msg)
	{
		super(msg);
	}
}
