package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdInstanceShow extends DWCommand {

	DWCmdInstanceShow(DWProtocol dwProto, DWCommand parent)
	{
		setParentCmd(parent);
		
	}
	
	public String getCommand() 
	{
		return "show";
	}


	
	public String getShortHelp() 
	{
		return "Show instance status";
	}


	public String getUsage() 
	{
		return "dw instance show";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		String text = new String();
		
		text += "DriveWire protocol handler instances:\r\n\n";
		
		for (int i = 0;i<DriveWireServer.getNumHandlers();i++)
		{
			text += "#" + i + "  (";
			
			
			if (DriveWireServer.getHandler(i).isDying())
				text += "Dying..)   ";
			else if (DriveWireServer.getHandler(i).isReady())
				text += "Ready)     ";
			else if (DriveWireServer.getHandler(i).isStarted())
				text += "Starting)  ";
			else
				text += "Not ready) ";
			
			if (DriveWireServer.getHandler(i) == null)
			{
				text += " Null (?)\r\n";
			}
			else
			{
				String proto = DriveWireServer.getHandler(i).getConfig().getString("Protocol", "DriveWire");
				text += String.format("Proto: %-11s", proto);
				
				String dtype = DriveWireServer.getHandler(i).getConfig().getString("DeviceType", "Unknown");
				
				
				if (dtype.equals("serial") ||  proto.equals("VModem") )
				{
					text += String.format("Type: %-11s", "serial");
					text += " Dev: " + DriveWireServer.getHandler(i).getConfig().getString("SerialDevice", "Unknown");
				}
				else if (dtype.equals("tcp-server") )
				{
					text += String.format("Type: %-11s", "tcp-server");
					text += "Port: " + DriveWireServer.getHandler(i).getConfig().getString("TCPServerPort","Unknown");
				}
				else if (dtype.equals("tcp-client") )
				{
					text += String.format("Type: %-11s", dtype);
					text += "Host: " + DriveWireServer.getHandler(i).getConfig().getString("TCPClientHost", "Unknown") + ":" + DriveWireServer.getHandler(i).getConfig().getString("TCPClientPort","Unknown");
				}
				
				
				
				text += "\r\n";
			}
			
		}
		
		return(new DWCommandResponse(text));
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
