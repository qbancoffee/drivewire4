package com.groupunix.drivewireui.nineserver;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

import com.groupunix.drivewireserver.OS9Defs;

public class OS9TextWindow implements OS9Window
{
	
	private static final int CHRMODE_NONE = 0;
	private static final int CHRMODE_POS1 = 1;
	private static final int CHRMODE_POS2 = 2;
	private static final int CHRMODE_CURSOR = 3;
	private static final int CHRMODE_EXTENDED = 4;
	
	private int chrmode = CHRMODE_NONE;
	
	private byte[] text;
	private byte[] attr;
	
	byte curattr;
	
	int cursorX = 0;
	int cursorY = 0;
	
	int offsetX;
	int offsetY;
	int width;
	int height;
	int CWAoffsetX;
	int CWAoffsetY;
	int CWAwidth;
	int CWAheight;
	
	int cw = 8;
	int ch = 8;
	
	
	
	private boolean textCursorVisible = true;
	private boolean saveBackground = true;

	private int sty;
	private int selectStart = -1;
	private int selectEnd = -1;
	
	ImageData imageData;
	private boolean reverse;
	
	
	
	public OS9TextWindow(int sty, int ofX, int ofY, int w, int h, byte fg, byte bg, boolean sa, ImageData imgdata)
	{
		this.sty = sty;
		
		this.offsetX = this.CWAoffsetX = ofX;
		this.offsetY = this.CWAoffsetY = ofY;
		

		this.saveBackground  = sa;
		this.imageData = imgdata;
	
		this.curattr = (byte) (fg + (bg << 3));
		
				
		if (sty == OS9Defs.STY_Text40)
		{
			cw = 16;
			ch = 8;
			this.width = this.CWAwidth = Math.min(40, w);
			this.height = this.CWAheight = Math.min(24, w);
		}
		else
		{
			cw = 8;
			ch = 8;
			this.width = this.CWAwidth = Math.min(80, w);
			this.height = this.CWAheight = Math.min(24, w);
		}
		
		this.text = new byte[width * height];
		this.attr = new byte[width * height];
		
		for (int i = 0;i<width * height;i++)
		{
			this.text[i] = 32;
			this.attr[i] = curattr;
		}
				
		
		this.cw = 8;
		this.ch = 8;
		
		this.width = this.CWAwidth = Math.min(80, w);
		this.height = this.CWAheight = Math.min(24, w);
		
		for (int i = 0;i<imageData.height;i++)
			for (int j = 0;j<imageData.width;j++)
				imageData.setPixel(j, i, getBGCol());
		
	}

	
	
	/*
	
	public void render(GC srcGC)
	{
		System.out.println("render");
		
		/*
		switch(this.sty)
		{
			case OS9Defs.STY_Text80:
			case OS9Defs.STY_Text40:
				
				// text
				for (int y = this.CWAoffsetY;y<(this.CWAheight);y++)
				{
					for (int x = this.CWAoffsetX;x<(this.CWAwidth);x++)
					{
						drawChr(srcGC, x, y);
					}
				}
			
				// cursor
				if (this.textCursorVisible )
				{
					srcGC.setBackground(cocopal.getColor(this.getFGCol()));
					srcGC.fillRectangle(((this.cursorX + this.CWAoffsetX)*this.cw + 16),((this.cursorY+ this.CWAoffsetY)*this.ch + 8 ) , this.cw, this.ch);
					drawChr(srcGC, cocopal, this.cursorX, this.cursorY, true);
				}
				
				break;
				
			case OS9Defs.STY_GfxHiRes2Col:
			case OS9Defs.STY_GfxHiRes4Col:
				srcGC.drawImage(winImg, this.CWAoffsetX * 8, this.CWAoffsetY * 8, this.CWAwidth * 8, this.CWAheight * 8, this.CWAoffsetX * 8 + 16, this.CWAoffsetY * 8 +8, this.CWAwidth * 8, this.CWAheight * 8);
				break;
				
			case OS9Defs.STY_GfxLoRes4Col:
			case OS9Defs.STY_GfxLoRes16Col:
				
				try
				{
					Image tmp = new Image(Display.getCurrent(), this.imageData);
					
					srcGC.drawImage(tmp, this.CWAoffsetX * 8, this.CWAoffsetY * 8, this.CWAwidth * 8, this.CWAheight * 8, this.CWAoffsetX * 8 + 16, this.CWAoffsetY * 8 +8, this.CWAwidth * 16, this.CWAheight * 8);
					tmp.dispose();
				}
				catch (NullPointerException e)
				{
					System.out.println("null ptr in render");
				}
				
				break;
				
		}
	
		
		
		try
		{
			Image tmp = new Image(Display.getCurrent(), this.imageData);
			
			srcGC.drawImage(tmp, this.CWAoffsetX * 8, this.CWAoffsetY * ch, this.CWAwidth * 8, this.CWAheight * 8, this.CWAoffsetX * 8 + 16, this.CWAoffsetY * 8 +8, this.CWAwidth * cw, this.CWAheight * ch);
			
			tmp.dispose();
		}
		catch (NullPointerException e)
		{
			System.out.println("null ptr in render");
		}
		
	}
	
	
	public void drawChr(GC srcGC, CoCoPalette cocopal, int xx, int yy)
	{
		drawChr( srcGC, cocopal, xx, yy, false);
	}
	
	
	public void drawChr(GC srcGC, CoCoPalette cocopal, int xx, int yy, boolean cursor)
	{
		byte chr = this.text[yy * this.CWAwidth + xx];
		byte atr = this.attr[yy * this.CWAwidth + xx];
		
		if (chr > 0)
		{	
			
			int x = ((xx + this.CWAoffsetX)*cw + 16);
			int y = ((yy + this.CWAoffsetY)*ch + 8);
			
			byte lb;
			
			// inverse
			if ((atr & 0x80) == 0x80)
			{
				srcGC.setBackground(cocopal.getColor(atr & 7));
				srcGC.setForeground(cocopal.getColor((atr & 0x38) >> 3));
			}
			else
			{
				srcGC.setForeground(cocopal.getColor(atr & 7));
				srcGC.setBackground(cocopal.getColor((atr & 0x38) >> 3));
			}
			
			if (cursor)
			{
				Color t = srcGC.getForeground();
				srcGC.setForeground(srcGC.getBackground());
				srcGC.setBackground(t);
			}
			
			// selected
			if ((yy * this.CWAwidth + xx) >= Math.min(this.selectEnd , this.selectStart))
				if ((yy * this.CWAwidth + xx) <= Math.max(this.selectEnd , this.selectStart))
				{
					Color t = srcGC.getForeground();
					srcGC.setForeground(srcGC.getBackground());
					srcGC.setBackground(t);
				}
			
			srcGC.fillRectangle(x, y, cw, ch);
			
			// broken for font height/char height != 8, but look like all are 8
			for (int l = 0;l<ch;l++)
			{
				lb = CoCoFont.getChrLine(chr , l);
					
				for (int p = 7;p>-1;p--)
				{
					if ((lb & 0x01) == 0x01)
					{
						if (cw == 8)
							srcGC.drawPoint(x + p, y + l);
						else if (cw == 16)
						{
							srcGC.drawPoint(x + p*2, y + l);
							srcGC.drawPoint(x + p*2 + 1, y + l);
						}
					}
					
					lb = (byte) (lb >>> 1);
				}
				
			}
			
			// underline
			if ((atr & 0x40) == 0x40)
			{
				for (int i = 0;i<cw;i++)
					srcGC.drawPoint(x + i, y + ch-1);
			}
		}
		else
		{
			System.out.println("unprintable? " + chr);
		}
	}
	
	
	*/
	
	
	public Rectangle addToScreen(byte chr)
	{
		
		int oldX = cursorX;
		int oldY = cursorY;
		
		
		Rectangle res = addChrToWin(chr);
		
		// redraw characters in changed area only
		
		for (int x = res.x/cw; x < res.width/cw + res.x/cw ;x++)
			for (int y = res.y/ch;y < res.height/ch + res.y/ch; y++)
			{
				drawCharacter(this.text[y*this.width + x] , x, y, this.attr[y*this.width + x]);
			}
		
		drawCharacter(this.text[oldY * this.width + oldX] , oldX, oldY, this.attr[oldY * this.width + oldX]);
		drawCharacter(this.text[this.cursorY * this.width + this.cursorX] , this.cursorX, this.cursorY, this.attr[this.cursorY * this.width + this.cursorX]);
		
		res = res.union(new Rectangle(oldX * cw , oldY * ch, this.cw, this.ch));
		res = res.union(new Rectangle(this.cursorX * cw , this.cursorY * ch, this.cw, this.ch));
		
		return(res);
	}
	
	

	public Rectangle addChrToWin(byte chr) 
	{
		this.selectEnd = -1;
		this.selectStart = -1;
		
		Rectangle rect = new Rectangle(0, 0, 0, 0);
		
		switch(this.chrmode )
		{
			case CHRMODE_NONE:
				switch(chr)
				{
					case 0:
						break;
				
					case OS9Defs.CTL_Home:
						cursorX = 0;
						cursorY = 0;
						break;
					
					case OS9Defs.CTL_Position:
						this.chrmode = CHRMODE_POS1;
						break;
						
					case OS9Defs.CTL_EraseLine:	
						for (int i = 0;i<this.CWAwidth;i++)
						{
							this.text[cursorY * this.CWAwidth + i] = 32;
							this.attr[cursorY * this.CWAwidth + i] = curattr;
						}
						
						rect.x = 0;
						rect.y = cursorY*ch;
						rect.height = ch;
						rect.width = CWAwidth*cw;
						
						break;
						
					case OS9Defs.CTL_EraseToEOL:
						for (int i = cursorX;i<this.width;i++)
						{
							this.text[cursorY * this.CWAwidth + i] = 32;
							this.attr[cursorY * this.CWAwidth + i] = curattr;
						}
						
						rect.x = cursorX*cw;
						rect.y = cursorY*ch;
						rect.height = ch;
						rect.width = CWAwidth*cw - rect.x;
						
						break;
						
					case OS9Defs.CTL_CursorOnOff:
						this.chrmode = CHRMODE_CURSOR;
						break;
						
					case OS9Defs.CTL_CursorRight:
						this.cursorX++;
						if (this.cursorX == this.CWAwidth)
						{
							if (this.cursorY < this.CWAheight-1)
							{
								this.cursorY++;
								this.cursorX = 0;
							}
							else
							{
								this.cursorX = this.CWAwidth-1;
							}
						}
						break;
						
					case OS9Defs.CTL_Bell:
						break;
						
					case OS9Defs.CTL_CursorLeft:
						this.cursorX--;
						if (this.cursorX < 0)
						{
							if (this.cursorY > 0)
							{
								this.cursorY--;
								this.cursorX = this.CWAwidth -1;
							}
							else
							{
								this.cursorX = 0;
							}
						}
						break;
						
					case OS9Defs.CTL_CursorUp:
						
						if (cursorY > 0)
							cursorY--;
						break;
						
					case OS9Defs.CTL_CursorDown:
						if (cursorY < this.CWAheight-1)
							cursorY++;
						break;
						
					case OS9Defs.CTL_EraseToEOS:
						for (int i = (cursorY * this.CWAwidth + cursorX);i<(this.CWAheight * this.CWAwidth) ;i++)
						{
							this.text[i] = 32;
							this.attr[i] = curattr;
						}
						
						rect.x = 0;
						rect.y = cursorY*ch;
						rect.height = CWAheight*ch - rect.y;
						rect.width = CWAwidth*cw;
					
						
						break;
						
					case OS9Defs.CTL_ClearScreen:	
						for (int i = 0;i<(this.CWAheight * this.CWAwidth) ;i++)
						{
							this.text[i] = 32;
							this.attr[i] = curattr;
						}
						cursorX = 0;
						cursorY = 0;
						
						rect.height = this.CWAheight*ch;
						rect.width = this.CWAwidth*cw;
						
						break;
						
					case OS9Defs.CTL_CR:
						rect = doCR();
						break;
					
						
					case OS9Defs.CTL_Extended:
						this.chrmode = CHRMODE_EXTENDED;
						break;
						
					default:
						this.text[this.cursorY * this.CWAwidth + this.cursorX] = chr;
						this.attr[this.cursorY * this.CWAwidth + this.cursorX] = curattr;
						
						rect.x = cursorX*cw;
						rect.y = cursorY*ch;
						rect.height = ch;
						rect.width = cw;
						
						rect = rect.union(advanceCursor());
						
						
						break;
				}
				
				break;
				
			case CHRMODE_POS1:
				this.chrmode = CHRMODE_POS2;
				
				if ((0xff & chr) - 0x20 < this.CWAwidth)
					cursorX = Math.max((0xff & chr) - 0x20, 0);
				else
					System.out.println("CANT MOVE TO X = " + ((0xff & chr) - 0x20));
				break;
				
			case CHRMODE_POS2:
				this.chrmode = CHRMODE_NONE;
				
				if ((0xff & chr) - 0x20 < this.CWAheight)
					cursorY = Math.max((0xff & chr) - 0x20, 0);
				else
					System.out.println("CANT MOVE TO Y = " + ((0xff & chr) - 0x20));
				
				break;
				
			case CHRMODE_CURSOR:
				this.chrmode = CHRMODE_NONE;
				if (chr == OS9Defs.CTL_CursorOnOff_Off)
					this.setTextCursorVisible(false);
				else
					this.setTextCursorVisible(true);
				break;
			
			case CHRMODE_EXTENDED:
				this.chrmode = CHRMODE_NONE;
				
				switch(chr)
				{
					case OS9Defs.CTL_Ext_Blink_Off:
					case OS9Defs.CTL_Ext_Blink_On:
						break;
						
					case OS9Defs.CTL_Ext_Delete_Line:
					case OS9Defs.CTL_Ext_Insert_Line:
						break;
						
					case OS9Defs.CTL_Ext_Reverse_Off:
						curattr = (byte) (curattr & ~0x80);
						break;
						
					case OS9Defs.CTL_Ext_Reverse_On:
						curattr = (byte) (curattr | 0x80);
						break;
						
					case OS9Defs.CTL_Ext_Underline_Off:
						curattr = (byte) (curattr & ~0x40);
						break;
						
					case OS9Defs.CTL_Ext_Underline_On:
						curattr = (byte) (curattr | 0x40);
						break;
							
				}
				
				break;
		}
		
		
		return rect;
	}

	


	private void drawCharacter(byte chr, int cx, int cy, byte attr)
	{
		byte lb;
		
		int x = ((cx + this.CWAoffsetX)*cw );
		int y = ((cy + this.CWAoffsetY)*ch );
		
		int c;
		int fg = attr & 0x07;
		int bg = (attr & 0x38) >>> 3;
		
		if (this.reverse) 
		{
			int b = fg;
			fg = bg;
			bg = b;
		}
		
		if ((cx == this.cursorX) && (cy == this.cursorY))
		{
			int b = fg;
			fg = bg;
			bg = b;
		}
		
		if ((cy * this.CWAwidth + cx) >= Math.min(this.selectEnd , this.selectStart))
			if ((cy * this.CWAwidth + cx) <= Math.max(this.selectEnd , this.selectStart))
			{
				int b = fg;
				fg = bg;
				bg = b;
			}
	
		
		for (int l = 0;l<ch;l++)
		{
			lb = CoCoFont.getChrLine(chr , l);
				
			for (int p = 7;p>-1;p--)
			{
				if ((lb & 0x01) == 0x01)
					c = fg;
				else
					c = bg;
					
				
				imageData.setPixel(x + p, y + l, c);
				
				
				lb = (byte) (lb >>> 1);
			}
			
		}
	}
	
	

	private Rectangle advanceCursor() 
	{
		Rectangle rect;
		
		
		cursorX++;
		
		if (this.cursorX == this.CWAwidth)
		{
			this.cursorX = 0;
			this.cursorY++;
		}
		
		if (this.cursorY == this.CWAheight)
		{
			this.cursorY = this.CWAheight - 1;
			rect = doScroll();
		}
		else
		{
			rect = new Rectangle(cursorX*cw, cursorY*ch, cw, ch);
		}
		
	
		
		return rect;
	}



	@SuppressWarnings("unused")
	private Rectangle doLF() 
	{
		this.cursorY++;
	
		Rectangle rect;
		
		if (this.cursorY == this.CWAheight)
		{
			this.cursorY = this.CWAheight - 1;
			rect = doScroll();
		}
		else
		{
			rect = new Rectangle(cursorX*cw, cursorY*ch, cw, ch);
		}
		
		return rect;
	}
	
	private Rectangle doCR()
	{
		this.cursorX = 0;
		
		Rectangle rect = new Rectangle(cursorX*cw, cursorY*ch, cw, ch);
		
		if (this.cursorY == this.CWAheight - 1)
		{
			rect = doScroll();
		}
		
		//rect = rect.union(this.doLF());
		
		return rect;
	}

	
	private Rectangle doScroll() 
	{
		System.arraycopy(this.text, this.CWAwidth, this.text, 0, this.text.length-this.CWAwidth);
		System.arraycopy(this.attr, this.CWAwidth, this.attr, 0, this.attr.length-this.CWAwidth);
		
		for (int x = 0;x < this.width;x++)
		{
			this.text[this.CWAwidth * (this.CWAheight - 1) + x] = 32;
			this.attr[this.CWAwidth * (this.CWAheight - 1) + x] = curattr;
		}
		
		return(new Rectangle(this.CWAoffsetX*cw, this.CWAoffsetY*ch, this.CWAwidth*cw, this.CWAheight*ch));
		//return(new Rectangle(this.CWAoffsetX*cw, this.CWAheight*ch - ch, this.CWAwidth*cw,ch));

	}

	
	
	public int getFGCol()
	{
		return (this.curattr & 0x07);
	}

	public int getBGCol()
	{
		return (this.curattr & 0x38) >>> 3;
	}
	
	public void setFGCol(int f)
	{
		this.curattr = (byte) ((this.curattr & 0xf8) + f);
	}
	
	public void setBGCol(int b)
	{
		this.curattr = (byte) ((this.curattr & 0xc7) + (b << 3));
	}
	

	public boolean isUnderline()
	{
		if ((this.curattr & 0x40) == 0x40)
			return true;
		return false;
	}
	
	public boolean isReverse()
	{
		if ((this.curattr & 0x80) == 0x80)
			return true;
		return false;
	}
	

	public int getWidth()
	{
		return this.width;
	}


	public int getHeight()
	{
		return this.height;
	}

	public boolean isSave()
	{
		return this.saveBackground;
	}

	public int getOffsetY()
	{
		return this.offsetY;
	}
	
	public int getOffsetX()
	{
		return this.offsetX;
	}

	public byte getText(int x, int y)
	{
		return this.text[y * this.width + x];
	}
	
	public byte getAttr(int x, int y)
	{
		return this.attr[y * this.width + x];
	}
	
	public void setText(int x, int y, byte b)
	{
		this.text[y * this.width + x] = b;
	}
	
	public void setAttr(int x, int y, byte b)
	{
		this.attr[y * this.width + x] = b;
	}

	
	public int getCursorX()
	{
		return this.cursorX;
	}
	
	public int getCursorY()
	{
		return this.cursorY;
	}

	

	public void setCWA(Byte ofX, Byte ofY, Byte w, Byte h)
	{
		this.CWAoffsetX = ofX;
		this.CWAoffsetY = ofY;
		this.CWAwidth = w;
		this.CWAheight = h;
		
	}

	
	
	

	
	
	


	public int getSTY()
	{
		return this.sty;
	}

	public void mouseDown(Rectangle bounds, int x, int y)
	{
		this.selectEnd = -1;
		this.selectStart = -1;
		
		
		for (int xx = this.CWAoffsetX; xx < this.CWAwidth;xx++)
			for (int yy = this.CWAoffsetY;yy < this.CWAheight; yy++)
			{
				drawCharacter(this.text[yy*this.width + xx] , xx, yy, this.attr[yy*this.width + xx]);
			}
		
		
	}
	
	public void mouseUp(Rectangle bounds, int x, int y)
	{
		
	}
	
	public void moveSelect(Rectangle bounds, int x, int y)
	{
		if (this.selectStart < 0)
			this.selectStart = getChrPosFromCoord(bounds, x,y);
		
		this.selectEnd = getChrPosFromCoord(bounds, x,y);
		
		for (int xx = this.CWAoffsetX; xx < this.CWAwidth;xx++)
			for (int yy = this.CWAoffsetY;yy < this.CWAheight; yy++)
			{
				drawCharacter(this.text[yy*this.width + xx] , xx, yy, this.attr[yy*this.width + xx]);
			}
		
		
	}

	private int getChrPosFromCoord(Rectangle bounds, int x, int y)
	{
		double fx = Double.valueOf(this.width * cw) / Double.valueOf(bounds.width);
		double fy = Double.valueOf(this.height * ch) / Double.valueOf(bounds.height);
		
		int ax = (int) (fx * x) / cw;
		int ay = (int) (fy * y) / ch;
		
		return (ay * this.width + ax);
	}

	public String getSelected()
	{
		if ((this.selectEnd > -1) && (this.selectStart > -1))
		{
			int len = Math.abs(this.selectEnd - this.selectStart);
			int str = Math.min(this.selectEnd, this.selectStart);
			
			String res = "";
			String tmp = "";
			
			for (int i = str;i <= (str + len);i++)
			{
				if (i % this.CWAwidth == 0)
				{
					res += tmp.trim() + System.getProperty("line.separator");
					tmp = "";
				}
				
				if (i < this.text.length)
					tmp += (char) this.text[i];
			}
			
			res += tmp.trim();
			
			this.selectEnd = -1;
			this.selectStart = -1;
			
			
			for (int xx = this.CWAoffsetX; xx < this.CWAwidth;xx++)
				for (int yy = this.CWAoffsetY;yy < this.CWAheight; yy++)
				{
					drawCharacter(this.text[yy*this.width + xx] , xx, yy, this.attr[yy*this.width + xx]);
				}
			
			
			return(res);
			
			
		}
		return null;
	}






	public Rectangle getBounds()
	{
		return new Rectangle(this.CWAoffsetX*cw, this.CWAoffsetY*ch, this.CWAwidth*cw, this.CWAheight*ch);
	}





	@Override
	public int getCWAOffsetX()
	{
		return this.CWAoffsetX;
	}





	@Override
	public int getCWAOffsetY()
	{
		return this.CWAoffsetY;
	}





	@Override
	public int getCWAWidth()
	{
		return this.CWAwidth;
	}





	@Override
	public int getCWAHeight()
	{
		return this.CWAheight;
	}





	@Override
	public int getCharWidth()
	{

		return cw;
	}





	@Override
	public int getCharHeight()
	{

		return ch;
	}



	public boolean isTextCursorVisible() {
		return textCursorVisible;
	}



	public void setTextCursorVisible(boolean textCursorVisible) {
		this.textCursorVisible = textCursorVisible;
	}


}
