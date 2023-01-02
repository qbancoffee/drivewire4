package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdServerShowHelp extends DWCommand {


	private DWProtocol dwProto;
	private DWUIClientThread dwuiClientThread;

	public UICmdServerShowHelp(DWUIClientThread dwuiClientThread) 
	{
		this.dwuiClientThread = dwuiClientThread;
	}


	public UICmdServerShowHelp(DWProtocol dwProto) 
	{
		this.dwProto = dwProto;
	}


	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "help";
	}


	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show help";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show help topic";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		if (this.dwProto == null)
			this.dwProto = DriveWireServer.getHandler(dwuiClientThread.getInstance());

		
		if (dwProto.getHelp() == null)
			return(new DWCommandResponse(false, DWDefs.RC_HELP_TOPIC_NOT_FOUND, "No help available"));
		
		try
		{
			return(new DWCommandResponse(dwProto.getHelp().getTopicText(cmdline)));
		} 
		catch (DWHelpTopicNotFoundException e)
		{
			return(new DWCommandResponse(false, DWDefs.RC_HELP_TOPIC_NOT_FOUND ,e.getLocalizedMessage()));
		}
		
		
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
