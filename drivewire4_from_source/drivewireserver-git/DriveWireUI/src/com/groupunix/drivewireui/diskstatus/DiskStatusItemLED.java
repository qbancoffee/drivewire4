package com.groupunix.drivewireui.diskstatus;

import com.groupunix.drivewireui.DiskDef;

public class DiskStatusItemLED extends DiskStatusItem
{


	
	public DiskStatusItemLED(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(25);
		this.setY(215);
		this.setWidth(41);
		this.setHeight(30);
		
		this.setDisabledImage("/disk/diskdrive-leddark-big.png");
		this.setEnabledImage( "/disk/diskdrive-ledgreeen-big.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("led hover text");
		
	}

	@Override
	public void setDisk(DiskDef diskDef)
	{
		if ((diskDef != null) && diskDef.isLoaded())
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
		//nop
		
	}
	
	@Override
	public void redraw()
	{
		if ((canvas != null) && !canvas.isDisposed())
		{
			canvas.redraw();
		}
	}

	
	
}
