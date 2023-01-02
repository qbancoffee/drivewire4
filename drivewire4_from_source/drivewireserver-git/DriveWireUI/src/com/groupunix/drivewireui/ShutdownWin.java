package com.groupunix.drivewireui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

public class ShutdownWin
{

	protected Object result;
	protected Shell shlShuttingDown;
	protected Label lblPleaseWaitA;
	protected Label lblStatus;
	protected ProgressBar progressBar;
	private int x;
	private int y;
	private Shell parent;
	
	public ShutdownWin(Shell parent, int style)
	{
		//super(parent, style);
		this.parent = parent;
	}


	
	public void open()
	{
		createContents();
		
		if (parent != null)
		{
			this.x = parent.getLocation().x + (parent.getSize().x / 2) - 180;
			this.y = parent.getLocation().y + (parent.getSize().y / 2) - 90;
		}
		
		shlShuttingDown.setLocation(x , y);
		shlShuttingDown.open();
		shlShuttingDown.layout();
		
	
	}


	/**
	 * @wbp.parser.entryPoint
	 */
	private void createContents()
	{
		shlShuttingDown = new Shell(Display.getCurrent(), SWT.NO_TRIM);
		shlShuttingDown.setSize(421, 173);
		shlShuttingDown.setText("Shutting down...");
		shlShuttingDown.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(shlShuttingDown, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("    ");
		new Label(composite, SWT.NONE);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("    ");
		new Label(composite, SWT.NONE);
		
		lblPleaseWaitA = new Label(composite, SWT.WRAP);
		lblPleaseWaitA.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblPleaseWaitA.setAlignment(SWT.CENTER);
		lblPleaseWaitA.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPleaseWaitA.setText("Please wait a moment for DriveWire to save\r\nthe configuration and shutdown cleanly...");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		progressBar.setSize(184, 181);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setSelection(0);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		
		lblStatus = new Label(composite, SWT.WRAP);
		lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setSize(123, 181);
		new Label(composite, SWT.NONE);

	}

	
	
	public void setStatus(final String txt, final int prog)
	{
		
		
			if ((progressBar != null) && !progressBar.isDisposed())
			{
				this.setProgress(prog);
			}
			
			
			if ((lblStatus != null) && !lblStatus.isDisposed())
			{
				lblStatus.setText(txt);
	
			}
			else
				System.out.println("shutdown: " + txt + " (" + prog  + ")");
			
	}



	public void setProgress(final int i)
	{

		if ((progressBar != null) && !progressBar.isDisposed())
		{
			if (progressBar.getSelection() != i)
			{
				progressBar.setSelection(i);
				
				progressBar.redraw();
				lblPleaseWaitA.redraw();
				lblStatus.redraw();
				
			}
			
		}
		
	}
}
