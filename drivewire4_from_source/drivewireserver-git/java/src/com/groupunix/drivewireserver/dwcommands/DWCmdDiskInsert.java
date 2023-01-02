package com.groupunix.drivewireserver.dwcommands;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.vfs.FileSystemException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveAlreadyLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdDiskInsert extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdDiskInsert(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "insert";
	}


	public DWCommandResponse parse(String cmdline) 
	{
		String[] args = cmdline.split(" ");
		
		if (args.length > 1)
		{
			// insert disk
			try
			{
				return(doDiskInsert(dwProto.getDiskDrives().getDriveNoFromString(args[0]),   DWUtils.dropFirstToken(cmdline)));
			} 
			catch (DWDriveNotValidException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
			}
		}
		
		return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error"));
	
	}

	
	
	private DWCommandResponse doDiskInsert(int driveno, String path) 
	{
		
		// hack for os9 vs URLs
		path = DWUtils.convertStarToBang(path);
		
		try
		{
			
			// load new disk
			
			dwProto.getDiskDrives().LoadDiskFromFile(driveno, path);
			
			return(new DWCommandResponse("Disk inserted in drive " + driveno + "."));

		}
		catch (DWDriveNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
			
		} 
		catch (FileSystemException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION,e.getMessage()));
			
		} 
		catch (DWDriveAlreadyLoadedException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_ALREADY_LOADED,e.getMessage()));
		} 
		catch (FileNotFoundException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILE_NOT_FOUND,e.getMessage()));
		} 
		catch (IOException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e.getMessage()));
		} 
		catch (DWImageFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_IMAGE_FORMAT_EXCEPTION,e.getMessage()));
		}
		

		
	}



	public String getShortHelp() 
	{
		return "Load disk into drive #";
	}


	public String getUsage() 
	{
		return "dw disk insert # path";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}
