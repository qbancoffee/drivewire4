package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.MainWin;

public class PrinterPage extends WizardPage
{
	private Text textDir;

	/**
	 * Create the wizard.
	 */
	public PrinterPage()
	{
		super("wizardPage");
		setTitle("Printer Options");
		setDescription("Select default printing options.");
		setImageDescriptor(ResourceManager.getImageDescriptor(PrinterPage.class, "/wizard/dwlogo_pad64.png"));
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		
		setControl(container);
		container.setLayout(new GridLayout(5, false));
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		Label lblThere = new Label(container, SWT.WRAP);
		GridData gd_lblThere = new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1);
		gd_lblThere.widthHint = 550;
		gd_lblThere.horizontalIndent = 20;
		gd_lblThere.verticalIndent = 10;
		lblThere.setLayoutData(gd_lblThere);
		lblThere.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblThere.setText("There are two different types of printer output available.  You can switch at any time, but DriveWire will default to the type you choose here.\r\n\r\nThe 'text' mode will produce files containing the raw data that has been sent to the printer.  No interpretation of the contents is performed.  If you send regular text to the printer, the file will be readable with any text editor.\r\n\r\nThe 'FX80' mode will produce image files containing the output of a simulated Epson FX-80 printer.  FX-80 control codes are interpretted.  These files can be viewed with an image viewer.");
		
		Label label = new Label(container, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 120;
		label.setLayoutData(gd_label);
		
		final Button btnDefaultTo = new Button(container, SWT.RADIO);
		btnDefaultTo.setFont(SWTResourceManager.getFont(btnDefaultTo.getFont().getFontData()[0].getName(), btnDefaultTo.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		btnDefaultTo.setImage(null);
		btnDefaultTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					((SimpleWizard) getWizard()).setPrinterTextMode(btnDefaultTo.getSelection());
				
			}
		});
		GridData gd_btnDefaultTo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnDefaultTo.verticalIndent = 10;
		btnDefaultTo.setLayoutData(gd_btnDefaultTo);
		btnDefaultTo.setText(" Default to text mode");
		btnDefaultTo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		
		final Button btnDefaultToFx = new Button(container, SWT.RADIO);
		btnDefaultToFx.setFont(SWTResourceManager.getFont(btnDefaultToFx.getFont().getFontData()[0].getName(), btnDefaultToFx.getFont().getFontData()[0].getHeight(), SWT.BOLD));

		GridData gd_btnDefaultToFx = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_btnDefaultToFx.horizontalIndent = 10;
		gd_btnDefaultToFx.verticalIndent = 10;
		btnDefaultToFx.setLayoutData(gd_btnDefaultToFx);
		btnDefaultToFx.setText(" Default to FX-80 mode");
		btnDefaultToFx.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		btnDefaultToFx.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					((SimpleWizard) getWizard()).setPrinterTextMode(!btnDefaultToFx.getSelection());
				
			}
		});
		
		btnDefaultTo.setSelection(true);
		
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.WRAP);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1);
		gd_lblNewLabel.widthHint = 550;
		gd_lblNewLabel.horizontalIndent = 20;
		gd_lblNewLabel.verticalIndent = 30;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setText("When the printer is flushed, DriveWire will create a file on the server that contains the output.  \r\nPlease choose a directory where you would like these files to be created:");
		
		textDir = new Text(container, SWT.BORDER);
		textDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				((SimpleWizard) getWizard()).setPrinterDir(textDir.getText());
			}
		});
		
		if (((SimpleWizard) getWizard()).getPrinterDir() != null)
			textDir.setText( ((SimpleWizard) getWizard()).getPrinterDir() );
		
		GridData gd_textDir = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_textDir.verticalIndent = 10;
		gd_textDir.horizontalIndent = 20;
		textDir.setLayoutData(gd_textDir);
		
		Button btnDir = new Button(container, SWT.NONE);
		btnDir.setFont(SWTResourceManager.getFont(btnDir.getFont().getFontData()[0].getName(), btnDir.getFont().getFontData()[0].getHeight(), SWT.BOLD));

		btnDir.setImage(SWTResourceManager.getImage(PrinterPage.class, "/menu/folder-yellow_open.png"));
		GridData gd_btnDir = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnDir.verticalIndent = 10;
		btnDir.setLayoutData(gd_btnDir);
		btnDir.setText(" Choose..  ");
		
		btnDir.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				
				String res = MainWin.getFile(false,true,textDir.getText(),"Choose a directory for printer output..", "Open");
					
				if (res != null)
				{
					textDir.setText(res);
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			
				
			}
			
		});
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

}
