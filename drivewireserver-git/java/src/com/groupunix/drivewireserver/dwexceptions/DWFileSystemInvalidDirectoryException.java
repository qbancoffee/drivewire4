package com.groupunix.drivewireserver.dwexceptions;

public class DWFileSystemInvalidDirectoryException extends Exception
{

	private static final long serialVersionUID = 1L;

	public DWFileSystemInvalidDirectoryException(String msg)
	{
		super(msg);
	}
}
