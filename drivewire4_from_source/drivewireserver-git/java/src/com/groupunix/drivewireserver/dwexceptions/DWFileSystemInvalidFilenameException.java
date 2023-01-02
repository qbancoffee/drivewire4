package com.groupunix.drivewireserver.dwexceptions;

public class DWFileSystemInvalidFilenameException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWFileSystemInvalidFilenameException(String msg)
	{
		super(msg);
	}
}
