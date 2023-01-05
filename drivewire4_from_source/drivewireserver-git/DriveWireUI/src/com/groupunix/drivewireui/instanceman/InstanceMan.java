package com.groupunix.drivewireui.instanceman;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.DWUIOperationFailedException;
import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.UIUtils;

public class InstanceMan extends Shell 
{

	
	private static final String IMG_INSTANCE_READY = "/status/completed_16.png";



	private static final String IMG_INSTANCE_NOTREADY = "/menu/application-exit-5.png";
	
	private static final long UPDATE_DELAY = 500;

	private static final int DEVTYPE_UNKNOWN = 0;
	private static final int DEVTYPE_SERIAL = 1;
	private static final int DEVTYPE_TCP_SERVER = 2;
	private static final int DEVTYPE_TCP_CLIENT = 3;
	
	
	private ExpandBar expandBar;
	private HashMap<Integer,ExpandItem> expandItems = new HashMap<Integer,ExpandItem>();
	private HashMap<Integer,List<String>> instanceStatusCache = new HashMap<Integer,List<String>>();
	
	private Thread updateT;
	private boolean sizeset = false;



	private int lastUpdateInstance = -1;
	
	
	public InstanceMan(final Display display) 
	{
		super(display, SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.DOUBLE_BUFFERED);
		setImage(SWTResourceManager.getImage(InstanceMan.class, "/menu/database-gear.png"));
		
		addShellListener(new ShellAdapter() {
			

			@Override
			public void shellClosed(ShellEvent e) 
			{
				if (updateT != null)
					updateT.interrupt();
				
				/*
					try {
						updateT.join();
					} catch (InterruptedException e1) {
					
					}
					*/
			}
		});
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		expandBar = new ExpandBar(this, SWT.NONE);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
	
		
		
		setText("Instance Manager");
		
		setSize(412, 221);
		
		int x = display.getActiveShell().getBounds().x + (display.getActiveShell().getBounds().width / 2) - (getBounds().width / 2);
		int y = display.getActiveShell().getBounds().y + (display.getActiveShell().getBounds().height / 2) - (getBounds().height / 2);
		
		setLocation(x, y);
	
		updateT = new Thread(new Runnable(){

			

			@Override
			public void run() 
			{
				boolean wanttodie = false;
				
				while (!wanttodie)
				{
					if (display.isDisposed())
						wanttodie = true;
					else
					{
						display.syncExec(new Runnable(){
	
							@Override
							public void run() {
								updateInstanceDisplay();
							}});
						
						
						try {
							Thread.sleep(UPDATE_DELAY);
						} 
						catch (InterruptedException e) 
						{
							wanttodie = true;
						}
					}
				}
				
			}});
		
		updateT.setDaemon(true);
		updateT.start();
		
		
	}

	
	

	private void updateInstanceDisplay() 
	{
		try 
		{
			
			List<String> instances = UIUtils.loadList("ui server show instances");
				
			if (!sizeset )
			{
				this.setSize(380, 38 + instances.size() * 116);
				sizeset = true;
			}
			
			for (String inst : instances)
			{
				String[] parts = inst.split("\\|");
				
				if (parts.length == 2)
				{
					int devtype = DEVTYPE_UNKNOWN;
					boolean connected = false;
					boolean ready = false;
					String devname = "Unknown";
					String devclient = "Unknown";
					ExpandItem xpndtmInstance;
					InstanceManComposite composite;
					boolean started = false;
					boolean dying = false;
					
					int devrate = -1;
					final int handlerno = Integer.parseInt(parts[0]);
					
					List<String> instvars = UIUtils.loadList("ui instance status " + parts[0]);
					
					if (instanceStatusHasChanged(instvars, handlerno) || (lastUpdateInstance  != MainWin.getInstance()))
					{
						
						if (this.expandItems.containsKey(handlerno))
						{
							xpndtmInstance = this.expandItems.get(handlerno);
							composite = (InstanceManComposite) xpndtmInstance.getControl();
						}
						else
						{
							xpndtmInstance = new ExpandItem(expandBar, SWT.NONE);
							composite = new InstanceManComposite(expandBar, SWT.NONE, handlerno);
							xpndtmInstance.setControl(composite);
							xpndtmInstance.setHeight(87);
							xpndtmInstance.setExpanded(true);
							this.expandItems.put(handlerno, xpndtmInstance);
						}
						
				
						
						if (handlerno == MainWin.getInstance())
							composite.setCanConnect(false);
						else
							composite.setCanConnect(true);
						 
											
						for (int i = 0;i<instvars.size();i++)
						{
							Pattern p_item = Pattern.compile("^(.+)\\|(.+)");
							Matcher m = p_item.matcher(instvars.get(i));
							
							if (m.find())
							{
								
								if (m.group(1).equals("name"))
								{
									xpndtmInstance.setText("Instance " + parts[0] + ": " + m.group(2));
								}
								else if (m.group(1).equals("connected"))
								{
									if (m.group(2).equals("true"))
										connected = true;
								}
								else if (m.group(1).equals("started"))
								{
									if (m.group(2).equals("true"))
									{
										started = true;
									}
								}
								else if (m.group(1).equals("dying"))
								{
									if (m.group(2).equals("true"))
									{
										dying = true;
									}
								}
								
								else if (m.group(1).equals("ready"))
								{
									if (m.group(2).equals("true"))
									{
										xpndtmInstance.setImage(SWTResourceManager.getImage(InstanceMan.class, IMG_INSTANCE_READY));
										composite.setReady(true);
										ready = true;
									}
									else
									{
										xpndtmInstance.setImage(SWTResourceManager.getImage(InstanceMan.class, IMG_INSTANCE_NOTREADY));
										composite.setReady(false);
									}
								}
								else if (m.group(1).equals("devicetype"))
								{
									if (m.group(2).equals("tcp"))
									{
										devtype = DEVTYPE_TCP_SERVER;
									}
									else if (m.group(2).equals("tcp-client"))
									{
										devtype = DEVTYPE_TCP_CLIENT;
									}
									else if (m.group(2).equals("serial"))
									{
										devtype = DEVTYPE_SERIAL;
									}
									
								}
								else if (m.group(1).equals("devicename"))
								{
									devname = m.group(2);
								}
								else if (m.group(1).equals("devicerate"))
								{
									try 
									{
										devrate = Integer.parseInt(m.group(2));
									}
									catch (NumberFormatException ne)
									{
									}
									
								}
								else if (m.group(1).equals("deviceclient"))
								{
									devclient = m.group(2);
								}
								else if (m.group(1).equals("autostart"))
								{
									if (m.group(2).equals("true"))
										composite.setIsAutostart(true);
									else
										composite.setIsAutostart(false);
							
								}
								
								
							}
							
						}
						
						String stat2 = "";
						
						switch (devtype)
						{
								
							case DEVTYPE_UNKNOWN:
								if (ready)
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/unknown.png"));
									composite.setStatus1("Unknown device type, name " + devname);
									if (connected)
										stat2 += "Connected";
									else
										stat2 += "Not connected";
								}
								else
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/notready.png"));
									composite.setStatus1("Instance is not ready");
								}
								break;
										
									
							case DEVTYPE_TCP_SERVER:
								String[] tcpsp = devname.split(":");
								String stat = "TCP Server listening ";
								if (tcpsp.length == 2)
									stat += "on port " + tcpsp[1];
										
								composite.setStatus1(stat);
										
								if (connected)
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/network-connect-3.png"));
									stat2 += "Client at " + devclient + " connected";
								}
								else
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/network-disconnect-3.png"));
									stat2 += "Waiting for client...";
								}
								break;
										
									
							case DEVTYPE_TCP_CLIENT:
										
								composite.setStatus1("TCP Client");
										
								if (connected)
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/network-connect-3.png"));
									stat2 += "Connected to " + devclient;
								}
								else
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/network-disconnect-3.png"));
									stat2 += "Not connected";
								}
								break;	
								
							case DEVTYPE_SERIAL:
								composite.setStatus1("Serial device '" + devname + "' at " + devrate + "bps");
							
								if (connected)
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/serial.png"));
									stat2 += "Connected";
								}
								else
								{
									composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/serial-disconnected.png"));
									stat2 += "Disconnected";
								}
								break;
							
						}
						
						if (dying)
							composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/dying.png"));
						else if (!ready && started)
							composite.setDevImage(SWTResourceManager.getImage(InstanceMan.class, "/instman/starting.png"));
						
						if (dying)
							stat2 = "Dying..";
						else if (ready)
							stat2 = "Ready, " + stat2;
						else if (started)
							stat2 = "Starting.. ";
						else 
							stat2 = "Not started";
						
						composite.setStatus2(stat2);
						
					
					}
				}
			}
			
			lastUpdateInstance = MainWin.getInstance();
		} 
		// silent death for all
		catch (IOException e) 
		{
			
		} 
		catch (DWUIOperationFailedException e) 
		{
			
		}
		catch (NumberFormatException e)
		{
			
		}
		
		
		
	}




	private boolean instanceStatusHasChanged(List<String> instvars,	int handlerno) 
	{
		if (this.instanceStatusCache.containsKey(handlerno))
		{
			List<String> oldvars = this.instanceStatusCache.get(handlerno);
			
			if (instvars.size() != oldvars.size())
			{
				this.instanceStatusCache.put(handlerno, instvars);
				return true;
			}
			
			for (int i = 0;i < oldvars.size();i++)
			{
				if (!instvars.get(i).equals(oldvars.get(i)))
				{
					this.instanceStatusCache.put(handlerno, instvars);
					return true;
				}
			}
			
		}
		else
		{
			this.instanceStatusCache.put(handlerno, instvars);
			return true;
		}
		

		return false;
	}




	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	
	
	
	
	
}
