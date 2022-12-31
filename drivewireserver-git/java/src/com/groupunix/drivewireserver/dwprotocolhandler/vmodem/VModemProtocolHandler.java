package com.groupunix.drivewireserver.dwprotocolhandler.vmodem;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TooManyListenersException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotOpenException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwhelp.DWHelp;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolDevice;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolTimers;
import com.groupunix.drivewireserver.dwprotocolhandler.DWSerialDevice;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.virtualserial.DWVSerialPorts;

public class VModemProtocolHandler implements Runnable, DWVSerialProtocol
{

	private final Logger logger = Logger.getLogger("DWServer.VModemProtocolHandler");
	
	private DWProtocolDevice protodev = null;
	
	private boolean started = false;
	private boolean ready = false;
	private boolean connected = false;
	private GregorianCalendar inittime = new GregorianCalendar();


	private int handlerno;
	private HierarchicalConfiguration config;


	private boolean wanttodie = false;

	private DWProtocolTimers timers = new DWProtocolTimers();

	private DWHelp dwhelp = new DWHelp(this);
	private DWVSerialPorts vSerialPorts;

	private boolean logdevbytes = false;

	
	public VModemProtocolHandler(int handlerno, HierarchicalConfiguration hconf )
	{
		this.handlerno = handlerno;
		this.config = hconf;
		this.logdevbytes = config.getBoolean("LogDeviceBytes", false);
		this.vSerialPorts = new DWVSerialPorts(this);
		this.vSerialPorts.resetAllPorts();
		
		if (config.containsKey("HelpFile"))
    	{
    		this.dwhelp = new DWHelp(config.getString("HelpFile"));
    	}
    	
		
	}
	

	
	
	public void run() 
	{
		
		Thread.currentThread().setName("vmodemproto-" + handlerno + "-" +  Thread.currentThread().getId());
		
		
		logger.info("VModemHandler #" + this.handlerno + " starting");
		this.started = true;
		
		this.timers.resetTimer(DWDefs.TIMER_START);
		
		if (this.protodev == null)
			setupProtocolDevice();
		
		try 
		{
			this.vSerialPorts.openPort(0);
			this.vSerialPorts.getPortVModem(0).setEcho(true);
			this.vSerialPorts.getPortVModem(0).setVerbose(true);
		} 
		catch (DWPortNotValidException e1) 
		{
			logger.error(e1.getMessage());
			wanttodie = true;
		}
		
		Thread VModemToSerialT = new Thread(new Runnable()
		{
			boolean wanttodie = false;
			
			
			public void run() 
			{
				while (!wanttodie)
				{
					try
					{
						int bread = protodev.comRead1(false);
						vSerialPorts.serWrite(0, bread);
						
						if (logdevbytes)
							logger.debug("read byte from serial device: " + bread);
					}
					catch (IOException e) 
					{
						wanttodie = true;
					} 
					catch (DWPortNotOpenException e) 
					{
						wanttodie = true;
					} 
					catch (DWPortNotValidException e) 
					{
						wanttodie = true;
					} 
					catch (DWCommTimeOutException e) 
					{
						wanttodie = true;
						
					}
					catch (NullPointerException e)
					{
						wanttodie = true;
					}
				}
				
			}});
		
		 VModemToSerialT.start();
		
		
		if (!wanttodie && (this.protodev != null))
		{
			this.ready = true;
			logger.debug("handler #" + handlerno + " is ready");
		}
		else
		{
			logger.warn("handler #" + handlerno + " failed to get ready");
		}
		
		byte[] buffer = new byte[256];	
		
		while (!wanttodie && (this.protodev != null))
		{
			
			try 
			{
				int bread = vSerialPorts.getPortOutput(0).read(buffer);
				this.protodev.comWrite(buffer, bread, false);
				if (logdevbytes)
					logger.debug("read " + bread + " bytes from vmodem: " + DWUtils.byteArrayToHexString(buffer, bread));
			}				
			catch (IOException e) 
			{
				logger.error( e.getMessage());
			} 
			catch (DWPortNotValidException e) 
			{
				logger.error(e.getMessage());
				
			} 
					
		}
		
		logger.debug("handler #" + handlerno + " is exiting");
		this.vSerialPorts.shutdown();
	}
	
	
	
	
	private void setupProtocolDevice()
	{
		
		if ((protodev != null))
			protodev.shutdown();
		
		// create serial device
		if ((config.containsKey("SerialDevice") && config.containsKey("SerialRate")))		
		{
			try 
			{
				protodev = new DWSerialDevice(this);
			}
			catch (NoSuchPortException e1)
			{
				logger.error("handler #"+handlerno+": Serial device '" + config.getString("SerialDevice") + "' not found");
			} 
			catch (PortInUseException e2)
			{
				logger.error("handler #"+handlerno+": Serial device '" + config.getString("SerialDevice") + "' in use");
			}
			catch (UnsupportedCommOperationException e3)
			{
				logger.error("handler #"+handlerno+": Unsupported comm operation while opening serial port '"+config.getString("SerialDevice")+"'");
			} 
			catch (IOException e)
			{
				logger.error("handler #"+handlerno+": IO exception while opening serial port '"+config.getString("SerialDevice")+"'");
			} 
			catch (TooManyListenersException e)
			{
				logger.error("handler #"+handlerno+": Too many listeneres while opening serial port '"+config.getString("SerialDevice")+"'");
			}
			
		}	
		else
		{
			logger.error("VModem requires both SerialDevice and SerialRate to be set, please configure this instance.");
		}
			
	}



	
	public void shutdown() 
	{
		logger.debug("vmodem handler #" + handlerno + ": shutdown requested");
		
		this.wanttodie  = true;
		
		if (this.protodev != null)
			this.protodev.shutdown();
	}

	
	public boolean isDying() 
	{
		return wanttodie;
	}

	
	public boolean isStarted() 
	{
		return this.started ;
	}

	
	public boolean isReady() 
	{
		return this.ready ;
	}

	
	public HierarchicalConfiguration getConfig() 
	{
		return this.config;
	}

	
	public DWProtocolDevice getProtoDev() 
	{
		return this.protodev;
	}

	
	public GregorianCalendar getInitTime() 
	{
		return this.inittime;
	}

	
	public String getStatusText() 
	{
		return "VModem status TODO";
	}

	
	public void resetProtocolDevice() 
	{
		logger.debug("resetting serial port");
		if (this.protodev != null)
		{
			this.protodev.shutdown();
			this.protodev = null;
		}
		
		setupProtocolDevice();
		
		
	}

	
	public void syncStorage() 
	{
		// noop
		
	}

	
	public int getHandlerNo() 
	{
		return this.handlerno;
	}

	
	public Logger getLogger() 
	{
		return this.logger;
	}

	
	public int getCMDCols() 
	{
		return 0;
	}

	
	public DWHelp getHelp() 
	{
		return this.dwhelp ;
	}

	
	public void submitConfigEvent(String propertyName, String string) 
	{
		// noop
	}

	
	public long getNumOps() 
	{
		return 0;
	}

	
	public long getNumDiskOps() 
	{
		return 0;
	}

	
	public long getNumVSerialOps() 
	{
		return 0;
	}

	
	public DWProtocolTimers getTimers() 
	{
		return this.timers;
	}



	
	public boolean isConnected() 
	{
		return this.connected ;
	}


	
	public boolean hasPrinters() 
	{
		
		return false;
	}
	
	
	public boolean hasDisks() {
		
		return false;
	}

	
	public boolean hasMIDI() {
		
		return false;
	}

	
	public boolean hasVSerial() {
		
		return true;
	}



	
	public DWVSerialPorts getVPorts() 
	{
		return this.vSerialPorts;
	}



}
