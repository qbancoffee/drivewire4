package com.groupunix.drivewireserver.dwcommands;

import java.io.IOException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdDiskWrite extends DWCommand {

	private DWProtocolHandler dwProto;
	
	public DWCmdDiskWrite(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	
	public String getCommand() 
	{
		return "write";
	}


	
	public String getShortHelp() 
	{
		return "Write disk image in drive #";
	}

	public String getUsage() 
	{
		return "dw disk write # [path]";
	}


	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error"));
		
		String[] args = cmdline.split(" ");
		
		if (args.length == 1)
		{
			try
			{
				return(doDiskWrite(dwProto.getDiskDrives().getDriveNoFromString(args[0])));
			}
			catch (DWDriveNotValidException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
			}
		}
		else 
		{
			
			try
			{
				return(doDiskWrite(dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline) ));
			}
			catch (DWDriveNotValidException e)
			{
				// its an int, but its not a valid drive
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,"Invalid drive number."));
			}
			
		}
		
	}

	
		
	private DWCommandResponse doDiskWrite(int driveno)
	{
		
		try
		{
			
			dwProto.getDiskDrives().writeDisk(driveno);
					
			return(new DWCommandResponse("Wrote disk #" + driveno + " to source image."));

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
		catch (DWImageHasNoSourceException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILE_NOT_FOUND,e.getMessage()));
			
		}
	}
	
	
	private DWCommandResponse doDiskWrite(int driveno, String path)
	{
		path = DWUtils.convertStarToBang(path);
		
		try
		{
			System.out.println("write " + path);
			
			dwProto.getDiskDrives().writeDisk(driveno,path);
					
			return(new DWCommandResponse("Wrote disk #" + driveno + " to '" + path + "'"));

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
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
