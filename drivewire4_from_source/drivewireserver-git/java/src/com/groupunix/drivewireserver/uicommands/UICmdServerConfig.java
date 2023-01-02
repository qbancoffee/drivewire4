package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdServerConfig extends DWCommand {

	static final String command = "config";

		
	public UICmdServerConfig(DWUIClientThread dwuiClientThread)
	{

		commands.addcommand(new UICmdServerConfigShow());
		commands.addcommand(new UICmdServerConfigSet());
		commands.addcommand(new UICmdServerConfigSerial());
		commands.addcommand(new UICmdServerConfigWrite());
		commands.addcommand(new UICmdServerConfigFreeze());
	}

	
	public UICmdServerConfig(DWProtocol dwProto) 
	{
		commands.addcommand(new UICmdServerConfigShow());
		commands.addcommand(new UICmdServerConfigSet());
		commands.addcommand(new UICmdServerConfigWrite());
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
		return "ui server config [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}