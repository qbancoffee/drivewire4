package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerConfigSerial extends DWCommand {

	static final String command = "serial";
	

	public String getCommand() 
	{
		return command;
	}


	public DWCommandResponse parse(String cmdline)
	{
		String res = new String();

		res = DriveWireServer.configserial + "";
		
		return(new DWCommandResponse(res));
	}


	public String getShortHelp() 
	{
		return "Show server config serial#";
	}


	public String getUsage() 
	{
		return "ui server config serial";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}