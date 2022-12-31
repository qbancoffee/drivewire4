package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidi extends DWCommand {

	static final String command = "midi";
	private DWCommandList commands;
	private DWProtocolHandler dwProto;	
	
	public DWCmdMidi(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdMidiStatus(dwProto, this));
		commands.addcommand(new DWCmdMidiOutput(dwProto, this));
		commands.addcommand(new DWCmdMidiSynth(dwProto, this));	
		
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
		return "Manage the MIDI subsystem";
	}


	public String getUsage() 
	{
		return "dw midi [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
}
