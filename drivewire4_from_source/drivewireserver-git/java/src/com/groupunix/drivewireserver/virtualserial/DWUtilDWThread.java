package com.groupunix.drivewireserver.virtualserial;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwcommands.DWCmd;
import com.groupunix.drivewireserver.dwcommands.DWCommandList;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.uicommands.UICmd;

public class DWUtilDWThread implements Runnable 
{

	private static final Logger logger = Logger.getLogger("DWServer.DWUtilDWThread");
	
	private int vport = -1;
	private String strargs = null;
	private DWVSerialPorts dwVSerialPorts;
	private boolean protect = false;
	
	private DWCommandList commands;
	
	public DWUtilDWThread(DWVSerialProtocol dwProto, int vport, String args)
	{
		this.vport = vport;
		this.strargs = args;
		this.dwVSerialPorts = dwProto.getVPorts();
		
		if (vport <= this.dwVSerialPorts.getMaxPorts())
		{
			this.protect = dwProto.getConfig().getBoolean("ProtectedMode", false); 
		}
		

		commands = new DWCommandList(dwProto, dwProto.getCMDCols());
		commands.addcommand(new DWCmd(dwProto));
		commands.addcommand(new UICmd(dwProto));
		
		logger.debug("init dw util thread (protected mode: " + this.protect + ")");	
	}
	
	
	
	public void run() 
	{
		
		Thread.currentThread().setName("dwutil-" + Thread.currentThread().getId());
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		//logger.debug("run for port " + vport);
		
		try
		{
			this.dwVSerialPorts.markConnected(vport);
			
			if (this.strargs.toLowerCase().startsWith("dw"))
				this.dwVSerialPorts.setUtilMode(this.vport, DWDefs.UTILMODE_DWCMD);
			else if (this.strargs.toLowerCase().startsWith("ui"))
				this.dwVSerialPorts.setUtilMode(this.vport, DWDefs.UTILMODE_UICMD);
			else
				this.dwVSerialPorts.setUtilMode(this.vport, DWDefs.UTILMODE_UNSET);
				
			
			DWCommandResponse resp = commands.parse(this.strargs);
			
			if (resp.getSuccess())
			{
				if (resp.isUsebytes())
				{
					dwVSerialPorts.sendUtilityOKResponse(this.vport, resp.getResponseBytes());
				}
				else
				{
					dwVSerialPorts.sendUtilityOKResponse(this.vport, resp.getResponseText());
				}
			}
			else
			{
				dwVSerialPorts.sendUtilityFailResponse(this.vport, resp.getResponseCode(), resp.getResponseText());
			}
			
		// wait for output to flush
			while ((dwVSerialPorts.bytesWaiting(this.vport) > 0) && (dwVSerialPorts.isOpen(this.vport)))
			{
				//logger.debug("pause for the cause: " + dwVSerialPorts.bytesWaiting(this.vport) + " bytes left" );
				Thread.sleep(250);
			}
			
			if (this.vport < this.dwVSerialPorts.getMaxPorts())
			{
				dwVSerialPorts.closePort(this.vport);
			}	
			
		} 
		catch (InterruptedException e) 
		{
			logger.error(e.getMessage());
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
		
		
		logger.debug("exiting");
		
	}

	
	





	
}
