package com.groupunix.drivewireserver.dwcommands;




import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdInstanceStop extends DWCommand {



	public DWCmdInstanceStop(DWProtocol dwProto2,DWCommand parent)
	{
		setParentCmd(parent);

	}
	
	public String getCommand() 
	{
		return "stop";
	}

	
	public String getShortHelp() 
	{
		return "Stop instance #";
	}


	public String getUsage() 
	{
		return "dw instance stop #";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: dw instance stop requires an instance # as an argument"));
		}
		
		return(doStart(cmdline));
	}

	
	private DWCommandResponse doStart(String instr) 
	{
		
		
		try
		{
			int intno = Integer.parseInt(instr);
		
			if (!DriveWireServer.isValidHandlerNo(intno))
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_HANDLER, "Invalid instance number."));
			
			if (DriveWireServer.getHandler(intno) == null)
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_HANDLER, "Instance " + intno + " is not defined."));
			
			if (!DriveWireServer.getHandler(intno).isReady())
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_ALREADY_STARTED, "Instance " + intno + " is not started."));
			
			if (DriveWireServer.getHandler(intno).isDying())
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_NOT_READY, "Instance " + intno + " is in the process of shutting down."));
			
			DriveWireServer.stopHandler(intno);
			
			return(new DWCommandResponse("Stopping instance # " + intno));
		
		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR, "dw instance stop requires a numeric instance # as an argument"));
				
		} 
		
	}
	
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
