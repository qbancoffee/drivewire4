package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWCmdPortClose extends DWCommand {

	private DWVSerialProtocol dwProto;

	public DWCmdPortClose(DWVSerialProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
	}
	
	public String getCommand() 
	{
		return "close";
	}

	public String getShortHelp() 
	{
		return "Close port #";
	}


	public String getUsage() 
	{
		return "dw port close #";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw port close requires a port # as an argument"));
		}
		return(doPortClose(cmdline));
	}

	
	private DWCommandResponse doPortClose(String port)
	{

		
		try
		{
			int portno = Integer.parseInt(port);
			
			dwProto.getVPorts().closePort(portno);
			
			return(new DWCommandResponse("Port #"+ portno + " closed."));

		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric port #"));
		} 
		catch (DWPortNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_PORT,e.getMessage()));
		} 
		
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}

}
