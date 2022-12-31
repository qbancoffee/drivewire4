package com.groupunix.drivewireserver.uicommands;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowLocalDisks extends DWCommand {

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "localdisks";
	}



	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show server local disks";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show localdisks";
	}

	
	@Override
	public DWCommandResponse parse(String cmdline) 
	{

		String res = new String();
		
		try 
	    {
			if (!DriveWireServer.serverconfig.containsKey("LocalDiskDir"))
				return(new DWCommandResponse(false,DWDefs.RC_CONFIG_KEY_NOT_SET,"LocalDiskDir must be defined in configuration"));
			
			String path = DriveWireServer.serverconfig.getString("LocalDiskDir");
			
			FileSystemManager fsManager;
			
			fsManager = VFS.getManager();
			
			FileObject dirobj = fsManager.resolveFile(path);
			
			FileObject[] children = dirobj.getChildren();
	    	
	    	for (int i=0; i<children.length; i++) 
	    	{
	    		if (children[i].getType() == FileType.FILE)
	    			res += children[i].getName() + "\n";
	    	}
		    
	    }
		catch (IOException e) 
	    {
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e.getMessage()));
		}
			
		return(new DWCommandResponse(res));
	}

	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
