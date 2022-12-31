package com.groupunix.drivewireserver.uicommands;

import java.io.File;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdServerFileInfo extends DWCommand {

	static final String command = "info";



	
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		return(new DWCommandResponse( DWUtils.getFileDescriptor(new File(cmdline)) + "|false" ));
	}



	public String getShortHelp() 
	{
		return "Show file details";
	}


	public String getUsage() 
	{
		return "ui server file info";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}