package com.groupunix.drivewireserver.dwdisk;

public class DWDMKDiskIDAM
{
	
	private byte[] data;
	private byte idamptr_msb;
	private byte idamptr_lsb;


	public DWDMKDiskIDAM(byte msb, byte lsb, byte[] data)
	{
		this.data = data;
		this.idamptr_msb = msb;
		this.idamptr_lsb = lsb;
	}
	
	public int getTrack()
	{
		return(0xFF & data[0]);
	}
	
	public int getSide()
	{
		return(0xFF & data[1]);
	}
	
	public int getSector()
	{
		return(0xFF & data[2]);
	}
	
	public int getSectorSize()
	{
		
		return (int) (java.lang.Math.pow(2,(7 + data[3]) ));
	}
	
	public boolean isDoubleDensity()
	{
		return((0x3F & this.idamptr_msb) == 0x3F);
	}

	public int getPtr()
	{
		return( (0x3F & this.idamptr_msb) * 256 + (0xFF & this.idamptr_lsb)  );
	}
	

}
