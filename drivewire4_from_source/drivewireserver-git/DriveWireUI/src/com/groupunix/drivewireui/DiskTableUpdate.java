package com.groupunix.drivewireui;

import org.eclipse.swt.graphics.Image;

public class DiskTableUpdate
{
	private Object value;
	private int disk;
	private String key;
	
	public DiskTableUpdate(int disk, String key, String val)
	{
		
		this.disk = disk;
		this.key = key;
		
		if (val == null)
			this.value = "";
		else
			this.value = val;

	}

	public DiskTableUpdate(int disk, String key, Image img)
	{
		this.disk = disk;
		this.key = key;
		
		this.value = img;

	}
	
	
	public String getKey()
	{
		return this.key;
	}
	
	public Object getValue()
	{
		return value;
	}

	public int getDisk()
	{
		// TODO Auto-generated method stub
		return disk;
	}
	
	
	
}
