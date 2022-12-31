package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstanceTimerReset extends DWCommand {

	private DWProtocol dwProto = null;
	private DWUIClientThread  uiref;
	

	public UICmdInstanceTimerReset(DWProtocol dwProtocol) 
	{
		this.dwProto = dwProtocol;
	}

	public UICmdInstanceTimerReset(DWUIClientThread dwuiClientThread)
	{
		this.uiref = dwuiClientThread;
	}

	@Override
	public String getCommand() 
	{
		return "reset";
	}

	@Override
	public String getShortHelp() {
		return "reset instance timer";
	}

	@Override
	public String getUsage() {
		return "ui instance timer reset [#]";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR , "Must specify timer #"));
		}	
		else
		{
			String[] args = cmdline.split(" ");
			
			// TODO ASSumes we are using DW protocol
			if (this.dwProto == null)
				dwProto = (DWProtocolHandler) DriveWireServer.getHandler(this.uiref.getInstance());
			
			
			try
			{
				byte tno = (byte) Integer.parseInt(args[0]);
				this.dwProto.getTimers().resetTimer(tno);	
				
				return(new DWCommandResponse("Reset timer " + (tno & 0xff)));
			}
			catch (NumberFormatException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR , "Timer # must be 0-255"));
			}
			
			
		}
		
	
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
