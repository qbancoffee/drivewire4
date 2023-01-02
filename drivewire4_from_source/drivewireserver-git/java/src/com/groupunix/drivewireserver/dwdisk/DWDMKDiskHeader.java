package com.groupunix.drivewireserver.dwdisk;

public class DWDMKDiskHeader
{
	private byte[] header = new byte[16];
	
	public DWDMKDiskHeader(byte[] hdr)
	{
		System.arraycopy(hdr, 0, this.header, 0, 16);
	}
	
	// byte 0 for WP
	public boolean isWriteProtected()
	{
		if (this.header[0] == 0xFF)
			return true;
		return false;
	}
	
	// byte 1 is # tracks
	public int getTracks()
	{
		return(0xFF & this.header[1]);
	}
	
	// byte 3,2 is track length in bytes
	public int getTrackLength()
	{
		return( (0xFF & this.header[3]) * 256 + (0xFF & this.header[2])  );
	}
	
	// byte 4 is option bits
	public int getOptions()
	{
		return(0xFF & this.header[4]);
	}
	
	public boolean isSingleSided()
	{
		// bit 4
		return(((byte)0x10 & this.header[4]) == (byte)0x10);
	}
	
	public boolean isSingleDensity()
	{
		// bit 6
		return(((byte)0x40 & this.header[4]) == (byte)0x40);
	}
	
	// bytes 5-11 reserved
	
	// bytes 12-15 for real disk or not
	public boolean isRealDisk()
	{
		// not sure i've got this right, or if it even matters
		if ( ((0xFF & header[12])==0x12) && ((0xFF & header[13])==0x34) && ((0xFF & header[14])==0x56) && ((0xFF & header[15])==0x78)) 
			return true;
		
		return false;
	}

	public int getSides()
	{
		if (isSingleSided())
			return 1;
		
		return 2;
			
	}

	public Object getDensity()
	{
		if (isSingleDensity())
			return 1;
		
		return 2;
	}
}
