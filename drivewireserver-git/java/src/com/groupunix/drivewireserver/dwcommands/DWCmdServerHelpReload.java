package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdServerHelpReload extends DWCommand {

	 
	private DWProtocol dwProto;




	public DWCmdServerHelpReload(DWProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
	}


	public String getCommand() 
	{
		return "reload";
	}

	
	public String getShortHelp() 
	{
		return "Reload help topics";
	}


	public String getUsage() 
	{
		return "dw help reload";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		return(doHelpReload(cmdline));
	}

	
	

	private DWCommandResponse doHelpReload(String cmdline) 
	{
		((DWProtocolHandler)dwProto).getHelp().reload();
		
		return(new DWCommandResponse("Reloaded help topics."));
	}




	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
