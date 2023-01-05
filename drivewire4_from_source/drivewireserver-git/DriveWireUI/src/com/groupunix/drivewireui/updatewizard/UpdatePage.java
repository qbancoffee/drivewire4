package com.groupunix.drivewireui.updatewizard;

import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.Updater;
import com.groupunix.drivewireui.Version;

public class UpdatePage extends WizardPage {

	private Updater updater;
	private Table table;

	private Composite container;
	private HashMap<Long,TableItem> threadStatus = new HashMap<Long,TableItem>();
	protected int updateErrors = 0;
	private TableItem tiTop;
	private Version version;

	/**
	 * Create the wizard.
	 * @param updater 
	 */
	public UpdatePage(Updater updater) {
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(IntroPage.class, "/wizard/dwlogo_pad64.png"));
		setTitle("DriveWire version " + updater.getLatestVersion().toString() + " is available");
		setDescription("An updated version of DriveWire is available.");
		this.updater = updater;
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		
		container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(container, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(40);
		
		TableColumn tblclmnAction = new TableColumn(table, SWT.NONE);
		tblclmnAction.setWidth(485);
		tblclmnAction.setText("Action");

		
	}
	
	
	
	@Override
	public void setVisible(boolean vis)
	{
		
		if (vis)
		{
			table.removeAll();
			
			this.container.setVisible(true);
			
			version = this.updater.getLatestVersion();
			
			if (this.updater.isUserIgnore())
			{
				this.setPageComplete(true);
				TableItem ti = new TableItem(table, SWT.NONE);
				ti.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/completed.png"));
				ti.setText(1, "Will add version " + version.toString() + " to ignore list.");
				
			}
			else
			{
				//this.setPageComplete(false);
				tiTop = new TableItem(table, SWT.NONE);
				tiTop.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/active.png"));
				tiTop.setText(1, "Updating to version " + version.toString());
				
				Thread updateT = new Thread(new FileUpdateManager(this, version.getFiles()));
				
				updateT.start();

			}
		}
	}

	
	
	public void addErrorStatus(final String txt) 
	{
		this.getShell().getDisplay().asyncExec(new Runnable() 
				{

					@Override
					public void run() 
					{
						TableItem ti = new TableItem(table, SWT.NONE);
						ti.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/dialog-warning-3.png"));
						ti.setText(1, txt);
						updateErrors ++;
					}
					
				});
		
	}
	
	
	
	
	public void setThreadStatus(final String txt) 
	{
		final long tid = Thread.currentThread().getId();
		
		this.getShell().getDisplay().asyncExec(new Runnable() 
				{

					@Override
					public void run() 
					{
						if (!threadStatus.containsKey(tid))
						{
							TableItem ti = new TableItem(table, SWT.NONE);
							ti.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/network-idle.png"));
							ti.setData("ProgressImage", true);
							threadStatus.put(tid, ti);
						}
						
						
						threadStatus.get(tid).setText(1, txt);
					}
					
				});
		
	}

	
	public void removeThreadStatus() 
	{
		final long tid = Thread.currentThread().getId();
		
		this.getShell().getDisplay().asyncExec(new Runnable() 
			{
			@Override
			public void run() 
			{
		
				if (threadStatus.containsKey(tid))
				{
					
					threadStatus.get(tid).dispose();
					threadStatus.remove(tid);
				}
			}
		
			});
	
	}

	public void finishedUpdate() 
	{
	 
		getShell().getDisplay().asyncExec(new Runnable() 
		{
			@Override
			public void run() 
			{
				if (updateErrors > 0)
				{
					tiTop.setImage(0, ResourceManager.getImage(UpdatePage.class, "/status/dialog-error-5.png"));
					tiTop.setText(1, "The update was not successful.  See errors below for details.");
				}
				else
				{
					tiTop.setImage(0, ResourceManager.getImage(UpdatePage.class, "/status/completed.png"));
					tiTop.setText(1, "The update completed successfully.");
				}
				
				
				setPageComplete(true);
				updater.setUpdateComplete(true);
				getContainer().updateButtons();
			}
	
		});
	
	}

	public void updateProgressIcons() 
	{
		
		// so, so fake.  but kinda pretty.
		
		if ((this.getShell() != null) && (!this.getShell().isDisposed()) && (this.getContainer() != null) && (this.getContainer().getShell() != null) && (!this.getContainer().getShell().isDisposed()))
		
			this.getShell().getDisplay().asyncExec(new Runnable() 
			{
				@Override
				public void run() 
				{
					for (TableItem ti : table.getItems())
					{
						if ((ti.getData("ProgressImage") != null) && ( (Boolean)ti.getData("ProgressImage") == true ))
						{
							if (Math.random() > 0.5)
								ti.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/network-receive.png"));
							else
								ti.setImage(0, ResourceManager.getImage(IntroPage.class, "/status/network-transmit.png"));
						}
					}
				}
		
			});
	}

	public Version getVersion() 
	{
		return(this.version);
	}
	
	
	
}
