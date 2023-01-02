package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.virtualserial.DWVPortTCPConnectionThread;

public class DWCmdPortOpen extends DWCommand {

	private DWVSerialProtocol dwProto;

	public DWCmdPortOpen(DWVSerialProtocol dwProtocol,DWCommand parent)
	{
		setParentCmd(parent);
		this.dwProto = dwProtocol;
	}
	
	public String getCommand() 
	{
		return "open";
	}

	
	public String getShortHelp() 
	{
		return "Connect port # to tcp host:port";
	}


	public String getUsage() 
	{
		return "dw port open port# host:port";
	}

	public DWCommandResponse parse(String cmdline) 
	{
		
		String[] args = cmdline.split(" ");
		
		if (args.length < 2)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"dw port open requires a port # and tcphost:port as an argument"));

		}
		else
		{
			return(doPortOpen(args[0],args[1]));
		}
	}

	
	private DWCommandResponse doPortOpen(String port,String hostport)
	{
		int portno = 0;
		int tcpport = 0;
		String tcphost = new String();
		
		try
		{
			portno = Integer.parseInt(port);
		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric port #"));
		} 		
	
		String[] tcpargs = hostport.split(":");
		
		try
		{
			tcpport = Integer.parseInt(tcpargs[1]);
			tcphost = tcpargs[0];
			
			dwProto.getVPorts().openPort(portno);
			
			Thread cthread = new Thread(new DWVPortTCPConnectionThread(this.dwProto, portno, tcphost, tcpport, false));
			cthread.start();
			
			return(new DWCommandResponse("Port #"+ portno + " open."));
		
			
		}
		catch (NumberFormatException e)
		{
			return(new DWCommandResponse(false,DWDefs.RC_SYNTAX_ERROR,"Syntax error: non numeric tcp port"));
		} 
		catch (DWPortNotValidException e) 
		{
			return(new DWCommandResponse(false,DWDefs.RC_INVALID_PORT,e.getMessage()));
		} 
		
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}

}
