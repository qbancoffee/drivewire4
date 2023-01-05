package com.groupunix.drivewireui.updatewizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.groupunix.drivewireui.MainWin;

public class RestartDialog extends Dialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RestartDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		new Label(container, SWT.NONE);
		
		Label label = new Label(container, SWT.NONE);
		label.setImage(SWTResourceManager.getImage(RestartDialog.class, "/status/appointment-recurring.png"));
		new Label(container, SWT.NONE);
		
		Label lblARestartIs = new Label(container, SWT.NONE);
		lblARestartIs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblARestartIs.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblARestartIs.setText("DriveWire must restart for this update to complete.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblYouCanContinue = new Label(container, SWT.NONE);
		lblYouCanContinue.setAlignment(SWT.CENTER);
		GridData gd_lblYouCanContinue = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_lblYouCanContinue.widthHint = 392;
		lblYouCanContinue.setLayoutData(gd_lblYouCanContinue);
		lblYouCanContinue.setText("You can continue using this session of DriveWire as long as you'd like.\r\nThe new version will run the next time you start DriveWire.");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				MainWin.doShutdown();
				
			}
		});
		button.setText("Exit Now");
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				//getShell().dispose();
			}
		});
		button_1.setText("Exit Later");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 257);
	}

}
