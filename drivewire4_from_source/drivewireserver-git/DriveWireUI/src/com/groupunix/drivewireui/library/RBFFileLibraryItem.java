package com.groupunix.drivewireui.library;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.plugins.FileTypeDetector;

public class RBFFileLibraryItem extends LibraryItem
{
	
	private Image icon;
	private DWRBFFileSystemDirEntry entry;
	private DWRBFFileSystem rbffs;
	
	public RBFFileLibraryItem(DWRBFFileSystemDirEntry entry2, DWRBFFileSystem rbffs)
	{
		super(entry2.getFileName());
		
		this.setEntry(entry2);
		this.rbffs = rbffs;
		
		
		
		if (entry2.isDirectory())
		{
			this.type = DWLibrary.TYPE_RBF_DIR;
			this.icon =  org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/folder.png");
		}
		else
		{
			this.type = DWLibrary.TYPE_RBF_FILE;
			this.icon = FileTypeDetector.getFileIcon(FileTypeDetector.getRBFFileType(entry2, rbffs));
		}
		
	}

	
	public Image getIcon()
	{
		return icon;
	}





	public void setEntry(DWRBFFileSystemDirEntry entry2)
	{
		this.entry = entry2;
	}


	public DWRBFFileSystemDirEntry getEntry()
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
				ArrayList<DWRBFFileSystemDirEntry> dir = this.rbffs.getDirectoryFromFD(this.entry.getFD());
				
				for (DWRBFFileSystemDirEntry e : dir)
				{
					if ((!e.getFileName().equals(".")) && (!e.getFileName().equals("..")))
						this.children.add(new RBFFileLibraryItem(e, this.rbffs));
				}
				
			} 
			catch (IOException e)
			{
			} 
			catch (DWDiskInvalidSectorNumber e)
			{
			} 
			catch (DWFileSystemInvalidDirectoryException e)
			{
			}
		}
		
		return this.children;
	}


	


	public DWRBFFileSystem getRBFFS()
	{
		return rbffs;
	}
}	
