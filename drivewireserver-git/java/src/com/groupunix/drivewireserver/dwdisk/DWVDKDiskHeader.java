package com.groupunix.drivewireserver.dwdisk;

public class DWVDKDiskHeader
{
	private byte[] data;
	
	public DWVDKDiskHeader(byte[] hbuff)
	{
		// leave 4 bytes at start just so docs and calls match up
		this.data = new byte[hbuff.length+4];
		System.arraycopy(hbuff, 0, this.data, 4, hbuff.length);

	}

	public int getVersion()
	{
		return(0xFF & data[4]);
	}
	
	public int getVersionCompatible()
	{
		return(0xFF & data[5]);
	}
	
	public int getSourceID()
	{
		return(0xFF & data[6]);
	}
	
	public int getSourceVersion()
	{
		return(0xFF & data[7]);
	}
	
	public int getTracks()
	{
		return(0xFF & data[8]);
	}
	
	public int getSides()
	{
		return(0xFF & data[9]);
	}
	
	public int getFlags()
	{
		return(0xFF & data[10]);
	}
	
	public int getHeaderLen()
	{
		return this.data.length;
	}
	
	public boolean isWriteProtected()
	{
		if ((data[10] & 0x1) == 0x1)
			return true;
		
		return false;
	}

}
