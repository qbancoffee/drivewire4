package com.groupunix.drivewireserver.dwdisk;

public class DWJVCDiskHeader
{
	private byte[] data;
	
	public void setData(byte[] dat)
	{
		this.data = new byte[dat.length];
		System.arraycopy(dat, 0, this.data, 0, dat.length);
	}
	
	public int getSectorsPerTrack()
	{
		if ((data == null) || (data.length < 1))
			return 18;
		
		return(0xFF & data[0]);
	}
	
	public int getSides()
	{
		if ((data == null) || (data.length < 2))
			return 1;
		
		return(0xFF & data[1]);
	}
	
	public int getSectorSize()
	{
	
		if ((data == null) || (data.length < 3))
			return 256;
	
		return (128 << this.data[2]);
	}
	
	
	public int getFirstSector()
	{
		if ((data == null) || (data.length < 4))
			return 1;
		
		return(0xFF & data[3]);
	}
	
	public int getSectorAttributes()
	{
		if ((data == null) || (data.length < 5))
			return 0;
		
		return(0xFF & data[4]);
	}
}
