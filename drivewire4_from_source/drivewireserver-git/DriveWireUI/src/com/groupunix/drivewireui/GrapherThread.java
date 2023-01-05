package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public class GrapherThread implements Runnable
{
	private long lastdisk = 0;
	private long lastvser = 0;
	
	private int interval = 2000;
	private int samples = 200;
	private boolean wanttodie = false;
	
	private int topgap = 20;
	private int ylabel = 30;
	private int xlabel = 70;
	
	private int[] disksamp = new int[samples];
	private int[] vsersamp = new int[samples];
	private long[] memfsamp = new long[samples];
	private long[] memtsamp = new long[samples];
	
	private int diskmax = 0;
	private int vsmax = 0;
	
	private int pos = 0;

	private Color colorGraphBGH;
	private Color colorGraphBGL;
	private Color colorLabel;
	private Color colorDiskOps;
	private Color colorMemGraphTotal;
	private Color colorMemGraphUsed;
	
	@Override
	public void run()
	{
		Thread.currentThread().setName("dwuiGrapher-" + Thread.currentThread().getId());
		
		// setup colors
		colorGraphBGH = new Color(MainWin.getDisplay(), 0x90,0x90,0x90);
		colorGraphBGL = new Color(MainWin.getDisplay(), 0x30,0x30,0x30);
		colorLabel = new Color(MainWin.getDisplay(), 0xB5,0xB5,0xB5);
		colorDiskOps = new Color(MainWin.getDisplay(), 0x80, 0x80,0xB5);
		colorMemGraphTotal = new Color(MainWin.getDisplay(), 0x80, 0xB5,0x80);
		colorMemGraphUsed = new Color(MainWin.getDisplay(), 0xB5, 0x80,0x80);
		
		
		
		initGraph(MainWin.graphMemUse);
		initGraph(MainWin.graphDiskOps);
		initGraph(MainWin.graphVSerialOps);
		
		
		// fill samples.. not sure if necessary
		for (int i = 0; i < samples;i++)
		{
			disksamp[i] = 0;
			vsersamp[i] = 0;
			memfsamp[i] = 0;
			memtsamp[i] = 0;
		}
		
		// wait for server status..
		
		while ((MainWin.serverStatus == null) && (!wanttodie))
		{
			try
			{
				Thread.sleep(interval);
			} 
			catch (InterruptedException e)
			{
				wanttodie = true;
			}
		}
		
		if (!wanttodie)
		{
			// init
			lastdisk = MainWin.serverStatus.getDiskops();
			lastvser = MainWin.serverStatus.getVserialops();
		
			try
			{
				Thread.sleep(interval);
			} 
			catch (InterruptedException e)
			{
				wanttodie = true;
			}
			
			while ((MainWin.serverStatus != null) && (!wanttodie))
			{
				// snapshot
				synchronized(MainWin.serverStatus)
				{
					this.disksamp[pos] = (int) (MainWin.serverStatus.getDiskops() - lastdisk);
					this.vsersamp[pos] = (int) (MainWin.serverStatus.getVserialops() - lastvser);
					
					this.memfsamp[pos] = MainWin.serverStatus.getMemfree();
					this.memtsamp[pos] = MainWin.serverStatus.getMemtotal();
					
					lastdisk = MainWin.serverStatus.getDiskops();
					lastvser = MainWin.serverStatus.getVserialops();
				}
				
				if (this.disksamp[pos] > this.diskmax)
					diskmax = this.disksamp[pos];
				
				if (this.vsersamp[pos] > this.vsmax)
					vsmax = this.vsersamp[pos];
				
				
				// draw graphs
				drawMemGraph();
				drawDiskOpsGraph();
				drawVSerialOpsGraph();
				
				
				MainWin.doDisplayAsync(new Runnable() {

					@Override
					public void run()
					{
						if (! MainWin.getDisplay().isDisposed() && (MainWin.canvasMemUse != null) && (!MainWin.canvasMemUse.isDisposed()))
						{
							MainWin.canvasMemUse.redraw();
						}
						
						if (! MainWin.getDisplay().isDisposed() && (MainWin.canvasDiskOps != null) && (!MainWin.canvasDiskOps.isDisposed()))
						{
							MainWin.canvasDiskOps.redraw();
						}
						
						if (! MainWin.getDisplay().isDisposed() && (MainWin.canvasVSerialOps != null) && (!MainWin.canvasVSerialOps.isDisposed()))
						{
							MainWin.canvasVSerialOps.redraw();
						}
					}
					
				});
				
				
				pos++;
				if (pos == samples)
					pos = 0;
				
				try
				{
					Thread.sleep(interval);
				} 
				catch (InterruptedException e)
				{
					wanttodie = true;
				}
			}
			
		}
		
	}

	
	private void initGraph(Image img)
	{
		GC gc = new GC(img);
		int width = MainWin.graphMemUse.getImageData().width;
		int height = MainWin.graphMemUse.getImageData().height;
		gc.setForeground(colorGraphBGH);
		gc.setBackground(colorGraphBGL);
		gc.fillGradientRectangle(0,0,width,height,true);
		gc.dispose();
	}

	
	
	
	private void drawVSerialOpsGraph()
	{
		GC gc = new GC(MainWin.graphVSerialOps);
		int width = MainWin.graphVSerialOps.getImageData().width - xlabel;
		int height = MainWin.graphVSerialOps.getImageData().height - ylabel - topgap;
	
		// get scale
		long maxops = 0;
		
		for (int i = 0;i<samples;i++)
		{
			if (this.vsersamp[i] > maxops)
				maxops = vsersamp[i];
		}
		
		double vscale = Double.valueOf(height) / Double.valueOf(maxops);
		double hscale = width / samples;
		
		// background
		gc.setForeground(colorGraphBGH);
		gc.setBackground(colorGraphBGL);
		gc.fillGradientRectangle(0,0,width + xlabel,height + ylabel + topgap,true);
		
		for (int i = 0; i< samples; i++)
		{
			// latest sample always last line
			int samp = pos + 1 + i;
			if (samp > (samples-1))
				samp = samp - samples;
			
			if (vsersamp[samp] > 0)
			{
				gc.setBackground(colorDiskOps);
				
				double top = vsersamp[samp] * vscale;
				gc.fillRectangle(i, height - (int)top + topgap, (int)hscale, (int)top);
			}
			
		}
		
		// labels
		gc.setFont(MainWin.fontGraphLabel);
		
		for (int i = 0; i<5; i++)
		{
			int y = i * (height / 5);
			double mb = (Double.valueOf(height - y) / vscale);
			gc.setForeground(colorLabel);
			gc.drawString(((int)Math.rint(mb)) + "", width+4, y-5 + topgap, true);
			
			gc.setLineStyle(SWT.LINE_DOT);
			gc.setForeground(colorGraphBGL);
			gc.drawLine(0, y+topgap, width, y+topgap);
			
		}
		
		gc.setForeground(colorLabel);
		gc.drawString(String.format("Virtual serial operations/sec:  %d   Max:  %d",this.vsersamp[pos], this.vsmax), 5, height+5+topgap, true);
		
		gc.dispose();
	}
	
	
	
	
	
	
	

	private void drawDiskOpsGraph()
	{
		GC gc = new GC(MainWin.graphDiskOps);
		int width = MainWin.graphDiskOps.getImageData().width - xlabel;
		int height = MainWin.graphDiskOps.getImageData().height - ylabel - topgap;
	
		// get scale
		long maxdisk = 0;
		
		for (int i = 0;i<samples;i++)
		{
			if (this.disksamp[i] > maxdisk)
				maxdisk = disksamp[i];
		}
		
		double vscale = Double.valueOf(height) / Double.valueOf(maxdisk);
		double hscale = width / samples;
		
		// background
		gc.setForeground(colorGraphBGH);
		gc.setBackground(colorGraphBGL);
		gc.fillGradientRectangle(0,0,width + xlabel,height + ylabel + topgap,true);
		
		for (int i = 0; i< samples; i++)
		{
			// latest sample always last line
			int samp = pos + 1 + i;
			if (samp > (samples-1))
				samp = samp - samples;
			
			if (disksamp[samp] > 0)
			{
				gc.setBackground(colorDiskOps);
				
				double top = disksamp[samp] * vscale;
				gc.fillRectangle(i, height - (int)top + topgap, (int)hscale, (int)top);
			}
			//MainWin.debug("Pos " + i + " Samp: " + samp + " rawT: " + memtsamp[samp] + "  Top: " +  top + "  vscale: " + vscale);
		}
		
		// labels
		gc.setFont(MainWin.fontGraphLabel);
		
		for (int i = 0; i<5; i++)
		{
			int y = i * (height / 5);
			double mb = (Double.valueOf(height - y) / vscale);
			gc.setForeground(colorLabel);
			gc.drawString(((int)Math.rint(mb)) + "", width+4, y-5 + topgap, true);
			
			gc.setLineStyle(SWT.LINE_DOT);
			gc.setForeground(colorGraphBGL);
			gc.drawLine(0, y+topgap, width, y+topgap);
			
		}
		
		gc.setForeground(colorLabel);
		gc.drawString(String.format("Disk operations/sec:  %d   Max:  %d",this.disksamp[pos], this.diskmax), 5, height+5+topgap, true);
		
		gc.dispose();
	}
	
	
	
	private void drawMemGraph()
	{
		if ((MainWin.getDisplay() != null) && !MainWin.getDisplay().isDisposed())
		{
			GC gc = new GC(MainWin.graphMemUse);
			int width = MainWin.graphMemUse.getImageData().width - xlabel;
			int height = MainWin.graphMemUse.getImageData().height - ylabel - topgap;
		
			// get scale
			long maxmem = 0;
			
			for (int i = 0;i<samples;i++)
			{
				if (memtsamp[i] > maxmem)
					maxmem = memtsamp[i];
			}
			
			double vscale = Double.valueOf(height) / Double.valueOf(maxmem);
			double hscale = width / samples;
			
			// background
			gc.setForeground(colorGraphBGH);
			gc.setBackground(colorGraphBGL);
			gc.fillGradientRectangle(0,0,width + xlabel,height + ylabel + topgap,true);
			
			for (int i = 0; i< samples; i++)
			{
				// latest sample always last line
				int samp = pos + 1 + i;
				if (samp > (samples-1))
					samp = samp - samples;
				
				if (memtsamp[samp] > 0)
				{
					gc.setBackground(colorMemGraphTotal);
					
					double top = (memtsamp[samp] * vscale);
					gc.fillRectangle(i, height - (int)top + topgap, 1, 2);
					
					gc.setBackground(colorMemGraphUsed);
					top = (memtsamp[samp] - memfsamp[samp]) * vscale;
					gc.fillRectangle(i, height - (int)top + topgap, (int)hscale, (int)top);
				}
				//MainWin.debug("Pos " + i + " Samp: " + samp + " rawT: " + memtsamp[samp] + "  Top: " +  top + "  vscale: " + vscale);
			}
			
			// labels
			gc.setFont(MainWin.fontGraphLabel);
			gc.setForeground(colorLabel);
			
			for (int i = 0; i<5; i++)
			{
				int y = i * (height / 5);
				double mb = (Double.valueOf(height - y) / vscale / 1024.0);
				gc.setForeground(colorLabel);
				gc.drawString(((int)Math.rint(mb)) + " MB", width+4, y-5 + topgap, true);
				
				gc.setLineStyle(SWT.LINE_DOT);
				gc.setForeground(colorGraphBGL);
				gc.drawLine(0, y+topgap, width, y+topgap);
				
			}
			gc.setForeground(colorLabel);
			gc.drawString(String.format("Total: %.1f MB   Used: %.1f MB   Free: %.1f MB",(this.memtsamp[pos] /1024.0), ((this.memtsamp[pos] - this.memfsamp[pos]) / 1024.0) , (this.memfsamp[pos] / 1024.0)   ), 5, height+5+topgap, true);
			
			gc.dispose();
		}		
	}

}
