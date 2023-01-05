package com.groupunix.drivewireui.nineserver;



import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.OS9Defs;
import com.groupunix.drivewireui.MainWin;



public class NineScreen extends Composite
{

	private static final int CMDMODE_NONE = 0;
	private static final int CMDMODE_GETCMD = 1;
	private static final int CMDMODE_GETARGS = 2;
	private static final int CMDMODE_DWEXT = 3;
	private static final int CMDMODE_DATA = 4;
	
	
	
	static final int BORDER_WIDTH = 8;
	

	protected static final int TITLE_BAR_HEIGHT = 20;
	
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 384;
	 
	
	// white blue black green red yellow magenta cyan x2
	
	private RGB[] DEFAULT_PALETTE = new RGB[]{ 
		new RGB(255,255,255),
		new RGB(0,0,255),
		new RGB(0,0,0),
		new RGB(0,255,0),
		new RGB(255,0,0),
		new RGB(255,255,0),
		new RGB(255,0,255),
		new RGB(0,255,255),
		new RGB(255,255,255),
		new RGB(0,0,255),
		new RGB(0,0,0),
		new RGB(0,255,0),
		new RGB(255,0,0),
		new RGB(255,255,0),
		new RGB(255,0,255),
		new RGB(0,255,255) };
	
	

		
	private Socket skt;
	private CTabItem ctabitem = null;
	private int cmdmode = CMDMODE_NONE;
	private int argbytes = 0;
	private Vector<Byte> args = new Vector<Byte>();
	private byte os9cmd = 0;
	private byte dwcmd = 0;
	private ImageData srcImgData;
	private PaletteData srcPaletteData;
	//private Image srcImg;
	//private Image bufImg;
	//private GC srcGC;
	
	//private CoCoPalette cocoPalette = new CoCoPalette();
	private final Canvas canvasScreen;
	
	

	private boolean needsUpdate = true;
	private Boolean redrawInProgress = false;
	
	private boolean doAntiAlias = true;
	private boolean doFrameSkip = true;


	private Vector<OS9Window> textwin = new Vector<OS9Window>();
	private int datapos;
	private byte[] databuf;
	private OS9Buffer curbuffer;
	protected boolean selectMode = false;

	private boolean attached = false;
	private Shell shell = null;
	private String devicename = null;
	private int bordercolor = 0;
	private Image srcImg;
	protected int lastBorderColor = -1;
	protected Color borderColor = MainWin.colorGreen;
	protected long lastRedrawRequest = 0;

	private RedrawList redraws = new RedrawList();
	private Composite compositeBorderNorth;
	private Composite compositeBorderSouth;
	private Composite compositeBorderEast;
	private Composite compositeBorderWest;
	protected boolean doAdvanced = true;
	protected int doInterpolation = 2;
	private Label labelBNorth;
	private Label labelBSouth;
	private Label labelBEast;
	private Label labelBWest;
	private MenuItem mntmAttach;
	private Composite compositeScreen;
	private Composite compositeScreenCenter;
	
	
	
	public NineScreen(Composite display, int style, final CTabItem mytab, final Socket skt)
	{
		super(display, SWT.DOUBLE_BUFFERED);
		
		this.skt = skt;
		
		
		this.srcPaletteData = new PaletteData(DEFAULT_PALETTE);
		
		
		// initial window
		
		int sty = (MainWin.config.getInt("NineServerDefault_STY", 2));
		
		int cpx = (MainWin.config.getInt("NineServerDefault_CPX", 0));
		int cpy = (MainWin.config.getInt("NineServerDefault_CPY", 0));
		
		int szx = (MainWin.config.getInt("NineServerDefault_SZX", 80));
		int szy = (MainWin.config.getInt("NineServerDefault_SZY", 24));
		
		int prn1 = (MainWin.config.getInt("NineServerDefault_PRN1", 2));
		int prn2 = (MainWin.config.getInt("NineServerDefault_PRN2", 3));
		int prn3 = (MainWin.config.getInt("NineServerDefault_PRN3", 3));
		
		
		switch(sty)
		{
			case OS9Defs.STY_Text40:
			case OS9Defs.STY_GfxLoRes16Col:
			case OS9Defs.STY_GfxLoRes4Col:
				this.srcImgData = new ImageData(320,192, 8, this.srcPaletteData);
				break;
		
			case OS9Defs.STY_GfxHiRes2Col:
			case OS9Defs.STY_GfxHiRes4Col:
			case OS9Defs.STY_Text80:
				this.srcImgData = new ImageData(640,192, 8, this.srcPaletteData);
				break;
		}	
		
		this.setBorderColor(prn3);
		
		
		switch (sty)
		{
			case OS9Defs.STY_Text40:
			case OS9Defs.STY_Text80:
				this.textwin.add((OS9Window) new OS9TextWindow(sty, cpx, cpy, szx, szy, (byte)prn1, (byte)prn2 , false, this.srcImgData));
				break;
				
				
			case OS9Defs.STY_GfxHiRes2Col:
			case OS9Defs.STY_GfxHiRes4Col:
			case OS9Defs.STY_GfxLoRes16Col:
			case OS9Defs.STY_GfxLoRes4Col:
				this.textwin.add((OS9Window) new OS9GfxWindow(sty, cpx, cpy, szx, szy, (byte)prn1, (byte)prn2 , false, this.srcImgData));
				break;
			
				
		}
		
		
		
		setLayout(new BorderLayout(0, 0));
		
		/*
		if (this.ctabitem != null)
		{
			btnDetach = new Button(compositeToolbar, SWT.NONE);
			btnDetach.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					if (attached)
					{
						shell = new Shell(Display.getCurrent(), SWT.RESIZE | SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX ); 
						shell.setText(mytab.getText());
						
						
						shell.setLayout(new BorderLayout(0, 0));
						
						setParent(shell); 
						
						shell.setSize(getBounds().width,getBounds().height);
						
						shell.layout();
						
						shell.open();
						attached = false;
						btnDetach.setText("Attach");
					
					}
					else
					{
						Composite tmp = getParent();
						
						setParent(mytab.getParent());
						
						mytab.getParent().layout(true);
						
						tmp.dispose();
						
						attached = true;
						btnDetach.setText("Detach");
					}
				}
			});
			btnDetach.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
			btnDetach.setText("Detach");
			
		}
		*/
		
		compositeScreen = new Composite(this, SWT.DOUBLE_BUFFERED);
		compositeScreen.setLayoutData(BorderLayout.CENTER);
		
		compositeScreen.setLayout(new BorderLayout(0, 0));
	
		
		compositeScreenCenter = new Composite(compositeScreen, SWT.NONE);
		compositeScreenCenter.setLayoutData(BorderLayout.CENTER);
		compositeScreenCenter.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		
		GridLayout gl_compositeScreenCenter = new GridLayout(1, false);
		gl_compositeScreenCenter.horizontalSpacing = 0;
		gl_compositeScreenCenter.marginHeight = 0;
		gl_compositeScreenCenter.marginWidth = 0;
		gl_compositeScreenCenter.verticalSpacing = 0;
		compositeScreenCenter.setLayout(gl_compositeScreenCenter);
		
		
		canvasScreen = new Canvas(compositeScreenCenter, SWT.DOUBLE_BUFFERED);
		canvasScreen.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		GridData gd_canvasScreen = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_canvasScreen.widthHint = DEFAULT_WIDTH;
		gd_canvasScreen.heightHint = DEFAULT_HEIGHT;
		canvasScreen.setLayoutData(gd_canvasScreen);	
		

		
		Menu menu = new Menu(canvasScreen);
		canvasScreen.setMenu(menu);
		
		MenuItem mntmCopy = new MenuItem(menu, SWT.NONE);
		mntmCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				doCopy();
			}
		});
		mntmCopy.setText("Copy");
		
		MenuItem mntmPaste = new MenuItem(menu, SWT.NONE);
		mntmPaste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				doPaste();
			}
		});
		mntmPaste.setText("Paste");
		
		
		
		
		
		
		
		
		
			
		compositeBorderNorth = new Composite(compositeScreen, SWT.NONE);
		compositeBorderNorth.setLayoutData(BorderLayout.NORTH);
		GridLayout gl_compositeBorderNorth = new GridLayout(1, false);
		gl_compositeBorderNorth.horizontalSpacing = 0;
		gl_compositeBorderNorth.marginHeight = 0;
		gl_compositeBorderNorth.marginWidth = 0;
		gl_compositeBorderNorth.verticalSpacing = 0;
		compositeBorderNorth.setLayout(gl_compositeBorderNorth);
		
		labelBNorth = new Label(compositeBorderNorth, SWT.NONE);
		GridData gd_labelBNorth = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_labelBNorth.heightHint = BORDER_WIDTH;
		labelBNorth.setLayoutData(gd_labelBNorth);
		
		labelBNorth.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e)
			{
				labelBNorth.setBackground(borderColor);
			}});
		
		compositeBorderSouth = new Composite(compositeScreen, SWT.NONE);
		compositeBorderSouth.setLayoutData(BorderLayout.SOUTH);
		GridLayout gl_compositeBorderSouth = new GridLayout(1, false);
		gl_compositeBorderSouth.verticalSpacing = 0;
		gl_compositeBorderSouth.marginWidth = 0;
		gl_compositeBorderSouth.marginHeight = 0;
		gl_compositeBorderSouth.horizontalSpacing = 0;
		compositeBorderSouth.setLayout(gl_compositeBorderSouth);
		
		
		labelBSouth = new Label(compositeBorderSouth, SWT.NONE);
		GridData gd_labelBSouth = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_labelBSouth.heightHint = BORDER_WIDTH;
		labelBSouth.setLayoutData(gd_labelBSouth);
		
		labelBSouth.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e)
			{
				labelBSouth.setBackground(borderColor);
			}});
		
		
		compositeBorderEast = new Composite(compositeScreen, SWT.NONE);
		compositeBorderEast.setLayoutData(BorderLayout.EAST);
		GridLayout gl_compositeBorderEast = new GridLayout(1, false);
		gl_compositeBorderEast.verticalSpacing = 0;
		gl_compositeBorderEast.marginWidth = 0;
		gl_compositeBorderEast.marginHeight = 0;
		gl_compositeBorderEast.horizontalSpacing = 0;
		compositeBorderEast.setLayout(gl_compositeBorderEast);
		
		labelBEast = new Label(compositeBorderEast, SWT.NONE);
		GridData gd_labelBEast = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_labelBEast.widthHint = BORDER_WIDTH;
		labelBEast.setLayoutData(gd_labelBEast);
		
		
		labelBEast.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e)
			{
				labelBEast.setBackground(borderColor);
			}});
		
		compositeBorderWest = new Composite(compositeScreen, SWT.NONE);
		compositeBorderWest.setLayoutData(BorderLayout.WEST);
		GridLayout gl_compositeBorderWest = new GridLayout(1, false);
		gl_compositeBorderWest.verticalSpacing = 0;
		gl_compositeBorderWest.horizontalSpacing = 0;
		gl_compositeBorderWest.marginWidth = 0;
		gl_compositeBorderWest.marginHeight = 0;
		compositeBorderWest.setLayout(gl_compositeBorderWest);
		
		labelBWest = new Label(compositeBorderWest, SWT.NONE);
		GridData gd_labelBWest = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_labelBWest.widthHint = BORDER_WIDTH;
		labelBWest.setLayoutData(gd_labelBWest);

		labelBWest.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e)
			{
				labelBWest.setBackground(borderColor);
			}});
		
		
		
		canvasScreen.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				Byte b = getCoCoKey(e);
				
				if (b != null)
				{
					try
					{
						skt.getOutputStream().write(b);
					} 
					catch (IOException e1)
					{
						// TODO
					}
				}
				
			}
		});
		
	
		
		canvasScreen.addPaintListener(new PaintListener(){
			
			@Override
			public void paintControl(PaintEvent e)
			{
			
					long startt = System.currentTimeMillis();
					
					/*
					if (doInterpolation > SWT.NONE)
					{
						e.gc.setAntialias(SWT.ON);
						e.gc.setInterpolation(doInterpolation);
						e.gc.setAdvanced(true);
					}
					else
					{
						e.gc.setAntialias(SWT.OFF);
					
						e.gc.setAdvanced(false);
					}
					*/
					
					
					
					if ((srcImgData != null) && (Display.getCurrent() != null) && ((canvasScreen.getBounds().width >  0)) && (canvasScreen.getBounds().height >  0))
					{
						
						
						srcImg = new Image(Display.getCurrent(), srcImgData.scaledTo(canvasScreen.getBounds().width, canvasScreen.getBounds().height));
						
						//e.gc.drawImage(srcImg, 0, 0, srcImgData.width, srcImgData.height, 0, 0, canvasScreen.getBounds().width, canvasScreen.getBounds().height);
						
						
						
						if (e.data == null)
						{
							e.gc.drawImage(srcImg, 0, 0);
						}
						else
						{ 
							//Rectangle rarea = (Rectangle) e.data;
							
							//System.out.println("redraw " + rarea.x + "," + rarea.y + " " + rarea.width + "x" + rarea.height + "  to  " + e.x + "," + e.y + " " + e.width + "x" + e.height);
							
							e.gc.drawImage(srcImg, e.x, e.y , e.width, e.height, e.x, e.y, e.width, e.height);
						}
						
						
						
						srcImg.dispose();
						
					}	
					
					long dtime = (System.currentTimeMillis() - startt);
					
					if (dtime > 50)
						System.out.println("Draw time: " + dtime);
				
				lastBorderColor++;
					
			}});
		
		
		
		canvasScreen.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (e.button == 1)
				{
					if (getCurWin() != null)
						getCurWin().mouseDown(canvasScreen.getBounds(), e.x, e.y);
					selectMode = true;
				}
			}

			@Override
			public void mouseUp(MouseEvent e)
			{
				if (getCurWin() != null)
					getCurWin().mouseUp(canvasScreen.getBounds(), e.x, e.y);
				selectMode = false;
				updateScreen();
			}});
		
		canvasScreen.addMouseMoveListener(new MouseMoveListener(){

			@Override
			public void mouseMove(MouseEvent e)
			{
				if (selectMode)
				{
					if (getCurWin() != null)
						getCurWin().moveSelect(canvasScreen.getBounds(), e.x, e.y);
					updateScreen();
				}
			}});
		
		
		
		
		
		
		if (mytab != null)
		{
			this.ctabitem = mytab;
			setParent(mytab.getParent());
			mytab.setText("NineScreen");
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			mntmAttach = new MenuItem(menu, SWT.NONE);
			mntmAttach.addSelectionListener(new SelectionAdapter() 
			{
				
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (attached)
					{
						shell = new Shell(Display.getCurrent(), SWT.RESIZE | SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX ); 
						shell.setText(mytab.getText());
						
						shell.setLayout(new BorderLayout(0, 0));
						
						setParent(shell); 
						
						shell.setSize(getBounds().width,getBounds().height);
						
						shell.layout();
						
						shell.open();
						attached = false;
						mntmAttach.setText("Attach");
					
					}
					else
					{
						Composite tmp = getParent();
						
						setParent(mytab.getParent());
						
						mytab.getParent().layout(true);
						
						tmp.dispose();
						
						attached = true;
						mntmAttach.setText("Detach");
					}
				}
				
			});
			
			
			
			mntmAttach.setText("Detach");
			attached = true;
		}
		else
		{
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			MenuItem mntmSize_1 = new MenuItem(menu, SWT.CASCADE);
			mntmSize_1.setText("Size");
			
			Menu menu_1 = new Menu(mntmSize_1);
			mntmSize_1.setMenu(menu_1);
			
			
			MenuItem mntmSize1x1 = new MenuItem(menu_1, SWT.NONE );
			mntmSize1x1.setText("1x1");
			mntmSize1x1.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd.widthHint = 640;
					gd.heightHint = 192;
					canvasScreen.setLayoutData(gd);
					shell.layout();
					shell.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					
				}} );
			
			MenuItem mntmSize1x2 = new MenuItem(menu_1, SWT.NONE );
			mntmSize1x2.setText("1x2");
			mntmSize1x2.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd.widthHint = 640;
					gd.heightHint = 384;
					canvasScreen.setLayoutData(gd);
					shell.layout();
					shell.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					
				}} );
			
			
			MenuItem mntmSize2x2 = new MenuItem(menu_1, SWT.NONE );
			mntmSize2x2.setText("2x2");
			mntmSize2x2.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd.widthHint = 1280;
					gd.heightHint = 384;
					canvasScreen.setLayoutData(gd);
					shell.layout();
					shell.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					
				}} );
			
			
			MenuItem mntmSize2x3 = new MenuItem(menu_1, SWT.NONE );
			mntmSize2x3.setText("2x3");
			mntmSize2x3.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd.widthHint = 1280;
					gd.heightHint = 384 + 192;
					canvasScreen.setLayoutData(gd);
					shell.layout();
					shell.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					
				}} );
			
			
			MenuItem mntmSize2x4 = new MenuItem(menu_1, SWT.NONE );
			mntmSize2x4.setText("2x4");
			mntmSize2x4.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd.widthHint = 1280;
					gd.heightHint = 192 * 4;
					canvasScreen.setLayoutData(gd);
					shell.layout();
					shell.pack();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					
				}} );
			
			
			
			shell = new Shell(Display.getCurrent(), SWT.RESIZE | SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX ); 
			
			shell.setLayout(new BorderLayout(0, 0));
			
			setParent(shell); 
			

			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gd.widthHint = 640;
			gd.heightHint = 384;
			canvasScreen.setLayoutData(gd);
			
				
			shell.addListener (SWT.Resize,  new Listener () 
			{
			    public void handleEvent (Event e) 
			    {
			    	if (devicename != null)
			    	{
				      Rectangle rect = shell.getBounds();
				      MainWin.config.setProperty("NineServerWinPosW_" + devicename, rect.width);
				      MainWin.config.setProperty("NineServerWinPosH_" + devicename, rect.height);
			    	}
			    	
			    }
			  });
			
			shell.addListener (SWT.Move,  new Listener () {
			    public void handleEvent (Event e) {
			    	if (devicename != null)
			    	{
				      Rectangle rect = shell.getBounds();
				      MainWin.config.setProperty("NineServerWinPosX_" + devicename, rect.x);
				      MainWin.config.setProperty("NineServerWinPosY_" + devicename, rect.y);
				      
			    	}
			    }
			  });
			
			shell.addDisposeListener(new DisposeListener(){

				@Override
				public void widgetDisposed(DisposeEvent e)
				{
					if (ctabitem == null)
					{
						try
						{
							skt.close();
						} catch (IOException e1)
						{
						
						}
					}
				}});
			
			
			shell.layout();
			shell.pack();
			
			shell.open();
			
		}
		
		redrawBorders();
		
		layout();
		pack();
		
		setIcon(OS9Defs.CMD_DWExt_Icon_Normal);
	}

	


	private void setBorderColor(int prn3)
	{
		this.bordercolor = prn3;
		
		if (this.borderColor != null)
			this.borderColor.dispose();
		
		this.borderColor = new Color(this.getDisplay(), srcImgData.getRGBs()[bordercolor]); 
		
	}




	protected void doPaste()
	{
		Clipboard clipboard = new Clipboard(getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		String textData = (String)clipboard.getContents(textTransfer);
		   
		if (textData != null)
		{
		  	try
			{
				skt.getOutputStream().write(textData.getBytes());
			} catch (IOException e1)
			{
			
			}
		}
	}



	protected void doCopy()
	{
		String cp = getCurWin().getSelected();
		
		if (cp != null)
		{
			Clipboard clipboard = new Clipboard(getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			Transfer[] transfers = new Transfer[]{textTransfer};
			Object[] data = new Object[]{cp};
			clipboard.setContents(data, transfers);
			clipboard.dispose();

		}
		
		updateScreen();
	}

	
	


	


	protected Byte getCoCoKey(KeyEvent e)
	{
		Byte res = null;
		
		//System.out.println("key: " + e.keyCode + "\t mask: " + e.stateMask + "\t char: " + (int)e.character);
		
		
		switch(e.keyCode)
		{
			
			case SWT.INSERT:
				
				if (e.stateMask == SWT.CTRL)
				{
					doCopy();
				}
				else if (e.stateMask == SWT.SHIFT)
				{
					doPaste();
				}
				break;
				
			case SWT.ARROW_RIGHT:  // right
				res = 0x09;
				break;
				
			case SWT.ARROW_LEFT:  // left
				res = 0x08;
				break;
				
			case SWT.ARROW_UP:  // up
				res = 0x0c;
				break;
				
			case SWT.ARROW_DOWN:  // down
				res = 0x0a;
				break;
				
			case 13:
				res = OS9Defs.CTL_CR;
				break;
				
			default:
				if ((e.character > 0))
					res = (byte) e.character;
				
		}
		
		return res;
	}



	public void takeInput(byte data)
	{
		
		//System.out.println("input: " + data + "\t" + (char)data);
		
		switch(this.cmdmode)
		{
			case CMDMODE_NONE:
				
				switch(data)
				{
					case OS9Defs.CMD_Escape:
						this.cmdmode = CMDMODE_GETCMD;
						break;
					
					default:
						if (getCurWin() == null)
						{
							System.out.println("No window for output, data byte: " + Integer.toHexString(data));
						}
						else
						{
							Rectangle up = getCurWin().addToScreen(data);		
							updateScreen(up);
						}
						
				}
				
				break;
				
				
			case CMDMODE_GETCMD:
				
				//System.out.println("CMD " + Integer.toHexString((0xff & data)));
				
				this.os9cmd = data;
				
				switch(data)
				{
					case OS9Defs.CMD_Arc3P:
						this.argbytes = 12;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_GetBlk:
						this.argbytes = 10;
						this.cmdmode = CMDMODE_GETARGS;
						break;
				
					case OS9Defs.CMD_GPLoad:
						this.argbytes = 9;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_DWSet:
						this.argbytes = 8;
						this.cmdmode = CMDMODE_GETARGS;
						break;
					
					case OS9Defs.CMD_OWSet:
						this.argbytes = 7;
						this.cmdmode = CMDMODE_GETARGS;
						break;
					
					case OS9Defs.CMD_PutBlk:
						this.argbytes = 6;
						this.cmdmode = CMDMODE_GETARGS;
						break;	
						
					case OS9Defs.CMD_CWArea:
					case OS9Defs.CMD_DfnGPBuf:
					case OS9Defs.CMD_Bar:
					case OS9Defs.CMD_RBar:
					case OS9Defs.CMD_Box:
					case OS9Defs.CMD_RBox:
					case OS9Defs.CMD_Ellipse:
					case OS9Defs.CMD_Line:
					case OS9Defs.CMD_RLine:
					case OS9Defs.CMD_LineM:
					case OS9Defs.CMD_RLineM:
					case OS9Defs.CMD_Point:
					case OS9Defs.CMD_RPoint:
					case OS9Defs.CMD_PutGC:
					case OS9Defs.CMD_SetDPtr:
					case OS9Defs.CMD_RSetDPtr:
						this.argbytes = 4;
						this.cmdmode = CMDMODE_GETARGS;
						break;
					
						
					case OS9Defs.CMD_Palette:
					case OS9Defs.CMD_Font:
					case OS9Defs.CMD_GCSet:
					case OS9Defs.CMD_KilBuf:
					case OS9Defs.CMD_PSet:
					case OS9Defs.CMD_Circle:
						this.argbytes = 2;
						this.cmdmode = CMDMODE_GETARGS;
						break;
										
					case OS9Defs.CMD_BColor:
					case OS9Defs.CMD_FColor:
					case OS9Defs.CMD_Border:
					case OS9Defs.CMD_BoldSw:
					case OS9Defs.CMD_DWProtSw:
					case OS9Defs.CMD_LSet:
					case OS9Defs.CMD_PropSw:
					case OS9Defs.CMD_ScaleSw:
					case OS9Defs.CMD_TCharSw:
						this.argbytes = 1;
						this.cmdmode = CMDMODE_GETARGS;
						break;
					
					case OS9Defs.CMD_OWEnd:
						// close overlay
						synchronized(this.textwin)
						{
							if (this.textwin.size()>1)
							{
								OS9Window owt = this.textwin.lastElement();
								// handle "save"
								if (!owt.isSave())
								{
									// TODO
									
									
								}
								
								this.textwin.remove(textwin.size()-1);
								
								//reportOnCurrentWin();
							}
							else
							{
								System.out.println("OWEnd called but no overlays to remove");
							}
						}
						this.cmdmode = CMDMODE_NONE;
						
						break;
					
						
					case OS9Defs.CMD_DefColr:
						this.srcPaletteData.colors = DEFAULT_PALETTE;
						this.cmdmode = CMDMODE_NONE;
						break;
						
					case OS9Defs.CMD_DWEnd:
						this.cmdmode = CMDMODE_NONE;
						
						
						break;
					case OS9Defs.CMD_Select:
					case OS9Defs.CMD_FFill:
						this.cmdmode = CMDMODE_NONE;
						break;
					
					case OS9Defs.CMD_DWExt:
						this.cmdmode = CMDMODE_DWEXT;
						break;
						
					default:
						// well...
						System.out.println("Not implemented: CMD " + Integer.toHexString(data));
						this.cmdmode = CMDMODE_NONE;
					
				}
				
				break;
			
			case CMDMODE_DWEXT:
				
				this.dwcmd = data;
				
				switch(data)
				{
					case OS9Defs.CMD_DWExt_DevName:
						this.argbytes = 5;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_DWExt_DefWin:
						this.argbytes = 4;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_DWExt_Title:
						this.argbytes = 32;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_DWExt_Palette:
						this.argbytes = 4;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
					case OS9Defs.CMD_DWExt_Icon:
						this.argbytes = 1;
						this.cmdmode = CMDMODE_GETARGS;
						break;
						
				}
				
				
				break;
				
			case CMDMODE_GETARGS:
				this.args.add(data);
				this.argbytes--;
				
				if (argbytes == 0)
				{
					this.cmdmode = CMDMODE_NONE;
					executeCMD(this.os9cmd,this.dwcmd,this.args);
					this.os9cmd = 0;
					this.dwcmd = 0;
					this.args = new Vector<Byte>();
				}
				
				break;
				
			case CMDMODE_DATA:
				this.databuf[datapos] = data;
				this.datapos++;
				
				if (datapos == this.databuf.length)
				{
					this.cmdmode = CMDMODE_NONE;
					this.curbuffer.setData(this.databuf);
				}
				
				break;
				
		}
		
	//	System.out.println("cmdmode: " + this.cmdmode + "\t os9cmd: " + this.os9cmd + "\t dwcmd: " + this.dwcmd + "\t argbytes: " + this.argbytes);
		
		updateStatus();
	}

	
	
	
	
	
	private void updateStatus()
	{
		/*
		if (!isDisposed())
		{
			getDisplay().asyncExec(new Runnable(){
	
				@Override
				public void run()
				{
					String st = "";
					
					OS9Window tw = getCurWin();
					
					if (!isDisposed() & (tw != null))
					{
						st = "win " + textwin.size() + "  ";
						
						st += "tCur " + tw.getCursorX() + "," + tw.getCursorY() + "  ";
						
						st += "off " + tw.getOffsetX() + "," + tw.getOffsetY() + "  ";
						st += "sz " + tw.getWidth() + "x" + tw.getHeight() + "  ";
						
						st += "cwaOff " + tw.getCWAOffsetX() + "," + tw.getCWAOffsetY() + "  ";
						st += "cwaSz " + tw.getCWAWidth() + "x" + tw.getCWAHeight() + "  ";
						
						st += "fg.bg.bo " + tw.getFGCol() + "." + tw.getBGCol() + "." + bordercolor + "  ";
						
						if (tw.getClass().equals(OS9GfxWindow.class))
						{
							OS9GfxWindow gw = (OS9GfxWindow)tw;
							
							st += "gCur " + gw.getGCursorX() + "," + gw.getGCursorY() + "  ";
							st += "fnt " + gw.getFontGrp() + ":" + gw.getFontBuf() + "  ";
							st += "gc " + gw.getGcurGrp() + ":" + gw.getGcurBuf() + "  ";
							st += "pat " + gw.getPatGrp() + ":" + gw.getPatBuf() + "  ";
							st += "log " + gw.getLogic();
							
						}
						
						textWinStatus.setText(st);
					}
					
					
					
				}});
		}
		*/
	}
	



	


	private OS9Window getCurWin()
	{
		synchronized(textwin)
		{
		if (this.textwin.size() > 0)
			return(this.textwin.lastElement());
		else
			return null;
		}
	}
	
	private OS9GfxWindow getCurGfxWin()
	{
		if ((getCurWin() != null) && (getCurWin().getClass().equals(OS9GfxWindow.class)))
		{
			return (OS9GfxWindow) getCurWin();
		}
		
		return null;
	}
	


	private void executeCMD(byte cmd, byte dwcmd, Vector<Byte> args)
	{
		Rectangle rect;
		
		if (getCurWin() == null)
			rect = new Rectangle(0,0,0,0);
		else
			rect = getCurWin().getBounds(); 
				
		
		if (dwcmd > 0)
		{	
			executeDWCMD(dwcmd, args);
		}
		else
		{
			/*
			System.out.print("CMD " + Integer.toHexString(cmd) + "\t");
			for (Byte b : args)
				System.out.print(Integer.toHexString(b) + " ");
			System.out.println();
			*/
			
			// all os9 windows process these
			
			switch(cmd)
			{
				case OS9Defs.CMD_DWSet:
					doCMD_DWSet(args);
					break;
				
				case OS9Defs.CMD_OWSet:
					doCMD_OWSet(args);
					break;
					
					
				case OS9Defs.CMD_CWArea:
					getCurWin().setCWA(args.get(0), args.get(1), args.get(2), args.get(3));
					break;
			
					
				case OS9Defs.CMD_Border:
					this.setBorderColor(args.get(0));
				
					redrawBorders();
					
				
					
					break;
			
					
				case OS9Defs.CMD_Palette:
					//System.out.println("palette set " + args.get(0) + " to " + args.get(1));
					this.srcPaletteData.colors[args.get(0)] = getRGB(args.get(1));
					
					if (args.get(0) == this.bordercolor)
					{
						if (this.borderColor != null)
							this.borderColor.dispose();
						this.borderColor = new Color(Display.getCurrent(), srcImgData.getRGBs()[bordercolor]); 
					}
					
					break;
					
					
				case OS9Defs.CMD_BColor:
					getCurWin().setBGCol(args.get(0));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_FColor:
					getCurWin().setFGCol(args.get(0));
					rect = new Rectangle(0,0,0,0);
					break;
					
			
			}	
			
			
			// only GFX windows process these..
			
			if (getCurGfxWin() != null)
			{
				
				switch(cmd)
				{
					
				case OS9Defs.CMD_GPLoad:
					doCMD_GPLoad(args);
					rect = new Rectangle(0,0,0,0);
					break;
					
					
				case OS9Defs.CMD_PutBlk:
					rect = getCurGfxWin().putBlk(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5));
					break;
					
				case OS9Defs.CMD_DfnGPBuf: // dont need?
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_Arc3P:
					getCurGfxWin().drawArc(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5), args.get(6), args.get(7), args.get(8), args.get(9), args.get(10), args.get(11));  
					break;
					
				case OS9Defs.CMD_Bar:
					rect = getCurGfxWin().drawBar(args.get(0), args.get(1), args.get(2), args.get(3), true);
					break;
					
				case OS9Defs.CMD_RBar:
					rect = getCurGfxWin().drawRBar(args.get(0), args.get(1), args.get(2), args.get(3), true);
					break;
					
				case OS9Defs.CMD_Box:
					rect = getCurGfxWin().drawBar(args.get(0), args.get(1), args.get(2), args.get(3), false);
					break;
					
				case OS9Defs.CMD_RBox:
					rect = getCurGfxWin().drawRBar(args.get(0), args.get(1), args.get(2), args.get(3), false);
					break;
					
				case OS9Defs.CMD_Ellipse:
					getCurGfxWin().drawEllipse(args.get(0), args.get(1), args.get(2), args.get(3));
					break;
					
				case OS9Defs.CMD_Line:
					getCurGfxWin().drawLine(args.get(0), args.get(1), args.get(2), args.get(3), false, false);
					break;
					
				case OS9Defs.CMD_RLine:
					getCurGfxWin().drawLine(args.get(0), args.get(1), args.get(2), args.get(3), true, false);
					break;
					
				case OS9Defs.CMD_LineM:
					getCurGfxWin().drawLine(args.get(0), args.get(1), args.get(2), args.get(3), false, true);
					break;
					
				case OS9Defs.CMD_RLineM:
					getCurGfxWin().drawLine(args.get(0), args.get(1), args.get(2), args.get(3), true, true);
					break;
					
					
				case OS9Defs.CMD_Point:
					getCurGfxWin().drawPoint(args.get(0), args.get(1), args.get(2), args.get(3), false);
					break;
					
				case OS9Defs.CMD_RPoint:
					getCurGfxWin().drawPoint(args.get(0), args.get(1), args.get(2), args.get(3), true);
					break;
					
				case OS9Defs.CMD_PutGC:
					
					break;
					
				case OS9Defs.CMD_SetDPtr:
					getCurGfxWin().setDrawPtr(args.get(0), args.get(1), args.get(2), args.get(3), false);
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_RSetDPtr:
					getCurGfxWin().setDrawPtr(args.get(0), args.get(1), args.get(2), args.get(3), true);
					rect = new Rectangle(0,0,0,0);
					break;
					
				
				case OS9Defs.CMD_Font:
					getCurGfxWin().setFontGrp(args.get(0));
					getCurGfxWin().setFontBuf(args.get(1));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_GCSet:
					getCurGfxWin().setGcurGrp(args.get(0));
					getCurGfxWin().setGcurBuf(args.get(1));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_KilBuf:
					if (args.get(1) == 0)
					{
						// kill group
						MainWin.os9BufferGroups.set(args.get(0), null);
					}
					else
					{
						// kill buffer
						if (MainWin.os9BufferGroups.get(args.get(0)) != null)
							MainWin.os9BufferGroups.get(args.get(0)).setBuffer(args.get(1), null); 
					}
				
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_PSet:
					getCurGfxWin().setPatGrp(args.get(0));
					getCurGfxWin().setPatBuf(args.get(1));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_Circle:
					getCurGfxWin().drawEllipse(args.get(0), args.get(1), args.get(0), args.get(1));
					break;
					
			
				case OS9Defs.CMD_BoldSw:
					getCurGfxWin().setBoldSw(byteToBool(args.get(0)));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_DWProtSw:
					getCurGfxWin().setProtectSw(byteToBool(args.get(0)));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_LSet:
					getCurGfxWin().setLogic(args.get(0));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_PropSw:
					getCurGfxWin().setPropSw(byteToBool(args.get(0)));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_ScaleSw:
					getCurGfxWin().setScaleSw(byteToBool(args.get(0)));
					rect = new Rectangle(0,0,0,0);
					break;
					
				case OS9Defs.CMD_TCharSw:
					getCurGfxWin().setTransparentSw(byteToBool(args.get(0)));
					rect = new Rectangle(0,0,0,0);
					break;
				
					
				}
			}
			
			
			updateScreen(rect);
		}
	}



	private void redrawBorders()
	{
		this.getDisplay().asyncExec(new Runnable(){

			@Override
			public void run()
			{
				
				labelBEast.redraw();
				labelBWest.redraw();
				labelBNorth.redraw();
				labelBSouth.redraw();
				
				labelBEast.update();
				labelBWest.update();
				labelBNorth.update();
				labelBSouth.update();
				
			}});
	}




	private RGB getRGB(Byte d)
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
		
		
		
		return new RGB(r,g,b);
	}



	private boolean byteToBool(Byte byte1)
	{
		if (byte1 == 1)
			return true;
		return false;
	}



	private void doCMD_GPLoad(Vector<Byte> args)
	{
		// load buffer
		// GRP BFN STY HSX LSX HSY LSY HBL LBL (Data. ..)
		
		int grp = (0xff & args.get(0));
		int buf = (0xff & args.get(1));
		
		if (MainWin.os9BufferGroups.get(grp) == null)
			MainWin.os9BufferGroups.set(grp,new OS9BufferGroup());
		
		int x = (0xff & args.get(3)) * 256 + (0xff & args.get(4));
		int y = (0xff & args.get(5)) * 256 + (0xff & args.get(6));
		int s = (0xff & args.get(7)) * 256 + (0xff & args.get(8));
		
		this.curbuffer = new OS9Buffer((0xff & args.get(2)), x , y, s); 
		this.databuf = new byte[s];
		this.datapos = 0;
		
		MainWin.os9BufferGroups.get(grp).setBuffer(buf, this.curbuffer );
		
		System.out.println("GPLoad grp " + grp + "  buf " + buf + "  sty " + args.get(2) + "  x " + x + "  y " + y + "  sz " + s);
		
		this.cmdmode = CMDMODE_DATA;
		
	}



	private void doCMD_OWSet(Vector<Byte> args)
	{
		// overlay window
		// SVS CPX CPY SZX SZY PRN1 PRN2
		
		System.out.print("New overlay window (stack size " + this.textwin.size() +  "), args: ");
		for (Byte b : args)
			System.out.print(Integer.toHexString(b) + " ");
		System.out.println();
	
		
		OS9Window rwin = this.textwin.get(0) ;
		
		// sanity
		// switch for save.. ignore?
		if  ( (args.get(0) == 0) || (args.get(0) == 1) )
			// X start
			if ((args.get(1) >= 0) && (args.get(1) < rwin.getWidth()))
				// Y start
				if ((args.get(2) >= 0) && (args.get(2) < rwin.getHeight()))
					// width
					if ((args.get(3) > 0) && (args.get(1) + args.get(3) < rwin.getWidth()))
						// height
						if ((args.get(4) > 0) && (args.get(2) + args.get(4) < rwin.getWidth()))
							// colors
							if ((args.get(5) >= 0) && (args.get(6) >= 0)) 
								// colors
								if ((args.get(5) < 16) && (args.get(6) < 16))
								{
									synchronized(this.textwin)
									{
										boolean save = false;
										if (args.get(0) == 1)
											save = true;
										
										switch (getCurWin().getSTY())
										{
											case OS9Defs.STY_Text40:
											case OS9Defs.STY_Text80:
												this.textwin.add((OS9Window) new OS9TextWindow(getCurWin().getSTY(), args.get(1), args.get(2), args.get(3),args.get(4), args.get(5), args.get(6) , save, this.srcImgData));
												break;
												
												
											case OS9Defs.STY_GfxHiRes2Col:
											case OS9Defs.STY_GfxHiRes4Col:
											case OS9Defs.STY_GfxLoRes16Col:
											case OS9Defs.STY_GfxLoRes4Col:
												this.textwin.add((OS9Window) new OS9GfxWindow(getCurWin().getSTY(), args.get(1), args.get(2), args.get(3),args.get(4), args.get(5), args.get(6) , save, this.srcImgData));
												break;
											
												
										}
									}
								}
				
	}



	private void doCMD_DWSet(Vector<Byte> args)
	{
		// device window
		// STY CPX CPY SZX SZY PRN1 PRN2 PRN3
		
		// sanity
		
		
		System.out.print("New window, args: ");
		for (Byte b : args)
			System.out.print(Integer.toHexString(b) + " ");
		System.out.println();
		
		// sanity... X start
		if ((args.get(1) >= 0) && (args.get(1) < 80))
			// Y start
			if ((args.get(2) >= 0) && (args.get(2) < 24))
				// width
				if ((args.get(3) > 0) && (args.get(3) + args.get(1) <= 80))
					// height
					if ((args.get(4) > 0) && (args.get(4) + args.get(2) <= 24))
						// colors
						if ((args.get(5) >= 0) && (args.get(6) >= 0) && (args.get(7) >= 0)) 
							// colors
							if ((args.get(5) < 16) && (args.get(6) < 16) && (args.get(7) < 16))
							{
								// by the gods, its a valid request..
								
								this.textwin = new Vector<OS9Window>();
								
								switch(args.get(0))
								{
									case OS9Defs.STY_Text40:
									case OS9Defs.STY_GfxLoRes16Col:
									case OS9Defs.STY_GfxLoRes4Col:
										this.srcImgData = new ImageData(320,192, 8, this.srcPaletteData);
										break;
								
									case OS9Defs.STY_GfxHiRes2Col:
									case OS9Defs.STY_GfxHiRes4Col:
									case OS9Defs.STY_Text80:
										this.srcImgData = new ImageData(640,192, 8, this.srcPaletteData);
										break;
								}	
								
								switch (args.get(0))
								{
									case OS9Defs.STY_Text40:
									case OS9Defs.STY_Text80:
										this.textwin.add((OS9Window) new OS9TextWindow(args.get(0), args.get(1), args.get(2), args.get(3),args.get(4), args.get(5), args.get(6) , false, this.srcImgData));
										break;
										
										
									case OS9Defs.STY_GfxHiRes2Col:
									case OS9Defs.STY_GfxHiRes4Col:
									case OS9Defs.STY_GfxLoRes16Col:
									case OS9Defs.STY_GfxLoRes4Col:
										this.textwin.add((OS9Window) new OS9GfxWindow(args.get(0), args.get(1), args.get(2), args.get(3),args.get(4), args.get(5), args.get(6) , false, this.srcImgData));
										break;
									
										
								}
								this.bordercolor = args.get(7);
								if (this.borderColor != null)
									this.borderColor.dispose();
								this.borderColor = new Color(Display.getCurrent(), srcImgData.getRGBs()[bordercolor]); 
								
							}
	
	}


	private void updateScreen()
	{
		if (getCurWin() != null)
			updateScreen(getCurWin().getBounds());
	}
	
	private void updateScreen(final Rectangle area)
	{
		
		class screenDrawer implements Runnable
		{

			
				@Override
				public void run()
				{
					while ((canvasScreen != null) && (redraws != null) && (getCurWin() != null) && redraws.hasArea() && !isDisposed())
					{
						Rectangle rarea = redraws.getArea(getCurWin().getBounds());
						
						double sw = srcImgData.width;
						double sh = srcImgData.height;
						
						double dw = canvasScreen.getBounds().width;
						double dh = canvasScreen.getBounds().height;
						
						Event pe = new Event();
						
						pe.x = (int) Math.floor(Double.valueOf(rarea.x) * dw / sw); 
						pe.y = (int) Math.floor(Double.valueOf(rarea.y) * dh / sh);
						pe.width = (int) Math.ceil(Double.valueOf(rarea.width) * dw / sw);
						pe.height = (int) Math.ceil(Double.valueOf(rarea.height) * dh / sh);
						pe.gc = new GC(canvasScreen);
						pe.data = rarea;
						
						canvasScreen.notifyListeners(SWT.Paint, pe);
						
						canvasScreen.update();
						pe.gc.dispose();
						
						redraws.setInProgress(false);
				
					}
				}
			

		}
		
		
		
		
		
		if (area.height + area.width > 0)
		{
			
			this.redraws.addRect(area);
			//this.redraws.addRect(new Rectangle(0,0,1,1).union(area));
			//this.redraws.addRect(getCurWin().getBounds());
			//System.out.println("add area\t" + area.x + "," + area.y + "  " + area.width + "x" + area.height);
			
			
			if (this.doFrameSkip)
			{
				
				if (this.redraws.isInProgress() == false)
				{
					
					this.redraws.setInProgress(true);
					
					this.getDisplay().asyncExec(new screenDrawer());
		
					
				}
				
			}
			else
			{
				this.getDisplay().syncExec(new screenDrawer());
			}
		}			
	}
	


	private void executeDWCMD(byte dwcmd, final Vector<Byte> args)
	{
		switch(dwcmd)
		{
			case OS9Defs.CMD_DWExt_DevName:
				// set title to device name
				String devname = "";
				for (Byte b : args)
					devname += (char) b.byteValue();
				
			
				this.devicename = devname.trim();
			
				MainWin.getDisplay().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						if (ctabitem != null)
							ctabitem.setText(" " + devicename + " ");
						else if (shell != null)
						{
							shell.setText(devicename);
							
							Rectangle cb = shell.getBounds();
							
							shell.setBounds(MainWin.config.getInt("NineServerWinPosX_" + devicename, cb.x), MainWin.config.getInt("NineServerWinPosY_" + devicename, cb.y), MainWin.config.getInt("NineServerWinPosW_" + devicename, cb.width), MainWin.config.getInt("NineServerWinPosH_" + devicename, cb.height));
							shell.layout();
							shell.pack();
							
							updateScreen();
						}
					}});
				
				break;
				
			case OS9Defs.CMD_DWExt_DefWin:
				// set window params.. ??
				
				MainWin.getDisplay().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						if ((shell != null) && (!attached))
						{
							shell.setBounds((0xff & args.get(0))*256 + (0xff & args.get(1)), (0xff & args.get(2))*256 + (0xff & args.get(3)), (0xff & args.get(4))*256 + (0xff & args.get(5)), (0xff & args.get(6))*256 + (0xff & args.get(7)) );
							
							updateScreen();
						}
					}});
				
				break;
				
			case OS9Defs.CMD_DWExt_Title:
				// set window title
				String tmp = "";
				for (Byte b : args)
					tmp += (char) b.byteValue();
				
				
				final String title = tmp.trim();
			
				MainWin.getDisplay().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						if (ctabitem != null)
							ctabitem.setText(" " + title + " ");
						else if (shell != null)
						{
							shell.setText(title);
							
							updateScreen();
						}
					}});
				
				break;
				
			case OS9Defs.CMD_DWExt_Palette:
				// redefine palette
				if ((args.get(0) >= 0) && (args.get(0) < this.srcImgData.getRGBs().length))
				{
				
					this.srcImgData.getRGBs()[args.get(0)] = new RGB((0xff & args.get(1)), (0xff & args.get(2)), (0xff & args.get(3)));
					
					updateScreen();
				}
				
				break;
				
			case OS9Defs.CMD_DWExt_Icon:
				setIcon(args.get(0));
				break;
		}
	}
	
	
	private void setIcon(final Byte icon)
	{
		MainWin.getDisplay().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				
				String ip = "/status/window_16.png";
				
				switch(icon)
				{
					case OS9Defs.CMD_DWExt_Icon_Busy:
						ip = "/status/active_16.png";
						break;
					
					case OS9Defs.CMD_DWExt_Icon_Error:
						ip = "/status/error_16.png";
						break;
					
					case OS9Defs.CMD_DWExt_Icon_Info:
						ip = "/status/info_16.png";
						break;
					
					case OS9Defs.CMD_DWExt_Icon_OK:
						ip = "/status/completed_16.png";
						break;
					
					case OS9Defs.CMD_DWExt_Icon_Warn:
						ip = "/status/failed_16.png";
						break;
					
				}
					
				if ((shell == null) && (ctabitem != null))
				{
					ctabitem.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, ip));
				}
				else if (shell != null)
				{
					shell.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, ip));
				}
				
			}});
		
		
	}




	public boolean isNeedsUpdate() {
		return needsUpdate;
	}




	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}




	public Boolean getRedrawInProgress() {
		return redrawInProgress;
	}




	public void setRedrawInProgress(Boolean redrawInProgress) {
		this.redrawInProgress = redrawInProgress;
	}




	public boolean isDoAntiAlias() {
		return doAntiAlias;
	}




	public void setDoAntiAlias(boolean doAntiAlias) {
		this.doAntiAlias = doAntiAlias;
	}



}
