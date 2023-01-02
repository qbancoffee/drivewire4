package com.groupunix.drivewireserver;

public class DWShutdownHandler extends Thread {
	
	public void run() 
	{
		Thread.currentThread().setName("shutdown-" + Thread.currentThread().getId());
		// call shutdown from separate thread
        DriveWireServer.serverShutdown();
    }


}
