package com.groupunix.drivewireui;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class BugDialog extends Dialog {

	protected Object result;
	protected Shell shlAnErrorHas;

	private Button btnClose;
	
	private Text textDetail;
	private Throwable throwed;
	
	
	

	public BugDialog(Shell parent, int style, Throwable e)
	{
		super(parent, style);
		this.throwed = e;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();

		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlAnErrorHas.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlAnErrorHas.getBounds().height / 2);
		
		shlAnErrorHas.setLocation(x, y);
		
		shlAnErrorHas.open();
		shlAnErrorHas.layout();
		
		while (!shlAnErrorHas.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	

	
	private void createContents() 
	{
		shlAnErrorHas = new Shell(getParent(), getStyle());
		shlAnErrorHas.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(BugDialog.class, "/logging/error.png"));
		shlAnErrorHas.setText("DW4UI has thrown an exception");
		shlAnErrorHas.setSize(600, 387);
		GridLayout gl_shlAnErrorHas = new GridLayout(2, false);
		gl_shlAnErrorHas.marginTop = 5;
		gl_shlAnErrorHas.marginRight = 5;
		gl_shlAnErrorHas.marginLeft = 5;
		gl_shlAnErrorHas.marginBottom = 5;
		shlAnErrorHas.setLayout(gl_shlAnErrorHas);
		
		Label lblStackTrace = new Label(shlAnErrorHas, SWT.NONE);
		GridData gd_lblStackTrace = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblStackTrace.horizontalIndent = 5;
		lblStackTrace.setLayoutData(gd_lblStackTrace);
		lblStackTrace.setText("Stack Trace:");
		new Label(shlAnErrorHas, SWT.NONE);
		
		textDetail = new Text(shlAnErrorHas, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		textDetail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		textDetail.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WHITE));
		textDetail.setEditable(false);
		textDetail.setText(UIUtils.getStackTrace(this.throwed));
		
		Label lblItMayBe = new Label(shlAnErrorHas, SWT.NONE);
		GridData gd_lblItMayBe = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_lblItMayBe.verticalIndent = 5;
		lblItMayBe.setLayoutData(gd_lblItMayBe);
		lblItMayBe.setText("This is probably a bug.  Please consider submitting a bug report.\r\nDriveWire will attempt a clean shutdown when you close this dialog.");
		
		Button btnSubmitABug = new Button(shlAnErrorHas, SWT.NONE);
		GridData gd_btnSubmitABug = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnSubmitABug.verticalIndent = 10;
		btnSubmitABug.setLayoutData(gd_btnSubmitABug);
		btnSubmitABug.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(BugDialog.class, "/menu/bug.png"));
		btnSubmitABug.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				BugReportWin brwin = new BugReportWin(shlAnErrorHas,SWT.DIALOG_TRIM,"UI Exception",throwed.getMessage(),UIUtils.getStackTrace(throwed));
				brwin.open();
			
			}
		});
		btnSubmitABug.setText("Submit a bug report...");
		
		
		
		btnClose = new Button(shlAnErrorHas, SWT.NONE);
		GridData gd_btnClose = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnClose.verticalIndent = 10;
		btnClose.setLayoutData(gd_btnClose);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
			
				e.display.getActiveShell().close();
			
			}
		});
		btnClose.setText("Close");
		Random rand = new Random();
	}
}
