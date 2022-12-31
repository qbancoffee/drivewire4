package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdInstanceConfigSet extends DWCommand {

	static final String command = "set";
	private DWUIClientThread uiref = null;
	private DWProtocol dwProto = null;

	public UICmdInstanceConfigSet(DWUIClientThread dwuiClientThread) 
	{
		this.uiref = dwuiClientThread;
	}

	public UICmdInstanceConfigSet(DWProtocol dwProto) 
	{
		this.dwProto = dwProto;
	}

	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{

		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR , "Must specify item"));
		}	
		
		String[] args = cmdline.split(" ");
		
		if (args.length == 1)
		{
			return(doSetConfig(args[0]));
		}
		else
		{
			
			
			return(doSetConfig(args[0],cmdline.substring(args[0].length()+1)));
		}
		
	}


	public String getShortHelp() 
	{
		return "Set instance configuration item";
	}


	public String getUsage() 
	{
		return "ui instance config set [item] [value]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
	
	private DWCommandResponse doSetConfig(String item)
	{
		
		if (this.uiref != null)
		{
			if (DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().containsKey(item))
			{
				synchronized(DriveWireServer.serverconfig)
				{
					DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().clearProperty(item);
				}
				return(new DWCommandResponse("Item '" + item + "' removed from config."));
			}
			else
			{
				return(new DWCommandResponse("Item '" + item + "' is not set."));
			}
			
		}
		else
		{
			if (dwProto.getConfig().containsKey(item))
			{
				synchronized(DriveWireServer.serverconfig)
				{
					dwProto.getConfig().clearProperty(item);
				}
				return(new DWCommandResponse("Item '" + item + "' removed from config."));
			}
			else
			{
				return(new DWCommandResponse("Item '" + item + "' is not set."));
			}
		}
		
	}
	
	
	private DWCommandResponse doSetConfig(String item, String value)
	{
		synchronized(DriveWireServer.serverconfig)
		{
			if (this.uiref != null)
				DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().setProperty(item, value);
			else
				dwProto.getConfig().setProperty(item, value);
		}
		return(new DWCommandResponse("Item '" + item + "' set to '" + value + "'."));
	}
	
	
}