package com.groupunix.drivewireserver.dwdisk.filesystem;

public class DWRBFFileDescriptor
{
	private int attributes;
	private int owner;
	private byte[] date_modified = new byte[5];
	private byte[] date_created = new byte[3];
	private int link_count;
	private int filesize;
	private DWRBFFileSegment[] segmentlist = new DWRBFFileSegment[48];
	
	public DWRBFFileDescriptor(byte[] data)
	{
		this.setAttributes( (data[0] & 0xFF));
		this.setOwner( (data[1] & 0xFF) * 256 + (data[2] & 0xFF));
		System.arraycopy(data, 3, this.date_modified, 0, 5);
		this.setLink_count(0xFF & data[8]);
		this.setFilesize( (data[9] & 0xFF) * 256 * 256 * 256 + (data[10] & 0xFF) * 256 * 256 + (data[11] & 0xff) * 256 + (data[12] & 0xFF));
		System.arraycopy(data, 13, this.date_created, 0, 3);
		
		for (int i = 0;i<48;i++)
		{
			this.segmentlist[i] = new DWRBFFileSegment(data, 16 + (i*5));
		}
		
	}

	public void setAttributes(int attributes)
	{
		this.attributes = attributes;
	}

	public int getAttributes()
	{
		return attributes;
	}

	public void setOwner(int owner)
	{
		this.owner = owner;
	}

	public int getOwner()
	{
		return owner;
	}

	public void setLink_count(int link_count)
	{
		this.link_count = link_count;
	}

	public int getLink_count()
	{
		return link_count;
	}

	public void setFilesize(int filesize)
	{
		this.filesize = filesize;
	}

	public int getFilesize()
	{
		return filesize;
	}
	
	public DWRBFFileSegment[] getSegmentList()
	{
		return this.segmentlist;
	}

	public byte[] getDateModified()
	{
		return this.date_modified;
	}
	
	public byte[] getDateCreated()
	{
		return this.date_created;
	}

	public boolean isAttr_D()
	{
		if ((this.attributes & 0x80) == 0x80)
			return true;
		
		return false;
	}
	
	public boolean isAttr_S()
	{
		if ((this.attributes & 0x40) == 0x40)
			return true;
		
		return false;
	}
	
	
	public boolean isAttr_PE()
	{
		if ((this.attributes & 0x20) == 0x20)
			return true;
		
		return false;
	}
	
	
	public boolean isAttr_PW()
	{
		if ((this.attributes & 0x10) == 0x10)
			return true;
		
		return false;
	}
	
	public boolean isAttr_PR()
	{
		if ((this.attributes & 0x08) == 0x08)
			return true;
		
		return false;
	}
	
	
	public boolean isAttr_E()
	{
		if ((this.attributes & 0x04) == 0x04)
			return true;
		
		return false;
	}
	
	public boolean isAttr_W()
	{
		if ((this.attributes & 0x02) == 0x02)
			return true;
		
		return false;
	}
	
	public boolean isAttr_R()
	{
		if ((this.attributes & 0x01) == 0x01)
			return true;
		
		return false;
	}
	
}	
