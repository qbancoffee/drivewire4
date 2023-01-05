package com.groupunix.drivewireui;

public class DiskStatusItemWriteProtect extends DiskStatusItem
{


	
	public DiskStatusItemWriteProtect(DiskWin diskwin)
	{
		super(diskwin);
	
		this.setX(75);
		this.setY(225);
		this.setWidth(41);
		this.setHeight(24);
		
		this.setDisabledImage( "/disk/disk_writeprotect_disabled.png");
		this.setEnabledImage( "/disk/disk_writeprotect_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("wp hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded())
		{
			this.setButton(true);
			this.setEnabled(diskDef.isWriteProtect());
			
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
		MainWin.sendCommand("dw disk set " + this.diskwin.currentDisk.getDriveNo() + " writeprotect " + (!this.isEnabled()));
		
	}
	


	
	
}
