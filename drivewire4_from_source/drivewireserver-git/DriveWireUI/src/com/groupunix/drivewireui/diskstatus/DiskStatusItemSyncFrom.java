package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public class DiskStatusItemSyncFrom extends DiskStatusItem
{


	
	public DiskStatusItemSyncFrom(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(135);
		this.setY(84);
		this.setWidth(60);
		this.setHeight(32);
		
		this.setDisabledImage( "/disk/disk_sync_from_disabled.png");
		this.setEnabledImage( "/disk/disk_sync_from_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("sync-from hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded())
		{
			this.setButton(diskDef.hasParam("syncfrom"));
			this.setEnabled(diskDef.isSyncFrom());
			
		}
		else
		{
			this.setButton(false);
			this.setEnabled(false);
		}
		
	}

	@Override
	public void handleClick()
	{
		MainWin.sendCommand("dw disk set " + this.diskwin.currentDisk.getDriveNo() + " syncfrom " + (!this.isEnabled()));
		
	}
	


	
	
}
