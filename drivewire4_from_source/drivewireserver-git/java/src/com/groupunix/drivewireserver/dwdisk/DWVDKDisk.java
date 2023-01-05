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

public class DWVDKDisk extends DWDisk
{
	// ftp://davidgunn.org/coco/VARIOUS/INFO/MISC/VDK_Format.txt
	
	/*
	
	
	Dragon Emulator Virtual Disk (VDK) Format
Paul Burgin / v1.0 / April 1999

The new virtual disk format used by PC-Dragon v2.05 has at least 12 header bytes as follows:

Offset	Size	Item						Notes
0		2		Signature (“dk”)			MSB first. Note lower case to differentiate 
											a VDK from a DOS ROM file.
2		2		Header length				LSB first. Total length of the header (equal 
											to the offset to the start of disk data). 
											Intended to allow the header to be extended 
											easily.
4		1		VDK version - actual		Indicates the version of the VDK format 
											used to write the file. Currently $10 (VDK 
											v1.0).
5		1		VDK version - compatibility	For backwards compatibility, this indicates 
											the minimum version of the VDK format 
											that can be used to read the file. Effectively 
											it says to the emulator “If you can 
											understand VDK version X then you can 
											understand this file…”. Usually this byte 
											will be the same as the previous one, but if 
											a minor addition is made to the header then 
											it becomes useful.
6		1		Source id					Indicates how the file was created. 
											Essentially this is for information only, 
											but may be useful for debugging. The 
											following values are defined:
												   0 = created by hand
												   1 = header stub
												   2 = mkdsk.exe
												   3 = other tools
												 ‘P’ = PC-Dragon
												 ‘T’ = T3
												>$7F = other emulator
7		1		Source version				Version information for the source 
											identified above. E.g. $25=v2.05
8		1		Number of tracks			40 or 80
9		1		Number of sides				1 or 2
10		1		Flags						bit 0 = write protect [0=off, 1=on]
											bit 1 = lock (advisory) [0=off, 1=on]
											bit 2 = lock (mandatory) [0=off, 1=on]
											bit 3 = disk-set [0=last disk, 1=not last 
											disk]
											bits 4-7 = unused in VDK v1.0
11		1		Compression & Name length	bits 0-2 = compression [0=off, >0=TBD]
											bits 3-7 = disk name length [min 0, max 
											31]
12		0-31	Disk name					Optional ASCII name for the virtual disk. 
											Not zero terminated.
(min 12)	0+								Compression variables
											TBD

Some of the above needs a little more explanation. The write protect
ability is included as a bit in the header so that it can survive
circumstances which file attributes might not, if necessary (e.g. public
file servers, e-mail attachments). This also allows easy change from
within an emulator, and might be useful for disk-sets (see below).
Support for write protection by file attributes is at the option of each
individual emulator.

The lock bits were added as a possible approach for preventing a disk
being loaded more than once. It is typically not required for a single
instance of an emulator, but may be used across multiple simultaneous
emulators, multiple instances of the same emulator, or in multi-user
environments. The difference between bit1 and bit2 is that the user is
asked whether or not they wish to ignore the bit1 lock but are not
allowed to override the bit2 lock. A well behaved emulator should at
least obey the locks upon opening the VDK, but for the current v1.0 of
the file format need not set the locks unless it wishes to. PC-Dragon
v2.05 does not set either of the locks (I felt a little uncomfortable
about modifying the disk without the user’s consent) but they may be
used in the future.

The disk-set bit allows more than one virtual disk to be stored in a
single VDK file. This is intended for software supplied on >1 disk, or
for a collection of similar disks. Emulators may allow disk-sets to be
created/modified/loaded at their option. PC-Dragon v2.05 supports
loading of disk-sets, but the user interface is rather basic and there’s
no facility for creating disk-sets.

Virtual disk compression has been anticipated by the format, but is left
TBD for the moment due to the complexity of randomly accessing a
compressed file. The disk name is optional and isn’t ever displayed by
PC-Dragon v2.05. Any data associated with compression is assumed to
follow the disk name.
	
	
	*/
	
	
	// per the docs
	public static final int VDK_HEADER_SIZE_MIN = 12;
	// per me to use as sanity check. technically its 64k?
	public static final int VDK_HEADER_SIZE_MAX = 256;
	// per my guesses based on what docs I could find, these are fixed?
	public static final int VDK_SECTOR_SIZE = 256;
	public static final int VDK_SECTORS_PER_TRACK = 18;
	
	
	private static final Logger logger = Logger.getLogger("DWServer.DWVDKDisk");
	private DWVDKDiskHeader header;
	
	
	public DWVDKDisk(FileObject fileobj) throws IOException, DWImageFormatException
	{
		super(fileobj);
		
		this.setParam("_format", "vdk");
		
		load();
		
		logger.debug("New VDK disk for " + fileobj.getName().getURI());
	}
	
	

	public int getDiskFormat()
	{
		return DWDefs.DISK_FORMAT_VDK;
	}



	public void load() throws IOException, DWImageFormatException
	{
		// load file into sector array
	    InputStream fis;
		    
	    fis = this.fileobj.getContent().getInputStream();
	    
	    this.setLastModifiedTime(this.fileobj.getContent().getLastModifiedTime()); 
	    
	    // read disk header
	    this.header = readHeader(fis);

	 
	    this.setParam("writeprotect", header.isWriteProtected());
	    this.setParam("_tracks", header.getTracks());
	    this.setParam("_sides", header.getSides());
	    this.setParam("_sectors", (header.getTracks() * header.getSides() * DWVDKDisk.VDK_SECTORS_PER_TRACK));
	    
	    
	    if ( this.fileobj.getContent().getSize() != (this.params.getInt("_sectors")*DWVDKDisk.VDK_SECTOR_SIZE + this.header.getHeaderLen() ))
	    {
	    	throw new DWImageFormatException("Invalid VDK image, wrong file size");
	    }
	    
	    this.sectors.setSize(this.params.getInt("_sectors"));
	    
	    byte[] buf = new byte[DWVDKDisk.VDK_SECTOR_SIZE];
	    int readres;
	    
	    for (int i =0;i<this.params.getInt("_sectors") ;i++)
	    {
	    	readres = 0;
	    	
	    	while (readres < DWVDKDisk.VDK_SECTOR_SIZE)
		    	readres += fis.read(buf, readres, DWVDKDisk.VDK_SECTOR_SIZE - readres);
	    	
	    	this.sectors.set(i, new DWDiskSector(this, i, DWVDKDisk.VDK_SECTOR_SIZE, false));
	    	this.sectors.get(i).setData(buf, false);
	    	
	    	
	    }
	    	    
		fis.close();
		
		this.setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
		 
	}


	private static DWVDKDiskHeader readHeader(InputStream fis) throws IOException, DWImageFormatException
	{
	    // read sig and hdr length
	    int readres = 0;
	    byte[] hbuff1 = new byte[4];
	    
	    while (readres < 4)
	    	readres += fis.read(hbuff1, readres, 4 - readres);
	    
	    // size and sanity
	    int headerlen = getHeaderLen(hbuff1);
	    
	    // make complete header buffer
	    byte[] hbuff2 = new byte[headerlen];
	    System.arraycopy(hbuff1, 0, hbuff2, 0, 4);
	    
	    while (readres < headerlen)
	    	readres += fis.read(hbuff2, readres, headerlen  - readres);
	    
	    return(new DWVDKDiskHeader(hbuff2));
	}




	private static int getHeaderLen(byte[] hbuff) throws DWImageFormatException
	{
		 // check sanity
		if (hbuff.length < 4)
			throw new DWImageFormatException("Invalid VDK header: too short to read size bytes");
	
		// check signature
		if (((0xFF & hbuff[0]) != 'd') || ((0xFF & hbuff[1]) != 'k'))
	    	throw new DWImageFormatException("Invalid VDK header: " + hbuff[0] + " " +  hbuff[1]);
		
		// check header length
		int len = (0xFF & hbuff[2]) + ((0xFF & hbuff[3]) * 256);
		
		if (len > DWVDKDisk.VDK_HEADER_SIZE_MAX)
			throw new DWImageFormatException("Invalid VDK header: too big for sanity");
		
		
		return len;
		
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

	

	
	public void writeSector(byte[] data) throws DWDriveWriteProtectedException,	IOException
	{
		if (this.getWriteProtect())
		{
			throw new DWDriveWriteProtectedException("Disk is write protected");
		}
		else
		{
			
			this.sectors.get(this.getLSN()).setData(data);
			
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
		if (fobjsize >= DWVDKDisk.VDK_HEADER_SIZE_MIN)
		{
			int hdrlen;
			
			// getheaderlen checks for sanity
			try
			{
				hdrlen = getHeaderLen(hdr);
			}
			catch (DWImageFormatException e)
			{
				return(DWDefs.DISK_CONSIDER_NO);
			}
			
			
			// make proper sized buffer for hdr
			byte[] buf = new byte[hdrlen];
			System.arraycopy(hdr, 0, buf, 0, hdrlen);
			
			DWVDKDiskHeader header = new DWVDKDiskHeader(buf);
		
			// is the size right?
			if (fobjsize ==  (header.getHeaderLen() + ( header.getSides() * header.getTracks() * DWVDKDisk.VDK_SECTOR_SIZE * DWVDKDisk.VDK_SECTORS_PER_TRACK) ))
			{
				return DWDefs.DISK_CONSIDER_YES;
			}
		
		}
		
		return DWDefs.DISK_CONSIDER_NO;
	}

}
