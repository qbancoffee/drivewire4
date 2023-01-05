package com.groupunix.drivewireui.nineserver;

import java.util.Vector;

import org.eclipse.swt.graphics.Rectangle;

public class RedrawList
{
	private Vector<Rectangle> rects = new Vector<Rectangle>();
	private boolean inProgress = false;
	
	
	public synchronized void addRect(Rectangle r)
	{
		rects.add(r);
	}
	
	public synchronized Rectangle getArea()
	{
		
		if (rects.size() == 0)
			return(new Rectangle(0,0,0,0));
		
		Rectangle res = rects.remove(0);
		
		for (Rectangle r : rects)
		{
			res = res.union(r);
		}
		
		rects = new Vector<Rectangle>();
		
		return res;
	}

	public synchronized boolean hasArea()
	{
		if (this.rects.size() > 0)
			return true;
		return false;
	}

	
	
	public synchronized  Rectangle getArea(Rectangle bounds)
	{
		return getArea().intersection(bounds);
	}

	public synchronized void setInProgress(boolean b)
	{
		this.inProgress = b;
	}
	
	public synchronized boolean isInProgress()
	{
		return this.inProgress;
	}
	
	
}
