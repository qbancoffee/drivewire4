package com.groupunix.drivewireserver.dwcommands;

import java.util.ArrayList;
import java.util.Iterator;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdServerHelpShow extends DWCommand {

	
	private DWProtocol dwProto;


	public DWCmdServerHelpShow(DWProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
	}


	public String getCommand() 
	{
		return "show";
	}

	
	public String getShortHelp() 
	{
		return "Show help topic";
	}


	public String getUsage() 
	{
		return "dw help show [topic]";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(doShowHelp());
		}
		return(doShowHelp(cmdline));
	}

	
	

	private DWCommandResponse doShowHelp(String cmdline) 
	{
		String text = "Help for " + cmdline + ":\r\n\r\n";
		
		try 
		{
			text += ((DWProtocolHandler)dwProto).getHelp().getTopicText(cmdline);
			return(new DWCommandResponse(text));
		} 
		catch (DWHelpTopicNotFoundException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_CONFIG_KEY_NOT_SET, e.getMessage()));
		}
		
	}


	private DWCommandResponse doShowHelp() 
	{
		String text = new String();
		
		text = "Help Topics:\r\n\r\n";
		
		ArrayList<String> tops = ((DWProtocolHandler)dwProto).getHelp().getTopics(null);
		
		Iterator<String> t = tops.iterator();
		while (t.hasNext())
        {
			text += t.next() + "\r\n";
        }
		return(new DWCommandResponse(text));
	}


	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
