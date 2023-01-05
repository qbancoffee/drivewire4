package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class UpdateWin extends Dialog {

	protected Object result;
	protected Shell shlAnUpdateIs;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UpdateWin(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlAnUpdateIs.open();
		shlAnUpdateIs.layout();
		Display display = getParent().getDisplay();
		while (!shlAnUpdateIs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	

	

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlAnUpdateIs = new Shell(getParent(), getStyle());
		shlAnUpdateIs.setImage(SWTResourceManager.getImage(UpdateWin.class, "/menu/network.png"));
		shlAnUpdateIs.setSize(469, 530);
		shlAnUpdateIs.setText("An update is available...");
		shlAnUpdateIs.setLayout(new GridLayout(1, false));
		
		Browser browser = new Browser(shlAnUpdateIs, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(shlAnUpdateIs, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(4, false));
		
		Button btnIgnoreUpdate = new Button(composite, SWT.NONE);
		btnIgnoreUpdate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnIgnoreUpdate.setText(" Ignore Update ");
		
		Button btnInstallUpdate = new Button(composite, SWT.NONE);
		btnInstallUpdate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnInstallUpdate.setText(" Install Update ");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText(" Cancel ");

	}

}
