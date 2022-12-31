package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdInstanceStatus extends DWCommand {

	private DWUIClientThread clientref = null;
	private DWProtocol gproto = null;
	
	public UICmdInstanceStatus(DWUIClientThread dwuiClientThread) 
	{
		clientref = dwuiClientThread;
	}

	public UICmdInstanceStatus(DWProtocol dwProto) 
	{
		this.gproto = dwProto;
	}

	@Override
	public String getCommand() 
	{
		return "status";
	}

	@Override
	public String getShortHelp() {
		return "show instance status";
	}

	@Override
	public String getUsage() {
		return "ui instance status";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String txt = "";
		int hno = 0;
		
		if (cmdline.length() > 0)
		{
			try
			{
				hno = Integer.parseInt(cmdline);
				
				if (DriveWireServer.isValidHandlerNo(hno))
					 gproto = DriveWireServer.getHandler(hno);
				else
					return(new DWCommandResponse(false,DWDefs.RC_INVALID_HANDLER,"Invalid handler number"));
			}
			catch (NumberFormatException ne)
			{
				return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric instance #"));
			}
		}
		else
		{
			if (this.clientref != null)
			{
				gproto = DriveWireServer.getHandler(clientref.getInstance());
				hno = this.clientref.getInstance();
			}
		}
		
		
		txt = "num|" + hno + "\n";
		txt += "name|" + gproto.getConfig().getString("[@name]","not set") + "\n";
		txt += "desc|" + gproto.getConfig().getString("[@desc]","not set") + "\n";
		
		txt += "proto|" + gproto.getConfig().getString("Protocol","DriveWire") + "\n";
		
		txt += "autostart|" + gproto.getConfig().getBoolean("AutoStart", true) + "\n";
		txt += "dying|" + gproto.isDying() + "\n";
		txt += "started|" + gproto.isStarted() + "\n";
		txt += "ready|" + gproto.isReady() + "\n";
		txt += "connected|" + gproto.isConnected() + "\n";
		
		if (gproto.getProtoDev() != null)
		{
			txt += "devicetype|" + gproto.getProtoDev().getDeviceType() + "\n";
			
			txt += "devicename|" + gproto.getProtoDev().getDeviceName() + "\n";
			txt += "deviceconnected|" + gproto.getProtoDev().connected() + "\n";
			
			if (gproto.getProtoDev().getRate() > -1)
				txt += "devicerate|" + gproto.getProtoDev().getRate() + "\n";
			
			if (gproto.getProtoDev().getClient() != null)
				txt += "deviceclient|" + gproto.getProtoDev().getClient() + "\n";
			
		}
		
		if (gproto.getConfig().getString("Protocol", "DriveWire").equals("DriveWire"))
		{
		
			DWProtocolHandler dwProto = (DWProtocolHandler) gproto;
			txt += "lastopcode|" + DWUtils.prettyOP(dwProto.getLastOpcode()) + "\n";
			txt += "lastgetstat|" + DWUtils.prettySS(dwProto.getLastGetStat()) + "\n";
			txt += "lastsetstat|" + DWUtils.prettySS(dwProto.getLastSetStat()) + "\n";
			txt += "lastlsn|" + DWUtils.int3(dwProto.getLastLSN()) + "\n";
			txt += "lastdrive|" + dwProto.getLastDrive() +"\n";
			txt += "lasterror|" + dwProto.getLastError() + "\n";
			txt += "lastchecksum|" + dwProto.getLastChecksum() + "\n";
			
		}
		
		return(new DWCommandResponse(txt));
	
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
