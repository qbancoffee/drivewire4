package com.groupunix.drivewireui.updatewizard;

import org.eclipse.jface.wizard.Wizard;

import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.Updater;



public class UpdateWizard extends Wizard {

	private IntroPage introPage;
	private Updater updater;
	private UpdatePage updatePage;

	public UpdateWizard(Updater up) 
	{
		setWindowTitle("Update Wizard");
		this.updater = up;
	}

	@Override
	public void addPages() 
	{
		this.introPage = new IntroPage(this.updater);
		this.updatePage = new UpdatePage(this.updater);
		
		addPage(introPage);
		addPage(updatePage);
	
	}

	@Override
	public boolean performFinish() 
	{
		if (this.updater.isUserIgnore())
		{
			MainWin.config.addProperty("IgnoreUpdateVersion", this.updater.getLatestVersion().toString());
			return true;
		}
		
		if (this.updater.getLatestVersion().isRestart())
		{
			MainWin.config.setProperty("UpdateRestartRequired", true);
			RestartDialog rd = new RestartDialog(this.getShell());
			rd.open();
		}
		
		return true;
	}

	
	@Override
	public boolean canFinish()
	{
		if (this.updater.isUserIgnore() || this.updater.isUpdateComplete())
			return true;
		
		return false;
	}


	
}
