package com.groupunix.drivewireui.plugins;

import org.eclipse.swt.widgets.Composite;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;

public abstract class FileViewer extends Composite
{

	public FileViewer(Composite parent, int style)
	{
		super(parent, style);
	}
	
	
	 public abstract int getViewable(DWFileSystemDirEntry direntry, byte[] content);
	  
	 public abstract void viewFile(DWFileSystemDirEntry direntry, byte[] content);
	 public abstract String getTypeName();
	 public abstract String getTypeIcon();
	 
}
