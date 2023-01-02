package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdTest extends DWCommand {

	static final String command = "test";

		
	public UICmdTest(DWUIClientThread dwuiClientThread)
	{
		commands.addcommand(new UICmdTestDGraph(dwuiClientThread));
	}

	
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		return(commands.parse(cmdline));
	}


	public String getShortHelp() 
	{
		return "Test commands";
	}


	public String getUsage() 
	{
		return "ui test [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}