package com.groupunix.drivewireui.library;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.graphics.Image;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWLW16FileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWLW16FileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWRBFFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFileNotFoundException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireui.DWLibrary;
import com.groupunix.drivewireui.MainWin;

public class PathLibraryItem extends LibraryItem
{
	private String path = null;
	private String iconpath;
	private boolean validdisk = false;
	private int validfs = DWLibrary.FSTYPE_UNKNOWN;
	private Node node = null;
	private boolean directory = false;
	
	
	public PathLibraryItem(String title, String path, Node item)
	{
		super(title);
		this.setPath(path);
		if (item != null)
			this.node = item;
		this.type = DWLibrary.TYPE_PATH;
		checkPath();
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}

	@Override
	public String getHoverText()
	{
		return path;
	}
	
	
	public Node getNode()
	{
		return this.node;
	}
	

	public Image getIcon()
	{
		return org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, this.iconpath);
	}
	
	
	private void checkPath()
	{
		FileObject fobj;
		
		try
		{
			fobj = VFS.getManager().resolveFile(this.path);
			
			if (fobj.isReadable())
			{
				if (fobj.getType() == FileType.FOLDER)
				{
					this.directory = true;
					if (this.node == null)
						this.iconpath = "/menu/folder.png";
					else
						this.iconpath = "/menu/database-save.png";
				}
				else
				{

					try
					{
						//System.out.println("Scanning " + fobj.getName().getFriendlyURI());
						DWDisk disk = DWDiskDrives.DiskFromFile(fobj);
						
						DWRBFFileSystem tmprbffs = new DWRBFFileSystem(disk);
						
						if (tmprbffs.isValidFS())
						{
							this.iconpath = "/fs/rbf.png";
							this.validdisk  = true;
							this.validfs  = DWLibrary.FSTYPE_RBF;
							return;
						}
						else
						{
							
							try
							{
								DWLW16FileSystem lw16ffs = new DWLW16FileSystem(disk);
								

								if (lw16ffs.isValidFS())
								{
									this.iconpath = "/fs/lw16.png";
									this.validdisk  = true;
									this.validfs  = DWLibrary.FSTYPE_LW16;
									return;
								}
								
							} catch (DWDiskInvalidSectorNumber e)
							{
						
							}
							
							
							DWDECBFileSystem tmpdecbfs = new DWDECBFileSystem(disk);
								
							if (tmpdecbfs.isValidFS())
							{
								this.iconpath = "/fs/decb.png";
								this.validdisk  = true;
								this.validfs  = DWLibrary.FSTYPE_DECB;
							}
							else
							{
								this.iconpath = "/fs/unknown.png";
								this.validdisk  = true;
							}
						}
						
						
						
					} 
					catch (DWImageFormatException e)
					{
						//System.out.println("bad format: " + fobj.getName().getFriendlyURI());
						this.iconpath = "/status/failed_16.png";
					} 
					catch (IOException e)
					{
						this.iconpath = "/status/failed_16.png";
					}
				
				}
				
			}
			else
			{
				System.out.println( this.path + " '" + fobj.getName().getPath()+ "' unreadable" );
				this.iconpath = "/status/failed_16.png";
			}
			
		} 
		catch (FileSystemException e)
		{
			System.out.println( this.path + " error: " + e.getMessage() );
			this.iconpath = "/status/failed_16.png";
		} 
	}
	
	
	
	public Vector<LibraryItem> getChildren()
	{
		// find disks/folders
		
		if (this.children.size() == 0)
		{
		
			FileObject fobj;
			
			try
			{
				fobj = VFS.getManager().resolveFile(this.path);
				
				if (fobj.isReadable())
				{
					if (fobj.getType() == FileType.FOLDER)
					{
						FileObject[] kids = fobj.getChildren();
						
						for (int i = 0;i<kids.length;i++)
						{
							PathLibraryItem pitmp = new PathLibraryItem(kids[i].getName().getBaseName(), kids[i].getName().getFriendlyURI(), null );
							
							if ((kids[i].getType() == FileType.FOLDER) || (pitmp.isValidDisk()))
								this.children.add(pitmp);
						}
						
					}
					else if (this.validfs == DWLibrary.FSTYPE_DECB)
					{
						DWDECBFileSystem decbfs = new DWDECBFileSystem(DWDiskDrives.DiskFromFile(fobj, true));
						
						for (DWFileSystemDirEntry entry : decbfs.getDirectory(null))
						{
							
							if (((DWDECBFileSystemDirEntry) entry).isUsed() && !((DWDECBFileSystemDirEntry) entry).isKilled())
							{
								this.children.add(new DECBFileLibraryItem((DWDECBFileSystemDirEntry) entry, decbfs.getFileContents(entry.getFileName().trim() + "." + entry.getFileExt() ) ));
							}
						}
					
					}
					else if (this.validfs == DWLibrary.FSTYPE_RBF)
					{
						DWRBFFileSystem rbffs = new DWRBFFileSystem(DWDiskDrives.DiskFromFile(fobj, true));
						
						for (DWFileSystemDirEntry entry : rbffs.getDirectory(null))
						{
							if ((!entry.getFileName().equals(".")) && (!entry.getFileName().equals("..")))
							{
								this.children.add(new RBFFileLibraryItem((DWRBFFileSystemDirEntry) entry, rbffs ));
							}
						}
					
					}
					else if (this.validfs == DWLibrary.FSTYPE_LW16)
					{
						DWLW16FileSystem lwfs = new DWLW16FileSystem(DWDiskDrives.DiskFromFile(fobj, true));
						
						for (DWFileSystemDirEntry entry : lwfs.getDirectory(null))
						{
							if ((!entry.getFileName().equals(".")) && (!entry.getFileName().equals("..")))
							{
								this.children.add(new LW16FileLibraryItem((DWLW16FileSystemDirEntry) entry, lwfs ));
							}
						}
					
					}
				}
				
			}
			catch (FileSystemException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} catch (DWImageFormatException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} catch (IOException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} 
			catch (DWFileSystemInvalidDirectoryException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} 
			catch (DWFileSystemFileNotFoundException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} 
			catch (DWFileSystemInvalidFATException e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			} 
			catch (DWDiskInvalidSectorNumber e)
			{
				System.out.println(this.path + ": " + e.getMessage());
			}
		}
		
		return this.children;
	}

	public boolean isValidDisk()
	{

		return this.validdisk;
	}
	
	public int getFSType()
	{
		return this.validfs;
	}

	public void setDirectory(boolean directory)
	{
		this.directory = directory;
	}

	public boolean isDirectory()
	{
		return directory;
	}
	
	
}
