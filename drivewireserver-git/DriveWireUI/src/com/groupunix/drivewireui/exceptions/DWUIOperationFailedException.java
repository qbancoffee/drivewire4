package com.groupunix.drivewireui.exceptions;


public class DWUIOperationFailedException extends Exception 
{

	private static final long serialVersionUID = 1L;

	public DWUIOperationFailedException(String msg)
	{
		super(msg);
	}

	public DWUIOperationFailedException(byte err)
	{
		//super(MainWin.errorHelpCache.getErrMessage((int)(0xFF & err)));
		super("DW Error # " + (int)(0xFF & err));
	}

	public DWUIOperationFailedException(byte err, String msg)
	{
		//super(MainWin.errorHelpCache.getErrMessage((int)(0xFF & err)) + ": " + msg);
		super("DW Error # " + (int)(0xFF & err) + ": " + msg);
	}
}
