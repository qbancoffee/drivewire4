package com.groupunix.drivewireserver.dwexceptions;



public class DWInvalidSectorException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public DWInvalidSectorException(String msg)
	{
		super(msg);
	}
}