package com.groupunix.drivewireserver.uicommands;

import java.io.File;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdServerFileDir extends DWCommand {

	static final String command = "dir";

		
		
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		File dir = new File(cmdline);
		
		String text = "";
		
		File[] contents = dir.listFiles();
		
		if (contents != null)
		{
			for (File f : contents)
			{
				if (f.isDirectory())
					text += DWUtils.getFileDescriptor(f) + "|false\n";
			}
			
			for (File f : contents)
			{
				if (!f.isDirectory())
					text += DWUtils.getFileDescriptor(f) + "|false\n";
			}
			
		}
		
		return(new DWCommandResponse(text));
	}



	public String getShortHelp() 
	{
		return "List directory contents";
	}


	public String getUsage() 
	{
		return "ui server file dir [path]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}