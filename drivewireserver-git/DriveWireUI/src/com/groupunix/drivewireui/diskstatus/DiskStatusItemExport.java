package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public class DiskStatusItemExport extends DiskStatusItem
{


	
	public DiskStatusItemExport(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(345);
		this.setY(84);
		this.setWidth(70);
		this.setHeight(32);
		
		this.setDisabledImage( "/disk/disk_export_disabled.png");
		this.setEnabledImage( "/disk/disk_export_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("export hover text");
		
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
		MainWin.writeDiskTo(this.diskwin.currentDisk.getDriveNo());
		
	}
	


	
	
}
