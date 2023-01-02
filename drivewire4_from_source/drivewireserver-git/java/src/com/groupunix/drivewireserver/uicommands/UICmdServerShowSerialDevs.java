package com.groupunix.drivewireserver.uicommands;

import java.util.ArrayList;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdServerShowSerialDevs extends DWCommand {

	@Override
	public String getCommand() 
	{
		// TODO Auto-generated method stub
		return "serialdevs";
	}



	@Override
	public String getShortHelp() {
		// TODO Auto-generated method stub
		return "show available serial devices";
	}

	

	
	
	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "ui server show serialdevs";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String txt = new String();
			
		ArrayList<String> ports = DriveWireServer.getAvailableSerialPorts();
		
		for (int i = 0;i<ports.size();i++)
			txt += ports.get(i) + "\n";
		
		return(new DWCommandResponse(txt));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
