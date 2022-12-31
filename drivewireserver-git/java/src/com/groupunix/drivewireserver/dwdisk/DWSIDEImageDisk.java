package com.groupunix.drivewireserver.dwdisk;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWSIDEImageDisk extends DWDisk
{
	private static final Logger logger = Logger.getLogger("DWServer.DWSIDEImageDisk");
	private long startpos;
	private long endpos;
	@SuppressWarnings("unused")
	private boolean halfsector;
	

	public DWSIDEImageDisk(FileObject fileobj, long start, long end, boolean halfsector) throws IOException, DWImageFormatException
	{
		super(fileobj);
		this.setParam("_format", "side");
		
		this.startpos = start;
		this.endpos = end;
		this.halfsector = halfsector;
				
		load();
		
		logger.debug("New SuperIDE image disk for " + fileobj.getName().getURI() + " (start: " + start + " end: " + end + " halfsectors: " + halfsector);
	}
	
	@Override
	public void seekSector(int lsn) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException
	{
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void writeSector(byte[] data, boolean update) throws DWDriveWriteProtectedException, IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] readSector() throws IOException, DWImageFormatException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void load() throws IOException, DWImageFormatException
	{
		// load file into sector array

		int sector = 0;
	    
	    long filesize = this.endpos - this.startpos;
	    
	    if ((filesize > Integer.MAX_VALUE))
	    	throw new DWImageFormatException("Image file is too large");
	    
	    int sz = 0;
	    	
	    this.sectors.setSize( (int) (filesize / 256));
	    	
	    while (sz < filesize)
	    {
	    	this.sectors.set(sector, new DWDiskSector(this, sector, 256, true));
	    	sector++;
	    	sz += 256;
	    }
	    
	    
	    long lastmodtime = -1;
	    
	    try
	    {
	    	lastmodtime = this.fileobj.getContent().getLastModifiedTime();
	    }
	    catch (FileSystemException e)
	    {
	    	logger.warn(e.getMessage());
	    }
	    
	    this.setLastModifiedTime(lastmodtime); 

	    this.setParam("_sectors", sector);
	    
		this.setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
		 
			
	}

	@Override
	public int getDiskFormat()
	{
		return DWDefs.DISK_FORMAT_SIDE;
	}

}
