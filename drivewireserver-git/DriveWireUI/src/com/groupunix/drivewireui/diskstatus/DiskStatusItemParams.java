package com.groupunix.drivewireui.diskstatus;

import org.eclipse.swt.SWT;

import com.groupunix.drivewireui.DiskAdvancedWin;
import com.groupunix.drivewireui.DiskDef;

public class DiskStatusItemParams extends DiskStatusItem
{


	
	public DiskStatusItemParams(DiskStatusWin diskwin)
	{
		super(diskwin);
		this.setX(175);
		this.setY(352);
		this.setWidth(79);
		this.setHeight(29);
		
		this.setDisabledImage( "/disk/disk_edit_params_disabled.png");
		this.setEnabledImage( "/disk/disk_edit_params_enabled.png");
		this.setClickImage("/animals/alien.png");
		
		this.setButton(false);
		this.setHovertext("edit params hover text");
		
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
	public void handleClick() {
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public void handleClick()
	{
		if (this.diskwin.currentDisk.hasParamwin())
		{
			this.diskwin.currentDisk.getParamwin().shell.setActive();
		}
		else
		{
			this.diskwin.currentDisk.setParamwin(new DiskAdvancedWin(this.diskwin.shlDwDrive, SWT.DIALOG_TRIM, this.diskwin.currentDisk));
			this.diskwin.currentDisk.getParamwin().open();

		}
		
	}
	*/


	
	
}
