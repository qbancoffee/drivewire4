package com.groupunix.drivewireui.library;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.plugins.FileTypeDetector;

public class DECBFileLibraryItem extends LibraryItem
{
	private byte[] data;
	private Image icon;
	private DWDECBFileSystemDirEntry entry;
	
	public DECBFileLibraryItem(DWDECBFileSystemDirEntry entry, byte[] data)
	{
		super(entry.getFileName() + "." + entry.getFileExt() );
		
		this.setData(data);
		this.setEntry(entry);
		this.icon = FileTypeDetector.getFileIcon(FileTypeDetector.getDECBFileType(entry, data));
		this.type = DWLibrary.TYPE_DECB_FILE;
		
	}

	
	public Image getIcon()
	{
		return icon;
	}


	public void setData(byte[] data)
	{
		this.data = data;
	}


	public byte[] getData()
	{
		return data;
	}


	public void setEntry(DWDECBFileSystemDirEntry entry)
	{
		this.entry = entry;
	}


	public DWDECBFileSystemDirEntry getEntry()
	{
		return entry;
	}
	
	
	
}
