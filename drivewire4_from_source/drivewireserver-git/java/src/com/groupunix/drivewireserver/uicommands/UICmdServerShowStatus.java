package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowStatus extends DWCommand {

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "status";
	}


	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show server status";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show status";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
	
		text += "version|" + DriveWireServer.DWVersion + "\n"; 
		text += "versiondate|" + DriveWireServer.DWVersion.getDate() + "\n";
		
		text += "totmem|" + Runtime.getRuntime().totalMemory() / 1024 + "\n";
	    text += "freemem|" + Runtime.getRuntime().freeMemory() / 1024 + "\n";
	    
	    text += "instances|" + DriveWireServer.getNumHandlers() + "\n";
	    text += "configpath|" + DriveWireServer.serverconfig.getBasePath() + "\n";
		
		return(new DWCommandResponse(text));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
