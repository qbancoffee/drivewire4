package com.groupunix.drivewireserver.dwcommands;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdConfigShow extends DWCommand {

	DWProtocol dwProto;
	
	public DWCmdConfigShow(DWProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
	}

	public String getCommand() 
	{
		return "show";
	}


	
	public String getShortHelp() 
	{
		return "Show current instance config (or item)";
	}


	public String getUsage() 
	{
		return "dw config show [item]";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() > 0)
		{
			return(doShowConfig(cmdline));
		}
		return(doShowConfig());
	}

	private DWCommandResponse doShowConfig(String item)
	{
		String text = new String();
		
		if (dwProto.getConfig().containsKey(item))
		{
			String key = item;
			String value = StringUtils.join(dwProto.getConfig().getStringArray(key), ", ");
		
			text += key + " = " + value;
			return(new DWCommandResponse(text));
		}
		else
		{
			return(new DWCommandResponse(false,DWDefs.RC_CONFIG_KEY_NOT_SET, "Key '" + item + "' is not set."));
		}
		
		
	}
	
	
	public boolean validate(String cmdline)
	{

		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	private DWCommandResponse doShowConfig()
	{
		String text = new String();
		
		text += "Current protocol handler configuration:\r\n\n";
		
		for (Iterator<String> i = dwProto.getConfig().getKeys(); i.hasNext();)
		{
			String key = i.next();
			//String value = StringUtils.join(dwProto.getConfig().getStringArray(key), ", ");
			String value = dwProto.getConfig().getProperty(key).toString();
			
			text += key + " = " + value + "\r\n";
		            
		}
		
		return(new DWCommandResponse(text));
	}
	

}
