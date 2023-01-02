package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;


public class DWCmdServerShow extends DWCommand {


	private DWCommandList commands;
	private DWProtocol dwProto;
	
	public DWCmdServerShow(DWProtocol dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdServerShowThreads(this));
		commands.addcommand(new DWCmdServerShowTimers(this.dwProto, this));
		commands.addcommand(new DWCmdServerShowSerial(this.dwProto, this));
	}
	
	public String getCommand() 
	{
		return "show";
	}

	public DWCommandList getCommandList()
	{
		return(this.commands);
	}
	
	
	public String getShortHelp() 
	{
		return "Show various server information";
	}


	public String getUsage() 
	{
		return "dw server show [option]";
	}

	public DWCommandResponse parse(String cmdline)
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(this.commands.getShortHelp()));
		}
		return(commands.parse(cmdline));
	}

	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}

}
