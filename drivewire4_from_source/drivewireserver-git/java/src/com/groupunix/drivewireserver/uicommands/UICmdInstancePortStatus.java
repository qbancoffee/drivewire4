package com.groupunix.drivewireserver.uicommands;

import java.util.Iterator;

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
				if (!gproto.getVPorts().isNull(p) && (p != gproto.getVPorts().getMaxNPorts()-1) )
				{
					try
					{
						if (p < gproto.getVPorts().getMaxNPorts())
							res += "N|";
						else
							res += "Z|"; 
							
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
							
							res += gproto.getVPorts().getPD_INT(p) + "|";
							res += gproto.getVPorts().getPD_QUT(p);
							
							// res += new String(gproto.getVPorts().getDD(p));
							
							
						}
						else
						{
							res += "closed";
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
		
		/*
		Iterator<DWUIClientThread> itr = DriveWireServer.getDWUIThread().getUIClientThreads().iterator(); 
		
		while(itr.hasNext()) 
		{	
			DWUIClientThread client = itr.next();
			
			// filter for instance
			if ((client.getInstance() == -1) || (client.getInstance() == this.dwuithread.getInstance()))
			{
				res += "U|" + client.getInstance() + "|" + client.getState() + "|" + client.getCurCmd() + "|" + client.getSocket().getInetAddress().getHostAddress() + "|" + client.getSocket().getPort();
				res += "\r\n";
				
			}
		}
		*/
		
		return(new DWCommandResponse(res));
	}

	public boolean validate(String cmdline) 
	{
		return(true);
	}
}
