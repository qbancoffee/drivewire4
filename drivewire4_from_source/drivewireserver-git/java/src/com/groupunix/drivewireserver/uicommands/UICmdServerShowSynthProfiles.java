package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowSynthProfiles extends DWCommand {

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "synthprofiles";
	}


	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show MIDI synth profiles";
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show synthprofiles";
	}

	@SuppressWarnings("unchecked")
	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String res = new String();
		
		List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
    	
		for(Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext();)
		{
			
		    HierarchicalConfiguration mprof = (HierarchicalConfiguration) it.next();
		    res += mprof.getString("[@name]") + "|" + mprof.getString("[@desc]") + "\n";
	    	
		    
		}
	
	
			
		return(new DWCommandResponse(res));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
