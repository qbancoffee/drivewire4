package com.groupunix.drivewireserver.dwdisk.filesystem;

import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFATException;

public class DWDECBFileSystemDirEntry extends DWFileSystemDirEntry
{
	/*
	Byte Description
	0—7 Filename, which is left justified and blank, filled. If byte0 is 0,
	then the file has been ‘KILL’ed and the directory entry is available
	for use. If byte0 is $FF, then the entry and all following entries
	have never been used.
	8—10 Filename extension
	11 File type: 0=BASIC, 1=BASIC data, 2=Machine language, 3= Text editor
	source
	12 ASCII flag: 0=binary or crunched BASIC, $FF=ASCII
	13 Number of the first granule in the file
	14—15 Number of bytes used in the last sector of the file
	16—31 Unused (future use)
	*/
	
		
	public DWDECBFileSystemDirEntry(byte[] buf)
	{
		super(buf);
	}

	public String getFileName()
	{
		return(new String(data).substring(0, 8));
	}
	
	public String getFileExt()
	{
		return(new String(data).substring(8,11));
	}

	public boolean isUsed()
	{
		if (data[0] == (byte) 255)
			return false;
		
		if (data[0] == (byte) 0)
			return false;
		
		return true;
	}
	
	public boolean isKilled()
	{
		if (data[0] == (byte) 0)
			return true;
		
		return false;
	}
	
	
	
	public int getFileType()
	{
		return(this.data[11] & 0xFF);
	}
	
	public String getPrettyFileType()
	{
		String res = "unknown";
		
		switch(this.data[11])
		{
			case 0:
				return("BASIC");
			case 1:
				return("Data");
			case 2:
				return("ML");
			case 3:
				return("Text");
		}
		
		
		
		return res;
	}
	
	public int getFileFlag()
	{
		return(this.data[12] & 0xFF);
	}
	
	
	public boolean isAscii()
	{
		if (getFileFlag() == 255)
			return true;
		
		return false;
	}
	
	
	public String getPrettyFileFlag()
	{
		String res = "unknown";
		
		if ((this.data[12] & 0xFF) == 255)
			return("ASCII");
		if (this.data[12] == 0)
			return("Binary");
		
		return res;
	}
	
	public byte getFirstGranule()
	{
		return(this.data[13]);
	}
	
	public int getBytesInLastSector() throws DWFileSystemInvalidFATException
	{
		int res = (0xFF & this.data[14])*256 + (0xFF & this.data[15]);
		
		if (res > 256)
			throw new DWFileSystemInvalidFATException("file " + this.getFileName() + "." + this.getFileExt() + " claims to use " + res + " bytes in last sector?");
		
		return(res);
	}

	@Override
	public String getFilePath()
	{
		return null;
	}

	@Override
	public DWFileSystemDirEntry getParentEntry()
	{
		return null;
	}

	@Override
	public boolean isDirectory()
	{
		return false;
	}
	
	
}
