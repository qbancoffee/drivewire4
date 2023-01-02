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

public class CreateDiskWin extends Dialog {

	protected Object result;
	protected static Shell shlCreateANew;
	private Text textPath;
	private Spinner spinnerDrive;
	private Button btnFile;
	private CCombo comboFormat;
	private CCombo comboImgType;

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
		shlCreateANew.setSize(336, 261);
		shlCreateANew.setText("Create a new disk image...");
		GridLayout gl_shlCreateANew = new GridLayout(5, false);
		gl_shlCreateANew.marginTop = 5;
		gl_shlCreateANew.marginRight = 5;
		gl_shlCreateANew.marginLeft = 5;
		gl_shlCreateANew.marginBottom = 5;
		shlCreateANew.setLayout(gl_shlCreateANew);
		
		Label lblInsertNewDisk = new Label(shlCreateANew, SWT.NONE);
		lblInsertNewDisk.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblInsertNewDisk.setAlignment(SWT.RIGHT);
		lblInsertNewDisk.setText("Create new disk in drive:");
		
		spinnerDrive = new Spinner(shlCreateANew, SWT.BORDER);
		spinnerDrive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerDrive.setMaximum(255);
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblNewLabel = new Label(shlCreateANew, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setImage(SWTResourceManager.getImage(CreateDiskWin.class, "/wizard/new-disk-32.png"));
		
		
		Label lblImgType = new Label(shlCreateANew, SWT.NONE);
		lblImgType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 2, 1));
		lblImgType.setText("Format for new image:");
		
		comboImgType = new CCombo(shlCreateANew, SWT.BORDER);
		comboImgType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboImgType.setEditable(false);
		comboImgType.setItems(new String[] {"raw (.dsk)" });
		comboImgType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		comboImgType.select(0);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		
		Label lblFormatForNew = new Label(shlCreateANew, SWT.NONE);
		lblFormatForNew.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 2, 1));
		lblFormatForNew.setText("Filesystem for new disk:");
		
		comboFormat = new CCombo(shlCreateANew, SWT.BORDER);
		comboFormat.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		comboFormat.setEditable(false);
		comboFormat.setItems(new String[] {"none", "DECB"});
		comboFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
		comboFormat.select(0);
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		Label lblPath = new Label(shlCreateANew, SWT.NONE);
		lblPath.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true, 3, 1));
		lblPath.setText("File for new disk image (optional):");
		new Label(shlCreateANew, SWT.NONE);
		new Label(shlCreateANew, SWT.NONE);
		
		textPath = new Text(shlCreateANew, SWT.BORDER);
		textPath.setEditable(false);
		textPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
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
		new Label(shlCreateANew, SWT.NONE);
		
		Label label_1 = new Label(shlCreateANew, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
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
				sendCreateCommand(e);
				e.display.getActiveShell().close();
				
			}
		});
		btnCreate.setText(" Create ");
		
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

	protected void sendCreateCommand(final SelectionEvent e) 
	{
		final List<String> cmds = new ArrayList<String>();
		
		if (textPath.getText().equals(""))
			cmds.add("dw disk create " + spinnerDrive.getSelection());
		else
			cmds.add("dw disk create " + spinnerDrive.getSelection() + " " + textPath.getText());
		
		if (comboFormat.getSelectionIndex() == 1)
		{
			cmds.add("dw disk dos format " + spinnerDrive.getSelection());
		}
		
		
		getParent().getDisplay().asyncExec(
				  new Runnable() 
				  {
					  public void run()
					  {
						  SendCommandWin win = new SendCommandWin(e.display.getActiveShell(), SWT.DIALOG_TRIM, cmds, "Creating disk image...", "Please wait while the new image is created.");
						  win.open();
					  }
				  });
		
	}

	protected Spinner getSpinner() {
		return spinnerDrive;
	}
	protected Button getBtnFile() {
		return btnFile;
	}
}
