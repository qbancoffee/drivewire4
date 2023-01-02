package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstancePrinterStatus extends DWCommand {

	private DWUIClientThread dwuithread = null;
	private DWProtocolHandler dwProto = null;

	public UICmdInstancePrinterStatus(DWUIClientThread dwuiClientThread) 
	{
		this.dwuithread = dwuiClientThread;
	}


	public UICmdInstancePrinterStatus(DWProtocolHandler dwProto) 
	{
		this.dwProto = dwProto;
	}


	@Override
	public String getCommand() 
	{
		return "printerstatus";
	}


	@Override
	public String getShortHelp() {
		return "show printer status";
	}

	@Override
	public String getUsage() {
		return "ui instance printerstatus";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String res = "";
		
		if (dwProto == null)
		{
			if (DriveWireServer.getHandler(this.dwuithread.getInstance()).hasPrinters())
			{
				dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.dwuithread.getInstance());
			}
			else
			{
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_WONT ,"This operation is not supported on this type of instance"));
			}
		}
			
			
		res = "currentprinter|" + dwProto.getConfig().getString("CurrentPrinter","none") + "\r\n";
			
		@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> profiles =  dwProto.getConfig().configurationsAt("Printer");
    	
		for(Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext();)
		{
		    HierarchicalConfiguration mprof = it.next();
		    
		    res += "printer|" + mprof.getString("[@name]") +"|" + mprof.getString("[@desc]") + "\r\n";
		}
	
		
		return(new DWCommandResponse(res));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
