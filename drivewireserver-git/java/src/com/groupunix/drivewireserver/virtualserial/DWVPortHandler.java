package com.groupunix.drivewireserver.virtualserial;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.virtualserial.api.DWAPISerial;
import com.groupunix.drivewireserver.virtualserial.api.DWAPITCP;



public class DWVPortHandler 
{
	private static final Logger logger = Logger.getLogger("DWServer.DWVPortHandler");
	
	private String port_command = new String();
	private int vport;
	private DWVModem vModem;
	private Thread utilthread;
	private DWVSerialPorts dwVSerialPorts;
//	private	DWVSerialCircularBuffer inputBuffer = new DWVSerialCircularBuffer(1024, true);
	private DWVSerialProtocol dwProto;

	
	public DWVPortHandler(DWVSerialProtocol dwProto, int port) 
	{
		this.vport = port;	
		this.vModem = new DWVModem(dwProto, port);
		this.dwProto = dwProto;
		this.dwVSerialPorts = dwProto.getVPorts();
		
		//logger.debug("init handler for port " + port);
	}

	public void takeInput(int databyte) 
	{
		
		
		// echo character if modem echo is on
		if (this.vModem.isEcho())
		{
			try
			{
				dwVSerialPorts.writeToCoco(this.vport, (byte) databyte);
				
				// send extra lf on cr, not sure if this is right
				if (databyte == this.vModem.getCR())
				{
					dwVSerialPorts.writeToCoco(this.vport,(byte) this.vModem.getLF());
				} 
				
			} 
			catch (DWPortNotValidException e)
			{
				logger.warn("in takeinput: " + e.getMessage());
			}
					
			

		}
		
		
	
		
		// process command if enter
		if (databyte == this.vModem.getCR())
		{
			logger.debug("port command '" + port_command + "'");
				
			processCommand(port_command.trim());
				
			this.port_command = "";
		}
		else
		{
			// add character to command
			// handle backspace
			if ((databyte == this.vModem.getBS()) && (this.port_command.length() > 0))
			{
				this.port_command = this.port_command.substring(0, this.port_command.length() - 1);
			}
			else if (databyte > 0)
			{
				// is this really the easiest way to append a character to a string??  
				this.port_command += Character.toString((char) databyte);
				
				// check for os9 window wcreate:1b 20 + (valid screen type: ff,0,1,2,5,6,7,8)   
				
				
			}
				
		}
			
	}


	
	private void processCommand(String cmd) 
	{
		// hitting enter on a blank line is ok
		if (cmd.length() == 0)
		{
			return;
		}
		
		// anything beginning with AT or A/ is a modem command
		if ((cmd.toUpperCase().startsWith("AT")) || (cmd.toUpperCase().startsWith("A/")))
		{
			this.vModem.processCommand(cmd);
		}
		else
		{
			processAPICommand(cmd);
		}
		
	}

	private void processAPICommand(String cmd) 
	{
	// new API based implementation 1/2/10
		
		
		
		String[] cmdparts = cmd.split("\\s+");
		
		if (cmdparts.length > 0)
		{
			if (cmdparts[0].equalsIgnoreCase("tcp"))
			{
				respond(new DWAPITCP(cmdparts, this.dwProto, this.vport).process());
			}
			else if (cmdparts[0].equalsIgnoreCase("ser"))
			{
				respond(new DWAPISerial(cmdparts, this.dwVSerialPorts, this.vport).process());
			}


			else if (cmdparts[0].equalsIgnoreCase("dw") || cmdparts[0].equalsIgnoreCase("ui"))
			{
				// start DWcmd thread
				
				this.utilthread = new Thread(new DWUtilDWThread(this.dwProto, this.vport, cmd));
				this.utilthread.start();
			}
			
			else if (cmdparts[0].equalsIgnoreCase("log"))
			{
				// log entry
				logger.info("coco " + cmd);
			}
			else
			{
				logger.warn("Unknown API command: '" + cmd + "'");
				respondFail(DWDefs.RC_SYNTAX_ERROR, "Unknown API '" + cmdparts[0] + "'");
			}
		}
		else
		{
			logger.debug("got empty command?");
			respondFail(DWDefs.RC_SYNTAX_ERROR,"Syntax error: no command?");
		}
			
	}


	
	
	public void respond(DWCommandResponse cr) 
	{
		if (cr.getSuccess())
			respondOk(cr.getResponseText());
		else
			respondFail(cr.getResponseCode(), cr.getResponseText());
	}

	

	public void respondOk(String txt) 
	{
		try 
		{
			dwVSerialPorts.writeToCoco(this.vport, "OK " + txt + (char) 13);
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
	}
	
	
	public void respondFail(byte errno, String txt) 
	{
		String perrno = String.format("%03d", errno & 0xFF);
		logger.debug("command failed: " + perrno + " " + txt);
		try 
		{
			dwVSerialPorts.writeToCoco(this.vport, "FAIL " + perrno + " " + txt + (char) 13);
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
	}
	
	public synchronized void announceConnection(int conno, int localport, String hostaddr)
	{
		try 
		{
			dwVSerialPorts.writeToCoco(this.vport, conno + " " + localport + " " +  hostaddr + (char) 13);
		} 
		catch (DWPortNotValidException e) 
		{
		
			logger.error(e.getMessage());
		}		
	}

	public DWVModem getVModem() 
	{
		return this.vModem;
	}
	
}
