package com.groupunix.drivewireui.simplewizard;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireserver.DriveWireServer;

public class SerialPage extends WizardPage
{

	private static final int PORTSTATUS_AVAILABLE = 1;
	@SuppressWarnings("unused")
	private static final int PORTSTATUS_UNKNOWN = 0;
	private static final int PORTSTATUS_INUSE = 2;
	private static final int PORTSTATUS_ERROR = 3;
	
	private Boolean updatingPorts = false;
	private Table portlist;
	private ArrayList<String> manualPorts = new ArrayList<String>();
	private Label lblChooseAnAvailable;
	private String chooseone = "Choose an available serial port, \r\nor connect an adapter and \r\nscan serial ports again.";
	private TableColumn tblclmnIcon;
	private TableColumn tblclmnDev;
	private TableColumn tblclmnStatus;

	/**
	 * Create the wizard.
	 */
	public SerialPage()
	{
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(SerialPage.class, "/wizard/dwlogo_pad64.png"));
		
		setTitle("Select Serial Port");
		setDescription("Please choose the serial port that DriveWire will use.");
	}
	
	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		setControl(container);
		GridLayout gl_container = new GridLayout(2, false);
		container.setLayout(gl_container);
		
		portlist = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		portlist.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) 
			{
				
				tblclmnDev.setWidth((portlist.getClientArea().width/2) - 18);
				tblclmnStatus.setWidth((portlist.getClientArea().width/2) - 18);
				
			}
		});
		portlist.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (e.item.getData("status") != null)
				{
					if ((Integer) e.item.getData("status") == PORTSTATUS_AVAILABLE)
					{
						selectPort( ((TableItem)e.item).getText(1));
						setPageComplete(true);
					}
				}
				
			}
		});
		portlist.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		portlist.setLinesVisible(true);
		
		portlist.addListener(SWT.MeasureItem, new Listener() {
	        public void handleEvent(Event event) {
	            event.height = 40;
	        }
	    });
		
		tblclmnIcon = new TableColumn(portlist, SWT.NONE);
		tblclmnIcon.setWidth(36);
		tblclmnIcon.setText("Icon");
		tblclmnIcon.setAlignment(SWT.CENTER);
		
		
		tblclmnDev = new TableColumn(portlist, SWT.NONE);
		//tblclmnDev.setWidth(170);
		
		tblclmnDev.setText("Dev");
		portlist.setSortColumn(tblclmnDev);
		
		
		tblclmnStatus = new TableColumn(portlist, SWT.NONE);
		//tblclmnStatus.setWidth(162);
		tblclmnStatus.setText("Status");
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		label.setImage(SWTResourceManager.getImage(SerialPage.class, "/wizard/cocoman_looks.png"));
		
		lblChooseAnAvailable = new Label(container, SWT.WRAP);
		lblChooseAnAvailable.setFont(SWTResourceManager.getFont(lblChooseAnAvailable.getFont().getFontData()[0].getName(), lblChooseAnAvailable.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		lblChooseAnAvailable.setAlignment(SWT.CENTER);
		GridData gd_lblChooseAnAvailable = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd_lblChooseAnAvailable.widthHint = 175;
		gd_lblChooseAnAvailable.minimumWidth = 175;
		gd_lblChooseAnAvailable.verticalIndent = 10;
		lblChooseAnAvailable.setLayoutData(gd_lblChooseAnAvailable);
		lblChooseAnAvailable.setText("Choose an available serial port, or connect an adapter and scan serial ports again.");
		
		Button btnRescanForPorts = new Button(container, SWT.NONE);
		btnRescanForPorts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runSerialMonitor();
			}
		});
		btnRescanForPorts.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnRescanForPorts.setImage(SWTResourceManager.getImage(SerialPage.class, "/menu/system-search-4.png"));
		btnRescanForPorts.setText(" Scan serial ports ");
		
		Button btnMyPortIsnt = new Button(container, SWT.NONE);
		btnMyPortIsnt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ManualPortDialog m = new ManualPortDialog(getShell());
				m.open();
				
				if (m.getReturnCode() == 0)
				{
					String tdev = m.getDevname();
					
					if (!manualPorts.contains(tdev)  && (!manualPorts.contains(tdev.toUpperCase())) )
						manualPorts.add(tdev);
					
					runSerialMonitor();
				}
			}
		});
		btnMyPortIsnt.setImage(SWTResourceManager.getImage(SerialPage.class, "/menu/edit-add-2.png"));
		btnMyPortIsnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnMyPortIsnt.setText(" Add port manually ");
		
		runSerialMonitor();
	}

	
	protected void selectPort(String pname)
	{
		((SimpleWizard)this.getWizard()).setSerialPort(pname);
		lblChooseAnAvailable.setText("You've chosen:\r\n" + pname +  "\r\n\r\n" + ((SimpleWizard) this.getWizard()).getRandomYay());
	}

	private void updatePortEntry(String p, String s)
	{
		boolean updated = false;
		
		for (TableItem ti : this.portlist.getItems())
		{
			if (ti.getText(1).equals(p))
			{
				ti.setText(2, s);
				ti.setImage(getPortImage(getPortStatusValue(s)));
				ti.setData("status", getPortStatusValue(s) );
				
				updated = true;
				break;
			}
		}
		
		if (!updated)
		{
			TableItem ti = new TableItem(this.portlist, SWT.NONE);
			ti.setText(1, p);
			ti.setText(2, s);
			ti.setImage(getPortImage(getPortStatusValue(s)));
			ti.setData("status", getPortStatusValue(s) );
		}
		
	}
	
	
	
	
	private int getPortStatusValue(String stat)
	{
		if (stat.equals("Available") || stat.equals("In use by DriveWire"))
		{
			return PORTSTATUS_AVAILABLE;
		}
		
		if (stat.startsWith("PortInUseException"))
		{
			return PORTSTATUS_INUSE;
		}
		
		return PORTSTATUS_ERROR;
	}

	private Image getPortImage(int stat)
	{
		String imgpath = "/status/unknown.png";
		
		if (stat == PORTSTATUS_AVAILABLE)
			imgpath = "/status/completed.png";
		
		if (stat == PORTSTATUS_INUSE)
			imgpath = "/status/active-timeout.png";
		
		if (stat == PORTSTATUS_ERROR)
			imgpath = "/status/failed.png";
		
		return SWTResourceManager.getImage(SerialPage.class, imgpath);
	}

	private void runSerialMonitor()
	{
		
		 if ((!this.getShell().isDisposed()) && (portlist != null) && (manualPorts != null) && !updatingPorts)
		 {
			 // check available serial ports..
			 synchronized(updatingPorts )
			 {
				 updatingPorts = true;
			 }
			
			 
			 
			 // keep the port queries out of the gui thread
			 Runnable updater = new Runnable(){
						 
				@Override
				public void run()
				{
					try
					{
					
						 final ArrayList<String> ports = DriveWireServer.getAvailableSerialPorts();
							
						 
						 // notice absent friends
						 
						 if (!getShell().isDisposed())
						 {
						 		getShell().getDisplay().syncExec(
								  new Runnable() 
								  {
									  public void run()
									  {
										  lblChooseAnAvailable.setText("Scanning serial ports...");
										  
										  for (TableItem i : portlist.getItems())
											 {
												 if (!ports.contains(i.getText(1)) && !manualPorts.contains(i.getText(1)))
												 {
													 // port's gone missing
													 
													 i.setText(2, "Failed, uplugged adapter?");
													 i.setData("status", PORTSTATUS_ERROR);
													 i.setImage(getPortImage(PORTSTATUS_ERROR));
												 }
											 }
									  }
								  });
						 } 
						 
						 
						  
						 // auto > manual
						 for (String p: ports)
						 {
							 if (manualPorts.contains(p))
								 manualPorts.remove(p);
						 }
						 
						 
						 if (!getShell().isDisposed())
						 {
							 for (String p : ports)
							 {
								 final String port = p;
								 final String stat = DriveWireServer.getSerialPortStatus(port);
										 
								 if (!getShell().isDisposed())
								 {
								 		getShell().getDisplay().syncExec(
										  new Runnable() 
										  {
											  public void run()
											  {
												  updatePortEntry(port, stat);
											  }
										  });
								 } 
							 }
						 }
								 
							
						 
						 if (!getShell().isDisposed())
						 {
							 for (String p : manualPorts)
							 {
								 final String port = p;
								 final String stat = DriveWireServer.getSerialPortStatus(port);
						
								 if (!getShell().isDisposed())
								 {
								    getShell().getDisplay().syncExec(new Runnable()
								    {
										  public void run()
										  {
											  updatePortEntry(port, stat);
										
										  }
										  
								    });
								 }
							 }
						 }
						
					}
					catch (Exception e)
					{
						System.out.println("Wizard serial port scan thread failed: " + e.getMessage());
						System.out.println("Carry on my wayward son..");
					}
						
					 synchronized(updatingPorts)
					 {
						 updatingPorts = false;
					 }
							 
					 
					 
					 if (!getShell().isDisposed())
					 {
					    getShell().getDisplay().syncExec(new Runnable()
					    {
							  public void run()
							  {
								  lblChooseAnAvailable.setText(chooseone);
								  
								  if (portlist.getItemCount() < 1)
								  {
									 setErrorMessage("No serial ports were found.  You may need to manually specify your device."); 
							
								  }
								  else
								  {
									 setErrorMessage(null);
								  }
								  
							  }
									
					    });
					 }
						
				}
						 	 
			 };
			 
			 Thread t = new Thread(updater);
			 t.start(); 
			 
		 }
		
	}
	
	
	
	@Override
	public boolean isPageComplete()
	{
		if ( ((SimpleWizard) this.getWizard()).getSerialPort() == null)
			return false;
		
		return true;
	}
	
	
}
