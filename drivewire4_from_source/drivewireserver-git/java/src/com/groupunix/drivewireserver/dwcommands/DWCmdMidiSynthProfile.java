package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidiSynthProfile extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdMidiSynthProfile(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "profile";
	}


	
	public String getShortHelp() 
	{
		return "Load synth translation profile";
	}


	public String getUsage() 
	{
		return "dw midi synth profile name";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		if (cmdline.length() == 0)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw midi synth profile requires a profile name as an argument"));
		}
		
		return(doMidiSynthProfile(cmdline));
	}

	
	private DWCommandResponse doMidiSynthProfile(String path)
	{
		
		if (dwProto.getVPorts().setMidiProfile(path))
		{
			return(new DWCommandResponse("Set translation profile to '" + path + "'"));
		}
		else
		{
			return(new DWCommandResponse(false,DWDefs.RC_MIDI_INVALID_PROFILE,"Invalid translation profile '" + path + "'"));
		}
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}
