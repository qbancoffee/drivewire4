package com.groupunix.drivewireserver.dwdisk;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;

public class DWDiskDrive
{
	private static final Logger logger = Logger.getLogger("DWServer.DWDiskDrive");
	
	private int driveno;
	private boolean loaded = false;
	private DWDisk disk = null;
	private DWDiskDrives drives;
	
	
	
	public DWDiskDrive(DWDiskDrives drives, int driveno)
	{
		this.drives = drives;
		this.driveno = driveno;
	}



	public int getDriveNo()
	{
		return driveno;
	}


	public boolean isLoaded()
	{
		return this.loaded;
	}
	


	public DWDisk getDisk() throws DWDriveNotLoadedException
	{
		if (this.loaded)
		{
			return(this.disk);
		}
		else
		{
			throw new DWDriveNotLoadedException("No disk in drive " + this.getDriveNo());
		}

	}


	public void eject() throws DWDriveNotLoadedException
	{
		
		if (this.disk == null)
		{
			throw new DWDriveNotLoadedException("There is no disk in drive " + this.driveno);
		}
		
		synchronized(this.disk)
		{
			try
			{
				this.disk.eject();
			} 
			catch (IOException e)
			{
				logger.warn("Ejecting from drive " + this.getDriveNo() + ": " + e.getMessage());
			}
			
			this.loaded = false;
			this.disk = null;
			this.submitEvent("*eject", "");
		}
	}


	public void insert(DWDisk disk)
	{
		this.disk = disk;
		this.loaded = true;
		this.submitEvent("*insert", this.disk.getFilePath());
		this.disk.insert(this);
		
	}


	public void seekSector(int lsn) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveNotLoadedException
	{
		if (this.disk == null)
			throw new DWDriveNotLoadedException("No disk in drive " + this.driveno);
			
		synchronized(this.disk)
		{
			this.disk.seekSector(lsn);
		}
		
	}


	public byte[] readSector() throws IOException, DWImageFormatException
	{
		if (this.disk == null)
			throw new IOException("Disk is null");
		
		synchronized(this.disk)
		{
			return(this.disk.readSector());
		}
	}


	public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException
	{
		if (this.disk == null)
			throw new IOException("Disk is null");
		
		synchronized(this.disk)
		{
			this.disk.writeSector(data);
		}
	}


	public void submitEvent(String key, String val)
	{
		if (this.drives != null)
			this.drives.submitEvent(this.driveno, key, val);
	}
	
	public DWDiskDrives getDiskDrives()
	{
		return this.drives;
	}
	
}
