package com.groupunix.drivewireserver.uicommands;

import java.io.File;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFilenameException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdServerFileXDir extends DWCommand {

	static final String command = "xdir";

		
		
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
					try {
						text += DWUtils.getFileXDescriptor(f) + "\n";
					} catch (DWFileSystemInvalidFilenameException e) 
					{
						
					}
			}
			
			for (File f : contents)
			{
				if (!f.isDirectory())
					try {
						text += DWUtils.getFileXDescriptor(f) + "\n";
					} catch (DWFileSystemInvalidFilenameException e) {
					}
			}
			
		}
		
		return(new DWCommandResponse(text));
	}



	public String getShortHelp() 
	{
		return "List directory contents (short form)";
	}


	public String getUsage() 
	{
		return "ui server file xdir [path]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}