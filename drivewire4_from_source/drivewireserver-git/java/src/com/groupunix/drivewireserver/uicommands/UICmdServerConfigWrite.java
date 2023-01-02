package com.groupunix.drivewireserver.uicommands;

import java.io.StringWriter;

import org.apache.commons.configuration.ConfigurationException;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerConfigWrite extends DWCommand {

	static final String command = "write";
	

	public String getCommand() 
	{
		return command;
	}


	public DWCommandResponse parse(String cmdline)
	{
		String res = new String();
		
		StringWriter sw = new StringWriter();
		
		try 
		{
			DriveWireServer.serverconfig.save(sw);
		} 
		catch (ConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		res = sw.getBuffer().toString();
		
		return(new DWCommandResponse(res));
	}



	public String getShortHelp() 
	{
		return "Write config xml";
	}


	public String getUsage() 
	{
		return "ui server config write";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}