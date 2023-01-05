package com.groupunix.drivewireui.instanceman;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.DWUIOperationFailedException;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;

public class InstanceManComposite extends Composite 
{

	private int handlerno;
	private ToolItem tltmStopStart;
	private ToolItem tltmRestartDelete;
	private ToolItem tltmAutostart;
	private ToolItem tltmConnect;
	private Label lblDevImage;
	private Label lblStatus1;
	private Label lblStatus2;
	private Composite compositeStatus;
	private ToolBar toolBar;
	
	public InstanceManComposite(Composite parent, int style, final int handlerno) 
	{
		super(parent, style | SWT.DOUBLE_BUFFERED);
		
		this.handlerno = handlerno;
		
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		setBackgroundMode(SWT.INHERIT_FORCE);
		setLayout(new GridLayout(1, false));
		
		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		tltmStopStart = new ToolItem(toolBar, SWT.NONE);
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmRestartDelete = new ToolItem(toolBar, SWT.NONE);
		tltmRestartDelete.setImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/media-repeat-blue.png"));
		tltmRestartDelete.setText("Restart");
		tltmRestartDelete.setEnabled(true);
		tltmRestartDelete.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				MainWin.sendCommand("dw instance stop " + handlerno);
				
				MainWin.sendCommand("dw instance start " + handlerno);
			
			}
		});
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmAutostart = new ToolItem(toolBar, SWT.NONE);
		tltmAutostart.setText("AutoStart");
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		tltmConnect = new ToolItem(toolBar, SWT.NONE);
		tltmConnect.setImage(SWTResourceManager.getImage(InstanceMan.class, "/menu/database-go.png"));
		tltmConnect.setText("Attach UI ");
		tltmConnect.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				MainWin.setInstance(handlerno);
				
			}});
		
		if (MainWin.getInstance() == handlerno)
			tltmConnect.setEnabled(false);
		
		compositeStatus = new Composite(this, SWT.BORDER);
		compositeStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		compositeStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		compositeStatus.setLayout(new GridLayout(3, false));
		
		lblDevImage = new Label(compositeStatus, SWT.NONE);
		
		GridData gd_lblDevImage = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 2);
		gd_lblDevImage.horizontalIndent = 3;
		lblDevImage.setLayoutData(gd_lblDevImage);
		
		Label label = new Label(compositeStatus, SWT.NONE);
		label.setText(" ");
		
		lblStatus1 = new Label(compositeStatus, SWT.NONE);
		lblStatus1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		new Label(compositeStatus, SWT.NONE);
		
		lblStatus2 = new Label(compositeStatus, SWT.NONE);
		lblStatus2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
	}
	
	
	public void setStatus1(String txt)
	{
		this.lblStatus1.setText(txt);
		this.compositeStatus.layout();
	}

	public void setStatus2(String txt)
	{
		this.lblStatus2.setText(txt);
		this.compositeStatus.layout();
	}
	
	public void setDevImage(Image img)
	{
		this.lblDevImage.setImage(img);
		this.compositeStatus.layout();
	}
	
	public void setCanConnect(boolean can)
	{
		this.tltmConnect.setEnabled(can);
	}
	
	public void setIsAutostart(boolean isa)
	{
		for (Listener l : tltmAutostart.getListeners(SWT.Selection))
		{
			tltmAutostart.removeListener(SWT.Selection, l);
		}
		
		if (isa)
		{
			this.tltmAutostart.setImage(SWTResourceManager.getImage(InstanceMan.class, "/menu/active.png"));
			
			tltmAutostart.addSelectionListener(new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent arg0) 
				{
					HashMap<String,String> settings = new HashMap<String,String>();
					settings.put("AutoStart","false");
					try 
					{
						UIUtils.setInstanceSettings(handlerno, settings);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DWUIOperationFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}
		else
		{
			this.tltmAutostart.setImage(SWTResourceManager.getImage(InstanceMan.class, "/menu/inactive.png"));
			tltmAutostart.addSelectionListener(new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent arg0) 
				{
					HashMap<String,String> settings = new HashMap<String,String>();
					settings.put("AutoStart","true");
					try 
					{
						UIUtils.setInstanceSettings(handlerno, settings);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DWUIOperationFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			});
		}
	}
	
	
	public void setReady(boolean ready)
	{
		for (Listener l : tltmStopStart.getListeners(SWT.Selection))
		{
			tltmStopStart.removeListener(SWT.Selection, l);
		}
		
	
		if (ready)
		{
			tltmStopStart.setImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/media-playback-stop-blue.png"));
			tltmStopStart.setText("Stop");
			tltmStopStart.addSelectionListener(new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent arg0) 
				{
					MainWin.sendCommand("dw instance stop " + handlerno);
				
				}
			});
			
			tltmRestartDelete.setEnabled(true);
			
		}
		else
		{

			tltmStopStart.setImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/media-playback-play-blue.png"));
			tltmStopStart.setText("Start");
			tltmStopStart.addSelectionListener(new SelectionAdapter() 
			{
				@Override
				public void widgetSelected(SelectionEvent arg0) 
				{
					MainWin.sendCommand("dw instance start " + handlerno);
				}
			});
			
			tltmRestartDelete.setEnabled(false);
			
		}
	}
	
}

