package com.groupunix.drivewireui;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.groupunix.drivewireui.exceptions.DWUIOperationFailedException;

public class ServerConfigWin extends Dialog {

	protected Object result;
	protected Shell shlServerConfiguration;
	
	
	private Button btnLogToConsole;
	private Button btnLogToFile;
	private Combo comboLogLevel;
	private Text textLogFile;
	private Text textLogFormat;
	private Button btnUIEnabled;
	private Spinner textUIPort;
	private Spinner textLazyWrite;
	private Text textLocalDiskDir;
	
	private static Composite grpLogging;
	private static Composite grpMiscellaneous;
	private static Composite grpUserInterfaceSupport;
	private Button btnLogUIConnections;
	private static Button btnApply; 
	private Button btnUseRxtx;
	private Button btnLoadRxtx;
	private boolean dataReady = false;
	private static Composite grpRxtx;
	private Composite composite;
	private TabFolder tabFolder;
	private TabItem tbtmLogging;
	private TabItem tbtmUISupport;
	private TabItem tbtmDisk;
	private TabItem tbtmRXTX;

	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ServerConfigWin(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 * @throws IOException 
	 * @throws DWUIOperationFailedException 
	 */
	public Object open() throws DWUIOperationFailedException, IOException {
		createContents();
		displaySettings();
		
		shlServerConfiguration.open();
		shlServerConfiguration.layout();
		Display display = getParent().getDisplay();
		this.dataReady = true;
		while (!shlServerConfiguration.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	

	
	
	private void updateApply()
	{
		if (this.dataReady )
		{
			HashMap<String,String> res = getChangedValues();
		
			if (res.size() > 0)
			{
				ServerConfigWin.btnApply.setEnabled(true);
			}	
			else
			{
				ServerConfigWin.btnApply.setEnabled(false);
			}
		}
	}
	

	
	
	private HashMap<String, String> getChangedValues() 
	{
		HashMap<String,String> res = new HashMap<String,String>();
		
		addIfChanged(res,"LogToConsole",UIUtils.bTos(this.btnLogToConsole.getSelection()));
		addIfChanged(res,"LogToFile",UIUtils.bTos(this.btnLogToFile.getSelection()));
		addIfChanged(res,"UIEnabled",UIUtils.bTos(this.btnUIEnabled.getSelection()));
		addIfChanged(res,"LogFile",this.textLogFile.getText());
		addIfChanged(res,"LogFormat",this.textLogFormat.getText());
		addIfChanged(res,"UIPort",this.textUIPort.getText());
		addIfChanged(res,"DiskLazyWriteInterval",this.textLazyWrite.getText());
		addIfChanged(res,"LocalDiskDir",this.textLocalDiskDir.getText());
		addIfChanged(res,"LogLevel",this.comboLogLevel.getItem(this.comboLogLevel.getSelectionIndex()));
		addIfChanged(res,"LogUIConnections",UIUtils.bTos(this.btnLogUIConnections.getSelection()));
		addIfChanged(res,"UseRXTX",UIUtils.bTos(this.btnUseRxtx.getSelection()));
		addIfChanged(res,"LoadRXTX",UIUtils.bTos(this.btnLoadRxtx.getSelection()));
		
		return(res);
	}

	private void addIfChanged(HashMap<String, String> map, String key, String value) 
	{
		if ((!MainWin.dwconfig.containsKey(key)) || (!MainWin.dwconfig.getProperty(key).equals(value)))
		{ 
			map.put(key, value);
		}
		
	}

	



	private void displaySettings() 
	{
		// apply settings, considering defaults
		
		
		this.btnLogToConsole.setSelection(MainWin.dwconfig.getBoolean("LogToConsole",true));
		this.btnLogToFile.setSelection(MainWin.dwconfig.getBoolean("LogToFile", false));
		this.btnUIEnabled.setSelection(MainWin.dwconfig.getBoolean("UIEnabled", false));
		
		this.textLogFile.setText(MainWin.dwconfig.getString("LogFile",""));
		
		this.textLogFormat.setText(MainWin.dwconfig.getString("LogFormat","%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %26.26C: %m%n"));
		this.textLocalDiskDir.setText(MainWin.dwconfig.getString("LocalDiskDir",""));
		this.comboLogLevel.select(this.comboLogLevel.indexOf(MainWin.dwconfig.getString("LogLevel","WARN")));
		this.btnLogUIConnections.setSelection(MainWin.dwconfig.getBoolean("LogUIConnections",false));
		
		this.btnLoadRxtx.setSelection(MainWin.dwconfig.getBoolean("LoadRXTX",true));
		this.btnUseRxtx.setSelection(MainWin.dwconfig.getBoolean("UseRXTX",true));
		
		this.textLazyWrite.setSelection(MainWin.dwconfig.getInt("DiskLazyWriteInterval", 15000));
		this.textUIPort.setSelection(MainWin.dwconfig.getInt("UIPort",6800));
		updateApply();
		
	}

	private void createContents() 
	{
		
		shlServerConfiguration = new Shell(getParent(), getStyle());
		shlServerConfiguration.setSize(449, 290);
		
		
		shlServerConfiguration.setText("Server Configuration");
		shlServerConfiguration.setLayout(new FormLayout());
			

		
		
		
		tabFolder = new TabFolder(shlServerConfiguration, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.right = new FormAttachment(100, -10);
		fd_tabFolder.bottom = new FormAttachment(0, 212);
		fd_tabFolder.top = new FormAttachment(0, 10);
		fd_tabFolder.left = new FormAttachment(0, 10);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setBounds(5, 5, 440, 200);
		
		tbtmLogging = new TabItem(tabFolder, SWT.NONE);
		tbtmLogging.setText("Logging");
		
		grpLogging = new Composite(tabFolder, SWT.NONE);
		tbtmLogging.setControl(grpLogging);
		grpLogging.setLayout(null);
		
		btnLogToConsole = new Button(grpLogging, SWT.CHECK);
		btnLogToConsole.setBounds(240, 16, 165, 23);
		btnLogToConsole.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnLogToConsole.setText("Log to console");
		
		btnLogToFile = new Button(grpLogging, SWT.CHECK);
		btnLogToFile.setBounds(240, 40, 165, 23);
		btnLogToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnLogToFile.setText("Log to file");
		
		comboLogLevel = new Combo(grpLogging, SWT.READ_ONLY);
		comboLogLevel.setBounds(101, 17, 112, 23);
		comboLogLevel.setLocation(101, 22);
		comboLogLevel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateApply();
			}
		});
		comboLogLevel.setItems(new String[] {"ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"});
		
		Label lblLogLevel = new Label(grpLogging, SWT.NONE);
		lblLogLevel.setBounds(20, 20, 75, 24);
		lblLogLevel.setLocation(20, 24);
		lblLogLevel.setAlignment(SWT.RIGHT);
		lblLogLevel.setText("Log level:");
		
		Label lblLogFile = new Label(grpLogging, SWT.NONE);
		lblLogFile.setBounds(10, 92, 75, 18);
		lblLogFile.setAlignment(SWT.RIGHT);
		lblLogFile.setText("Log file:");
		
		textLogFile = new Text(grpLogging, SWT.BORDER);
		textLogFile.setBounds(91, 89, 259, 24);
		textLogFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateApply();
			}
		});
		
		Button button = new Button(grpLogging, SWT.NONE);
		button.setBounds(356, 83, 40, 32);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				final FileChooser fc = new FileChooser(textLogFile.getText(),"Choose a log file..",false);
				
				SwingUtilities.invokeLater(new Runnable() 
				{
					
					public void run() 
					{
						final String fname = fc.getFile();
						
						if (!(fname == null) && (!textLogFile.isDisposed()))
						{
							Display.getDefault().asyncExec(new Runnable() 
							{

								
								public void run() 
								{
									textLogFile.setText(fname);	
								}
							});
						}
					}
				});
			}
		});
		button.setText("...");
		
		Label lblLineFormat = new Label(grpLogging, SWT.NONE);
		lblLineFormat.setBounds(10, 124, 75, 15);
		lblLineFormat.setAlignment(SWT.RIGHT);
		lblLineFormat.setText("Log format:");
		
		textLogFormat = new Text(grpLogging, SWT.BORDER);
		textLogFormat.setBounds(101, 121, 301, 24);
		textLogFormat.setBounds(91, 121, 304, 24);
		textLogFormat.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateApply();
			}
		});
		
		
		tbtmUISupport = new TabItem(tabFolder, SWT.NONE);
		tbtmUISupport.setToolTipText("UI Support");
		tbtmUISupport.setText("UI Support");
		
		
		
		grpUserInterfaceSupport = new Composite(tabFolder, SWT.NONE);
		tbtmUISupport.setControl(grpUserInterfaceSupport);
		
		btnUIEnabled = new Button(grpUserInterfaceSupport, SWT.CHECK);
		btnUIEnabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnUIEnabled.setBounds(30, 30, 138, 19);
		btnUIEnabled.setText("UI Enabled");
		
		textUIPort = new Spinner(grpUserInterfaceSupport, SWT.BORDER);
		textUIPort.setPageIncrement(1000);
		textUIPort.setMaximum(65535);
		textUIPort.setMinimum(1);
		textUIPort.setBounds(311, 29, 79, 24);
		
		Label lblListenOnTcp = new Label(grpUserInterfaceSupport, SWT.NONE);
		lblListenOnTcp.setAlignment(SWT.RIGHT);
		lblListenOnTcp.setBounds(174, 32, 131, 18);
		lblListenOnTcp.setText("Listen on TCP port:");
		
		btnLogUIConnections = new Button(grpUserInterfaceSupport, SWT.CHECK);
		btnLogUIConnections.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnLogUIConnections.setBounds(30, 55, 225, 16);
		btnLogUIConnections.setText("Log UI Connections");
	
		
		tbtmDisk = new TabItem(tabFolder, SWT.NONE);
		tbtmDisk.setText("Disk");
		
		
		grpMiscellaneous = new Composite(tabFolder, SWT.NONE);
		tbtmDisk.setControl(grpMiscellaneous);
		
		Label lblDiskSyncLazy = new Label(grpMiscellaneous, SWT.NONE);
		lblDiskSyncLazy.setAlignment(SWT.RIGHT);
		lblDiskSyncLazy.setBounds(22, 49, 200, 18);
		lblDiskSyncLazy.setText("Disk sync lazy write interval (ms):");
		
		textLazyWrite = new Spinner(grpMiscellaneous, SWT.BORDER);
		textLazyWrite.setIncrement(100);
		textLazyWrite.setPageIncrement(1000);
		textLazyWrite.setMaximum(60000);
		textLazyWrite.setMinimum(1000);
		textLazyWrite.setBounds(228, 46, 79, 24);
		
		textLocalDiskDir = new Text(grpMiscellaneous, SWT.BORDER);
		textLocalDiskDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateApply();
			}
		});
		textLocalDiskDir.setBounds(22, 105, 317, 24);
		
		Label lblLocalDiskDirectory = new Label(grpMiscellaneous, SWT.NONE);
		lblLocalDiskDirectory.setBounds(22, 86, 171, 18);
		lblLocalDiskDirectory.setText("Default disk directory:");
		
		Button btnChooseDiskDir = new Button(grpMiscellaneous, SWT.NONE);
		btnChooseDiskDir.addSelectionListener(new SelectionAdapter() 
		{
		
			public void widgetSelected(SelectionEvent e) 
			{
				final FileChooser fc = new FileChooser(textLocalDiskDir.getText(),"Choose disk directory...", true);
				
				SwingUtilities.invokeLater(new Runnable() 
				{
				
					public void run() 
					{
						final String fname = fc.getFile();
						if (!(fname == null) && (!textLocalDiskDir.isDisposed()))
						{
							Display.getDefault().asyncExec(new Runnable() 
							{

								
								public void run() 
								{
									textLocalDiskDir.setText(fname);	
								}
							});
						}
					}
				});
			}
		});
		
			btnChooseDiskDir.setBounds(345, 100, 40, 32);
			btnChooseDiskDir.setText("...");
		
		
		tbtmRXTX = new TabItem(tabFolder, SWT.NONE);
		tbtmRXTX.setText("RXTX");
		
		
		grpRxtx = new Composite(tabFolder, SWT.NONE);
		tbtmRXTX.setControl(grpRxtx);
		
		btnUseRxtx = new Button(grpRxtx, SWT.CHECK);
		btnUseRxtx.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnUseRxtx.setBounds(28, 47, 363, 16);
		btnUseRxtx.setText("Use RXTX (needed for any serial connections)");
		
		btnLoadRxtx = new Button(grpRxtx, SWT.CHECK);
		btnLoadRxtx.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateApply();
			}
		});
		btnLoadRxtx.setBounds(28, 76, 363, 16);
		btnLoadRxtx.setText("Load internal RXTX (disable if provided by system)");
		
	
	
		
		
		composite = new Composite(shlServerConfiguration, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.right = new FormAttachment(100, -13);
		fd_composite.left = new FormAttachment(0, 166);
		fd_composite.bottom = new FormAttachment(100, -10);
		fd_composite.top = new FormAttachment(0, 225);
		composite.setLayoutData(fd_composite);

		FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
		fl_composite.spacing = 10;
		composite.setLayout(fl_composite);
		
		Button btnOk = new Button(composite, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
					
					try 
					{
						// TODO: ? deal with changes while we were open
						
						//if (curserial == UIUtils.getDWConfigSerial())
						//{
							UIUtils.setServerSettings(getChangedValues());
							e.display.getActiveShell().close();
							//shlServerConfiguration.close();
						//}
						//else
						//{
							// something changed while we were open...
							
						//}
					} 
					catch (IOException e1) 
					{
						MainWin.showError("Error sending updated config", e1.getMessage() , UIUtils.getStackTrace(e1));
						
					} catch (DWUIOperationFailedException e2) 
					{
						MainWin.showError("Error sending updated config", e2.getMessage() , UIUtils.getStackTrace(e2));
					}
					
				}
				
		
		});
		btnOk.setText("Ok");
	
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				e.display.getActiveShell().close();
				//shlServerConfiguration.close();
			}
		});
		btnCancel.setText("Cancel");
		
		btnApply = new Button(composite, SWT.NONE);
		btnApply.setEnabled(false);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				try 
				{
					UIUtils.setServerSettings(getChangedValues());
					updateApply();
				} 
				catch (IOException e1) 
				{
					MainWin.showError("Failed to apply settings", "One or more items could not be set", e1.getMessage());
				} 
				catch (DWUIOperationFailedException e1) 
				{
					MainWin.showError("Failed to apply settings", "One or more items could not be set", e1.getMessage());
				}
			}
		});
		btnApply.setText("Apply");
		
		
	}
	
	
	
	
	

	public void submitEvent(String key, Object val)
	{
		System.out.println("ci: " + key + " / " + val);
		
	}
}
