package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdConfig extends DWCommand
{
	
	static final String command = "config";
	private DWCommandList commands;
	private DWProtocol dwProto;
	
	public DWCmdConfig(DWProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdConfigShow(dwProtocol, this));
		commands.addcommand(new DWCmdConfigSet(dwProtocol, this));
		commands.addcommand(new DWCmdConfigSave(dwProtocol, this));
		// save/load not implemented here
	} 

	
	public String getCommand() 
	{
		return command;
	}
	
	public DWCommandList getCommandList()
	{
		return(this.commands);
	}

	public DWCommandResponse parse(String cmdline)
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(this.commands.getShortHelp()));
		}
		return(commands.parse(cmdline));
	}




	public String getShortHelp() 
	{
		return "Commands to manipulate the config";
	}


	public String getUsage() 
	{
		return "dw config [command]";
	}


	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
	
}
