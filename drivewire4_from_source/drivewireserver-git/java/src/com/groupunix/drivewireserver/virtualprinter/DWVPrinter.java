package com.groupunix.drivewireserver.virtualprinter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWVPrinter 
{

	private ArrayList<DWVPrinterDriver> drivers = new ArrayList<DWVPrinterDriver>();
	
	
	private static final Logger logger = Logger.getLogger("DWServer.DWVPrinter");
	
	private DWProtocol dwProto;
	
	public DWVPrinter(DWProtocol dwProto)
	{
		this.dwProto = dwProto;
		
		logger.debug("dwprinter init for handler #" + dwProto.getHandlerNo());
		
		// load drivers
		
		@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> printers = dwProto.getConfig().configurationsAt("Printer");
		
		for (HierarchicalConfiguration printer : printers)
		{
			if (printer.containsKey("[@name]") && printer.containsKey("Driver"))
			{
				// definition appears valid, now can we instantiate the requested driver...
				
				try 
				{
					@SuppressWarnings("unchecked")
					Constructor<DWVPrinterDriver> pconst = (Constructor<DWVPrinterDriver>) Class.forName(("com.groupunix.drivewireserver.virtualprinter.DWVPrinter" + printer.getString("Driver")), true, this.getClass().getClassLoader()).getConstructor(Class.forName("org.apache.commons.configuration.HierarchicalConfiguration"));
					this.drivers.add((DWVPrinterDriver) pconst.newInstance(printer));
					// yes we can
					logger.debug("init printer '" + printer.getString("[@name]") + "' using driver '" + printer.getString("Driver") + "'");
					
				} 
				// so many ways to fail...
				catch (ClassNotFoundException e) 
				{
					logger.warn("Invalid printer definition '" + printer.getString("[@name]") + "' in config, '" + printer.getString("Driver") + "' is not a known driver.");
				} 
				catch (InstantiationException e) 
				{
					logger.warn("InstantiationException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				} 
				catch (IllegalAccessException e) 
				{
					logger.warn("IllegalAccessException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				} 
				catch (SecurityException e) 
				{
					logger.warn("SecurityException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				} 
				catch (NoSuchMethodException e) 
				{
					logger.warn("NoSuchMethodException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				} 
				catch (IllegalArgumentException e) 
				{
					logger.warn("IllegalArgumentException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				} 
				catch (InvocationTargetException e) 
				{
					logger.warn("InvocationTargetException on printer '" + printer.getString("[@name]") + "': " + e.getMessage());
				}
				
			}
			else
			{
				logger.warn("Invalid printer definition in config, name " + printer.getString("[@name]","not defined") + " driver " + printer.getString("Driver","not defined"));
			}
		}
	}
	
	
	public void addByte(byte data)
	{
		try 
		{
			getCurrentDriver().addByte(data);
		} 
		catch (IOException e) 
		{
			logger.warn("error writing to print buffer: " + e.getMessage());
		} 
		catch (DWPrinterNotDefinedException e) 
		{
			logger.warn("error writing to print buffer: " + e.getMessage());
		}
	}
	
	private DWVPrinterDriver getCurrentDriver() throws DWPrinterNotDefinedException
	{
		String curprinter = this.dwProto.getConfig().getString("CurrentPrinter",null);
		
		if (curprinter == null)
		{
			throw new DWPrinterNotDefinedException("No current printer is set in the configuration");
		}
		
		for (DWVPrinterDriver drv : this.drivers)
		{
			if (drv.getPrinterName().equals(curprinter))
				return(drv);
		}
		
		throw new DWPrinterNotDefinedException("Cannot find printer named '" + curprinter + "'");
	}

	
	public void flush()
	{
		logger.debug("Printer flush");
		
		// get out of main thread for flush..
		
		Thread flusher = new Thread(new Runnable() 
		{
		
			
			public void run() 
			{
				Thread.currentThread().setName("printflush-" + Thread.currentThread().getId());
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				logger.debug("flush thread run");
				try 
				{
					getCurrentDriver().flush();
				} 
				catch (DWPrinterNotDefinedException e) 
				{
					logger.warn("error flushing print buffer: " + e.getMessage());
				} 
				catch (IOException e) 
				{
					logger.warn("error flushing print buffer: " + e.getMessage());
				} 
				catch (DWPrinterFileError e) 
				{	
					logger.warn("error flushing print buffer: " + e.getMessage());
				}
			}
		
		});
		
		flusher.start();

	}

	

	
	public HierarchicalConfiguration getConfig()
	{
		return dwProto.getConfig();
	}

	public Logger getLogger() 
	{
		return dwProto.getLogger();
	}
	
}
