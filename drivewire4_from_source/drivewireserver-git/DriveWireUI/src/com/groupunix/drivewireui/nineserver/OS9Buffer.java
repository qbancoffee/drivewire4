package com.groupunix.drivewireui.nineserver;


public class OS9Buffer
{
	private int format;
	private int xdim;
	private int ydim;
	private byte[] data;
	
	public OS9Buffer(int f, int x, int y, int s)
	{
		this.setFormat(f);
		this.setXdim(x);
		this.setYdim(y);
		this.data = new byte[s];
		
	}
	
	public void setData(byte[] b)
	{
		System.arraycopy(b, 0, this.data, 0, this.data.length);
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	public int getSize()
	{
		return this.data.length;
	}

	public void setFormat(int format)
	{
		this.format = format;
	}

	public int getFormat()
	{
		return format;
	}

	public void setXdim(int xdim)
	{
		this.xdim = xdim;
	}

	public int getXdim()
	{
		return xdim;
	}

	public void setYdim(int ydim)
	{
		this.ydim = ydim;
	}

	public int getYdim()
	{
		return ydim;
	}

	public int getPixel(int x, int y)
	{
		int res = 0;
		int dat = -1;
		
		if (this.format == 8)
		{
			// 4 bits
			
			dat = 0xff & this.data[(( (y * this.xdim) + x) /2 )];
			
			if (x % 2 != 0)
			{
				res = (0x000f & dat);
			}
			else
			{
				res = ((0x00f0 & dat) >> 4);
			}
		}
		
		return res;
	}
	
	
}
