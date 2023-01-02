
package com.groupunix.drivewireserver;



import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.lf5.LF5Appender;
import org.apache.log4j.spi.LoggingEvent;

import com.groupunix.drivewireserver.dwdisk.DWDiskLazyWriter;
import com.groupunix.drivewireserver.dwexceptions.DWPlatformUnknownException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.MCXProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.vmodem.VModemProtocolHandler;



public class DriveWireServer 
{

	public static final int DWVersionMajor = 4;
	public static final int DWVersionMinor = 3;
	public static final int DWVersionBuild = 4;
	public static final String DWVersionRevision = "f";
	@SuppressWarnings("deprecation")
	public static final Date DWVersionDate = new Date(2013 - 1900, 10 - 1, 21);
	
	public static final Version DWVersion = new Version(DWVersionMajor, DWVersionMinor, DWVersionBuild, DWVersionRevision, DWVersionDate);
	
	
	static Logger logger = Logger.getLogger(com.groupunix.drivewireserver.DriveWireServer.class);
	private static ConsoleAppender consoleAppender;
	private static DWLogAppender dwAppender;
	private static FileAppender fileAppender;
	private static PatternLayout logLayout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %m%n");
	
	public static XMLConfiguration serverconfig;
	
	public static int configserial = 0;
	
	private static Vector<Thread> dwProtoHandlerThreads = new Vector<Thread>();
	private static Vector<DWProtocol> dwProtoHandlers = new Vector<DWProtocol>();

	private static Thread lazyWriterT;
	private static DWUIThread uiObj;
	private static Thread uiT;	
	
	
	private static boolean wanttodie = false;
	private static String configfile = "config.xml";
	private static boolean ready = false;
	private static boolean useLF5 = false;
	private static LF5Appender lf5appender;
	private static boolean useBackup = false;
	private static SerialPort testSerialPort;
	
	private static DWEvent statusEvent = new DWEvent(DWDefs.EVENT_TYPE_STATUS, -1);
	private static long lastMemoryUpdate = 0;
	private static ArrayList<DWEvent> logcache = new ArrayList<DWEvent>();
	private static boolean useDebug = false;
	private static long magic = System.currentTimeMillis();
	private static DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_STATUS, -1);
	private static DWEvent fevt;
	private static boolean noMIDI = false;
	private static boolean noMount = false;
	private static boolean configFreeze = false;
	
	@SuppressWarnings("unused")
	private static boolean noUI = false;
	@SuppressWarnings("unused")
	private static boolean noServer = false;
	
	@SuppressWarnings("unused")
	private static boolean restart_logging = false;
	@SuppressWarnings("unused")
	private static boolean restart_ui = false;
	
	public static void main(String[] args) throws ConfigurationException
	{
		
		// catch everything
		Thread.setDefaultUncaughtExceptionHandler(new DWExceptionHandler());
		
		init(args);
		
		// install clean shutdown handler
        //Runtime.getRuntime().addShutdownHook(new DWShutdownHandler());
		
	 	// hang around 
		logger.debug("ready...");	
		
		DriveWireServer.ready = true;
		while (!wanttodie)
    	{
    	
    		try
    		{
    			Thread.sleep(DriveWireServer.serverconfig.getInt("StatusInterval",1000));
    			
    			checkHandlerHealth();
    			
    			submitServerStatus();
    			
    		} 
    		catch (InterruptedException e)
    		{
    			logger.debug("I've been interrupted, now I want to die");
    			wanttodie = true;
    		}

    	}
    	
		serverShutdown();
		//System.exit(0);
	}

	


	private static void checkHandlerHealth()
	{
		for (int i = 0;i < DriveWireServer.dwProtoHandlers.size() ;i++)
		{
			if ((dwProtoHandlers.get(i)!= null) && (dwProtoHandlers.get(i).isReady()) && (!dwProtoHandlers.get(i).isDying())) 
			{
				
				// check thread
				if (dwProtoHandlerThreads.get(i) == null)
				{
					logger.error("Null thread for handler #" + i);
				}
				else 
				{
					if (!dwProtoHandlerThreads.get(i).isAlive() )
					{
						logger.error("Handler #" + i + " has died. RIP.");
						
						if (dwProtoHandlers.get(i).getConfig().getBoolean("ZombieResurrection", true))
						{
							logger.info("Arise chicken! Reanimating handler #" + i + ": " + dwProtoHandlers.get(i).getConfig().getString("[@name]","unnamed"));
							
							
							@SuppressWarnings("unchecked")
							List<HierarchicalConfiguration> handlerconfs = serverconfig.configurationsAt("instance");

							dwProtoHandlers.set(i, new DWProtocolHandler(i, handlerconfs.get(i)));
							
					    	dwProtoHandlerThreads.set(i, new Thread(dwProtoHandlers.get(i)));
					    	dwProtoHandlerThreads.get(i).start();	
						}
						
					}
	
				}
			}
		}
	}




	private static void submitServerStatus()
	{
		long ticktime = System.currentTimeMillis();
	
		if (uiObj != null)
		{
			
			
			// add everything
			
			evt.setParam(DWDefs.EVENT_ITEM_MAGIC, DriveWireServer.getMagic()+"");
			evt.setParam(DWDefs.EVENT_ITEM_INTERVAL, DriveWireServer.serverconfig.getInt("StatusInterval",1000)+"");
			evt.setParam(DWDefs.EVENT_ITEM_INSTANCES, DriveWireServer.getNumHandlers()+"");
			evt.setParam(DWDefs.EVENT_ITEM_INSTANCESALIVE, DriveWireServer.getNumHandlersAlive()+"");
			evt.setParam(DWDefs.EVENT_ITEM_THREADS, DWUtils.getRootThreadGroup().activeCount()+"");
			evt.setParam(DWDefs.EVENT_ITEM_UICLIENTS, DriveWireServer.uiObj.getNumUIClients()+"");

			// ops
			evt.setParam(DWDefs.EVENT_ITEM_OPS, DriveWireServer.getTotalOps()+"");
			evt.setParam(DWDefs.EVENT_ITEM_DISKOPS, DriveWireServer.getDiskOps()+"");
			evt.setParam(DWDefs.EVENT_ITEM_VSERIALOPS, DriveWireServer.getVSerialOps()+"");
			
			// some things should not be updated every tick..
			if (ticktime - lastMemoryUpdate > DWDefs.SERVER_MEM_UPDATE_INTERVAL)
			{	
				//System.gc();
				evt.setParam(DWDefs.EVENT_ITEM_MEMTOTAL, (Runtime.getRuntime().totalMemory() / 1024)+"");
				evt.setParam(DWDefs.EVENT_ITEM_MEMFREE, (Runtime.getRuntime().freeMemory() / 1024)+"");
				lastMemoryUpdate = ticktime; 
			}
			
			// only send updated vals
			fevt = new DWEvent(DWDefs.EVENT_TYPE_STATUS, -1);
			
			for (String key : evt.getParamKeys())
			{
				if (!statusEvent.hasParam(key) || (!statusEvent.getParam(key).equals(evt.getParam(key))))
				{
					fevt.setParam(key, evt.getParam(key));
					statusEvent.setParam(key, evt.getParam(key));
				}
			}
			
			
			if (fevt.getParamKeys().size() > 0)
				uiObj.submitEvent(fevt);
		}
		
	}



	public static DWEvent getServerStatusEvent()
	{
		return DriveWireServer.statusEvent;
	}
	

	private static long getTotalOps()
	{
		long res = 0;
		
		for (DWProtocol p : dwProtoHandlers)
		{
			if (p != null)
				res += p.getNumOps();
		}
			
		return res;
	}


	private static long getDiskOps()
	{
		long res = 0;
		
		for (DWProtocol p : dwProtoHandlers)
		{
			if (p != null)
				res += p.getNumDiskOps();
		}
			
		return res;
	}
	
	
	private static long getVSerialOps()
	{
		long res = 0;
		
		for (DWProtocol p : dwProtoHandlers)
		{
			if (p != null)
				res += p.getNumVSerialOps();
		}
			
		return res;
	}
	


	public static void init(String[] args) 
	{
		// set thread name
		Thread.currentThread().setName("dwserver-" + Thread.currentThread().getId());
		
		// command line arguments
        doCmdLineArgs(args);
               
		// 	set up initial logging config
        initLogging();
        
        logger.info("DriveWire Server " + DWVersion + " starting");
        //logger.info("Heap max: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB " + " cur: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
		// load server settings
        try 
        {
        	
    		// try to load/parse config
    		serverconfig = new XMLConfiguration(configfile);
    		// only backup if it loads
    		if (useBackup)
    			backupConfig(configfile);
    		
		} 
        catch (ConfigurationException e1) 
    	{
    		logger.fatal(e1.getMessage());
    		System.exit(-1);
		}

        
        // start UI server first, so we can bail if UIorBust
        if (applyUISettings() == true)
        {
	        
	        
	        // apply settings to logger
	        applyLoggingSettings();
	        
	        /* not needed with NRSerialJava
	    	// Try to add native rxtx to lib path
			if (serverconfig.getBoolean("LoadRXTX",true))
			{
			   loadRXTX();
			}
			*/
			
			// test for RXTX..
			if (serverconfig.getBoolean("UseRXTX", true) && !checkRXTXLoaded())
			{
				logger.fatal("UseRXTX is set, but RXTX native libraries could not be loaded");
				logger.fatal("Please see http://sourceforge.net/apps/mediawiki/drivewireserver/index.php");
				System.exit(-1);
			}
			
			
	
	    	// add server config listener
	    	serverconfig.addConfigurationListener(new DWServerConfigListener());    	
	    	
	    	// apply configuration
	    	
	    	// auto save
	    	if (serverconfig.getBoolean("ConfigAutosave",true))
	    	{
	    		logger.debug("Auto save of configuration is enabled");
	    		serverconfig.setAutoSave(true);
	    	}
	    	
	
			
	    	// start protocol handler instance(s)
	    	startProtoHandlers();
	    	
	    	// start lazy writer
			startLazyWriter();
    	}

	}




	private static void startProtoHandlers() 
	{
	   	@SuppressWarnings("unchecked")
		List<HierarchicalConfiguration> handlerconfs = serverconfig.configurationsAt("instance");
    	
    	dwProtoHandlers.ensureCapacity(handlerconfs.size());
    	dwProtoHandlerThreads.ensureCapacity(handlerconfs.size());
    	
    	int hno = 0;
    	
		for(HierarchicalConfiguration hconf : handlerconfs)
		{
		    if (hconf.containsKey("Protocol"))
		    {
		    	if (hconf.getString("Protocol").equals("DriveWire"))
		    	{
		    		dwProtoHandlers.add(new DWProtocolHandler(hno, hconf));
		    	}
		    	else if (hconf.getString("Protocol").equals("MCX"))
		    	{
		    		dwProtoHandlers.add(new MCXProtocolHandler(hno, hconf));
		    	}
		    	else if (hconf.getString("Protocol").equals("VModem"))
		    	{
		    		dwProtoHandlers.add(new VModemProtocolHandler(hno, hconf));
		    	}
		    	else
		    	{
		    		logger.error("Unknown protocol '" + hconf.getString("Protocol") + "' in handler.");
		    	}
		    }
		    else
		    {
		    	// default to drivewire
		    	dwProtoHandlers.add(new DWProtocolHandler(hno, hconf));
		    }
		    
		    dwProtoHandlerThreads.add(new Thread(dwProtoHandlers.get(hno)) );
		    
		    
		    if (hconf.getBoolean("AutoStart", true))
		    {
		    	startHandler(hno);
    	    }
		    
		    hno++;
		}
	}




	public static void startHandler(int hno) 
	{
		if (dwProtoHandlerThreads.get(hno).isAlive())
		{
			logger.error("Requested start of already alive handler #" + hno);
		}
		else
		{
			logger.info("Starting handler #" + hno + ": " + dwProtoHandlers.get(hno).getClass().getSimpleName() );
	    	
	    	dwProtoHandlerThreads.get(hno).start();	
	    	
	    	while (!dwProtoHandlers.get(hno).isReady())
	    	{
	    		try 
	    		{
					Thread.sleep(100);
				} 
	    		catch (InterruptedException e) 
	    		{
	    			logger.warn("Interrupted while waiting for instance " + hno + "  to become ready.");
	    		}
	    	}
		}
	}


	public static void stopHandler(int hno) 
	{
		logger.info("Stopping handler #" + hno + ": " + dwProtoHandlers.get(hno).getClass().getSimpleName() );
		
		HierarchicalConfiguration hc = dwProtoHandlers.get(hno).getConfig();
		
		dwProtoHandlers.get(hno).shutdown();	
		
		try 
		{
			dwProtoHandlerThreads.get(hno).join(15000);
		} 
		catch (InterruptedException e) 
		{
			logger.warn("Interrupted while waiting for handler " + hno + " to exit");
		}
		
		dwProtoHandlers.remove(hno);
		dwProtoHandlers.add(hno,  new DWProtocolHandler(hno, hc));
		dwProtoHandlerThreads.remove(hno);
		dwProtoHandlerThreads.add(hno , new Thread(dwProtoHandlers.get(hno)) );
		
    }
    
    
	private static boolean checkRXTXLoaded() 
	{
		// try to load RXTX, redirect it's version messages into our logs
		
		PrintStream ops = System.out;
		PrintStream eps = System.err;
		
		ByteArrayOutputStream rxtxbaos = new ByteArrayOutputStream();
		ByteArrayOutputStream rxtxbaes = new ByteArrayOutputStream();
		
		PrintStream rxtxout = new PrintStream(rxtxbaos);
		PrintStream rxtxerr = new PrintStream(rxtxbaes);
		
		System.setOut(rxtxout);
		System.setErr(rxtxerr);
		
		boolean res = DWUtils.testClassPath("gnu.io.RXTXCommDriver");
		
		for (String l : rxtxbaes.toString().trim().split("\n"))
		{
			System.out.println(l);
			
			if (!l.equals(""))
				logger.warn(l);
		}
		
		for (String l : rxtxbaos.toString().trim().split("\n"))
		{
			System.out.println(l);
			// ignore pesky version warning that doesn't ever seem to matter
			if (!l.equals("WARNING:  RXTX Version mismatch") && !l.equals(""))
			{
				
				logger.debug(l);
			}
		}
	
		System.setOut(ops);
		System.setErr(eps);
		
		return(res);
	}




	@SuppressWarnings("unused")
	private static void loadRXTX() 
	{
		
		try 
		{
			String rxtxpath;
			
			if (!serverconfig.getString("LoadRXTXPath","").equals(""))
			{
				rxtxpath = serverconfig.getString("LoadRXTXPath");
			}
			else
			{
				// look for native/x/x in current dir
				File curdir = new File(".");
				rxtxpath = curdir.getCanonicalPath();
			
				// 	+ native platform dir
				String[] osparts = System.getProperty("os.name").split(" ");
			
				if (osparts.length < 1)
				{
					throw new DWPlatformUnknownException("No native dir for os '" + System.getProperty("os.name") + "' arch '" + System.getProperty("os.arch") + "'");
				}
			
				rxtxpath += File.separator + "native" + File.separator + osparts[0] + File.separator + System.getProperty("os.arch");
			}
			
			File testrxtxpath = new File(rxtxpath);
			logger.debug("Using rxtx lib path: " + rxtxpath);
			
			if (!testrxtxpath.exists())
			{
				throw new DWPlatformUnknownException("No native dir for os '" + System.getProperty("os.name") + "' arch '" + System.getProperty("os.arch") + "'");
			}
			
			// add this dir to path..
			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + rxtxpath);
		    
			//set sys_paths to null so they will be reread by jvm
			Field sysPathsField;
			sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
			sysPathsField.setAccessible(true);
		    sysPathsField.set(null, null);
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
			
			if (useDebug)
			{
				System.out.println("--------------------------------------------------------------------------------");
				e.printStackTrace();
				System.out.println("--------------------------------------------------------------------------------");
			}
				
		} 
				
	}






	private static void initLogging() 
	{
		Logger.getRootLogger().removeAllAppenders();
		consoleAppender = new ConsoleAppender(logLayout);
		Logger.getRootLogger().addAppender(consoleAppender);

		if (useLF5)
			Logger.getRootLogger().addAppender(lf5appender);
		
		
		if (useDebug)
			Logger.getRootLogger().setLevel(Level.ALL);
		else
			Logger.getRootLogger().setLevel(Level.INFO);
		
		
		
	}




	private static void doCmdLineArgs(String[] args) 
	{
		// set options from cmdline args
        Options cmdoptions = new Options();
		
		cmdoptions.addOption("config", true, "configuration file (defaults to config.xml)");
		cmdoptions.addOption("backup", false, "make a backup of config at server start");
		cmdoptions.addOption("help", false, "display command line argument help");
		cmdoptions.addOption("logviewer", false, "open GUI log viewer at server start");
		cmdoptions.addOption("debug", false, "log extra info to console");
		cmdoptions.addOption("nomidi", false, "disable MIDI");
		cmdoptions.addOption("nomount", false, "do not remount disks from last run");
		cmdoptions.addOption("noui", false, "do not start user interface");
		cmdoptions.addOption("noserver", false, "do not start server");
		cmdoptions.addOption("liteui", false, "use lite user interface");
		
		
		
		CommandLineParser parser = new GnuParser();
		try 
		{
			CommandLine line = parser.parse( cmdoptions, args );
		    
			// help
			if (line.hasOption("help"))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar DriveWire.jar [OPTIONS]", cmdoptions );
				System.exit(0);
			}
			
			if( line.hasOption( "config" ) ) 
			{
			    configfile = line.getOptionValue( "config" );
			}
			
			if (line.hasOption( "backup"))
			{
				useBackup  = true;
			}
			
			if (line.hasOption( "debug"))
			{
				useDebug  = true;
			}
			
			if( line.hasOption( "logviewer" ) ) 
			{
				useLF5 = true;
				lf5appender = new LF5Appender();
				lf5appender.setName("DriveWire 4 Server Log");
			
			}
			
			if (line.hasOption( "nomidi"))
			{
				noMIDI  = true;
			}
			
			if (line.hasOption( "nomount"))
			{
				noMount  = true;
			}
			
			if (line.hasOption( "noui"))
			{
				noUI  = true;
			}
			
			if (line.hasOption( "noserver"))
			{
				noServer  = true;
			}
			
			
			
		}
		catch( ParseException exp ) 
		{
		    System.err.println( "Could not parse command line: " + exp.getMessage() );
		    System.exit(-1);
		}
		
	}



	private static void backupConfig(String cfile)
	{
		try
		{
			DWUtils.copyFile(cfile,cfile +".bak");
			logger.debug("Backed up config to " + cfile +".bak" );
		} 
		catch (IOException e)
		{
			logger.error("Could not create config backup: " + e.getMessage());
		}
	}




	public static void serverShutdown() 
	{
		logger.info("server shutting down...");
		
		if (dwProtoHandlerThreads != null)
		{
			logger.debug("stopping protocol handler(s)...");
		
			for (DWProtocol p : dwProtoHandlers)
			{
				if (p != null)
				{
					p.shutdown();
				}
			}
			
			for (Thread t : dwProtoHandlerThreads)
			{
				if (t.isAlive())
					
				try 
				{	
					t.interrupt();
					t.join();
				} 
				catch (InterruptedException e) 
				{
					logger.warn(e.getMessage());
				}
			}
		
		}
		
		
		if (lazyWriterT != null)
		{
			logger.debug("stopping lazy writer...");
		
			lazyWriterT.interrupt();
			try 
			{
				lazyWriterT.join();
			} 
			catch (InterruptedException e) 
			{
				logger.warn(e.getMessage());
			}
		}
		
		
		if (uiObj != null)
		{
			logger.debug("stopping UI thread...");
			uiObj.die();
			try 
			{
				uiT.join();
			} 
			catch (InterruptedException e) 
			{
				logger.warn(e.getMessage());
			}
		}
		
		
		logger.info("server shutdown complete");
		logger.removeAllAppenders();
	}




	private static void startLazyWriter() 
	{
    	lazyWriterT = new Thread(new DWDiskLazyWriter());
		lazyWriterT.start();
	}




	public static boolean applyUISettings() 
	{
		if ((uiT != null) && (uiT.isAlive()))
		{
			uiObj.die();
			uiT.interrupt();
			try 
			{
				uiT.join();
			} 
			catch (InterruptedException e) 
			{
				logger.warn(e.getMessage());
			}
		}
		
		if (serverconfig.getBoolean("UIEnabled",false))
		{
			ServerSocket srvr = null;
			
			// check for port in use (another server providing UI on our port)
			try 
			{
				// check for listen address
					
				srvr = new ServerSocket(serverconfig.getInt("UIPort",6800));
				logger.info("UI listening on port " + srvr.getLocalPort());

			} 
			catch (IOException e2) 
			{
				
				// BAIL if UIorBust
				if (serverconfig.getBoolean("UIorBust",true))
					return false;
				
				logger.warn("Error opening UI socket: " + e2.getClass().getSimpleName() + " " + e2.getMessage());
				
			}
			
			if (srvr != null)
			{
				uiObj = new DWUIThread( srvr );
				uiT = new Thread(uiObj);
				uiT.start();
			}
			
		}

		return true;
	}




	public static void applyLoggingSettings() 
	{
		// logging
		if (!serverconfig.getString("LogFormat","").equals(""))
    	{
    		logLayout = new PatternLayout(serverconfig.getString("LogFormat"));
    	}
    	
    	Logger.getRootLogger().removeAllAppenders();
		
    	dwAppender = new DWLogAppender(logLayout);
    	Logger.getRootLogger().addAppender(dwAppender);
    	
    	if (useLF5)
			Logger.getRootLogger().addAppender(lf5appender);
    	
    	if (serverconfig.getBoolean("LogToConsole", true) || useDebug)
    	{
    		consoleAppender = new ConsoleAppender(logLayout);
    		Logger.getRootLogger().addAppender(consoleAppender);
    	}
    	
    	
    	if ((serverconfig.getBoolean("LogToFile", false)) && (serverconfig.containsKey("LogFile")))
    	{
    		
    		try 
    		{
    			fileAppender = new FileAppender(logLayout,serverconfig.getString("LogFile"),true,false,128);
    			Logger.getRootLogger().addAppender(fileAppender);
    		} 
    		catch (IOException e) 
    		{
    			logger.error("Cannot log to file '" + serverconfig.getString("LogFile") +"': " + e.getMessage());
    		}
    	 		
    	}
    	
    	if (useDebug)
    		Logger.getRootLogger().setLevel(Level.ALL);
    	else
    		Logger.getRootLogger().setLevel(Level.toLevel(serverconfig.getString("LogLevel", "INFO")));
    	
    	
		
    	
	}




	public static DWProtocol getHandler(int handlerno)
	{
		if ((handlerno < dwProtoHandlers.size()) && (handlerno > -1))
			return(dwProtoHandlers.get(handlerno));
		
		return null;
	}
	
	
	public static ArrayList<String> getLogEvents(int num)
	{
		return(dwAppender.getLastEvents(num));
	}



	public static int getLogEventsSize()
	{
		return(dwAppender.getEventsSize());
	}




	public static int getNumHandlers()
	{
		return dwProtoHandlers.size();
	}


	public static int getNumHandlersAlive()
	{
		int res = 0;
		
		for (DWProtocol p : dwProtoHandlers)
		{
			if (p != null)
			{
				if (!p.isDying() && p.isReady())
					res++;
			}
		}
		
		return res;
	}


	public static boolean isValidHandlerNo(int handler)
	{
		if ((handler < dwProtoHandlers.size()) && (handler >= 0))
		{
			if (dwProtoHandlers.get(handler) != null)
			{
				return true;
			}
		}
		
		return false;
	}




	public static void restartHandler(int handler)
	{
		
		logger.info("Restarting handler #" + handler);
		
		stopHandler(handler);
		startHandler(handler);
		
	}

	
	



	public static boolean handlerIsAlive(int h)
	{
		if ((dwProtoHandlers.get(h) != null) && (dwProtoHandlerThreads.get(h) != null))
		{
			if ((!dwProtoHandlers.get(h).isDying()) && (dwProtoHandlerThreads.get(h).isAlive() ))
			{
				return(true);
			}
		}
		
		return false;
	}
	
	


	public static String getHandlerName(int handlerno) 
	{
		if (isValidHandlerNo(handlerno))
		{
			return(dwProtoHandlers.get(handlerno).getConfig().getString("[@name]","unnamed instance " + handlerno));
		}
		
		return("null handler " + handlerno);
	}
	
	
	public static void saveServerConfig() throws ConfigurationException
	{
		serverconfig.save();
	}
	
	 @SuppressWarnings("unchecked")
	public static ArrayList<String> getAvailableSerialPorts() 
	 {
	        ArrayList<String> h = new ArrayList<String>();
	        
	        java.util.Enumeration<gnu.io.CommPortIdentifier> thePorts =  gnu.io.CommPortIdentifier.getPortIdentifiers();
	        while (thePorts.hasMoreElements()) 
	        {
	        	try
	        	{
		            gnu.io.CommPortIdentifier com = thePorts.nextElement();
		            if (com.getPortType() == gnu.io.CommPortIdentifier.PORT_SERIAL)
		                 h.add(com.getName());
	        	}
	        	catch (Exception e)
	        	{
	        		logger.error("While detecting serial devices: " + e.getMessage());
	        		
	        		if (useDebug)
	    			{
	    				System.out.println("--------------------------------------------------------------------------------");
	    				e.printStackTrace();
	    				System.out.println("--------------------------------------------------------------------------------");
	    			}
	        	}
	            
	        }
	
	        return h;

	 }



	public static String getSerialPortStatus(String port)
	{
		String res = "";
		
		try
		{
			CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(port);
			
			if (pi.isCurrentlyOwned())
			{
				res = "In use by " + pi.getCurrentOwner();
			}
			else
			{
				CommPort commPort = pi.open("DriveWireServer",2000);
				
				if ( commPort instanceof SerialPort )
				{
            		res = "Available";
					
				}
				else
				{
					res = "Not a serial port";
				}
				
				commPort.close();
			}
			
		} 
		catch (Exception e)
		{
			
			res = e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
			
			if (useDebug)
			{
				System.out.println("--------------------------------------------------------------------------------");
				e.printStackTrace();
				System.out.println("--------------------------------------------------------------------------------");
			}
			
		}
		
		return res;
	}
	 

	public static void shutdown() 
	{
		logger.info("server shutdown requested");
		wanttodie = true;
	}




	public static void submitServerConfigEvent(String propertyName, String propertyValue) 
	{
		if (uiObj != null)
		{
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_SERVERCONFIG, -1);
		
			evt.setParam(DWDefs.EVENT_ITEM_KEY, propertyName);
			evt.setParam(DWDefs.EVENT_ITEM_VALUE, propertyValue);
		
			uiObj.submitEvent(evt);
		}
	}


	public static void submitInstanceConfigEvent(int instance, String propertyName, String propertyValue) 
	{
		if (uiObj != null)
		{
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_INSTANCECONFIG, instance);
		
			evt.setParam(DWDefs.EVENT_ITEM_INSTANCE, String.valueOf(instance));
			evt.setParam(DWDefs.EVENT_ITEM_KEY, propertyName);
			evt.setParam(DWDefs.EVENT_ITEM_VALUE, propertyValue);
		
			uiObj.submitEvent(evt);
		}
	}


	public static void submitDiskEvent(int instance, int diskno, String key, String val) 
	{
		if (uiObj != null)
		{
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_DISK, instance);
			
			evt.setParam(DWDefs.EVENT_ITEM_INSTANCE, String.valueOf(instance));
			evt.setParam(DWDefs.EVENT_ITEM_DRIVE, String.valueOf(diskno));
			evt.setParam(DWDefs.EVENT_ITEM_KEY, key);
			evt.setParam(DWDefs.EVENT_ITEM_VALUE, val);
			
			uiObj.submitEvent(evt);
		}
	}
	

	public static void submitMIDIEvent(int instance, String key, String val) 
	{
		if (uiObj != null)
		{
			DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_MIDI, instance);
			
			evt.setParam(DWDefs.EVENT_ITEM_INSTANCE, String.valueOf(instance));
			evt.setParam(DWDefs.EVENT_ITEM_KEY, key);
			evt.setParam(DWDefs.EVENT_ITEM_VALUE, val);
			
			uiObj.submitEvent(evt);
		}
	}

	public static void submitLogEvent(LoggingEvent event) 
	{
		DWEvent evt = new DWEvent(DWDefs.EVENT_TYPE_LOG, -1);
		
		evt.setParam(DWDefs.EVENT_ITEM_LOGLEVEL, event.getLevel().toString());
		evt.setParam(DWDefs.EVENT_ITEM_TIMESTAMP, event.getTimeStamp()+"");
		evt.setParam(DWDefs.EVENT_ITEM_LOGMSG, event.getMessage().toString());
		evt.setParam(DWDefs.EVENT_ITEM_THREAD, event.getThreadName());
		evt.setParam(DWDefs.EVENT_ITEM_LOGSRC, event.getLoggerName());
		
		synchronized(logcache)
		{
			logcache.add(evt);
			if (logcache.size() > DWDefs.LOGGING_MAX_BUFFER_EVENTS)
				logcache.remove(0);
		}
		
		if (uiObj != null)
		{
			uiObj.submitEvent(evt);
		}
	}

	public static void submitEvent(DWEvent evt) 
	{
		if (uiObj != null)
		{
			uiObj.submitEvent(evt);
		}
	}



	public static boolean isReady() 
	{
		return DriveWireServer.ready ;
	}




	public static boolean testSerialPort_Open(String device) throws Exception
	{
		
		try
		{
			CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(device);
			
			if (pi.isCurrentlyOwned())
			{
				throw (new Exception("In use by " + pi.getCurrentOwner()));
			}
			else
			{
				CommPort commPort = pi.open("DriveWireTest",2000);
				
				if ( commPort instanceof SerialPort )
				{
            	
					testSerialPort = (SerialPort) commPort;
					
					testSerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					testSerialPort.enableReceiveThreshold(1);
					testSerialPort.enableReceiveTimeout(3000);
					
					return true;
					
				}
				else
				{
					throw (new Exception("Not a serial port"));
				}
				
			}
			
		} 
		catch (Exception e)
		{
			
			throw (new Exception(e.getLocalizedMessage()));
		}
		
		
		
	}

	public static boolean testSerialPort_setParams(int rate) throws Exception
	{
		try
		{
			testSerialPort.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			return true;
		}
		catch (Exception e)
		{
			
			throw (new Exception(e.getLocalizedMessage()));
		}
			
	}


	public static int testSerialPort_read() throws Exception
	{
		
		try
		{
			return( testSerialPort.getInputStream().read());
		}
		catch (Exception e)
		{
			
			throw (new Exception(e.getLocalizedMessage()));
		}
	}




	public static void testSerialPort_close()
	{
		try
		{
			testSerialPort.close();
		}
		catch (Exception e)
		{
			
		}
	}




	public static ArrayList<DWEvent> getLogCache()
	{
		return DriveWireServer.logcache;
	}




	public static boolean isConsoleLogging()
	{
		return(DriveWireServer.serverconfig.getBoolean("LogToConsole", false));
	}

	public static boolean isDebug()
	{
		return(useDebug);
	}




	

	
	
	
	
	public static long getMagic()
	{
		return(magic);
	}

	public static boolean getNoMIDI()
	{
		return noMIDI;
	}
	
	public static boolean getNoMount()
	{
		return noMount;
	}




	public static void setConfigFreeze(boolean b)
	{
		DriveWireServer.configFreeze  = b;
		
	}
	
	public static boolean isConfigFreeze()
	{
		return DriveWireServer.configFreeze;
	}




	public static void setLoggingRestart()
	{
		DriveWireServer.restart_logging = true;
	}
	
	public static void setUIRestart()
	{
		DriveWireServer.restart_ui = true;
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public static DWUIThread getDWUIThread()
	{
		return DriveWireServer.uiObj;
	}




	
	
	
}
