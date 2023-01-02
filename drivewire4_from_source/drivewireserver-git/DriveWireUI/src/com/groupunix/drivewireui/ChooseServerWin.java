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

public class ChooseServerWin extends Dialog {

	protected Object result;
	protected Shell shlChooseServer;
	private Combo cmbHost;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ChooseServerWin(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		loadServerHistory();
		
		shlChooseServer.open();
		shlChooseServer.layout();
		Display display = getParent().getDisplay();
		
		int x = getParent().getBounds().x + (getParent().getBounds().width / 2) - (shlChooseServer.getBounds().width / 2);
		int y = getParent().getBounds().y + (getParent().getBounds().height / 2) - (shlChooseServer.getBounds().height / 2);
		
		shlChooseServer.setLocation(x, y);
		
		while (!shlChooseServer.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void loadServerHistory() 
	{
		List<String> sh = MainWin.getServerHistory();
		
		if (sh != null)
		{
			for (int i = sh.size() - 1;i > -1;i--)
			{
				this.cmbHost.add(sh.get(i));
			}
			
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlChooseServer = new Shell(getParent(), getStyle());
		shlChooseServer.setSize(362, 149);
		shlChooseServer.setText("Choose Server...");
		shlChooseServer.setLayout(new GridLayout(2, false));
		
		Label lblEnterServerAddress = new Label(shlChooseServer, SWT.NONE);
		lblEnterServerAddress.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblEnterServerAddress.setText("Enter server address in the form host:port");
		
		cmbHost = new Combo(shlChooseServer, SWT.NONE);
		cmbHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		cmbHost.setText(MainWin.getHost() + ":" + MainWin.getPort());
		
		Button btnOk = new Button(shlChooseServer, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (cmbHost.getText().contains(":"))
				{
					String[] hp = cmbHost.getText().split(":");
	
					if (UIUtils.validateNum(hp[1], 1, 65535))	
					{
						MainWin.addServerToHistory(cmbHost.getText());
						MainWin.setHost(hp[0]);
						MainWin.setPort(hp[1]);
						MainWin.restartServerConn();
						
					
						e.display.getActiveShell().close();
					}
					else
					{
						MainWin.showError("Invalid server entry", "The port entered for server address is not valid.", "Valid TCP port range is 1-65535.");
					}
				}
				else
				{
					MainWin.showError("Invalid server entry", "The data entered for server address is not valid.", "Please enter a server address and port in the form host:port.\r\n\nFor example: 127.0.0.1:6800");
				}
			
			}
		});
		btnOk.setText("Ok");
		
		Button btnCancel = new Button(shlChooseServer, SWT.NONE);
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
