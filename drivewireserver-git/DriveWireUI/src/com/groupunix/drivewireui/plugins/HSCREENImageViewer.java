package com.groupunix.drivewireui.plugins;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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

public class HSCREENImageViewer extends FileViewer
{

	private static final String TYPENAME = "HSCREEN Image Viewer";
	private static final String TYPEIMAGE = "/filetypes/image.png";
	
	
	byte[] lastImageData = null;
	Canvas canvas = null;
	Image image = null;
	
	private Color[] coco_cols;

	private Color[] palette;
	
	private DWFileSystemDirEntry lastDirEntry;
	private ToolBar toolBar_c;
	private Composite compositeMainToolBar;
	private ToolItem[] tltmColor = new ToolItem[4];
	private Image[] imagec = new Image[4];

	private int hscreen = 1;
	
	protected boolean border = false;
	private Color color_box;
	
	
	
	public HSCREENImageViewer(Composite parent, int style)
	{
		super(parent, style);

		setLayout(new BorderLayout(0, 0));
		
		setColor_box(new Color(getDisplay(), 128,128,128));
		
		setPalette(getDefaultPalette());
		
		createToolbar();
		createImgView();
	}

	
	


	private Color[] getDefaultPalette()
	{
		Color[] res = new Color[16];
		
		res[0] = new Color(getDisplay(), 0,255,0); // green
		res[1] = new Color(getDisplay(), 255,255,0); // yellow
		res[2] = new Color(getDisplay(), 0,0,255); // blue
		res[3] = new Color(getDisplay(), 255,0,0); // red
		res[4] = new Color(getDisplay(), 255,255,255); // buff
		res[5] = new Color(getDisplay(), 0,255,255); // cyan
		res[6] = new Color(getDisplay(), 255,0,255); // magenta
		res[7] = new Color(getDisplay(), 255, 128,0); // orange
		res[8] = new Color(getDisplay(), 0,0,0); // black 
		res[9] = new Color(getDisplay(), 255,0,0); // green
		res[10] = new Color(getDisplay(), 0,0,0); // black
		res[11] = new Color(getDisplay(), 255,255,255); // buff
		res[12] = new Color(getDisplay(), 0,0,0); // black
		res[13] = new Color(getDisplay(), 255,0,0); // green
		res[14] = new Color(getDisplay(), 0,0,0); // black
		res[15] = new Color(getDisplay(), 255,128,0); // orange
		
		return res;
	}







	private void createToolbar()
	{
		compositeMainToolBar = new Composite( this, SWT.NONE);
		compositeMainToolBar.setLayoutData(BorderLayout.NORTH);
		
		GradientHelper.applyVerticalGradientBG(compositeMainToolBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		compositeMainToolBar.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				GradientHelper.applyVerticalGradientBG(compositeMainToolBar, MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),MainWin.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
				
				
			} } );
		
		compositeMainToolBar.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		final CCombo comboPmode = new CCombo(compositeMainToolBar, SWT.BORDER);
		
		comboPmode.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboPmode.setBounds(10, 6, 88, 21);
		comboPmode.add("HSCREEN 1");
		comboPmode.add("HSCREEN 2");
		comboPmode.add("HSCREEN 3");
		comboPmode.add("HSCREEN 4");
		
		comboPmode.select(hscreen - 1);
		
		comboPmode.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				hscreen = comboPmode.getSelectionIndex()+1;
				drawImage();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				
			} } );
		
		
		ToolBar toolBar_3 = new ToolBar(compositeMainToolBar, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setBounds(110, 5, 135, 28);
		
		
		
		
		
		Label label_2 = new Label(compositeMainToolBar, SWT.SEPARATOR | SWT.VERTICAL);
		label_2.setBounds(250, 5, 2, 23);
		
		
		genColorBar();
		
		Label label_3 = new Label(compositeMainToolBar, SWT.SEPARATOR | SWT.VERTICAL);
		label_3.setBounds(370, 5, 2, 23);
		
		
	}
	
	
	
	
	protected void drawImage()
	{
		genColorBar();
		
		if (lastImageData != null)
		{
			viewFile(lastDirEntry, lastImageData);
		}
	}



	private void genColorBar()
	{
		if (toolBar_c == null)
		{
			toolBar_c = new ToolBar(compositeMainToolBar, SWT.FLAT | SWT.RIGHT);
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
				       // cd.setRGB(cur_cols[colnum-1].getRGB() );
				        
				        RGB tmp = cd.open();
				        
				        if (tmp != null)
				        {
				        	//cur_cols[colnum-1] =  new Color(e.display,tmp);
						
					        genColorBar();
							
							if (lastImageData != null)
							{
								viewFile(lastDirEntry, lastImageData);
							}
				        }
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e)
					{
						
					}} );
				
				
				
			}
			
			/*
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
			*/
		}
		
		
		
		
		
		toolBar_c.redraw();
		toolBar_c.update();
		compositeMainToolBar.redraw();
		compositeMainToolBar.update();
		
	}





	private void createImgView()
	{
		final ScrolledComposite composite_1 = new ScrolledComposite(this, SWT.V_SCROLL);
		composite_1.setLayout(new FillLayout());
		
		canvas = new Canvas(composite_1, SWT.DOUBLE_BUFFERED);
		
		composite_1.setContent(canvas);
		composite_1.setExpandVertical(true);
		composite_1.setExpandHorizontal(true);
		
		composite_1.getParent().addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event)
			{
				if (image != null)
				{
					int wx = canvas.getBounds().width;
					
					double effh = (double)image.getImageData().height;
					double effw = (double)image.getImageData().width;
					
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
		
		canvas.addPaintListener(new PaintListener() 
		{
			
			@Override
			
			public void paintControl(PaintEvent e)
			{
					
				if (image != null)
				{
					e.gc.setTextAntialias(SWT.OFF);
					e.gc.setAntialias(SWT.ON);
					e.gc.setAdvanced(true);
					
					int wx = canvas.getBounds().width;
					double effh = (double)image.getImageData().height;
					double effw = (double)image.getImageData().width;
					
					if (border == false)
					{
						effh -= 48.0;
						effw -= 64.0;
					}
					
					int hx = (int) Math.rint((double)wx * (effh / effw));
					
					if (border)
					{
						e.gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height , 0, 0, wx, hx);
					}
					else
					{
						e.gc.drawImage(image, 32, 24, image.getImageData().width - 64, image.getImageData().height - 48, 0, 0, wx, hx);
					}
					
					composite_1.setMinHeight(hx);
				}
			}
			
		});
		
	}
	

	public int getViewable(DWFileSystemDirEntry direntry, byte[] fc)
	{
		
		
		if ((fc[0] == 0) && (fc[3] == 0x40) && (fc[4] == 0))
		{
			return 3;
		}
		
		return 0;
	}




/*
	private boolean isPMODE4(byte[] fc)
	{
		if ((fc[0] == 0) && (fc[1] == 0x18) && (fc[2] == 0x01) && (fc[3] == 0x0e) && (fc[4] == 0))
			return true;
		
		return false;
	}



	private boolean isPMODE1(byte[] fc)
	{
		if ((fc[0] == 0) && (fc[1] == 0x0c) && (fc[2] == 0x01) && (fc[3] == 0x0e) && (fc[4] == 0))
			return true;
		
		return false;
	}
*/


	@SuppressWarnings("unused")
	public void viewFile(DWFileSystemDirEntry direntry, byte[] fc)
	{
		this.lastDirEntry = direntry;
		this.lastImageData = fc;
		
		int width = widthFor(this.hscreen);
		int height = 200;
		
		
		
		this.canvas.redraw();
	}





	private int widthFor(int pm)
	{
		if (pm < 3)
			return 320;
		
		return 640;
	}


	@SuppressWarnings("unused")
	private int colorsFor(int pm)
	{
		if (pm % 2 == 0)
			return 2;
			
		return 4;
	}
	
	@SuppressWarnings("unused")
	private int pagesFor(int pm)
	{
		if (pm == 2)
			return 16;
		if (pm == 3)
			return 2;
		
		return 4;
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





	public Color[] getCoco_cols() {
		return coco_cols;
	}





	public void setCoco_cols(Color[] coco_cols) {
		this.coco_cols = coco_cols;
	}





	public Color[] getPalette() {
		return palette;
	}





	public void setPalette(Color[] palette) {
		this.palette = palette;
	}





	public Image[] getImagec() {
		return imagec;
	}





	public void setImagec(Image[] imagec) {
		this.imagec = imagec;
	}





	public Color getColor_box() {
		return color_box;
	}





	public void setColor_box(Color color_box) {
		this.color_box = color_box;
	}

	
	
}
