package com.groupunix.drivewireui;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class DiskDef implements Cloneable
{
	private int drive = -1;
	private boolean loaded = false;
	
	private boolean changed = true;
	private boolean diskchanged = false;
	
		
	
	private HierarchicalConfiguration params;
	
	private Image diskgraph = new Image(null, DiskWin.DGRAPH_WIDTH, DiskWin.DGRAPH_HEIGHT);
	private HashMap<Integer,Integer> sectors = new HashMap<Integer,Integer>();
	
	
	private double graphscale = 1.0;
	private GC gc;
	
	private int lastread = 0;
	private int lastwrite = 0;

	
	
	private DiskWin diskwin = null;
	private boolean graphchanged = true;
	private DiskAdvancedWin paramwin = null;
	
	
	
	
	public DiskDef(int driveno)
	{
		this.drive = driveno;
		this.params = new HierarchicalConfiguration();
		this.gc = new GC(this.diskgraph); 
		this.makeNewDGraph();
		
		if (MainWin.config.getBoolean("DiskWin_" + driveno + "_open", false))
		{
			final DiskDef ref = this;
			MainWin.getDisplay().asyncExec(new Runnable() {
				  public void run()
				  {
					  setDiskwin(new DiskWin(ref , MainWin.getDiskWinInitPos(drive).x,MainWin.getDiskWinInitPos(drive).y));
					  getDiskwin().open(MainWin.getDisplay());
					  
				  }
			  });
			
		}
	}
	
	
	
	private void makeNewDGraph()
	{
		synchronized(this.diskgraph)
		{
			
			this.gc.setAdvanced(true);
			
			this.gc.setBackground(DiskWin.colorDiskBG);
			this.gc.fillRectangle(0,0, DiskWin.DGRAPH_WIDTH, DiskWin.DGRAPH_HEIGHT);
		
			this.gc.setTextAntialias(SWT.ON);
			//this.gc.setAntialias(SWT.ON);
			
			this.sectors = new HashMap<Integer,Integer>();

			this.lastread = 0;
			this.lastwrite = 0;
			this.graphchanged = true;
		}
		
	}



	public void setParam(final String key, final Object val)
	{
	
		// actions don't go into params..
		if (key.startsWith("*"))
		{
				
			if (key.equals("*clean"))
			{
				markGraphClean(Integer.parseInt(val.toString()));
			}
			else if (key.equals("*insert"))
			{
				this.loaded = true;
				this.makeNewDGraph();
				
			}	
			else if (key.equals("*eject"))
			{
				this.loaded = false;
				this.makeNewDGraph();
			}
			
		}
		else
		{
			// parameters
			this.params.setProperty(key, val);
			
			
			// graph params
			if (key.equals("_reads") && !val.equals("0") && this.params.containsKey("_reads"))
			{
				markGraphRead(this.getLsn());
			}
			else if (key.equals("_writes") && !val.equals("0") && this.params.containsKey("_writes"))
			{
				markGraphWrite(this.getLsn());
			}
			else if (key.equals("_sectors"))
			{
				this.graphchanged = true;
			}
			
		
		}
		
		// notify our paramwin 
		if (this.hasParamwin())
		{
			MainWin.getDisplay().asyncExec(new Runnable() {
				  public void run()
				  {
					  if (hasParamwin())
						  if (val == null)
								paramwin.submitEvent(key, "");
							else
								paramwin.submitEvent(key, val.toString());
						  
				  }
			  });
			
		}
		
		// notify our diskwin
		if (this.hasDiskwin())
		{
				MainWin.getDisplay().asyncExec(new Runnable() {
					  public void run()
					  {
						 if (hasDiskwin())
						 {
							if (val == null)
								diskwin.submitEvent(key, "");
							else
								diskwin.submitEvent(key, val.toString());
						 }
					  }
				  });
			
		}
		
		
		
	}
	
	private void markGraphClean(int sector)
	{
	//	synchronized(this.diskgraph)
	//	{
			//this.lastclean = sector;
			//this.sectors.put(sector, 100);
			//this.graphchanged = true;
	//	}
	}

	

	



	private void markGraphWrite(int lsn)
	{
	//	synchronized(this.diskgraph)
	//	{
			this.lastwrite = lsn;
			this.sectors.put(lsn,255);
			this.graphchanged = true;
	//	}
	}



	private void markGraphRead(int lsn)
	{
	//	synchronized(this.diskgraph)
	//	{
			this.lastread = lsn;
			this.sectors.put(lsn, -255);
			this.graphchanged = true;
	//	}
	}



	public Object getParam(String key)
	{
		return(this.params.getProperty(key));
	}
	
	
	public String getPath() 
	{
		return this.params.getString("_path","");
	}
	
	
	public int getSectors() 
	{
		return this.params.getInt("_sectors", 0);
	}
	
	
	public int getLsn() 
	{
		return this.params.getInt("_lsn", 0);
	}
	
		
	public int getReads() 
	{
		return this.params.getInt("_reads", 0);
	}
	
	public int getWrites() 
	{
		return this.params.getInt("_writes", 0);
	}
	
	
	public void setLoaded(boolean loaded) 
	{	

		this.loaded = loaded;
		
		if (loaded != this.loaded)
		{
			this.changed = true;
		}
	}
	
	
	public boolean isLoaded() 
	{
		return this.loaded;
	}

	
	
	
	public boolean isChanged() 
	{
		return this.changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isDiskChanged() 
	{
		return this.diskchanged;
	}

	public void setDiskChanged(boolean b) 
	{
		this.diskchanged  = b;
	}




	
	@SuppressWarnings("unchecked")
	public Iterator<String> getParams()
	{
		return this.params.getKeys();
		
	}

	public boolean isSyncTo()
	{
		return(this.params.getBoolean("syncto", false));
	}
	
	public boolean isSyncFrom()
	{
		return(this.params.getBoolean("syncfrom", false));
	}

	public boolean hasParam(String key)
	{
		
		return(this.params.containsKey(key));
	}

	public String getFileName()
	{
		String res = "";
		
		if (this.params.containsKey("_path"))
		{
			res = UIUtils.getFilenameFromURI(this.params.getString("_path"));
			
			if ((res.indexOf('/') > -1) && (res.indexOf('/') < (res.length()-1)))
				res = res.substring(res.lastIndexOf('/')+1);
		}
		else
		{
			res = "(in memory)";
		}
		
		return(res);
	}

	
	public String getFormat()
	{
		return(this.params.getString("_format","unknown"));
	}

	public boolean isWriteProtect()
	{
		return(this.params.getBoolean("writeprotect", false));
	}

	public int getOffset()
	{
		return(this.params.getInt("offset",0));
	}

	public void setDiskgraph(Image diskgraph)
	{
		this.diskgraph = diskgraph;
	}




	public Image getDiskGraph()
	{
	 	//synchronized(this.diskgraph)
		//{
	 		
	 	if (this.graphchanged)
	 	{
			this.graphscale = ((double)DiskWin.DGRAPH_WIDTH / (double)(this.getSectors() - this.getOffset()));
			
			//System.out.println("diskdef get graph: " + this.graphscale + "  cache size: " + this.sectors.size() + " advanced: " + gc.getAdvanced());
	  
			int[] tri = new int[6];
				
			this.gc.setFont(DiskWin.fontDiskGraph);
			this.gc.setBackground(DiskWin.colorDiskBG);
			this.gc.fillRectangle(0,0, DiskWin.DGRAPH_WIDTH, 15);
			
			
			this.gc.setForeground(DiskWin.colorDiskGraphFG );
			this.gc.setBackground(DiskWin.colorDiskBG);
			
			this.gc.drawText("" + this.getOffset(), 1, 0, true);
			this.gc.drawText("" + this.getSectors() , DiskWin.DGRAPH_WIDTH - gc.textExtent("" + this.getSectors()).x, 0, true);
			
			int p1 = DiskWin.DGRAPH_WIDTH / 4;
			int p2 = DiskWin.DGRAPH_WIDTH / 2 + 60;
			
			int readpos = p1;
			int writepos = p2;
			
				
			String tmp = "LAST READ ";
			Point textsize = gc.textExtent(tmp);
			
			this.gc.drawText(tmp, readpos -  textsize.x, 0, true);
			this.gc.drawText(this.lastread + "", readpos+10, 0, true);
			
			
			tri[0] = readpos;
			tri[1] = (textsize.y - 9) / 2;
			tri[2] = tri[0];
			tri[3] = tri[1]+9;
			tri[4] = tri[0]+7;
			tri[5] = tri[1]+5;
			
			this.gc.setBackground(MainWin.colorGreen);
			this.gc.fillPolygon(tri);
			
			
			tmp = "LAST WRITE ";
			this.gc.drawText(tmp, writepos - gc.textExtent(tmp).x , 0, true);
			this.gc.drawText(this.lastwrite + "", writepos+10, 0, true);
			
			tri[0] = writepos;
			tri[2] = tri[0];
			tri[4] = tri[0]+7;
			
			this.gc.setBackground(DiskWin.colorDiskDirty);
			this.gc.fillPolygon(tri);
			
			
			int lastchange = 0;
			@SuppressWarnings("unused")
			int changes = 0;
			
			this.gc.setBackground(DiskWin.colorBlack);
			this.gc.setForeground(DiskWin.colorDiskBG);
			
			
			for (int i = 0;i<DiskWin.DGRAPH_WIDTH;i++)
			{
				Color curcol = getGraphColorFor(i);
				
				if (!curcol.equals(gc.getBackground()))
				{					
					gc.fillGradientRectangle(lastchange, 20, lastchange + i, DiskWin.DGRAPH_HEIGHT-35, true);
					gc.setBackground(curcol);
					lastchange = i;
					changes++;
				}
			}
			
			gc.fillGradientRectangle(lastchange, 20, DiskWin.DGRAPH_WIDTH - lastchange, DiskWin.DGRAPH_HEIGHT-35, true);
			
			
					
			// footer
			this.gc.setBackground(DiskWin.colorDiskBG);
			this.gc.fillRectangle(0,DiskWin.DGRAPH_HEIGHT-15, DiskWin.DGRAPH_WIDTH, DiskWin.DGRAPH_HEIGHT);
			

			
			
			tri[0] = (int) (this.graphscale * this.lastread)+1;
			tri[1] = DiskWin.DGRAPH_HEIGHT-15;
			tri[2] = tri[0]+7;
			tri[3] = DiskWin.DGRAPH_HEIGHT-7;
			tri[4] = tri[0]-7;
			tri[5] = DiskWin.DGRAPH_HEIGHT-7;
			
			this.gc.setBackground(MainWin.colorGreen);
			this.gc.fillPolygon(tri);
			
			tri[0] = (int) (this.graphscale * this.lastwrite)+1;
			tri[2] = tri[0]+7;
			tri[4] = tri[0]-7;
			
			this.gc.setBackground(DiskWin.colorDiskDirty);
			this.gc.fillPolygon(tri);
			
			
			this.graphchanged = false;
	 	}	
			
		
		return(this.diskgraph);
	}

	

	private Color getGraphColorFor(int x)
	{
		
		// figure out effective LSN
		int elsn = (int) Math.round(((double)x) / this.graphscale);
		
	
		//synchronized(sectors)
		//{
			if (this.sectors.containsKey(elsn))
			{
		
				
				int scache = this.sectors.get(elsn);
				
				if (scache < 0)
					return(MainWin.colorGreen);
				
				if (scache > 0)
					return(DiskWin.colorDiskClean);
				
			}
		//}
		
		return DiskWin.colorDiskGraphBG;
	}

	
	@SuppressWarnings("unused")
	private int getGraphAlphaFor(int x)
	{
		
		// figure out effective LSN
		int elsn = (int) Math.round(((double)x) / this.graphscale);
		
	
		if (this.sectors.containsKey(elsn))
		{
			int scache = this.sectors.get(elsn);
			
			if (scache < 0)
			{
				this.sectors.put(elsn, (this.sectors.get(elsn) + 1));
				return(Math.abs(scache));
			}
			
			if (scache > 0)
			{
				this.sectors.put(elsn, (this.sectors.get(elsn) - 1));
				return(scache);
			}
				
		}
		
		return 255;
	}



	public boolean isExpand()
	{
		return(this.params.getBoolean("expand", false));
	}
	

	public int getLimit()
	{
		return(this.params.getInt("sizelimit", -1));
	}



	public void setDiskwin(DiskWin diskwin)
	{
		this.diskwin = diskwin;
	}



	public DiskWin getDiskwin()
	{
		return diskwin;
	}



	public boolean hasDiskwin()
	{
		if (this.diskwin == null)
			return false;
		
		if (this.diskwin.shlDwDrive == null)
			return false;
		
		if (this.diskwin.shlDwDrive.isDisposed())
			return false;
		
		return true;
	}



	public int getDriveNo()
	{
		return this.drive;
	}



	public void setParamwin(DiskAdvancedWin win)
	{
		this.paramwin = win;
	}
	
	public DiskAdvancedWin getParamwin()
	{
		return paramwin;
	}



	public boolean hasParamwin()
	{
		if (this.paramwin == null)
			return false;
		
		if (this.paramwin.shell == null)
			return false;
		
		if (this.paramwin.shell.isDisposed())
			return false;
		
		return true;
	}



	public boolean isGraphchanged()
	{
		return this.graphchanged;
	}
	
	
}

