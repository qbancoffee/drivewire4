package com.groupunix.drivewireserver.dwdisk;

public class DWDMKDiskTrack
{
	private byte[] trackdata;
	
	
	public DWDMKDiskTrack(byte[] data)
	{
		this.trackdata = new byte[data.length];
		System.arraycopy(data, 0, this.trackdata, 0, data.length);
		
	}
	
	
	public DWDMKDiskIDAM getIDAM(int index)
	{
		byte msb = this.trackdata[(index*2)+1];
		byte lsb = this.trackdata[(index*2)];
		
		byte[] ibuf = new byte[6];
		System.arraycopy(this.trackdata, getIDAMPtr(msb,lsb)+1 , ibuf, 0, 6);
		
		return(new DWDMKDiskIDAM(msb, lsb, ibuf));
	}

	
	public int getIDAMPtr(byte msb, byte lsb)
	{
		return( (0x3F & msb) * 256 + (0xFF & lsb)  );
	}
	
	
	public byte[] getData()
	{
		return this.trackdata;
	}


	public int getNumSectors()
	{
		int ns = 0;
		for (int i = 0;i<64;i++)
		{
			if (getIDAM(i).getPtr() != 0)
				ns++;
		}
		return ns;
	}
	
	
	
}
