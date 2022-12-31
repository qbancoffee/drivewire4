package com.groupunix.drivewireserver.virtualserial.api;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.virtualserial.DWVPortTCPConnectionThread;
import com.groupunix.drivewireserver.virtualserial.DWVPortTCPListenerThread;
import com.groupunix.drivewireserver.virtualserial.DWVPortTCPServerThread;

public class DWAPITCP 
{
	private String[] cmdparts;
	private DWVSerialProtocol dwProto;
	private int vport;



	public DWAPITCP(String[] cmd, DWVSerialProtocol dwProto, int vport)
	{
		this.dwProto = dwProto;
		this.vport = vport;
		this.cmdparts = cmd;
	}
	
	
	
	
	public DWCommandResponse process()
	{
		if ((cmdparts.length == 4) && (cmdparts[1].equalsIgnoreCase("connect"))) 
		{
			return doTCPConnect(cmdparts[2],cmdparts[3]);
		}
		else if ((cmdparts.length >= 3) && (cmdparts[1].equalsIgnoreCase("listen"))) 
		{
			return doTCPListen(cmdparts);
		}
		// old
		else if ((cmdparts.length == 3) && (cmdparts[1].equalsIgnoreCase("listentelnet"))) 
		{
			return doTCPListen(cmdparts[2],1);
		}
		else if ((cmdparts.length == 3) && (cmdparts[1].equalsIgnoreCase("join"))) 
		{
			return doTCPJoin(cmdparts[2]);
		}
		else if ((cmdparts.length == 3) && (cmdparts[1].equalsIgnoreCase("kill"))) 
		{
			return doTCPKill(cmdparts[2]);
		}
		
		return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"Syntax error in TCP command");
		
	}

	
	
	private DWCommandResponse doTCPJoin(String constr) 
	{
		int conno;
		
		try
		{
			conno = Integer.parseInt(constr);
		}
		catch (NumberFormatException e)
		{
			return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"non-numeric port in tcp join command");
		}
		

		try 
		{
			this.dwProto.getVPorts().getListenerPool().validateConn(conno);
			// start TCP thread
			Thread utilthread = new Thread(new DWVPortTCPServerThread(this.dwProto, this.vport, conno));
			utilthread.start();
			
			return new DWCommandResponse("attaching to connection " + conno);
		} 
		catch (DWConnectionNotValidException e) 
		{
			return new DWCommandResponse(false, DWDefs.RC_NET_INVALID_CONNECTION,"invalid connection number");
		}
		
		
		
	}
	
	private DWCommandResponse doTCPKill(String constr) 
	{
		int conno;
		
		try
		{
			conno = Integer.parseInt(constr);
		}
		catch (NumberFormatException e)
		{
			return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"non-numeric port in tcp kill command");
		}
		
		// close socket
		try 
		{
			this.dwProto.getVPorts().getListenerPool().killConn(conno);
			return new DWCommandResponse("killed connection " + conno);
		} 
		catch (DWConnectionNotValidException e) 
		{
			return new DWCommandResponse(false, DWDefs.RC_NET_INVALID_CONNECTION,"invalid connection number");
		}
		
		
	}
	



	private DWCommandResponse doTCPConnect(String tcphost, String tcpportstr) 
	{
		int tcpport;
		
		// get port #
		try
		{
			tcpport = Integer.parseInt(tcpportstr);
		}
		catch (NumberFormatException e)
		{
			return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"non-numeric port in tcp connect command");
		}
		
		// respondOk("connecting");
		
		// start TCP thread
		Thread utilthread = new Thread(new DWVPortTCPConnectionThread(this.dwProto, this.vport, tcphost, tcpport));
		utilthread.start();
		
		return new DWCommandResponse("connected to " + tcphost + ":" + tcpportstr);
	}
	
	private DWCommandResponse doTCPListen(String strport, int mode)
	{
		int tcpport;
		
		// get port #
		try
		{
			tcpport = Integer.parseInt(strport);
		}
		catch (NumberFormatException e)
		{
			return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"non-numeric port in tcp listen command");
		}
		
		DWVPortTCPListenerThread listener = new DWVPortTCPListenerThread(this.dwProto, this.vport, tcpport);
		
		
		// simulate old behavior
		listener.setMode(mode);
		listener.setDo_banner(true);
		listener.setDo_telnet(true);
		
		// start TCP listener thread
		Thread listenThread = new Thread(listener);
		listenThread.start();
		
		return new DWCommandResponse("listening on port " + tcpport);
	}
	
	
	private DWCommandResponse doTCPListen(String[] cmdparts) 
	{
		int tcpport;
		
		// get port #
		try
		{
			tcpport = Integer.parseInt(cmdparts[2]);
		}
		catch (NumberFormatException e)
		{
			return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR,"non-numeric port in tcp listen command");
		}
		
		DWVPortTCPListenerThread listener = new DWVPortTCPListenerThread(this.dwProto, this.vport, tcpport);
				
		// parse options
		if (cmdparts.length > 3)
		{
			for (int i = 3;i<cmdparts.length;i++)
			{
				if (cmdparts[i].equalsIgnoreCase("telnet"))
				{
					listener.setDo_telnet(true);
				}
				else if (cmdparts[i].equalsIgnoreCase("httpd"))
				{
					listener.setMode(2);
				}
				else if (cmdparts[i].equalsIgnoreCase("banner"))
				{
					listener.setDo_banner(true);
				}
				
			}
				
		}
		
		// start TCP listener thread
		Thread listenThread = new Thread(listener);
		listenThread.start();
		
		return new DWCommandResponse("listening on port " + tcpport);
	}
	
	
	
	
}
