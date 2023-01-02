package com.groupunix.drivewireui.exceptions;


public class DWUINoSuchTaskException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWUINoSuchTaskException(String msg)
	{
		super(msg);
	}

	public DWUINoSuchTaskException(byte err)
	{
		// super(MainWin.errorHelpCache.getErrMessage((int)(0xFF & err)));
		super("DW Error # " + (int)(0xFF & err));
	}

	public DWUINoSuchTaskException(byte err, String msg)
	{
		// super(MainWin.errorHelpCache.getErrMessage((int)(0xFF & err)) + ": " + msg);
		super("DW Error # " + (int)(0xFF & err) + ": " + msg);
	}
}
