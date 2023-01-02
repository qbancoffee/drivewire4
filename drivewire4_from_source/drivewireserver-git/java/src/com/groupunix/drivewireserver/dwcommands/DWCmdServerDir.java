package com.groupunix.drivewireserver.dwcommands;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class DWCmdServerDir extends DWCommand {

	
	public DWCmdServerDir(DWCommand parent)
	{
		setParentCmd(parent);
	}
	
	
	public String getCommand() 
	{
		return "dir";
	}


	public String getShortHelp() 
	{
		return "Show directory of URI or local path";
	}


	public String getUsage() 
	{
		return "dw server dir URI/path";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw server dir requires a URI or path as an argument"));
		}
		return(doDir(cmdline));
	}

	private DWCommandResponse doDir(String path)
	{
		FileSystemManager fsManager;
		
		String text = new String();
		
		path = DWUtils.convertStarToBang(path);
				
		try
		{
			fsManager = VFS.getManager();
		
			FileObject dirobj = fsManager.resolveFile(path);
			
			FileObject[] children = dirobj.getChildren();

			text += "Directory of " + dirobj.getName().getURI() + "\r\n\n";
		
			
			int longest = 0;
	    	
	    	for (int i=0; i<children.length; i++) 
	    	{
	    		if (children[i].getName().getBaseName().length() > longest)
	    			longest = children[i].getName().getBaseName().length();
	    	}
	    	
	    	longest++;
	    	longest++;
	    	
	    	int cols = Math.max(1, 80 / longest);
	    	
	    	for (int i=0; i<children.length; i++) 
	        {
	        	text += String.format("%-" + longest + "s",children[i].getName().getBaseName());
	        	if (((i+1) % cols) == 0)
	        		text += "\r\n";
	        }
			
		} 
		catch (FileSystemException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION,e.getMessage()));
		}
		
	    return(new DWCommandResponse(text));
	}


	public boolean validate(String cmdline) {
		return true;
	}
	
	
}
