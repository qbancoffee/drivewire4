package com.groupunix.drivewireserver.dwcommands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.groupunix.drivewireserver.DriveWireServer;

public class DWCmdMidiSynthShowProfiles extends DWCommand {

 
	public DWCmdMidiSynthShowProfiles(DWCommand parent)
	{
		setParentCmd(parent);
	}
	
	public String getCommand() 
	{
		return "profiles";
	}


	public String getShortHelp() 
	{
		return "Show internal synth profiles";
	}


	public String getUsage() 
	{
		return "dw midi synth show profiles";
	}

	@SuppressWarnings("unchecked")
	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
		
		text = "\r\nAvailable sound translation profiles:\r\n\n";
		
		List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
    	
		for(Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext();)
		{
		    HierarchicalConfiguration mprof = it.next();
		    
		    text += String.format("%-10s: %-35s dev_adjust: %2d  gm_adjust: %2d", mprof.getString("[@name]", "n/a"), mprof.getString("[@desc]","n/a") , mprof.getInt("[@dev_adjust]",0) ,mprof.getInt("[@gm_adjust]", 0) );
		    text += "\r\n";
		}
	
		text += "\r\n";
		
		return(new DWCommandResponse(text));
	}


	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
