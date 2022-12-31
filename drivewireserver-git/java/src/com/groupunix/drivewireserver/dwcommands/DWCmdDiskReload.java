package com.groupunix.drivewireserver.dwcommands;

import java.io.IOException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdDiskReload extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdDiskReload(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "reload";
	}


	
	public String getShortHelp() 
	{
		return "Reload disk in drive #";
	}


	public String getUsage() 
	{
		return "dw disk reload {# | all}";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw disk reload requires a drive # or 'all' as an argument"));
		}
		
		return(doDiskReload(cmdline));
	}

	
	private DWCommandResponse doDiskReload(String drivestr)
	{
		
		try
		{
			if (drivestr.equals("all"))
			{
				dwProto.getDiskDrives().ReLoadAllDisks();
				
				return(new DWCommandResponse("All disks reloaded."));
			}
			else
			{
				
				dwProto.getDiskDrives().ReLoadDisk(dwProto.getDiskDrives().getDriveNoFromString(drivestr));
	
				return(new DWCommandResponse("Disk in drive #"+ drivestr + " reloaded."));
			}
		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric drive #"));
		} 
		catch (IOException e1)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e1.getMessage()));
		} 
		catch (DWDriveNotLoadedException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED,e.getMessage()));
		} 
		catch (DWDriveNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		} 
		catch (DWImageFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_IMAGE_FORMAT_EXCEPTION,e.getMessage()));
		}
		
		
	}
	
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
