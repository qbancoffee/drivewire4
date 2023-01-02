package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;

public class DiskStatusItemLimit extends DiskStatusItem
{


	
	public DiskStatusItemLimit(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(305);
		this.setY(352);
		this.setWidth(46);
		this.setHeight(24);
		
		this.setDisabledImage( "/disk/disk_sizelimit_disabled.png");
		this.setEnabledImage( "/disk/disk_sizelimit_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("limit hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded() && (diskDef.getLimit() > -1) )
		{
			this.setButton(false);
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
		
		
	}
	


	
	
}
