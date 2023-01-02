package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

public class UICmdInstanceTimerShow extends DWCommand {

	private DWProtocol dwProto = null;
	private DWUIClientThread uiref;
	

	public UICmdInstanceTimerShow(DWProtocol dwProtocol) 
	{
		this.dwProto = dwProtocol;
	}

	public UICmdInstanceTimerShow(DWUIClientThread dwuiClientThread)
	{
		this.uiref = dwuiClientThread;
	}

	@Override
	public String getCommand() 
	{
		return "show";
	}

	@Override
	public String getShortHelp() {
		return "show instance timer(s)";
	}

	@Override
	public String getUsage() {
		return "ui instance timer show {#}";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{

		if (this.dwProto == null)
		{
			if (DriveWireServer.isValidHandlerNo(this.uiref.getInstance()))
				dwProto = DriveWireServer.getHandler(this.uiref.getInstance());
			else
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_WONT ,"The operation is not supported by this instance"));
		}
		
		if (cmdline.length() == 0)
		{
			String txt = "";
			
			for (int i = 0;i < 256;i++)
			{
				if (dwProto.getTimers().getTimer((byte) i) > 0 )
				{
					txt += getTimerData((byte) i) + "\r\n";
				}
			}
			
			return(new DWCommandResponse(txt));
		}	
		else
		{
			String[] args = cmdline.split(" ");
			
			try
			{
				byte tno = (byte) Integer.parseInt(args[0]);
				
				return(new DWCommandResponse(getTimerData(tno)));
			}
			catch (NumberFormatException e)
			{
				return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR , "Timer # must be 0-255"));
			}
			
			
		}
		
	
	}

	private String getTimerData(byte tno)
	{
		return( (tno & 0xff) + "|" + DWUtils.prettyTimer(tno) +"|" + dwProto.getTimers().getTimer(tno));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
