package com.groupunix.drivewireui.library;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class MountedLibraryItem extends LibraryItem
{
	private int diskno;

	public MountedLibraryItem(String title, int diskno)
	{
		super(title);
		this.type = DWLibrary.TYPE_FOLDER_MOUNTED;
		this.setDiskno(diskno);
	}

	
	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-insert.png");
	}


	public int getDiskno() {
		return diskno;
	}


	public void setDiskno(int diskno) {
		this.diskno = diskno;
	}
}
