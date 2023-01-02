package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class UICmdInstance extends DWCommand {

	static final String command = "instance";
		
	public UICmdInstance(DWUIClientThread dwuiClientThread)
	{
		commands.addcommand(new UICmdInstanceAttach(dwuiClientThread));
		commands.addcommand(new UICmdInstanceConfig(dwuiClientThread));
		commands.addcommand(new UICmdInstanceDisk(dwuiClientThread));
		commands.addcommand(new UICmdInstanceReset(dwuiClientThread));
		commands.addcommand(new UICmdInstanceStatus(dwuiClientThread));
		commands.addcommand(new UICmdInstanceMIDIStatus(dwuiClientThread));
		commands.addcommand(new UICmdInstancePrinterStatus(dwuiClientThread));
		commands.addcommand(new UICmdInstancePortStatus(dwuiClientThread));
		commands.addcommand(new UICmdInstanceTimer(dwuiClientThread));
		commands.addcommand(new UICmdInstanceVPortSniff(dwuiClientThread));
	}

	
	public UICmdInstance(DWProtocol dwProto) 
	{
		commands.addcommand(new UICmdInstanceConfig(dwProto) );
		if (dwProto.hasDisks())
			commands.addcommand(new UICmdInstanceDisk((DWProtocolHandler) dwProto));
		
		commands.addcommand(new UICmdInstanceReset(dwProto));
		commands.addcommand(new UICmdInstanceStatus(dwProto));
		
		if (dwProto.hasMIDI())
			commands.addcommand(new UICmdInstanceMIDIStatus(dwProto));
		
		if (dwProto.hasPrinters())
			commands.addcommand(new UICmdInstancePrinterStatus((DWProtocolHandler) dwProto));
		
		if (dwProto.hasVSerial())
			commands.addcommand(new UICmdInstancePortStatus((DWVSerialProtocol)dwProto));
		
		commands.addcommand(new UICmdInstanceTimer(dwProto));
	}


	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		return(commands.parse(cmdline));
	}


	public String getShortHelp() 
	{
		return "Instance commands";
	}


	public String getUsage() 
	{
		return "ui instance [command]";
	}
	
	public boolean validate(String cmdline) 
	{
		return(commands.validate(cmdline));
	}
	
}