package com.groupunix.drivewireserver.virtualprinter;

public class DWVPrinterFX80Character 
{

	private int[] bits;
	private int len;
	
	public DWVPrinterFX80Character(int[] bits, int len)
	{
		this.bits = bits;
		this.len = len;
	}
	
	public int getCol(int col)
	{
		// get bits for this col 
		
		return(bits[col]);
	}

	public int getLen()
	{
		return len;
	}
	
}