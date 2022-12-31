package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public class DiskStatusItemEject extends DiskStatusItem
{


	
	public DiskStatusItemEject(DiskStatusWin diskwin)
	{
		super(diskwin);
		
		this.setX(364);
		this.setY(156);
		this.setWidth(30);
		this.setHeight(15);
		
		this.setDisabledImage(null);
		this.setEnabledImage( "/disk/disk_eject_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("eject hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded())
		{
			this.setButton(true);
			this.setEnabled(true);
			
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
		MainWin.sendCommand("dw disk eject " +  this.diskwin.currentDisk.getDriveNo());
		
	}
	


	
	
}
