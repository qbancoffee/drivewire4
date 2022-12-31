package com.groupunix.drivewireserver.dwprotocolhandler;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TooManyListenersException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.MCXDefs;
import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
import com.groupunix.drivewireserver.dwhelp.DWHelp;
import com.groupunix.drivewireserver.virtualprinter.DWVPrinter;



public class MCXProtocolHandler implements Runnable, DWProtocol
{

	private final Logger logger = Logger.getLogger("DWServer.MCXProtocolHandler");
	  

	
	// record keeping portion of dwTransferData
	private byte lastDrive = 0;
	private int readRetries = 0;
	private int writeRetries = 0;
	private int sectorsRead = 0;
	private int sectorsWritten = 0;
	private byte lastOpcode = DWDefs.OP_RESET1;
	private int lastChecksum = 0;
	private int lastError = 0;
	private byte[] lastLSN = new byte[3];
	
	private GregorianCalendar dwinitTime = new GregorianCalendar();
	
	// serial port instance
	
	private DWProtocolDevice protodev;
	
	// printer
	private DWVPrinter vprinter;
	
	// disk drives
	private DWDiskDrives diskDrives;
	
	private boolean wanttodie = false;
	// private static Thread readerthread;
	private boolean started = false;

	private int handlerno;
	private HierarchicalConfiguration config;
	
	
	public MCXProtocolHandler(int handlerno, HierarchicalConfiguration hconf)
	{
		this.handlerno = handlerno;
		this.config = hconf;
		
		//config.addConfigurationListener(new DWProtocolConfigListener());   
		
	}

	
	public HierarchicalConfiguration getConfig()
	{
		return(this.config);
	}
	

	public void reset()
	{
		DoOP_RESET();
	}
	
	
	public boolean connected()
	{
		return(protodev.connected());
	}
	
	
	
	public void shutdown()
	{
		logger.info("handler #" + handlerno + ": shutdown requested");
		
		this.wanttodie = true;
		this.protodev.shutdown();
	}
	
	
	public void run()
	{
		int opcodeint = -1;
		int alertcodeint = -1;
		this.started = true;
		
		Thread.currentThread().setName("mcxproto-" + handlerno + "-" +  Thread.currentThread().getId());
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		setupProtocolDevice();
		
		// setup environment and get started
		if (!wanttodie)
		{
			logger.info("handler #" + handlerno + ": starting...");

		
			// setup printer
			vprinter = new DWVPrinter(this);
				
		}			

		logger.info("handler #" + handlerno + ": ready");
		
		// protocol loop
		while(!wanttodie)
		{ 
						
			// try to get an opcode
			if (protodev != null)
			{
				try 
				{
					alertcodeint = protodev.comRead1(false);
					opcodeint = protodev.comRead1(false);
					
					
					if (alertcodeint == MCXDefs.ALERT)
					{
					
						switch(opcodeint)
						{
							case MCXDefs.OP_DIRFILEREQUEST:
								DoOP_DIRFILEREQUEST();
								break;
						
							case MCXDefs.OP_DIRNAMEREQUEST:
								DoOP_DIRNAMEREQUEST();
								break;
								
							case MCXDefs.OP_GETDATABLOCK:
								DoOP_GETDATABLOCK();
								break;
								
							case MCXDefs.OP_LOADFILE:
								DoOP_LOADFILE();
								break;
								
							case MCXDefs.OP_OPENDATAFILE:
								DoOP_OPENDATAFILE();
								break;
								
							case MCXDefs.OP_PREPARENEXTBLOCK:
								DoOP_PREPARENEXTBLOCK();
								break;
						
							case MCXDefs.OP_RETRIEVENAME:
								DoOP_RETRIEVENAME();
								break;
								
							case MCXDefs.OP_SAVEFILE:
								DoOP_SAVEFILE();
								break;
								
							case MCXDefs.OP_SETCURRENTDIR:
								DoOP_SETCURRENTDIR();
								break;
								
							case MCXDefs.OP_WRITEBLOCK:
								DoOP_WRITEBLOCK();
								break;
								
							default:
								logger.warn("UNKNOWN OPCODE: " + opcodeint);
								break;
						}
						
					}
					else
					{
						logger.warn("Got non alert code when expected alert code: " + alertcodeint);
					}
					
					
				} 
				catch (IOException  e) 
				{
					// this should not actually ever get thrown, since we call comRead1 with timeout = false..
					logger.error(e.getMessage());
					opcodeint = -1;
				} catch (DWCommTimeOutException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				logger.debug("cannot access the device.. maybe it has not been configured or maybe it does not exist");
				
				// take a break, reset, hope things work themselves out
				try 
				{
					Thread.sleep(config.getInt("FailedPortRetryTime",1000));
					resetProtocolDevice();
					
				} 
				catch (InterruptedException e) 
				{
					logger.error("Interrupted during failed port delay.. giving up on this situation");
					wanttodie = true;
				}
				
			}

		}
 
			
		logger.info("handler #"+ handlerno+ ": exiting");
		
		
		
		if (this.diskDrives != null)
		{
			this.diskDrives.shutdown();
		}
		
		
		if (protodev != null)
		{
			protodev.shutdown();
		}
			
	}

	
	
	


	// MCX OP methods

	
	
	private void DoOP_LOADFILE() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_LOADFILE");
		}
	}
	
	
	private void DoOP_GETDATABLOCK() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_GETDATABLOCK");
		}
	}
	
	private void DoOP_PREPARENEXTBLOCK() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_PREPARENEXTBLOCK");
		}
	}
	
	private void DoOP_SAVEFILE() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_SAVEFILE");
		}
	}
	
	private void DoOP_WRITEBLOCK() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_WRITEBLOCK");
		}
	}
	
	private void DoOP_OPENDATAFILE() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_OPENDATAFILE");
		}
	}
	
	
	
	private void DoOP_DIRFILEREQUEST() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_DIRFILEREQUEST");
		}
		
		
		try 
		{
			// read flag byte
			int flag = protodev.comRead1(true);
			
			// read arg length
			int arglen = protodev.comRead1(true);
			
			// read arg
			byte[] buf = new byte[arglen];
			buf = protodev.comRead(arglen);
			
			logger.debug("DIRFILEREQUEST fl: " + flag + "  arg: " + new String(buf));
			
			//respond
			if (flag == 0)
			{
				protodev.comWrite1(0, false);
				protodev.comWrite1(4, false);
			}
			else
			{
				protodev.comWrite1(0, false);
				protodev.comWrite1(0, false);
			}
			
			
		} 
		catch (IOException  e) 
		{
			logger.warn(e.getMessage());
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	private void DoOP_RETRIEVENAME() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_RETRIEVENAME");
		}
		
		try 
		{
			int arglen = protodev.comRead1(true);
			
			if (arglen == 4)
			{
				protodev.comWrite1('T', false);
				protodev.comWrite1('e', false);
				protodev.comWrite1('s', false);
				protodev.comWrite1('2', false);
			}
			
		} 
		catch (IOException e) 
		{
			logger.warn(e.getMessage());
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void DoOP_DIRNAMEREQUEST() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_DIRNAMEREQUEST");
		}
	}
	
	private void DoOP_SETCURRENTDIR() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_SETCURRENTDIR");
		}
	}
	
	
	
	
	
	private void DoOP_RESET() 
	{
		// coco has been reset/turned on
		
		// reset stats
		
		lastDrive = 0;
		readRetries = 0;
		writeRetries = 0;
		sectorsRead = 0;
		sectorsWritten = 0;
		lastOpcode = DWDefs.OP_RESET1;
		lastChecksum = 0;
		lastError = 0;
		
		lastLSN = new byte[3];
		
		// Sync disks??
		
	
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_RESET");
		}
		
	}
	

	
	@SuppressWarnings("unused")
	private void DoOP_WRITE(byte opcode)
	{
		byte[] cocosum = new byte[2];
		byte[] responsebuf = new byte[262];
		byte response = 0;
		byte[] sector = new byte[256];
		
		try 
		{
			// read rest of packet
			responsebuf = protodev.comRead(262);

			lastDrive = responsebuf[0];
			System.arraycopy( responsebuf, 1, lastLSN, 0, 3 );
			System.arraycopy( responsebuf, 4, sector, 0, 256 );
			System.arraycopy( responsebuf, 260, cocosum, 0, 2 );

			
			// Compute Checksum on sector received - NOTE: no V1 version checksum
			lastChecksum = computeChecksum(sector, 256);
			
			
			
		} 
		catch (IOException e1) 
		{
			// Timed out reading data from Coco
			logger.error("DoOP_WRITE error: " + e1.getMessage());
			
			// reset, abort
			return;
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		// Compare checksums 
		if (lastChecksum != DWUtils.int2(cocosum))
		{
			// checksums do not match, tell Coco
			protodev.comWrite1(DWDefs.DWERROR_CRC, false);
			
			logger.warn("DoOP_WRITE: Bad checksum, drive: " + lastDrive + " LSN: " + DWUtils.int3(lastLSN) + " CocoSum: " + DWUtils.int2(cocosum) + " ServerSum: " + lastChecksum);
			
			return;
		}

				
		// do the write
		response = DWDefs.DWOK;
		
		try 
		{
			// Seek to LSN in DSK image 
			diskDrives.seekSector(lastDrive,DWUtils.int3(lastLSN));
			// Write sector to DSK image 
			diskDrives.writeSector(lastDrive,sector);
		} 
		catch (DWDriveNotLoadedException e1) 
		{
			// send drive not ready response
			response = DWDefs.DWERROR_NOTREADY;
			logger.warn(e1.getMessage());
		} 
		catch (DWDriveNotValidException e2) 
		{
			// basically the same as not ready
			response = DWDefs.DWERROR_NOTREADY;
			logger.warn(e2.getMessage());
		} 
		catch (DWDriveWriteProtectedException e3) 
		{
			// hopefully this is appropriate
			response = DWDefs.DWERROR_WP;
			logger.warn(e3.getMessage());
		} 
		catch (IOException e4) 
		{
			// error on our end doing the write
			response = DWDefs.DWERROR_WRITE;
			logger.error(e4.getMessage());
		} 
		catch (DWInvalidSectorException e5) 
		{
			response = DWDefs.DWERROR_WRITE;
			logger.warn(e5.getMessage());
		} 
		catch (DWSeekPastEndOfDeviceException e6) 
		{
			response = DWDefs.DWERROR_WRITE;
			logger.warn(e6.getMessage());
		} 
		
		// record error
		if (response != DWDefs.DWOK)
			lastError = response;
		
		// send response
		protodev.comWrite1(response, false);
		
		// Increment sectorsWritten count
		if (response == DWDefs.DWOK)
			sectorsWritten++;
		
		if (opcode == DWDefs.OP_REWRITE)
		{
			writeRetries++;
			if (config.getBoolean("LogOpCode", false))
			{
				logger.info("DoOP_REWRITE lastDrive: " + (int) lastDrive + " LSN: " + DWUtils.int3(lastLSN));
			}
		}
		else
		{
			if (config.getBoolean("LogOpCode", false))
			{
				logger.info("DoOP_WRITE lastDrive: " + (int) lastDrive + " LSN: " + DWUtils.int3(lastLSN));
			}
		}
		
		return;
	}
	
	
	
	

	
	// printing
	
	@SuppressWarnings("unused")
	private void DoOP_PRINT() 
	{
		int tmpint;

		try 
		{
			tmpint = protodev.comRead1(true);
			
			if (config.getBoolean("LogOpCode", false))
			{
				logger.info("DoOP_PRINT: byte "+ tmpint);
			}
			
			vprinter.addByte((byte) tmpint);
		} 
		catch (IOException e) 
		{
			logger.error("IO exception reading print byte: " + e.getMessage());
		} catch (DWCommTimeOutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@SuppressWarnings("unused")
	private void DoOP_PRINTFLUSH() 
	{
		if (config.getBoolean("LogOpCode", false))
		{
			logger.info("DoOP_PRINTFLUSH");
		}
		
		vprinter.flush();
	}
	
	
	
	
		
	
	private int computeChecksum(byte[] data, int numbytes) 
	{
		int lastChecksum = 0;

		/* Check to see if numbytes is odd or even */
		while (numbytes > 0)
		{
			numbytes--;
			lastChecksum += (int) (data[numbytes] & 0xFF) ;
		}

		return(lastChecksum);
		
	}



	public byte getLastDrive() {
		return lastDrive;
	}


	public int getReadRetries() {
		return readRetries;
	}


	public int getWriteRetries() {
		return writeRetries;
	}


	public int getSectorsRead() {
		return sectorsRead;
	}


	public int getSectorsWritten() {
		return sectorsWritten;
	}


	public byte getLastOpcode() {
		return lastOpcode;
	}




	public int getLastChecksum() {
		return lastChecksum;
	}


	public int getLastError() {
		return lastError;
	}


	public byte[] getLastLSN() {
		return lastLSN;
	}
		

	
	public GregorianCalendar getInitTime()
	{
		return(dwinitTime);
	}



	public boolean isDying()
	{
		return this.wanttodie;
	}



	
	public DWProtocolDevice getProtoDev()
	{
		return(this.protodev);
	}




	
	public void resetProtocolDevice()
	{
		if (!this.wanttodie)
		{
			logger.warn("resetting protocol device");
			// 	do we need to do anything else here?
			setupProtocolDevice();
		}
	}
	
	
	private void setupProtocolDevice()
	{
		
		if (protodev != null)
			protodev.shutdown();
		
		if (config.getString("DeviceType","serial").equalsIgnoreCase("serial") )
		{
		
			// create serial device
			if (config.containsKey("SerialDevice"))		
			{
				try 
				{
					
					protodev = new DWSerialDevice(this);
				}
				catch (NoSuchPortException e1)
				{
					//wanttodie = true; lets keep on living and see how that goes
					logger.error("handler #"+handlerno+": Serial device '" + config.getString("SerialDevice") + "' not found");
				} 
				catch (PortInUseException e2)
				{
					//wanttodie = true;
					logger.error("handler #"+handlerno+": Serial device '" + config.getString("SerialDevice") + "' in use");
				}
				catch (UnsupportedCommOperationException e3)
				{
					//wanttodie = true;
					logger.error("handler #"+handlerno+": Unsupported comm operation while opening serial port '"+config.getString("SerialDevice")+"'");
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TooManyListenersException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			else
			{
				logger.error("Serial mode requires SerialDevice to be set, cannot use this configuration");
				//wanttodie = true;
			}
		}
		else if (config.getString("DeviceType").equalsIgnoreCase("tcp"))
		{
			// create TCP device
			if (config.containsKey("TCPDevicePort"))		
			{
				try 
				{
					protodev = new DWTCPDevice(this.handlerno, config.getInt("TCPDevicePort"));
				} 
				catch (IOException e) 
				{
					//wanttodie = true;
					logger.error("handler #"+handlerno+": " + e.getMessage());
				}
			}	
			else
			{
				logger.error("TCP mode requires TCPDevicePort to be set, cannot use this configuration");
				//wanttodie = true;
			}
			
		}
		else if (config.getString("DeviceType").equalsIgnoreCase("tcp-client"))
		{
			// create TCP device
			if (config.containsKey("TCPClientPort") && config.containsKey("TCPClientHost"))		
			{
				try 
				{
					protodev = new DWTCPClientDevice(this.handlerno, config.getString("TCPClientHost"), config.getInt("TCPClientPort"));
				} 
				catch (IOException e) 
				{
					//wanttodie = true;
					logger.error("handler #"+handlerno+": " + e.getMessage());
				}
			}	
			else
			{
				logger.error("TCP mode requires TCPClientPort and TCPClientHost to be set, cannot use this configuration");
				//wanttodie = true;
			}
			
		}	
	}


	
	public String getStatusText() 
	{
		String text = new String();
		
		text += "Last OpCode:   " + DWUtils.prettyOP(getLastOpcode()) + "\r\n";
		text += "Last Drive:    " + getLastDrive() + "\r\n";
		text += "Last LSN:      " + getLastLSN() + "\r\n";
		text += "Last Error:    " + ((int) getLastError() & 0xFF) + "\r\n";
	
		return(text);
	}






	
	public void syncStorage() {
		// TODO Auto-generated method stub
		
	}


	
	public int getHandlerNo() {
		// TODO Auto-generated method stub
		return this.handlerno;
	}


	
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return this.logger;
	}


	
	public int getCMDCols() {
		// TODO Auto-generated method stub
		return 32;
	}


	
	public DWHelp getHelp() {
		// TODO Auto-generated method stub
		return null;
	}


	
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}


	
	public void submitConfigEvent(String propertyName, String string)
	{
		// TODO Auto-generated method stub
		
	}


	
	public long getNumOps()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	
	public long getNumDiskOps()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	
	public long getNumVSerialOps()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	
	public DWProtocolTimers getTimers()
	{
		// TODO Auto-generated method stub
		return null;
	}


	
	public boolean isStarted() 
	{
		return this.started;
	}


	
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public boolean hasPrinters() 
	{
		
		return true;
	}
	
	
	public boolean hasDisks() {
		
		return true;
	}
	
	
	public boolean hasMIDI() {
		
		return false;
	}
	
	
	public boolean hasVSerial() {
		
		return false;
	}
}
	

