package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class IntroPage extends WizardPage
{

	/**
	 * Create the wizard.
	 */
	public IntroPage()
	{
		super("wizardPage");
		setTitle("DriveWire 4 Configuration");
		setDescription("Simple configuration wizard");
		setImageDescriptor(ResourceManager.getImageDescriptor(IntroPage.class, "/wizard/dwlogo_pad64.png"));
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
		container.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont(lblNewLabel_1.getFont().getFontData()[0].getName(), lblNewLabel_1.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_1.horizontalIndent = 10;
		gd_lblNewLabel_1.verticalIndent = 20;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setText("Welcome to DriveWire 4!");
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 2));
		label.setImage(SWTResourceManager.getImage(IntroPage.class, "/wizard/cocoman_welcome.png"));
		
		Label lblNewLabel = new Label(container, SWT.WRAP);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_lblNewLabel.horizontalIndent = 10;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setText("This wizard will help you create a working\r\nDriveWire configuration.   \r\n\r\nYou will select which type of client device\r\nyou have and how we will communicate \r\nwith it.  You'll also have the opportunity to\r\ncustomize a few common options.\r\n\r\nYou may cancel at any time if you would \r\nprefer to use the full configuration editor.  \r\nYou can also run this wizard again later by \r\nchoosing \"Simple Config Wizard\" from\r\nthe Config menu.\r\n");
		
		
	}

	
	
	
}
