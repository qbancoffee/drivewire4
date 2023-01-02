package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class UICmdInstanceConfigShow extends DWCommand {

	static final String command = "show";
	
	private DWUIClientThread uiref = null;
	private DWProtocol dwProto = null;

	
	public UICmdInstanceConfigShow(DWUIClientThread dwuiClientThread) 
	{

		this.uiref = dwuiClientThread;
	}

	public UICmdInstanceConfigShow(DWProtocol dwProto) 
	{
		this.dwProto  = dwProto;
	}

	
	public String getCommand() 
	{
		return command;
	}

	@SuppressWarnings("unchecked")
	public DWCommandResponse parse(String cmdline)
	{
		String res = new String();
		
		int instance;
		
		if (this.uiref != null)
		{
			instance = this.uiref.getInstance();
		}
		else
		{
			instance = this.dwProto.getHandlerNo();
		}
		
		
		if (cmdline.length() == 0)
		{
			for (Iterator<String> i = DriveWireServer.getHandler(instance).getConfig().getKeys(); i.hasNext();)
			{
				String key = i.next();
				String value = StringUtils.join(DriveWireServer.getHandler(instance).getConfig().getStringArray(key), ", ");
		
				res += key + " = " + value + "\r\n";
		            
			}
		}
		else
		{
			if (DriveWireServer.getHandler(instance).getConfig().containsKey(cmdline))
			{
				String value = StringUtils.join(DriveWireServer.getHandler(instance).getConfig().getStringArray(cmdline), ", ");
				return(new DWCommandResponse(value));
			}
			else
			{
				return(new DWCommandResponse(false,DWDefs.RC_CONFIG_KEY_NOT_SET, "Key '" + cmdline + "' is not set."));
			}
		}
		
		return(new DWCommandResponse(res));
	}



	public String getShortHelp() 
	{
		return "Show instance configuration";
	}


	public String getUsage() 
	{
		return "ui instance config show [item]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}