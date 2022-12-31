package com.groupunix.drivewireserver.virtualserial.api;

import java.io.IOException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.virtualserial.DWVSerialPort;
import com.groupunix.drivewireserver.virtualserial.DWVSerialPorts;

public class DWAPISerial {

	
	private String[] command;
	private DWVSerialPorts dwVSerialPorts;
	private int vport;

	public DWAPISerial(String[] cmd, DWVSerialPorts dwVSerialPorts, int vport)
	{
		this.vport = vport;
		this.dwVSerialPorts = dwVSerialPorts;
		this.setCommand(cmd);
	}

	public DWAPISerial(String[] cmdparts, int vport2) {
		// TODO Auto-generated constructor stub
	}

	public DWCommandResponse process() 
	{
		if ((command.length > 2) && ((command.length & 1) == 1))
		{
			if (command[1].equals("join"))
				return(doCommandJoin(command));
		}
		if (command.length > 2)
		{
			if (command[1].equals("show"))
				return(doCommandShow(command[2]));
		}
		else if (command.length > 1)
		{
			if (command[1].equals("devs"))
				return(doCommandDevs());
		}
		return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax Error");
	}

	
	
	
	
	private DWCommandResponse doCommandJoin(String[] cmd) 
	{
		// validate
		String port = cmd[2];
		DWAPISerialPortDef spd = new DWAPISerialPortDef();
		
		for (int i = 3;i<cmd.length;i+=2)
		{
			String item = cmd[i];
			String val = cmd[i+1];
			int valno = -1;
			try
			{
				valno = Integer.parseInt(val);
			}
			catch (NumberFormatException e)
			{
				
			}
			
			
			// bps rate
			if (item.equalsIgnoreCase("r") &&  isInteger(val))
			{
				spd.setRate(valno);
			}
			// stop bits
			else if (item.equalsIgnoreCase("sb") && isInteger(val))
			{
				if (valno == 1)
					spd.setStopbits(SerialPort.STOPBITS_1);
				else if (valno == 2)
					spd.setStopbits(SerialPort.STOPBITS_2);
				else if (valno == 5)
					spd.setStopbits(SerialPort.STOPBITS_1_5);
				else
					return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg sb (1,2 or 5 is valid)");
			}
			// parity
			else if (item.equalsIgnoreCase("p") && val.length() == 1)
			{
				if (val.equalsIgnoreCase("N"))
					spd.setParity(SerialPort.PARITY_NONE);
				else if (val.equalsIgnoreCase("E"))
					spd.setParity(SerialPort.PARITY_EVEN);
				else if (val.equalsIgnoreCase("O"))
					spd.setParity(SerialPort.PARITY_ODD);
				else if (val.equalsIgnoreCase("M"))
					spd.setParity(SerialPort.PARITY_MARK);
				else if (val.equalsIgnoreCase("S"))
					spd.setParity(SerialPort.PARITY_SPACE);
				else
					return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg p (N,E,O,M or S is valid)");
				 
			}
			// data bits
			else if (item.equalsIgnoreCase("db") && isInteger(val))
			{
				if (valno == 5)
					spd.setDatabits(SerialPort.DATABITS_5);
				else if (valno == 6)
					spd.setDatabits(SerialPort.DATABITS_6);
				else if (valno == 7)
					spd.setDatabits(SerialPort.DATABITS_7);
				else if (valno == 8)
					spd.setDatabits(SerialPort.DATABITS_8);
				else
					return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg db (5,6,7 or 8 is valid)");
				
			}
			else if (item.equalsIgnoreCase("fc"))
			{
				int fc = 0;
				
				for (byte b : val.getBytes())
				{
					if (b == 'r')
						fc += SerialPort.FLOWCONTROL_RTSCTS_OUT;
					else if (b == 'R')
						fc += SerialPort.FLOWCONTROL_RTSCTS_IN;
					else if (b == 'x')
						fc += SerialPort.FLOWCONTROL_XONXOFF_OUT;
					else if (b == 'X')
						fc += SerialPort.FLOWCONTROL_XONXOFF_IN;
					else if (b == 'n')
						fc += SerialPort.FLOWCONTROL_NONE;
					else
						return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg fc (R,r,X,x or n is valid)");
					
				}
				
				spd.setFlowcontrol(fc);
			}
		}
		CommPort commPort = null;
		
		try
		{
			
			CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(port);
			
			if (pi.isCurrentlyOwned())
			{
				return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTINUSE, "Port in use");
			}
			
			commPort = pi.open("DriveWireServer",2000);
					
					
			if  (!( commPort instanceof SerialPort ))
			{
				return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTINVALID, "Invalid port");
			}
			
			final SerialPort sp = (SerialPort) commPort;
			
			spd.setParams(sp);
			
			// join em
			
			Thread inputT = new Thread(new Runnable(){

				
				public void run() 
				{
					boolean wanttodie = false;
					dwVSerialPorts.markConnected(vport);
					
					while (!wanttodie)
					{
						int databyte = -1;
						
						try 
						{
							databyte = dwVSerialPorts.getPortOutput(vport).read();
							System.out.println("input: " + databyte);
						} 
						catch (IOException e) 
						{
							wanttodie = true;
						} 
						catch (DWPortNotValidException e) 
						{
							wanttodie = true;
						}
							
						if (databyte == -1)
							wanttodie = true;
						else
						{
							try 
							{
								sp.getOutputStream().write(databyte);
							} 
							catch (IOException e) 
							{
								wanttodie = true;
							}
						}
						
					}
							
				}});
			
			inputT.setDaemon(true);	
			inputT.start();
			
			
			Thread outputT = new Thread(new Runnable(){

				
				public void run() 
				{
					boolean wanttodie = false;
					
					while (!wanttodie)
					{
						int databyte = -1;
						
						try 
						{
							
							databyte = sp.getInputStream().read();
							System.out.println("output: " + databyte);
						} 
						catch (IOException e) 
						{
							wanttodie = true;
						} 
							
						if (databyte != -1)
						{
							try 
							{
								dwVSerialPorts.writeToCoco(vport, (byte)(databyte & 0xff));
							} 
							catch (DWPortNotValidException e) 
							{
								wanttodie = true;
							}
						}
						
					}
							
				}});
			
			outputT.setDaemon(true);	
			outputT.start();
			
			return new DWCommandResponse("Connect to " + port);
			
		}
		catch (Exception e)
		{
			if (commPort != null)
				commPort.close();
			return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, e.getClass().getSimpleName());
		}
		
	}

	private DWCommandResponse doCommandShow(String port) 
	{
		String res = "";
		boolean ok = true;
		
		try
		{
			CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(port);
			
			if (pi.isCurrentlyOwned())
			{
				return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTINUSE, "In use");
			}
			else
			{
				CommPort commPort = null;
				
				try
				{
					
					commPort = pi.open("DriveWireServer",2000);
					
					if ( commPort instanceof SerialPort )
					{
						SerialPort sp = (SerialPort) commPort;
						
	            		res = sp.getBaudRate() + "|" + sp.getDataBits() + "|";
	            		
	            		if (sp.getParity() == SerialPort.PARITY_EVEN)
	            			res += "E";
	            		else if (sp.getParity() == SerialPort.PARITY_ODD)
	            			res += "O";
	            		else if (sp.getParity() == SerialPort.PARITY_NONE)
	            			res += "N";
	            		else if (sp.getParity() == SerialPort.PARITY_MARK)
	            			res += "M";
	            		else 
	            			res += "S";
	            		
	            		res += "|";
	            		
	            		if (sp.getStopBits() == SerialPort.STOPBITS_1)
	            			res += "1";
	            		else if (sp.getStopBits() == SerialPort.STOPBITS_2)
	            			res += "2";
	            		else 
	            			res += "5";
	            		
	            		res += "|";
	            		
	            		if ((sp.getFlowControlMode() & SerialPort.FLOWCONTROL_RTSCTS_IN) == SerialPort.FLOWCONTROL_RTSCTS_IN)
	            			res += "R";
	            		
	            		if ((sp.getFlowControlMode() & SerialPort.FLOWCONTROL_XONXOFF_IN) == SerialPort.FLOWCONTROL_XONXOFF_IN)
	            			res += "X";
	                	
	            		if ((sp.getFlowControlMode() & SerialPort.FLOWCONTROL_RTSCTS_OUT) == SerialPort.FLOWCONTROL_RTSCTS_OUT)
	            			res += "r";
	            		
	            		if ((sp.getFlowControlMode() & SerialPort.FLOWCONTROL_XONXOFF_OUT) == SerialPort.FLOWCONTROL_XONXOFF_OUT)
	            			res += "x";
	                	
	            		res += "|";
	            		
	            		if (sp.isCD())
	            			res += "CD ";
	            		
	            		if (sp.isCTS())
	            			res += "CTS ";
	            		
	            		if (sp.isDSR())
	            			res += "DSR ";
	            		
	            		if (sp.isDTR())
	            			res += "DTR ";
	            		
	            		if (sp.isRI())
	            			res += "RI ";
	            		
	            		if (sp.isRTS())
	            			res += "RTS ";
	            		
	            		res = res.trim();
					}
					else
					{
						ok = false;
						res = "Invalid port";
					}
				}
				catch (Exception e)
				{
					ok = false;
					res = e.toString();
				}
				finally
				{
					if (commPort != null)
						commPort.close();
				}
			}
			
		} 
		catch (Exception e)
		{
			ok = false;
			res = e.getClass().getSimpleName();
		}
		
		if (ok)
			return new DWCommandResponse(res);
		else
			return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, res);
	}

	private DWCommandResponse doCommandDevs() 
	{
		String res = "";
		
		for (String p : DriveWireServer.getAvailableSerialPorts())
		{
			if (!res.equals(""))
				res +="|";
			res += p;
		}
		
		return new DWCommandResponse(res);
	}

	public String[] getCommand() {
		return command;
	}

	public void setCommand(String[] command) {
		this.command = command;
	}
	
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	
	
	
}
