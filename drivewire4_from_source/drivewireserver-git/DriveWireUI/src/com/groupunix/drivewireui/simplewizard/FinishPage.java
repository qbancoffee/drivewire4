package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class FinishPage extends WizardPage
{

	private Label lblThatsItDrivewire;


	/**
	 * Create the wizard.
	 */
	public FinishPage()
	{
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(FinishPage.class, "/wizard/dwlogo_pad64.png"));
		setTitle("Configuration Complete");
		setDescription("");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Label label = new Label(container, SWT.NONE);
		GridData gd_label = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_label.verticalIndent = 10;
		label.setLayoutData(gd_label);
		label.setImage(SWTResourceManager.getImage(FinishPage.class, "/wizard/cocoman3.png"));
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblThatsItDrivewire = new Label(container, SWT.WRAP);
		lblThatsItDrivewire.setText("DriveWire will be configured to work with your (device name here...) when you click Finish.");
		lblThatsItDrivewire.setFont(SWTResourceManager.getFont(lblThatsItDrivewire.getFont().getFontData()[0].getName(), lblThatsItDrivewire.getFont().getFontData()[0].getHeight(), SWT.BOLD));
	
		lblThatsItDrivewire.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblThatsItDrivewire.setAlignment(SWT.CENTER);
		GridData gd_lblThatsItDrivewire = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblThatsItDrivewire.verticalIndent = 20;
		lblThatsItDrivewire.setLayoutData(gd_lblThatsItDrivewire);
		
		Label lblThereAreMany = new Label(container, SWT.WRAP);
		lblThereAreMany.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblThereAreMany = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblThereAreMany.verticalIndent = 10;
		lblThereAreMany.setLayoutData(gd_lblThereAreMany);
		lblThereAreMany.setText("There are many additional parameters that can be customized to your liking using\r\n the Configuration Editor, found in the Config menu.  You can also run this wizard \r\nagain at any time by choosing Simple Config Wizard from the same menu.\r\n\r\nWe hope you enjoy DriveWire 4 and find it useful.  Please use the Bug Report tool\r\nfound in the Help menu to report any trouble you have.\r\n");
	}
	
	
	@Override
	public void setVisible(boolean v)
	{
		super.setVisible(v);
		
		if (v)
		{
			lblThatsItDrivewire.setText("DriveWire will be configured to work with your " + ((SimpleWizard) getWizard()).getPlatform().name + " when you click Finish.");
			getShell().layout();
		}
	}

}
