package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdServer extends DWCommand {

	static final String command = "server";
		
	public UICmdServer(DWUIClientThread dwuiClientThread)
	{
		commands.addcommand(new UICmdServerShow(dwuiClientThread));
		commands.addcommand(new UICmdServerConfig(dwuiClientThread));
		commands.addcommand(new UICmdServerTerminate(dwuiClientThread));
		commands.addcommand(new UICmdServerFile());
	}

		
	
	
	public UICmdServer(DWProtocol dwProto) 
	{
		commands.addcommand(new UICmdServerShow(dwProto));
		commands.addcommand(new UICmdServerConfig(dwProto));
		commands.addcommand(new UICmdServerTerminate(dwProto));
		commands.addcommand(new UICmdServerFile());
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
		return "Server commands";
	}


	public String getUsage() 
	{
		return "ui server [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}