package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstanceMIDIStatus extends DWCommand {

	private DWUIClientThread dwuithread = null;
	private DWProtocol gproto = null;

	public UICmdInstanceMIDIStatus(DWUIClientThread dwuiClientThread) 
	{
		this.dwuithread = dwuiClientThread;
		
	}


	public UICmdInstanceMIDIStatus(DWProtocol dwProto) 
	{
		this.gproto = dwProto;
	}


	@Override
	public String getCommand() 
	{
		return "midistatus";
	}


	@Override
	public String getShortHelp() {
		return "show MIDI status";
	}

	@Override
	public String getUsage() {
		return "ui instance midistatus";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		
		String res = "enabled|false\n\n";
		
		if (this.gproto == null)
		{
			if (DriveWireServer.isValidHandlerNo(this.dwuithread.getInstance()))
			{
				this.gproto = DriveWireServer.getHandler(this.dwuithread.getInstance());
			}
			else
			{
				return(new DWCommandResponse(res));
			}
		}
			
		
		if (this.gproto.hasMIDI())
		{
			DWProtocolHandler dwProto = (DWProtocolHandler)gproto;
		
			if (!(dwProto == null) && !(dwProto.getVPorts() == null) &&  !(dwProto.getVPorts().getMidiDeviceInfo() == null) )
			{
				try
				{
					res = "enabled|" + dwProto.getConfig().getBoolean("UseMIDI", false) + "\r\n";
					res += "cdevice|" + dwProto.getVPorts().getMidiDeviceInfo().getName() + "\r\n";
					res += "cprofile|" +dwProto.getVPorts().getMidiProfileName() + "\r\n";
				
					MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		
					for (int j = 0;j<infos.length;j++)
					{
						MidiDevice.Info i = infos[j];
						MidiDevice dev = MidiSystem.getMidiDevice(i);
						
						res += "device|" + j + "|" + dev.getClass().getSimpleName() + "|" + i.getName() +"|"+ i.getDescription() +"|" + i.getVendor() + "|" + i.getVersion() +"\r\n";
						
					}
	
					@SuppressWarnings("unchecked")
					List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
			    	
					for(Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext();)
					{
					    HierarchicalConfiguration mprof = it.next();
					    
					    res += "profile|" + mprof.getString("[@name]") +"|" + mprof.getString("[@desc]") + "\r\n";
					}
				}
				catch (MidiUnavailableException e)
				{
					res = "enabled|false\n\n";
				}
			}
		}
		
		return(new DWCommandResponse(res));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
