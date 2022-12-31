package com.groupunix.drivewireserver.dwdisk;

import java.nio.ByteBuffer;

import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWDiskFile2Descriptor {

	private ByteBuffer FD;
	private byte[] fdBytes;
	
	public DWDiskFile2Descriptor(byte[] sector)
	{
		this.fdBytes = sector;
		this.FD = ByteBuffer.wrap(fdBytes);
	}
	
	public byte FD_ATT()
	{
		return(fdBytes[0]);
	}
	
	public long FD_OWN()
	{
		byte[] fd_own = new byte[2];
		FD.position(1);
		FD.get(fd_own, 0, 2);
		return(DWUtils.int2(fd_own));
	}
	
	public byte[] FD_DAT()
	{
		byte[] fd_dat = new byte[5];
		FD.position(3);
		FD.get(fd_dat, 0, 5);
		return(fd_dat);
	}
	
	public byte FD_LNK()
	{
		return(fdBytes[8]);
	}
	
	public long FD_SIZ()
	{
		byte[] fd_siz = new byte[4];
		FD.position(9);
		FD.get(fd_siz, 0, 4);
		return(DWUtils.int4(fd_siz));
	}
	
	public byte[] FD_CREAT()
	{
		byte[] fd_creat = new byte[3];
		FD.position(13);
		FD.get(fd_creat, 0, 3);
		return(fd_creat);
	}
	
	public byte[] FD_SEG()
	{
		byte[] fd_seg = new byte[240];
		FD.position(16);
		FD.get(fd_seg, 0, 240);
		return(fd_seg);
	}
	
	
	
}
