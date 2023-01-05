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

public class ErrorWin extends Dialog {

	protected Object result;
	protected Shell shlAnErrorHas;

	private Text txtSummary;

	private Button btnClose;
	
	private String title;
	private String summary;
	private String detail;
	private Text textDetail;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 * @wbp.parser.constructor
	 */
	public ErrorWin(Shell parent, int style, String title, String summary, String detail) {
		super(parent, style);
		this.title = title;
		this.summary = summary;
		this.detail = detail;
		
	}

	public ErrorWin(Shell parent, int style, DWError dwerror)
	{
		super(parent, style);
		this.title = dwerror.getTitle();
		this.summary = dwerror.getSummary();
		this.detail = dwerror.getDetail();

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

	

	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		Random rand = new Random();
		
		shlAnErrorHas = new Shell(getParent(), getStyle());
		shlAnErrorHas.setSize(487, 320);
		shlAnErrorHas.setText(title);
		GridLayout gl_shlAnErrorHas = new GridLayout(3, false);
		gl_shlAnErrorHas.marginTop = 5;
		gl_shlAnErrorHas.marginRight = 5;
		gl_shlAnErrorHas.marginLeft = 5;
		gl_shlAnErrorHas.marginBottom = 5;
		shlAnErrorHas.setLayout(gl_shlAnErrorHas);
		
		
		
		txtSummary = new Text(shlAnErrorHas, SWT.READ_ONLY | SWT.WRAP);
		txtSummary.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		txtSummary.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtSummary.setEditable(false);
		txtSummary.setText(summary);
		
		Label lblNewLabel = new Label(shlAnErrorHas, SWT.NONE);
		lblNewLabel.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(ErrorWin.class, "/animals/a" + rand.nextInt(22) + ".png"));
		
		textDetail = new Text(shlAnErrorHas, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		GridData gd_textDetail = new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1);
		gd_textDetail.verticalIndent = 10;
		textDetail.setLayoutData(gd_textDetail);
		textDetail.setEditable(false);
		textDetail.setText(detail);
		
		Button btnSubmitABug = new Button(shlAnErrorHas, SWT.NONE);
		GridData gd_btnSubmitABug = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		gd_btnSubmitABug.verticalIndent = 10;
		btnSubmitABug.setLayoutData(gd_btnSubmitABug);
		btnSubmitABug.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(ErrorWin.class, "/menu/bug.png"));
		btnSubmitABug.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				BugReportWin brwin = new BugReportWin(shlAnErrorHas,SWT.DIALOG_TRIM,title,summary,detail);
				brwin.open();
			
			}
		});
		btnSubmitABug.setText("Submit a bug report...");
		
		
		
		btnClose = new Button(shlAnErrorHas, SWT.NONE);
		GridData gd_btnClose = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 2, 1);
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
		
	}
}
