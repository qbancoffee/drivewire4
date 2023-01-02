package com.groupunix.drivewireserver.uicommands;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstanceDiskDosDir extends DWCommand {

	static final String command = "dir";
	
	private DWUIClientThread uiref = null;

	private DWProtocolHandler dwProto = null;

	public UICmdInstanceDiskDosDir(DWUIClientThread dwuiClientThread) 
	{

		this.uiref = dwuiClientThread;
	}

	public UICmdInstanceDiskDosDir(DWProtocolHandler dwProto) 
	{
		this.dwProto = dwProto;
	}

	public String getCommand() 
	{
		return command;
	}

	@SuppressWarnings("unchecked")
	public DWCommandResponse parse(String cmdline)
	{
		String res = new String();
		
		// TODO hackish!
		if (this.dwProto == null)
		{
			if (DriveWireServer.getHandler(this.uiref.getInstance()).hasDisks())
				dwProto = (DWProtocolHandler) DriveWireServer.getHandler(this.uiref.getInstance());
			else
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_WONT ,"This operation is not supported on this type of instance"));
		}
			
		
		
		if (cmdline.length() == 1)
		{
			if (dwProto.getDiskDrives() == null)
			{
				return(new DWCommandResponse(false,DWDefs.RC_NO_SUCH_DISKSET, "Disk drives are null, is server restarting?"));
			}
			
			// disk details
			try
			{
			
				int driveno = Integer.parseInt(cmdline);
				
				DWDECBFileSystem fs = new DWDECBFileSystem(dwProto.getDiskDrives().getDisk(driveno));
				
				for (DWFileSystemDirEntry e : fs.getDirectory(null))
				{
					if (((DWDECBFileSystemDirEntry) e).isUsed())
						res += ((DWDECBFileSystemDirEntry) e).toString() + "\n";
				}
				
			}
			catch (NumberFormatException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Non numeric drive number"));
			}
			catch (DWDriveNotLoadedException e) 
			{
				return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED, e.getMessage()));
			} 
			catch (DWDriveNotValidException e) 
			{
				return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE, e.getMessage()));
			} 
			catch (IOException e) 
			{
				return(new DWCommandResponse(false,DWDefs.RC_FAIL, e.getMessage()));
			} 
			catch (DWFileSystemInvalidDirectoryException e) 
			{
				return(new DWCommandResponse(false,DWDefs.RC_IMAGE_FORMAT_EXCEPTION, e.getMessage()));
			}
			
		}
		
		return(new DWCommandResponse(res));
	}



	public String getShortHelp() 
	{
		return "Show DOS disk directory";
	}


	public String getUsage() 
	{
		return "ui instance disk dos dir";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}