package com.groupunix.drivewireui;

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

public class CreateDiskWin extends Dialog {

	protected Object result;
	protected static Shell shlCreateANew;
	private Text textPath;
	private Spinner spinnerDrive;
	private Button btnFile;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateDiskWin(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		applyFont();
		
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

	
	private static void applyFont() 
	{
		/*
		FontData f = new FontData(MainWin.config.getString("DialogFont",MainWin.default_DialogFont), MainWin.config.getInt("DialogFontSize", MainWin.default_DialogFontSize), MainWin.config.getInt("DialogFontStyle", MainWin.default_DialogFontStyle) );
		
		
		Control[] controls = shlCreateANew.getChildren();
		
		for (int i = 0;i<controls.length;i++)
		{
			controls[i].setFont(new Font(shlCreateANew.getDisplay(), f));
		}
		
		for (int i = 0;i<controls.length;i++)
		{
			controls[i].setFont(new Font(shlCreateANew.getDisplay(), f));
		}
	*/	
	}
	
	private void createContents() {
		shlCreateANew = new Shell(getParent(), getStyle());
		shlCreateANew.setSize(375, 210);
		shlCreateANew.setText("Create a new disk image...");
		GridLayout gl_shlCreateANew = new GridLayout(4, false);
		gl_shlCreateANew.marginTop = 5;
		gl_shlCreateANew.marginRight = 5;
		gl_shlCreateANew.marginLeft = 5;
		gl_shlCreateANew.marginBottom = 5;
		shlCreateANew.setLayout(gl_shlCreateANew);
		
		Label lblInsertNewDisk = new Label(shlCreateANew, SWT.NONE);
		lblInsertNewDisk.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblInsertNewDisk.setAlignment(SWT.RIGHT);
		lblInsertNewDisk.setText("Create new disk for drive:");
		
		spinnerDrive = new Spinner(shlCreateANew, SWT.BORDER);
		spinnerDrive.setMaximum(255);
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblNewLabel = new Label(shlCreateANew, SWT.NONE);
		lblNewLabel.setImage(SWTResourceManager.getImage(CreateDiskWin.class, "/wizard/new-disk-32.png"));
		
		Label lblPath = new Label(shlCreateANew, SWT.NONE);
		lblPath.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true, 3, 1));
		lblPath.setText("File for new disk image (optional):");
		new Label(shlCreateANew, SWT.NONE);
		
		textPath = new Text(shlCreateANew, SWT.BORDER);
		textPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		if (MainWin.getInstanceConfig().containsKey("LocalDiskDir"))
			textPath.setText(MainWin.getInstanceConfig().getString("LocalDiskDir") + System.getProperty("file.separator"));
		
		btnFile = new Button(shlCreateANew, SWT.NONE);
		btnFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				final String curpath = textPath.getText();
				
				//SwingUtilities.invokeLater(new Runnable() 
				//{
						
				//		public void run() 
						{
							final String filename = MainWin.getFile(true, false, curpath, "File for new disk image..", "Create");
								// 	check if a file was selected
							if (filename != null)
							{
								if (!textPath.isDisposed())
								{
									shlCreateANew.getDisplay().asyncExec(new Runnable() 
									{
										
										public void run() 
										{
											textPath.setText(filename);
										}
									});
								}
							}
						}
					
			//	});
			
			}
		});
		btnFile.setText("...");
		
		Button btnCreate = new Button(shlCreateANew, SWT.NONE);
		btnCreate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.sendCommand("dw disk create " + spinnerDrive.getSelection() + " " + textPath.getText());
					
				e.display.getActiveShell().close();
				
			}
		});
		btnCreate.setText("Create");
		
		Button btnCancel = new Button(shlCreateANew, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			}
		});
		btnCancel.setText("Cancel");

	}

	protected Spinner getSpinner() {
		return spinnerDrive;
	}
	protected Button getBtnFile() {
		return btnFile;
	}
}
