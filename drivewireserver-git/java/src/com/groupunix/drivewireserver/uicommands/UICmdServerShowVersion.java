package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowVersion extends DWCommand {

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "version";
	}

	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show server version";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show version";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String txt = new String();
		
		txt = "DriveWire version " + DriveWireServer.DWVersion + " (" + DriveWireServer.DWVersion.getDate() + ")";
		
		return(new DWCommandResponse(txt));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
