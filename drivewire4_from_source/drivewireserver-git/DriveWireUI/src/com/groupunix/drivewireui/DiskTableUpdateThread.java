package com.groupunix.drivewireui;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.graphics.Image;

public class DiskTableUpdateThread implements Runnable
{
	private LinkedBlockingQueue<DiskTableUpdate> updates = new LinkedBlockingQueue<DiskTableUpdate>();
	private Hashtable<Integer, Hashtable<String, Object>> colval = new Hashtable<Integer, Hashtable<String, Object>>();
	
	@Override
	public void run()
	{
		Thread.currentThread().setName("dwuiDTUpdater-" + Thread.currentThread().getId());
		
		
		DiskTableUpdate dtu;
		
		while (!MainWin.shell.isDisposed())
		{
			try
			{
				// get update
				dtu = this.updates.take();
				
				// apply to local cache
				if (!this.colval.containsKey(dtu.getDisk()))
					this.colval.put(dtu.getDisk(), new Hashtable<String, Object>());
					
				this.colval.get(dtu.getDisk()).put(dtu.getKey(), dtu.getValue());
				
				// no more updates, apply to table 
				if (this.updates.size() == 0)
				{
					for (Entry<Integer, Hashtable<String, Object>> diskentry :this.colval.entrySet())
					{
						for (Entry<String, Object> param : diskentry.getValue().entrySet())
						{
							// synced call
							MainWin.updateDiskTableItem(diskentry.getKey(), param.getKey(), param.getValue());
							
						}
						
						diskentry.getValue().clear();
					}
					
					MainWin.updateDiskTabs();
					
					// clear cache
					colval.clear();
					
				}
				
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		System.out.println("dtu die");
	}

	public void addUpdate(int disk, String key, String val)
	{
		this.updates.add(new DiskTableUpdate(disk, key, val));
	}


	public void addUpdate(int disk, String key, Image val)
	{
		this.updates.add(new DiskTableUpdate(disk, key, val));
	}


}
