package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdInstance extends DWCommand {

	static final String command = "instance";
	private DWCommandList commands;
	private DWProtocol dwProto;	
	
	public DWCmdInstance(DWProtocol dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdInstanceShow(dwProto,this));
		commands.addcommand(new DWCmdInstanceStart(dwProto,this));
		commands.addcommand(new DWCmdInstanceStop(dwProto,this));
		commands.addcommand(new DWCmdInstanceRestart(dwProto,this));
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
		return "Commands to control instances";
	}


	public String getUsage() 
	{
		return "dw instance [command]";
	}

	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
}
