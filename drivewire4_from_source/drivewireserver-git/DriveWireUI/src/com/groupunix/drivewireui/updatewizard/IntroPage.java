package com.groupunix.drivewireui.updatewizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.Updater;

public class IntroPage extends WizardPage {

	private Updater updater;
	private Text textDescription;

	/**
	 * Create the wizard.
	 */
	public IntroPage(Updater updater) {
		super("wizardPage");
		setImageDescriptor(ResourceManager.getImageDescriptor(IntroPage.class, "/wizard/dwlogo_pad64.png"));
		setTitle("DriveWire version " + updater.getLatestVersion().toString() + " is available");
		setDescription("An updated version of DriveWire is available.");
		this.updater = updater;
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		setControl(container);
		container.setLayout(new GridLayout(7, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		label_2.setText(" ");
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label lblYourCurrentVersion = new Label(container, SWT.NONE);
		lblYourCurrentVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblYourCurrentVersion.setText("Your current version:");
		lblYourCurrentVersion.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label labelCurVer = new Label(container, SWT.NONE);
		labelCurVer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label label = new Label(container, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 40;
		label.setLayoutData(gd_label);
		label.setText(" ");
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label lblReleasedOn = new Label(container, SWT.NONE);
		lblReleasedOn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReleasedOn.setText("Released on:");
		lblReleasedOn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label labelCurDate = new Label(container, SWT.NONE);
		labelCurDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label labelIcon = new Label(container, SWT.NONE);
		labelIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		labelIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		labelIcon.setImage(SWTResourceManager.getImage(IntroPage.class, "/status/dialog-information-3.png"));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblAvailableVersion = new Label(container, SWT.NONE);
		lblAvailableVersion.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAvailableVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAvailableVersion.setText("Available version:");
		
		Label labelNewVer = new Label(container, SWT.NONE);
		labelNewVer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		Label lblReleasedOn_1 = new Label(container, SWT.NONE);
		lblReleasedOn_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReleasedOn_1.setText("Released on:");
		lblReleasedOn_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label labelNewDate = new Label(container, SWT.NONE);
		labelNewDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		
		new Label(container, SWT.NONE);
		
		Label lblAppliesTo = new Label(container, SWT.NONE);
		lblAppliesTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAppliesTo.setText("Recommended for:");
		lblAppliesTo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		Label lblTarget = new Label(container, SWT.NONE);
		lblTarget.setText(" ");
		lblTarget.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTarget.setText(this.updater.getLatestVersion().getTarget());
		
		new Label(container, SWT.NONE);
		
		Label lblPriority = new Label(container, SWT.NONE);
		lblPriority.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPriority.setText("Priority:");
		lblPriority.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Label labelPriority = new Label(container, SWT.NONE);
		labelPriority.setText(" ");
		labelPriority.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		labelPriority.setText(this.updater.getLatestVersion().getPriority());
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblReleaseNotes = new Label(container, SWT.NONE);
		lblReleaseNotes.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReleaseNotes.setText("Release notes:");
		lblReleaseNotes.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		textDescription = new Text(container, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 2));
		textDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		new Label(container, SWT.NONE);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		label_1.setText(" ");
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		
		
		
		
		labelCurVer.setText(MainWin.DWUIVersion.toString());
		labelCurDate.setText(MainWin.DWUIVersion.getDate());
		
		labelNewVer.setText(this.updater.getLatestVersion().toString());
		labelNewDate.setText(this.updater.getLatestVersion().getDate());
		textDescription.setText(this.updater.getLatestVersion().getDescription());
		new Label(container, SWT.NONE);
		
		Label label_4 = new Label(container, SWT.NONE);
		label_4.setText(" ");
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label label_6 = new Label(container, SWT.NONE);
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblWhatShallWe = new Label(container, SWT.NONE);
		lblWhatShallWe.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblWhatShallWe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWhatShallWe.setText("What shall we do?");
		lblWhatShallWe.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		new Label(container, SWT.NONE);
		
		Button btnInstallThisUpdate = new Button(container, SWT.RADIO);
		btnInstallThisUpdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnInstallThisUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updater.setUserIgnore(false);
				setPageComplete(true);
			}
		});
		btnInstallThisUpdate.setSelection(true);
		btnInstallThisUpdate.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		btnInstallThisUpdate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		btnInstallThisUpdate.setText("Install this update");
		
		Button btnIgnoreThisUpdate = new Button(container, SWT.RADIO);
		btnIgnoreThisUpdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btnIgnoreThisUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updater.setUserIgnore(true);
				setPageComplete(true);
			}
		});
		btnIgnoreThisUpdate.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		btnIgnoreThisUpdate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		btnIgnoreThisUpdate.setText("Ignore this update");
		new Label(container, SWT.NONE);
		
		Label label_5 = new Label(container, SWT.NONE);
		label_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
	}

	
	
	
}
