package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstanceDisk extends DWCommand {

	static final String command = "disk";
	
		
	public UICmdInstanceDisk(DWUIClientThread dwuiClientThread)
	{

		commands.addcommand(new UICmdInstanceDiskShow(dwuiClientThread));
		commands.addcommand(new UICmdInstanceDiskDos(dwuiClientThread));
		// commands.addcommand(new UICmdInstanceDiskSerial(dwuiClientThread));
		// commands.addcommand(new UICmdInstanceDiskStatus(dwuiClientThread));
	}

	
	public UICmdInstanceDisk(DWProtocolHandler dwProto) 
	{
		commands.addcommand(new UICmdInstanceDiskShow(dwProto));
		commands.addcommand(new UICmdInstanceDiskDos(dwProto));
		// commands.addcommand(new UICmdInstanceDiskSerial(dwProto));
		// commands.addcommand(new UICmdInstanceDiskStatus(dwProto));
	
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
		return "Instance disk commands";
	}


	public String getUsage() 
	{
		return "ui instance disk [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}