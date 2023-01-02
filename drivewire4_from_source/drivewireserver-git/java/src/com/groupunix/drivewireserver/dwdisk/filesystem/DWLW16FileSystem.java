package com.groupunix.drivewireserver.dwdisk.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskSector;
import com.groupunix.drivewireserver.dwexceptions.DWDiskInvalidSectorNumber;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFileNotFoundException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemFullException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFilenameException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;

public class DWLW16FileSystem extends DWFileSystem
{
	
	
	private static final String FSNAME = "LW16";
	private List<String> fserrors = new ArrayList<String>();

	DWLW16FileSystemSuperBlock superblock;
	
	
	
	public DWLW16FileSystem(DWDisk disk) throws IOException, DWDiskInvalidSectorNumber
	{
		super(disk);
		
		this.superblock = new DWLW16FileSystemSuperBlock(this.disk.getSector(0));
	}
	
	


	@Override
	public List<String> getFSErrors()
	{
		return this.fserrors;
	}







	@Override
	public List<DWFileSystemDirEntry> getDirectory(String path)
			throws IOException, DWFileSystemInvalidDirectoryException, DWDiskInvalidSectorNumber
	{
		
		List<DWFileSystemDirEntry> res = new ArrayList<DWFileSystemDirEntry>();
		
		if (path == null)
		{
			for (DWLW16FileSystemDirEntry entry : this.getRootDirectory())
			{
				res.add(entry);
			}
		}
		else
		{
			System.out.println("req dir: ");
		}
		
		return res;
	}







	public List<DWLW16FileSystemDirEntry> getRootDirectory() throws DWDiskInvalidSectorNumber, IOException
	{
		List<DWLW16FileSystemDirEntry> res = new ArrayList<DWLW16FileSystemDirEntry>();
		
		// get inode 0
		
		System.out.println("first inode: " + this.superblock.getFirstinodeblock());
		System.out.println("first data: " + this.superblock.getFirstdatablock());
		System.out.println("data blocks: " + this.superblock.getDatablocks());
		System.out.println("data bmps: " + this.superblock.getDatabmpblocks());
		System.out.println("tot inodes: " + this.superblock.getInodes());
		System.out.println();
		
		
		DWLW16FileSystemInode in0 = new DWLW16FileSystemInode(0, this.disk.getSector(this.superblock.getFirstinodeblock() + 1).getData()) ;
		
		System.out.println(in0.toString());
		
		return res;
	}




	@Override
	public boolean hasFile(String filename) throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}







	@Override
	public ArrayList<DWDiskSector> getFileSectors(String filename)
			throws DWFileSystemFileNotFoundException,
			DWFileSystemInvalidFATException, IOException,
			DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		return null;
	}







	@Override
	public DWFileSystemDirEntry getDirEntry(String filename)
			throws DWFileSystemFileNotFoundException, IOException,
			DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		return null;
	}







	@Override
	public byte[] getFileContents(String filename)
			throws DWFileSystemFileNotFoundException,
			DWFileSystemInvalidFATException, IOException,
			DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		return null;
	}







	@Override
	public void addFile(String filename, byte[] filecontents)
			throws DWFileSystemFullException,
			DWFileSystemInvalidFilenameException,
			DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException,
			IOException, DWDiskInvalidSectorNumber,
			DWFileSystemInvalidDirectoryException
	{
		// TODO Auto-generated method stub
		
	}







	@Override
	public void format() throws DWInvalidSectorException,
			DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException,
			IOException
	{
		// TODO Auto-generated method stub
		
	}







	@Override
	public String getFSName()
	{
		return DWLW16FileSystem.FSNAME;
	}




	@Override
	public boolean isValidFS()
	{
		// valid superblock?
		if (this.superblock.isValid())
			{
		// image size checks
				
			if ((this.disk.getSectors().size() < 65536) && (this.superblock.getFirstdatablock() < this.disk.getSectors().size()) && (this.superblock.getFirstinodeblock() < this.disk.getSectors().size()))
			{
				return true;
			}
				
		}
			
			
		return false;
	}
	
}
