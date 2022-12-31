package com.groupunix.drivewireserver.uicommands;

import java.io.File;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdServerFileDefaultDir extends DWCommand {

	static final String command = "defaultdir";

		
	
	
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		return(new DWCommandResponse( DWUtils.getFileDescriptor(new File(DriveWireServer.serverconfig.getString("LocalDiskDir","."))) + "|false" ));
	}



	public String getShortHelp() 
	{
		return "Show default dir dir";
	}


	public String getUsage() 
	{
		return "ui server file defaultdir";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}