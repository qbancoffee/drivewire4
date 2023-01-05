package com.groupunix.drivewireserver;

import java.lang.Thread.UncaughtExceptionHandler;

public class DWExceptionHandler implements UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(Thread thread, Throwable thrw)
	{
		DriveWireServer.handleUncaughtException(thread, thrw);
	}

}
