package com.groupunix.drivewireserver.dwdisk.filesystem;

public class DWRBFFileSegment
{
	private int lsn;
	private int size;
	
	public DWRBFFileSegment(byte[] data, int i)
	{
		this.setLsn((data[i] & 0xff) * 256 * 256 + (data[i+1] & 0xff) * 256 + (data[i+2] & 0xff) );
		this.setSize((data[i+3]  & 0xff) * 256 + (data[i+4]  & 0xff));
	}

	public void setLsn(int lsn)
	{
		this.lsn = lsn;
	}

	public int getLsn()
	{
		return lsn;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}
	
	public boolean isUsed()
	{
		if (this.lsn + this.size > 0)
		{
			return true;
		}
		
		return false;
	}
	
}
