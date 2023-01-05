package com.groupunix.drivewireui;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireui.configeditor.ConfigEditor;
import com.groupunix.drivewireui.instanceman.InstanceMan;
import com.groupunix.drivewireui.library.CloudLibraryItem;
import com.groupunix.drivewireui.library.FolderLibraryItem;
import com.groupunix.drivewireui.library.LibraryItem;
import com.groupunix.drivewireui.library.MountedFolderLibraryItem;
import com.groupunix.drivewireui.nineserver.NineServer;
import com.groupunix.drivewireui.nineserver.OS9BufferGroup;
import com.groupunix.drivewireui.plugins.DWBrowser;
import com.groupunix.drivewireui.simplewizard.SimpleWizard;
import com.groupunix.drivewireui.updatewizard.NoUpdateDialog;
import com.groupunix.drivewireui.updatewizard.UpdateWizard;
import com.groupunix.dwlite.DWLite;
import com.swtdesigner.SWTResourceManager;



public class MainWin {

	static Logger logger = Logger.getLogger(MainWin.class);
	private static PatternLayout logLayout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %m%n");
	
	public static final int DWUIVersionMajor = 4;
	public static final int DWUIVersionMinor = 3;
	public static final int DWUIVersionBuild = 3;
	public static final String DWUIVersionRevision = "o";
	public static final String DWUIVersionDate = "06/21/2013";
	
	public static final Version DWUIVersion = new Version(DWUIVersionMajor, DWUIVersionMinor, DWUIVersionBuild, DWUIVersionRevision, DWUIVersionDate);
	
	public static final double LOWMEM_START = 4096 * 1024;
	public static final double LOWMEM_STOP = LOWMEM_START * 2;
	
	public static final int DISPLAY_ANIM_TIMER = 90;


	public final static int LTYPE_LOCAL_ROOT = 0;
	public final static int LTYPE_LOCAL_FOLDER = 1;
	public final static int LTYPE_LOCAL_ENTRY = 2;
	public final static int LTYPE_NET_ROOT = 10;
	public final static int LTYPE_NET_FOLDER = 11;
	public final static int LTYPE_NET_ENTRY = 12;
	public final static int LTYPE_CLOUD_ROOT = 20;
	public final static int LTYPE_CLOUD_FOLDER = 21;
	public final static int LTYPE_CLOUD_ENTRY = 22;
	
	
	public static boolean lowMem = false;
	
	public static final String default_Host = "127.0.0.1";
	public static final int default_Port = 6800;
	public static final int default_Instance = 0;
	
	public static final int default_DiskHistorySize = 20;
	public static final int default_ServerHistorySize = 20;
	public static final int default_CmdHistorySize = 20;
	
	public static final int default_TCPTimeout = 15000;
	
	
	public static XMLConfiguration config;
	public static XMLConfiguration master;
	public static HierarchicalConfiguration dwconfig;
	public static final String configfile = "drivewireUI.xml";

	public static LibraryItem[] libraryroot;
	
	private static int currentDisk = 0;
	
	protected static Shell shell;

	private static Text txtYouCanEnter;
	private static int cmdhistpos = 0;
	private static Display display;
	
	private static String host;
	private static int port;
	private static int instance;
	
	private static Boolean connected = false;
	
	

	static Table table;
	
	private static SashForm sashForm;
	
	private static MenuItem mntmFile;
	private static MenuItem mntmTools;
	private static MenuItem mntmHdbdosTranslation;

	
	private static Menu menu_file;
	private static Menu menu_tools;
	private static Menu menu_config;
	private static Menu menu_help;

	private static MenuItem mntmInstances;	
	private static MenuItem mntmInitialConfig;
	private static MenuItem mntmUserInterface;
	
	private static Thread syncThread = null;
	public static int dwconfigserial = -1;
	
	
	private static MenuItem mntmMidi;
	private static Menu menuMIDIOutputs;
	private static MenuItem mntmLockInstruments;
	private static Menu menuMIDIProfiles;
	private static MenuItem mntmSetProfile;
	private static MenuItem mntmSetOutput;
	private static Thread dwThread;
	private static Thread nsThread;
	public static CTabFolder tabFolderOutput;
	
	
	
	private static SyncThread syncObj;
	
	private static DiskDef[] disks = new DiskDef[256];
	private static MIDIStatus midiStatus;
	
	public static Color colorWhite;
	public static Color colorRed;
	public static Color colorGreen;
	public static Color colorBlack;
	public static Color colorCmdTxt;
	public static Color colorGraphBG;
	public static Color colorGraphFG;
	public static Color colorMemGraphFreeH;
	public static Color colorMemGraphFreeL;
	
	public static Color colorMemGraphUsed;
	
	
	protected static Font fontDiskNumber;
	protected static Font fontDiskGraph;
	public static Font fontGraphLabel;
	
	
	private static Boolean driveactivity = false;

	

	private static MenuItem mitemInsert;
	private static MenuItem mntmInsertFromUrl;
	private static MenuItem mitemEject;
	private static MenuItem mitemExport;
	private static MenuItem mitemCreate;
	private static MenuItem mitemParameters;
	private static MenuItem mitemReload;
	private static MenuItem mitemController;
	
	protected static Vector<DiskStatusItem> diskStatusItems;
	protected static Vector<LogItem> logItems = new Vector<LogItem>();
	
	private static Table logTable;
	
	private static boolean ready = false;
	
	private static Image diskLEDgreen;
	private static Image diskLEDred;
	private static Image diskLEDdark;
	protected static Image diskBigLEDgreen;
	protected static Image diskBigLEDred;
	protected static Image diskBigLEDdark;
	
	private static DiskTableUpdateThread diskTableUpdater;
	protected static boolean safeshutdown = false;
	
	
	private static ServerConfigWin serverconfigwin;

	static UITaskMaster taskman;
	static ScrolledComposite scrolledComposite;
	
	public static Font logFont;
	public static Font dialogFont;
	public static ErrorHelpCache errorHelpCache = new ErrorHelpCache();
	private static Composite compositeList;
	private Composite compServer;

	protected static Image graphMemUse;
	protected static Image graphDiskOps;
	protected static Image graphVSerialOps;
	
	protected static Canvas canvasMemUse;
	protected static Canvas canvasDiskOps;
	protected static Canvas canvasVSerialOps;
	
	protected static ServerStatusItem serverStatus = new ServerStatusItem();
	protected static long servermagic;
	private Menu menu_1;
	private MenuItem mntmRestartClientsOnOpen;
	private static CTabItem tbtmUi;
	private static boolean logscroll = true;
	private static MenuItem mntmCopy;
	private static MenuItem mntmSaveLogTo;
	private static LogItem lowMemLogItem;
	private static int lowMemWarningTid = -1;
	private static boolean noServer = false;
	static boolean debugging = false;
	protected static int logNoticeLevel = -1;
	
	public static Vector<OS9BufferGroup> os9BufferGroups;
	private static boolean serverLocal = false;
	
	
	public static void main(String[] args) 
	{
		// args we care about
		MainWin.noServer = UIUtils.hasArg(args,"noserver");
		
		
		// thread stuff
		Thread.currentThread().setName("dwuiMain-" + Thread.currentThread().getId());
		Thread.currentThread().setContextClassLoader(MainWin.class.getClassLoader());
		
		// setup logging
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(new ConsoleAppender(logLayout));
		
		// create lowmem entry now so we don't have to create it when we're actually low on memory..
		lowMemLogItem = new LogItem();
		lowMemLogItem.setLevel("WARN");
		lowMemLogItem.setSource("UI");
		lowMemLogItem.setThread(Thread.currentThread().getName());
		lowMemLogItem.setMessage("Due to low free memory, display of non-critical log events and some UI functions have been disabled.");
	
		
		// attempt to maintain control in times of insanity
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, final Throwable e)
			{
				System.out.println("Yikes!");
				System.out.println();
				System.out.println(UIUtils.getStackTrace(e));
				
				logger.warn(e.getClass().getSimpleName() + " in UI thread " + t.getName() + " " + t.getId());
				
				if ((MainWin.shell != null) && (!MainWin.shell.isDisposed()))
				{
				
					MainWin.shell.getDisplay().syncExec(new Runnable() 
					{

						@Override
						public void run()
						{
							BugDialog ew = new BugDialog(MainWin.shell, SWT.DIALOG_TRIM, e);
							ew.open();
							
						}
					});
				}
				
				
				if ((config != null) && (config.getBoolean("TermServerOnExit",false) || config.getBoolean("LocalServer",false) ))
				{
					if (MainWin.dwThread.isAlive())
						stopDWServer();
				}
				
				System.exit(1);
			}
		
		});
		
		
		// get our client config
		loadConfig();
		
		
		
		
		// fire up a server
		if (config.getBoolean("LocalServer",true) && !noServer)
			startDWServer(args);

		
		if (UIUtils.hasArg(args,"noui"))
		{
			logger.info("UI disabled (with --noui)");
		}
		else if (UIUtils.hasArg(args, "liteui"))
		{
			
			DWLite.main(args);
		}
		else
		{
			
			// make ourselves look pretty
			Display.setAppName("DriveWire");
			Display.setAppVersion(MainWin.DWUIVersion.toString());
		
			display = new Display();
			
			colorWhite = new Color(display, 255,255,255);
			colorRed = new Color(display, 255,0,0);
			colorGreen = new Color(display, 0,255,0);
			colorBlack = new Color(display, 0,0,0);
			colorCmdTxt = new Color(display, 0x90,0x90,0x90);

			colorGraphBG = new Color(display, 50,50,50);
			colorGraphFG = new Color(display, 200,200,200);
			
			colorMemGraphFreeH = new Color(display, 80,255,80);
			colorMemGraphFreeL = new Color(display, 40,200,40);
			
			colorMemGraphUsed = new Color(display, 200, 50, 50);
			
			
			
			diskLEDgreen = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledgreen.png");
			diskLEDred = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledred.png");
			diskLEDdark = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-leddark.png");
			diskBigLEDgreen = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledgreen-big.png");
			diskBigLEDred = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledred-big.png");
			diskBigLEDdark = org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-leddark-big.png");
			
			
			UIUtils.loadFonts();
			
			
			HashMap<String,Integer> fontmap = new HashMap<String,Integer>();
			
			fontmap.put("Droid Sans Mono", SWT.NORMAL);
			
			logFont = UIUtils.findFont(display, fontmap, "WARNING", 40, 12);
			
			fontmap.clear();
			fontmap.put("Droid Sans", SWT.NORMAL);
			
			fontGraphLabel = UIUtils.findFont(display, fontmap, "WARNING", 42, 13);
			
			graphMemUse = new Image(null,270,110);
			graphDiskOps = new Image(null,270,110);
			graphVSerialOps = new Image(null,270,110);
			
			// start grapher
			Thread gt = new Thread(new GrapherThread());
			gt.setDaemon(true);
			gt.start();
			
			// setup library roots
			
		
			if (! MainWin.config.containsKey("Library.Local.updated") )
			{
				MainWin.config.addProperty("Library.Local.autocreated", System.currentTimeMillis());
				MainWin.config.addProperty("Library.Local.updated", 0);
			}
			
			if (! MainWin.config.containsKey("Library.Cloud.updated") )
			{
				MainWin.config.addProperty("Library.Cloud.autocreated", System.currentTimeMillis());
				MainWin.config.addProperty("Library.Cloud.updated", 0);
			}
			
			
			HierarchicalConfiguration locallib = MainWin.config.configurationAt("Library.Local");
			
			MainWin.libraryroot = new LibraryItem[3];
			
			libraryroot[0] = new MountedFolderLibraryItem("Mounted");
			
			libraryroot[1] = new FolderLibraryItem("Local", locallib.getRoot());
			
			libraryroot[2] = new CloudLibraryItem("CoCoCloud");
			
			
			MainWin window = new MainWin();

			
			// macs are special, special things
			doMacStuff();
			
			// sync
			restartServerConn();
		
			
			// allocate nineserver buffers
			os9BufferGroups = new Vector<OS9BufferGroup>();
			os9BufferGroups.setSize(256); 
			
			// nineserver thread
			if (MainWin.config.getInt("NineServerPort",6309) > 0)
			{
				nsThread = new Thread(new NineServer(MainWin.config.getInt("NineServerPort",6309)));
				nsThread.setDaemon(true);
				nsThread.start();
			}
			
			// update check	
			// if (config.getBoolean("CheckForUpdates", true))
			//		doUpdateCheck();
			
			// get this party started
			
			
			window.open(display, args);
			
			
		}
			
		// game over.  flag to let threads know.
		host = null;
				
	}


	private static void startDWServer(final String[] args) 
	{
		dwThread = new Thread(new Runnable() {

			@Override
			public void run() 
			{

				try 
				{
					MainWin.servermagic = DriveWireServer.getMagic();
					DriveWireServer.main(args);
				} 
				catch (ConfigurationException e) 
				{
					logger.fatal(e.getMessage());
					System.exit(-1);
				}
			}
			
		});
		
		//dwThread.setDaemon(true);
		dwThread.start();
	}

	public static void stopDWServer()
	{
		if ((dwThread != null) && (!dwThread.isInterrupted()))
		{
			
			// 	interrupt the server..
			dwThread.interrupt();
			
				try 
				{
					dwThread.join(3000);
				
				} 
				catch (InterruptedException e) 
				{
				}
			
		}
	}
	

	private static void doMacStuff() 
	{
		// used to work.. swt doesnt do this now?
		
		//Menu systemMenu = display.getSystemMenu();
		//if (systemMenu != null) 
		//{
			// we've got a mac
			
			
		//}
		
	}




	private static void loadConfig() 
	{
		try 
    	{
			// dwui config..
			
			File f = new File(configfile);
			
			if (f.exists())
			{
				config = new XMLConfiguration(configfile);	
			}
			else
			{
				logger.info("Creating new UI config file");
				config = new XMLConfiguration();
				config.setFileName(configfile);
				config.addProperty("AutoCreated", true);
				config.save();
			
					
			}
			
			config.setAutoSave(true);
			
			MainWin.host = config.getString("LastHost",default_Host);
			MainWin.port = config.getInt("LastPort",default_Port);
			MainWin.instance = config.getInt("LastInstance",default_Instance);
			
			
			// clear update lock
			config.setProperty("UpdateRestartRequired", false);
			
			
			// master file
			try
			{
				master = new XMLConfiguration(config.getString("MasterPath","master.xml"));
				master.setAutoSave(false);
			}
			catch (ConfigurationException e1) 
		    {
				logger.error("Could not load master config, some functions will not work correctly:  " + e1.getMessage());	
				
		    }
			
			
			// server
			
			if (config.getBoolean("LocalServer",true) && !noServer)
			{
				f = new File(config.getString("ServerConfigFile", "config.xml"));
				
				if (!f.exists())
				{
					// try to make a default server config
					f = new File("default/serverconfig.xml");
					if (!f.exists())
					{
						logger.fatal("LocalServer is true, but server config can not be found or created.");
						System.exit(-1);
					}
					else
					{
						logger.info("Creating new server config file");
						try 
						{
							UIUtils.fileCopy(f.getCanonicalPath(), config.getString("ServerConfigFile", "config.xml"));
						} 
						catch (IOException e) 
						{
							logger.fatal("Error copying default server config: " + e.getMessage());
							System.exit(-1);
						}
					}
				}
			}
		} 
    	catch (ConfigurationException e1) 
    	{
    		System.out.println("Fatal - Could not process config file '" + configfile + "'.  Please consult the documentation.");
    		System.exit(-1);
		} 
  
		
	}


	
	


	public void open(final Display display, String[] args) {
		
		createContents();
		
		MainWin.diskTableUpdater = new DiskTableUpdateThread();
		Thread dtuThread = new Thread(MainWin.diskTableUpdater);
		dtuThread.setDaemon(true);
		dtuThread.start();
		
		
		
		if (!connected)
		{
			MainWin.setItemsConnectionEnabled(false);
	
		}
		
		shell.open();
		shell.layout();

		
		
		
		// drive light and other animations
		
		 Runnable drivelightoff = new Runnable() 
		 	{
			 
			 private int ctr = 0;
			 
		      public void run() 
		      {
		    	  if (MainWin.ready)
		    	  {
			    	  ctr++;
			    	  if ((ctr % 12 == 0) && (MainWin.driveactivity.booleanValue() == true))
			    	  {
		    			  
		    			  for (int i=0;i<256;i++)
		    			  {
		    				  if (MainWin.table.getItem(i) != null)
		    				  {
		    					  if ((disks[i] != null) && (disks[i].isLoaded()))
		    						  MainWin.diskTableUpdater.addUpdate(i,"LED",MainWin.diskLEDdark);
		    					 
		    				  }
		    				 
		    			  }
	
			    		  MainWin.driveactivity = false;
			    	  }
				  	 
			    	
			    	  MainWin.taskman.rotateWaiters();
			    	  
			    	  display.timerExec(MainWin.DISPLAY_ANIM_TIMER, this);
		    	  }
		      }
		    };
		    
		    
	    display.timerExec(2000, drivelightoff);
		
	    
	    MainWin.ready = true;
		
	    int tid = MainWin.taskman.addTask("/splash");
		MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE, MainWin.DWUIVersion + " (" + MainWin.DWUIVersionDate + ")" );
	    
		MainWin.doSplashTimers(tid, true);
		
		// initial selection?
		MainWin.sashForm.forceFocus();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}

	
	
	




	private static void doSplashTimers(final int tid, boolean both)
	{
		final Runnable anim2 =   new Runnable() {
			public void run()
			  {
				try
				{
				
					for (int i = 0;i<10;i++)
					{
							((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim2(i);
					
					}
					
					for (int i = 9;i>=0;i--)
					{
						((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim2(i);
						
					}
					
	
					((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).showVer();

				} 
				catch (DWUINoSuchTaskException e)
				{
				}

			  }
			  
		};
		
	    Runnable anim1 =   new Runnable() {
					  public void run()
					  {
						  
						 try { 
						  if (((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim())
						  	display.timerExec(10, this);
						  else
							display.timerExec(20, anim2);
						 }
						  catch (DWUINoSuchTaskException e)
							{
							}
					  }
				  };
				  
	    if (both)
	    	display.timerExec(750, anim1);
	    else
	    	display.timerExec(100, anim2);
	}


	
	

	

	private static void updateTitlebar() 
	{
		String txt = "DriveWire - " + host + ":" + port + " (";
		
		if (MainWin.getInstanceConfig() != null)
			txt += MainWin.getInstanceConfig().getString("[@name]", "Instance " + instance) + ")";
		else
			txt += "Instance " + instance + ")";
		
		shell.setText(txt);
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		

		
		shell.setImage(SWTResourceManager.getImage(MainWin.class, "/dw/dw4square.jpg"));
		shell.addShellListener(new ShellAdapter() {
			

			@Override
			public void shellClosed(ShellEvent e) 
			{
				 doShutdown();
				
			}
		});
		
		// 		shell.setSize(config.getInt("MainWin_Width",753), config.getInt("MainWin_Height", 486));
		shell.setSize(config.getInt("MainWin_Width",753), config.getInt("MainWin_Height", 486));
		
		if (config.containsKey("MainWin_x") && config.containsKey("MainWin_y"))
		{
			Point p = new Point(config.getInt("MainWin_x",0), config.getInt("MainWin_y",0));
			
			if (MainWin.isValidDisplayPos(p))
				shell.setLocation(p);
		}
		
		shell.setText("DriveWire User Interface");
		shell.setLayout(new BorderLayout(0, 2));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		// file menu
		
		mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		menu_file = new Menu(mntmFile);
		mntmFile.setMenu(menu_file);
		
		MenuItem mntmChooseServer = new MenuItem(menu_file, SWT.NONE);
		mntmChooseServer.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/computer-go.png"));
		mntmChooseServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				ChooseServerWin chooseServerWin = new ChooseServerWin(shell,SWT.DIALOG_TRIM);
				chooseServerWin.open();
				
			}
		});
		mntmChooseServer.setText("Choose server...");
		
		
		mntmInstances = new MenuItem(menu_file, SWT.CASCADE);
		mntmInstances.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/view-list-tree-4.png"));
		mntmInstances.setText("Choose instance");
		
		final Menu menuInstances = new Menu(mntmInstances);
		mntmInstances.setMenu(menuInstances);
		
		
		mntmInstances.addArmListener(new ArmListener() 
		{
			public void widgetArmed(ArmEvent e) 
			{
				for (MenuItem i : menuInstances.getItems())
				{
					i.dispose();
				}
				
				try
				{
					List<String> res = UIUtils.loadList(MainWin.getInstance(), "ui server show instances");
					
					if (res !=null)
					{
						for (String l : res)
						{
							final String[] parts = l.split("\\|");
							int intno = -1;
							
							try
							{
								intno = Integer.parseInt(parts[0]);
							}
							catch (NumberFormatException e2)
							{
								
							}
							
							if ((parts.length > 1) && (intno > -1))
							{
								MenuItem tmp = new MenuItem(menuInstances, SWT.CHECK);
								
								tmp.setText(parts[1]);
								
								if (intno == MainWin.getInstance())
								{
									tmp.setSelection(true);
								}
								else
								{
									tmp.setSelection(false);
								}
								
								final int fintno = intno;
								
								tmp.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) 
									{
										MainWin.setInstance(fintno);
										MainWin.restartServerConn();
									}
								});
								
							}
						}
						
					}
				} 
				catch (IOException e1)
				{
						} 
				catch (DWUIOperationFailedException e1)
				{
 				}
				
			}
		});
						
						
		
		
		
	
		
		new MenuItem(menu_file, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menu_file, SWT.NONE);
		mntmExit.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/application-exit-5.png"));
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{

				shell.close();
			}
		});
		mntmExit.setText("Exit");
		
		
		
		// tools menu
		
		mntmTools = new MenuItem(menu, SWT.CASCADE);

		mntmTools.setText("Tools");
		
		menu_tools = new Menu(mntmTools);
		mntmTools.setMenu(menu_tools);
		
		menu_tools.addMenuListener(new MenuListener() {

			@Override
			public void menuHidden(MenuEvent arg0) {
				
			}

			@Override
			public void menuShown(MenuEvent arg0) 
			{
				// set menu toggles
				mntmHdbdosTranslation.setSelection(MainWin.getInstanceConfig().getBoolean("HDBDOSMode", false));
				mntmRestartClientsOnOpen.setSelection(MainWin.getInstanceConfig().getBoolean("RestartClientsOnOpen", false));
			}
			
		});
		
		MenuItem mntmEjectAllDisks = new MenuItem(menu_tools, SWT.NONE);
		mntmEjectAllDisks.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/media-eject.png"));
		mntmEjectAllDisks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.sendCommand("dw disk eject all");
			}
		});
		mntmEjectAllDisks.setText("Eject all disks");
		
		new MenuItem(menu_tools, SWT.SEPARATOR);
		
		mntmHdbdosTranslation = new MenuItem(menu_tools, SWT.CHECK);
		mntmHdbdosTranslation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (mntmHdbdosTranslation.getSelection())
				{
					MainWin.sendCommand("dw config set HDBDOSMode true");
				}
				else
				{
					MainWin.sendCommand("dw config set HDBDOSMode false");
				}
				
	
			}
		});
		mntmHdbdosTranslation.setText("HDBDOS translation");
		
		
		
		new MenuItem(menu_tools, SWT.SEPARATOR);
		
		MenuItem mntmTimers = new MenuItem(menu_tools, SWT.NONE);
		mntmTimers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				TimerWin tw = new TimerWin(Display.getCurrent().getActiveShell(), 0);
				tw.open();
	
			}
		});
		mntmTimers.setText("Display timers");
		mntmTimers.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/constatus/user-away.png"));
		
		
		new MenuItem(menu_tools, SWT.SEPARATOR);
		
		MenuItem mntmRestart = new MenuItem(menu_tools, SWT.NONE);
		mntmRestart.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.sendCommand("dw client restart");
			}
		});
		mntmRestart.setText("Send restart request to client device");
		mntmRestart.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/document-quick_restart.png"));
		
		
		mntmRestartClientsOnOpen = new MenuItem(menu_tools, SWT.CHECK);
		mntmRestartClientsOnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (mntmRestartClientsOnOpen.getSelection())
				{
					MainWin.sendCommand("dw config set RestartClientsOnOpen true");
				}
				else
				{
					MainWin.sendCommand("dw config set RestartClientsOnOpen false");
				}
				
	
			}
		});
		
		mntmRestartClientsOnOpen.setText("Restart clients when server starts");
		
		
		
		
		new MenuItem(menu_tools, SWT.SEPARATOR);
		
		
		final MenuItem mntmCheckUpdates = new MenuItem(menu_tools, SWT.NONE);
		mntmCheckUpdates.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
					doUpdateCheck();
				
			}
		});
		mntmCheckUpdates.setText("Check for new version...");
		mntmCheckUpdates.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/wand.png"));
		
		
		
		

		// config menu
				
		MenuItem mntmConfig = new MenuItem(menu, SWT.CASCADE);
		mntmConfig.setText("Config");
		
		menu_config = new Menu(mntmConfig);
		mntmConfig.setMenu(menu_config);
		
		mntmInitialConfig = new MenuItem(menu_config, SWT.NONE);
		mntmInitialConfig.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/wand.png"));
		mntmInitialConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				SimpleWizard ww = new SimpleWizard();
				WizardDialog dialog = new WizardDialog(shell, ww);
			    dialog.create();
			    dialog.open();
			    
			}
		});
		mntmInitialConfig.setText("Simple Config Wizard...");
		
		new MenuItem(menu_config, SWT.SEPARATOR);
		
		MenuItem mntmServer_1 = new MenuItem(menu_config, SWT.NONE);
		mntmServer_1.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/computer-edit.png"));
		mntmServer_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{

				
				ConfigEditor ce = new ConfigEditor(display);
				if (!ce.isDisposed())
					ce.open();
			
			}
		});
		mntmServer_1.setText("Configuration Editor...");
		mntmServer_1.setEnabled(false);

		
		mntmUserInterface = new MenuItem(menu_config, SWT.CASCADE);
		
		mntmUserInterface.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/image-edit.png"));
		
		mntmUserInterface.setText("User Interface Options");
		
		menu_1 = new Menu(mntmUserInterface);
		mntmUserInterface.setMenu(menu_1);
		
		final MenuItem mntmUseInternalServer = new MenuItem(menu_1, SWT.CHECK);
		mntmUseInternalServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (config.getBoolean("LocalServer",false))
				{
					config.setProperty("LocalServer", false);
					
					stopDWServer();
					
				}
				else
				{
					config.setProperty("LocalServer", true);
					
					startDWServer(null);
				}
			}
		});
		mntmUseInternalServer.setText("Use internal server");
		
		
		
		final MenuItem mntmUseRemoteFile = new MenuItem(menu_1, SWT.CHECK);
		mntmUseRemoteFile.setText("Use remote file dialogs");
		mntmUseRemoteFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (config.getBoolean("UseRemoteFilebrowser",false))
				{
					config.setProperty("UseRemoteFilebrowser", false);
				}
				else
				{
					config.setProperty("UseRemoteFilebrowser", true);
				}
			}
		});
		
	
		
		final MenuItem mntmNoBrowsers = new MenuItem(menu_1, SWT.CHECK);
		mntmNoBrowsers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (config.getBoolean("NoBrowsers",false))
				{
					config.setProperty("NoBrowsers", false);
					
				}
				else
				{
					config.setProperty("NoBrowsers", true);
									
				}
			}
		});
		mntmNoBrowsers.setText("Disable web browsers");
		
		
		
		
		
		
		mntmUserInterface.addArmListener(new ArmListener() {
			public void widgetArmed(ArmEvent e) 
			{
				if ((MainWin.dwThread != null) && MainWin.dwThread.isAlive())
					mntmUseInternalServer.setSelection(true);
				else
					mntmUseInternalServer.setSelection(false);
				
				mntmUseRemoteFile.setSelection(config.getBoolean("UseRemoteFilebrowser", false));
				mntmNoBrowsers.setSelection(config.getBoolean("NoBrowsers", false));
				mntmCheckUpdates.setSelection(config.getBoolean("CheckForUpdates", true));
			}
		});
		
		
		mntmMidi = new MenuItem(menu_config, SWT.CASCADE);

		mntmMidi.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/audio-keyboard.png"));
		mntmMidi.setText("MIDI");
		
		Menu menu_6 = new Menu(mntmMidi);
		mntmMidi.setMenu(menu_6);
		
		mntmSetOutput = new MenuItem(menu_6, SWT.CASCADE);
		mntmSetOutput.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/audio-volume-high.png"));
		mntmSetOutput.setText("Set output");
		
		menuMIDIOutputs = new Menu(mntmSetOutput);
		mntmSetOutput.setMenu(menuMIDIOutputs);
				
		
		MenuItem mntmLoadSoundbank = new MenuItem(menu_6, SWT.CASCADE);
		mntmLoadSoundbank.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String path = getFile(false, false, "", "Choose a soundbank file to load...", "Open");
			
				if (path != null)
					MainWin.sendCommand("dw midi synth bank " + path );
			
			}
		});
		mntmLoadSoundbank.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/music.png"));
		mntmLoadSoundbank.setText("Load soundbank...");
		
		
		
		mntmSetProfile = new MenuItem(menu_6, SWT.CASCADE);
		mntmSetProfile.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/format-text-columns.png"));
		mntmSetProfile.setText("Set profile");
		
		menuMIDIProfiles = new Menu(mntmSetProfile);
		mntmSetProfile.setMenu(menuMIDIProfiles);
		
		mntmLockInstruments = new MenuItem(menu_6, SWT.CHECK);
		mntmLockInstruments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				sendCommand("dw midi synth lock");
			}
		});
		mntmLockInstruments.setText("Lock instruments");
		
		final MenuItem mntmPrinting = new MenuItem(menu_config, SWT.CASCADE);
		
		mntmPrinting.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/document-print.png"));
		mntmPrinting.setText("Current Printer");
		
		final Menu menu_2 = new Menu(mntmPrinting);
		mntmPrinting.setMenu(menu_2);
		
		mntmPrinting.addArmListener(new ArmListener() {
			public void widgetArmed(ArmEvent e) {
				for (MenuItem i : menu_2.getItems())
				{
					i.dispose();
				}
				
				try
				{
					List<String> res = UIUtils.loadList(MainWin.getInstance(), "ui instance printer");
					
					if (res !=null)
					{
						String curp = "";
						
						for (String l : res)
						{
							final String[] parts = l.split("\\|");
							if (parts.length > 1)
							{
								if (parts[0].equals("currentprinter"))
									curp = parts[1].trim();
								if (parts[0].equals("printer") && (parts.length == 3))
								{
									MenuItem tmp = new MenuItem(menu_2, SWT.CHECK);
									tmp.setText(parts[1] + ": " + parts[2].trim());
									
									if (parts[1].equals(curp))
										tmp.setSelection(true);
									else
										tmp.setSelection(false);
									
									tmp.addSelectionListener(new SelectionAdapter() {
										@Override
										public void widgetSelected(SelectionEvent e) 
										{
											sendCommand("dw config set CurrentPrinter " + parts[1]);
											
										}
									});
									
								}
							}
						}
						
					}
				} 
				catch (IOException e1)
				{
						} 
				catch (DWUIOperationFailedException e1)
				{
 				}
				
			}
		});
		
		new MenuItem(menu_config, SWT.SEPARATOR);
		
		MenuItem mntmInstMan = new MenuItem(menu_config, SWT.NONE);
		mntmInstMan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				InstanceMan im = new InstanceMan(display);
				im.open();
	
			}
		});
		mntmInstMan.setText("Instance Manager..");
		mntmInstMan.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/database-gear.png"));
		
		
		MenuItem mntmResetInstanceDevice = new MenuItem(menu_config, SWT.NONE);
		mntmResetInstanceDevice.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/toolbar/arrow-refresh.png"));
		mntmResetInstanceDevice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				sendCommand("ui instance reset protodev");
				
			}
		});
		mntmResetInstanceDevice.setText("Reset Current Instance Device");

		
		
		// help menu
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		menu_help = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_help);
		
		MenuItem mntmDocumentation = new MenuItem(menu_help, SWT.NONE);
		mntmDocumentation.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/world-link.png"));
		mntmDocumentation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				openURL(this.getClass(),"http://sourceforge.net/apps/mediawiki/drivewireserver/index.php");
			}
		});
		mntmDocumentation.setText("Documentation Wiki");
		
		
		
			
		new MenuItem(menu_help, SWT.SEPARATOR);
		
		MenuItem mntmSubmitBugReport = new MenuItem(menu_help, SWT.NONE);
		mntmSubmitBugReport.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/bug.png"));
		mntmSubmitBugReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				BugReportWin brwin = new BugReportWin(shell,SWT.DIALOG_TRIM,"User submitted","User submitted", "User submitted");
				brwin.open();
			}
		});
		mntmSubmitBugReport.setText("Submit bug report...");
		
		new MenuItem(menu_help, SWT.SEPARATOR);
		
		MenuItem mntmAbout = new MenuItem(menu_help, SWT.NONE);
		mntmAbout.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/help-about-3.png"));
		mntmAbout.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				AboutWin window = new AboutWin(shell,SWT.SHELL_TRIM);
				window.open();
			}
		});
		mntmAbout.setText("About...");
		
		
		
		txtYouCanEnter = new Text(shell, SWT.BORDER);
		txtYouCanEnter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (txtYouCanEnter.getText().equals("Hint: You can enter 'dw' commands here.  Enter dw by itself for help."))
				{
					txtYouCanEnter.setText("");
				}
			}
		});
		
		txtYouCanEnter.setText("Hint: You can enter 'dw' commands here.  Enter dw by itself for help.");
		txtYouCanEnter.setLayoutData(BorderLayout.SOUTH);
		
	   
	    
	    
	    
		sashForm = new SashForm(shell, SWT.SMOOTH | SWT.VERTICAL);

		sashForm.setLayoutData(BorderLayout.CENTER);

		
		compositeList = new Composite(sashForm, SWT.NONE);
		compositeList.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		createOutputTabs();
		updateTitlebar();
		
		
		// disk table
		
		table = new Table(compositeList, SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) 
			{
				int drive = table.getSelectionIndex();
				
				if ((drive > -1) && (MainWin.disks != null) && (MainWin.disks[drive] != null))
				{
					if (MainWin.disks[drive].isLoaded())
					{
						if (MainWin.disks[drive].hasDiskwin())
						{
							MainWin.disks[drive].getDiskwin().shlDwDrive.setActive();
						}
						else
						{
							if (!lowMem)
							{
								MainWin.disks[drive].setDiskwin(new DiskWin( MainWin.disks[drive],getDiskWinInitPos(drive).x,getDiskWinInitPos(drive).y));
								MainWin.disks[drive].getDiskwin().open(display);
							}

						}
					}
					else
					{
						if (!lowMem)
							quickInDisk(drive);
				
					}
				
				}
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.currentDisk = table.getSelectionIndex();
				
			
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createDiskTableColumns();
		
		
		
		// rmb menu for disks...
		
		Menu diskPopup = new Menu (shell, SWT.POP_UP);
		diskPopup.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) 
			{
				
				int sdisk = table.getSelectionIndex();
				if (sdisk > -1)
				{
					if (lowMem)
					{
						mitemInsert.setText ("Insert disabled due to low mem");
						mntmInsertFromUrl.setText ("Insert from URL disabled due to low mem");
						mitemCreate.setText ("Create disabled due to low mem");
						mitemParameters.setText ("Parameters disabled due to low mem");
						mitemController.setText ("Controller disabled due to low mem");
						
					}
					else
					{
						mitemInsert.setText ("Insert disk for drive " + sdisk + "...");
						mntmInsertFromUrl.setText ("Insert disk from URL for drive " + sdisk + "...");
						mitemCreate.setText ("Create new disk for drive " + sdisk + "...");
						mitemParameters.setText ("Drive " + sdisk + " parameters...");
						mitemController.setText ("Open controller for drive " + sdisk + "...");
						
					}
					
					mitemReload.setText ("Reload disk in drive " + sdisk + "...");
					mitemExport.setText("Export image in drive " + sdisk + " to...");
					mitemEject.setText ("Eject disk in drive " + sdisk);
					
					mitemInsert.setEnabled(false);
					mntmInsertFromUrl.setEnabled(false);
					mitemCreate.setEnabled(false);
					mitemExport.setEnabled(false);	
					mitemEject.setEnabled(false);
					mitemParameters.setEnabled(false);
					mitemReload.setEnabled(false);
					mitemController.setEnabled(false);
					
					if ((MainWin.disks != null) && (MainWin.disks[sdisk] != null))
					{
						if (!lowMem)
						{
							mitemInsert.setEnabled(true);
							mntmInsertFromUrl.setEnabled(true);
							mitemCreate.setEnabled(true);
							mitemController.setEnabled(true);
							mitemParameters.setEnabled(true);
						}
							
						if (MainWin.disks[sdisk].isLoaded())
						{
							mitemEject.setEnabled(true);
							mitemExport.setEnabled(true);
							mitemReload.setEnabled(true);
						}
					}
					
				}
			}
		});
		
		
		
		mitemInsert = new MenuItem (diskPopup, SWT.PUSH);
		mitemInsert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				quickInDisk(table.getSelectionIndex());
			}
		});
		mitemInsert.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-insert.png"));
		mitemInsert.setText ("Insert...");
		
		mntmInsertFromUrl = new MenuItem(diskPopup, SWT.NONE);
		mntmInsertFromUrl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				quickURLInDisk(table.getSelectionIndex());
			}
		});
		
		mntmInsertFromUrl.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/world-link.png"));
		mntmInsertFromUrl.setText("Insert from URL...");
		
		new MenuItem(diskPopup, SWT.SEPARATOR);
		
		mitemReload = new MenuItem(diskPopup, SWT.NONE);
		mitemReload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//MainWin.sendCommand("dw disk reload " + table.getSelectionIndex(), false);
				MainWin.sendCommand("dw disk reload " + table.getSelectionIndex());
			}
		});
		mitemReload.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/toolbar/arrow-refresh.png"));
		mitemReload.setText("Reload...");
		
		
		mitemExport = new MenuItem (diskPopup, SWT.PUSH);
		mitemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.writeDiskTo(MainWin.getCurrentDiskNo());
			}
		});
		mitemExport.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/document-save-as-5.png"));
		mitemExport.setText ("Export...");
		
		
		mitemCreate = new MenuItem (diskPopup, SWT.PUSH);
		mitemCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				CreateDiskWin window = new CreateDiskWin(shell,SWT.DIALOG_TRIM);
				
				window.open();
			}
		});
		mitemCreate.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/new-disk-16.png"));
		mitemCreate.setText ("Create...");
		
		
		new MenuItem(diskPopup, SWT.SEPARATOR);
		
		
		mitemEject = new MenuItem (diskPopup, SWT.PUSH);
		mitemEject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				MainWin.sendCommand("dw disk eject " + table.getSelectionIndex());
			}
		});
		mitemEject.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/media-eject.png"));
		mitemEject.setText ("Eject");
		
		
		
		new MenuItem(diskPopup, SWT.SEPARATOR);
		
		mitemController = new MenuItem(diskPopup, SWT.NONE);
		mitemController.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int drive = table.getSelectionIndex();
				if ((drive > -1) && (MainWin.disks != null) && (MainWin.disks[drive] != null))
				{
					if (MainWin.disks[drive].hasDiskwin())
					{
						MainWin.disks[drive].getDiskwin().shlDwDrive.setActive();
					}
					else
					{
						MainWin.disks[drive].setDiskwin(new DiskWin( MainWin.disks[drive],getDiskWinInitPos(drive).x,getDiskWinInitPos(drive).y));
						MainWin.disks[drive].getDiskwin().open(display);
	
					}
				}
				else
				{
					showError("Disk system not initialized", "It seems our disk drive objects are null." , "Maybe the server is still starting up, or maybe it has a serious problem.");
				}
			}
		});
		mitemController.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-controller.png"));
		mitemController.setText("Controller...");
		
		mitemParameters = new MenuItem (diskPopup, SWT.PUSH);
		mitemParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (MainWin.disks[table.getSelectionIndex()].hasParamwin())
				{
					MainWin.disks[table.getSelectionIndex()].getParamwin().shell.setActive();
				}
				else
				{
					MainWin.disks[table.getSelectionIndex()].setParamwin(new DiskAdvancedWin(shell, SWT.DIALOG_TRIM, MainWin.disks[table.getSelectionIndex()]));
					MainWin.disks[table.getSelectionIndex()].getParamwin().open();

				}
			}
		});
		mitemParameters.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-params.png"));
		mitemParameters.setText ("Parameters...");
		
		table.setMenu(diskPopup);
		
		
	
		
		
		
		if ((config.getInt("SashForm_Weights(0)", 1) != 0) && (config.getInt("SashForm_Weights(1)", 1) != 0))
			setSashformWeights(new int[] { config.getInt("SashForm_Weights(0)", 391), config.getInt("SashForm_Weights(1)", 136)});
		
		
		txtYouCanEnter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if (e.character == 13)
				{
					if (!txtYouCanEnter.getText().trim().equals(""))
					{
						MainWin.tabFolderOutput.setSelection(MainWin.tbtmUi);
						MainWin.sendCommand(txtYouCanEnter.getText().trim(), true);
						MainWin.addCommandToHistory(txtYouCanEnter.getText().trim());
						txtYouCanEnter.setText("");
						MainWin.cmdhistpos = 0;
					}
				}
				else if (e.keyCode == 16777217)
				{
					// up
					if (config.getInt("CmdHistorySize",default_CmdHistorySize) > 0)
					{
						
						@SuppressWarnings("unchecked")
						List<String> cmdhist = config.getList("CmdHistory",null);
						
						if (cmdhist != null)
						{
							if (cmdhist.size() > MainWin.cmdhistpos)
							{
								MainWin.cmdhistpos++;

								txtYouCanEnter.setText(cmdhist.get(cmdhist.size() - MainWin.cmdhistpos));
								txtYouCanEnter.setSelection(txtYouCanEnter.getText().length() + 1);
								
								e.doit = false;
								
							}
						}
						
					}
				}
				else if (e.keyCode == 16777218)
				{
					// down
					if (config.getInt("CmdHistorySize",default_CmdHistorySize) > 0)
					{
						@SuppressWarnings("unchecked")
						List<String> cmdhist = config.getList("CmdHistory",null);
						
						if (MainWin.cmdhistpos > 1)
						{
							MainWin.cmdhistpos--;
							txtYouCanEnter.setText(cmdhist.get(cmdhist.size() - MainWin.cmdhistpos));
							txtYouCanEnter.setSelection(txtYouCanEnter.getText().length() + 1);
							
						}
						else if (MainWin.cmdhistpos == 1)
						{
							MainWin.cmdhistpos--;
							txtYouCanEnter.setText("");
						}
					}
				}
			}
		});
		
		/*  Tray... doesn't work so well
		
	    Tray tray = display.getSystemTray();
	    if (tray != null) {
	      TrayItem item = new TrayItem(tray, SWT.NONE);
	      
	      item.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/dw/dw4square.jpg"));
	      
	      final Menu traymenu = new Menu (shell, SWT.POP_UP);
			for (int i = 0; i < 4; i++) {
				
				MenuItem mi = new MenuItem (traymenu, SWT.CASCADE);
				mi.setText ("Drive " + i);
				
				Menu dmenu = new Menu(mi);
				mi.setMenu(dmenu);
				MenuItem mins = new MenuItem(dmenu, SWT.PUSH);
				mins.setText("Insert..");
				final int dno = i;
				mins.addListener (SWT.Selection, new Listener () {
					public void handleEvent (Event event) 
					{
						quickInDisk(dno);
					}
				});
				//if (i == 0) traymenu.setDefaultItem(mi);
			}
			item.addListener (SWT.MenuDetect, new Listener () {
				public void handleEvent (Event event) {
					traymenu.setVisible (true);
				}
			});
	      
	      
	    } 
		
		*/
		
	}

	

	
	
	
	protected static String doVerifyVersion() 
	{
		String res = "";
		
		String cr = System.getProperty("line.separator");
		
		try 
		{
			res = "Running version is " + MainWin.DWUIVersion.toString() + cr + cr;
			
			Updater up;
			
			up = new Updater();
			
			Version ver = up.getVersion(MainWin.DWUIVersionMajor, MainWin.DWUIVersionMinor, MainWin.DWUIVersionBuild, MainWin.DWUIVersionRevision);
			
			if (ver == null)
			{
				res += "No definition was found for the running version, cannot verify.";
			}
			else
			{
				
				for (String fn : ver.getFiles().keySet())
				{
					
					try 
					{
						File f = new File(fn);
						
						String cursha = UIUtils.getSHA1(new FileInputStream(f));
							
						if (!cursha.equalsIgnoreCase( ver.getFiles().get(fn)))
						{
							res += "File '" + fn + "' checksum does not match the version definition.";
						}
							
					} 
					
					catch (FileNotFoundException e) 
					{
						
						res += "File '" + fn + "' is defined for this version, but does not exist locally.";
					} 
				}
					
				
				
			}
			
		} 
		catch (Exception e) 
		{
			res += "Error while verifying version: " + e.getClass().getName() + ": " + e.getMessage();
			e.printStackTrace();
		}
		
		return res;
	}
	
	
	
	protected static void doUpdateCheck()
	{
		doUpdateCheck(false);
	}

	protected static void doUpdateCheck(boolean force) 
	{
		
		try 
		{
			Updater up;
			
			up = new Updater();
			
			if ((force) || ((up.getLatestVersion().newerThan(MainWin.DWUIVersion)) && (!isIgnoredUpdate(up.getLatestVersion()))))
			{
				// update available..
				
				if (config.getBoolean("UpdateRestartRequired", false))
				{
					showError("A restart is pending", "We cannot do further updates until DriveWire is restarted", "An update is available for the running version, but a newer version is already installed.  Please check for updates again after restarting DriveWire.");
				}
				else
				{
					UpdateWizard uz = new UpdateWizard(up);
					
					WizardDialog dialog = new WizardDialog(shell, uz);
				    dialog.create();
				    dialog.open();
				}
			}
			else
			{
				NoUpdateDialog nd = new NoUpdateDialog(shell);
				nd.open();
				
			}
		} 
		catch (Exception e) 
		{
			MainWin.showError("Error while checking for updates", e.getMessage(), e.getMessage(), true);
			
		}
		
		
	}


	private static boolean isIgnoredUpdate(Version ver) 
	{
		
		for (int x = 0; x <= MainWin.config.getMaxIndex("IgnoreUpdateVersion");x++)
		{
			if (MainWin.config.getString("IgnoreUpdateVersion(" + x + ")","").equals(ver.toString()) )
				return true;
		}
		
		return false;
	}


	public static void setSashformWeights(int[] w)
	{
		if ((!MainWin.shell.isDisposed()) && (sashForm != null) && (!sashForm.isDisposed()))
		{
			try
			{
				sashForm.setWeights(w);
			}
			catch (IllegalArgumentException e)
			{
				// dont care
			}
		}
	}

	
	public static int[] getSashformWeights()
	{
		return sashForm.getWeights();
	}
	

	private static void createDiskTableColumns()
	{
		synchronized(table)
		{
			for (String key: MainWin.getDiskTableParams())
			{
				TableColumn col = new TableColumn(table, SWT.NONE);
				col.setMoveable(true);
				col.setResizable(true);
				col.setData("param", key);
				
				col.setWidth(config.getInt(key +"_ColWidth",50));
				
				if (key.startsWith("_"))
				{
					if (key.length()>4)
						col.setText(key.substring(1, 2).toUpperCase() + key.substring(2));
					else if (key.length() > 1)
						col.setText(key.substring(1).toUpperCase());
				}
				else if (!key.equals("LED") && key.length()>1)
					col.setText(key.substring(0, 1).toUpperCase() + key.substring(1));
			}
		}
	}


	private static List<String> getDiskTableParams()
	{
		return(Arrays.asList(MainWin.config.getStringArray("DiskTable_Items")));
	}


	public static int getTPIndex(String key)
	{
		synchronized(table)
		{
			for (int i = 0;i<table.getColumnCount();i++)
			{
				if (table.getColumn(i).getData("param").equals(key))
					return i;
			}
		}
		
		
		return(-1);
	}

	protected static Point getDiskWinInitPos(int drive)
	{
		Point res = new Point(MainWin.config.getInt("DiskWin_"+ drive +"_x",shell.getLocation().x + 20), MainWin.config.getInt("DiskWin_"+ drive +"_y",shell.getLocation().y + 20));
		
		if (!isValidDisplayPos(res))
				res = new Point(shell.getLocation().x + 20, shell.getLocation().y + 20);
				
		
		return(res);
	}


	static boolean isValidDisplayPos(Point p)
	{
		// check for invalid saved position
		Monitor[] list = display.getMonitors();
	
		for (int i = 0; i < list.length; i++) 
		{
			if (list[i].getBounds().contains(p))
				return true;
		}
		return false;
	}





	public static void doShutdown() 
	{
	  
		// remove gui error trap
				
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					
			@Override
			public void uncaughtException(Thread t, final Throwable e)
			{
			
				System.out.println("Error while exiting:");
				System.out.println();
				System.out.println(UIUtils.getStackTrace(e));
				
				if (config.getBoolean("TermServerOnExit",false) || config.getBoolean("LocalServer",false) )
				{
					System.out.println("Trying to terminate server..");
					stopDWServer();
				}
				
				System.out.println("Checking for updated files..");
				UIUtils.replaceFiles(".", "");
				
				System.exit(1);
		
			}
				
		});
		 
		
		// display progress..
		ShutdownWin sdwin = new ShutdownWin(shell, SWT.DIALOG_TRIM);
		
		sdwin.open();
		  
	  
		// yeah right
		sdwin.setStatus("Encouraging consistency...",10);
		  
		// kill sync
		MainWin.host = null;
		MainWin.ready = false;
		MainWin.syncObj.die();
		
		  
		  
		if (config.getBoolean("TermServerOnExit",false) || config.getBoolean("LocalServer",false) )
		{
			sdwin.setStatus("Stopping DriveWire server...",25);
			// sendCommand("ui server terminate");
			stopDWServer();
		}

		  
		  
		  // save window pos
		
		  if (shell != null)
		  {
			  sdwin.setStatus("Saving main window layout...",40);
		
		
							  
			  config.setProperty("MainWin_Width", shell.getSize().x);
			  config.setProperty("MainWin_Height", shell.getSize().y);
				
			  config.setProperty("MainWin_x", shell.getLocation().x);
			  config.setProperty("MainWin_y", shell.getLocation().y);
				
			  config.setProperty("MainWin_Tab", MainWin.tabFolderOutput.getSelectionIndex());
				
			  //sanity check, wizard might have screwed these
			  if (!(sashForm.getWeights()[0] == 0) && !(sashForm.getWeights()[1] == 0))
				  config.setProperty("SashForm_Weights", sashForm.getWeights());
				
			  sdwin.setStatus("Saving main window layout...",50);
	
			  for (int i = 0;i<table.getColumnCount();i++)
			  {
				  config.setProperty("DiskTable_Items("+ i +")", table.getColumn(table.getColumnOrder()[i]).getData("param") );
				  config.setProperty(table.getColumn(i).getData("param") +"_ColWidth", table.getColumn(i).getWidth());
			  }
				
			  
			  sdwin.setStatus("Saving disk window layouts...",65);
			
			  
		
			  for (int i = 0;i<256;i++)
			  {
				  sdwin.setProgress(65 + (i / 8));
				
				  if (disks != null)
					  if ((disks[i] != null) && (disks[i].hasDiskwin()))
					  {
						  disks[i].getDiskwin().close();
						  config.setProperty("DiskWin_"+ i+"_open",true);
					  }
			  }
			  
			  sdwin.setStatus("Saving library window layouts...",95);
			  
			  int tabs = 0;
			   
			  for (Control c : MainWin.tabFolderOutput.getTabList() )
			  {
				  if  ((c.getClass().getCanonicalName().equals("com.groupunix.drivewireui.DWLibrary")))
				  {
					  if (!((DWLibrary) c).getOurTab().isDisposed())
					  {
						  config.setProperty("LibraryTab_" + tabs, ((DWLibrary) c).getCurrentItemPath());
						  tabs++;
					  }
				  }
				  
			  }
			  
			  config.setProperty("LibraryTabs", tabs);

		  }
		  
		  sdwin.setStatus("Exiting...",100);
		  
		// finish drawing..?
		  int waits = 0;
	
		  while (display.readAndDispatch() && waits < 10) {
					
			try
			{
				waits++;
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				//dont care, just catching a few redraws
			}
				
			}
		  
		  // do updated file replacement on the way out..
		  try
		  {
			  UIUtils.replaceFiles(".", "");
		  }
		  catch (Exception e)
		  {
			  System.err.println(e.getMessage());
			  System.exit(1);
		  }
		  
		  System.exit(0);
	}


	

	 



	void createOutputTabs()
	{
		tabFolderOutput = new CTabFolder(sashForm, SWT.BORDER);
		tabFolderOutput.setSimple(false);
		
		tabFolderOutput.marginHeight = 0;
		tabFolderOutput.marginWidth = 0;
		tabFolderOutput.setTabHeight(24);
		//tabFolderOutput.setTabPosition(SWT.BOTTOM);
		//tabFolderOutput.setBorderVisible(false);
		
		/*
		int s1 = 245;
		int s2 = 215;
		
		tabFolderOutput.setSelectionBackground(new Color[] { 
				new Color(display,s2,s2,s2) , 
				new Color(display,s1,s1,s1) , 
				new Color(display,s2,s2,s2) } ,
				//new Color(display,220,15,20) }, 
				new int[] {50, 100}, true); 
		*/
		
		tabFolderOutput.setSelectionBackground(new Color[]{display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND)} , new int[]{75}, true);
				
		tabFolderOutput.setSelectionForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		
		tabFolderOutput.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (e.item.getData("ttype") != null)
				{
					if (e.item.getData("ttype").equals("server"))
					{
						logNoticeLevel = -1;
						//tabFolderOutput.getItems()[tabFolderOutput.getSelectionIndex()].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png"));
						((CTabItem) e.item).setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png"));
						
					}
					else if (e.item.getData("ttype").equals("ui"))
					{
						((CTabItem) e.item).setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png"));
						
					}
					else if (e.item.getData("ttype").equals("library"))
					{
						if (e.item.getData("addlib") != null)
						{
							MainWin.addBrowserTab();
						}
					}
				}
				
				
			}
		});
		
		
		tbtmUi = new CTabItem(tabFolderOutput, SWT.NONE);
		
		tbtmUi.setText("UI  ");
		tbtmUi.setToolTipText("Output from UI events");
		
		tbtmUi.setData("ttype", "ui");
		
		scrolledComposite = new ScrolledComposite(tabFolderOutput, SWT.V_SCROLL |  SWT.DOUBLE_BUFFERED);
		tbtmUi.setControl(scrolledComposite);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setBackground(MainWin.colorWhite);
		
		Composite scrollcontents = new Composite(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrollcontents.setBackground(MainWin.colorWhite);
		
		scrolledComposite.setContent(scrollcontents);
		scrolledComposite.setExpandHorizontal(true);
	   // scrolledComposite.setExpandVertical(true);
		
		MainWin.taskman = new UITaskMaster(scrollcontents);
		
		
		CTabItem tbtmServer = new CTabItem(tabFolderOutput, SWT.NONE);
		tbtmServer.setText("Server  ");
		
		tbtmServer.setData("ttype", "server");
		
		tbtmServer.setToolTipText("Server status and log");
		Composite composite_1 = new Composite(tabFolderOutput, SWT.NONE);
		
		//composite_1.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite_1.setBackground(org.eclipse.wb.swt.SWTResourceManager.getColor(SWT.COLOR_RED));
		
		tbtmServer.setControl(composite_1);
		composite_1.setLayout(new BorderLayout(0, 0));
		
		compServer = new Composite(composite_1, SWT.NONE);
		compServer.setLayoutData(BorderLayout.NORTH);
		compServer.setBackgroundImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/logging/serverbgbar.png"));
		
		
		canvasDiskOps = new Canvas(compServer,SWT.DOUBLE_BUFFERED);
		canvasDiskOps.setBounds(20,0, MainWin.graphDiskOps.getImageData().width, MainWin.graphDiskOps.getImageData().height);
		
		canvasDiskOps.addPaintListener(new PaintListener()
		{
		   public void paintControl(final PaintEvent event)
		   {
			   event.gc.drawImage(MainWin.graphDiskOps,0,0);
				
		   }
		 });
		
		
		canvasVSerialOps = new Canvas(compServer,SWT.DOUBLE_BUFFERED);
		canvasVSerialOps.setBounds(290,0, MainWin.graphVSerialOps.getImageData().width, MainWin.graphVSerialOps.getImageData().height);
		
		canvasVSerialOps.addPaintListener(new PaintListener()
		{
		   public void paintControl(final PaintEvent event)
		   {
			   event.gc.drawImage(MainWin.graphVSerialOps,0,0);
				
		   }
		 });
		
		
		
		canvasMemUse = new Canvas(compServer,SWT.DOUBLE_BUFFERED);
		canvasMemUse.setBounds(560,0, MainWin.graphMemUse.getImageData().width, MainWin.graphMemUse.getImageData().height);
		
		canvasMemUse.addPaintListener(new PaintListener()
		{
		   public void paintControl(final PaintEvent event)
		   {
			   event.gc.drawImage(MainWin.graphMemUse,0,0);
				
		   }
		 });
		
		
		
		logTable = new Table(composite_1, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		
		
		
		
		
		logTable.addListener(SWT.SetData, new Listener() 
		{
			    public void handleEvent(Event event) {
			       TableItem item = (TableItem)event.item;
				       int i = event.index;
				       
				       synchronized(logItems)
				       {
					       if (i < MainWin.logItems.size())
					       {
					    	   item.setImage(0, org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/logging/" + MainWin.logItems.get(i).getLevel().toLowerCase() + ".png"));
						       item.setText(1, MainWin.logItems.get(i).getShortTimestamp());
						       item.setText(2, MainWin.logItems.get(i).getLevel());
						       item.setText(3, MainWin.logItems.get(i).getShortSource());
						       item.setText(4, MainWin.logItems.get(i).getThread());
						       item.setText(5, MainWin.logItems.get(i).getMessage());
					       }
					       else
					       {
					    	   item.setText(1,"hmmm");
					       }
				       }
				    }
				 });
		
		logTable.setHeaderVisible(true);
		
		TableColumn logImg = new TableColumn(logTable,SWT.RESIZE);
		logImg.setText("");
		logImg.setWidth(24);
		
		TableColumn logTime = new TableColumn(logTable,SWT.RESIZE);
		logTime.setText("Time");
		logTime.setWidth(80);
			
		TableColumn logLevel = new TableColumn(logTable,SWT.RESIZE);
		logLevel.setText("Level");
		logLevel.setWidth(60);
		
		TableColumn logSource = new TableColumn(logTable,SWT.RESIZE);
		logSource.setText("Source");
		logSource.setWidth(100);
		
		TableColumn logThread = new TableColumn(logTable,SWT.RESIZE);
		logThread.setText("Thread");
		logThread.setWidth(100);
		
		
		TableColumn logMessage = new TableColumn(logTable,SWT.RESIZE);
		logMessage.setText("Message");
		logMessage.setWidth(400);
		
		Menu menu_3 = new Menu(logTable);
		menu_3.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				if (logTable.getSelectionCount() > 0)
				{
					mntmCopy.setEnabled(true);
					if (logTable.getSelectionCount() > 1)
						mntmCopy.setText("Copy " + logTable.getSelectionCount() + " selected items");
					else
						mntmCopy.setText("Copy selected item");
				}
				else
				{
					mntmCopy.setEnabled(false);
					mntmCopy.setText("Copy");
				}
				
				if (logItems.size() > 0)
					mntmSaveLogTo.setEnabled(true);
				else
					mntmSaveLogTo.setEnabled(false);
			}
		});
		logTable.setMenu(menu_3);
		
		MenuItem mntmAutoscroll = new MenuItem(menu_3, SWT.CHECK);
		mntmAutoscroll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MainWin.logscroll)
					MainWin.logscroll = false;
				else
					MainWin.logscroll = true;
				
				((MenuItem) e.widget).setSelection(MainWin.logscroll);
			}
		});
		mntmAutoscroll.setText("Autoscroll");
		mntmAutoscroll.setSelection(logscroll);
		
		new MenuItem(menu_3, SWT.SEPARATOR);
		
		mntmCopy = new MenuItem(menu_3, SWT.NONE);
		mntmCopy.setText("Copy");
		
		mntmCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (logTable.getSelectionCount() > 0)
				{
					String logtxt = "";
					
					for (int i : logTable.getSelectionIndices())
					{
						logtxt += logItems.get(i).toString() + System.getProperty("line.separator");
					}
					
					Clipboard clipboard = new Clipboard(MainWin.getDisplay());
					
					TextTransfer textTransfer = TextTransfer.getInstance();
				
					Transfer[] transfers = new Transfer[]{textTransfer};
					Object[] data = new Object[]{ logtxt };
					clipboard.setContents(data, transfers);
					clipboard.dispose();
				}
			}
		});
		
		mntmSaveLogTo = new MenuItem(menu_3, SWT.NONE);
		mntmSaveLogTo.setText("Save log to file...");
		
		mntmSaveLogTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				UIUtils.saveLogItemsToFile();
			}
		});
		
		new MenuItem(menu_3, SWT.SEPARATOR);
		
		MenuItem mntmClearLog = new MenuItem(menu_3, SWT.NONE);
		mntmClearLog.setText("Clear log");
		
		mntmClearLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				synchronized(MainWin.logItems)
				{
					logItems.removeAllElements();
					logTable.removeAll();
				}
			}
		});
		
		
		for (int i = 0; i < config.getInt("LibraryTabs", 1);i++)
		{
		
			CTabItem tbtmLibrary = new CTabItem(tabFolderOutput, SWT.NONE);
			tbtmLibrary.setShowClose(true);
			tbtmLibrary.setText("Library ");
			tbtmLibrary.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
			tbtmLibrary.setData("ttype", "library");
			DWLibrary lt = new DWLibrary(tabFolderOutput, SWT.FULL_SELECTION, tbtmLibrary);
			tbtmLibrary.setControl(lt);
			
			if (config.getString("LibraryTab_" + i, "") != "")
			{
				lt.setCurrentItemPath(config.getString("LibraryTab_" + i));
			}
						
		}
		
		addNewBrowserTab(null);
		
		//tabFolderOutput.setSelection(0);
		//tabFolderOutput.update();
		
		tabFolderOutput.setSelection(config.getInt("MainWin_Tab", 0));
	}
	
	
	
	static void addBrowserTab()
	{
		// do we have a new tab waiting..
		if (MainWin.tabFolderOutput.getItem(MainWin.tabFolderOutput.getItemCount()-1).getData("addlib") != null)
		{
			addNewBrowserTab(MainWin.tabFolderOutput.getItem(MainWin.tabFolderOutput.getItemCount()-1));
		}
		
	}
	
	static void addNewBrowserTab(CTabItem tab)
	{
		if (tab != null)
		{
			// make into browser
			tab.setControl(new DWLibrary(tabFolderOutput, SWT.FULL_SELECTION, tab));
			tab.setData("addlib", null);
			tab.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/disk-insert.png"));
			tab.setShowClose(true);
			tab.setText("Library ");
		
		}
		
		// add new tab
		CTabItem tbtmAdd = new CTabItem(tabFolderOutput,SWT.NONE);
		tbtmAdd.setText("");
		tbtmAdd.setToolTipText("Add library viewer...");
		tbtmAdd.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/menu/add.png"));
		
		tbtmAdd.setData("ttype", "library");
		tbtmAdd.setData("addlib", "yes");
		
	}


	static void debug(String string)
	{
		if (MainWin.debugging == true)
			System.out.println("debug: thread " + Thread.currentThread().getName() + ": " + string);
	}


	

	public static void openURL(@SuppressWarnings("rawtypes") Class cl, String url) 
	{
		// this odd bit of code tries to use the org.eclipse.swt.program.Program.launch method to open a native browser with a url in it.
		// usually that is straighforward, but on some systems it can crash the whole works, so we use invoke to call it carefully and catch crashes.
		boolean failed = false;
		
		Class<?> c;
		try {
			
			c = Class.forName("org.eclipse.swt.program.Program", true, cl.getClassLoader() );
			java.lang.reflect.Method launch = c.getMethod("launch",new Class[]{ String.class });
			launch.invoke((Object)null, url);
			
			
		} 
		catch (ClassNotFoundException e) 
		{
			failed = true;
		} 
		catch (SecurityException e) 
		{
			failed = true;
		} 
		catch (NoSuchMethodException e) 
		{
			failed = true;
		} 
		catch (IllegalArgumentException e) 
		{
			failed = true;
		} 
		catch (IllegalAccessException e) 
		{
			failed = true;
		} 
		catch (InvocationTargetException ex)
	    {
        	failed = true;
		}	
		
		if (failed)
		{
			MainWin.showError("No browser available", "Could not open a browser automatically on this system.", "The URL I wanted to show you was:  " + url);
        	
		}
	}


	
	
	private static void updateMidiMenus()
	{

		// much more complicated than necessary so that changes are reflected in real time
		
		
		if (MainWin.midiStatus.isEnabled())
		{
			mntmMidi.setEnabled(true);
			
			// is menu built ok
			MenuItem[] profitems = MainWin.menuMIDIProfiles.getItems();
			String[] profiles = MainWin.midiStatus.getProfiles().toArray(new String[0]);
			
			java.util.Arrays.sort(profiles);
			
			boolean profok = false;
			
			if (profitems.length == profiles.length)
			{
				profok = true;
				
				for (int i = 0;i<profitems.length;i++)
				{
					if (profitems[i].getText().equals(MainWin.midiStatus.getProfile(profiles[i]).getDesc()))
					{
						profitems[i].setSelection(profiles[i].equals(MainWin.midiStatus.getCurrentProfile()));
					}
					else
					{
						profok = false;
					}
				}
			}
			
			if (!profok)
			{
				// rebuild the whole thing
				MainWin.menuMIDIProfiles.dispose();
				MainWin.menuMIDIProfiles = new Menu(MainWin.mntmSetProfile);
				MainWin.mntmSetProfile.setMenu(MainWin.menuMIDIProfiles);
			
			
				
				for (String pn : profiles)
				{
					final String key = pn;
					MenuItem tmp = new MenuItem(MainWin.menuMIDIProfiles, SWT.CHECK);
					tmp.setText(MainWin.midiStatus.getProfile(key).getDesc());
				
					tmp.setSelection(key.equals(MainWin.midiStatus.getCurrentProfile()));
					tmp.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
						sendCommand(("dw midi synth profile " + key));
						}
					});
				}	
			}
			
			
			// same for outputs.. is menu built ok
			MenuItem[] outitems = MainWin.menuMIDIOutputs.getItems();
			String[] outs = MainWin.midiStatus.getDevices().toArray(new String[0]);
			
			boolean outok = false;
			
			if (outitems.length == outs.length)
			{
				outok = true;
				
				for (int i = 0;i<outitems.length;i++)
				{
					if (outitems[i].getText().equals(outs[i]))
					{
						outitems[i].setSelection(outs[i].equals(MainWin.midiStatus.getCurrentDevice()));
					}
					else
					{
						outok = false;
					}
				}
			}
			
			if (!outok)
			{
			
				MainWin.menuMIDIOutputs.dispose();
				MainWin.menuMIDIOutputs = new Menu(MainWin.mntmSetOutput);
				MainWin.mntmSetOutput.setMenu(MainWin.menuMIDIOutputs);
			
				Iterator<String> itr = MainWin.midiStatus.getDevices().iterator();
			
				while (itr.hasNext())
				{
					final String key = itr.next();
					MenuItem tmp = new MenuItem(MainWin.menuMIDIOutputs, SWT.CHECK);
					tmp.setText(key);
				
						
					tmp.addSelectionListener(new SelectionAdapter() 
					{
						@Override
						public void widgetSelected(SelectionEvent e) 
						{
							sendCommand(("dw midi output " + MainWin.midiStatus.getDevice(key).getDevnum()));
						}
					});
					
				
					tmp.setSelection(key.equals(MainWin.midiStatus.getCurrentDevice()));
				}
				
			}	
			
			// voice lock
			mntmLockInstruments.setSelection(MainWin.midiStatus.isVoiceLock());
		}
		else
		{
			mntmMidi.setEnabled(false);
		}
	}


	@SuppressWarnings("unchecked")
	protected static void addCommandToHistory(String cmd) 
	{

		List<String> cmdhist = config.getList("CmdHistory",null);
		
		if (config.getInt("CmdHistorySize",default_CmdHistorySize) > 0)
		{
			if (cmdhist == null)
			{
			
				config.addProperty("CmdHistory", cmd);
				cmdhist = config.getList("CmdHistory",null);
			}
		
			
			if (cmdhist.size() >= config.getInt("CmdHistorySize",default_CmdHistorySize))
			{	
				cmdhist.remove(0);
			}
				
			cmdhist.add(cmd);
			config.setProperty("CmdHistory", cmdhist);
		}	
	}

	



	
	
	


	
	public static void refreshDiskTable() 
	{
		
		MainWin.table.setRedraw(false);
		
		
		for (int i = 0;i<256;i++)
		{
			MainWin.clearDiskTableEntry(i);
			
			if ((disks[i] != null) && disks[i].isLoaded())
			{
				MainWin.setDiskTableEntryFile(i, disks[i].getPath());
				
				Iterator<String> itr = disks[i].getParams();
				
				while (itr.hasNext())
				{
					String key = itr.next();
					setDiskTableEntry(i,key,disks[i].getParam(key).toString());
				}
				
			
			}
			
		}
		
		if (MainWin.currentDisk >= 0)
		{
			table.setSelection(MainWin.currentDisk);
		}
	
		MainWin.table.setRedraw(true);
		
		
		if (MainWin.serverLocal )
		{
			for (Control cti : MainWin.tabFolderOutput.getTabList())
			{
				if  ((cti.getClass().getCanonicalName().equals("com.groupunix.drivewireui.DWLibrary")))
				  {
					  if (!((DWLibrary) cti).getOurTab().isDisposed())
					  {
				
						  ((DWLibrary)cti).updateTree();
					  }
				}
			}
		}
	}
		


	
	public static void setDiskTableEntry(int disk, String key, String val)
	{
		int col = MainWin.getTPIndex(key);
		
		if (col > -1)
			table.getItem(disk).setText(col,val);
	}

	
	private static void quickURLInDisk(final int diskno)
	{
		MainWin.quickURLInDisk(shell, diskno);
	}
	
	public static void quickURLInDisk(final Shell theshell, final int diskno)
	{ 
		String res = null;
		
		URLInputWin urlwin = new URLInputWin(theshell, diskno);
		res = urlwin.open();
		
		
		if ((res != null) && (!res.equals("")))
		{
			final List<String> cmds = new ArrayList<String>();
			cmds.add("dw disk insert "+ diskno + " " + res);
			display.asyncExec(
					  new Runnable() {
						  public void run()
						  {
							  SendCommandWin win = new SendCommandWin(theshell, SWT.DIALOG_TRIM, cmds, "Inserting disk image...", "Please wait while the image is inserted into drive " + diskno + ".");
							  win.open();
						  }
					  });
		}
	}
	

	private static void quickInDisk(final int diskno)
	{
		MainWin.quickInDisk(shell, diskno);
	}
	
	public static void quickInDisk(final Shell theshell, final int diskno)
	{    
		final String curpath;
		
		if ((diskno > -1) && (diskno < disks.length) && (disks[diskno] != null) && (disks[diskno].isLoaded()))
			curpath = disks[diskno].getPath();
		else
			curpath = "";
		
	
		Thread t = new Thread(new Runnable() {
			  public void run()
			  {
				  
				  final String res = getFile(false,false,curpath,"Choose an image for drive " + diskno, "Open", new String[] { MainWin.config.getString("FileSelectionMask","*.dsk;*.os9;*.vhd") , "*.*" });
				  
					if (res != null)
					{
						
						display.syncExec(
								  new Runnable() {
									  public void run()
									  {
								
										  List<String> cmds = new ArrayList<String>();
										  cmds.add("dw disk insert "+ diskno + " " + res);
										  
										  SendCommandWin win = new SendCommandWin(theshell, SWT.DIALOG_TRIM, cmds, "Inserting disk image...", "Please wait while the image is inserted into drive " + diskno + ".");
										  
										  win.open();
								  
									  }
								  });
								
					}
					
				
			  }
			});
		
	
		t.start();
		
	}
	
	public static void quickInDisk(final Shell theshell, final String path)
	{
		
		
		
		Thread t = new Thread(new Runnable() {
			  public void run()
			  {
				  	ChooseDriveWin cdw = new ChooseDriveWin(theshell, SWT.DIALOG_TRIM, MainWin.table.getSelectionIndex());
				  	final int diskno =  cdw.open();
				  	
					if (diskno > -1)
					{
						
						display.syncExec(
								  new Runnable() {
									  public void run()
									  {
								
										  List<String> cmds = new ArrayList<String>();
										  cmds.add("dw disk insert "+ diskno + " " + path);
										  
										  SendCommandWin win = new SendCommandWin(theshell, SWT.DIALOG_TRIM, cmds, "Inserting disk image...", "Please wait while the image is inserted into drive " + diskno + ".");
										  
										  win.open();
								  
									  }
								  });
								
					}
					
				
			  }
			});
		
	
		display.asyncExec(t);
		
	}
	
	
	protected static void writeDiskTo(final int diskno) 
	{
		final String curpath;
		
		if (table.getItem(diskno) != null)
			curpath = table.getItem(diskno).getText(2);
		else
			curpath = "";
		
		Thread t = new Thread(new Runnable() {
					  public void run()
					  {
						  String res = getFile(true,false,curpath,"Write image in drive " + diskno + " to...", "Save",  new String[] { MainWin.config.getString("FileSelectionMask","*.dsk;*.os9;*.vhd") , "*.*" });
							
							if (res != null)
								MainWin.sendCommand("dw disk write "+ diskno + " " + res);
					  }
					});
		
		t.start();
				
				
		
	}
	
	public static String getFile(final boolean save, final boolean dir, final String startpath, final String title, final String buttontext)
	{
		return(getFile(save, dir,  startpath,  title,  buttontext, null));

	}
	
	public static String getFile(final boolean save, final boolean dir, final String startpath, final String title, final String buttontext, final String[] fileext)
	{
			
		if (config.getBoolean("UseRemoteFilebrowser", false))
		{
			// remote
			
			RemoteFileBrowser rfb = new RemoteFileBrowser(save,dir,startpath,title,buttontext,fileext);
			
			
			try
			{
				SwingUtilities.invokeAndWait(rfb);
			} 
			catch (InterruptedException e)
			{
				MainWin.showError("Error in file browser", e.getMessage(), UIUtils.getStackTrace(e), true);
			} 
			catch (InvocationTargetException e)
			{
				MainWin.showError("Error in file browser", e.getMessage(), UIUtils.getStackTrace(e), true);
			}
			
		
			return (rfb.getSelected());
			
			
		}
		else
		{
			LocalFileBrowser lfb = new LocalFileBrowser(save,dir,startpath,title,buttontext,fileext);
			
			display.syncExec(lfb);
			
			return (lfb.getSelected());
			
		}
		
		
	}
	
	

	public static void sendCommand(String cmd)
	{
		sendCommand(cmd, true);
	}
	
	protected static void sendCommand(String cmd, int tid)
	{
		sendCommand(cmd, tid, true);
	}
	
	protected static void sendCommand(String cmd, boolean markComplete)
	{
		int tid = MainWin.taskman.addTask(cmd);
		sendCommand(cmd, tid, markComplete);
	}
	
	protected static void sendCommand(final String cmd, final int tid, final boolean markComplete) 
	{
	
		Thread cmdT = new Thread(new Runnable() 
		{
			  public void run() 
			  {
				  if (cmd.startsWith("/"))
				  {
					  // client command
					  processClientCmd(cmd, tid, markComplete);
				  }
				  else
				  {
					  if (markComplete)
						  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_ACTIVE, "Connecting to server...");
					  
					  Connection connection = new Connection(host,port,instance);
						
					  try 
					  {
						  connection.Connect();
						  
						  if (markComplete)
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_ACTIVE, "Sending command: " + cmd);
						  
						  connection.sendCommand(tid,cmd,instance,markComplete);
						  connection.close();
						 
					  } 
					  catch (UnknownHostException e) 
					  {
						  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_FAILED, e.getMessage() +  " You may have a DNS problem, or the server hostname may not be specified correctly.");
					  } 
					  catch (IOException e1) 
					  {
						// UIUtils.getStackTrace(e1)
						  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_FAILED, e1.getMessage() +  " You may have a connectivity problem, or the server may not be running.");
							
					  } 
					  catch (DWUIOperationFailedException e2) 
					  {
						  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_FAILED, e2.getMessage());
					  } 
				  }
			  } 
		});
		
		cmdT.start();

	}

	
	
	
	
	
	public static void processClientCmd(String cmd)
	{
		int tid = MainWin.taskman.addTask(cmd);
	
		processClientCmd(cmd, tid, true);
	}
	
	public static void processClientCmd(String cmd, final int tid, boolean markComplete)
	{
		  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_ACTIVE, null);
		  
		  final int statint;
		  
		  if (markComplete)
			  statint = UITaskMaster.TASK_STATUS_COMPLETE;
		  else
			  statint = UITaskMaster.TASK_STATUS_ACTIVE;
		  
		  
		  if (cmd.equals("/fonts"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE, UIUtils.listFonts());
						  }
					  });
		  }
		  else if (cmd.equals("/splash"))
		  {
			  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE, MainWin.DWUIVersion + " (" + MainWin.DWUIVersionDate + ")" );
			   
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {
							  MainWin.doSplashTimers(tid, true);
						  }
					  });
		  }
		  else if (cmd.equals("/sha"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  String txt = UIUtils.getSHAForDir(".", "."); 
							  
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE , txt);
							 
						  }
					  });
			
		  }
		  else if (cmd.equals("/forceupdate"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  doUpdateCheck(true);
							  
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE , "Forced update complete");
						  }
					  });
			
		  }
		  else if (cmd.equals("/verify"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  
							  
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE , doVerifyVersion());
						  }
					  });
			
		  }
		  else if (cmd.equals("/mem"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  String txt = "Max  : " + (Runtime.getRuntime().maxMemory() / 1024) 
							  + System.getProperty("line.separator") + "Total: " + (Runtime.getRuntime().totalMemory() / 1024)  
							  + System.getProperty("line.separator") + "Free : " + (Runtime.getRuntime().freeMemory() / 1024);
							  
							  long realfree = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
							  txt += System.getProperty("line.separator") + "TFree: " + (realfree / 1024);
							  txt += "     " + ((Double.valueOf(realfree) / Double.valueOf(Runtime.getRuntime().maxMemory()) * 100) + "%");
							  
							  MainWin.taskman.updateTask(tid, statint , txt);
							 
						  }
					  });
		  }
		  else if (cmd.equals("/dumperr"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_COMPLETE, MainWin.errorHelpCache.dump());
						  }
					  });
		  }
		  else if (cmd.equals("/midistatus"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  MainWin.taskman.updateTask(tid, statint, UIUtils.dumpMIDIStatus(MainWin.midiStatus));
						  }
					  });
		  }
		  else if (cmd.equals("/bugout"))
		  {
			  display.asyncExec(
					  new Runnable() {
						  public void run()
						  {  
							  Integer.parseInt("not an int");
						  }
					  });
		  }
		  else
		  {
			  MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_FAILED, "Unknown client command");
		  }
		  
	}



	

	
	


	public static String getHost()
	{
		return host;
	}

	public static int getPort()
	{
		return port;
	}
	
	public static int getInstance()
	{
		return instance;
	}

	public static void setHost(String h) 
	{
		host = h;
		config.setProperty("LastHost",h);
		updateTitlebar();
	}

	public static void setPort(String p) 
	{
		try
		{
			port = Integer.parseInt(p);
			config.setProperty("LastPort", p);
			updateTitlebar();
		}
		catch (NumberFormatException e)
		{
			showError("Invalid port number","'" + p + "' is not a valid port number.","Valid port numbers are 1-65535.");
		}
		
		
	}

	@SuppressWarnings("unchecked")
	public static void addDiskFileToHistory(String filename) 
	{
		List<String> diskhist = config.getList("DiskHistory",null);
		
		
		if (config.getInt("DiskHistorySize",default_DiskHistorySize) > 0)
		{
			if (diskhist == null)
			{
			
				config.addProperty("DiskHistory", filename);
				diskhist = config.getList("DiskHistory",null);
			}
			else 
			{
				diskhist.remove(filename);
				
				if (diskhist.size() >= config.getInt("DiskHistorySize",default_DiskHistorySize))
				{
					diskhist.remove(0);
				}	
				
				diskhist.add(filename);
			}
			
			config.setProperty("DiskHistory", diskhist);
			
		}
	
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<String> getDiskHistory()
	{
		return(config.getList("DiskHistory",null));
	}

	


	@SuppressWarnings("unchecked")
	public static List<String> getServerHistory() 
	{
		return(config.getList("ServerHistory",null));
	}

	@SuppressWarnings("unchecked")
	public static void addServerToHistory(String server) 
	{
		List<String> shist = config.getList("ServerHistory",null);
		
		if (shist == null)
		{
			if (config.getInt("ServerHistorySize",default_ServerHistorySize) > 0)
			{
				config.addProperty("ServerHistory", server);
			}
		}
		else if (!shist.contains(server))
		{
			if (shist.size() >= config.getInt("ServerHistorySize",default_ServerHistorySize))
			{
				shist.remove(0);
			}
			
			shist.add(server);
			config.setProperty("ServerHistory", shist);
			
		}
		
	}

	public static void setInstance(int inst) 
	{
		if (inst != MainWin.instance)
		{
			MainWin.instance = inst;
		
			restartServerConn();
			
		}
		
		config.setProperty("LastInstance", inst);
		updateTitlebar();
		
	}

	

	
	

	public static HierarchicalConfiguration getInstanceConfig() 
	{
		if (MainWin.dwconfig != null)
		{
			@SuppressWarnings("unchecked")
			List<HierarchicalConfiguration> handlerconfs = (List<HierarchicalConfiguration>)MainWin.dwconfig.configurationsAt("instance");
			
			if (MainWin.getInstance() < handlerconfs.size())
			{
				return handlerconfs.get(MainWin.getInstance());
			}
		}
		return(null);
	}

	

	public static void setConStatusConnect() 
	{
		
		if (MainWin.isReady())
		{
			
			synchronized(connected)
			{
				if (!connected)
				{
					display.syncExec(
							new Runnable() {
								public void run()
								{
							MainWin.connected = true;
							/*
							 MainWin.lblConStatus.setRedraw(false);
							 MainWin.lblConStatus.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/constatus/network-transmit-2.png"));
							 MainWin.lblConStatus.setRedraw(true);
							*/
								
							MainWin.setItemsConnectionEnabled(true);
							
						
					  }
				  });
				}
			}
		}
		
		
	}
	
	protected static void setItemsConnectionEnabled(boolean en) 
	{
		// enable/disable all controls requiring a server connection
		
	
	
		MenuItem[] items = MainWin.menu_tools.getItems();
		
		for (int i = 0;i<items.length;i++)
		{
			items[i].setEnabled(en);
		}
		
		items = MainWin.menu_config.getItems();
		
		for (int i = 0;i<items.length;i++)
		{
			items[i].setEnabled(en);
		}
		
	
		mntmInstances.setEnabled(en);
		
		MainWin.table.setEnabled(en);
		MainWin.txtYouCanEnter.setEnabled(en);
		
		// stuff that stays enabled
		MainWin.mntmInitialConfig.setEnabled(true);
		MainWin.mntmUserInterface.setEnabled(true);
		
		
	}




	public static void setConStatusError() 
	{
		/*
		if (MainWin.lblConStatus != null)
		{
			synchronized(connected)
			{
				if (connected)
				{
					display.syncExec(
							new Runnable() {
								public void run()
								{
							  MainWin.lblConStatus.setRedraw(false);
							  MainWin.lblConStatus.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/constatus/network-error-2.png"));
							  MainWin.lblConStatus.setRedraw(true);
						  
							  MainWin.connected = false;

							  MainWin.setItemsConnectionEnabled(false);
								
						  
					  }
				  
				  });
				}
			}
		}
	
		*/
	}


	
	public static void setConStatusTrying() 
	{
		/*
		if (MainWin.lblConStatus != null)
		{
			display.asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  MainWin.lblConStatus.setRedraw(false);
						  MainWin.lblConStatus.setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/constatus/user-away.png"));
						  MainWin.lblConStatus.setRedraw(true);
					  }
				  });
		}
		*/
	}
	
	
	
	
	


	public static void doDisplayAsync(Runnable runnable) 
	{
		display.asyncExec(runnable);
	}
	
	protected CTabFolder getTabFolderOutput() {
		return tabFolderOutput;
	}


	public static void setServerConfig(HierarchicalConfiguration serverConfig) 
	{
		MainWin.dwconfig = serverConfig;
		
		/*
		if (MainWin.getInstanceConfig().getString("DeviceType","").equals("dummy"))
		{
			SimpleWizard ww = new SimpleWizard();
			WizardDialog dialog = new WizardDialog(shell, ww);
		    dialog.create();
		    dialog.open();
		}
		*/
	}


	public static void restartServerConn() 
	{
		//logger.warn("Restarting server connection..");
		if (syncObj != null)
		{
			MainWin.syncObj.die();
			syncThread.interrupt();
			
			try 
			{
				syncThread.join();
			}
			catch (InterruptedException e) {
			}
		}
		
		if (MainWin.table != null)
			MainWin.table.removeAll();
		
		// start threads that talk with server
		syncObj = new SyncThread();
		syncThread = new Thread(syncObj);
		syncThread.setDaemon(true);
		syncThread.start();
		
		MainWin.serverLocal = UIUtils.isServerLocal();
		
	}


	
	public static void applyDisks() 
	{
		display.asyncExec(
				  new Runnable() {
					  public void run()
					  {
							refreshDiskTable();
							
					  }
				  });
		
	}


	
	public static DiskDef getCurrentDisk() 
	{
		return disks[MainWin.currentDisk];
	}

	public static int getCurrentDiskNo()
	{
		return(MainWin.currentDisk);
	}

	public static DiskDef getDiskDef(int dno) 
	{
		return(disks[dno]);
	}






	public static void setMidiStatus(MIDIStatus serverMidiStatus) 
	{

		MainWin.midiStatus = serverMidiStatus;
	}


	public static void applyMIDIStatus() 
	{
		display.asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  // midi menus
						  updateMidiMenus();
						  
					  }
				  });
	}


	public static MIDIStatus getMidiStatus() 
	{
		return(MainWin.midiStatus);
	}


	public static void setDisks(DiskDef[] serverDisks) 
	{
		MainWin.disks = new DiskDef[serverDisks.length];
		System.arraycopy(serverDisks, 0, MainWin.disks, 0, serverDisks.length);
	}


	public static String getUIText() 
	{
		String res = "";
		for (UITask t : taskman.getTasks())
		{
			res += "tid:"+ t.getTaskID() +", stat:"+t.getStatus() + ", txt:" + t.getText() + ", " + t.getCommand();
			res += "\r\n";
			
		}
		
		return(res);
		
	}







	public static void submitDiskEvent(final int disk, final String key, final String val)
	{

		
		// local display items
		
		if (disks[disk] == null)
		{
			debug("NULL disk in submitevent: " + disk);
			disks[disk] = new DiskDef(disk);
		}

		disks[disk].setParam(key, val);
		
		if (key.startsWith("*"))
		{
			if (key.equals("*insert"))
			{
				clearDiskTableEntry(disk);
				
				setDiskTableEntryFile(disk, val);
				
			}
			else if (key.equals("*eject"))
			{
				clearDiskTableEntry(disk);
			}
		}
		
		// update disk table 
		MainWin.diskTableUpdater.addUpdate(disk,key, val);
		
		if (key.equals("_reads") && !val.equals("0"))
		{
			MainWin.diskTableUpdater.addUpdate(disk,"LED",MainWin.diskLEDgreen);
			MainWin.driveactivity = true;
		}
		else if (key.equals("_writes") && !val.equals("0"))
		{
			MainWin.diskTableUpdater.addUpdate(disk,"LED",MainWin.diskLEDred);
			MainWin.driveactivity = true;
		}
		
		
	}

	
	private static void setDiskTableEntryFile(final int disk, final String val)
	{
		// sync?
		display.syncExec(new Runnable() {
			  public void run()
			  {
		
		// set file
				  int filecol = MainWin.getTPIndex("File");
				  if (filecol > -1)
					  table.getItem(disk).setText(filecol, UIUtils.getFilenameFromURI(val));
		 
		// set location
				  int loccol = MainWin.getTPIndex("Location");
				  if (loccol > -1)
					  table.getItem(disk).setText(loccol, UIUtils.getLocationFromURI(val));
				  
				  int ledcol = MainWin.getTPIndex("LED");
				  if (ledcol > -1)
					  table.getItem(disk).setImage(ledcol, MainWin.diskLEDdark);
		
			  }
		  });

	}


	private static void clearDiskTableEntry(final int disk)
	{
		
		
		// sync?
		display.syncExec(new Runnable() {
			  public void run()
			  {
				  synchronized(table)
				  {
					  int drivecol = MainWin.getTPIndex("Drive");
					  int ledcol = MainWin.getTPIndex("LED");
					  
					  String[] txt = new String[table.getColumnCount()];
					  for (int i = 0;i<txt.length;i++)
						  txt[i] = "";
					  
					  // make sure it exists
					  while (table.getItemCount() < (disk + 1))
					  {
						  TableItem item = new TableItem(table, SWT.NONE);
						  
						 
						  if (drivecol > -1)
							  item.setText(drivecol, disk+"");
					  }
					  
					  // clear all txt
					  table.getItem(disk).setText(txt);
					  
					  // set Drive #
					  if (drivecol > -1)
						  table.getItem(disk).setText(drivecol, disk+"");
					  
					  // clear image
					  if (ledcol > -1)
						  table.getItem(disk).setImage(ledcol,null);
					  
				  }
				  
			  }
		  });

		
	}




	public static String getServerText()
	{
		String res = "";
		
		for (LogItem e : MainWin.logItems)
		{
			res += e.getShortTimestamp() + "," + e.getLevel() + "," + e.getShortSource() + "," + e.getThread() + "," + e.getMessage() + "\r\n";
		}
		
		return(res);
	}


	


	public static boolean isReady()
	{
		return MainWin.ready;
	}


	public static void updateDiskTableItem(final int item, final String key, final Object object)
	{
		
		if (disks[item].isLoaded() && (display != null) && !display.isDisposed())
		{
			display.syncExec(new Runnable() {
					  public void run()
					  {
						  int keycol = MainWin.getTPIndex(key);

						  if (keycol > -1)
						  {
							  if (object.getClass().getSimpleName().equals("String"))
								  table.getItem(item).setText(keycol, object.toString());
							  else if (object.getClass().getSimpleName().equals("Image"))
								  table.getItem(item).setImage(keycol, (Image) object);
						  }
						  
					  }
				  });
		}
	}

	
	public static void updateDiskTabs()
	{
		display.syncExec(new Runnable() {
			  public void run()
			  {
				for (Control cti : MainWin.tabFolderOutput.getTabList())
				{
					if  ((cti.getClass().getCanonicalName().equals("com.groupunix.drivewireui.DWLibrary")))
					  {
						  if (!((DWLibrary) cti).getOurTab().isDisposed())
						  {
					
							  ((DWLibrary)cti).updateTree();
						  }
					}
				}
			  }
		});
	}
	
	

	public static Display getDisplay()
	{
		return display;
	}


	


	public static void addDiskTableColumn(String key)
	{
		synchronized(table)
		{
			if (MainWin.getTPIndex(key) < 0)
			{
				MainWin.config.addProperty("DiskTable_Items", key);
				
				while(table.getColumnCount() > 0)
				{
					table.getColumn(0).dispose();
				}
				
				MainWin.createDiskTableColumns();
				
				MainWin.refreshDiskTable();
				
			}
		}
	}


	public static void removeDiskTableColumn(String key)
	{
		synchronized(table)
		{
			if (MainWin.getTPIndex(key) > -1)
			{
				MainWin.config.clearProperty("DiskTable_Items(" + MainWin.getTPIndex(key) + ")");
				
				while(table.getColumnCount() > 0)
				{
					table.getColumn(0).dispose();
				}
				
				MainWin.createDiskTableColumns();
				
				MainWin.refreshDiskTable();
				
			}
		}
		
	}


	public static void submitServerConfigEvent(String key, Object val)
	{
		if (val == null)
		{
			MainWin.dwconfig.clearProperty(key);
		}
		else
		{
			MainWin.dwconfig.setProperty(key, val);
		}
		
		if ((serverconfigwin != null) && (!serverconfigwin.shlServerConfiguration.isDisposed()))
		{
			serverconfigwin.submitEvent(key,val);
		}
	}
	
	
	
	
	public static void showError(String t, String s, String d)
	{
		showError(new DWError(t,s,d,true));
	}
	
	public static void showError(String t, String s, String d, boolean gui)
	{
		showError(new DWError(t,s,d,!gui));
	}
	

	public static void showError(final DWError dwerror)
	{
	
		if (dwerror.isGui())
		{
			display.asyncExec(
					  new Runnable() {
						  public void run()
						  {
							  ErrorWin ew = new ErrorWin(shell,SWT.DIALOG_TRIM,dwerror);
							  ew.open();
						  }
					  });
			
		}
		else
		{
			int tid = MainWin.taskman.addTask("Error");
			
			MainWin.taskman.updateTask(tid, UITaskMaster.TASK_STATUS_FAILED, dwerror.getTextError());
		}
	}


	public static void addToServerLog(final LogItem litem)
	{
		if (!MainWin.lowMem || litem.isImportant() )
		{	
			display.asyncExec(
				  new Runnable() {
					  public void run()
					  {
						  int lisize;
							
						  synchronized(logItems)
						  {
								MainWin.logItems.add(litem);
								lisize = MainWin.logItems.size();
						  }
						  
						  logTable.setItemCount(lisize);
						  
						  if (MainWin.logscroll)
							  logTable.setTopIndex(lisize);
						  
						  
						  if (MainWin.tabFolderOutput.getSelectionIndex() != 1)
						  {
							 if (UIUtils.getLogLevelVal(litem.getLevel()) > MainWin.logNoticeLevel)
							 {
								 for (CTabItem ti : MainWin.tabFolderOutput.getItems())
								 {
									 if ((ti.getData("ttype") != null) && (ti.getData("ttype").equals("ui")))
											 MainWin.tabFolderOutput.getItems()[1].setImage(org.eclipse.wb.swt.SWTResourceManager.getImage(MainWin.class, "/logging/" + litem.getLevel().toLowerCase() + ".png"));
								 }
									 
								 MainWin.logNoticeLevel = UIUtils.getLogLevelVal(litem.getLevel());
							 }
						  }
						  
					  }
				  });
		}
	}
	
	
	

	public static void submitServerStatusEvent(final ServerStatusItem ssbuf)
	{
		//MainWin.debug(ssbuf.toString());
		
		synchronized(serverStatus)
		{
			MainWin.serverStatus  = ssbuf;
			
				
			
			// lowmem check.. using UI mem vals now but doing at server update interval seems ok 
			
			long realfree = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
			
			if (!lowMem)
			{
				if (realfree < MainWin.LOWMEM_START)
				{
					
					lowMemLogItem.setTimestamp(System.currentTimeMillis());
					MainWin.addToServerLog(lowMemLogItem);
					if (!taskman.hasTask(lowMemWarningTid)) 
						lowMemWarningTid = MainWin.taskman.addTask("Free memory status");
					
					MainWin.taskman.updateTask(lowMemWarningTid, UITaskMaster.TASK_STATUS_FAILED, "Due to extremely low free memory, some functions have been disabled.  This includes mounting new disk images.");
					
					
					lowMem = true;
				}
			}
			else
			{
				if (realfree > MainWin.LOWMEM_STOP)
				{
					LogItem li = new LogItem();
					li.setTimestamp(System.currentTimeMillis());
					li.setLevel("INFO");
					li.setSource("UI");
					li.setThread(Thread.currentThread().getName());
					li.setMessage("Due to increase in free memory, log display has been enabled.");
					lowMem = false;
					MainWin.addToServerLog(li);
					
					if (!taskman.hasTask(lowMemWarningTid)) 
						lowMemWarningTid = MainWin.taskman.addTask("Free memory status");
					
					MainWin.taskman.updateTask(lowMemWarningTid, UITaskMaster.TASK_STATUS_COMPLETE, "Free memory has increased, all functions are now enabled.");
					
					
				}
			}
			
			// magic check - turned off for now..
			/*
			if (ssbuf.getMagic() != MainWin.servermagic)
			{
				// do we care..
				if (config.getBoolean("LocalServer",true) && !noServer)
				{
					// lame
					if (MainWin.host.equals("127.0.0.1"))
					{
						MainWin.showError("We may have a problem...", "The server we've just connected to is not the server we just started.", "This usually happens because more than one copy of DriveWire is running.\n\nThe first instance is listening on the UI TCP port, so the second server cannot start it's UI thread.  Meanwhile, the client part of the second DriveWire then opens a connection to the UI port and becomes attached to the server of the *first* DriveWire.\n\nWhat now?\n\nEasy answer: Close this copy of DriveWire and go back to using the first one\n\nIf you actually wanted a second UI connected to the first server, carry on, you've got it.  I won't bother you again.  If that isn't what you wanted but you can't find another copy of DriveWire running, check your task manager/process list, it is likely to be listed there, possibly as simply 'java' or 'java.exe'.", false);
						
					}
				}
				
				servermagic = ssbuf.getMagic();
			}
			*/
		}
	}


	public static void setDWCmdText(String cmd)
	{
		MainWin.txtYouCanEnter.setText(cmd);
	}


	public static void selectUIPage()
	{
		if (MainWin.tabFolderOutput.getSelectionIndex() != 0)
		{
			MainWin.tabFolderOutput.setSelection(0);
		}
	}


	public static DiskDef[] getDiskDefs()
	{
		
		return MainWin.disks;
	}


	public static CTabItem getNineScreenTab()
	{
		
		CTabItem cti = new CTabItem(tabFolderOutput, SWT.NONE, 0);
		cti.setData("ttype","ninescreen");
		
		tabFolderOutput.setSelection(cti);
		return cti;
	}


	public static Composite getShell()
	{
		return shell;
	}


	
}
