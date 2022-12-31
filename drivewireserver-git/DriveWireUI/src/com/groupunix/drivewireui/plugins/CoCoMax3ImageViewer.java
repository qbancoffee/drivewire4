package com.groupunix.drivewireui.plugins;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireui.GradientHelper;
import com.groupunix.drivewireui.MainWin;

public class CoCoMax3ImageViewer extends FileViewer
{

	private static final String TYPENAME = "CoCoMAX 3 Image";
	private static final String TYPEIMAGE = "/plugins/cocomax.png";
	
	byte[] lastPModeData = null;
	Canvas canvasPMode = null;
	Image imagePMode = null;
	private Color[] coco_cols;

	
	private int pmode = 4;
	int colorset = 0;
	int height = 384;
	int width = 256;
	private DWFileSystemDirEntry lastDirEntry;
	
	public CoCoMax3ImageViewer(Composite parent, int style)
	{
		super(parent, style);

		setLayout(new BorderLayout(0, 0));
		
		coco_cols = new Color[9];
		coco_cols[0] = new Color(parent.getDisplay(), new RGB(0,0,0));  // black
		coco_cols[1] = new Color(parent.getDisplay(), new RGB(0,255,0)); // green
		coco_cols[2] = new Color(parent.getDisplay(), new RGB(255,255,0)); // yellow
		coco_cols[3] = new Color(parent.getDisplay(), new RGB(0,0,255));  // blue
		coco_cols[4] = new Color(parent.getDisplay(), new RGB(255,0,0));  // red
		coco_cols[5] = new Color(parent.getDisplay(), new RGB(255,255,0xA0)); // buff
		coco_cols[6] = new Color(parent.getDisplay(), new RGB(0,255,255));  // cyan
		coco_cols[7] = new Color(parent.getDisplay(), new RGB(255,0,255));  // magenta
		coco_cols[8] = new Color(parent.getDisplay(), new RGB(255,0x80,0));  // orange
		
		
		
		createToolbar();
		createImgView();
	}

	
	


	private void createToolbar()
	{
		final Composite compositePModeBar = new Composite( this, SWT.NONE);
		compositePModeBar.setLayoutData(BorderLayout.NORTH);
		
		GradientHelper.applyVerticalGradientBG(compositePModeBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		compositePModeBar.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositePModeBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		compositePModeBar.setBackgroundMode(SWT.INHERIT_FORCE);
		
		

		ToolBar toolBar_2 = new ToolBar(compositePModeBar, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setBounds(0, 0, 140, 28);
		
		ToolItem tltmPmode3 = new ToolItem(toolBar_2, SWT.RADIO);
		tltmPmode3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				pmode = 3;
				if (lastPModeData != null)
				{
					viewFile(lastDirEntry, lastPModeData);
				}
			}
		});
		tltmPmode3.setText("PMODE 3");
		
		ToolItem tltmPmode4 = new ToolItem(toolBar_2, SWT.RADIO);
		tltmPmode4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				pmode = 4;
				if (lastPModeData != null)
				{
					viewFile(lastDirEntry, lastPModeData);
				}
			}
		});
		tltmPmode4.setText("PMODE 4");
		
		tltmPmode4.setSelection(true);
		
		
		
		
		
		ToolBar toolBar_3 = new ToolBar(compositePModeBar, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setBounds(150, 0, 250, 28);
		
		ToolItem tltmColorset = new ToolItem(toolBar_3, SWT.RADIO);
		tltmColorset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				colorset = 0;
				if (lastPModeData != null)
				{
					viewFile(lastDirEntry, lastPModeData);
				}
			}
		});
		tltmColorset.setText("Colorset 1");
		tltmColorset.setSelection(true);
		
		ToolItem tltmColorset_1 = new ToolItem(toolBar_3, SWT.RADIO);
		tltmColorset_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				colorset = 1;
				if (lastPModeData != null)
				{
					viewFile(lastDirEntry, lastPModeData);
				}
			}
		});
		tltmColorset_1.setText("Colorset 2");
		
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
				int wx = canvasPMode.getBounds().width;
				
				int hx = (int) Math.rint(wx * 1.5);
				
				if (height == 192)
					hx = (int) Math.rint(wx * .75);
				
				//canvasPMode.setSize(wx,hx);
				composite_1.setMinHeight(hx);
				
			} } );
		
		canvasPMode.addPaintListener(new PaintListener() 
		{
			int wx;
			int hx;
			
			@Override
			
			public void paintControl(PaintEvent e)
			{
				
				
				e.gc.setTextAntialias(SWT.OFF);
				e.gc.setAntialias(SWT.ON);
				e.gc.setAdvanced(true);
				
				wx = canvasPMode.getBounds().width;
				hx = (int) Math.rint(wx * 1.5);
				
				if (height == 192)
					hx = (int) Math.rint(wx * .75);
				
				if (imagePMode != null)
					e.gc.drawImage(imagePMode, 0, 0, imagePMode.getImageData().width, imagePMode.getImageData().height, 0, 0, wx, hx);
					//e.gc.drawImage(imagePMode, 0, 0);
			}
			
		});
		
	}
	
	
	public int getViewable(DWFileSystemDirEntry direntry, byte[] fc)
	{
		
		
		return 0;
	}




	public void viewFile(DWFileSystemDirEntry direntry, byte[] fc)
	{
		this.lastDirEntry = direntry;
		this.lastPModeData = fc;
		
		int width = 320;
		
		this.height = 384;
		
		// tall
		if (fc[0] == 0)
			this.height = 192;
		
		// 16 color bytes?
		
		// anirate
		
		// cycrate
		
		// CM3CYC
		
		// animation flag?
		
		// cyc flag,
		
		// skip 243 bytes of patterns?
		
		
		
		this.imagePMode = new Image(this.getDisplay(), width, height);
		GC gc = new GC(this.imagePMode);
			
		int x = 0;
		int y = 0;
		
		
		
		for (int pos = 5;pos < fc.length;pos++)
		{
			// draw byte
			int dbyte = fc[pos] & 0xff;
			
			if (pmode == 4)
			{
				for (int i = 0;i<8;i++)
				{
					if ((dbyte & 0x0080) == 0)
						gc.setForeground(this.coco_cols[0]);
					else
						gc.setForeground(this.coco_cols[1 + (4 * this.colorset)]);
					
					gc.drawPoint(x, y);
					
					x++;
					if (x == width)
					{
						x = 0;
						y++;
					}
					
					dbyte = dbyte << 1;
				}
			}
			else if (pmode == 3)
			{
				for (int i = 0;i<4;i++)
				{
					gc.setBackground(this.coco_cols[(dbyte & 0x00C0)/64 + 1 + (4 * this.colorset)]);
					
					gc.fillRectangle(x*2, y, 2, 1);
					
					x++;
					if (x == width)
					{
						x = 0;
						y++;
					}
					
					dbyte = dbyte << 2;
				}
			}
		}
		
		
		//gc.dispose();
		
		this.canvasPMode.redraw();
	}




	@Override
	public String getTypeName()
	{	
		return TYPENAME;
	}





	@Override
	public String getTypeIcon()
	{
		// TODO Auto-generated method stub
		return TYPEIMAGE;
	}


	public int getPriority()
	{
		return 9;
	}



	
}
