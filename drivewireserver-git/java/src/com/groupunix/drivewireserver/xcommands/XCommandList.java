package com.groupunix.drivewireserver.xcommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;


public class XCommandList {
	
	private List<XCommand> commands = new ArrayList<XCommand>();
	
	
	public XCommandList(DWProtocol dwProtocol)
	{
		if (dwProtocol != null)
		{
			// commands requiring an instance
			
		}
		
		commands.add(new XCmdServerMemory());
		commands.add(new XCmdServerVersion());
		
	}
	
	
	public List<XCommand> getCommands() 
	{
		return(this.commands);
	}
	
	
	public XCommandResponse parse(String cmdline) 
	{
		XCommand xcmd = getCommandMatch(cmdline);
		
		if (xcmd == null)
		{
			return(new XCommandResponse(DWDefs.RC_SYNTAX_ERROR));
		}
		else 
		{
			return(xcmd.parse(cmdline));
		}
		
	}

	

	
	private XCommand getCommandMatch(String arg) 
	{
		XCommand cmd;
		
		for (Iterator<XCommand> it = this.commands.iterator(); it.hasNext(); )
		{
			cmd = it.next();
			
			if (cmd.getCommand().startsWith(arg.toLowerCase()))
			{
				return(cmd);
			}
		}
		
		return null;
	}

	public boolean validate(String cmd) 
	{
		if (getCommandMatch(cmd) != null)
			return true;
		
		return false;
	}


}
