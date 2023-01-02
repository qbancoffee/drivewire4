package com.groupunix.drivewireserver.dwcommands;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdMidiStatus extends DWCommand {

	private DWProtocolHandler dwProto;

	public DWCmdMidiStatus(DWProtocolHandler dwProto,DWCommand parent)
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
		return "Show MIDI status";
	}


	public String getUsage() 
	{
		return "dw midi status";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		return(doMidiStatus());
	}

	private DWCommandResponse doMidiStatus()
	{
		String text = new String();
		
		text += "\r\nDriveWire MIDI status:\r\n\n";

		if (dwProto.getConfig().getBoolean("UseMIDI",true))
		{
			text +="Devices:\r\n";
	
			MidiDevice device;
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
	
			for (int i = 0; i < infos.length; i++) 
			{
				try 
				{
					device = MidiSystem.getMidiDevice(infos[i]);
					text += "[" + i + "] ";
					text += device.getDeviceInfo().getName() + " (" + device.getClass().getSimpleName()  + ")\r\n";
					text += "    " + device.getDeviceInfo().getDescription() + ", ";
					text += device.getDeviceInfo().getVendor() + " ";
					text += device.getDeviceInfo().getVersion() + "\r\n";
	        
				} 
				catch (MidiUnavailableException e) 
				{
					return(new DWCommandResponse(false,DWDefs.RC_MIDI_UNAVAILABLE,e.getMessage()));
				}
	    
			}

			text += "\r\nCurrent MIDI output device: ";
        
			if (dwProto.getVPorts().getMidiDeviceInfo() == null)
			{
        	
				text += "none\r\n";
			}
			else
			{
				text += dwProto.getVPorts().getMidiDeviceInfo().getName() + "\r\n";  
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
