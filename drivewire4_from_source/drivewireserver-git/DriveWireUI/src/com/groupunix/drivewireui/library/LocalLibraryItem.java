package com.groupunix.drivewireui.library;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class LocalLibraryItem extends LibraryItem
{
	
	public LocalLibraryItem(String title)
	{
		super(title);
		this.type = DWLibrary.TYPE_FOLDER_LOCAL_PATHS;
		
	}

	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/database-save.png");
	}
	
}
