package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class TCPPage extends WizardPage
{
	private Label lblListenPort;
	private Spinner spinnerListenPort;
	private Text textServerHost;
	private Label lblServerHost;
	private Label lblServerPort;
	private Spinner spinnerServerPort;
	private Button btnUseClientMode;
	private Button btnUseServerMode;

	/**
	 * Create the wizard.
	 */
	public TCPPage()
	{
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(TCPPage.class, "/wizard/dwlogo_pad64.png"));
		setTitle("TCP/IP Connection Details");
		setDescription("Define the parameters for this TCP/IP connection.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		setControl(container);
		container.setLayout(new GridLayout(5, false));
		
		Label lblThereAreTwo = new Label(container, SWT.NONE);
		GridData gd_lblThereAreTwo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_lblThereAreTwo.horizontalIndent = 10;
		gd_lblThereAreTwo.verticalIndent = 10;
		lblThereAreTwo.setLayoutData(gd_lblThereAreTwo);
		lblThereAreTwo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblThereAreTwo.setText("DriveWire supports two different types of TCP/IP device connections.");
		
		Label label = new Label(container, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 3);
		gd_label.verticalIndent = 10;
		label.setLayoutData(gd_label);
		label.setImage(SWTResourceManager.getImage(TCPPage.class, "/wizard/cocoman_shout.png"));
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		Label lblInServerMode = new Label(container, SWT.NONE);
		GridData gd_lblInServerMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_lblInServerMode.horizontalIndent = 10;
		gd_lblInServerMode.verticalIndent = 30;
		lblInServerMode.setLayoutData(gd_lblInServerMode);
		lblInServerMode.setText("In Server mode, DriveWire will listen for connections on a specified\r\nport.  Emulators with DriveWire capability normally use this mode.");
		lblInServerMode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		btnUseServerMode = new Button(container, SWT.RADIO);
		btnUseServerMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMode();
			}
		});
		
		
		btnUseServerMode.setFont(SWTResourceManager.getFont(btnUseServerMode.getFont().getFontData()[0].getName(), btnUseServerMode.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		GridData gd_btnUseServerMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnUseServerMode.horizontalIndent = 10;
		gd_btnUseServerMode.verticalIndent = 5;
		btnUseServerMode.setLayoutData(gd_btnUseServerMode);
		btnUseServerMode.setText(" Use Server Mode");
		btnUseServerMode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblListenPort = new Label(container, SWT.NONE);
		lblListenPort.setFont(SWTResourceManager.getFont(lblListenPort.getFont().getFontData()[0].getName(), lblListenPort.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		GridData gd_lblListenPort = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblListenPort.horizontalIndent = 20;
		gd_lblListenPort.verticalIndent = 5;
		lblListenPort.setLayoutData(gd_lblListenPort);
		lblListenPort.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblListenPort.setText("Listen on port:");
		
		spinnerListenPort = new Spinner(container, SWT.BORDER);
		spinnerListenPort.setMaximum(65535);
		spinnerListenPort.setMinimum(1);
		spinnerListenPort.setSelection(65504);
		GridData gd_spinnerListenPort = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spinnerListenPort.verticalIndent = 5;
		spinnerListenPort.setLayoutData(gd_spinnerListenPort);
		
		spinnerListenPort.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				((SimpleWizard) getWizard()).setTcpPort(spinnerListenPort.getSelection());
			}
		});
		new Label(container, SWT.NONE);
		
		Label lblInClientMode = new Label(container, SWT.NONE);
		GridData gd_lblInClientMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_lblInClientMode.horizontalIndent = 10;
		gd_lblInClientMode.verticalIndent = 30;
		lblInClientMode.setLayoutData(gd_lblInClientMode);
		lblInClientMode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblInClientMode.setText("In Client mode, DriveWire will attempt to establish and maintain a connection to a specified host.\r\nThis is typically used to communicate with TCP to serial adapters.");
		new Label(container, SWT.NONE);
		
		btnUseClientMode = new Button(container, SWT.RADIO);
		btnUseClientMode.setFont(SWTResourceManager.getFont(btnUseClientMode.getFont().getFontData()[0].getName(), btnUseClientMode.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		GridData gd_btnUseClientMode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnUseClientMode.verticalIndent = 5;
		gd_btnUseClientMode.horizontalIndent = 10;
		btnUseClientMode.setLayoutData(gd_btnUseClientMode);
		btnUseClientMode.setText(" Use Client Mode");
		btnUseClientMode.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnUseClientMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMode();
			}
		});
		
		lblServerHost = new Label(container, SWT.NONE);
		lblServerHost.setFont(SWTResourceManager.getFont(lblServerHost.getFont().getFontData()[0].getName(), lblServerHost.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		GridData gd_lblServerHost = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblServerHost.verticalIndent = 5;
		lblServerHost.setLayoutData(gd_lblServerHost);
		lblServerHost.setText("Connect to host:");
		lblServerHost.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		textServerHost = new Text(container, SWT.BORDER);
		textServerHost.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				((SimpleWizard) getWizard()).setTcpClientHost(textServerHost.getText());
			}
		});
		GridData gd_textServerHost = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_textServerHost.verticalIndent = 5;
		textServerHost.setLayoutData(gd_textServerHost);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		lblServerPort = new Label(container, SWT.NONE);
		lblServerPort.setFont(SWTResourceManager.getFont(lblServerPort.getFont().getFontData()[0].getName(), lblServerPort.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		lblServerPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerPort.setText("On port:");
		lblServerPort.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		spinnerServerPort = new Spinner(container, SWT.BORDER);
		spinnerServerPort.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				((SimpleWizard) getWizard()).setTcpPort(spinnerServerPort.getSelection());
			}
		});
		spinnerServerPort.setMaximum(65535);
		spinnerServerPort.setMinimum(1);
		spinnerServerPort.setSelection(65504);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		if ( ((SimpleWizard) getWizard()).isTcpClient() )
		{
			this.btnUseClientMode.setSelection(true);
		}
		else
		{
			this.btnUseServerMode.setSelection(true);
		}
		
		handleMode();
		
	}

	protected void handleMode()
	{
		this.lblListenPort.setEnabled(this.btnUseServerMode.getSelection());
		this.spinnerListenPort.setEnabled(this.btnUseServerMode.getSelection());
		
		this.lblServerHost.setEnabled(!this.btnUseServerMode.getSelection());
		this.textServerHost.setEnabled(!this.btnUseServerMode.getSelection());
		this.lblServerPort.setEnabled(!this.btnUseServerMode.getSelection());
		this.spinnerServerPort.setEnabled(!this.btnUseServerMode.getSelection());
		
		((SimpleWizard) this.getWizard()).setTcpClient(!this.btnUseServerMode.getSelection());
		
	}

	protected Label getLblListenPort() {
		return lblListenPort;
	}
	protected Spinner getSpinnerListenPort() {
		return spinnerListenPort;
	}
	protected Label getLblServerHost() {
		return lblServerHost;
	}
	protected Text getTextServerHost() {
		return textServerHost;
	}
	protected Label getLblServerPort() {
		return lblServerPort;
	}
	protected Spinner getSpinnerServerPort() {
		return spinnerServerPort;
	}
}
