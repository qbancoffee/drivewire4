package com.groupunix.drivewireserver.uicommands;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWEvent;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdInstanceVPortSniff extends DWCommand {

	static final String command = "vportsniffer";
		
	private static final Logger logger = Logger.getLogger("DWServer.DWUtilUIThread");
	
	private DWUIClientThread dwuiref;

	private DWEvent lastevt = new DWEvent((byte) 0, -1);
	
	
	public UICmdInstanceVPortSniff(DWUIClientThread dwuiClientThread) 
	{
		this.dwuiref = dwuiClientThread;
	}

	public String getCommand() 
	{
		return command;
	}

	public DWCommandResponse parse(String cmdline)
	{
		boolean wanttodie = false;
		
		logger.debug("adding vportsniff client");
		
		
		this.dwuiref.setEventFilter(new byte[] { DWDefs.EVENT_TYPE_VSERIAL });
		
		while ((wanttodie == false) && (!dwuiref.getSocket().isClosed()))
		{
			try 
			{	
				sendEvent(this.dwuiref.getEventQueue().take());
			} 
			catch (InterruptedException e) 
			{
				wanttodie = true;
			} 
			catch (IOException e) 
			{
				wanttodie = true;
			}
			
		}
		
		logger.debug("removing vport sniff client");
			
		return(new DWCommandResponse(false, DWDefs.RC_FAIL, "Sync closed"));
	}

	
	private void sendEvent(DWEvent msg) throws IOException
	{
		for (String key : msg.getParamKeys())
		{
			
			// only send changed params 
			if (!lastevt.hasParam(key) || !lastevt.getParam(key).equals(msg.getParam(key)))
			{
				dwuiref.getOutputStream().write( (key + ':' + msg.getParam(key) ).getBytes());
				dwuiref.getOutputStream().write(13);
				lastevt.setParam(key, msg.getParam(key));
			}
		}
		
		dwuiref.getOutputStream().write(msg.getEventType());
		dwuiref.getOutputStream().write(13);
		dwuiref.getOutputStream().flush();
	}
	

	public String getShortHelp() 
	{
		return "Vport Sniffer (real time)";
	}


	public String getUsage() 
	{
		return "ui instance vportsniff";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}