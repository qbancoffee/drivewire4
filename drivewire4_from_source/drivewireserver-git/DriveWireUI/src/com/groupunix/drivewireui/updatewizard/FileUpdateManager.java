package com.groupunix.drivewireui.updatewizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.groupunix.drivewireui.UIUtils;

public class FileUpdateManager implements Runnable {

	private static final int MAX_UPDATE_TASKS = 4;
	private HashMap<String, String> files;
	private UpdatePage updatePage;

	public FileUpdateManager(UpdatePage updatePage, HashMap<String, String> files) 
	{
		this.files = files;
		this.updatePage = updatePage;
	}

	@Override
	public void run() 
	{
		
		
		ExecutorService threadPool = Executors.newFixedThreadPool(MAX_UPDATE_TASKS);
		
		for (String fn : files.keySet())
		{
			boolean download = false;
			
			try 
			{
				File f = new File(fn);
				
				String cursha = UIUtils.getSHA1(new FileInputStream(f));
					
				if (!cursha.equalsIgnoreCase( files.get(fn)))
				{
					download = true;
				}
					
			} 
			
			catch (FileNotFoundException e) 
			{
				
				download = true;
			} 
			
			
			if (download)
				threadPool.submit(new FileUpdater(fn, files.get(fn), this.updatePage));
		}
	
		threadPool.shutdown();
		
		try
		{
			while (!threadPool.isTerminated())
			{
				Thread.sleep(100);
				updatePage.updateProgressIcons();
			}
				
			//threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} 
		catch (InterruptedException e) 
		{
			
		}
		
		this.updatePage.finishedUpdate();
		
	}

}
