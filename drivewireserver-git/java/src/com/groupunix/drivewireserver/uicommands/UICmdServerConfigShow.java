package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerConfigShow extends DWCommand {

	static final String command = "show";
	

	public String getCommand() 
	{
		return command;
	}

	@SuppressWarnings("unchecked")
	public DWCommandResponse parse(String cmdline)
	{
		String res = new String();
		
		
		if (cmdline.length() == 0)
		{
			for (Iterator<String> i = DriveWireServer.serverconfig.getKeys(); i.hasNext();)
			{
				String key = i.next();
				String value = StringUtils.join(DriveWireServer.serverconfig.getStringArray(key), ", ");
		
				res += key + " = " + value + "\r\n";
		            
			}
		}
		else
		{
			if (DriveWireServer.serverconfig.containsKey(cmdline))
			{
				String value = StringUtils.join(DriveWireServer.serverconfig.getStringArray(cmdline), ", ");
				return(new DWCommandResponse(value));
			}
			else
			{
				return(new DWCommandResponse(false,DWDefs.RC_CONFIG_KEY_NOT_SET, "Key '" + cmdline + "' is not set."));
			}
		}
		
		return(new DWCommandResponse(res));
	}


	public String getShortHelp() 
	{
		return "Show server configuration";
	}


	public String getUsage() 
	{
		return "ui server config show [item]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}