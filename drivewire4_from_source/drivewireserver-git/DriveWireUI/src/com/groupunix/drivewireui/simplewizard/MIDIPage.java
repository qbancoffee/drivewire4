package com.groupunix.drivewireui.simplewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class MIDIPage extends WizardPage
{

	/**
	 * Create the wizard.
	 */
	public MIDIPage()
	{
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(MIDIPage.class, "/wizard/dwlogo_pad64.png"));
		setTitle("Choose Virtual MIDI Support");
		setDescription("Select whether virtual MIDI will be enabled by default.");
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
		container.setLayout(new GridLayout(3, false));
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblMidiBlahBlah = new Label(container, SWT.WRAP);
		GridData gd_lblMidiBlahBlah = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMidiBlahBlah.horizontalIndent = 10;
		gd_lblMidiBlahBlah.widthHint = 240;
		lblMidiBlahBlah.setLayoutData(gd_lblMidiBlahBlah);
		lblMidiBlahBlah.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMidiBlahBlah.setText("DriveWire 4 can provide a virtual MIDI port and software synthesizer to your device.\r\n\r\nHowever, this will increase the server's memory use by several megabytes.  \r\n\r\nIf you are running the server on an embedded device, or need to conserve RAM for other reasons, or simply will not be using MIDI, it is recommended to disable it.");
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		label.setImage(SWTResourceManager.getImage(MIDIPage.class, "/wizard/cocoman_piano.png"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label lblEnableVirtualMidi = new Label(container, SWT.NONE);
		lblEnableVirtualMidi.setFont(SWTResourceManager.getFont(lblEnableVirtualMidi.getFont().getFontData()[0].getName(), lblEnableVirtualMidi.getFont().getFontData()[0].getHeight(), SWT.BOLD));
		
		lblEnableVirtualMidi.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEnableVirtualMidi.setAlignment(SWT.RIGHT);
		lblEnableVirtualMidi.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEnableVirtualMidi.setText("Enable virtual MIDI by default?");
		
		Button btnYes = new Button(container, SWT.RADIO);
		btnYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button)e.widget).getSelection())
				{
					((SimpleWizard) getWizard()).setMIDI(true);
				}
			}
		});
		btnYes.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_btnYes = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnYes.horizontalIndent = 10;
		gd_btnYes.widthHint = 50;
		gd_btnYes.minimumWidth = 50;
		btnYes.setLayoutData(gd_btnYes);
		btnYes.setText("Yes");
		btnYes.setSelection(true);
		
		Button btnNoThanks = new Button(container, SWT.RADIO);
		btnNoThanks.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnNoThanks.setText("No, thanks");
		btnNoThanks.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnNoThanks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button)e.widget).getSelection())
				{
					((SimpleWizard) getWizard()).setMIDI(false);
				}
			}
		});
		
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

}
