package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class SerialParamPage extends WizardPage
{
	private Combo comboRate;
	protected Label lblTheDefaultValues;
	private Combo comboParity;
	private Combo comboStopbits;
	private Button buttonRTS_IN;
	private Button buttonRTS_OUT;
	private Button buttonSetRTS;
	private Button buttonSetDTR;

	/**
	 * Create the wizard.
	 */
	public SerialParamPage()
	{
		super("wizardPage");
		setTitle("Choose Serial Port Parameters");
		setDescription("If necessary, adjust the serial port parameters.");
		setImageDescriptor(ResourceManager.getImageDescriptor(PlatformPage.class, "/wizard/dwlogo_pad64.png"));
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	
	@Override
	public void setVisible(boolean v)
	{
		super.setVisible(v);
		
		if (v)
		{
			lblTheDefaultValues.setText("The default values for the " + ((SimpleWizard) getWizard()).getPlatform().name + " will be correct in most situations."  );
			
			for (int x = 0;x<this.comboRate.getItemCount();x++)
			{
				if (  ((SimpleWizard) getWizard()).getSerialRate() == Integer.parseInt(this.comboRate.getItem(x))  )
				{
					this.comboRate.select(x);
				}
			}
			
			for (int x = 0;x<this.comboParity.getItemCount();x++)
			{
				if (  ((SimpleWizard) getWizard()).getSerialParity().equals(this.comboParity.getItem(x))  )
				{
					this.comboParity.select(x);
				}
			}
			
			for (int x = 0;x<this.comboStopbits.getItemCount();x++)
			{
				if (  ((SimpleWizard) getWizard()).getSerialStopbits().equals(this.comboStopbits.getItem(x))  )
				{
					this.comboStopbits.select(x);
				}
			}
			
			this.buttonRTS_IN.setSelection( ((SimpleWizard) getWizard()).isSerialRTSCTSin()  );
			this.buttonRTS_OUT.setSelection( ((SimpleWizard) getWizard()).isSerialRTSCTSout()  );
			
			this.buttonSetDTR.setSelection( ((SimpleWizard) getWizard()).isSerialDTR()  );
			this.buttonSetRTS.setSelection( ((SimpleWizard) getWizard()).isSerialRTS()  );
			
		}
		
	}
	
	
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label labelTopspace = new Label(container, SWT.NONE);
		labelTopspace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		labelTopspace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblTheDefaultValues = new Label(container, SWT.NONE);
		lblTheDefaultValues.setFont(SWTResourceManager.getFont(lblTheDefaultValues.getFont().getFontData()[0].getName(),lblTheDefaultValues.getFont().getFontData()[0].getHeight(), SWT.BOLD));

		GridData gd_lblTheDefaultValues = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_lblTheDefaultValues.horizontalIndent = 10;
		gd_lblTheDefaultValues.verticalIndent = 10;
		lblTheDefaultValues.setLayoutData(gd_lblTheDefaultValues);
		lblTheDefaultValues.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTheDefaultValues.setText("The default values will be correct in most situations."  );
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label label_1 = new Label(container, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 8);
		gd_label_1.horizontalIndent = 20;
		label_1.setLayoutData(gd_label_1);
		label_1.setImage(SWTResourceManager.getImage(SerialParamPage.class, "/wizard/cocoman_doors.png"));
		
		Label lblPortRate = new Label(container, SWT.NONE);
		lblPortRate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPortRate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPortRate.setText("Port speed:");
		
		comboRate = new Combo(container, SWT.NONE);
		comboRate.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((SimpleWizard) getWizard()).setSerialRate(Integer.parseInt(comboRate.getItem(comboRate.getSelectionIndex())));
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			
			}
		});
		comboRate.setItems(new String[] {"19200", "38400", "57600", "115200", "230400", "460800", "921600"});
		
		Label lblParity = new Label(container, SWT.NONE);
		GridData gd_lblParity = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblParity.verticalIndent = 10;
		lblParity.setLayoutData(gd_lblParity);
		lblParity.setAlignment(SWT.RIGHT);
		lblParity.setText("Parity:");
		lblParity.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		comboParity = new Combo(container, SWT.NONE);
		comboParity.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((SimpleWizard) getWizard()).setSerialParity(comboParity.getItem(comboParity.getSelectionIndex()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				
			}
				
		
		});
		comboParity.setItems(new String[] {"none", "even", "odd", "mark", "space" });
		GridData gd_comboParity = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboParity.verticalIndent = 10;
		comboParity.setLayoutData(gd_comboParity);
		
		Label lblStopbits = new Label(container, SWT.NONE);
		lblStopbits.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStopbits.setText("Stopbits:");
		lblStopbits.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		comboStopbits = new Combo(container, SWT.NONE);
		comboStopbits.setItems(new String[] {"1", "1.5", "2"});
		
		comboStopbits.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((SimpleWizard) getWizard()).setSerialStopbits(comboStopbits.getItem(comboStopbits.getSelectionIndex()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			
			}
		});
		
		Label lblSetDtr = new Label(container, SWT.NONE);
		GridData gd_lblSetDtr = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSetDtr.verticalIndent = 10;
		lblSetDtr.setLayoutData(gd_lblSetDtr);
		lblSetDtr.setText("Set DTR:");
		lblSetDtr.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		buttonSetDTR = new Button(container, SWT.CHECK);
		buttonSetDTR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((SimpleWizard) getWizard()).setSerialDTR( buttonSetDTR.getSelection() );
			}
		});
		GridData gd_buttonSetDTR = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_buttonSetDTR.verticalIndent = 10;
		buttonSetDTR.setLayoutData(gd_buttonSetDTR);
		
		Label lblSetRts = new Label(container, SWT.NONE);
		lblSetRts.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSetRts.setText("Set RTS:");
		lblSetRts.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		buttonSetRTS = new Button(container, SWT.CHECK);
		buttonSetRTS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((SimpleWizard) getWizard()).setSerialRTS( buttonSetRTS.getSelection() );
			}
		});
		
		Label lblUseInboundRtscts = new Label(container, SWT.NONE);
		GridData gd_lblUseInboundRtscts = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblUseInboundRtscts.verticalIndent = 10;
		lblUseInboundRtscts.setLayoutData(gd_lblUseInboundRtscts);
		lblUseInboundRtscts.setText("Inbound RTS/CTS:");
		lblUseInboundRtscts.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		buttonRTS_IN = new Button(container, SWT.CHECK);
		buttonRTS_IN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((SimpleWizard) getWizard()).setSerialRTSCTSin( buttonRTS_IN.getSelection() );
			}
		});
		
		GridData gd_buttonRTS_IN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_buttonRTS_IN.verticalIndent = 10;
		buttonRTS_IN.setLayoutData(gd_buttonRTS_IN);
		
		Label lblOutboundRtscts = new Label(container, SWT.NONE);
		GridData gd_lblOutboundRtscts = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblOutboundRtscts.horizontalIndent = 10;
		lblOutboundRtscts.setLayoutData(gd_lblOutboundRtscts);
		lblOutboundRtscts.setText("Outbound RTS/CTS:");
		lblOutboundRtscts.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		buttonRTS_OUT = new Button(container, SWT.CHECK);
		buttonRTS_OUT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((SimpleWizard) getWizard()).setSerialRTSCTSout( buttonRTS_OUT.getSelection() );
			}
		});
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}


	
	@Override
	public IWizardPage getNextPage()
	{
		return ((SimpleWizard)getWizard()).midiPage;
	}
	
}
