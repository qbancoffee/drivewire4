package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdDiskSet extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdDiskSet(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "set";
	}


	
	public String getShortHelp() 
	{
		return "Set disk parameters";
	}


	public String getUsage() 
	{
		return "dw disk set # param val";
	}

	public DWCommandResponse parse(String cmdline)  
	{
		String[] args = cmdline.split(" ");
		
		if (args.length < 3)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw disk set requires 3 arguments."));
		}
		
		try
		{
			return(doDiskSet(dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline)));
		} 
		catch (DWDriveNotValidException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		}

	}

	
	private DWCommandResponse doDiskSet(int driveno, String cmdline) 
	{
		// driveno + param/val
		
		String[] parts = cmdline.split(" ");
		
		// set item
			
		try
		{
			if (dwProto.getDiskDrives().getDisk(driveno).getParams().containsKey(parts[0]))
			{
				dwProto.getDiskDrives().getDisk(driveno).getParams().setProperty(parts[0], DWUtils.dropFirstToken(cmdline));
				return(new DWCommandResponse("Param '" + parts[0] + "' set for disk " + driveno + "."));
			}
			else
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DISK_DEF,"No parameter '" + parts[0] + "' available for disk " + driveno + "."));
			}
		} 
		catch (DWDriveNotLoadedException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED,e.getMessage()));
		} 
		catch (DWDriveNotValidException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		}
		
				
		
	
	}

	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}
