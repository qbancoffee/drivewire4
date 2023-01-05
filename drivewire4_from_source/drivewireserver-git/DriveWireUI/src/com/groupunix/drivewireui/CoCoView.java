package com.groupunix.drivewireui;




import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.swtdesigner.SWTResourceManager;

public class CoCoView extends Composite 
{


	private int width = 512;
	private int height = 384;

	private int xborder = 5;
	private int yborder = 5;
	private int yfudge = 38;
	private int xfudge = 16;
	
	private int scols = MainWin.config.getInt("BasicViewer_columns", 32);
	private int srows = MainWin.config.getInt("BasicViewer_rows", 16);
	
	private Image cocotext;
	private Image cocotext2;
	private Image cocotext3;
	private Image cocotext3_80;
	
	private Image cocotext_trans;
	private Image cocotext2_trans;
	private Image cocotext3_trans;
	private Image cocotext3_80_trans;
	
	
	private Canvas coco;
	private Image cocoimg;
	private GC cocogc;
	

	private int[][] dtext = new int[srows][scols];
	private int[][] atext;
	private int[][] basictxt;
	private Vector<Integer> linerefs;
	
	private Color[] curscols = new Color[8];


	private int noscalex = -1;
	private int noscaley = -1;
	private boolean[] toggles;
	
	private int basicpos = 0;
	private String[] origtxt;
	
	private String search = null;
	
	
	public CoCoView(Composite parent, int style)
	{
		super(parent, style);
		
		cocotext = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext.png");
		cocotext2 = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext2.png");
		cocotext3 = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext3.png");
		cocotext3_80 = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/cocotext3_80.png");
		

		
		cocotext_trans = new Image(this.getDisplay(), transConv(cocotext.getImageData(), 24));
		cocotext2_trans = new Image(this.getDisplay(), transConv(cocotext2.getImageData(), 24));
		cocotext3_trans = new Image(this.getDisplay(), transConv(cocotext3.getImageData(), 16));
		cocotext3_80_trans = new Image(this.getDisplay(), transConv(cocotext3_80.getImageData(), 16));
		
		toggles = new boolean[10];
		for (int i = 0;i<10;i++)
		{
			toggles[i] = true;
		}
		
		this.scols = MainWin.config.getInt("BasicViewer_columns", scols);
		this.srows = MainWin.config.getInt("BasicViewer_rows", srows);
		
		Point hw = getHWForRes(scols,srows);
		this.width = hw.x;
		this.height = hw.y;
		
		setText( new String[]{ ("DriveWire " + MainWin.DWUIVersion + " (" + MainWin.DWUIVersionDate + ")").toUpperCase() } );

		createContents();
	}

	private ImageData transConv(ImageData imageData, int tall)
	{
		int[] lineData = new int[imageData.width];
		
		for (int y = tall-2; y < imageData.height; y += tall) 
	    {
	    	imageData.getPixels(0,y,imageData.width,lineData,0);
	        
	    	for (int x=0; x<lineData.length; x++)
	    	{
	           imageData.setPixel(x,y, 0xEFEFEF);
	           imageData.setPixel(x,y+1, 0x888888);
	        }
	    	
	    	
	    	
	    }
	    
	    return imageData;
	}

	public Vector<Integer> getLineRefs()
	{
		return this.linerefs;
	}
	
	
	private Point getHWForRes(int cols, int row)
	{
		int w=512;
		int h=384;
		
		if (cols>32)
			w=640;
		
		return(new Point(w,h));
		
	}


	public void setText(String[] txt)
	{
		setText(txt, true);
	}

	public void setText(String[] txt, boolean redraw)
	{
		//long starttime = System.currentTimeMillis();
		
		this.origtxt = txt;
		this.basictxt = generateMap(txt);
		this.atext = searchMap(search);
		this.basicpos  = 0;
		
		if (redraw && this.coco != null)
			coco.redraw();
		
		
	}
	
	
	public void search(String txt)
	{
		this.search = txt;
		int curpos = this.basicpos;
		this.setText(this.origtxt);
		
		int lpos = curpos+1;
		
		while (lpos < this.basictxt.length)
		{
			for (int i = 0;i< scols;i++)
			{
				if (atext[lpos][i] == 0)
				{
					this.basicpos = Math.min(  Math.max(0, basictxt.length - srows), lpos);
					this.updateImg();
					return;
				}	
			}
			lpos++;
		}
		
		lpos = 0;
		
		while (lpos < curpos)
		{
			for (int i = 0;i< scols;i++)
			{
				if (atext[lpos][i] == 0)
				{
					this.basicpos = Math.min(  Math.max(0, basictxt.length - srows), lpos);
					this.updateImg();
					return;
				}	
			}
			lpos++;
		}
		
		
		this.basicpos = Math.min(  Math.max(0, basictxt.length - srows), curpos);
		this.updateImg();
	}
	
	
	private int[][] searchMap(String match)
	{
		int[][] res = new int[ basictxt.length ][scols];
		Vector<Point> mlist = new Vector<Point>();
		
		if (match == null)
		{
			for (int y = 0;y<basictxt.length;y++)
			{
				for (int x = 0;x < scols;x++)
				{
					res[y][x] = 255;
				}
			}
		}
		else
		{
			for (int y = 0;y<basictxt.length;y++)
			{
				for (int x = 0;x < scols;x++)
				{
					res[y][x] = 255;
					
					if (basictxt[y][x] == match.charAt(mlist.size()))
					{
						mlist.add(new Point(x,y));
						
						if (mlist.size() == match.length())
						{
							// full match
							for (int i = 0;i<mlist.size();i++)
							{
								res[mlist.get(i).y][mlist.get(i).x] = 0;
							}
							
							mlist.removeAllElements();
						}
					}
					else
					{
						mlist.removeAllElements();
					}
					
				}
			}
		}
		return res;
	}
	
	private int[][] generateMap(String[] txt)
	{
		int lc = 0;
		
		this.linerefs = new Vector<Integer>();
		dtext = new int[srows][scols];
		
		for (String l:txt)
		{
			lc += (l.length() / scols) + 1;
		}
		
		
		int[][] res = new int[lc][scols];
		
		for (int x = 0;x<scols;x++)
			for (int y = 0;y<lc;y++)
				res[y][x] = 32;
		
		int x = 0;
		int y = 0;
		
		for (String l:txt)
		{
			// line ref
			
			BasicLine bl = new BasicLine(l);
			
			if (bl.getLineno() > -1)
				this.linerefs.add(bl.getLineno());
			
			byte[] bytes = l.getBytes();
			
			for (int i = 0;i<l.length();i++)
			{
				res[y][x] = bytes[i];
				x++;
				
				if (x>=scols)
				{
					x = 0;
					y++;
					this.linerefs.add(-1);
				}
			}
			y++;
			x=0;
		}
		
		return res;
	}









	private void createContents() {
		setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		
		setSize(width + xfudge + xborder*2, height + yfudge + yborder*2);
		
		curscols[0] = new Color(getDisplay(), 0, 255, 0);
		curscols[1] = new Color(getDisplay(), 255, 255, 0);
		curscols[2] = new Color(getDisplay(), 0, 0, 255);
		curscols[3] = new Color(getDisplay(), 255, 0, 0);
		curscols[4] = new Color(getDisplay(), 255, 255, 255);
		curscols[5] = new Color(getDisplay(), 0, 255, 255);
		curscols[6] = new Color(getDisplay(), 255, 0, 255);
		curscols[7] = new Color(getDisplay(), 255, 128, 0);
		
	
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		coco = new Canvas(this, SWT.DOUBLE_BUFFERED);
		
		coco.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e)
			{
			
				 if (e.count> 0)
				 {
					 // up
					 basicpos = Math.max(0, basicpos - e.count);
					
				 }
				 else
				 {
					 // down
					 if (basicpos < (basictxt.length))
					 {
					 	 basicpos = Math.min(  Math.max(0, basictxt.length - srows), basicpos - e.count);
					 }
				 }
		          
		
				 ((Canvas) e.widget).redraw();
				 
			} } );
		
		
		coco.addKeyListener(new KeyListener() 
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
			
				if (e.character == 's')
				{
					System.out.println("test");
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
		} 
		);
		
		// weird bug report from GH.. ?
		if ((MainWin.colorGreen != null) && (coco != null))
			coco.setBackground(MainWin.colorGreen);
		
		cocoimg = new Image(null, width, height);
		this.cocogc = new GC(cocoimg);
		
		coco.addPaintListener(new PaintListener() 
		{
			int wx;
			int hx;
			
			@Override
			
			public void paintControl(PaintEvent e)
			{
				
				e.gc.setTextAntialias(SWT.OFF);
				
				if (MainWin.config.getBoolean("BasicViewer_antialias", true))
				{
					e.gc.setAntialias(SWT.ON);
					e.gc.setAdvanced(true);
				}
				else
				{
					e.gc.setAntialias(SWT.OFF);
					e.gc.setAdvanced(false);
				}
				
				 if (scols != MainWin.config.getInt("BasicViewer_columns", scols))
				 {
				 	 int curpos = basicpos;
					 scols = MainWin.config.getInt("BasicViewer_columns", scols);
					 srows = MainWin.config.getInt("BasicViewer_rows", srows);
					
					Point hw = getHWForRes(scols, srows);
					
					width = hw.x;
					height = hw.y;
					
						 
					 
					 setText(origtxt,false);
					 basicpos = curpos;
					 
					 cocoimg.dispose();
					 cocogc.dispose();
					 
					 cocoimg = new Image(null, width, height);
					 cocogc = new GC(cocoimg);
					
				 }
						 
				
				
				
				genCocoimg();
				
				wx = coco.getBounds().width - (xborder*2);
				hx = coco.getBounds().height - (yborder*2);
				
				
				if ((wx < 64) || (hx < 64))
				{
					e.gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					e.gc.setFont(MainWin.fontGraphLabel);
					e.gc.drawString("I'm small!", 1, 1);
				}
				else
				{
					if (((wx == noscalex) && (hx == noscaley)) || !toggles[8])
					{
						e.gc.drawImage(cocoimg, xborder, yborder);
					}
					else
					{
						
						e.gc.drawImage(cocoimg, 0, 0, width, height, xborder, yborder, wx, hx);
					}
				}

			}
			
		});

	}
	
	

	protected void genCocoimg()
	{
		if (!isDisposed())
		{
			cocogc.setBackground(MainWin.colorGreen);
			cocogc.fillRectangle(0, 0, width, height);
			
			for (int y = 0;y<srows;y++)
			{
				for (int x = 0;x<scols;x++)
				{
					int chr = 32;
					int alf = 255;
					
					if ((y+this.basicpos < this.basictxt.length))
					{
						chr = basictxt[y+this.basicpos][x];
						alf = atext[y+this.basicpos][x];
					}
					
					int xsize = 16;
					int ysize = 24;

					Image charimg = cocotext;
					Point p = new Point(0,0);
					
					if (scols == 32)
					{
						if (chr < 128)
						{
							p = getCharPoint(chr);
							 
							if (alf < 255)
								charimg = cocotext_trans;
							
						}
						else
						{
							if (alf < 255)
								charimg = cocotext2_trans;
							else
								charimg = cocotext2;
							
							p = getGfxPoint(chr);
							
						}
					}
					else if (scols == 40)
					{
						xsize = 16;
						ysize = 16;
						
						if (chr > 127)
							chr = chr - 127;
						
						p = getCharPoint(chr);
						
						if (alf < 255)
							charimg = cocotext3_trans;
						else
							charimg = cocotext3;
					}
					else if (scols == 80)
					{
						xsize = 8;
						ysize = 16;
						
						if (chr > 127)
							chr = chr - 127;
							
						p = getCharPoint(chr);
						
						if (alf < 255)
							charimg = cocotext3_80_trans;
						else
							charimg = cocotext3_80;
					}
					
					cocogc.drawImage(charimg, p.x, p.y , xsize, ysize, x*xsize, y*ysize, xsize, ysize);

					dtext[y][x] = chr;
				}
			}
		}	
	}


	
	

	protected Point getCharPoint(int val)
	{
		Point res = new Point(0,0);
		
		if (val < 32)
			val = 32;
		
		if (scols == 32)
		{
			res.y = (val / 64) * 24;
			res.x = (val % 64) * 16;
		}
		else if (scols == 40)
		{
			res.y = (val / 32) * 16;
			res.x = (val % 32) * 16;
		}
		else if (scols == 80)
		{
			res.y = (val / 32) * 16;
			res.x = (val % 32) * 8;
		}
		
		return res;
	}

	protected Point getGfxPoint(int val)
	{
		val = val - 128;
		
		Point res = new Point(0,0);
		
		res.y = (val / 32) * 24;
		res.x = (val % 32) * 16;
		
		return res;
	}


	public void updateImg()
	{
		this.coco.redraw();
	}

	public void findLine(int ln)
	{
		
		int match = this.linerefs.indexOf(ln);
		
		if (match > -1)
		{
			this.basicpos = Math.min(  Math.max(0, basictxt.length - srows), match +1);  
			this.updateImg();
		}
	}

	
	
	
}
