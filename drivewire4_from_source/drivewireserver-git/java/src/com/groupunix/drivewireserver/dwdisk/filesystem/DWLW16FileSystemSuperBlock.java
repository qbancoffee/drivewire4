package com.groupunix.drivewireserver.dwdisk.filesystem;

import java.io.IOException;

import com.groupunix.drivewireserver.dwdisk.DWDiskSector;

public class DWLW16FileSystemSuperBlock
{

	private byte[] magic = new byte[4];			// magic number
	private int	inodes;				// # of inodes
	private int	inodebmpblocks;		// blocks in the inode bitmap
	private int	databmpblocks;		// blocks in the data bitmap
	private int	firstdatablock;		// number of the first data block
	private int	datablocks;			// number of data blocks
	private int	firstinodeblock;	// number of the first inode block


	public DWLW16FileSystemSuperBlock(DWDiskSector sector) throws IOException
	{
		
		System.arraycopy( sector.getData(), 0, magic, 0, 4 ); 
		this.inodes = (0xff & sector.getData()[4]) * 256 + (0xff & sector.getData()[5]);
		this.inodebmpblocks = 0xff & sector.getData()[6];
		this.databmpblocks = 0xff & sector.getData()[7];
		this.firstdatablock = (0xff & sector.getData()[8]) * 256 + (0xff & sector.getData()[9]);
		this.datablocks = (0xff & sector.getData()[10]) * 256 + (0xff & sector.getData()[11]);
		this.firstdatablock = (0xff & sector.getData()[12]) * 256 + (0xff & sector.getData()[13]);
		
	}

	
	public boolean isValid()
	{
		
		// magic number
		if (new String(this.magic).equals("LW16"))
		{
			// fs size
			
			if  ((this.datablocks + this.inodebmpblocks + this.databmpblocks + 1) <= 65536)
			{
				// inodes vs inodebmpblocks

				if (Math.ceil(this.inodes / 8 / 256) == this.inodebmpblocks )
				{
					return true;
				}
				
			}
			
		}
		
		
		return false;
	}
	

	public void setMagic(byte[] magic)
	{
		this.magic = magic;
	}


	public byte[] getMagic()
	{
		return magic;
	}


	public void setInodes(int inodes)
	{
		this.inodes = inodes;
	}


	public int getInodes()
	{
		return inodes;
	}


	public void setInodebmpblocks(int inodebmpblocks)
	{
		this.inodebmpblocks = inodebmpblocks;
	}


	public int getInodebmpblocks()
	{
		return inodebmpblocks;
	}


	public void setDatabmpblocks(int databmpblocks)
	{
		this.databmpblocks = databmpblocks;
	}


	public int getDatabmpblocks()
	{
		return databmpblocks;
	}


	public void setFirstdatablock(int firstdatablock)
	{
		this.firstdatablock = firstdatablock;
	}


	public int getFirstdatablock()
	{
		return firstdatablock;
	}


	public void setDatablocks(int datablocks)
	{
		this.datablocks = datablocks;
	}


	public int getDatablocks()
	{
		return datablocks;
	}


	public void setFirstinodeblock(int firstinodeblock)
	{
		this.firstinodeblock = firstinodeblock;
	}


	public int getFirstinodeblock()
	{
		return firstinodeblock;
	}

}
