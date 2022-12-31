package com.groupunix.drivewireui.diskstatus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.groupunix.drivewireui.DiskDef;
import com.groupunix.drivewireui.MainWin;

public abstract class DiskStatusItem
{
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	
	private String disabledImage;
	private String enabledImage;
	private String clickImage;
	private boolean button = false;
	private String hovertext = "No hover text";
	private boolean mouseDown = false;
	private boolean enabled = false;
	
	protected DiskStatusWin diskwin;
	
	protected Canvas canvas;
	
	public DiskStatusItem(DiskStatusWin diskwin)
	{
		this.diskwin = diskwin;
	}
	
	public abstract void setDisk(DiskDef diskDef);
	public abstract void handleClick();
	
	
	
	public void setX(int x)
	{
		this.x = x;
	}
	public int getX()
	{
		return x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	public int getY()
	{
		return y;
	}
	
	public void setButton(boolean button)
	{
		this.button = button;
	}
	
	public boolean isButton()
	{
		return button;
	}
	
	public void setDisabledImage(String disabledImage)
	{
		this.disabledImage = disabledImage;
		
	}
	public String getDisabledImage()
	{
		return disabledImage;
	}
	
	public void setEnabledImage(String enabledImage)
	{
		this.enabledImage = enabledImage;
	}
	public String getEnabledImage()
	{
		return enabledImage;
	}
	
	public void setClickImage(String clickImage)
	{
		this.clickImage = clickImage;
	}
	public String getClickImage()
	{
		return clickImage;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}
	public int getWidth()
	{
		return width;
	}
	public void setHeight(int height)
	{
		this.height = height;
	}
	public int getHeight()
	{
		return height;
	}
	public void setHovertext(String hovertext)
	{
		this.hovertext = hovertext;
	}
	public String getHovertext()
	{
		return hovertext;
	}
	public void setMouseDown(boolean b)
	{
		this.mouseDown  = b;
		
	}
	
	public boolean isMouseDown()
	{
		return(this.mouseDown);
	}

	public void createCanvas(final Composite composite)
	{
		canvas = new Canvas(composite, SWT.NONE);
		
		canvas.setBackground(DiskStatusWin.colorDiskBG);
		canvas.setLocation(this.getX(), this.getY());
		canvas.setSize(getWidth(), getHeight());
		
		canvas.addPaintListener(new PaintListener()
		{
		   public void paintControl(PaintEvent event)
		   {
			  if (getCurrentImage() != null)
				  event.gc.drawImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, getCurrentImage()),0,0);
		   
		   }
		 });
		
		
		
		canvas.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseDown(MouseEvent e) 
			{
				if (isButton())
				{
					canvas.setLocation(getX()+2, getY()+2);
				}
			}
			
			@Override
			public void mouseUp(MouseEvent e) 
			{
				if (isButton())
				{
					canvas.setLocation(getX(), getY());
					composite.setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_ARROW));
					
					handleClick();
						
					
				}
			}
		
		});
		
		
		canvas.addMouseTrackListener(new MouseTrackAdapter() 
		{
			@Override
			public void mouseEnter(MouseEvent e) 
			{
				if (isButton())
				{
					composite.setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_HAND));
				}
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) 
			{
				if (isButton())
				{
					composite.setCursor(new Cursor(composite.getDisplay(), SWT.CURSOR_ARROW));
				}
				
			}
			
			
			@Override
			public void mouseHover(MouseEvent e) 
			{
				//MainWin.debug("hover " + e.count + " " + e.time);
			}
			
			
		});
		
		
	}


	
	
	public String getCurrentImage()
	{
		if (this.enabled)
				return(this.getEnabledImage());
		else
				return(this.getDisabledImage());
		
	}

	
	public void setEnabled(boolean b)
	{
		this.enabled = b;
	}

	public boolean isEnabled()
	{
		return(this.enabled);
	}
	
	
	public void redraw()
	{
		if ((this.canvas != null) && !this.canvas.isDisposed())
			this.canvas.redraw();
	}
	
	public void setVisible(boolean b)
	{
		this.canvas.setVisible(b);
	}
	
}
