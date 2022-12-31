package com.groupunix.drivewireserver.dwcommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystem;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystemDirEntry;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWFileSystemDirEntry;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidDirectoryException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdDiskDosDir extends DWCommand 
{
	private DWProtocolHandler dwProto;
	
	public DWCmdDiskDosDir(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}

	public String getCommand() 
	{
		return "dir";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		
		String[] args = cmdline.split(" ");
		
		if (args.length == 1)
		{
				try
				{
					return(doDiskDosDir(dwProto.getDiskDrives().getDriveNoFromString(args[0])));
				} 
				catch (DWDriveNotValidException e)
				{
					return(new DWCommandResponse(false,DWDefs.RC_INVALID_DRIVE,e.getMessage()));
				} 
				catch (DWDriveNotLoadedException e)
				{
					return(new DWCommandResponse(false,DWDefs.RC_DRIVE_NOT_LOADED,e.getMessage()));
				} 
				catch (IOException e)
				{
					return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e.getMessage()));
				} 
				catch (DWFileSystemInvalidDirectoryException e)
				{
					return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION,e.getMessage()));
				}
			
		}
	
		return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error"));
	}
		
		
	private DWCommandResponse doDiskDosDir(int driveno) throws DWDriveNotLoadedException, DWDriveNotValidException, IOException, DWFileSystemInvalidDirectoryException
	{
		String res = "Directory of drive " + driveno + "\r\n\r\n";
		
		DWDECBFileSystem tmp = new DWDECBFileSystem(dwProto.getDiskDrives().getDisk(driveno));
		
		//res += tmp.dumpFat() + "\r\n";
		
		
		ArrayList<String> dir = new ArrayList<String>();
		
		for (DWFileSystemDirEntry e : tmp.getDirectory(null))
		{
			if (((DWDECBFileSystemDirEntry) e).isUsed())
				dir.add(e.getFileName() + "." + e.getFileExt());  //  + " " + e.getFirstGranule() + " " + e.getBytesInLastSector() + " " + e.getFileFlag() + " " + e.getFileType()
		}
		
		Collections.sort(dir);
		
		res += DWCommandList.colLayout(dir, this.dwProto.getCMDCols());
		
		
		
		return(new DWCommandResponse(res));
		
	}




	public String getShortHelp() 
	{
		return "Show DOS directory of disk in drive #";
	}

	public String getUsage() 
	{
		return "dw disk dos dir #";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
