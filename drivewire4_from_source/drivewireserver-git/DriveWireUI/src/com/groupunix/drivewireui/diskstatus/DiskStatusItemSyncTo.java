package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public class DiskStatusItemSyncTo extends DiskStatusItem
{


	
	public DiskStatusItemSyncTo(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(30);
		this.setY(84);
		this.setWidth(60);
		this.setHeight(32);
		
		this.setDisabledImage( "/disk/disk_sync_to_disabled.png");
		this.setEnabledImage( "/disk/disk_sync_to_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("sync-to hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded())
		{
			this.setButton(diskDef.hasParam("syncto"));
			this.setEnabled(diskDef.isSyncTo());
			
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
		MainWin.sendCommand("dw disk set " + this.diskwin.currentDisk.getDriveNo() + " syncto " + (!this.isEnabled()));
		
	}
	


	
	
}
