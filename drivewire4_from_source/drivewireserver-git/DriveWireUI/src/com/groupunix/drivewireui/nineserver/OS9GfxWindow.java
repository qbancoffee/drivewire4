package com.groupunix.drivewireui.nineserver;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

import com.groupunix.drivewireserver.OS9Defs;
import com.groupunix.drivewireui.MainWin;

public class OS9GfxWindow implements OS9Window
{
	
	private static final int CHRMODE_NONE = 0;
	private static final int CHRMODE_POS1 = 1;
	private static final int CHRMODE_POS2 = 2;
	private static final int CHRMODE_CURSOR = 3;
	private static final int CHRMODE_EXTENDED = 4;
	
	private int chrmode = CHRMODE_NONE;

	
	int cursorX = 0;
	int cursorY = 0;
	
	int gCursorX = 0;
	int gCursorY = 0;
	
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
	
	private boolean boldSw = false;
	private boolean propSw = false;
	private boolean protectSw = true;
	private boolean scaleSw = true;
	private boolean transparentSw = true;
	
	private byte fontGrp = (byte)200;
	private byte fontBuf = 1;
	private byte gcurGrp = 0;
	private byte gcurBuf = 0;
	private byte patGrp = 0;
	private byte patBuf = 0;
	
	private byte logic = 0;
	private int sty;
	private int selectStart = -1;
	private int selectEnd = -1;

	ImageData imageData;
	private boolean underline = false;
	private boolean reverse = false;
	private int fgcol = 1;
	private int bgcol = 0;
	
	
	
	public OS9GfxWindow(int sty, int ofX, int ofY, int w, int h, byte fg, byte bg, boolean sa, ImageData imgdata)
	{
		this.sty = sty;
		this.offsetX = this.CWAoffsetX = ofX;
		this.offsetY = this.CWAoffsetY = ofY;
		
		this.fgcol = fg;
		this.bgcol = bg;

		this.saveBackground  = sa;

	
		
		switch(sty)
		{
		
			case OS9Defs.STY_GfxLoRes16Col:
			case OS9Defs.STY_GfxLoRes4Col:
				cw = 8;
				ch = 8;
				this.width = this.CWAwidth = Math.min(40, w);
				this.height = this.CWAheight = Math.min(24, h);
				break;
			
			case OS9Defs.STY_GfxHiRes2Col:
			case OS9Defs.STY_GfxHiRes4Col:
				cw = 8;
				ch = 8;
				this.width = this.CWAwidth = Math.min(80, w);
				this.height = this.CWAheight = Math.min(24, h);
				break;
				
		}
		
	
		
		this.imageData = imgdata;
		
	
		
		for (int i = CWAoffsetX *cw; i < imageData.width && i < (CWAoffsetX*cw + CWAwidth*cw); i++)
			for (int j = CWAoffsetY * ch;j<imageData.height && j<(CWAoffsetY*ch + CWAheight*ch);j++)
				imageData.setPixel(i,j, getBGCol());

	}

	

	
	
	/*
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
		Rectangle rect = new Rectangle(0, 0, 0, 0);
		
		this.selectEnd = -1;
		this.selectStart = -1;
		
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
						
						for (int i = 0;i<this.CWAwidth*cw;i++)
						{
							for (int j = this.cursorY*ch;j<this.cursorY*ch+ch;j++)
							{
								this.imageData.setPixel(i, j, getBGCol());
							}
						}
						
						rect.x = 0;
						rect.y = cursorY*ch;
						rect.height = ch;
						rect.width = CWAwidth*cw;
						
						break;
						
					case OS9Defs.CTL_EraseToEOL:
						
						for (int i = cursorX*cw;i<this.CWAwidth*cw;i++)
						{
							for (int j = this.cursorY*ch;j<this.cursorY*ch+ch;j++)
							{
								this.imageData.setPixel(i, j, getBGCol());
							}
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
						
						for (int i = cursorX*cw;i<this.CWAwidth*cw;i++)
						{
							for (int j = this.cursorY*ch;j<this.cursorY*ch+ch;j++)
							{
								this.imageData.setPixel(i, j, getBGCol());
							}
						}
							
						for (int i = 0;i<this.CWAwidth*cw;i++)
						{
							for (int j = this.cursorY*ch+ch;j<this.CWAheight*ch;j++)
							{
								this.imageData.setPixel(i, j, getBGCol());
							}
						}	
						
						rect.x = 0;
						rect.y = cursorY*ch;
						rect.height = CWAheight*ch - rect.y;
						rect.width = CWAwidth*cw;
						
						break;
						
					case OS9Defs.CTL_ClearScreen:
						int bgc = getBGCol();
						for (int i = this.CWAoffsetX * cw; i< this.CWAwidth * cw;i++)
							for (int j = this.CWAoffsetY * ch; j<this.CWAheight * ch;j++)
							{
								imageData.setPixel(i, j, bgc);
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
						//this.text[this.cursorY * this.CWAwidth + this.cursorX] = chr;
						//this.attr[this.cursorY * this.CWAwidth + this.cursorX] = curattr;
						drawCharacter(chr, this.cursorX, this.cursorY, this.fgcol, this.bgcol);
						
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
					this.textCursorVisible = false;
				else
					this.textCursorVisible = true;
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
						this.reverse = false;
						break;
						
					case OS9Defs.CTL_Ext_Reverse_On:
						this.reverse = true;
						break;
						
					case OS9Defs.CTL_Ext_Underline_Off:
						this.underline = false;
						break;
						
					case OS9Defs.CTL_Ext_Underline_On:
						this.underline = true;
						break;
							
				}
				
				break;
		}
		
		return rect;
	}


	
	private void drawCharacter(byte chr, int cx, int cy, int fgc, int bgc)
	{
		byte lb;
		
		int x = ((cx + this.CWAoffsetX)*cw );
		int y = ((cy + this.CWAoffsetY)*ch );
		
		int c;
		int fg = fgc;
		int bg = bgc;
		
		if (this.reverse) 
		{
			fg = bgc;
			bg = fgc;
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
		
		return(rect);
	}



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
		
		rect = rect.union(this.doLF());
		
		return rect;
	}

	
	private Rectangle doScroll() 
	{ 
		
		int w = this.CWAwidth*cw;
		
		for (int l = 0; l < this.CWAheight*ch - ch;l++)
			this.imageData.setPixels(0, l, w, this.imageData.data , w*(l+ch) );
		
		
		for (int i = 0;i<this.CWAwidth*cw;i++)
		{
			for (int j = this.cursorY*ch;j<this.cursorY*ch+ch;j++)
			{
				this.imageData.setPixel(i, j, getBGCol());
			}
		}
		
		return(new Rectangle(this.CWAoffsetX*cw, this.CWAoffsetY*ch, this.CWAwidth*cw, this.CWAheight*ch));
	}

	 
	
	public int getFGCol()
	{
		return (this.fgcol);
	}

	public int getBGCol()
	{
		return (this.bgcol);
	}

	public void setFGCol(int f)
	{
		this.fgcol = f;
	}
	
	public void setBGCol(int b)
	{
		this.bgcol = b;
	}
	
	public boolean isUnderline()
	{
		return this.underline;
	}
	
	public boolean isReverse()
	{
		return this.reverse;
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

	

	public void setBoldSw(boolean boldSw)
	{
		this.boldSw = boldSw;
	}

	public boolean isBoldSw()
	{
		return boldSw;
	}

	public void setPropSw(boolean propSw)
	{
		this.propSw = propSw;
	}

	public boolean isPropSw()
	{
		return propSw;
	}

	public void setProtectSw(boolean protectSw)
	{
		this.protectSw = protectSw;
	}

	public boolean isProtectSw()
	{
		return protectSw;
	}

	public void setScaleSw(boolean scaleSw)
	{
		this.scaleSw = scaleSw;
	}

	public boolean isScaleSw()
	{
		return scaleSw;
	}

	public void setTransparentSw(boolean transparentSw)
	{
		this.transparentSw = transparentSw;
	}

	public boolean isTransparentSw()
	{
		return transparentSw;
	}

	public void setFontGrp(byte fontGrp)
	{
		this.fontGrp = fontGrp;
	}

	public int getFontGrp()
	{
		return fontGrp;
	}

	public void setFontBuf(byte fontBuf)
	{
		this.fontBuf = fontBuf;
	}

	public int getFontBuf()
	{
		return fontBuf;
	}

	public void setGcurGrp(byte gcurGrp)
	{
		this.gcurGrp = gcurGrp;
	}

	public int getGcurGrp()
	{
		return gcurGrp;
	}

	public void setGcurBuf(byte gcurBuf)
	{
		this.gcurBuf = gcurBuf;
	}

	public int getGcurBuf()
	{
		return gcurBuf;
	}

	public void setPatGrp(byte patGrp)
	{
		this.patGrp = patGrp;
	}

	public int getPatGrp()
	{
		return patGrp;
	}

	public void setPatBuf(byte patBuf)
	{
		this.patBuf = patBuf;
	}

	public int getPatBuf()
	{
		return patBuf;
	}

	public void setLogic(byte logic)
	{
		this.logic = logic;
	}

	public int getLogic()
	{
		return logic;
	}

	public int getCursorX()
	{
		return this.cursorX;
	}
	
	public int getCursorY()
	{
		return this.cursorY;
	}

	public int getGCursorX()
	{
		return this.gCursorX;
	}
	
	public int getGCursorY()
	{
		return this.gCursorY;
	}

	public void setCWA(Byte ofX, Byte ofY, Byte w, Byte h)
	{
		this.CWAoffsetX = ofX;
		this.CWAoffsetY = ofY;
		this.CWAwidth = w;
		this.CWAheight = h;
		
	}

	
	
	
	
	public Rectangle drawBar(Byte hx, Byte lx, Byte hy, Byte ly, boolean fill)
	{
		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		
		x = Math.min(this.gCursorX, get2bv(hx,lx) );
		w = Math.max(get2bv(hx,lx),this.gCursorX);
		
		y = Math.min(this.gCursorY, get2bv(hy,ly) );
		h = Math.max(get2bv(hy,ly),this.gCursorY);
		
		int fc = getFGCol();
		
		if (fill)
		{
			
			for (int i = x;i<w && i <imageData.width;i++)
				for (int j= y;j<=h && j < imageData.height ;j++)
					imageData.setPixel(i, j, fc);
			
		}
		else
		{
			
			for (int i = x;(i<w && i<imageData.width);i++)
			{
				if (y < imageData.height)
				{
					imageData.setPixel(i, y, fc);
					if (h < imageData.height)
					{
						imageData.setPixel(i, h, fc);
					}
				}
			}
			
			for (int j = y;(j<h && j<imageData.height);j++)
			{
				if (x < imageData.width)
				{
					imageData.setPixel(x, j, fc);
					if (w < imageData.width)
					{
						imageData.setPixel(w, j, fc);
						imageData.setPixel(w-16, j, fc);
					}
				}
			}
		}
		
		return(new Rectangle(x,y,w,h));
	}
	
	
	public Rectangle drawRBar(Byte hx, Byte lx, Byte hy, Byte ly, boolean fill)
	{

		int x = get2bv(hx,lx) + this.gCursorX;
		int y = get2bv(hy,ly) + this.gCursorY;
		int w = Math.abs(get2bv(hx,lx));
		int h = Math.abs(get2bv(hy,ly));
		
		int fc = getFGCol();
		
		if (fill)
		{
			
			for (int i = x;i<w && i <imageData.width;i++)
				for (int j= y;j<=h && j < imageData.height ;j++)
					imageData.setPixel(i, j, fc);
			
		}
		else
		{
			
			for (int i = x;(i<w && i<imageData.width);i++)
			{
				if (y < imageData.height)
				{
					imageData.setPixel(i, y, fc);
					if (h < imageData.height)
					{
						imageData.setPixel(i, h, fc);
					}
				}
			}
			
			for (int j = y;(j<h && j<imageData.height);j++)
			{
				if (x < imageData.width)
				{
					imageData.setPixel(x, j, fc);
					if (w < imageData.width)
					{
						imageData.setPixel(w, j, fc);
						imageData.setPixel(w-16, j, fc);
					}
				}
			}
		}
		
		return(new Rectangle(x,y,w,h));
		
	}
	
	
	
	public void drawPoint(Byte hx, Byte lx, Byte hy, Byte ly, boolean rel)
	{
			/*
			GC winGC = new GC(this.winImg);
			
			int x;
			int y;
			
			if (rel)
			{
				x = this.gCursorX + get2bv(hx,lx);
				y = this.gCursorY + get2bv(hy,ly);
			}
			else
			{
				x = get2bv(hx,lx);
				y = get2bv(hy,ly);
			}
			
			winGC.setForeground(cocopal.getColor(getFGCol()));
			winGC.drawPoint(x, y);
			
			winGC.dispose();
				*/
		
	}
	
	
	
	public void drawLine(Byte hx, Byte lx, Byte hy, Byte ly, boolean rel, boolean move)
	{

			/*
			GC winGC = new GC(this.winImg);
			
			int x = 0;
			int y = 0;
			
			if (rel)
			{
				x = this.gCursorX + get2bv(hx,lx);
				y = this.gCursorY + get2bv(hy,ly);
			}
			else
			{
				x = get2bv(hx,lx);
				y = get2bv(hy,ly);
			}
			
			winGC.setForeground(cocopal.getColor(getFGCol()));
			winGC.drawLine(this.gCursorX, this.gCursorY, x, y);
			
			if (move)
			{
				this.gCursorX = x;
				this.gCursorY = y;
			}
			
			winGC.dispose();
			*/
		
	}
	
	
	
	public void drawEllipse(Byte hx, Byte lx, Byte hy, Byte ly)
	{
			/*
			GC winGC = new GC(this.winImg);
			
			winGC.setForeground(cocopal.getColor(getFGCol()));
			winGC.drawOval(this.gCursorX - get2bv(hx,lx), this.gCursorY - get2bv(hy,ly), get2bv(hx,lx)*2  , get2bv(hy,ly)*2);
			
			winGC.dispose();
			*/
		
	}
	
	public void drawArc(Byte hrx, Byte lrx, Byte hry, Byte lry, Byte h1x, Byte l1x, Byte h1y, Byte l1y, Byte h2x, Byte l2x, Byte h2y, Byte l2y  )
	{
			/*
			GC winGC = new GC(this.winImg);
			
			winGC.setForeground(cocopal.getColor(getFGCol()));
			
			// TODO - limit drawing to line coords
			//winGC.drawArc(x, y, CWAwidth, CWAheight, startAngle, arcAngle)
			
			winGC.drawOval(this.gCursorX - get2bv(hrx,lrx), this.gCursorY - get2bv(hry,lry), get2bv(hrx,lrx)*2  , get2bv(hry,lry)*2);
			
			winGC.dispose();
			*/
		
	}
	
	
	public void setDrawPtr(Byte hx, Byte lx, Byte hy, Byte ly, boolean rel)
	{
		if (rel)
		{
			this.gCursorX = this.gCursorX + get2bv(hx,lx);
			this.gCursorY = this.gCursorY + get2bv(hy,ly);
		}
		else
		{
			this.gCursorX = get2bv(hx,lx);
			this.gCursorY = get2bv(hy,ly);
		}
	}
	
	
	
	
	public Rectangle putBlk(Byte grp, Byte buf, Byte hx, Byte lx, Byte hy, Byte ly)
	{
		Rectangle rect = new Rectangle(0,0,0,0);
		

		if (MainWin.os9BufferGroups.get(0xff & grp) != null)
		{
			OS9Buffer os9buf = MainWin.os9BufferGroups.get(0xff & grp).getBuffer(0xff & buf);
			
			if (os9buf != null)
			{
				//System.out.println("put " + (0xff & grp) + ":" + (0xff & buf) + " at " + get2bv(hx,lx) + "," + get2bv(hy,ly) + "  size " + os9buf.getXdim() + "x" + os9buf.getYdim() + " format " + os9buf.getFormat());
				
				rect.x = Math.min(CWAwidth*cw-1, get2bv(hx,lx,false));
				rect.y = Math.min(CWAheight*ch-1, get2bv(hy,ly,false));
				
				rect.width = Math.min(CWAwidth*cw -1 - rect.x, imageData.width);
				rect.height = Math.min(CWAheight*ch -1 - rect.y, imageData.height);
				
				for (int y = 0;y < os9buf.getYdim();y++)
				{
					for (int x = 0;x < os9buf.getXdim();x++)
					{
						
						if (x + get2bv(hx,lx,false) < imageData.width)
							if (y + get2bv(hy,ly,false) < imageData.height )
								imageData.setPixel(x + get2bv(hx,lx,false), y + get2bv(hy,ly,false), os9buf.getPixel(x,y));
						
					
						
					}
				}
				
				
				
			}
			else
				System.out.println("null buffer " + (0xff & buf));
		}
		else
			System.out.println("null buffer group " + (0xff & grp));
		
	
	
		
		return rect;
	}
	
	
	
	private int get2bv(Byte hx, Byte lx)
	{
		return get2bv(hx,lx,false);
	}

	private int get2bv(Byte hx, Byte lx, boolean sc)
	{
		if ((this.scaleSw) && (this.width < 80) && sc)
		{
			return  (int) ( (hx * 256.0 + (0xff & lx)) / (this.width / 80.0));
		}
		
		return  hx * 256 + (0xff & lx);
	}



	public int getSTY()
	{
		return this.sty;
	}

	public void mouseDown(Rectangle bounds, int x, int y)
	{
		this.selectEnd = -1;
		this.selectStart = -1;
	}
	
	public void mouseUp(Rectangle bounds, int x, int y)
	{
		
	}
	
	public void moveSelect(Rectangle bounds, int x, int y)
	{
		if (this.selectStart < 0)
			this.selectStart = getChrPosFromCoord(bounds, x,y);
		
		this.selectEnd = getChrPosFromCoord(bounds, x,y);

	}

	private int getChrPosFromCoord(Rectangle bounds, int x, int y)
	{
		double fx = Double.valueOf(672.0) / Double.valueOf(bounds.width);
		double fy = Double.valueOf(208.0) / Double.valueOf(bounds.height);
		
		int ax = (int) (fx * x - 16) / cw;
		int ay = (int) (fy * y - 8) / ch;
		
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
				
			//	if (i < this.text.length)
			//		tmp += (char) this.text[i];
			}
			
			res += tmp.trim();
			
			this.selectEnd = -1;
			this.selectStart = -1;
			
			return(res);
			
			
		}
		return null;
	}






	public boolean isTextCursorVisible()
	{
		
		return this.textCursorVisible;
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

	

/*
	public void setPalette(Byte cn, RGB rgb)
	{
		image.getImageData().palette.colors[cn] = rgb;
	}
*/



}
