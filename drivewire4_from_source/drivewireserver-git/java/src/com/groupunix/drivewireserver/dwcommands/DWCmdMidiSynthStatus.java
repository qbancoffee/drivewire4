package com.groupunix.drivewireserver.dwcommands;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidiSynthStatus extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdMidiSynthStatus(DWProtocolHandler dwProto,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProto;
	}
	
	public String getCommand() 
	{
		return "status";
	}

	
	public String getShortHelp() 
	{
		return "Show internal synth status";
	}


	public String getUsage() 
	{
		return "dw midi synth status";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		return(doSynthStatus());
	}

	private DWCommandResponse doSynthStatus()
	{
		String text = new String();
			
		// dw midi synth show
		text = "\r\nInternal synthesizer status:\r\n\n";
		
		if (dwProto.getVPorts().getMidiSynth() != null)
		{
			MidiDevice.Info midiinfo = dwProto.getVPorts().getMidiSynth().getDeviceInfo();

			text += "Device:\r\n";
			text += midiinfo.getVendor() + ", " + midiinfo.getName() + ", " + midiinfo.getVersion() + "\r\n";
			text += midiinfo.getDescription() + "\r\n";

			text += "\r\n";
			
			text += "Soundbank: ";
	
			if (dwProto.getVPorts().getMidiSoundbankFilename() == null)
			{
				Soundbank sbank = dwProto.getVPorts().getMidiSynth().getDefaultSoundbank();
				
				if (sbank != null)
				{
					text += " (default)\r\n";
					text += sbank.getVendor() + ", " + sbank.getName() + ", " + sbank.getVersion() + "\r\n";
					text += sbank.getDescription() + "\r\n";
				}
				else
				{
					text += " none\r\n";
				}
			}
			else
			{
				File file = new File(dwProto.getVPorts().getMidiSoundbankFilename());
				try 
				{
					Soundbank sbank = MidiSystem.getSoundbank(file);
			
					text += " (" + dwProto.getVPorts().getMidiSoundbankFilename() + ")\r\n";
					text += sbank.getVendor() + ", " + sbank.getName() + ", " + sbank.getVersion() + "\r\n";
					text += sbank.getDescription() + "\r\n";
			
				}	 
				catch (InvalidMidiDataException e) 
				{
					return(new DWCommandResponse(false,DWDefs.RC_MIDI_INVALID_DATA,e.getMessage()));
				} 
				catch (IOException e) 
				{
					return(new DWCommandResponse(false,DWDefs.RC_SERVER_IO_EXCEPTION,e.getMessage()));
				}
			}
			
			text += "\r\n";
			
			text += "Latency:   " + dwProto.getVPorts().getMidiSynth().getLatency() + "\r\n";
			text += "Polyphony: " + dwProto.getVPorts().getMidiSynth().getMaxPolyphony() + "\r\n";
			text += "Position:  " + dwProto.getVPorts().getMidiSynth().getMicrosecondPosition() + "\r\n\n";
			text += "Profile:   " + dwProto.getVPorts().getMidiProfileName() + "\r\n";
			text += "Instrlock: " + dwProto.getVPorts().getMidiVoicelock() + "\r\n";
			
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
