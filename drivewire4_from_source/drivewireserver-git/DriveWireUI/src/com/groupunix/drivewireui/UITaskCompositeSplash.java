package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class UITaskCompositeSplash extends UITaskComposite
{

	
	private Image logoi;
	private Canvas logoc;
	private ImageData logodat;
	
	public UITaskCompositeSplash(Composite master, int style, int tid)
	{
		super(master, style, tid);
		
		this.setData("splash","indeed");
		
		createContents(master);
	}
	
	
	private void createContents(Composite master)
	{
		
		
		
		
		status.dispose();
		
		this.setBackground(MainWin.colorWhite);
		
		
		logodat = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/anim0.png").getImageData();
		
		logodat.alpha = 0;
		
	    logoi = new Image(this.getDisplay(),logodat);
		logoc = new Canvas(this,SWT.DOUBLE_BUFFERED);
		logoc.setBackground(MainWin.colorWhite);
		
		this.setBounds(0, 0, master.getClientArea().width, logodat.height + 40);
		logoc.setBounds(this.getClientArea().width/2 - logodat.width/2, this.getClientArea().height/2 - logodat.height/2, logodat.width, logodat.height);
		
			
		logoc.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) 
			{
				e.gc.drawImage(logoi,0,0);
            }
		});
	    
		
		
		details.setBounds(logoc.getBounds().x, logodat.height+12, logoc.getBounds().width - 10, logodat.height + 28);
		//details.setWordWrap(false);
		details.setAlignment(SWT.RIGHT);
		details.setVisible(false);
		
		details.setFont(UITaskMaster.versionFont);
		details.setForeground(MainWin.colorCmdTxt);
		
	}
	
	
	public void showVer()
	{
		this.details.setVisible(true);
	}
	

	public int getHeight()
	{
		return(this.getBounds().height);
	}
	
	public void setStatus(int stat)
	{
	}
	
	public void setDetails(String text)
	{
		this.det = text;
		this.details.setText(det.trim());
	}
	

	public void setTop(int y)
	{
		this.setBounds(0, y, this.getBounds().width, this.getBounds().height);
	}
	
	public void setBottom(int y)
	{
		this.setBounds(0, this.getBounds().y, this.getBounds().width, y - this.getBounds().y);
		
	}
	
	
	boolean doAnim()
	{
		if ( (!this.getDisplay().isDisposed()) && (logoc != null ) && (logodat != null) && (logodat.alpha < 255))
		{	
			logodat.alpha = Math.min(255, logodat.alpha + 5);
			logoi = new Image(this.getDisplay(),logodat);
			logoc.redraw();
			
			return true;
		}
		
		return false;
	}


	public void doAnim2(int i)
	{
		if ( (!this.getDisplay().isDisposed()) && (logoc != null ) && (logodat != null) && (logodat.alpha < 255))
		{
			this.logodat = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/anim" + i + ".png").getImageData();
			logoi = new Image(this.getDisplay(),logodat);
			logoc.redraw();		
			this.update();
			
			try
			{
				Thread.sleep(15);
			} catch (InterruptedException e)
			{
			
			}
		}
	}

	
}
