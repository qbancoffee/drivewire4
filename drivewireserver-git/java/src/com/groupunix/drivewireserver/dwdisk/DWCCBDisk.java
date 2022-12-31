package com.groupunix.drivewireserver.dwdisk;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs.FileObject;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCCBDisk extends DWDisk
{
	private static final Logger logger = Logger.getLogger("DWServer.DWCCBDisk");
	private static final long CCB_FILE_SIZE_MIN = 3;
	
	public DWCCBDisk(FileObject fileobj) throws IOException, DWImageFormatException
	{
		super(fileobj);
		
		this.setParam("_format", "ccb");
		
		load();
		
		logger.debug("New CCB disk for " + fileobj.getName().getURI());
	}
	
	

	public int getDiskFormat()
	{
		return DWDefs.DISK_FORMAT_CCB;
	}



	public void load() throws IOException, DWImageFormatException
	{
		// load file into sector array
	    InputStream fis;
		    
	    fis = this.fileobj.getContent().getInputStream();
	  
	    long fobjsize = fileobj.getContent().getSize();
	    
	    if (fobjsize > Integer.MAX_VALUE)
	    	throw new DWImageFormatException("Image is too large.");
	    
	    this.sectors.setSize((int) ((fobjsize / DWDefs.DISK_SECTORSIZE)+1));
	   
	    byte[] buf = new byte[DWDefs.DISK_SECTORSIZE];
	    int readres = 0;
	    int secres = 0;
	    int sec = 0;
	    
	    while (readres > -1)
	    {
	    	int sz = (int) Math.min(DWDefs.DISK_SECTORSIZE - secres, fobjsize - (sec * DWDefs.DISK_SECTORSIZE));
	    	
	    	readres = fis.read(buf, readres, sz);
	    	
	    	// ccb scripts may not be /256
	    	if (readres == -1)
	    	{
	    		this.sectors.set(sec, new DWDiskSector(this, sec, DWDefs.DISK_SECTORSIZE, false));
	    		for (int i = secres;i < DWDefs.DISK_SECTORSIZE;i++)
	    			buf[i] = 0;
	    		this.sectors.get(sec).setData(buf, false);
	    		sec++;
	    	}
	    	else
	    	{
	    		secres += readres;
	    		readres = 0;
	    		
	    		if (secres == DWDefs.DISK_SECTORSIZE)
	    		{
	    			this.sectors.set(sec, new DWDiskSector(this, sec, DWDefs.DISK_SECTORSIZE, false));
		    		this.sectors.get(sec).setData(buf, false);
	    			secres = 0;
	    			
	    			sec++;
	    		}
	    			
	    	}
	    	
	    }
	    
	    this.setParam("_sectors", sec);
	    this.setParam("_filesystem", DWUtils.prettyFileSystem(DWDefs.DISK_FILESYSTEM_CCB));
	    
		fis.close();
		
		
	}




	public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException
	{
		if (newLSN < 0)
		{
			throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
		}
		else if (newLSN > (this.sectors.size()-1) )
		{
			throw new DWSeekPastEndOfDeviceException("Attempt to seek beyond end of image");
		}
		else
		{
			this.setParam("_lsn", newLSN);
		}
	}

	

	
	public void writeSector(byte[] data, boolean update) throws DWDriveWriteProtectedException, IOException
	{
		if (this.getWriteProtect())
		{
			throw new DWDriveWriteProtectedException("Disk is write protected");
		}
		else
		{
			
			this.sectors.get(this.getLSN()).setData(data);
			
			if (update)
				this.incParam("_writes");
			
		}
	}
	
	

	public byte[] readSector() throws IOException
	{
		this.incParam("_reads");
		return(this.sectors.get(this.getLSN()).getData() );
	}

	



	public static int considerImage(byte[] hdr, long fobjsize)
	{
		// 	is it big enough to have a header
		if (fobjsize >= DWCCBDisk.CCB_FILE_SIZE_MIN) 
		{
			
		
			// starts with 'ccb'
			if ((hdr[0]==99) && (hdr[1]==99) && (hdr[2]==98))
			{
				// has it been isaved..
				if (fobjsize % DWDefs.DISK_SECTORSIZE == 0)
				{
					return(DWDefs.DISK_CONSIDER_YES);
				}
				
				return(DWDefs.DISK_CONSIDER_MAYBE);
			}
		
		
		}
		
		
		return DWDefs.DISK_CONSIDER_NO;
	}



	
	

}
