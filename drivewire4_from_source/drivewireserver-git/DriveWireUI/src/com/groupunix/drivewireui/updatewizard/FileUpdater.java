package com.groupunix.drivewireui.updatewizard;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.groupunix.drivewireui.MainWin;
import com.groupunix.drivewireui.Updater;


public class FileUpdater implements Runnable 
{


	private String filename;
	private String targetsha;
	private UpdatePage updatePage;

	public FileUpdater(String fn, String sha, UpdatePage updatePage) 
	{
		this.filename = fn;
		this.setTargetsha(sha);
		this.updatePage = updatePage;
	}

	@Override
	public void run() 
	{
	
		this.updatePage.setThreadStatus("Updating " + filename);
		
		try 
		{
			URL url = new URL(MainWin.config.getString("UpdateURL", Updater.UPDATEURL) + "/" + this.updatePage.getVersion().toString() + "/" + filename + ".gz");
			
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		    FileOutputStream fos = new FileOutputStream(filename +".gzt");
		    fos.getChannel().transferFrom(rbc, 0, 1 << 25);
			fos.close();
			
		} 
		catch (Exception e) 
		{
			this.updatePage.addErrorStatus(e.getClass().getSimpleName() + System.getProperty("line.separator") + e.getMessage());
		} 
		
		
		this.updatePage.removeThreadStatus();
		
	}

	public String getTargetsha() {
		return targetsha;
	}

	public void setTargetsha(String targetsha) {
		this.targetsha = targetsha;
	}

}
