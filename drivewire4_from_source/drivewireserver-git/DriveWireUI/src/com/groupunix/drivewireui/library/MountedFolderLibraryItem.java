package com.groupunix.drivewireui.library;

import java.util.Vector;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public class MountedFolderLibraryItem extends LibraryItem
{

		
	
	public MountedFolderLibraryItem(String title)
	{
		super(title);
		this.type = DWLibrary.TYPE_FOLDER_MOUNTED;
	
	}

	
	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-insert.png");
	}
	
	
	public Vector<LibraryItem> getChildren()
	{
		
		Vector<LibraryItem> c = new Vector<LibraryItem>();
		
		for (DiskDef dd : MainWin.getDiskDefs())
		{
			if ((dd != null) && (dd.isLoaded()))
			{
				// c.add(new MountedLibraryItem("Drive " + dd.getDriveNo(), dd.getDriveNo()));
				c.add(new PathLibraryItem("Drive " + dd.getDriveNo(), dd.getPath(), null));
				
		
			}
		}
		
		
		return c;
	}
	
}
