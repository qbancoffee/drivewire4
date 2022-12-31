package com.groupunix.drivewireserver.dwexceptions;

public class DWConnectionNotValidException  extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWConnectionNotValidException(String msg)
	{
		super(msg);
	}
}
