package com.groupunix.drivewireui.library;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class CloudLibraryItem extends LibraryItem
{

	public CloudLibraryItem(String title)
	{
		super(title);
		this.type = DWLibrary.TYPE_FOLDER_CLOUD;
	}
	
	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/weather-clouds-2.png");
	}

}
