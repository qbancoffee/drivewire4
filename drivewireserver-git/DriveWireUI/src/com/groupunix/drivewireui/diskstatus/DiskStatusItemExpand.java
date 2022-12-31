package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;

public class DiskStatusItemExpand extends DiskStatusItem
{


	
	public DiskStatusItemExpand(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(372);
		this.setY(352);
		this.setWidth(57);
		this.setHeight(24);
		
		this.setDisabledImage( "/disk/disk_expand_disabled.png");
		this.setEnabledImage( "/disk/disk_expand_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("expand hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded() && (diskDef.isExpand()) )
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
