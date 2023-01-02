package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;

public class DWCmdServerTerminate extends DWCommand {

	
	public DWCmdServerTerminate(DWCommand parent)
	{
		setParentCmd(parent);
	}
	
	
	public String getCommand() 
	{
		return "terminate";
	}


	public String getShortHelp() 
	{
		return "Shut down server";
	}


	public String getUsage() 
	{
		return "dw server terminate [force]";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.equals("force"))
			System.exit(1);
		
		DriveWireServer.shutdown();
		
		return(new DWCommandResponse("Server shutdown requested."));
	}



	public boolean validate(String cmdline) {
		return true;
	}
	
	
}
