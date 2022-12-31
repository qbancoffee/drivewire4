package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerConfigFreeze extends DWCommand {

	static final String command = "freeze";
	

	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{

		
		String[] args = cmdline.split(" ");
		
		if (args.length == 1)
		{
			return(doSetFreeze(args[0]));
		}
		else
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR , "Must specify freeze state"));
			
		}
		
	}

	public String getShortHelp() 
	{
		return "Set server configuration item";
	}


	public String getUsage() 
	{
		return "ui server config freeze [boolean]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
	
	private DWCommandResponse doSetFreeze(String state)
	{
		if (state.equalsIgnoreCase("true"))
		{
			DriveWireServer.setConfigFreeze(true);
			return(new DWCommandResponse("Config freeze set."));
		}
		else
		{
			DriveWireServer.setConfigFreeze(false);
			return(new DWCommandResponse("Config freeze unset."));
		}
		
		
		
		
	}
	
		
	
}