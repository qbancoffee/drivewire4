package com.groupunix.drivewireserver.dwprotocolhandler;

public class DWRFMPathDescriptor
{
	private byte pdbytes[];

	public DWRFMPathDescriptor(byte[] responsebuf)
	{
		setPdbytes(responsebuf);
	}

	public void setPdbytes(byte pdbytes[])
	{
		this.pdbytes = pdbytes;
	}

	public byte[] getPdbytes()
	{
		return pdbytes;
	}

	public byte getPD()
	{
		return(this.pdbytes[0]);
	}
	
	public byte getMOD()
	{
		return(this.pdbytes[1]);
	}
	
	public byte getCNT()
	{
		return(this.pdbytes[2]);
	}
	
	public byte[] getDEV()
	{
		byte[] tmp = new byte[2];
		
		tmp[0] = this.pdbytes[3];
		tmp[1] = this.pdbytes[4];
		
		return(tmp);
	}
	
	public byte getCPR()
	{
		return(this.pdbytes[5]);
	}
	
	public byte[] getRGS()
	{
		byte[] tmp = new byte[2];
		
		tmp[0] = this.pdbytes[6];
		tmp[1] = this.pdbytes[7];
		
		return(tmp);
	}
	
	public byte[] getBUF()
	{
		byte[] tmp = new byte[2];
		
		tmp[0] = this.pdbytes[8];
		tmp[1] = this.pdbytes[9];
		
		return(tmp);
	}

	public byte[] getFST()
	{
		byte[] tmp = new byte[32];
		System.arraycopy(this.pdbytes, 10, tmp, 0, 32);
		return(tmp);
	}
	
}


