package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdDiskEject extends DWCommand 
{
	private DWProtocolHandler dwProto;
	
	public DWCmdDiskEject(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}

	public String getCommand() 
	{
		return "eject";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		
		String[] args = cmdline.split(" ");
		
		if (args.length == 1)
		{
		
		
			if (args[0].equals("all"))
			{
				// eject all disks
				return(doDiskEjectAll());
			}
			else
			{
				// eject specified disk
				try
				{
					return(doDiskEject(dwProto.getDiskDrives().getDriveNoFromString(args[0])));
				} 
				catch (DWDriveNotValidException e)
				{
					return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
				}
			}
		}
	
		return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error"));
		
	}

	
	private DWCommandResponse doDiskEjectAll()
	{
		dwProto.getDiskDrives().EjectAllDisks();
		return(new DWCommandResponse("Ejected all disks.\r\n"));
	}
	
	
	private DWCommandResponse doDiskEject(int driveno) 
	{
		try
		{
			dwProto.getDiskDrives().EjectDisk(driveno);
		
			return(new DWCommandResponse("Disk ejected from drive " + driveno + ".\r\n"));
		}
		catch (DWDriveNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		} 
		catch (DWDriveNotLoadedException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED,e.getMessage()));
		} 


		
	}


	public String getShortHelp() 
	{
		return "Eject disk from drive #";
	}

	public String getUsage() 
	{
		return "dw disk eject {# | all}";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
