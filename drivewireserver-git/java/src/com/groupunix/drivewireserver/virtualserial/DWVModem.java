package com.groupunix.drivewireserver.virtualserial;

import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;



public class DWVModem {

	private static final Logger logger = Logger.getLogger("DWServer.DWVModem");
	
	private int vport;
	
	// modem state
	private int[] vmodem_registers = new int[256];
	private String vmodem_lastcommand = new String();
	private String vmodem_dialstring = new String();
	
	// modem constants
	private static final int RESP_OK = 0;
	private static final int RESP_CONNECT = 1;
	private static final int RESP_RING = 2;
	private static final int RESP_NOCARRIER = 3;
	private static final int RESP_ERROR = 4;
	private static final int RESP_NODIALTONE = 6;
	private static final int RESP_BUSY = 7;
	private static final int RESP_NOANSWER = 8;
	
	// registers we use
	private static final int REG_ANSWERONRING = 0;
	private static final int REG_RINGS = 1;
	private static final int REG_ESCCHAR = 2;
	private static final int REG_CR = 3;
	private static final int REG_LF = 4;
	private static final int REG_BS = 5;
	private static final int REG_GUARDTIME = 12;
	
	private static final int REG_LISTENPORTHI = 13;
	private static final int REG_LISTENPORTLO = 14;

	private static final int REG_ECHO = 15;
	private static final int REG_VERBOSE = 16;
	private static final int REG_QUIET = 17;
	
	private static final int REG_DCDMODE = 18;
	private static final int REG_DSRMODE = 19;
	private static final int REG_DTRMODE = 20;
	
	
	private Thread tcpthread;

	private DWVSerialPorts dwVSerialPorts;

	private DWVSerialProtocol dwProto;

	private Thread listenThread;
	
	
	public DWVModem(DWVSerialProtocol dwProto, int port) 
	{
		this.vport = port;
		this.dwProto = dwProto;
		
		this.dwVSerialPorts = dwProto.getVPorts();
		// logger.debug("new vmodem for port " + port);
		
		doCommandReset(1);
	}

	
	public void processCommand(String cmd) 
	{	
		
		int errors = 0;
		
		// hitting enter on a blank line is ok
		if (cmd.length() == 0)
		{
			return;
		}
		
		// A/ repeats last command
		if (cmd.equalsIgnoreCase("A/"))
		{
			cmd = this.vmodem_lastcommand;
		}
		else
		{
			this.vmodem_lastcommand = cmd;
		}
		
		// must start with AT
		if (cmd.toUpperCase().startsWith("AT"))
		{
			// AT by itself is OK
			if (cmd.length() == 2)
			{
				sendResponse(RESP_OK);
				return;
			}
			
			// process the string looking for commands
			
			
			boolean registers = false;
			boolean extended = false;
			boolean extendedpart = false;
			boolean dialing = false;
			
			String thiscmd = new String();
			String thisarg = new String();
			String thisreg = new String();
			
			for (int i = 2;i<cmd.length();i++)
			{
				extendedpart = false;
				
				if (dialing)
				{
					thisarg += cmd.substring(i, i+1);
				}
				else
				{
					switch(cmd.toUpperCase().charAt(i))
					{
						// commands
						case 'E':
						case '&':
						case 'Q':
						case 'Z':
						case 'I':
						case 'S':
						case 'B':
						case 'L':
						case 'M':
						case 'N':
						case 'X':
						case 'V':
						case 'F':
						case 'D':
						case 'C':
						case 'A':
							
							// handle extended mode
							if (extended)
							{
								switch(cmd.toUpperCase().charAt(i))
								{
									case 'V':
									case 'F':
									case 'C':
									case 'D':
									case 'S':
									case 'Z':
									case 'A':
										extendedpart = true;
										break;
								}
							}
							
							if (cmd.toUpperCase().charAt(i) == '&')
								extended = true;
							
							if ((!extended) && cmd.toUpperCase().charAt(i) == 'D')
								dialing = true;
													
							if (extendedpart)
							{
								thiscmd += cmd.substring(i, i+1);
							}
							else
							{
								// terminate existing command if any
								if (!(thiscmd.length() == 0))
								{
									errors += doCommand(thiscmd, thisreg, thisarg);
								}
							
								// set up for new command
								thiscmd = cmd.substring(i, i+1);
								thisarg = new String();
								thisreg = new String();
								
								
								// registers
								if (thiscmd.equalsIgnoreCase("S"))
									registers = true;
								else
									registers = false;
								
							}
							break;

						// assignment
						case '=':
							registers = false;
							break;
							
						// query
						case '?':
							thisarg = "?";
							break;
							
						// ignored
						case ' ':	
							break;
							
						// digits	
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							if (registers)
							{
								thisreg += cmd.substring(i, i+1);
							}
							else
							{
								thisarg += cmd.substring(i, i+1);
							}
							break;
						
						default:
							// unknown command/bad syntax
							errors++;
							break;
					}
							
				}
			}
			
			// last/only command in string
			if (!(thiscmd.length() == 0))
			{
				errors += doCommand(thiscmd, thisreg, thisarg);
			}
			
		}
		else
		{
			errors++;
		}
		
		// send response
		if (errors >= 0)
		{
			if (errors > 0)
			{
				sendResponse(RESP_ERROR);
			}
			else
			{
				sendResponse(RESP_OK);
			}
		}
		
	}

	private int doCommand(String thiscmd, String thisreg, String thisarg) 
	{
		int errors = 0;
		int val = 0;
		int regval = 0;
		
		
		// convert arg and reg to int values
		try 
		{
			val = Integer.parseInt(thisarg);
			
		}
		catch (NumberFormatException e)
		{
			// we don't care
		}
		
		try 
		{
			regval = Integer.parseInt(thisreg);
		}
		catch (NumberFormatException e)
		{
			// we don't care
		}
			
		
		logger.debug("vmodem doCommand: " + thiscmd + "  reg: " + thisreg + " (" + regval +")  arg: " + thisarg + " (" + val +")");
		
		switch(thiscmd.toUpperCase().charAt(0))
		{
			// ignored
			case 'B':  // call negotiation, might implement if needed
			case 'L':  // speaker volume
			case 'M':  // speaker mode
			case 'N':  // handshake speed lock to s37
			case 'X':  // result code/dialing style
				break;
				
			// reset
			case 'Z':
				doCommandReset(val);
				break;
				
			// toggles
			case 'E':
				if (val > 1)
					errors++;
				else
					this.vmodem_registers[REG_ECHO] = val;
				break;
			case 'Q':
				if (val > 1)
					errors++;
				else
					this.vmodem_registers[REG_QUIET] = val;
				break;
			case 'V':
				if (val > 1)
					errors++;
				else
					this.vmodem_registers[REG_VERBOSE] = val;
				break;			
			
			// info
			case 'I':
				switch(val)
				{
					case 0:
						write(getCRLF() + "DWVM " +dwVSerialPorts.prettyPort(this.vport) + getCRLF());
						break;
					case 1:
					case 3:
						write(getCRLF() + "DriveWire virtual modem on port " + dwVSerialPorts.prettyPort(this.vport) + getCRLF());
						break;
					case 2:
						try 
						{
							if (dwVSerialPorts.getConn(this.vport) > -1)
								write(getCRLF() + "Connected to " + dwVSerialPorts.getHostIP(this.vport) + ":" + dwVSerialPorts.getHostPort(this.vport) + getCRLF());
							else
								write(getCRLF() + "Not connected" + getCRLF());
						} 
						catch (DWPortNotValidException e) 
						{
							logger.error(e.getMessage());
							write(e.getMessage());
						} 
						catch (DWConnectionNotValidException e) 
						{
							logger.error(e.getMessage());
							write(e.getMessage());
						}
						break;
					case 4:
						doCommandShowActiveProfile();
						break;
					default:
						errors++;
						break;
				}
				
				break;
				
			// extended commands
			case '&':
				if (thiscmd.length() > 1)
				{
					switch(thiscmd.toUpperCase().charAt(1))
					{
					
						case 'C':
							doCommandSetDCDMode(val);
							break;
						case 'D':
							doCommandSetDTRMode(val);
							break;
						case 'S':
							doCommandSetDSRMode(val);
							break;
						
						case 'A':
							doCommandAnswerMode(val);
							break;
						case 'Z':
						case 'F':
							doCommandReset(val);
							break;
						
						default:
							errors++;
							break;
					}
				}
				else
				{
					errors++;
				}
				
				break;
			
			// registers
			case 'S':
				// valid?
				if ((regval < 256) && (val < 256))
				{
					// display or set
					if (thisarg.equals("?"))
					{
						// display
						write(getCRLF() + this.vmodem_registers[regval] + getCRLF());	
					}	
					else
					{
						// set
						this.vmodem_registers[regval] = val;
					}
				}
				else
					errors++;
				
			    break;
			    
			 // dial   
			 case 'D':
			 	 if (!(thisarg.length() == 0))
			 	 {
			 	 	 // if its ATDL, dont reset vs_dev so we redial last host
			 	 	 if (!thisarg.equalsIgnoreCase("L"))
			 	 	 {
			 	 	 	 this.vmodem_dialstring = thisarg;
			 	 	 }
			 	 	 if (doDial() == 0)
			 	 	 {
		 	 	     	sendResponse(RESP_NOANSWER);
			 	 	 }
			 	 	 //don't print another response
			 	 	 errors = -1;
			 	 	 return(errors);
			 	 }
			 	 else
			 	 {
			 	 	 // ATD without a number/host?
			 	 	 errors++;
			 	 }
			 	 break;
			 	 
			default:
				// error on unknown commands?  OK might be preferable
				errors++;
				break;
		}
		
		return(errors);
	}

	private void doCommandAnswerMode(int val)
	{
		this.vmodem_registers[REG_LISTENPORTHI] = val / 256;
		this.vmodem_registers[REG_LISTENPORTLO] = val % 256;
		
		disableListener();
		
		if (val > 0)
		{
			enableListener(val);
		}
		
	}


	private void enableListener(int val)
	{
		this.listenThread = new Thread(new DWVModemListenerThread(this));
		listenThread.start();
	}


	private void disableListener()
	{
		
	}


	private int doDial() 
	{
		String tcphost;
		int tcpport;
		
		// parse dialstring
		String[] dparts = this.vmodem_dialstring.split(":");

		if (dparts.length == 1)
		{
			tcphost = dparts[0];
			tcpport = 23;
		}
		else if (dparts.length == 2)
		{
			tcphost = dparts[0];
			tcpport = Integer.parseInt(dparts[1]);
		}
		else
		{
			return 0;
		}
		
		// try to connect..
		
		this.tcpthread = new Thread(new DWVModemConnThread(this, tcphost, tcpport));
		this.tcpthread.setDaemon(true);
		this.tcpthread.start();
		
		return 1;
	}

	private void doCommandShowActiveProfile() 
	{
		// display current modem settings
		write("Active profile:" + getCRLF() + getCRLF());
		
		write("E" + onoff(this.isEcho()) + " ");
		write("Q" + onoff(this.isQuiet()) + " ");
		write("V" + onoff(this.isVerbose()) + " ");
		
		write("&C" + this.vmodem_registers[REG_DCDMODE] + " ");
		write("&D" + this.vmodem_registers[REG_DTRMODE] + " ");
		write("&S" + this.vmodem_registers[REG_DSRMODE] + " ");
		
		write(getCRLF() + getCRLF());
		
		// show S0-S37, only 0-13 and 36-37 are well documented
		for (int i = 0;i<38;i++)
		{
			write(String.format("S%03d=%03d  ", i, this.vmodem_registers[i]));
			Thread.yield();
		}
		
		write(getCRLF() + getCRLF());
	}

	
	
	
	
	private String onoff(boolean val) 
	{
		if (val)
			return("1");
		else
			return("0");
	}

	private void doCommandReset(int val) 
	{
		// common settings
		this.vmodem_lastcommand = new String();
		this.vmodem_registers[REG_LISTENPORTHI] = 0;
		this.vmodem_registers[REG_LISTENPORTLO] = 0;
		this.vmodem_registers[REG_ANSWERONRING] = 0;
		this.vmodem_registers[REG_RINGS] = 0;
		this.vmodem_registers[REG_CR] = 13;
		this.vmodem_registers[REG_LF] = 10;
		this.vmodem_registers[REG_BS] = 8;

		switch(val)
		{
			case 1:
				// settings for tcp mode/non modem use
				this.vmodem_registers[REG_ESCCHAR] = 255;
				this.vmodem_registers[REG_GUARDTIME] = 0;
				this.vmodem_registers[REG_ECHO] = 0;
				this.vmodem_registers[REG_VERBOSE] = 0;
				this.vmodem_registers[REG_QUIET] = 0;
				this.vmodem_registers[REG_DCDMODE] = 0;
				this.vmodem_registers[REG_DSRMODE] = 0;
				this.vmodem_registers[REG_DTRMODE] = 0;
				break;
			case 0:
			default:
				// settings better for use as a modem
				this.vmodem_registers[REG_ESCCHAR] = 43;
				this.vmodem_registers[REG_GUARDTIME] = 50;
				this.vmodem_registers[REG_ECHO] = 1;
				this.vmodem_registers[REG_VERBOSE] = 1;
				this.vmodem_registers[REG_QUIET] = 0;
				this.vmodem_registers[REG_DCDMODE] = 1;
				this.vmodem_registers[REG_DSRMODE] = 1;
				this.vmodem_registers[REG_DTRMODE] = 1;
				
				break;
		}
		
	}

	
	private void doCommandSetDCDMode(int val)
	{
		this.vmodem_registers[REG_DCDMODE] = val;
	}
	
	private void doCommandSetDTRMode(int val)
	{
		this.vmodem_registers[REG_DTRMODE] = val;
	}
	
	private void doCommandSetDSRMode(int val)
	{
		this.vmodem_registers[REG_DSRMODE] = val;
	}
	
	private String getCRLF()
	{
		return(Character.toString((char)this.vmodem_registers[REG_CR]) + Character.toString((char)this.vmodem_registers[REG_LF]));
	}
	
	
	private void sendResponse(int resp) 
	{
		
		// quiet mode
		if (!this.isQuiet())
		{
			// verbose mode
			if (this.isVerbose())
			{
				write(getVerboseResponse(resp) + getCRLF());
			}
			else
			{
				write(resp + getCRLF());
			}
		}
	}

	private String getVerboseResponse(int resp) 
	{
		String msg;
		
		switch(resp)
		{
			case RESP_OK:
				msg = "OK";
				break;
			case RESP_CONNECT:
				msg = "CONNECT";
				break;
			case RESP_RING:
				msg = "RING";
				break;
			case RESP_NOCARRIER:
				msg = "NO CARRIER";
				break;
			case RESP_ERROR:
				msg = "ERROR";
				break;
			case RESP_NODIALTONE:
				msg = "NO DIAL TONE";
				break;
			case RESP_BUSY:
				msg = "BUSY";
				break;
			case RESP_NOANSWER:
				msg = "NO ANSWER";
				break;
			default:
				msg = "UKNOWN";
				break;
		}
		return(msg);
	}
	
	public void write(String str)
	{
		try 
		{
			dwVSerialPorts.writeToCoco(this.vport, str);
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
	}
	

	public void write(byte data)
	{
		try 
		{
			dwVSerialPorts.writeToCoco(this.vport, data);
		} 
		catch (DWPortNotValidException e) 
		{
			logger.error(e.getMessage());
		}
	}
	


	public boolean isEcho() 
	{
		if (this.vmodem_registers[REG_ECHO] == 1)
			return true;
		
		return false;
	}

	public boolean isVerbose() 
	{
		if (this.vmodem_registers[REG_VERBOSE] == 1)
			return true;
		
		return false;
	}

	public boolean isQuiet() 
	{
		if (this.vmodem_registers[REG_QUIET] == 1)
			return true;
		
		return false;
	}


	public int getCR() 
	{
		return(this.vmodem_registers[REG_CR]);
	}

	public int getLF() 
	{
		return(this.vmodem_registers[REG_LF]);
	}
	
	public int getBS() 
	{
		return(this.vmodem_registers[REG_BS]);
	}


	public int getListenPort()
	{
		return(this.vmodem_registers[REG_LISTENPORTHI] * 256 + this.vmodem_registers[REG_LISTENPORTLO]);
	}


	public int getVPort()
	{
		return this.vport;
	}


	public DWVSerialPorts getVSerialPorts()
	{
		return this.dwVSerialPorts;
	}


	public int[] getRegisters()
	{
		return this.vmodem_registers;
	}


	public DWVSerialProtocol getVProto()
	{
		return this.dwProto;
	}


	public void setEcho(boolean b)
	{
		if (b)
			this.vmodem_registers[REG_ECHO] = 1;
		else
			this.vmodem_registers[REG_ECHO] = 0;
		
	}


	public void setVerbose(boolean b) 
	{
		if (b)
			this.vmodem_registers[REG_VERBOSE] = 1;
		else
			this.vmodem_registers[REG_VERBOSE] = 0;
			
	}

		
	
	
}
