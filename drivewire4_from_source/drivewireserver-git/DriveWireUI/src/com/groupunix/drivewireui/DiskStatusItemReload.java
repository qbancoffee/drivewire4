package com.groupunix.drivewireui;

public class DiskStatusItemReload extends DiskStatusItem
{


	
	public DiskStatusItemReload(DiskWin diskwin)
	{
		super(diskwin);
		this.setX(240);
		this.setY(84);
		this.setWidth(70);
		this.setHeight(32);
		
		this.setDisabledImage( "/disk/disk_reload_disabled.png");
		this.setEnabledImage( "/disk/disk_reload_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("reload hover text");
		
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
		MainWin.sendCommand("dw disk reload " + this.diskwin.currentDisk.getDriveNo());
		
	}
	


	
	
}
