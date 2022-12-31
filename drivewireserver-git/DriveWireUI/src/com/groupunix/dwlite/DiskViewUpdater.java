package com.groupunix.dwlite;

public class DiskViewUpdater implements Runnable {

	
	
	private static final long UPDATE_DELAY = 200;
	private DWLite window;
	private boolean wanttodie = false;
	
	public DiskViewUpdater(DWLite window) 
	{
		this.window = window;
	}

	
	public void run() 
	{
		while (!wanttodie)
		{
			try 
			{
				Thread.sleep(UPDATE_DELAY);
			} 
			catch (InterruptedException e) 
			{
				wanttodie = true;
			}
			
			window.updateDriveDisplay();
			
		}
	}

}
