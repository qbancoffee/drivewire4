package com.groupunix.drivewireui.nineserver;

import java.util.Vector;

public class OS9BufferGroup
{
	private Vector<OS9Buffer> buffers;
	
	public OS9BufferGroup()
	{
		buffers = new Vector<OS9Buffer>();
		buffers.setSize(256);
		
	}
	
	
	public OS9Buffer getBuffer(int bn)
	{
		return this.buffers.get(bn);
	}


	public void setBuffer(int buf, OS9Buffer curbuffer)
	{
		this.buffers.set(buf,curbuffer);
	}
}
