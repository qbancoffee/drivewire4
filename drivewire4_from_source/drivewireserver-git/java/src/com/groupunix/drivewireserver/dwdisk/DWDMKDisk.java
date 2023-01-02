package com.groupunix.drivewireserver.dwdisk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWDMKDisk extends DWDisk
{
	private static final Logger logger = Logger.getLogger("DWServer.DWDMKDisk");
	
	private ArrayList<DWDMKDiskTrack> tracks = new ArrayList<DWDMKDiskTrack>();
	private DWDMKDiskHeader header;
	
	
	public DWDMKDisk(FileObject fileobj) throws IOException, DWImageFormatException
	{
		super(fileobj);
		this.setParam("_format", "dmk");
		
		load();
		
		logger.debug("New DMK disk for " + fileobj.getName().getURI());
	}
	
	

	public int getDiskFormat()
	{
		return DWDefs.DISK_FORMAT_DMK;
	}



	public void load() throws IOException, DWImageFormatException
	{
		// load file into sector array
	    InputStream fis;
	
	    fis = this.fileobj.getContent().getInputStream();
	   
	    // read disk header
	    int readres = 0;
	    byte[] hbuff = new byte[16];
	    
	    while (readres < 16)
	    	readres += fis.read(hbuff, readres, 16 - readres);
	    
	    this.header = new DWDMKDiskHeader(hbuff);
	    
	    this.setParam("writeprotect", header.isWriteProtected());
	    this.setParam("_tracks", header.getTracks());
	    this.setParam("_sides", header.getSides());
	    this.setParam("_density", header.getDensity());
	 
	    
	    if (!header.isSingleSided() || header.isSingleDensity())
	    {
	    	String format = "";
	    	
	    	if (header.isSingleSided())
	    		format += "SS";
	    	else
	    		format += "DS";
	    	
	    	
	    	if (header.isSingleDensity())
	    		format += "SD";
	    	else
	    		format += "DD";
	    	
	    	
	    	fis.close();
	    	throw new DWImageFormatException("Unsupported DMK format " + format + ", only SSDD is supported at this time");
	    }
	    
	    // read tracks
	    byte[] tbuf;
	    
	    for (int i =0;i<header.getTracks();i++)
	    {
	    	
	    	// read track data
	    	tbuf = new byte[header.getTrackLength()];
	    	readres = 0;
	    	
	    	while ((readres < header.getTrackLength()) && (readres > -1))
	    	{
		    	int res = fis.read(tbuf, readres, header.getTrackLength() - readres);
		    	if (res > -1)
		    		readres += res;
		    	else
		    		readres = -1;
	    	}
	    	
	    	if (readres == -1)
	    	{
	    		fis.close();
	    		throw new DWImageFormatException("DMK format appears corrupt, incomplete data for track " + i);
	    	}
	    	
	    	DWDMKDiskTrack dmktrack = new DWDMKDiskTrack(tbuf);
	    	
	    	if (dmktrack.getNumSectors() != 18)
	    	{
	    		fis.close();
	    		throw new DWImageFormatException("Unsupported DMK format, only 18 sectors per track is supported at this time");
	    	}
	    	
	    	this.tracks.add(dmktrack);
	    }
	    	    
		fis.close();
		
		// all tracks loaded ok, find sector data
		loadSectors();
		
		this.setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
	 
	}


	private void loadSectors() throws DWImageFormatException, FileSystemException
	{
		this.sectors.clear();
		// hard coded to 18 spt until i find a reason not to
		this.sectors.setSize(header.getTracks() * 18);
		this.setParam("_sectors", header.getTracks() * 18);
		
		for (int t = 0;t < header.getTracks();t++)
		{
			
			// track header / IDAM ptr table
			for (int i = 0; i < 64; i++)
			{
				DWDMKDiskIDAM idam = this.tracks.get(t).getIDAM(i);
				
				
				if (idam.getPtr() != 0)
				{
					if (idam.getTrack() != t)
					{
						System.out.println("mismatch track in IDAM?");
					}

				
					
					addSectorFrom(idam, t);
					
				}	
			}
		}
	}
	
	
	
	private void addSectorFrom(DWDMKDiskIDAM idam, int track) throws DWImageFormatException, FileSystemException
	{
		int lsn = calcLSN(idam);
		
		if ((lsn > -1) && (lsn < this.sectors.size()))
		{
			this.sectors.set(lsn, new DWDiskSector(this, lsn, idam.getSectorSize(), false));
			this.sectors.get(lsn).setData(getSectorDataFrom(idam, track), false);
		}
		else
		{
			throw new DWImageFormatException("Invalid LSN " + lsn + " while adding sector from DMK!");
		}
			
	}

	
	private byte[] getSectorDataFrom(DWDMKDiskIDAM idam, int track) throws DWImageFormatException
	{
		byte[] buf = new byte[idam.getSectorSize()];
		int loc = idam.getPtr() + 7;
		int gap = 43;
		
		
		boolean sync = false;
		
		if (this.header.isSingleDensity())
			gap = 30;

		while (gap > 0)
		{
			if (((0xFF & this.tracks.get(track).getData()[loc]) >= 0xF8) && ((0xFF & this.tracks.get(track).getData()[loc]) <= 0xFB))
			{
				if (this.header.isSingleDensity() || sync)
				{
					break;
				}
			}
			
			if (!this.header.isSingleDensity())
			{
				if ((0xFF & this.tracks.get(track).getData()[loc]) == 0xA1)
				{
					sync = true;
				}
				else
				{
					sync = false;
				}
			}
			
			loc++;
			gap--;
		}
		
		if (gap > 0)
		{
			// found the data
			System.arraycopy(this.tracks.get(track).getData(), loc+1, buf, 0, idam.getSectorSize());
		}
		else
		{
			throw new DWImageFormatException("Sector data missing for track " + track + " sector " + idam.getSector());
		}
			
		return buf;
	}



	private int calcLSN(DWDMKDiskIDAM idam)
	{
		int t = idam.getTrack() * 18;
		
		if (!header.isSingleSided())
			t = t * 2;
		
		t += (idam.getSector() - 1) + (18 * idam.getSide());
		
		return(t);
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


	
	public void writeSector(byte[] data, boolean update) throws DWDriveWriteProtectedException,	IOException
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
			
			// logger.debug("write sector " + this.LSN + "\r" + DWProtocolHandler.byteArrayToHexString(this.sectors[this.LSN].getData()));
		}
	}
	
	

	public byte[] readSector() throws IOException
	{
		this.incParam("_reads");
		return(this.sectors.get(this.getLSN()).getData() );
	}

	



	public static int considerImage(byte[] hdr, long fobjsize)
	{
		    
		// is it big enough to have a header
		if (fobjsize > 16)
		{

			// make a header object
			DWDMKDiskHeader header = new DWDMKDiskHeader(hdr);
			
			// is the size right?
			if (fobjsize ==  (16 + (header.getSides() * header.getTracks() * header.getTrackLength())))
			{
				// good enough for mess, good enough for me
				return(DWDefs.DISK_CONSIDER_YES);
			}
			
		}
		
		return(DWDefs.DISK_CONSIDER_NO);
	}

}
