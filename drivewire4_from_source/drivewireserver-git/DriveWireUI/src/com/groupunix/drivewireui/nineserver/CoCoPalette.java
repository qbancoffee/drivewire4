package com.groupunix.drivewireui.nineserver;

import org.eclipse.swt.graphics.Color;

import com.groupunix.drivewireui.MainWin;

public class CoCoPalette
{
	private Color[] colors = new Color[16];
	
	public CoCoPalette()
	{
		initPalette();
	}
	
	public void initPalette()
	{
		// white
		this.setColor(0, (byte) 63);
		this.setColor(8, (byte) 63);
		
		// blue
		this.setColor(1, (byte) 9);
		this.setColor(9, (byte) 9);
		
		// black
		this.setColor(2, (byte) 0);
		this.setColor(10, (byte) 0);
		
		// green
		this.setColor(3, (byte) 18);
		this.setColor(11, (byte) 18);
		
		// red
		this.setColor(4, (byte) 36);
		this.setColor(12, (byte) 36);
		
		// yellow
		this.setColor(5, (byte) 54);
		this.setColor(13, (byte) 54);
		
		// magenta
		this.setColor(6, (byte) 45);
		this.setColor(14, (byte) 45);
		
		// cyan
		this.setColor(7, (byte) 27);
		this.setColor(15, (byte) 27);
		
	}
	
	public Color getColor(int c)
	{
		if (c < this.colors.length)
			return(this.colors[c]);
		
		System.out.println("req for invalid palette slot " + c);
		
		return(this.colors[0]);
	}
	
	public void setColor(int c, byte d)
	{
		/* 
		Bit Color
		0 	Blue low
		1 	Green low
		2 	Red low
		3 	Blue high
		4 	Green high
		5 	Red high
		6 	unused
		7 	unused
		*/
		
			
		if (c < this.colors.length)
		{
			int b = ((d & 0x01) + ((d & 0x08) >> 2)) * 64;
			if (b > 0)
				b += 63;
			d = (byte) (d >>> 1);
			int g = ((d & 0x01) + ((d & 0x08) >> 2)) * 64;
			if (g > 0)
				g += 63;
			d = (byte) (d >>> 1);
			int r = ((d & 0x01) + ((d & 0x08) >> 2)) * 64;
			if (r > 0)
				r += 63;
			
			
			
			this.colors[c] = new Color(MainWin.getDisplay(), r, g, b);
		}
	}

	public int getColorVal(Byte c)
	{
		return this.colors[c].getRed() * 256 * 256 + this.colors[c].getGreen() * 256 + this.colors[c].getBlue();
	}
	
}


