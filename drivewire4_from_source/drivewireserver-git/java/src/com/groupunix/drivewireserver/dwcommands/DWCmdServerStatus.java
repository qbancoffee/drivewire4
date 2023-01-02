package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdServerStatus extends DWCommand {

	
	
	public DWCmdServerStatus(DWProtocol dwProto,DWCommand parent)
	{
		setParentCmd(parent);
	
	}
	
	public String getCommand() 
	{
		return "status";
	}


	
	public String getShortHelp() 
	{
		return "Show server status information";
	}


	public String getUsage() 
	{
		return "dw server status";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		return(doServerStatus());
	}
	
	private DWCommandResponse doServerStatus()
	{
		String text = new String();
		
		text += "DriveWire " + DriveWireServer.DWVersion + " status:\r\n\n";
		
		text += "Total memory:  " + Runtime.getRuntime().totalMemory() / 1024 + " KB";
	    text += "\r\nFree memory:   " + Runtime.getRuntime().freeMemory() / 1024 + " KB";
	    text += "\r\n";
	    
		return(new DWCommandResponse(text));
		
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}

}
