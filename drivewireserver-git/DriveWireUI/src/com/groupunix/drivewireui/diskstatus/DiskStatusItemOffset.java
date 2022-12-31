package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;

public class DiskStatusItemOffset extends DiskStatusItem
{


	
	public DiskStatusItemOffset(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(20);
		this.setY(352);
		this.setWidth(56);
		this.setHeight(24);
		
		this.setDisabledImage( "/disk/disk_offset_disabled.png");
		this.setEnabledImage( "/disk/disk_offset_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("offset hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded() && (diskDef.getOffset()> 0) )
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
