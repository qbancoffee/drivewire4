package com.groupunix.drivewireserver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class DWExceptionHandler implements UncaughtExceptionHandler
{


	public void uncaughtException(Thread thread, Throwable thrw)
	{
		handleUncaughtException(thread, thrw);
	}

	
	public void handleUncaughtException(Thread thread, Throwable thrw)
	{
		String msg = "Exception in thread " + thread.getName();
		
		if (thrw.getClass().getSimpleName() != null)
			msg += ": " + thrw.getClass().getSimpleName();
		
		if (thrw.getMessage() != null)
			msg += ": " + thrw.getMessage();
		
		if (DriveWireServer.logger != null)
		{
			DriveWireServer.logger.error(msg);
			DriveWireServer.logger.info(getStackTrace(thrw));
		}
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println(msg);
		System.out.println("--------------------------------------------------------------------------------");
		thrw.printStackTrace();
	}

	
	public String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
	
}
