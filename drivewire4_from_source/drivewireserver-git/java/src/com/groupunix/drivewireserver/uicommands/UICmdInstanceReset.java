package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdInstanceReset extends DWCommand {

	static final String command = "reset";
	
		
	public UICmdInstanceReset(DWUIClientThread dwuiClientThread)
	{

		commands.addcommand(new UICmdInstanceResetProtodev(dwuiClientThread));

	}

	
	public UICmdInstanceReset(DWProtocol dwProto) 
	{
		commands.addcommand(new UICmdInstanceResetProtodev(dwProto));

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
		return "Restart commands";
	}


	public String getUsage() 
	{
		return "ui instance reset [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}