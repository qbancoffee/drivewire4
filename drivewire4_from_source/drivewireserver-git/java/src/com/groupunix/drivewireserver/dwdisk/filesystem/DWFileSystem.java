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

public abstract class DWFileSystem
{
	protected DWDisk disk;
		
	public DWFileSystem(DWDisk disk)
	{
		this.disk = disk;
	}
	

	public abstract List<DWFileSystemDirEntry> getDirectory(String path) throws IOException, DWFileSystemInvalidDirectoryException, DWDiskInvalidSectorNumber;
	
	public abstract boolean hasFile(String filename) throws IOException;
		
	public abstract ArrayList<DWDiskSector> getFileSectors(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;
	
	public abstract DWFileSystemDirEntry getDirEntry(String filename) throws DWFileSystemFileNotFoundException, IOException, DWFileSystemInvalidDirectoryException;
	
	public abstract byte[] getFileContents(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;
	
	public abstract void addFile(String filename, byte[] filecontents) throws DWFileSystemFullException, DWFileSystemInvalidFilenameException, DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;
	
	public abstract void format() throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException;

	public abstract String getFSName();
	
	public abstract boolean isValidFS();
	
	public abstract List<String> getFSErrors();
	
	
	
}
