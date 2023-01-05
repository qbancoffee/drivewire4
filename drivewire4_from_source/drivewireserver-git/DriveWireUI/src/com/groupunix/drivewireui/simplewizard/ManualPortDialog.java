package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ManualPortDialog extends Dialog
{
	private Text textDevice;

	private String devname;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ManualPortDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);
		
		container.setLayout(new GridLayout(3, false));
		
		Label lblDrivewireCannotAutomatically = new Label(container, SWT.NONE);
		GridData gd_lblDrivewireCannotAutomatically = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblDrivewireCannotAutomatically.verticalIndent = 20;
		gd_lblDrivewireCannotAutomatically.horizontalIndent = 10;
		lblDrivewireCannotAutomatically.setLayoutData(gd_lblDrivewireCannotAutomatically);
		lblDrivewireCannotAutomatically.setText("Unfortunately, DriveWire cannot automatically detect every type of serial port\r\non every type of computer it supports.  ");
		
		Label label = new Label(container, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 20;
		gd_label.minimumWidth = 20;
		gd_label.horizontalIndent = 10;
		gd_label.verticalIndent = 10;
		label.setLayoutData(gd_label);
		
		Label lblIfTheDevice = new Label(container, SWT.NONE);
		GridData gd_lblIfTheDevice = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblIfTheDevice.verticalIndent = 10;
		gd_lblIfTheDevice.horizontalIndent = 10;
		lblIfTheDevice.setLayoutData(gd_lblIfTheDevice);
		lblIfTheDevice.setText("If the device you wish to use is not listed in the wizard, there may be a problem\r\nwith your hardware, but things may be perfectly normal.\r\n\r\nYou may tell DriveWire the device name you'd like to use below.  DriveWire will\r\nattempt to access the serial port and report the results in the wizard.\r\n");
		new Label(container, SWT.NONE);
		
		Label lblDevice = new Label(container, SWT.NONE);
		lblDevice.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		GridData gd_lblDevice = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDevice.horizontalIndent = 10;
		gd_lblDevice.verticalIndent = 20;
		lblDevice.setLayoutData(gd_lblDevice);
		lblDevice.setText("Device Name:");
		
		textDevice = new Text(container, SWT.BORDER);
		textDevice.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				devname = textDevice.getText();
			}
		});
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.verticalIndent = 20;
		textDevice.setLayoutData(gd_text);
		new Label(container, SWT.NONE);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(459, 293);
	}

	public void setDevname(String devname)
	{
		this.devname = devname;
	}

	public String getDevname()
	{
		return devname;
	}



}
