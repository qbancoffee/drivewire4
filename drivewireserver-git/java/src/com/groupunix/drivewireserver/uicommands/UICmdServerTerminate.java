package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdServerTerminate extends DWCommand {

	static final String command = "terminate";

	@SuppressWarnings("unused")
	private DWUIClientThread dwuiref;

	@SuppressWarnings("unused")
	private DWProtocol dwProto;
		
	public UICmdServerTerminate(DWUIClientThread dwuiClientThread)
	{
		this.dwuiref = dwuiClientThread;
	}

	
	public UICmdServerTerminate(DWProtocol dwProto) 
	{
		this.dwProto = dwProto;
	}


	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		DriveWireServer.shutdown();
		return(new DWCommandResponse("Server shutdown requested."));
	}



	public String getShortHelp() 
	{
		return "Terminate the server";
	}


	public String getUsage() 
	{
		return "ui server terminate";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}