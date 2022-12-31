package com.groupunix.drivewireserver.uicommands;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowMIDIDevs extends DWCommand {

	@Override
	public String getCommand() 
	{
		return "mididevs";
	}


	@Override
	public String getShortHelp() {
		return "show available MIDI devices";
	}

	@Override
	public String getUsage() {
		return "ui server show mididevs";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String res = new String();
	
		// hack.. should look at current instance but I just don't care
		if (DriveWireServer.getHandler(0).getConfig().getBoolean("UseMIDI",true))
		{	
		
			MidiDevice device;
			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
	
			for (int i = 0; i < infos.length; i++) 
			{
				try 
				{
					device = MidiSystem.getMidiDevice(infos[i]);
					res += i + " " + device.getDeviceInfo().getName() +"\n";
				
				} 
				catch (MidiUnavailableException e) 
				{
					return(new DWCommandResponse(false,DWDefs.RC_MIDI_UNAVAILABLE,"MIDI unavailable during UI device listing"));
				}
	    
			}
		}
		else
		{
			return(new DWCommandResponse(false,DWDefs.RC_MIDI_UNAVAILABLE,"MIDI is disabled."));
		}
		
		return(new DWCommandResponse(res));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
