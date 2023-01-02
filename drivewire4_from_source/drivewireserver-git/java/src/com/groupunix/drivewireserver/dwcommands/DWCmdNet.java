package com.groupunix.drivewireserver.dwcommands;


import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWCmdNet extends DWCommand {

	static final String command = "net";
	private DWCommandList commands;
	private DWVSerialProtocol dwProto;	
	
	public DWCmdNet(DWVSerialProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdNetShow(dwProtocol,this));
		
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
		return "Manage network connections";
	}


	public String getUsage() 
	{
		return "dw net [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
}
