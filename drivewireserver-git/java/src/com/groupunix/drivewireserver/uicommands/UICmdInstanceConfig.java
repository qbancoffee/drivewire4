package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdInstanceConfig extends DWCommand {

	static final String command = "config";
	
		
	public UICmdInstanceConfig(DWUIClientThread dwuiClientThread)
	{

		commands.addcommand(new UICmdInstanceConfigShow(dwuiClientThread));
		commands.addcommand(new UICmdInstanceConfigSet(dwuiClientThread));
	}

	
	public UICmdInstanceConfig(DWProtocol dwProto) 
	{
		commands.addcommand(new UICmdInstanceConfigShow(dwProto));
		commands.addcommand(new UICmdInstanceConfigSet(dwProto));
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
		return "Configuration commands";
	}


	public String getUsage() 
	{
		return "ui instance config [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}