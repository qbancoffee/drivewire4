package com.groupunix.drivewireui;

public class PlatformDef
{
	public String name;
	public int rate;
	public String iconpath;
	public String parity;
	public String stopbits;
	public boolean setRTS;
	public boolean setDTR;
	public boolean useRTSin;
	public boolean useRTSout;
	public boolean tcp = false;
	
	
	// serial 
	public PlatformDef(String n, String p, int r, String par, String sb, boolean rts, boolean dtr, boolean rtsin, boolean rtsout)
	{
		this.name = n;
		this.iconpath = p;
		this.rate = r;
		this.parity = par;
		this.stopbits = sb;
		this.setRTS = rts;
		this.setDTR = dtr;
		this.useRTSin = rtsin;
		this.useRTSout = rtsout;
	}
	
	// tcp
	public PlatformDef(String n, String p)
	{
		this.name = n;
		this.iconpath = p;
		this.tcp = true;
	}
}
