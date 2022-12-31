package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidiSynth extends DWCommand {

	static final String command = "synth";
	private DWCommandList commands;
	private DWProtocolHandler dwProto;	
	
	public DWCmdMidiSynth(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
		commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
		commands.addcommand(new DWCmdMidiSynthStatus(dwProto,this));
		commands.addcommand(new DWCmdMidiSynthShow(dwProto,this));
		commands.addcommand(new DWCmdMidiSynthBank(dwProto,this));
		commands.addcommand(new DWCmdMidiSynthProfile(dwProto,this));
		commands.addcommand(new DWCmdMidiSynthLock(dwProto,this));
		commands.addcommand(new DWCmdMidiSynthInstr(dwProto,this));
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
		return "Manage the MIDI synth";
	}


	public String getUsage() 
	{
		return "dw midi synth [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
}
