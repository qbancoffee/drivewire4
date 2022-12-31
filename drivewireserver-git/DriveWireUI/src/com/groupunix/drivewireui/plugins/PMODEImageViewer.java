package com.groupunix.drivewireui.plugins;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;

public class PMODEImageViewer extends FileViewer
{

	private static final String TYPENAME = "PMODE Image Viewer";
	private static final String TYPEIMAGE = "/filetypes/image.png";
	
	private static final int XSIZE = 320;

	
	byte[] lastPModeData = null;
	Canvas canvasPMode = null;
	Image imagePMode = null;
	private Color[][][] coco_cols;
	private Color[][] artifact_colours;
	private Color[] cur_cols;
	
	private DWFileSystemDirEntry lastDirEntry;
	private ToolBar toolBar_c;
	private Composite compositePModeBar;
	private ToolItem[] tltmColor = new ToolItem[4];
	private Image[] imagec = new Image[4];
	
	private int artifacts = 1;
	private int pmode = 4;
	private int colorset = 1;
	protected boolean border = false;
	private Color color_box;
	private CCombo comboPmode;
	private ToolItem tltmColorset0;
	private ToolItem tltmColorset1;
	
	
	public PMODEImageViewer(Composite parent, int style)
	{
		super(parent, style);

		setLayout(new BorderLayout(0, 0));
		
		color_box = new Color(getDisplay(), 128,128,128);
		
		coco_cols = new Color[2][2][4];
		
		// 2 color modes
		coco_cols[0][0][0] = new Color(parent.getDisplay(), new RGB(0,0,0));  // black
		coco_cols[0][0][1] = new Color(parent.getDisplay(), new RGB(0,255,0)); // green
		
		coco_cols[0][1][0] = new Color(parent.getDisplay(), new RGB(0,0,0)); // black
		coco_cols[0][1][1] = new Color(parent.getDisplay(), new RGB(255,255,255));  // buff
		
		// 4 color modes
		coco_cols[1][0][0] = new Color(parent.getDisplay(), new RGB(0,255,0)); // green
		coco_cols[1][0][1] = new Color(parent.getDisplay(), new RGB(255,255,0));  // yellow
		coco_cols[1][0][2] = new Color(parent.getDisplay(), new RGB(0,0,255)); // blue
		coco_cols[1][0][3] = new Color(parent.getDisplay(), new RGB(255,0,0));  // red
		
		coco_cols[1][1][0] = new Color(parent.getDisplay(), new RGB(255,255,255)); // buff
		coco_cols[1][1][1] = new Color(parent.getDisplay(), new RGB(0,255,255));  // cyan
		coco_cols[1][1][2] = new Color(parent.getDisplay(), new RGB(255,0,255)); // magenta
		coco_cols[1][1][3] = new Color(parent.getDisplay(), new RGB(255,128,0));  // orange
		
		// artifact colors..
		// shamelessly borrowed from xroar which supposedly borrowed them from mess
		artifact_colours = new Color[2][32];
		
		artifact_colours[0][0x00] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[0][0x01] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[0][0x02] = new Color(parent.getDisplay(), new RGB(0x00, 0x32, 0x78));
		artifact_colours[0][0x03] = new Color(parent.getDisplay(), new RGB(0x00, 0x28, 0x00));
		artifact_colours[0][0x04] = new Color(parent.getDisplay(), new RGB(0xff, 0x8c, 0x64));
		artifact_colours[0][0x05] = new Color(parent.getDisplay(), new RGB(0xff, 0x8c, 0x64));
		artifact_colours[0][0x06] = new Color(parent.getDisplay(), new RGB(0xff, 0xd2, 0xff));
		artifact_colours[0][0x07] = new Color(parent.getDisplay(), new RGB(0xff, 0xf0, 0xc8));
		artifact_colours[0][0x08] = new Color(parent.getDisplay(), new RGB(0x00, 0x32, 0x78));
		artifact_colours[0][0x09] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x3c));
		artifact_colours[0][0x0a] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[0][0x0b] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[0][0x0c] = new Color(parent.getDisplay(), new RGB(0xd2, 0xff, 0xd2));
		artifact_colours[0][0x0d] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[0][0x0e] = new Color(parent.getDisplay(), new RGB(0x64, 0xf0, 0xff));
		artifact_colours[0][0x0f] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[0][0x10] = new Color(parent.getDisplay(), new RGB(0x3c, 0x00, 0x00));
		artifact_colours[0][0x11] = new Color(parent.getDisplay(), new RGB(0x3c, 0x00, 0x00));
		artifact_colours[0][0x12] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[0][0x13] = new Color(parent.getDisplay(), new RGB(0x00, 0x28, 0x00));
		artifact_colours[0][0x14] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[0][0x15] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[0][0x16] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[0][0x17] = new Color(parent.getDisplay(), new RGB(0xff, 0xf0, 0xc8));
		artifact_colours[0][0x18] = new Color(parent.getDisplay(), new RGB(0x28, 0x00, 0x28));
		artifact_colours[0][0x19] = new Color(parent.getDisplay(), new RGB(0x28, 0x00, 0x28));
		artifact_colours[0][0x1a] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[0][0x1b] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[0][0x1c] = new Color(parent.getDisplay(), new RGB(0xff, 0xf0, 0xc8));
		artifact_colours[0][0x1d] = new Color(parent.getDisplay(), new RGB(0xff, 0xf0, 0xc8));
		artifact_colours[0][0x1e] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[0][0x1f] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));

		artifact_colours[1][0x00] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[1][0x01] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[1][0x02] = new Color(parent.getDisplay(), new RGB(0xb4, 0x3c, 0x1e));
		artifact_colours[1][0x03] = new Color(parent.getDisplay(), new RGB(0x28, 0x00, 0x28));
		artifact_colours[1][0x04] = new Color(parent.getDisplay(), new RGB(0x46, 0xc8, 0xff));
		artifact_colours[1][0x05] = new Color(parent.getDisplay(), new RGB(0x46, 0xc8, 0xff));
		artifact_colours[1][0x06] = new Color(parent.getDisplay(), new RGB(0xd2, 0xff, 0xd2));
		artifact_colours[1][0x07] = new Color(parent.getDisplay(), new RGB(0x64, 0xf0, 0xff));
		artifact_colours[1][0x08] = new Color(parent.getDisplay(), new RGB(0xb4, 0x3c, 0x1e));
		artifact_colours[1][0x09] = new Color(parent.getDisplay(), new RGB(0x3c, 0x00, 0x00));
		artifact_colours[1][0x0a] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[1][0x0b] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[1][0x0c] = new Color(parent.getDisplay(), new RGB(0xff, 0xd2, 0xff));
		artifact_colours[1][0x0d] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[1][0x0e] = new Color(parent.getDisplay(), new RGB(0xff, 0xf0, 0xc8));
		artifact_colours[1][0x0f] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[1][0x10] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x3c));
		artifact_colours[1][0x11] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x3c));
		artifact_colours[1][0x12] = new Color(parent.getDisplay(), new RGB(0x00, 0x00, 0x00));
		artifact_colours[1][0x13] = new Color(parent.getDisplay(), new RGB(0x28, 0x00, 0x28));
		artifact_colours[1][0x14] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[1][0x15] = new Color(parent.getDisplay(), new RGB(0x00, 0x80, 0xff));
		artifact_colours[1][0x16] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[1][0x17] = new Color(parent.getDisplay(), new RGB(0x64, 0xf0, 0xff));
		artifact_colours[1][0x18] = new Color(parent.getDisplay(), new RGB(0x00, 0x28, 0x00));
		artifact_colours[1][0x19] = new Color(parent.getDisplay(), new RGB(0x00, 0x28, 0x00));
		artifact_colours[1][0x1a] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[1][0x1b] = new Color(parent.getDisplay(), new RGB(0xff, 0x80, 0x00));
		artifact_colours[1][0x1c] = new Color(parent.getDisplay(), new RGB(0x64, 0xf0, 0xff));
		artifact_colours[1][0x1d] = new Color(parent.getDisplay(), new RGB(0x64, 0xf0, 0xff));
		artifact_colours[1][0x1e] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		artifact_colours[1][0x1f] = new Color(parent.getDisplay(), new RGB(0xff, 0xff, 0xff));
		
		
		
		
		cur_cols = getColorsFor(pmode, colorset);
		
		createToolbar();
		createImgView();
	}

	
	


	private Color[] getColorsFor(int pm, int colset)
	{
		Color[] res = new Color[4];
		
		if (pm % 2 == 0)
		{
			res[0] = coco_cols[0][colset][0];
			res[1] = coco_cols[0][colset][1];
		}
		else
		{
			res[0] = coco_cols[1][colset][0];
			res[1] = coco_cols[1][colset][1];
			res[2] = coco_cols[1][colset][2];
			res[3] = coco_cols[1][colset][3];
		}
	
		return res;
	}





	private void createToolbar()
	{
		compositePModeBar = new Composite( this, SWT.NONE);
		compositePModeBar.setLayoutData(BorderLayout.NORTH);
		
		GradientHelper.applyVerticalGradientBG(compositePModeBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		compositePModeBar.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositePModeBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		compositePModeBar.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		comboPmode = new CCombo(compositePModeBar, SWT.BORDER);
		
		comboPmode.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboPmode.setBounds(10, 6, 88, 21);
		comboPmode.add("PMODE 0");
		comboPmode.add("PMODE 1");
		comboPmode.add("PMODE 2");
		comboPmode.add("PMODE 3");
		comboPmode.add("PMODE 4");
		
		comboPmode.select(pmode);
		
		comboPmode.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				pmode = ((CCombo) e.widget).getSelectionIndex();
				drawImage();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				
			} } );
		
		
		ToolBar toolBar_3 = new ToolBar(compositePModeBar, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setBounds(110, 5, 135, 28);
		
		
		
		tltmColorset0 = new ToolItem(toolBar_3, SWT.RADIO);
		tltmColorset0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				colorset = 0;
				drawImage();
			}
		});
		tltmColorset0.setText("Colorset 0");
		
		tltmColorset1 = new ToolItem(toolBar_3, SWT.RADIO);
		tltmColorset1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				colorset = 1;
				drawImage();
			}
		});
		tltmColorset1.setText("Colorset 1");
		
		tltmColorset1.setSelection(true);
		
		Label label_2 = new Label(compositePModeBar, SWT.SEPARATOR | SWT.VERTICAL);
		label_2.setBounds(250, 5, 2, 23);
		
		
		genColorBar();
		
		Label label_3 = new Label(compositePModeBar, SWT.SEPARATOR | SWT.VERTICAL);
		label_3.setBounds(370, 5, 2, 23);
		
		
		ToolBar toolBar_4 = new ToolBar(compositePModeBar, SWT.FLAT | SWT.RIGHT);
		toolBar_4.setBounds(385, 5, 360, 28);
		
		ToolItem tltmArtifact = new ToolItem(toolBar_4, SWT.RADIO);
		tltmArtifact.setText("Artifacts 1");

		tltmArtifact.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				artifacts = 1;
				drawImage();
			}
		} );

		tltmArtifact.setSelection(true);
		
		tltmArtifact = new ToolItem(toolBar_4, SWT.RADIO);
		tltmArtifact.setText("Artifacts 2");

		tltmArtifact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				artifacts = 2;
				drawImage();
			}
		} );
		
		tltmArtifact = new ToolItem(toolBar_4, SWT.RADIO);
		tltmArtifact.setText("Artifacts Off");
	
		tltmArtifact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				artifacts = 0;
				drawImage();
			}
		} );
		
		final ToolItem tltmBorder = new ToolItem(toolBar_4, SWT.CHECK);
		tltmBorder.setText("Border");
	
		tltmBorder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				border = tltmBorder.getSelection();
				drawImage();
			}
		} );
		
	}
	
	
	
	
	protected void drawImage()
	{
		cur_cols = getColorsFor(pmode, colorset);
		genColorBar();
		
		if (lastPModeData != null)
		{
			viewFile(lastDirEntry, lastPModeData);
		}
	}





	private void genColorBar()
	{
		if (toolBar_c == null)
		{
			toolBar_c = new ToolBar(compositePModeBar, SWT.FLAT | SWT.RIGHT);
			toolBar_c.setBounds(265, 6, 100, 24);
			
		}
		
		for (int i = 1;i<5;i++)
		{
			final int colnum = i;
			
			if (tltmColor[i-1] == null)
			{	
				tltmColor[i-1] = new ToolItem(toolBar_c, SWT.NONE);
				//tltmColor[i-1].setText("Color " + i);
				
				tltmColor[i-1].addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						ColorDialog cd = new ColorDialog(e.display.getActiveShell());
				        cd.setText("Choose Color " + colnum);
				        cd.setRGB(cur_cols[colnum-1].getRGB() );
				        
				        RGB tmp = cd.open();
				        
				        if (tmp != null)
				        {
				        	cur_cols[colnum-1] =  new Color(e.display,tmp);
						
					        genColorBar();
							
							if (lastPModeData != null)
							{
								viewFile(lastDirEntry, lastPModeData);
							}
				        }
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e)
					{
						
					}} );
				
				
				
			}
			
			
			if ((pmode == 4) && (colorset == 1) && (artifacts > 0))
			{
				tltmColor[i-1].setImage(null);
				tltmColor[i-1].setEnabled(false);
			}
			else
			{
				imagec [i-1] = new Image(this.getDisplay(), 16, 16);
				
				GC gc = new GC(imagec[i-1]);
			
				if (i < colorsFor(pmode)+1)
				{
					gc.setBackground(color_box);
					gc.fillRectangle(0, 0, 16, 16);
					
					gc.setBackground(cur_cols[i-1]);
					
					gc.fillRectangle(1, 1, 14 , 14);
					
					tltmColor[i-1].setEnabled(true);
				}
				else
				{
					gc.setBackground(color_box);
					gc.fillRectangle(0, 0, 16, 16);
					
					//gc.setBackground(new Color(getDisplay(), 128,128,128));
					
					//gc.fillRectangle(1, 1, 14 , 14);
					
					tltmColor[i-1].setEnabled(false);
				}
					
				gc.dispose();
				
				tltmColor[i-1].setImage(imagec[i-1]);
			}
		}
		
		
		
		
		
		toolBar_c.redraw();
		toolBar_c.update();
		compositePModeBar.redraw();
		compositePModeBar.update();
		
	}





	





	private void createImgView()
	{
		final ScrolledComposite composite_1 = new ScrolledComposite(this, SWT.V_SCROLL);
		composite_1.setLayout(new FillLayout());
		
		canvasPMode = new Canvas(composite_1, SWT.DOUBLE_BUFFERED);
		
		composite_1.setContent(canvasPMode);
		composite_1.setExpandVertical(true);
		composite_1.setExpandHorizontal(true);
		
		composite_1.getParent().addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				if (imagePMode != null)
				{
					int wx = canvasPMode.getBounds().width;
					
					double effh = (double)imagePMode.getImageData().height;
					double effw = (double)imagePMode.getImageData().width;
					
					if (border == false)
					{
						effh -= 48.0;
						effw -= 64.0;
					}
					
					int hx = (int) Math.rint((double)wx * (effh / effw));
					
					
					//canvasPMode.setSize(wx,hx);
					composite_1.setMinHeight(hx);
				}
			} } );
		
		canvasPMode.addPaintListener(new PaintListener() 
		{
			
			@Override
			
			public void paintControl(PaintEvent e)
			{
					
				if (imagePMode != null)
				{
					e.gc.setTextAntialias(SWT.OFF);
					e.gc.setAntialias(SWT.ON);
					e.gc.setAdvanced(true);
					
					int wx = canvasPMode.getBounds().width;
					double effh = (double)imagePMode.getImageData().height;
					double effw = (double)imagePMode.getImageData().width;
					
					if (border == false)
					{
						effh -= 48.0;
						effw -= 64.0;
					}
					
					int hx = (int) Math.rint((double)wx * (effh / effw));
					
					if (border)
					{
						e.gc.drawImage(imagePMode, 0, 0, imagePMode.getImageData().width, imagePMode.getImageData().height , 0, 0, wx, hx);
					}
					else
					{
						e.gc.drawImage(imagePMode, 32, 24, imagePMode.getImageData().width - 64, imagePMode.getImageData().height - 48, 0, 0, wx, hx);
					}
					
					composite_1.setMinHeight(hx);
				}
			}
			
		});
		
	}
	

	public int getViewable(DWFileSystemDirEntry direntry, byte[] fc)
	{
		if ((fc[0] == 0) && (fc[3] == 0x0e) && (fc[4] == 0))
		{
			return 3;
		}
		
		return 0;
	}






	private void guessAtPMODE(DWFileSystemDirEntry direntry, byte[] fc)
	{
		// pmode 1 size
		if ((fc[0] == 0) && (fc[1] == 0x0c) && (fc[2] == 0x01) && (fc[3] == 0x0e) && (fc[4] == 0))
		{
			this.pmode = 1;
			
			if (direntry.getFileExt().equals("P10"))
			{
				this.colorset = 0;
			}
			else
			{
				this.colorset = 1;
			}
		}
		// pmode 4 size..
		else if ((fc[0] == 0) && (fc[1] == 0x18) && (fc[2] == 0x01) && (fc[3] == 0x0e) && (fc[4] == 0))
		{
			this.pmode = 4;
			
			if (direntry.getFileExt().equals("P40"))
			{
				this.colorset = 0;
			}
			else
			{
				this.colorset = 1;
			}
		}
		// cocomax pmode 41
		else if (((fc.length - 10) % 6144 == 0)  &&  (fc[0] == 0) && ((fc[1] == 0x30) || (fc[1] == 0x18)) && (fc[2] == 0) && (fc[3] == 0x0e) && (fc[4] == 0))
		{
			this.pmode = 4;
			this.colorset = 1;
		}
		// more?
		// do file exts..
		else if (direntry.getFileExt().equals("P11"))
		{
			this.pmode = 1;
			this.colorset = 1;
		}
		else if (direntry.getFileExt().equals("P41"))
		{
			this.pmode = 4;
			this.colorset = 1;
		}
		else if (direntry.getFileExt().equals("MAX"))
		{
			this.pmode = 4;
			this.colorset = 1;
		}
		else if (direntry.getFileExt().equals("PIX"))
		{
			this.pmode = 4;
			this.colorset = 1;
		}	
		
		cur_cols = getColorsFor(pmode, colorset);
		comboPmode.select(pmode);
		if (this.colorset == 0)
		{
			tltmColorset0.setSelection(true);
			tltmColorset1.setSelection(false);
		}
		else
		{
			tltmColorset1.setSelection(true);
			tltmColorset0.setSelection(false);
		}
		
		genColorBar();
	}
	

	public void viewFile(DWFileSystemDirEntry direntry, byte[] fc)
	{
		if ((this.lastDirEntry == null) || (this.lastDirEntry != direntry))
		{
			guessAtPMODE(direntry, fc);
		}
		
		this.lastDirEntry = direntry;
		this.lastPModeData = fc;
		
		
		
		int width = widthFor(this.pmode);
		int height = heightFor(this.pmode) * (8 / pagesFor(pmode));
		
		
		byte[][] pixels = new byte[width][height];
		
			
		int x = 0;
		int y = 0;
		
		byte pixperbyte = (byte) (16 / colorsFor(pmode));
		
		for (int pos = 5;(pos < fc.length-6) && (y < height);pos++)
		{
			int dbyte = fc[pos] & 0xff;
			
			
			for (int i = 0;i<pixperbyte;i++)
			{
				int pval = 0;
				
				for (int j = 0;j<(8/pixperbyte);j++)
				{
					pval = pval << 1;
					
					if ((dbyte & 0x0080) == 128)
					{
						pval++;
					}
					
					dbyte = dbyte << 1;
				}
				
				
				pixels[x][y] = (byte) pval;
				
				x++;
			}	
			
			if (x == width)
			{
				x = 0;
				y++;
			}
						
		}
	
		this.imagePMode = new Image(this.getDisplay(), XSIZE, 192 * ( (y-1) / heightFor(this.pmode) + 1) + 48);
		GC gc = new GC(this.imagePMode);
		
		// actually draw the thing
		
		gc.setBackground(coco_cols[0][this.colorset][1]);
		gc.fillRectangle(0, 0, XSIZE, imagePMode.getImageData().height );

			
		int xscale = 256 / width;
		int yscale = 192 / heightFor(this.pmode);
		
		int octet = 0;
		int aindex = 31;
		
		for (int i = 0;i<y;i++)
		{
			aindex = 31;
			
			for (int j = 0;j<width;j++)
			{
				
				if ((pmode == 4) && (this.colorset == 1) && (this.artifacts > 0))
				{
					// artifacts - adapted to java from xroar source
					octet = (pixels[j][i] & 0xff);
					
					aindex = ((aindex << 1) | (octet)) & 0x1f;
					
					gc.setForeground(artifact_colours[(j&1)^(this.artifacts-1)][aindex]);
					gc.drawPoint(30 + j, 24 + i);
				}
				else
				{
					// non artifact
					gc.setBackground(cur_cols[ pixels[j][i] ]);
					gc.fillRectangle(32 + j*xscale, 24 + i*yscale, xscale, yscale);
					
				}
			}
			
			if ((pmode == 4) && (this.colorset == 1) && (this.artifacts > 0))
			{
				for (int j = 286;j<291;j++)
				{
					aindex = ((aindex << 1) | 1) & 0x1f;
					gc.setForeground(artifact_colours[(j&1)^(this.artifacts-1)][aindex]);
					gc.drawPoint(j, 24 + i);
				}
			}
			
		}
	
		gc.dispose();
		
		this.canvasPMode.redraw();
	}




	private int heightFor(int pm)
	{
		if (pm < 2)
			return 96;
		
		return 192;
	}



	private int widthFor(int pm)
	{
		if (pm < 4)
			return 128;
		
		return 256;
	}


	private int colorsFor(int pm)
	{
		if (pm % 2 == 0)
			return 2;
			
		return 4;
	}
	
	private int pagesFor(int pm)
	{
		if (pm >2)
			return 4;
		if (pm>0)
			return 2;
		
		return 1;
	}
	



	@Override
	public String getTypeName()
	{	
		return TYPENAME;
	}



	@Override
	public String getTypeIcon()
	{
		
		return TYPEIMAGE;
	}

	
	
}
