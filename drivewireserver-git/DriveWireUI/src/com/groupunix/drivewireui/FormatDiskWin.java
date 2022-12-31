package com.groupunix.drivewireui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.CCombo;

public class FormatDiskWin extends Dialog {

	protected Object result;
	protected static Shell shlCreateANew;
	private Spinner spinnerDrive;
	private CCombo comboFormat;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FormatDiskWin(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		

		
		shlCreateANew.open();
		shlCreateANew.layout();
		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlCreateANew.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlCreateANew.getBounds().height / 2);
		
		shlCreateANew.setLocation(x, y);
		
		if (MainWin.getCurrentDiskNo() > -1)
		{
			this.spinnerDrive.setSelection(MainWin.getCurrentDiskNo());
		}
		
		while (!shlCreateANew.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	
	
	
	private void createContents() {
		shlCreateANew = new Shell(getParent(), getStyle());
		shlCreateANew.setSize(336, 225);
		shlCreateANew.setText("Format Disk Image...");
		GridLayout gl_shlCreateANew = new GridLayout(5, false);
		gl_shlCreateANew.marginTop = 5;
		gl_shlCreateANew.marginRight = 5;
		gl_shlCreateANew.marginLeft = 5;
		gl_shlCreateANew.marginBottom = 5;
		shlCreateANew.setLayout(gl_shlCreateANew);
		
		Label lblInsertNewDisk = new Label(shlCreateANew, SWT.NONE);
		lblInsertNewDisk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 2, 1));
		lblInsertNewDisk.setAlignment(SWT.RIGHT);
		lblInsertNewDisk.setText("Format disk in drive:");
		
		spinnerDrive = new Spinner(shlCreateANew, SWT.BORDER);
		spinnerDrive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		spinnerDrive.setMaximum(255);
		
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblNewLabel = new Label(shlCreateANew, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		lblNewLabel.setImage(SWTResourceManager.getImage(FormatDiskWin.class, "/wizard/format-disk-32.png"));
		
		Label lblFormatForNew = new Label(shlCreateANew, SWT.NONE);
		lblFormatForNew.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 2, 1));
		lblFormatForNew.setText("Format for disk image:");
		
		comboFormat = new CCombo(shlCreateANew, SWT.BORDER);
		comboFormat.setEnabled(false);
		comboFormat.setEditable(false);
		comboFormat.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboFormat.setItems(new String[] {"DECB"});
		comboFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		comboFormat.select(0);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblImageSizeIn = new Label(shlCreateANew, SWT.NONE);
		lblImageSizeIn.setText("Image size:");
		lblImageSizeIn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		
		Spinner spinner = new Spinner(shlCreateANew, SWT.BORDER);
		spinner.setEnabled(false);
		spinner.setMaximum(100000);
		spinner.setSelection(630);
		spinner.setTextLimit(100000);
		spinner.setPageIncrement(630);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(shlCreateANew, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		Button btnCreate = new Button(shlCreateANew, SWT.NONE);
		btnCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				sendFormatCommand(e);
				e.display.getActiveShell().close();
				
			}
		});
		btnCreate.setText(" Format ");
		
		Button btnCancel = new Button(shlCreateANew, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			}
		});
		btnCancel.setText("Cancel");

	}

	protected void sendFormatCommand(final SelectionEvent e) 
	{
		final List<String> cmds = new ArrayList<String>();
	
		cmds.add("dw disk dos format " + spinnerDrive.getSelection());
		
		
		getParent().getDisplay().asyncExec(
				  new Runnable() 
				  {
					  public void run()
					  {
						  SendCommandWin win = new SendCommandWin(e.display.getActiveShell(), SWT.DIALOG_TRIM, cmds, "Formatting disk image...", "Please wait while the image is formatted.");
						  win.open();
					  }
				  });
		
	}

	protected Spinner getSpinner() {
		return spinnerDrive;
	}
	
}
