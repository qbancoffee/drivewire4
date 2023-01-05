package com.groupunix.drivewireui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class URLInputWin extends Dialog {

	protected String result = null;
	protected Shell shlEnterURL;
	private Combo cmbURL;
	private int diskno;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public URLInputWin(Shell parent, int diskno) 
	{
		super(parent, SWT.DIALOG_TRIM);
		this.diskno = diskno;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		
		loadURLHistory();
		
		shlEnterURL.open();
		shlEnterURL.layout();
		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlEnterURL.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlEnterURL.getBounds().height / 2);
		
		shlEnterURL.setLocation(x, y);
		
		while (!shlEnterURL.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	

	private void loadURLHistory()
	{
		if (MainWin.getDiskHistory() != null)
		{
			List<String> dhist = MainWin.getDiskHistory();
			for (String d : dhist)
			{
				cmbURL.add(d, 0);
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlEnterURL = new Shell(getParent(), getStyle());
		shlEnterURL.setSize(445, 165);
		shlEnterURL.setText("Enter URL for disk image...");
		GridLayout gl_shlEnterURL = new GridLayout(2, false);
		gl_shlEnterURL.marginTop = 5;
		gl_shlEnterURL.marginRight = 5;
		gl_shlEnterURL.marginLeft = 5;
		gl_shlEnterURL.marginBottom = 5;
		shlEnterURL.setLayout(gl_shlEnterURL);
		
		Label lblEnterURL = new Label(shlEnterURL, SWT.NONE);
		lblEnterURL.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblEnterURL.setText("Enter a URL to load image for drive " + diskno + " from:");
		
		cmbURL = new Combo(shlEnterURL, SWT.NONE);
		cmbURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		
		Button btnOk = new Button(shlEnterURL, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.verticalIndent = 10;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				result = cmbURL.getText();
				e.display.getActiveShell().close();
			}
		});
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(shlEnterURL, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.verticalIndent = 10;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
			
			}
		});
		btnCancel.setText("Cancel");
		
	}
}
