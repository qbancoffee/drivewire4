package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class UICmdInstancePortStatus extends DWCommand {

	private DWUIClientThread dwuithread = null;
	
	private DWVSerialProtocol gproto;

	public UICmdInstancePortStatus(DWUIClientThread dwuiClientThread) 
	{
		this.dwuithread = dwuiClientThread;
	}


	public UICmdInstancePortStatus(DWVSerialProtocol dwProto) 
	{
		this.gproto = dwProto;
	}


	@Override
	public String getCommand() 
	{
		return "portstatus";
	}


	@Override
	public String getShortHelp() {
		return "show port status";
	}

	@Override
	public String getUsage() {
		return "ui instance portstatus";
	}

	@Override
	public DWCommandResponse parse(String cmdline) 
	{
		String res = "";
		
		if (this.gproto == null)
		{
			if  ((DriveWireServer.isValidHandlerNo(this.dwuithread.getInstance()) && (DriveWireServer.getHandler(this.dwuithread.getInstance()).hasVSerial())))
			{
				gproto = (DWVSerialProtocol) DriveWireServer.getHandler(this.dwuithread.getInstance());
			}
			else
				return(new DWCommandResponse(false,DWDefs.RC_INSTANCE_WONT ,"The operation is not supported by this instance"));
		}
	
		
		if (!(gproto == null) && !(gproto.getVPorts() == null) )
		{
			
			
			for (int p = 0;p < gproto.getVPorts().getMaxPorts();p++)
			{
				if (!gproto.getVPorts().isNull(p))
				{
					try
					{
						res += gproto.getVPorts().prettyPort(p) + "|";
						
						if (gproto.getVPorts().isOpen(p))
						{
							res += "open|";
							
							res += gproto.getVPorts().getOpen(p) + "|";
							
							res += gproto.getVPorts().getUtilMode(p) + "|";
							
							res += DWUtils.prettyUtilMode(gproto.getVPorts().getUtilMode(p)) + "|";
							
							res += gproto.getVPorts().bytesWaiting(p)  + "|";
							
							res += gproto.getVPorts().getConn(p) + "|";
							
							if (gproto.getVPorts().getConn(p) > -1)
							{
								try
								{
									res += gproto.getVPorts().getHostIP(p) + "|";
									res += gproto.getVPorts().getHostPort(p) + "|";
									
								} 
								catch (DWConnectionNotValidException e)
								{
									res += "||";
								}
							}
							else
								res += "||";
							
							
							
							res += new String(gproto.getVPorts().getDD(p)) + "|";
							
							
						}
						else
						{
							res += "closed|";
						}
					}
					catch (DWPortNotValidException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					res += "\r\n";
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
