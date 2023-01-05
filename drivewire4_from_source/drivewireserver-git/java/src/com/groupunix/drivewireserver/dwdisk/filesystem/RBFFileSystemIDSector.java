package com.groupunix.drivewireserver.dwdisk.filesystem;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;

public class RBFFileSystemIDSector
{

	/*
	DD.TOT $00 3 Number of sectors on disk
	DD.TKS $03 1 Track size (in sectors)
	DD.MAP $04 2 Number of bytes in the allocation bit map
	DD.BIT $06 2 Number of sectors per cluster
	DD.DIR $08 3 Starting sector of the root directory
	DD.OWN $0B 2 Owner’s user number
	DD.ATT $0D 1 Disk attributes
	DD.DSK $0E 2 Disk identification (for internal use)
	DD.FMT $10 1 Disk format, density, number of sides
	DD.SPT $11 2 Number of sectors per track
	DD.RES $13 2 Reserved for future use
	DD.BT $15 3 Starting sector of the bootstrap file
	DD.BSZ $18 2 Size of the bootstrap file (in bytes)
	DD.DAT $1A 5 Time of creation (Y:M:D:H:M)
	DD.NAM $1F 32 Volume name in which the last character has the
	most significant bit set
	DD.OPT $3F Path descriptor options
	*/

	private byte[] data;
	private OrderedMap attribs = new LinkedMap();
	
	public RBFFileSystemIDSector(byte[] data)
	{
		this.data = data;
		
		addIntAttrib("DD.TOT", 0, 3);
		addIntAttrib("DD.TKS", 3, 1);
		addIntAttrib("DD.MAP", 4, 2);
		addIntAttrib("DD.BIT", 6, 2);
		addIntAttrib("DD.DIR", 8, 3);
		addIntAttrib("DD.OWN", 11, 2);
		addIntAttrib("DD.ATT", 13, 1);
		addIntAttrib("DD.DSK", 14, 2);
		addIntAttrib("DD.FMT", 16, 1);
		addIntAttrib("DD.SPT", 17, 2);
		addIntAttrib("DD.RES", 19, 2);
		addIntAttrib("DD.BT", 21, 3);
		addIntAttrib("DD.BSZ", 24, 2);
		addIntAttrib("DD.DAT", 26, 5);
		addStrAttrib("DD.NAM", 31);
		
	}
	
	@SuppressWarnings("unchecked")
	private void addStrAttrib(String key, int offset)
	{
		String val = "";
		
		while(  (offset < data.length-1) && ((data[offset] & 0xff) < 128))
		{
			val += (char) (data[offset] & 0xff);
			offset++;
		}
		
		val += (char) ((data[offset] & 0xff) - 128);
		
		attribs.put(key, val);
	}

	@SuppressWarnings("unchecked")
	private void addIntAttrib(String key, int offset, int len)
	{
		int val = 0;
		
		for (int i = 0;i<len;i++)
		{
			val += (data[offset+i] &0xff) << (8*(len - 1 - i));
		}
		
		this.attribs.put(key, val);
	}

	public OrderedMap getAttribs()
	{
		return this.attribs;
	}
	
	public Object getAttrib(String key)
	{
		if (this.attribs.containsKey(key))
		{
			return(this.attribs.get(key));
		}
		
		return null;
	}
	
}
