package com.groupunix.drivewireserver.dwcommands;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWDriveAlreadyLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdDiskCreate extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdDiskCreate(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "create";
	}


	
	public String getShortHelp() 
	{
		return "Create new disk image";
	}


	public String getUsage() 
	{
		return "dw disk create # [path]";
	}

	public DWCommandResponse parse(String cmdline)  
	{
		
		String[] args = cmdline.split(" ");

		if (args.length == 1)
		{
			try
			{
				return(doDiskCreate(dwProto.getDiskDrives().getDriveNoFromString(args[0])));
			} 
			catch (DWDriveNotValidException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
			}

		}
		else if (args.length > 1)
		{
			// create disk
		
			try
			{
				return(doDiskCreate(dwProto.getDiskDrives().getDriveNoFromString(args[0]), DWUtils.dropFirstToken(cmdline)));
			} 
			catch (DWDriveNotValidException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
			}

		}
		
		
		return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error"));
	}

	
	private DWCommandResponse doDiskCreate(int driveno, String filepath)
	{
		FileSystemManager fsManager;
		FileObject fileobj;
		
		try
		{
			
			// create file
			fsManager = VFS.getManager();
			fileobj = fsManager.resolveFile(filepath);
			
			if (fileobj.exists())
			{
				fileobj.close();
				throw new IOException("File already exists");
			}
		
			fileobj.createFile();
			
			if (dwProto.getDiskDrives().isLoaded(driveno))
				dwProto.getDiskDrives().EjectDisk(driveno);
				
			dwProto.getDiskDrives().LoadDiskFromFile(driveno, fileobj.getName().getURI());
					
			return(new DWCommandResponse("New disk image created for drive " + driveno + "."));

		}
		catch (IOException e1)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e1.getMessage()));
				
		} 
		catch (DWDriveNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		} 
		catch (DWDriveAlreadyLoadedException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_ALREADY_LOADED,e.getMessage()));
		} 
		catch (DWDriveNotLoadedException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED,e.getMessage()));
		} 
		catch (DWImageFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_IMAGE_FORMAT_EXCEPTION, e.getMessage()));
		}
		
	}

	
	private DWCommandResponse doDiskCreate(int driveno)
	{
		
		try
		{
			
			if (dwProto.getDiskDrives().isLoaded(driveno))
				dwProto.getDiskDrives().EjectDisk(driveno);
					
			dwProto.getDiskDrives().createDisk(driveno);

		}
		catch (DWDriveNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
		} 
		catch (DWDriveNotLoadedException e) 
		{
			// dont care
		} 
		catch (DWDriveAlreadyLoadedException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_DRIVE_ALREADY_LOADED,e.getMessage()));
		} 

		return(new DWCommandResponse("New image created for drive " + driveno + "."));
	}

	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
	
}
