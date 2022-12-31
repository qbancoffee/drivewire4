package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdServer extends DWCommand {

	static final String command = "server";
	private DWCommandList commands;
	private DWProtocol dwProto;	
	
	public DWCmdServer(DWProtocol dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdServerStatus(dwProto,this));
		commands.addcommand(new DWCmdServerShow(dwProto,this));
		commands.addcommand(new DWCmdServerList(this));
		commands.addcommand(new DWCmdServerDir(this));
		commands.addcommand(new DWCmdServerTerminate(this));
		commands.addcommand(new DWCmdServerTurbo(dwProto,this));
		commands.addcommand(new DWCmdServerPrint(dwProto,this));
		commands.addcommand(new DWCmdServerHelp(dwProto,this));
		
	//	commands.addcommand(new DWCmdServerRestart(handlerno));
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
		return "Various server based tools";
	}


	public String getUsage() 
	{
		return "dw server [command]";
	}

	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
}
