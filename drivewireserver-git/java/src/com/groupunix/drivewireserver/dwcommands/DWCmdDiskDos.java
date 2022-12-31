package com.groupunix.drivewireserver.dwcommands;


import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdDiskDos extends DWCommand {

	static final String command = "dos";
	private DWCommandList commands;
	private DWProtocolHandler dwProto;	
	
	public DWCmdDiskDos(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		
		commands.addcommand(new DWCmdDiskDosDir(dwProto,this));
		commands.addcommand(new DWCmdDiskDosList(dwProto,this));
		commands.addcommand(new DWCmdDiskDosFormat(dwProto,this));
		commands.addcommand(new DWCmdDiskDosAdd(dwProto,this));
	}

	
	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(this.commands.getShortHelp()));
		}
		return(commands.parse(cmdline));
	}

	public DWCommandList getCommandList()
	{
		return(this.commands);
	}



	public String getShortHelp() 
	{
		return "Manage DOS disks";
	}


	public String getUsage() 
	{
		return "dw disk dos [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}
