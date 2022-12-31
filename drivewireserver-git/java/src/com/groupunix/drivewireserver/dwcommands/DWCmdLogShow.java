package com.groupunix.drivewireserver.dwcommands;

import java.util.ArrayList;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;

public class DWCmdLogShow extends DWCommand {

	
	public DWCmdLogShow(DWCommand parent)
	{
		setParentCmd(parent);
	}
	
	public String getCommand() 
	{
		return "show";
	}

	
	public String getShortHelp() 
	{
		return "Show last 20 (or #) log entries";
	}


	public String getUsage() 
	{
		return "dw log show [#]";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(doShowLog("20"));
		}
		return(doShowLog(cmdline));
	}

	
	private DWCommandResponse doShowLog(String strlines)
	{
		String text = new String();
		
		try
		{
			int lines = Integer.parseInt(strlines);
			
			text += "\r\nDriveWire Server Log (" + DriveWireServer.getLogEventsSize() + " events in buffer):\r\n\n";
			
			ArrayList<String> loglines = DriveWireServer.getLogEvents(lines);
			
			for (int i = 0;i<loglines.size();i++)
			{
				text += loglines.get(i);
				
			}
	
			return(new DWCommandResponse(text));

		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric # of log lines"));
		} 
		
	}
	

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
