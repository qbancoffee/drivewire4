package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdTestDGraph extends DWCommand {

	static final String command = "dgraph";
	DWUIClientThread clientref;

	public UICmdTestDGraph(DWUIClientThread dwuiClientThread)
	{
		this.clientref = dwuiClientThread;
	}

	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_sectors", 630 + "");
		
		for (int i = 0;i<630;i++)
		{
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_lsn", i+"");
			
			if (i % 2 == 0)
				DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_writes", i+"");
			else
				DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_reads", i+"");
			
			
			int rndsec = (int) (Math.random() * 630);
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 1, "_lsn", rndsec +"");
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 1, "_reads", 1 +"");
			
			rndsec = (int) (Math.random() * 630);
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 2, "_lsn", rndsec +"");
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 2, "_reads", rndsec +"");
			
			rndsec = (int) (Math.random() * 630);
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 3, "_lsn", rndsec +"");
			DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 3, "_reads", 1 +"");
			
			
			try
			{
				Thread.sleep(15);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
				
		return(new DWCommandResponse("Test data submitted at: " + System.currentTimeMillis()));
	}


	public String getShortHelp() 
	{
		return "Test disk graphics";
	}


	public String getUsage() 
	{
		return "ui test dgraph";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}