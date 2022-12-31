
package com.groupunix.drivewireserver.dwcommands;


import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;


public class DWCmdServerPrint extends DWCommand {

	
	private DWProtocol dwProto;
	
	public DWCmdServerPrint(DWProtocol dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}

	
	public String getCommand() 
	{
		return "print";
	}

	
	public String getShortHelp() 
	{
		return "Print contents of file on server";
	}


	public String getUsage() 
	{
		return "dw server print URI/path";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw server print requires a URI or local file path as an argument"));
		}
		return(doPrint(cmdline));
	}

	
	
	private DWCommandResponse doPrint(String path) 
	{
		FileSystemManager fsManager;
		InputStream ins = null;
		FileObject fileobj = null;
		FileContent fc = null;
		
		try
		{
			fsManager = VFS.getManager();
		
			path = DWUtils.convertStarToBang(path);
			
			fileobj = fsManager.resolveFile(path);
		
			fc = fileobj.getContent();
			
			ins = fc.getInputStream();

			byte data = (byte) ins.read();
			
			while (data >= 0)
			{
				((DWProtocolHandler) dwProto).getVPrinter().addByte(data);
				data = (byte) ins.read();
			}
			
			ins.close();
			((DWProtocolHandler) dwProto).getVPrinter().flush();
			
			
		}	
		catch (FileSystemException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION,e.getMessage()));
		} 
		catch (IOException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e.getMessage()));
	    }	
		finally
		{
			try
			{
				if (ins != null)
					ins.close();
				
				if (fc != null)
					fc.close();
				
				if (fileobj != null)
					fileobj.close();
				
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return(new DWCommandResponse("Sent item to printer"));
	}

	public boolean validate(String cmdline) {
		return true;
	}

}
