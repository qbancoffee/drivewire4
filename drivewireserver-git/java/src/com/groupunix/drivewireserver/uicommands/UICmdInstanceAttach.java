package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdInstanceAttach extends DWCommand {

	private DWUIClientThread clientref;
	
	public UICmdInstanceAttach(DWUIClientThread dwuiClientThread) 
	{
		clientref = dwuiClientThread;
	}

	

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "attach";
	}


	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "attach to instance #";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui instance attach #";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		try
		{
			int handler = Integer.parseInt(cmdline);
			
			if (DriveWireServer.isValidHandlerNo(handler))
			{
				// set this connection's instance
				clientref.setInstance(handler);
				return(new DWCommandResponse("Attached to instance " + handler));
				
			}
			else
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_HANDLER,"Invalid handler number"));
			}
		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric instance #"));
		}
		

	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
