package com.groupunix.drivewireserver.dwcommands;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidiSynthShowChannels extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdMidiSynthShowChannels(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "channels";
	}

	
	public String getShortHelp() 
	{
		return "Show internal synth channel status";
	}


	public String getUsage() 
	{
		return "dw midi synth show channels";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
		
		text = "\r\nInternal synthesizer channel status:\r\n\n";
		
		if (dwProto.getVPorts().getMidiSynth() != null)
		{
			MidiChannel[] midchans = dwProto.getVPorts().getMidiSynth().getChannels();
		
			Instrument[] instruments = dwProto.getVPorts().getMidiSynth().getLoadedInstruments();
		
			text +="Chan#  Instr#  Orig#   Instrument\r\n";
			text +="-----------------------------------------------------------------------------\r\n";
		
			for (int i = 0; i < midchans.length; i++)
			{
				if (midchans[i] != null)
				{
					text += String.format(" %2d      %-3d    %-3d    ",(i+1),midchans[i].getProgram(),dwProto.getVPorts().getGMInstrumentCache(i));
				
					if (midchans[i].getProgram() < instruments.length)
					{
						text += instruments[midchans[i].getProgram()].getName();
					}
					else
					{
						text += "(unknown instrument or no soundbank loaded)";
					}
					text += "\r\n";
				}
			}
		}
		else
		{
			text += "MIDI is disabled.\r\n";
		}
		return(new DWCommandResponse(text));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}
