package com.groupunix.drivewireserver.uicommands;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWEvent;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

public class UICmdSync extends DWCommand {

	static final String command = "sync";
		
	private static final Logger logger = Logger.getLogger("DWServer.DWUtilUIThread");
	
	private DWUIClientThread dwuiref;
	private DWEvent lastevt = new DWEvent((byte) 0, -1);
	
	public UICmdSync(DWUIClientThread dwuiClientThread) 
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
		
		logger.debug("adding status sync client");
		
		try 
		{
			dwuiref.getOutputStream().write(13);
			
			// bring client up to date..
			
			sendEvent(DriveWireServer.getServerStatusEvent());
			
			/*
			for (DWEvent e : DriveWireServer.getLogCache())
			{
				sendEvent(e);
			}
			*/
			
		} 
		catch (IOException e1) 
		{
			logger.debug("immediate I/O error: " + e1.getMessage());
			wanttodie = true;
		}
		
		// set event filter
		this.dwuiref.setEventFilter(new byte[] { DWDefs.EVENT_TYPE_DISK, DWDefs.EVENT_TYPE_STATUS, DWDefs.EVENT_TYPE_LOG, DWDefs.EVENT_TYPE_INSTANCECONFIG, DWDefs.EVENT_TYPE_SERVERCONFIG, DWDefs.EVENT_TYPE_MIDI  });
	
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
		
		logger.debug("removing status sync client");
			
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
		return "Sync status (real time)";
	}


	public String getUsage() 
	{
		return "ui sync";
	}
	
	public boolean validate(String cmdline) 
	{
		return(true);
	}
	
}