package com.groupunix.drivewireui.library;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWLW16FileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWLW16FileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class LW16FileLibraryItem extends LibraryItem
{
	
	private Image icon;
	private DWLW16FileSystemDirEntry entry;
	private DWLW16FileSystem lwfs;
	
	public LW16FileLibraryItem(DWLW16FileSystemDirEntry entry2, DWLW16FileSystem lwfs)
	{
		super(entry2.getFileName());
		
		this.setEntry(entry2);
		this.lwfs = lwfs;
		
		
		
		if (entry2.isDirectory())
		{
			this.type = DWLibrary.TYPE_RBF_DIR;
			this.icon =  org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/folder.png");
		}
		else
		{
			this.type = DWLibrary.TYPE_RBF_FILE;
			
		}
		
	}

	
	public Image getIcon()
	{
		return icon;
	}





	public void setEntry(DWLW16FileSystemDirEntry entry2)
	{
		this.entry = entry2;
	}


	public DWLW16FileSystemDirEntry getEntry()
	{
		return entry;
	}
	
	@Override
	public Vector<LibraryItem> getChildren()
	{
		
		if ((this.children.size() == 0) && (this.entry.isDirectory()))
		{
			try
			{
				List<DWLW16FileSystemDirEntry> dir = this.lwfs.getRootDirectory();
				
				for (DWLW16FileSystemDirEntry e : dir)
				{
					if ((!e.getFileName().equals(".")) && (!e.getFileName().equals("..")))
						this.children.add(new LW16FileLibraryItem(e, this.lwfs));
				}
				
			} 
			catch (IOException e)
			{
			} 
			catch (DWDiskInvalidSectorNumber e)
			{
			}
		}
		
		return this.children;
	}


	


	
}	
