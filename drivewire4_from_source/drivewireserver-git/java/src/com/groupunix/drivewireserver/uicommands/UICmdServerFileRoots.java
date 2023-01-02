package com.groupunix.drivewireserver.uicommands;

import java.io.File;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdServerFileRoots extends DWCommand {

	static final String command = "roots";

		
	
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		
		File[] roots = File.listRoots();
		
		String text = "";
		
		for (File f : roots)
		{
			text += DWUtils.getFileDescriptor(f) + "|true\n";
		}
		
		return(new DWCommandResponse(text));
	}



	public String getShortHelp() 
	{
		return "List filesystem roots";
	}


	public String getUsage() 
	{
		return "ui server file roots";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}