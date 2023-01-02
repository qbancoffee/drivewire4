/*      */ package com.groupunix.drivewireui;
/*      */ import com.groupunix.drivewireserver.DriveWireServer;
/*      */ import com.swtdesigner.SWTResourceManager;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Vector;
/*      */ import javax.swing.SwingUtilities;
/*      */ import org.apache.commons.configuration.ConfigurationException;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.commons.configuration.XMLConfiguration;
/*      */ import org.apache.log4j.Appender;
/*      */ import org.apache.log4j.BasicConfigurator;
/*      */ import org.apache.log4j.ConsoleAppender;
///*      */ import org.apache.log4j.Layout;
/*      */ import org.apache.log4j.Level;
/*      */ import org.apache.log4j.Logger;
/*      */ import org.apache.log4j.PatternLayout;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.dnd.Clipboard;
/*      */ import org.eclipse.swt.dnd.TextTransfer;
/*      */ import org.eclipse.swt.dnd.Transfer;
/*      */ import org.eclipse.swt.events.ArmEvent;
/*      */ import org.eclipse.swt.events.ArmListener;
/*      */ import org.eclipse.swt.events.KeyAdapter;
/*      */ import org.eclipse.swt.events.KeyEvent;
/*      */ import org.eclipse.swt.events.KeyListener;
/*      */ import org.eclipse.swt.events.MenuAdapter;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseListener;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.events.ShellAdapter;
/*      */ import org.eclipse.swt.events.ShellEvent;
/*      */ import org.eclipse.swt.events.ShellListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.widgets.Canvas;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Decorations;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Monitor;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableColumn;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import com.swtdesigner.SWTResourceManager;
/*      */ import swing2swt.layout.BorderLayout;
/*      */ 
/*      */ public class MainWin {
/*   78 */   static Logger logger = Logger.getLogger(MainWin.class);
/*   79 */   private static PatternLayout logLayout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %m%n");
/*      */   
/*      */   public static final String DWUIVersion = "4.0.9c";
/*      */   
/*      */   public static final String DWUIVersionDate = "03/25/2012";
/*      */   
/*      */   public static final double LOWMEM_START = 4194304.0D;
/*      */   
/*      */   public static final double LOWMEM_STOP = 8388608.0D;
/*      */   
/*      */   public static final int DISPLAY_ANIM_TIMER = 90;
/*      */   
/*      */   public static final int LTYPE_LOCAL_ROOT = 0;
/*      */   
/*      */   public static final int LTYPE_LOCAL_FOLDER = 1;
/*      */   
/*      */   public static final int LTYPE_LOCAL_ENTRY = 2;
/*      */   
/*      */   public static final int LTYPE_NET_ROOT = 10;
/*      */   
/*      */   public static final int LTYPE_NET_FOLDER = 11;
/*      */   
/*      */   public static final int LTYPE_NET_ENTRY = 12;
/*      */   
/*      */   public static final int LTYPE_CLOUD_ROOT = 20;
/*      */   
/*      */   public static final int LTYPE_CLOUD_FOLDER = 21;
/*      */   
/*      */   public static final int LTYPE_CLOUD_ENTRY = 22;
/*      */   public static boolean lowMem = false;
/*      */   public static final String default_Host = "127.0.0.1";
/*      */   public static final int default_Port = 6800;
/*      */   public static final int default_Instance = 0;
/*      */   public static final int default_DiskHistorySize = 20;
/*      */   public static final int default_ServerHistorySize = 20;
/*      */   public static final int default_CmdHistorySize = 20;
/*      */   public static final int default_TCPTimeout = 15000;
/*      */   public static XMLConfiguration config;
/*      */   public static XMLConfiguration master;
/*      */   public static HierarchicalConfiguration dwconfig;
/*      */   public static final String configfile = "drivewireUI.xml";
/*  120 */   private static int currentDisk = 0;
/*      */   
/*      */   protected static Shell shell;
/*      */   
/*      */   private static Text txtYouCanEnter;
/*  125 */   private static int cmdhistpos = 0;
/*      */   
/*      */   private static Display display;
/*      */   
/*      */   private static String host;
/*      */   private static int port;
/*      */   private static int instance;
/*  132 */   private static Boolean connected = Boolean.valueOf(false);
/*      */   
/*      */   static Table table;
/*      */   
/*      */   private static SashForm sashForm;
/*      */   
/*      */   private static MenuItem mntmFile;
/*      */   
/*      */   private static MenuItem mntmTools;
/*      */   
/*      */   private static MenuItem mntmHdbdosTranslation;
/*      */   
/*      */   private static Menu menu_file;
/*      */   
/*      */   private static Menu menu_tools;
/*      */   
/*      */   private static Menu menu_config;
/*      */   
/*      */   private static Menu menu_help;
/*      */   private static MenuItem mntmChooseInstance;
/*      */   private static MenuItem mntmInitialConfig;
/*      */   private static MenuItem mntmUserInterface;
/*  154 */   private static Thread syncThread = null;
/*  155 */   public static int dwconfigserial = -1;
/*      */   
/*      */   private static MenuItem mntmMidi;
/*      */   
/*      */   private static Menu menuMIDIOutputs;
/*      */   
/*      */   private static MenuItem mntmLockInstruments;
/*      */   
/*      */   private static Menu menuMIDIProfiles;
/*      */   
/*      */   private static MenuItem mntmSetProfile;
/*      */   
/*      */   private static MenuItem mntmSetOutput;
/*      */   private static Thread dwThread;
/*      */   static CTabFolder tabFolderOutput;
/*      */   private static SyncThread syncObj;
/*  171 */   private static DiskDef[] disks = new DiskDef[256];
/*      */   
/*      */   private static MIDIStatus midiStatus;
/*      */   
/*      */   public static Color colorWhite;
/*      */   
/*      */   public static Color colorRed;
/*      */   
/*      */   public static Color colorGreen;
/*      */   
/*      */   public static Color colorBlack;
/*      */   
/*      */   public static Color colorCmdTxt;
/*      */   public static Color colorGraphBG;
/*      */   public static Color colorGraphFG;
/*      */   public static Color colorMemGraphFreeH;
/*      */   public static Color colorMemGraphFreeL;
/*      */   public static Color colorMemGraphUsed;
/*      */   protected static Font fontDiskNumber;
/*      */   protected static Font fontDiskGraph;
/*      */   protected static Font fontGraphLabel;
/*  192 */   private static Boolean driveactivity = Boolean.valueOf(false);
/*      */   
/*      */   private static MenuItem mitemInsert;
/*      */   
/*      */   private static MenuItem mntmInsertFromUrl;
/*      */   
/*      */   private static MenuItem mitemEject;
/*      */   
/*      */   private static MenuItem mitemExport;
/*      */   private static MenuItem mitemCreate;
/*      */   private static MenuItem mitemParameters;
/*      */   private static MenuItem mitemReload;
/*      */   private static MenuItem mitemController;
/*      */   protected static Vector<DiskStatusItem> diskStatusItems;
/*  206 */   protected static Vector<LogItem> logItems = new Vector<LogItem>();
/*      */   
/*      */   private static Table logTable;
/*      */   
/*      */   private static boolean ready = false;
/*      */   
/*      */   private static Image diskLEDgreen;
/*      */   
/*      */   private static Image diskLEDred;
/*      */   
/*      */   private static Image diskLEDdark;
/*      */   
/*      */   protected static Image diskBigLEDgreen;
/*      */   
/*      */   protected static Image diskBigLEDred;
/*      */   
/*      */   protected static Image diskBigLEDdark;
/*      */   private static DiskTableUpdateThread diskTableUpdater;
/*      */   protected static boolean safeshutdown = false;
/*      */   private static ServerConfigWin serverconfigwin;
/*      */   static UITaskMaster taskman;
/*      */   static ScrolledComposite scrolledComposite;
/*      */   public static Font logFont;
/*      */   public static Font dialogFont;
/*  230 */   public static ErrorHelpCache errorHelpCache = new ErrorHelpCache();
/*      */   
/*      */   private static Composite compositeList;
/*      */   
/*      */   private Composite compServer;
/*      */   
/*      */   protected static Image graphMemUse;
/*      */   protected static Image graphDiskOps;
/*      */   protected static Image graphVSerialOps;
/*      */   protected static Canvas canvasMemUse;
/*      */   protected static Canvas canvasDiskOps;
/*      */   protected static Canvas canvasVSerialOps;
/*  242 */   protected static ServerStatusItem serverStatus = new ServerStatusItem();
/*      */   protected static long servermagic;
/*      */   private Menu menu_1;
/*      */   private static boolean logscroll = true;
/*      */   private static MenuItem mntmCopy;
/*      */   private static MenuItem mntmSaveLogTo;
/*      */   private static LogItem lowMemLogItem;
/*  249 */   private static int lowMemWarningTid = -1;
/*      */   private static boolean noServer = false;
/*      */   private static boolean debugging = false;
/*  252 */   protected static int logNoticeLevel = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void main(String[] args) {
/*  258 */     noServer = UIUtils.hasArg(args, "noserver");
/*      */ 
/*      */ 
/*      */     
/*  262 */     Thread.currentThread().setName("dwuiMain-" + Thread.currentThread().getId());
/*  263 */     Thread.currentThread().setContextClassLoader(MainWin.class.getClassLoader());
/*      */ 
/*      */     
/*  266 */     BasicConfigurator.configure();
/*  267 */     Logger.getRootLogger().setLevel(Level.INFO);
/*  268 */     Logger.getRootLogger().removeAllAppenders();
/*  269 */     Logger.getRootLogger().addAppender((Appender)new ConsoleAppender((PatternLayout)logLayout));
/*      */ 
/*      */     
/*  272 */     lowMemLogItem = new LogItem();
/*  273 */     lowMemLogItem.setLevel("WARN");
/*  274 */     lowMemLogItem.setSource("UI");
/*  275 */     lowMemLogItem.setThread(Thread.currentThread().getName());
/*  276 */     lowMemLogItem.setMessage("Due to low free memory, display of non-critical log events and some UI functions have been disabled.");
/*      */ 
/*      */     
/*  279 */     Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
/*      */         {
/*      */ 
/*      */           
/*      */           public void uncaughtException(Thread t, final Throwable e)
/*      */           {
/*  285 */             System.out.println("Yikes!");
/*  286 */             System.out.println();
/*  287 */             System.out.println(UIUtils.getStackTrace(e));
/*      */             
/*  289 */             if (MainWin.shell != null && !MainWin.shell.isDisposed())
/*      */             {
/*      */               
/*  292 */               MainWin.shell.getDisplay().syncExec(new Runnable()
/*      */                   {
/*      */ 
/*      */                     
/*      */                     public void run()
/*      */                     {
/*  298 */                       ErrorWin ew = new ErrorWin(MainWin.shell, 2144, e.getClass().getSimpleName() + " in UI", "Well this is embarassing.. please submit a bug report with as much detail as possible.", "DriveWire will attempt to cleanly exit when this dialog is closed.  Please finish up anything important quickly (if the program is working well enough to allow that). \r\n\r\n" + UIUtils.getStackTrace(e));
/*  299 */                       ew.open();
/*      */                     }
/*      */                   });
/*      */             }
/*      */ 
/*      */             
/*  305 */             MainWin.logger.fatal(e.getClass().getSimpleName() + " in UI thread " + t.getName() + " " + t.getId());
/*      */             
/*  307 */             MainWin.stopDWServer();
/*  308 */             System.exit(1);
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  315 */     loadConfig();
/*      */ 
/*      */ 
/*      */     
/*  319 */     if (config.getBoolean("LocalServer", true) && !noServer) {
/*  320 */       startDWServer(args);
/*      */     }
/*      */     
/*  323 */     if (UIUtils.hasArg(args, "noui")) {
/*      */       
/*  325 */       logger.info("UI disabled (with --noui)");
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  331 */       Display.setAppName("DriveWire");
/*  332 */       Display.setAppVersion("4.0.9c");
/*      */       
/*  334 */       display = new Display();
/*      */       
/*  336 */       colorWhite = new Color((Device)display, 255, 255, 255);
/*  337 */       colorRed = new Color((Device)display, 255, 0, 0);
/*  338 */       colorGreen = new Color((Device)display, 0, 255, 0);
/*  339 */       colorBlack = new Color((Device)display, 0, 0, 0);
/*  340 */       colorCmdTxt = new Color((Device)display, 144, 144, 144);
/*      */       
/*  342 */       colorGraphBG = new Color((Device)display, 50, 50, 50);
/*  343 */       colorGraphFG = new Color((Device)display, 200, 200, 200);
/*      */       
/*  345 */       colorMemGraphFreeH = new Color((Device)display, 80, 255, 80);
/*  346 */       colorMemGraphFreeL = new Color((Device)display, 40, 200, 40);
/*      */       
/*  348 */       colorMemGraphUsed = new Color((Device)display, 200, 50, 50);
/*      */ 
/*      */ 
/*      */       
/*  352 */       diskLEDgreen = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledgreen.png");
/*  353 */       diskLEDred = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledred.png");
/*  354 */       diskLEDdark = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-leddark.png");
/*  355 */       diskBigLEDgreen = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledgreen-big.png");
/*  356 */       diskBigLEDred = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-ledred-big.png");
/*  357 */       diskBigLEDdark = SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-leddark-big.png");
/*      */ 
/*      */       
/*  360 */       UIUtils.loadFonts();
/*      */ 
/*      */       
/*  363 */       HashMap<String, Integer> fontmap = new HashMap<String, Integer>();
/*      */       
/*  365 */       fontmap.put("Droid Sans Mono", Integer.valueOf(0));
/*      */       
/*  367 */       logFont = UIUtils.findFont(display, fontmap, "WARNING", 40, 12);
/*      */       
/*  369 */       fontmap.clear();
/*  370 */       fontmap.put("Droid Sans", Integer.valueOf(0));
/*      */       
/*  372 */       fontGraphLabel = UIUtils.findFont(display, fontmap, "WARNING", 42, 13);
/*      */       
/*  374 */       graphMemUse = new Image(null, 270, 110);
/*  375 */       graphDiskOps = new Image(null, 270, 110);
/*  376 */       graphVSerialOps = new Image(null, 270, 110);
/*      */ 
/*      */       
/*  379 */       Thread gt = new Thread(new GrapherThread());
/*  380 */       gt.setDaemon(true);
/*  381 */       gt.start();
/*      */       
/*  383 */       MainWin window = new MainWin();
/*      */ 
/*      */ 
/*      */       
/*  387 */       doMacStuff();
/*      */ 
/*      */       
/*  390 */       restartServerConn();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  395 */       window.open(display, args);
/*      */     } 
/*      */ 
/*      */     
/*  399 */     host = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void startDWServer(final String[] args) {
/*  406 */     dwThread = new Thread(new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*      */           public void run()
/*      */           {
/*      */             try {
/*  414 */               MainWin.servermagic = DriveWireServer.getMagic();
/*  415 */               DriveWireServer.main(args);
/*      */             }
/*  417 */             catch (ConfigurationException e) {
/*      */               
/*  419 */               MainWin.logger.fatal(e.getMessage());
/*  420 */               System.exit(-1);
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/*  427 */     dwThread.start();
/*      */   }
/*      */ 
/*      */   
/*      */   private static void stopDWServer() {
/*  432 */     if (dwThread != null && !dwThread.isInterrupted()) {
/*      */       
/*  434 */       int tid = taskman.addTask("Stop local server");
/*      */       
/*  436 */       taskman.updateTask(tid, 0, "Stopping DriveWire server...");
/*      */ 
/*      */       
/*  439 */       dwThread.interrupt();
/*      */ 
/*      */       
/*      */       try {
/*  443 */         dwThread.join(3000L);
/*  444 */         taskman.updateTask(tid, 1, "DriveWire server shut down.");
/*      */       
/*      */       }
/*  447 */       catch (InterruptedException e) {
/*      */         
/*  449 */         taskman.updateTask(tid, 2, "Interrupted while waiting for the server to exit.");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void doMacStuff() {
/*  458 */     Menu systemMenu = display.getSystemMenu();
/*  459 */     if (systemMenu != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void loadConfig() {
/*      */     try {
/*  477 */       File f = new File("drivewireUI.xml");
/*      */       
/*  479 */       if (f.exists()) {
/*      */         
/*  481 */         config = new XMLConfiguration("drivewireUI.xml");
/*      */       }
/*      */       else {
/*      */         
/*  485 */         logger.info("Creating new UI config file");
/*  486 */         config = new XMLConfiguration();
/*  487 */         config.setFileName("drivewireUI.xml");
/*  488 */         config.addProperty("AutoCreated", Boolean.valueOf(true));
/*  489 */         config.save();
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  494 */       config.setAutoSave(true);
/*      */       
/*  496 */       host = config.getString("LastHost", "127.0.0.1");
/*  497 */       port = config.getInt("LastPort", 6800);
/*  498 */       instance = config.getInt("LastInstance", 0);
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  503 */         master = new XMLConfiguration(config.getString("MasterPath", "master.xml"));
/*  504 */         master.setAutoSave(false);
/*      */       }
/*  506 */       catch (ConfigurationException e1) {
/*      */         
/*  508 */         logger.error("Could not load master config, some functions will not work correctly:  " + e1.getMessage());
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  515 */       if (config.getBoolean("LocalServer", true) && !noServer) {
/*      */         
/*  517 */         f = new File(config.getString("ServerConfigFile", "config.xml"));
/*      */         
/*  519 */         if (!f.exists()) {
/*      */ 
/*      */           
/*  522 */           f = new File("default/serverconfig.xml");
/*  523 */           if (!f.exists()) {
/*      */             
/*  525 */             logger.fatal("LocalServer is true, but server config can not be found or created.");
/*  526 */             System.exit(-1);
/*      */           }
/*      */           else {
/*      */             
/*  530 */             logger.info("Creating new server config file");
/*      */             
/*      */             try {
/*  533 */               UIUtils.fileCopy(f.getCanonicalPath(), config.getString("ServerConfigFile", "config.xml"));
/*      */             }
/*  535 */             catch (IOException e) {
/*      */               
/*  537 */               logger.fatal("Error copying default server config: " + e.getMessage());
/*  538 */               System.exit(-1);
/*      */             }
/*      */           
/*      */           } 
/*      */         } 
/*      */       } 
/*  544 */     } catch (ConfigurationException e1) {
/*      */       
/*  546 */       System.out.println("Fatal - Could not process config file 'drivewireUI.xml'.  Please consult the documentation.");
/*  547 */       System.exit(-1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void open(final Display display, String[] args) {
/*  560 */     createContents();
/*      */     
/*  562 */     diskTableUpdater = new DiskTableUpdateThread();
/*  563 */     Thread dtuThread = new Thread(diskTableUpdater);
/*  564 */     dtuThread.setDaemon(true);
/*  565 */     dtuThread.start();
/*      */ 
/*      */ 
/*      */     
/*  569 */     if (!connected.booleanValue())
/*      */     {
/*  571 */       setItemsConnectionEnabled(false);
/*      */     }
/*      */ 
/*      */     
/*  575 */     shell.open();
/*  576 */     shell.layout();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  583 */     Runnable drivelightoff = new Runnable()
/*      */       {
/*      */         
/*  586 */         private int ctr = 0;
/*      */ 
/*      */         
/*      */         public void run() {
/*  590 */           if (MainWin.ready) {
/*      */             
/*  592 */             this.ctr++;
/*  593 */             if (this.ctr % 12 == 0 && MainWin.driveactivity.booleanValue() == true) {
/*      */ 
/*      */               
/*  596 */               for (int i = 0; i < 256; i++) {
/*      */                 
/*  598 */                 if (MainWin.table.getItem(i) != null)
/*      */                 {
/*  600 */                   if (MainWin.disks[i] != null && MainWin.disks[i].isLoaded()) {
/*  601 */                     MainWin.diskTableUpdater.addUpdate(i, "LED", MainWin.diskLEDdark);
/*      */                   }
/*      */                 }
/*      */               } 
/*      */ 
/*      */               
/*  607 */               MainWin.driveactivity = Boolean.valueOf(false);
/*      */             } 
/*      */ 
/*      */             
/*  611 */             MainWin.taskman.rotateWaiters();
/*      */             
/*  613 */             display.timerExec(90, this);
/*      */           } 
/*      */         }
/*      */       };
/*      */ 
/*      */     
/*  619 */     display.timerExec(2000, drivelightoff);
/*      */ 
/*      */     
/*  622 */     ready = true;
/*      */     
/*  624 */     int tid = taskman.addTask("/splash");
/*  625 */     taskman.updateTask(tid, 1, "4.0.9c (03/25/2012)");
/*      */     
/*  627 */     doSplashTimers(tid, true);
/*      */ 
/*      */     
/*  630 */     sashForm.forceFocus();
/*      */     
/*  632 */     while (!shell.isDisposed()) {
/*  633 */       if (!display.readAndDispatch()) {
/*  634 */         display.sleep();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void doSplashTimers(final int tid, boolean both) {
/*  649 */     final Runnable anim2 = new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/*      */             int i;
/*  655 */             for (i = 0; i < 10; i++)
/*      */             {
/*  657 */               ((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim2(i);
/*      */             }
/*      */ 
/*      */             
/*  661 */             for (i = 9; i >= 0; i--)
/*      */             {
/*  663 */               ((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim2(i);
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  668 */             ((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).showVer();
/*      */           
/*      */           }
/*  671 */           catch (DWUINoSuchTaskException e) {}
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  679 */     Runnable anim1 = new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/*  684 */             if (((UITaskCompositeSplash)MainWin.taskman.getTask(tid).getTaskcomp()).doAnim()) {
/*  685 */               MainWin.display.timerExec(10, this);
/*      */             } else {
/*  687 */               MainWin.display.timerExec(20, anim2);
/*      */             } 
/*  689 */           } catch (DWUINoSuchTaskException e) {}
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */     
/*  695 */     if (both) {
/*  696 */       display.timerExec(750, anim1);
/*      */     } else {
/*  698 */       display.timerExec(100, anim2);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void updateTitlebar() {
/*  709 */     String txt = "DriveWire - " + host + ":" + port + " [" + instance + "]";
/*      */     
/*  711 */     shell.setText(txt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void createContents() {
/*  719 */     shell = new Shell();
/*      */ 
/*      */ 
/*      */     
/*  723 */     shell.setImage(SWTResourceManager.getImage(MainWin.class, "/dw/dw4square.jpg"));
/*  724 */     shell.addShellListener((ShellListener)new ShellAdapter()
/*      */         {
/*      */ 
/*      */           
/*      */@Override
           public void shellClosed(ShellEvent e)
/*      */           {
/*  730 */             MainWin.doShutdown();
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/*  736 */     shell.setSize(config.getInt("MainWin_Width", 753), config.getInt("MainWin_Height", 486));
/*      */     
/*  738 */     if (config.containsKey("MainWin_x") && config.containsKey("MainWin_y")) {
/*      */       
/*  740 */       Point p = new Point(config.getInt("MainWin_x", 0), config.getInt("MainWin_y", 0));
/*      */       
/*  742 */       if (isValidDisplayPos(p)) {
/*  743 */         shell.setLocation(p);
/*      */       }
/*      */     } 
/*  746 */     shell.setText("DriveWire User Interface");
/*  747 */     shell.setLayout((Layout)new BorderLayout(0, 2));
/*      */     
/*  749 */     Menu menu = new Menu((Decorations)shell, 2);
/*  750 */     shell.setMenuBar(menu);
/*      */ 
/*      */ 
/*      */     
/*  754 */     mntmFile = new MenuItem(menu, 64);
/*  755 */     mntmFile.setText("File");
/*      */     
/*  757 */     menu_file = new Menu(mntmFile);
/*  758 */     mntmFile.setMenu(menu_file);
/*      */     
/*  760 */     MenuItem mntmChooseServer = new MenuItem(menu_file, 0);
/*  761 */     mntmChooseServer.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/computer-go.png"));
/*  762 */     mntmChooseServer.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  766 */             ChooseServerWin chooseServerWin = new ChooseServerWin(MainWin.shell, 2144);
/*  767 */             chooseServerWin.open();
/*      */           }
/*      */         });
/*      */     
/*  771 */     mntmChooseServer.setText("Choose server...");
/*      */     
/*  773 */     mntmChooseInstance = new MenuItem(menu_file, 0);
/*  774 */     mntmChooseInstance.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/view-list-tree-4.png"));
/*  775 */     mntmChooseInstance.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  779 */             ChooseInstanceWin window = new ChooseInstanceWin(MainWin.shell, 2144);
/*      */ 
/*      */             
/*      */             try {
/*  783 */               window.open();
/*      */             }
/*  785 */             catch (DWUIOperationFailedException e1) {
/*      */               
/*  787 */               MainWin.showError("Error sending command", e1.getMessage(), UIUtils.getStackTrace(e1));
/*      */             }
/*  789 */             catch (IOException e1) {
/*      */               
/*  791 */               MainWin.showError("Error sending command", e1.getMessage(), UIUtils.getStackTrace(e1));
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/*  796 */     mntmChooseInstance.setText("Choose instance...");
/*      */     
/*  798 */     new MenuItem(menu_file, 2);
/*      */     
/*  800 */     MenuItem mntmExit = new MenuItem(menu_file, 0);
/*  801 */     mntmExit.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/application-exit-5.png"));
/*  802 */     mntmExit.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  807 */             MainWin.shell.close();
/*      */           }
/*      */         });
/*  810 */     mntmExit.setText("Exit");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  816 */     mntmTools = new MenuItem(menu, 64);
/*      */     
/*  818 */     mntmTools.setText("Tools");
/*      */     
/*  820 */     menu_tools = new Menu(mntmTools);
/*  821 */     mntmTools.setMenu(menu_tools);
/*      */ 
/*      */     
/*  824 */     MenuItem mntmEjectAllDisks = new MenuItem(menu_tools, 0);
/*  825 */     mntmEjectAllDisks.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/media-eject.png"));
/*  826 */     mntmEjectAllDisks.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  830 */             MainWin.sendCommand("dw disk eject all");
/*      */           }
/*      */         });
/*  833 */     mntmEjectAllDisks.setText("Eject all disks");
/*      */     
/*  835 */     new MenuItem(menu_tools, 2);
/*      */     
/*  837 */     mntmHdbdosTranslation = new MenuItem(menu_tools, 32);
/*  838 */     mntmHdbdosTranslation.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  842 */             if (MainWin.mntmHdbdosTranslation.getSelection()) {
/*      */               
/*  844 */               MainWin.sendCommand("dw config set HDBDOSMode true");
/*      */             }
/*      */             else {
/*      */               
/*  848 */               MainWin.sendCommand("dw config set HDBDOSMode false");
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  854 */     mntmHdbdosTranslation.setText("HDBDOS translation");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  864 */     MenuItem mntmConfig = new MenuItem(menu, 64);
/*  865 */     mntmConfig.setText("Config");
/*      */     
/*  867 */     menu_config = new Menu(mntmConfig);
/*  868 */     mntmConfig.setMenu(menu_config);
/*      */     
/*  870 */     mntmInitialConfig = new MenuItem(menu_config, 0);
/*  871 */     mntmInitialConfig.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/wand.png"));
/*  872 */     mntmInitialConfig.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  876 */             MainWin.processClientCmd("/wizard");
/*      */           }
/*      */         });
/*  879 */     mntmInitialConfig.setText("Simple Config Wizard...");
/*      */     
/*  881 */     new MenuItem(menu_config, 2);
/*      */     
/*  883 */     MenuItem mntmServer_1 = new MenuItem(menu_config, 0);
/*  884 */     mntmServer_1.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/computer-edit.png"));
/*  885 */     mntmServer_1.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  891 */             ConfigEditor ce = new ConfigEditor(MainWin.display);
/*  892 */             if (!ce.isDisposed()) {
/*  893 */               ce.open();
/*      */             }
/*      */           }
/*      */         });
/*  897 */     mntmServer_1.setText("Configuration Editor...");
/*  898 */     mntmServer_1.setEnabled(false);
/*      */ 
/*      */     
/*  901 */     mntmUserInterface = new MenuItem(menu_config, 64);
/*      */     
/*  903 */     mntmUserInterface.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/image-edit.png"));
/*      */     
/*  905 */     mntmUserInterface.setText("User Interface Options");
/*      */     
/*  907 */     this.menu_1 = new Menu(mntmUserInterface);
/*  908 */     mntmUserInterface.setMenu(this.menu_1);
/*      */     
/*  910 */     final MenuItem mntmUseInternalServer = new MenuItem(this.menu_1, 32);
/*  911 */     mntmUseInternalServer.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  914 */             if (MainWin.config.getBoolean("LocalServer", false)) {
/*      */               
/*  916 */               MainWin.config.setProperty("LocalServer", Boolean.valueOf(false));
/*      */               
/*  918 */               MainWin.stopDWServer();
/*      */             
/*      */             }
/*      */             else {
/*      */               
/*  923 */               MainWin.config.setProperty("LocalServer", Boolean.valueOf(true));
/*      */               
/*  925 */               MainWin.startDWServer(null);
/*      */             } 
/*      */           }
/*      */         });
/*  929 */     mntmUseInternalServer.setText("Use internal server");
/*      */ 
/*      */ 
/*      */     
/*  933 */     final MenuItem mntmUseRemoteFile = new MenuItem(this.menu_1, 32);
/*  934 */     mntmUseRemoteFile.setText("Use remote file dialogs");
/*  935 */     mntmUseRemoteFile.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  938 */             if (MainWin.config.getBoolean("UseRemoteFilebrowser", false)) {
/*      */               
/*  940 */               MainWin.config.setProperty("UseRemoteFilebrowser", Boolean.valueOf(false));
/*      */             }
/*      */             else {
/*      */               
/*  944 */               MainWin.config.setProperty("UseRemoteFilebrowser", Boolean.valueOf(true));
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/*  951 */     mntmUserInterface.addArmListener(new ArmListener()
/*      */         {
/*      */           public void widgetArmed(ArmEvent e) {
/*  954 */             if (MainWin.dwThread.isAlive()) {
/*  955 */               mntmUseInternalServer.setSelection(true);
/*      */             } else {
/*  957 */               mntmUseInternalServer.setSelection(false);
/*      */             } 
/*  959 */             mntmUseRemoteFile.setSelection(MainWin.config.getBoolean("UseRemoteFilebrowser", false));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  964 */     mntmMidi = new MenuItem(menu_config, 64);
/*      */     
/*  966 */     mntmMidi.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/audio-keyboard.png"));
/*  967 */     mntmMidi.setText("MIDI");
/*      */     
/*  969 */     Menu menu_6 = new Menu(mntmMidi);
/*  970 */     mntmMidi.setMenu(menu_6);
/*      */     
/*  972 */     mntmSetOutput = new MenuItem(menu_6, 64);
/*  973 */     mntmSetOutput.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/audio-volume-high.png"));
/*  974 */     mntmSetOutput.setText("Set output");
/*      */     
/*  976 */     menuMIDIOutputs = new Menu(mntmSetOutput);
/*  977 */     mntmSetOutput.setMenu(menuMIDIOutputs);
/*      */ 
/*      */     
/*  980 */     MenuItem mntmLoadSoundbank = new MenuItem(menu_6, 64);
/*  981 */     mntmLoadSoundbank.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  985 */             String path = MainWin.getFile(false, false, "", "Choose a soundbank file to load...", "Open");
/*      */             
/*  987 */             if (path != null) {
/*  988 */               MainWin.sendCommand("dw midi synth bank " + path);
/*      */             }
/*      */           }
/*      */         });
/*  992 */     mntmLoadSoundbank.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/music.png"));
/*  993 */     mntmLoadSoundbank.setText("Load soundbank...");
/*      */ 
/*      */ 
/*      */     
/*  997 */     mntmSetProfile = new MenuItem(menu_6, 64);
/*  998 */     mntmSetProfile.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/format-text-columns.png"));
/*  999 */     mntmSetProfile.setText("Set profile");
/*      */     
/* 1001 */     menuMIDIProfiles = new Menu(mntmSetProfile);
/* 1002 */     mntmSetProfile.setMenu(menuMIDIProfiles);
/*      */     
/* 1004 */     mntmLockInstruments = new MenuItem(menu_6, 32);
/* 1005 */     mntmLockInstruments.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1009 */             MainWin.sendCommand("dw midi synth lock");
/*      */           }
/*      */         });
/* 1012 */     mntmLockInstruments.setText("Lock instruments");
/*      */     
/* 1014 */     MenuItem mntmPrinting = new MenuItem(menu_config, 64);
/*      */     
/* 1016 */     mntmPrinting.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/document-print.png"));
/* 1017 */     mntmPrinting.setText("Current Printer");
/*      */     
/* 1019 */     final Menu menu_2 = new Menu(mntmPrinting);
/* 1020 */     mntmPrinting.setMenu(menu_2);
/*      */     
/* 1022 */     mntmPrinting.addArmListener(new ArmListener() {
/*      */           public void widgetArmed(ArmEvent e) {
/* 1024 */             for (MenuItem i : menu_2.getItems())
/*      */             {
/* 1026 */               i.dispose();
/*      */             }
/*      */ 
/*      */             
/*      */             try {
/* 1031 */               List<String> res = UIUtils.loadList(MainWin.getInstance(), "ui instance printer");
/* 1032 */               String curp = "";
/*      */               
/* 1034 */               for (String l : res)
/*      */               {
/* 1036 */                 final String[] parts = l.split("\\|");
/* 1037 */                 if (parts.length > 1)
/*      */                 {
/* 1039 */                   if (parts[0].equals("currentprinter"))
/* 1040 */                     curp = parts[1].trim(); 
/* 1041 */                   if (parts[0].equals("printer") && parts.length == 3)
/*      */                   {
/* 1043 */                     MenuItem tmp = new MenuItem(menu_2, 32);
/* 1044 */                     tmp.setText(parts[1] + ": " + parts[2].trim());
/*      */                     
/* 1046 */                     if (parts[1].equals(curp)) {
/* 1047 */                       tmp.setSelection(true);
/*      */                     } else {
/* 1049 */                       tmp.setSelection(false);
/*      */                     } 
/* 1051 */                     tmp.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */                         {
/*      */                           public void widgetSelected(SelectionEvent e)
/*      */                           {
/* 1055 */                             MainWin.sendCommand("dw config set CurrentPrinter " + parts[1]);
/*      */                           }
/*      */                         });
/*      */                   
/*      */                   }
/*      */                 
/*      */                 }
/*      */               
/*      */               }
/*      */             
/*      */             }
/* 1066 */             catch (IOException e1) {
/*      */               
/* 1068 */               MainWin.showError("Error loading printer info", e1.getMessage(), UIUtils.getStackTrace(e1), false);
/*      */             }
/* 1070 */             catch (DWUIOperationFailedException e1) {
/*      */               
/* 1072 */               MainWin.showError("Error loading printer info", e1.getMessage(), UIUtils.getStackTrace(e1), false);
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1078 */     new MenuItem(menu_config, 2);
/*      */     
/* 1080 */     MenuItem mntmResetInstanceDevice = new MenuItem(menu_config, 0);
/* 1081 */     mntmResetInstanceDevice.setImage(SWTResourceManager.getImage(MainWin.class, "/toolbar/arrow-refresh.png"));
/* 1082 */     mntmResetInstanceDevice.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1086 */             MainWin.sendCommand("ui instance reset protodev");
/*      */           }
/*      */         });
/*      */     
/* 1090 */     mntmResetInstanceDevice.setText("Reset Instance Device");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1096 */     MenuItem mntmHelp = new MenuItem(menu, 64);
/* 1097 */     mntmHelp.setText("Help");
/*      */     
/* 1099 */     menu_help = new Menu(mntmHelp);
/* 1100 */     mntmHelp.setMenu(menu_help);
/*      */     
/* 1102 */     MenuItem mntmDocumentation = new MenuItem(menu_help, 0);
/* 1103 */     mntmDocumentation.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/world-link.png"));
/* 1104 */     mntmDocumentation.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1108 */             MainWin.openURL(getClass(), "http://sourceforge.net/apps/mediawiki/drivewireserver/index.php");
/*      */           }
/*      */         });
/* 1111 */     mntmDocumentation.setText("Documentation Wiki");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1116 */     new MenuItem(menu_help, 2);
/*      */     
/* 1118 */     MenuItem mntmSubmitBugReport = new MenuItem(menu_help, 0);
/* 1119 */     mntmSubmitBugReport.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/bug.png"));
/* 1120 */     mntmSubmitBugReport.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1124 */             BugReportWin brwin = new BugReportWin(MainWin.shell, 2144, "User submitted", "User submitted", "User submitted");
/* 1125 */             brwin.open();
/*      */           }
/*      */         });
/* 1128 */     mntmSubmitBugReport.setText("Submit bug report...");
/*      */     
/* 1130 */     new MenuItem(menu_help, 2);
/*      */     
/* 1132 */     MenuItem mntmAbout = new MenuItem(menu_help, 0);
/* 1133 */     mntmAbout.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/help-about-3.png"));
/* 1134 */     mntmAbout.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1139 */             AboutWin window = new AboutWin(MainWin.shell, 1264);
/* 1140 */             window.open();
/*      */           }
/*      */         });
/* 1143 */     mntmAbout.setText("About...");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1150 */     txtYouCanEnter = new Text((Composite)shell, 2048);
/* 1151 */     txtYouCanEnter.addMouseListener((MouseListener)new MouseAdapter()
/*      */         {
/*      */           public void mouseDown(MouseEvent e) {
/* 1154 */             if (MainWin.txtYouCanEnter.getText().equals("Hint: You can enter 'dw' commands here.  Enter dw by itself for help."))
/*      */             {
/* 1156 */               MainWin.txtYouCanEnter.setText("");
/*      */             }
/*      */           }
/*      */         });
/*      */     
/* 1161 */     txtYouCanEnter.setText("Hint: You can enter 'dw' commands here.  Enter dw by itself for help.");
/* 1162 */     txtYouCanEnter.setLayoutData("South");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1168 */     sashForm = new SashForm((Composite)shell, 66048);
/*      */     
/* 1170 */     sashForm.setLayoutData("Center");
/*      */ 
/*      */     
/* 1173 */     compositeList = new Composite((Composite)sashForm, 0);
/* 1174 */     compositeList.setLayout((Layout)new FillLayout(256));
/*      */ 
/*      */     
/* 1177 */     createOutputTabs();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1183 */     table = new Table(compositeList, 65536);
/* 1184 */     table.addMouseListener((MouseListener)new MouseAdapter()
/*      */         {
/*      */           public void mouseDoubleClick(MouseEvent e)
/*      */           {
/* 1188 */             int drive = MainWin.table.getSelectionIndex();
/*      */             
/* 1190 */             if (drive > -1 && MainWin.disks != null && MainWin.disks[drive] != null)
/*      */             {
/* 1192 */               if (MainWin.disks[drive].isLoaded()) {
/*      */                 
/* 1194 */                 if (MainWin.disks[drive].hasDiskwin())
/*      */                 {
/* 1196 */                   (MainWin.disks[drive].getDiskwin()).shlDwDrive.setActive();
/*      */ 
/*      */                 
/*      */                 }
/* 1200 */                 else if (!MainWin.lowMem)
/*      */                 {
/* 1202 */                   MainWin.disks[drive].setDiskwin(new DiskWin(MainWin.disks[drive], (MainWin.getDiskWinInitPos(drive)).x, (MainWin.getDiskWinInitPos(drive)).y));
/* 1203 */                   MainWin.disks[drive].getDiskwin().open(MainWin.display);
/*      */ 
/*      */                 
/*      */                 }
/*      */ 
/*      */               
/*      */               }
/* 1210 */               else if (!MainWin.lowMem) {
/* 1211 */                 MainWin.quickInDisk(drive);
/*      */               } 
/*      */             }
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1218 */     table.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1222 */             MainWin.currentDisk = MainWin.table.getSelectionIndex();
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1227 */     table.setHeaderVisible(true);
/* 1228 */     table.setLinesVisible(true);
/*      */     
/* 1230 */     createDiskTableColumns();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1236 */     Menu diskPopup = new Menu((Decorations)shell, 8);
/* 1237 */     diskPopup.addMenuListener((MenuListener)new MenuAdapter()
/*      */         {
/*      */           
/*      */           public void menuShown(MenuEvent e)
/*      */           {
/* 1242 */             int sdisk = MainWin.table.getSelectionIndex();
/* 1243 */             if (sdisk > -1) {
/*      */               
/* 1245 */               if (MainWin.lowMem) {
/*      */                 
/* 1247 */                 MainWin.mitemInsert.setText("Insert disabled due to low mem");
/* 1248 */                 MainWin.mntmInsertFromUrl.setText("Insert from URL disabled due to low mem");
/* 1249 */                 MainWin.mitemCreate.setText("Create disabled due to low mem");
/* 1250 */                 MainWin.mitemParameters.setText("Parameters disabled due to low mem");
/* 1251 */                 MainWin.mitemController.setText("Controller disabled due to low mem");
/*      */               
/*      */               }
/*      */               else {
/*      */                 
/* 1256 */                 MainWin.mitemInsert.setText("Insert disk for drive " + sdisk + "...");
/* 1257 */                 MainWin.mntmInsertFromUrl.setText("Insert disk from URL for drive " + sdisk + "...");
/* 1258 */                 MainWin.mitemCreate.setText("Create new disk for drive " + sdisk + "...");
/* 1259 */                 MainWin.mitemParameters.setText("Drive " + sdisk + " parameters...");
/* 1260 */                 MainWin.mitemController.setText("Open controller for drive " + sdisk + "...");
/*      */               } 
/*      */ 
/*      */               
/* 1264 */               MainWin.mitemReload.setText("Reload disk in drive " + sdisk + "...");
/* 1265 */               MainWin.mitemExport.setText("Export image in drive " + sdisk + " to...");
/* 1266 */               MainWin.mitemEject.setText("Eject disk in drive " + sdisk);
/*      */               
/* 1268 */               MainWin.mitemInsert.setEnabled(false);
/* 1269 */               MainWin.mntmInsertFromUrl.setEnabled(false);
/* 1270 */               MainWin.mitemCreate.setEnabled(false);
/* 1271 */               MainWin.mitemExport.setEnabled(false);
/* 1272 */               MainWin.mitemEject.setEnabled(false);
/* 1273 */               MainWin.mitemParameters.setEnabled(false);
/* 1274 */               MainWin.mitemReload.setEnabled(false);
/* 1275 */               MainWin.mitemController.setEnabled(false);
/*      */               
/* 1277 */               if (MainWin.disks != null && MainWin.disks[sdisk] != null) {
/*      */                 
/* 1279 */                 if (!MainWin.lowMem) {
/*      */                   
/* 1281 */                   MainWin.mitemInsert.setEnabled(true);
/* 1282 */                   MainWin.mntmInsertFromUrl.setEnabled(true);
/* 1283 */                   MainWin.mitemCreate.setEnabled(true);
/* 1284 */                   MainWin.mitemController.setEnabled(true);
/* 1285 */                   MainWin.mitemParameters.setEnabled(true);
/*      */                 } 
/*      */                 
/* 1288 */                 if (MainWin.disks[sdisk].isLoaded()) {
/*      */                   
/* 1290 */                   MainWin.mitemEject.setEnabled(true);
/* 1291 */                   MainWin.mitemExport.setEnabled(true);
/* 1292 */                   MainWin.mitemReload.setEnabled(true);
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1302 */     mitemInsert = new MenuItem(diskPopup, 8);
/* 1303 */     mitemInsert.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1307 */             MainWin.quickInDisk(MainWin.table.getSelectionIndex());
/*      */           }
/*      */         });
/* 1310 */     mitemInsert.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/disk-insert.png"));
/* 1311 */     mitemInsert.setText("Insert...");
/*      */     
/* 1313 */     mntmInsertFromUrl = new MenuItem(diskPopup, 0);
/* 1314 */     mntmInsertFromUrl.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1318 */             MainWin.quickURLInDisk(MainWin.table.getSelectionIndex());
/*      */           }
/*      */         });
/*      */     
/* 1322 */     mntmInsertFromUrl.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/world-link.png"));
/* 1323 */     mntmInsertFromUrl.setText("Insert from URL...");
/*      */     
/* 1325 */     new MenuItem(diskPopup, 2);
/*      */     
/* 1327 */     mitemReload = new MenuItem(diskPopup, 0);
/* 1328 */     mitemReload.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1331 */             MainWin.sendCommand("dw disk reload " + MainWin.table.getSelectionIndex(), false);
/*      */           }
/*      */         });
/* 1334 */     mitemReload.setImage(SWTResourceManager.getImage(MainWin.class, "/toolbar/arrow-refresh.png"));
/* 1335 */     mitemReload.setText("Reload...");
/*      */ 
/*      */     
/* 1338 */     mitemExport = new MenuItem(diskPopup, 8);
/* 1339 */     mitemExport.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1343 */             MainWin.writeDiskTo(MainWin.getCurrentDiskNo());
/*      */           }
/*      */         });
/* 1346 */     mitemExport.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/document-save-as-5.png"));
/* 1347 */     mitemExport.setText("Export...");
/*      */ 
/*      */     
/* 1350 */     mitemCreate = new MenuItem(diskPopup, 8);
/* 1351 */     mitemCreate.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1355 */             CreateDiskWin window = new CreateDiskWin(MainWin.shell, 2144);
/*      */             
/* 1357 */             window.open();
/*      */           }
/*      */         });
/* 1360 */     mitemCreate.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/new-disk-16.png"));
/* 1361 */     mitemCreate.setText("Create...");
/*      */ 
/*      */     
/* 1364 */     new MenuItem(diskPopup, 2);
/*      */ 
/*      */     
/* 1367 */     mitemEject = new MenuItem(diskPopup, 8);
/* 1368 */     mitemEject.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1372 */             MainWin.sendCommand("dw disk eject " + MainWin.table.getSelectionIndex());
/*      */           }
/*      */         });
/* 1375 */     mitemEject.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/media-eject.png"));
/* 1376 */     mitemEject.setText("Eject");
/*      */ 
/*      */ 
/*      */     
/* 1380 */     new MenuItem(diskPopup, 2);
/*      */     
/* 1382 */     mitemController = new MenuItem(diskPopup, 0);
/* 1383 */     mitemController.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1386 */             int drive = MainWin.table.getSelectionIndex();
/* 1387 */             if (drive > -1 && MainWin.disks != null && MainWin.disks[drive] != null) {
/*      */               
/* 1389 */               if (MainWin.disks[drive].hasDiskwin())
/*      */               {
/* 1391 */                 (MainWin.disks[drive].getDiskwin()).shlDwDrive.setActive();
/*      */               }
/*      */               else
/*      */               {
/* 1395 */                 MainWin.disks[drive].setDiskwin(new DiskWin(MainWin.disks[drive], (MainWin.getDiskWinInitPos(drive)).x, (MainWin.getDiskWinInitPos(drive)).y));
/* 1396 */                 MainWin.disks[drive].getDiskwin().open(MainWin.display);
/*      */               }
/*      */             
/*      */             }
/*      */             else {
/*      */               
/* 1402 */               MainWin.showError("Disk system not initialized", "It seems our disk drive objects are null.", "Maybe the server is still starting up, or maybe it has a serious problem.");
/*      */             } 
/*      */           }
/*      */         });
/* 1406 */     mitemController.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/disk-controller.png"));
/* 1407 */     mitemController.setText("Controller...");
/*      */     
/* 1409 */     mitemParameters = new MenuItem(diskPopup, 8);
/* 1410 */     mitemParameters.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1414 */             if (MainWin.disks[MainWin.table.getSelectionIndex()].hasParamwin()) {
/*      */               
/* 1416 */               (MainWin.disks[MainWin.table.getSelectionIndex()].getParamwin()).shell.setActive();
/*      */             }
/*      */             else {
/*      */               
/* 1420 */               MainWin.disks[MainWin.table.getSelectionIndex()].setParamwin(new DiskAdvancedWin(MainWin.shell, 2144, MainWin.disks[MainWin.table.getSelectionIndex()]));
/* 1421 */               MainWin.disks[MainWin.table.getSelectionIndex()].getParamwin().open();
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/* 1426 */     mitemParameters.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/disk-params.png"));
/* 1427 */     mitemParameters.setText("Parameters...");
/*      */     
/* 1429 */     table.setMenu(diskPopup);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1436 */     if (config.getInt("SashForm_Weights(0)", 1) != 0 && config.getInt("SashForm_Weights(1)", 1) != 0) {
/* 1437 */       setSashformWeights(new int[] { config.getInt("SashForm_Weights(0)", 391), config.getInt("SashForm_Weights(1)", 136) });
/*      */     }
/*      */     
/* 1440 */     txtYouCanEnter.addKeyListener((KeyListener)new KeyAdapter()
/*      */         {
/*      */           public void keyPressed(KeyEvent e)
/*      */           {
/* 1444 */             if (e.character == '\r') {
/*      */               
/* 1446 */               if (!MainWin.txtYouCanEnter.getText().trim().equals(""))
/*      */               {
/* 1448 */                 MainWin.sendCommand(MainWin.txtYouCanEnter.getText().trim(), true);
/* 1449 */                 MainWin.addCommandToHistory(MainWin.txtYouCanEnter.getText().trim());
/* 1450 */                 MainWin.txtYouCanEnter.setText("");
/* 1451 */                 MainWin.cmdhistpos = 0;
/*      */               }
/*      */             
/* 1454 */             } else if (e.keyCode == 16777217) {
/*      */ 
/*      */               
/* 1457 */               if (MainWin.config.getInt("CmdHistorySize", 20) > 0)
/*      */               {
/*      */ 
/*      */                 
/* 1461 */                 List<String> cmdhist = MainWin.config.getList("CmdHistory", null);
/*      */                 
/* 1463 */                 if (cmdhist != null)
/*      */                 {
/* 1465 */                   if (cmdhist.size() > MainWin.cmdhistpos) {
/*      */                     MainWin.cmdhistpos++;
/*      */ 
/*      */                     
/* 1469 */                     MainWin.txtYouCanEnter.setText(cmdhist.get(cmdhist.size() - MainWin.cmdhistpos));
/* 1470 */                     MainWin.txtYouCanEnter.setSelection(MainWin.txtYouCanEnter.getText().length() + 1);
/*      */                     
/* 1472 */                     e.doit = false;
/*      */                   }
/*      */                 
/*      */                 }
/*      */               }
/*      */             
/*      */             }
/* 1479 */             else if (e.keyCode == 16777218) {
/*      */ 
/*      */               
/* 1482 */               if (MainWin.config.getInt("CmdHistorySize", 20) > 0) {
/*      */ 
/*      */                 
/* 1485 */                 List<String> cmdhist = MainWin.config.getList("CmdHistory", null);
/*      */                 
/* 1487 */                 if (MainWin.cmdhistpos > 1) {
/*      */                   MainWin.cmdhistpos--;
/*      */                   
/* 1490 */                   MainWin.txtYouCanEnter.setText(cmdhist.get(cmdhist.size() - MainWin.cmdhistpos));
/* 1491 */                   MainWin.txtYouCanEnter.setSelection(MainWin.txtYouCanEnter.getText().length() + 1);
/*      */                 
/*      */                 }
/* 1494 */                 else if (MainWin.cmdhistpos == 1) {
/*      */                   MainWin.cmdhistpos--;
/*      */                   
/* 1497 */                   MainWin.txtYouCanEnter.setText("");
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setSashformWeights(int[] w) {
/* 1517 */     if (!shell.isDisposed() && sashForm != null && !sashForm.isDisposed()) {
/*      */       
/*      */       try {
/*      */         
/* 1521 */         sashForm.setWeights(w);
/*      */       }
/* 1523 */       catch (IllegalArgumentException e) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] getSashformWeights() {
/* 1533 */     return sashForm.getWeights();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void createDiskTableColumns() {
/* 1539 */     synchronized (table) {
/*      */       
/* 1541 */       for (String key : getDiskTableParams()) {
/*      */         
/* 1543 */         TableColumn col = new TableColumn(table, 0);
/* 1544 */         col.setMoveable(true);
/* 1545 */         col.setResizable(true);
/* 1546 */         col.setData("param", key);
/*      */         
/* 1548 */         col.setWidth(config.getInt(key + "_ColWidth", 50));
/*      */         
/* 1550 */         if (key.startsWith("_")) {
/*      */           
/* 1552 */           if (key.length() > 4) {
/* 1553 */             col.setText(key.substring(1, 2).toUpperCase() + key.substring(2)); continue;
/* 1554 */           }  if (key.length() > 1)
/* 1555 */             col.setText(key.substring(1).toUpperCase());  continue;
/*      */         } 
/* 1557 */         if (!key.equals("LED") && key.length() > 1) {
/* 1558 */           col.setText(key.substring(0, 1).toUpperCase() + key.substring(1));
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private static List<String> getDiskTableParams() {
/* 1566 */     return Arrays.asList(config.getStringArray("DiskTable_Items"));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getTPIndex(String key) {
/* 1572 */     synchronized (table) {
/*      */       
/* 1574 */       for (int i = 0; i < table.getColumnCount(); i++) {
/*      */         
/* 1576 */         if (table.getColumn(i).getData("param").equals(key)) {
/* 1577 */           return i;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1582 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Point getDiskWinInitPos(int drive) {
/* 1587 */     Point res = new Point(config.getInt("DiskWin_" + drive + "_x", (shell.getLocation()).x + 20), config.getInt("DiskWin_" + drive + "_y", (shell.getLocation()).y + 20));
/*      */     
/* 1589 */     if (!isValidDisplayPos(res)) {
/* 1590 */       res = new Point((shell.getLocation()).x + 20, (shell.getLocation()).y + 20);
/*      */     }
/*      */     
/* 1593 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isValidDisplayPos(Point p) {
/* 1600 */     Monitor[] list = display.getMonitors();
/*      */     
/* 1602 */     for (int i = 0; i < list.length; i++) {
/*      */       
/* 1604 */       if (list[i].getBounds().contains(p))
/* 1605 */         return true; 
/*      */     } 
/* 1607 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void doShutdown() {
/* 1618 */     ShutdownWin sdwin = new ShutdownWin(shell, 2144);
/*      */ 
/*      */     
/* 1621 */     sdwin.open();
/*      */ 
/*      */     
/* 1624 */     sdwin.setStatus("Encouraging consistency...", 10);
/*      */ 
/*      */     
/* 1627 */     host = null;
/* 1628 */     ready = false;
/* 1629 */     syncObj.die();
/*      */ 
/*      */ 
/*      */     
/* 1633 */     if (config.getBoolean("TermServerOnExit", false) || config.getBoolean("LocalServer", false)) {
/*      */ 
/*      */       
/* 1636 */       sdwin.setStatus("Stopping DriveWire server...", 25);
/*      */       
/* 1638 */       stopDWServer();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1645 */     sdwin.setStatus("Saving main window layout...", 40);
/*      */ 
/*      */ 
/*      */     
/* 1649 */     config.setProperty("MainWin_Width", Integer.valueOf((shell.getSize()).x));
/* 1650 */     config.setProperty("MainWin_Height", Integer.valueOf((shell.getSize()).y));
/*      */     
/* 1652 */     config.setProperty("MainWin_x", Integer.valueOf((shell.getLocation()).x));
/* 1653 */     config.setProperty("MainWin_y", Integer.valueOf((shell.getLocation()).y));
/*      */ 
/*      */ 
/*      */     
/* 1657 */     if (sashForm.getWeights()[0] != 0 && sashForm.getWeights()[1] != 0) {
/* 1658 */       config.setProperty("SashForm_Weights", sashForm.getWeights());
/*      */     }
/* 1660 */     sdwin.setStatus("Saving main window layout...", 50);
/*      */     int i;
/* 1662 */     for (i = 0; i < table.getColumnCount(); i++) {
/*      */       
/* 1664 */       config.setProperty("DiskTable_Items(" + i + ")", table.getColumn(table.getColumnOrder()[i]).getData("param"));
/* 1665 */       config.setProperty(table.getColumn(i).getData("param") + "_ColWidth", Integer.valueOf(table.getColumn(i).getWidth()));
/*      */     } 
/*      */ 
/*      */     
/* 1669 */     sdwin.setStatus("Saving disk window layouts...", 65);
/*      */ 
/*      */ 
/*      */     
/* 1673 */     for (i = 0; i < 256; i++) {
/*      */       
/* 1675 */       sdwin.setProgress(65 + i / 8);
/*      */       
/* 1677 */       if (disks != null && 
/* 1678 */         disks[i] != null && disks[i].hasDiskwin()) {
/*      */         
/* 1680 */         disks[i].getDiskwin().close();
/* 1681 */         config.setProperty("DiskWin_" + i + "_open", Boolean.valueOf(true));
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1686 */     sdwin.setStatus("Exiting...", 100);
/*      */ 
/*      */     
/* 1689 */     int waits = 0;
/*      */     
/* 1691 */     while (display.readAndDispatch() && waits < 150) {
/*      */ 
/*      */       
/*      */       try {
/* 1695 */         waits++;
/* 1696 */         Thread.sleep(20L);
/* 1697 */       } catch (InterruptedException e) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1704 */     System.exit(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void createOutputTabs() {
/* 1712 */     tabFolderOutput = new CTabFolder((Composite)sashForm, 2048);
/* 1713 */     tabFolderOutput.setSimple(false);
/*      */     
/* 1715 */     tabFolderOutput.marginHeight = 0;
/* 1716 */     tabFolderOutput.marginWidth = 0;
/* 1717 */     tabFolderOutput.setTabHeight(24);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1733 */     tabFolderOutput.setSelectionBackground(new Color[] { display.getSystemColor(22), display.getSystemColor(31) }, new int[] { 75 }, true);
/*      */     
/* 1735 */     tabFolderOutput.setSelectionForeground(display.getSystemColor(30));
/*      */     
/* 1737 */     tabFolderOutput.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1742 */             if (e.item.getData("ttype").equals("server")) {
/*      */               
/* 1744 */               MainWin.logNoticeLevel = -1;
/*      */               
/* 1746 */               ((CTabItem)e.item).setImage(SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png"));
/*      */             
/*      */             }
/* 1749 */             else if (e.item.getData("ttype").equals("ui")) {
/*      */               
/* 1751 */               ((CTabItem)e.item).setImage(SWTResourceManager.getImage(MainWin.class, "/menu/inactive.png"));
/*      */             
/*      */             }
/* 1754 */             else if (!e.item.getData("ttype").equals("library")) {
/*      */ 
/*      */ 
/*      */               
/* 1758 */               if (e.item.getData("ttype").equals("browser"))
/*      */               {
/* 1760 */                 if (e.item.getData("addbrowser") != null && e.item.getData("addbrowser").equals("yes"))
/*      */                 {
/* 1762 */                   MainWin.addNewBrowserTab((CTabItem)e.item, MainWin.config.getString("Browser_homepage", "http://cococoding.com/cloud"));
/*      */                 }
/*      */               }
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1773 */     CTabItem tbtmUi = new CTabItem(tabFolderOutput, 0);
/*      */     
/* 1775 */     tbtmUi.setText("UI  ");
/* 1776 */     tbtmUi.setToolTipText("Output from UI events");
/*      */     
/* 1778 */     tbtmUi.setData("ttype", "ui");
/*      */     
/* 1780 */     scrolledComposite = new ScrolledComposite((Composite)tabFolderOutput, 536871424);
/* 1781 */     tbtmUi.setControl((Control)scrolledComposite);
/* 1782 */     scrolledComposite.setAlwaysShowScrollBars(true);
/* 1783 */     scrolledComposite.setBackground(colorWhite);
/*      */     
/* 1785 */     Composite scrollcontents = new Composite((Composite)scrolledComposite, 536870912);
/* 1786 */     scrollcontents.setBackground(colorWhite);
/*      */     
/* 1788 */     scrolledComposite.setContent((Control)scrollcontents);
/* 1789 */     scrolledComposite.setExpandHorizontal(true);
/*      */ 
/*      */     
/* 1792 */     taskman = new UITaskMaster(scrollcontents);
/*      */ 
/*      */     
/* 1795 */     CTabItem tbtmServer = new CTabItem(tabFolderOutput, 0);
/* 1796 */     tbtmServer.setText("Server  ");
/*      */     
/* 1798 */     tbtmServer.setData("ttype", "server");
/*      */     
/* 1800 */     tbtmServer.setToolTipText("Server status and log");
/* 1801 */     Composite composite_1 = new Composite((Composite)tabFolderOutput, 0);
/*      */ 
/*      */     
/* 1804 */     composite_1.setBackground(SWTResourceManager.getColor(3));
/*      */     
/* 1806 */     tbtmServer.setControl((Control)composite_1);
/* 1807 */     composite_1.setLayout((Layout)new BorderLayout(0, 0));
/*      */     
/* 1809 */     this.compServer = new Composite(composite_1, 0);
/* 1810 */     this.compServer.setLayoutData("North");
/* 1811 */     this.compServer.setBackgroundImage(SWTResourceManager.getImage(MainWin.class, "/logging/serverbgbar.png"));
/*      */ 
/*      */     
/* 1814 */     canvasDiskOps = new Canvas(this.compServer, 536870912);
/* 1815 */     canvasDiskOps.setBounds(20, 0, (graphDiskOps.getImageData()).width, (graphDiskOps.getImageData()).height);
/*      */     
/* 1817 */     canvasDiskOps.addPaintListener(new PaintListener()
/*      */         {
/*      */           public void paintControl(PaintEvent event)
/*      */           {
/* 1821 */             event.gc.drawImage(MainWin.graphDiskOps, 0, 0);
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/* 1827 */     canvasVSerialOps = new Canvas(this.compServer, 536870912);
/* 1828 */     canvasVSerialOps.setBounds(290, 0, (graphVSerialOps.getImageData()).width, (graphVSerialOps.getImageData()).height);
/*      */     
/* 1830 */     canvasVSerialOps.addPaintListener(new PaintListener()
/*      */         {
/*      */           public void paintControl(PaintEvent event)
/*      */           {
/* 1834 */             event.gc.drawImage(MainWin.graphVSerialOps, 0, 0);
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1841 */     canvasMemUse = new Canvas(this.compServer, 536870912);
/* 1842 */     canvasMemUse.setBounds(560, 0, (graphMemUse.getImageData()).width, (graphMemUse.getImageData()).height);
/*      */     
/* 1844 */     canvasMemUse.addPaintListener(new PaintListener()
/*      */         {
/*      */           public void paintControl(PaintEvent event)
/*      */           {
/* 1848 */             event.gc.drawImage(MainWin.graphMemUse, 0, 0);
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1855 */     logTable = new Table(composite_1, 268500994);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1861 */     logTable.addListener(36, new Listener()
/*      */         {
/*      */           public void handleEvent(Event event) {
/* 1864 */             TableItem item = (TableItem)event.item;
/* 1865 */             int i = event.index;
/*      */             
/* 1867 */             synchronized (MainWin.logItems) {
/*      */               
/* 1869 */               if (i < MainWin.logItems.size()) {
/*      */                 
/* 1871 */                 item.setImage(0, SWTResourceManager.getImage(MainWin.class, "/logging/" + ((LogItem)MainWin.logItems.get(i)).getLevel().toLowerCase() + ".png"));
/* 1872 */                 item.setText(1, ((LogItem)MainWin.logItems.get(i)).getShortTimestamp());
/* 1873 */                 item.setText(2, ((LogItem)MainWin.logItems.get(i)).getLevel());
/* 1874 */                 item.setText(3, ((LogItem)MainWin.logItems.get(i)).getShortSource());
/* 1875 */                 item.setText(4, ((LogItem)MainWin.logItems.get(i)).getThread());
/* 1876 */                 item.setText(5, ((LogItem)MainWin.logItems.get(i)).getMessage());
/*      */               }
/*      */               else {
/*      */                 
/* 1880 */                 item.setText(1, "hmmm");
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/* 1886 */     logTable.setHeaderVisible(true);
/*      */     
/* 1888 */     TableColumn logImg = new TableColumn(logTable, 16);
/* 1889 */     logImg.setText("");
/* 1890 */     logImg.setWidth(24);
/*      */     
/* 1892 */     TableColumn logTime = new TableColumn(logTable, 16);
/* 1893 */     logTime.setText("Time");
/* 1894 */     logTime.setWidth(80);
/*      */     
/* 1896 */     TableColumn logLevel = new TableColumn(logTable, 16);
/* 1897 */     logLevel.setText("Level");
/* 1898 */     logLevel.setWidth(60);
/*      */     
/* 1900 */     TableColumn logSource = new TableColumn(logTable, 16);
/* 1901 */     logSource.setText("Source");
/* 1902 */     logSource.setWidth(100);
/*      */     
/* 1904 */     TableColumn logThread = new TableColumn(logTable, 16);
/* 1905 */     logThread.setText("Thread");
/* 1906 */     logThread.setWidth(100);
/*      */ 
/*      */     
/* 1909 */     TableColumn logMessage = new TableColumn(logTable, 16);
/* 1910 */     logMessage.setText("Message");
/* 1911 */     logMessage.setWidth(400);
/*      */     
/* 1913 */     Menu menu_3 = new Menu((Control)logTable);
/* 1914 */     menu_3.addMenuListener((MenuListener)new MenuAdapter()
/*      */         {
/*      */           public void menuShown(MenuEvent e) {
/* 1917 */             if (MainWin.logTable.getSelectionCount() > 0) {
/*      */               
/* 1919 */               MainWin.mntmCopy.setEnabled(true);
/* 1920 */               if (MainWin.logTable.getSelectionCount() > 1) {
/* 1921 */                 MainWin.mntmCopy.setText("Copy " + MainWin.logTable.getSelectionCount() + " selected items");
/*      */               } else {
/* 1923 */                 MainWin.mntmCopy.setText("Copy selected item");
/*      */               } 
/*      */             } else {
/*      */               
/* 1927 */               MainWin.mntmCopy.setEnabled(false);
/* 1928 */               MainWin.mntmCopy.setText("Copy");
/*      */             } 
/*      */             
/* 1931 */             if (MainWin.logItems.size() > 0) {
/* 1932 */               MainWin.mntmSaveLogTo.setEnabled(true);
/*      */             } else {
/* 1934 */               MainWin.mntmSaveLogTo.setEnabled(false);
/*      */             }  }
/*      */         });
/* 1937 */     logTable.setMenu(menu_3);
/*      */     
/* 1939 */     MenuItem mntmAutoscroll = new MenuItem(menu_3, 32);
/* 1940 */     mntmAutoscroll.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1943 */             if (MainWin.logscroll) {
/* 1944 */               MainWin.logscroll = false;
/*      */             } else {
/* 1946 */               MainWin.logscroll = true;
/*      */             } 
/* 1948 */             ((MenuItem)e.widget).setSelection(MainWin.logscroll);
/*      */           }
/*      */         });
/* 1951 */     mntmAutoscroll.setText("Autoscroll");
/* 1952 */     mntmAutoscroll.setSelection(logscroll);
/*      */     
/* 1954 */     new MenuItem(menu_3, 2);
/*      */     
/* 1956 */     mntmCopy = new MenuItem(menu_3, 0);
/* 1957 */     mntmCopy.setText("Copy");
/*      */     
/* 1959 */     mntmCopy.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1963 */             if (MainWin.logTable.getSelectionCount() > 0) {
/*      */               
/* 1965 */               String logtxt = "";
/*      */               
/* 1967 */               for (int i : MainWin.logTable.getSelectionIndices())
/*      */               {
/* 1969 */                 logtxt = logtxt + ((LogItem)MainWin.logItems.get(i)).toString() + System.getProperty("line.separator");
/*      */               }
/*      */               
/* 1972 */               Clipboard clipboard = new Clipboard(MainWin.getDisplay());
/*      */               
/* 1974 */               TextTransfer textTransfer = TextTransfer.getInstance();
/*      */               
/* 1976 */               Transfer[] transfers = { (Transfer)textTransfer };
/* 1977 */               Object[] data = { logtxt };
/* 1978 */               clipboard.setContents(data, transfers);
/* 1979 */               clipboard.dispose();
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/* 1984 */     mntmSaveLogTo = new MenuItem(menu_3, 0);
/* 1985 */     mntmSaveLogTo.setText("Save log to file...");
/*      */     
/* 1987 */     mntmSaveLogTo.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1991 */             UIUtils.saveLogItemsToFile();
/*      */           }
/*      */         });
/*      */     
/* 1995 */     new MenuItem(menu_3, 2);
/*      */     
/* 1997 */     MenuItem mntmClearLog = new MenuItem(menu_3, 0);
/* 1998 */     mntmClearLog.setText("Clear log");
/*      */     
/* 2000 */     mntmClearLog.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 2003 */             synchronized (MainWin.logItems) {
/*      */               
/* 2005 */               MainWin.logItems.removeAllElements();
/* 2006 */               MainWin.logTable.removeAll();
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2014 */     CTabItem tbtmLibrary = new CTabItem(tabFolderOutput, 0);
/* 2015 */     tbtmLibrary.setText("Library");
/* 2016 */     tbtmLibrary.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
/* 2017 */     tbtmLibrary.setData("ttype", "library");
/*      */ 
/*      */     
/* 2020 */     tbtmLibrary.setControl((Control)new DWLibrary((Composite)tabFolderOutput, 65536));
/*      */ 
/*      */     
/* 2023 */     addNewBrowserTab(null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void addBrowserTab(String url) {
/* 2033 */     if (tabFolderOutput.getItem(tabFolderOutput.getItemCount() - 1).getData("addbrowser") != null)
/*      */     {
/* 2035 */       addNewBrowserTab(tabFolderOutput.getItem(tabFolderOutput.getItemCount() - 1), url);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static void addNewBrowserTab(CTabItem tab, String url) {
/* 2041 */     if (tab != null) {
/*      */ 
/*      */       
/* 2044 */       tab.setControl((Control)new DWBrowser(tab, url));
/* 2045 */       tab.setData("addbrowser", null);
/* 2046 */       tab.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/www.png"));
/* 2047 */       tab.setShowClose(true);
/* 2048 */       tab.setText("DW Browser");
/*      */     } 
/*      */ 
/*      */     
/* 2052 */     CTabItem tbtmAdd = new CTabItem(tabFolderOutput, 0);
/* 2053 */     tbtmAdd.setText("");
/* 2054 */     tbtmAdd.setToolTipText("Add browser tab...");
/* 2055 */     tbtmAdd.setImage(SWTResourceManager.getImage(MainWin.class, "/menu/add.png"));
/*      */     
/* 2057 */     tbtmAdd.setData("ttype", "browser");
/* 2058 */     tbtmAdd.setData("addbrowser", "yes");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void debug(String string) {
/* 2065 */     if (debugging == true) {
/* 2066 */       System.out.println("debug: thread " + Thread.currentThread().getName() + ": " + string);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void openURL(Class cl, String url) {
/* 2076 */     boolean failed = false;
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2081 */       Class<?> c = Class.forName("org.eclipse.swt.program.Program", true, cl.getClassLoader());
/* 2082 */       Method launch = c.getMethod("launch", new Class[] { String.class });
/* 2083 */       launch.invoke(null, new Object[] { url });
/*      */ 
/*      */     
/*      */     }
/* 2087 */     catch (ClassNotFoundException e) {
/*      */       
/* 2089 */       failed = true;
/*      */     }
/* 2091 */     catch (SecurityException e) {
/*      */       
/* 2093 */       failed = true;
/*      */     }
/* 2095 */     catch (NoSuchMethodException e) {
/*      */       
/* 2097 */       failed = true;
/*      */     }
/* 2099 */     catch (IllegalArgumentException e) {
/*      */       
/* 2101 */       failed = true;
/*      */     }
/* 2103 */     catch (IllegalAccessException e) {
/*      */       
/* 2105 */       failed = true;
/*      */     }
/* 2107 */     catch (InvocationTargetException ex) {
/*      */       
/* 2109 */       failed = true;
/*      */     } 
/*      */     
/* 2112 */     if (failed)
/*      */     {
/* 2114 */       showError("No browser available", "Could not open a browser automatically on this system.", "The URL I wanted to show you was:  " + url);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void updateMidiMenus() {
/* 2127 */     if (midiStatus.isEnabled()) {
/*      */       
/* 2129 */       mntmMidi.setEnabled(true);
/*      */ 
/*      */       
/* 2132 */       MenuItem[] profitems = menuMIDIProfiles.getItems();
/* 2133 */       String[] profiles = midiStatus.getProfiles().<String>toArray(new String[0]);
/*      */       
/* 2135 */       boolean profok = false;
/*      */       
/* 2137 */       if (profitems.length == profiles.length) {
/*      */         
/* 2139 */         profok = true;
/*      */         
/* 2141 */         for (int i = 0; i < profitems.length; i++) {
/*      */           
/* 2143 */           if (profitems[i].getText().equals(midiStatus.getProfile(profiles[i]).getDesc())) {
/*      */             
/* 2145 */             profitems[i].setSelection(profiles[i].equals(midiStatus.getCurrentProfile()));
/*      */           }
/*      */           else {
/*      */             
/* 2149 */             profok = false;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 2154 */       if (!profok) {
/*      */ 
/*      */         
/* 2157 */         menuMIDIProfiles.dispose();
/* 2158 */         menuMIDIProfiles = new Menu(mntmSetProfile);
/* 2159 */         mntmSetProfile.setMenu(menuMIDIProfiles);
/*      */ 
/*      */         
/* 2162 */         Iterator<String> itr = midiStatus.getProfiles().iterator();
/*      */         
/* 2164 */         while (itr.hasNext()) {
/*      */           
/* 2166 */           final String key = itr.next();
/* 2167 */           MenuItem tmp = new MenuItem(menuMIDIProfiles, 32);
/* 2168 */           tmp.setText(midiStatus.getProfile(key).getDesc());
/*      */           
/* 2170 */           tmp.setSelection(key.equals(midiStatus.getCurrentProfile()));
/* 2171 */           tmp.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */               {
/*      */                 public void widgetSelected(SelectionEvent e)
/*      */                 {
/* 2175 */                   MainWin.sendCommand("dw midi synth profile " + key);
/*      */                 }
/*      */               });
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2183 */       MenuItem[] outitems = menuMIDIOutputs.getItems();
/* 2184 */       String[] outs = midiStatus.getDevices().<String>toArray(new String[0]);
/*      */       
/* 2186 */       boolean outok = false;
/*      */       
/* 2188 */       if (outitems.length == outs.length) {
/*      */         
/* 2190 */         outok = true;
/*      */         
/* 2192 */         for (int i = 0; i < outitems.length; i++) {
/*      */           
/* 2194 */           if (outitems[i].getText().equals(outs[i])) {
/*      */             
/* 2196 */             outitems[i].setSelection(outs[i].equals(midiStatus.getCurrentDevice()));
/*      */           }
/*      */           else {
/*      */             
/* 2200 */             outok = false;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 2205 */       if (!outok) {
/*      */ 
/*      */         
/* 2208 */         menuMIDIOutputs.dispose();
/* 2209 */         menuMIDIOutputs = new Menu(mntmSetOutput);
/* 2210 */         mntmSetOutput.setMenu(menuMIDIOutputs);
/*      */         
/* 2212 */         Iterator<String> itr = midiStatus.getDevices().iterator();
/*      */         
/* 2214 */         while (itr.hasNext()) {
/*      */           
/* 2216 */           final String key = itr.next();
/* 2217 */           MenuItem tmp = new MenuItem(menuMIDIOutputs, 32);
/* 2218 */           tmp.setText(key);
/*      */ 
/*      */           
/* 2221 */           tmp.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */               {
/*      */                 
/*      */                 public void widgetSelected(SelectionEvent e)
/*      */                 {
/* 2226 */                   MainWin.sendCommand("dw midi output " + MainWin.midiStatus.getDevice(key).getDevnum());
/*      */                 }
/*      */               });
/*      */ 
/*      */           
/* 2231 */           tmp.setSelection(key.equals(midiStatus.getCurrentDevice()));
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2237 */       mntmLockInstruments.setSelection(midiStatus.isVoiceLock());
/*      */     }
/*      */     else {
/*      */       
/* 2241 */       mntmMidi.setEnabled(false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void addCommandToHistory(String cmd) {
/* 2250 */     List<String> cmdhist = config.getList("CmdHistory", null);
/*      */     
/* 2252 */     if (config.getInt("CmdHistorySize", 20) > 0) {
/*      */       
/* 2254 */       if (cmdhist == null) {
/*      */ 
/*      */         
/* 2257 */         config.addProperty("CmdHistory", cmd);
/* 2258 */         cmdhist = config.getList("CmdHistory", null);
/*      */       } 
/*      */ 
/*      */       
/* 2262 */       if (cmdhist.size() >= config.getInt("CmdHistorySize", 20))
/*      */       {
/* 2264 */         cmdhist.remove(0);
/*      */       }
/*      */       
/* 2267 */       cmdhist.add(cmd);
/* 2268 */       config.setProperty("CmdHistory", cmdhist);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void refreshDiskTable() {
/* 2285 */     table.setRedraw(false);
/*      */     
/* 2287 */     for (int i = 0; i < 256; i++) {
/*      */       
/* 2289 */       clearDiskTableEntry(i);
/*      */       
/* 2291 */       if (disks[i] != null && disks[i].isLoaded()) {
/*      */         
/* 2293 */         setDiskTableEntryFile(i, disks[i].getPath());
/*      */         
/* 2295 */         Iterator<String> itr = disks[i].getParams();
/*      */         
/* 2297 */         while (itr.hasNext()) {
/*      */           
/* 2299 */           String key = itr.next();
/* 2300 */           setDiskTableEntry(i, key, disks[i].getParam(key).toString());
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2307 */     if (currentDisk >= 0)
/*      */     {
/* 2309 */       table.setSelection(currentDisk);
/*      */     }
/*      */     
/* 2312 */     table.setRedraw(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setDiskTableEntry(int disk, String key, String val) {
/* 2321 */     int col = getTPIndex(key);
/*      */     
/* 2323 */     if (col > -1) {
/* 2324 */       table.getItem(disk).setText(col, val);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static void quickURLInDisk(int diskno) {
/* 2330 */     quickURLInDisk(shell, diskno);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void quickURLInDisk(final Shell theshell, final int diskno) {
/* 2335 */     String res = null;
/*      */     
/* 2337 */     URLInputWin urlwin = new URLInputWin(theshell, diskno);
/* 2338 */     res = urlwin.open();
/*      */ 
/*      */     
/* 2341 */     if (res != null && !res.equals("")) {
/*      */       
/* 2343 */       final List<String> cmds = new ArrayList<String>();
/* 2344 */       cmds.add("dw disk insert " + diskno + " " + res);
/* 2345 */       display.asyncExec(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/* 2349 */               SendCommandWin win = new SendCommandWin(theshell, 2144, cmds, "Inserting disk image...", "Please wait while the image is inserted into drive " + diskno + ".");
/* 2350 */               win.open();
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void quickInDisk(int diskno) {
/* 2359 */     quickInDisk(shell, diskno);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void quickInDisk(final Shell theshell, final int diskno) {
/*      */     final String curpath;
/* 2366 */     if (disks[diskno] != null && disks[diskno].isLoaded()) {
/* 2367 */       curpath = disks[diskno].getPath();
/*      */     } else {
/* 2369 */       curpath = "";
/*      */     } 
/*      */     
/* 2372 */     Thread t = new Thread(new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/* 2376 */             final String res = MainWin.getFile(false, false, curpath, "Choose an image for drive " + diskno, "Open", new String[] { MainWin.config.getString("FileSelectionMask", "*.dsk;*.os9;*.vhd"), "*.*" });
/*      */             
/* 2378 */             if (res != null)
/*      */             {
/*      */               
/* 2381 */               MainWin.display.syncExec(new Runnable()
/*      */                   {
/*      */                     
/*      */                     public void run()
/*      */                     {
/* 2386 */                       List<String> cmds = new ArrayList<String>();
/* 2387 */                       cmds.add("dw disk insert " + diskno + " " + res);
/*      */                       
/* 2389 */                       SendCommandWin win = new SendCommandWin(theshell, 2144, cmds, "Inserting disk image...", "Please wait while the image is inserted into drive " + diskno + ".");
/*      */                       
/* 2391 */                       win.open();
/*      */                     }
/*      */                   });
/*      */             }
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2403 */     t.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void writeDiskTo(final int diskno) {
/*      */     final String curpath;
/* 2412 */     if (table.getItem(diskno) != null) {
/* 2413 */       curpath = table.getItem(diskno).getText(2);
/*      */     } else {
/* 2415 */       curpath = "";
/*      */     } 
/* 2417 */     Thread t = new Thread(new Runnable()
/*      */         {
/*      */           public void run() {
/* 2420 */             String res = MainWin.getFile(true, false, curpath, "Write image in drive " + diskno + " to...", "Save", new String[] { MainWin.config.getString("FileSelectionMask", "*.dsk;*.os9;*.vhd"), "*.*" });
/*      */             
/* 2422 */             if (res != null) {
/* 2423 */               MainWin.sendCommand("dw disk write " + diskno + " " + res);
/*      */             }
/*      */           }
/*      */         });
/* 2427 */     t.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getFile(boolean save, boolean dir, String startpath, String title, String buttontext) {
/* 2435 */     return getFile(save, dir, startpath, title, buttontext, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getFile(boolean save, boolean dir, String startpath, String title, String buttontext, String[] fileext) {
/* 2442 */     if (config.getBoolean("UseRemoteFilebrowser", false)) {
/*      */ 
/*      */ 
/*      */       
/* 2446 */       RemoteFileBrowser rfb = new RemoteFileBrowser(save, dir, startpath, title, buttontext, fileext);
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 2451 */         SwingUtilities.invokeAndWait(rfb);
/*      */       }
/* 2453 */       catch (InterruptedException e) {
/*      */         
/* 2455 */         showError("Error in file browser", e.getMessage(), UIUtils.getStackTrace(e), true);
/*      */       }
/* 2457 */       catch (InvocationTargetException e) {
/*      */         
/* 2459 */         showError("Error in file browser", e.getMessage(), UIUtils.getStackTrace(e), true);
/*      */       } 
/*      */ 
/*      */       
/* 2463 */       return rfb.getSelected();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2469 */     LocalFileBrowser lfb = new LocalFileBrowser(save, dir, startpath, title, buttontext, fileext);
/* 2470 */     display.syncExec(lfb);
/*      */     
/* 2472 */     return lfb.getSelected();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void sendCommand(String cmd) {
/* 2482 */     sendCommand(cmd, true);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static void sendCommand(String cmd, int tid) {
/* 2487 */     sendCommand(cmd, tid, true);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static void sendCommand(String cmd, boolean markComplete) {
/* 2492 */     int tid = taskman.addTask(cmd);
/* 2493 */     sendCommand(cmd, tid, markComplete);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void sendCommand(final String cmd, final int tid, final boolean markComplete) {
/* 2499 */     Thread cmdT = new Thread(new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/* 2503 */             if (cmd.startsWith("/")) {
/*      */ 
/*      */               
/* 2506 */               MainWin.processClientCmd(cmd, tid, markComplete);
/*      */             }
/*      */             else {
/*      */               
/* 2510 */               if (markComplete) {
/* 2511 */                 MainWin.taskman.updateTask(tid, 0, "Connecting to server...");
/*      */               }
/* 2513 */               Connection connection = new Connection(MainWin.host, MainWin.port, MainWin.instance);
/*      */ 
/*      */               
/*      */               try {
/* 2517 */                 connection.Connect();
/*      */                 
/* 2519 */                 if (markComplete) {
/* 2520 */                   MainWin.taskman.updateTask(tid, 0, "Sending command: " + cmd);
/*      */                 }
/* 2522 */                 connection.sendCommand(tid, cmd, MainWin.instance, markComplete);
/* 2523 */                 connection.close();
/*      */               
/*      */               }
/* 2526 */               catch (UnknownHostException e) {
/*      */                 
/* 2528 */                 MainWin.taskman.updateTask(tid, 2, e.getMessage() + " You may have a DNS problem, or the server hostname may not be specified correctly.");
/*      */               }
/* 2530 */               catch (IOException e1) {
/*      */ 
/*      */                 
/* 2533 */                 MainWin.taskman.updateTask(tid, 2, e1.getMessage() + " You may have a connectivity problem, or the server may not be running.");
/*      */               
/*      */               }
/* 2536 */               catch (DWUIOperationFailedException e2) {
/*      */                 
/* 2538 */                 MainWin.taskman.updateTask(tid, 2, e2.getMessage());
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/* 2544 */     cmdT.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void processClientCmd(String cmd) {
/* 2555 */     int tid = taskman.addTask(cmd);
/*      */     
/* 2557 */     processClientCmd(cmd, tid, true);
/*      */   }
/*      */   
/*      */   public static void processClientCmd(String cmd, final int tid, boolean markComplete) {
/*      */     final int statint;
/* 2562 */     taskman.updateTask(tid, 0, null);
/*      */ 
/*      */ 
/*      */     
/* 2566 */     if (markComplete) {
/* 2567 */       statint = 1;
/*      */     } else {
/* 2569 */       statint = 0;
/*      */     } 
/*      */     
/* 2572 */     if (cmd.equals("/fonts")) {
/*      */       
/* 2574 */       display.asyncExec(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/* 2578 */               MainWin.taskman.updateTask(tid, 1, UIUtils.listFonts());
/*      */             }
/*      */           });
/*      */     }
/* 2582 */     else if (cmd.equals("/splash")) {
/*      */       
/* 2584 */       taskman.updateTask(tid, 1, "4.0.9c (03/25/2012)");
/*      */       
/* 2586 */       display.asyncExec(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/* 2590 */               MainWin.doSplashTimers(tid, true);
/*      */             }
/*      */           });
/*      */     }
/* 2594 */     else if (!cmd.equals("/wizard")) {
/*      */ 
/*      */ 
/*      */       
/* 2598 */       if (cmd.equals("/mem")) {
/*      */         
/* 2600 */         display.asyncExec(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/* 2604 */                 String txt = "Max  : " + (Runtime.getRuntime().maxMemory() / 1024L) + System.getProperty("line.separator") + "Total: " + (Runtime.getRuntime().totalMemory() / 1024L) + System.getProperty("line.separator") + "Free : " + (Runtime.getRuntime().freeMemory() / 1024L);
/*      */ 
/*      */ 
/*      */                 
/* 2608 */                 long realfree = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
/* 2609 */                 txt = txt + System.getProperty("line.separator") + "TFree: " + (realfree / 1024L);
/* 2610 */                 txt = txt + "     " + (Double.valueOf(realfree).doubleValue() / Double.valueOf(Runtime.getRuntime().maxMemory()).doubleValue() * 100.0D) + "%";
/*      */                 
/* 2612 */                 MainWin.taskman.updateTask(tid, statint, txt);
/*      */               }
/*      */             });
/*      */       
/*      */       }
/* 2617 */       else if (cmd.equals("/dumperr")) {
/*      */         
/* 2619 */         display.asyncExec(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/* 2623 */                 MainWin.taskman.updateTask(tid, 1, MainWin.errorHelpCache.dump());
/*      */               }
/*      */             });
/*      */       }
/* 2627 */       else if (cmd.equals("/midistatus")) {
/*      */         
/* 2629 */         display.asyncExec(new Runnable()
/*      */             {
/*      */               public void run()
/*      */               {
/* 2633 */                 MainWin.taskman.updateTask(tid, statint, UIUtils.dumpMIDIStatus(MainWin.midiStatus));
/*      */               }
/*      */             });
/*      */       }
/*      */       else {
/*      */         
/* 2639 */         taskman.updateTask(tid, 2, "Unknown client command");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getHost() {
/* 2651 */     return host;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int getPort() {
/* 2656 */     return port;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int getInstance() {
/* 2661 */     return instance;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void setHost(String h) {
/* 2666 */     host = h;
/* 2667 */     config.setProperty("LastHost", h);
/* 2668 */     updateTitlebar();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setPort(String p) {
/*      */     try {
/* 2675 */       port = Integer.parseInt(p);
/* 2676 */       config.setProperty("LastPort", p);
/* 2677 */       updateTitlebar();
/*      */     }
/* 2679 */     catch (NumberFormatException e) {
/*      */       
/* 2681 */       showError("Invalid port number", "'" + p + "' is not a valid port number.", "Valid port numbers are 1-65535.");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addDiskFileToHistory(String filename) {
/* 2690 */     List<String> diskhist = config.getList("DiskHistory", null);
/*      */ 
/*      */     
/* 2693 */     if (config.getInt("DiskHistorySize", 20) > 0) {
/*      */       
/* 2695 */       if (diskhist == null) {
/*      */ 
/*      */         
/* 2698 */         config.addProperty("DiskHistory", filename);
/* 2699 */         diskhist = config.getList("DiskHistory", null);
/*      */       }
/*      */       else {
/*      */         
/* 2703 */         diskhist.remove(filename);
/*      */         
/* 2705 */         if (diskhist.size() >= config.getInt("DiskHistorySize", 20))
/*      */         {
/* 2707 */           diskhist.remove(0);
/*      */         }
/*      */         
/* 2710 */         diskhist.add(filename);
/*      */       } 
/*      */       
/* 2713 */       config.setProperty("DiskHistory", diskhist);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> getDiskHistory() {
/* 2723 */     return config.getList("DiskHistory", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> getServerHistory() {
/* 2732 */     return config.getList("ServerHistory", null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addServerToHistory(String server) {
/* 2738 */     List<String> shist = config.getList("ServerHistory", null);
/*      */     
/* 2740 */     if (shist == null) {
/*      */       
/* 2742 */       if (config.getInt("ServerHistorySize", 20) > 0)
/*      */       {
/* 2744 */         config.addProperty("ServerHistory", server);
/*      */       }
/*      */     }
/* 2747 */     else if (!shist.contains(server)) {
/*      */       
/* 2749 */       if (shist.size() >= config.getInt("ServerHistorySize", 20))
/*      */       {
/* 2751 */         shist.remove(0);
/*      */       }
/*      */       
/* 2754 */       shist.add(server);
/* 2755 */       config.setProperty("ServerHistory", shist);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setInstance(int inst) {
/* 2763 */     instance = inst;
/* 2764 */     config.setProperty("LastInstance", Integer.valueOf(inst));
/* 2765 */     updateTitlebar();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HierarchicalConfiguration getInstanceConfig() {
/* 2775 */     if (dwconfig != null) {
/*      */ 
/*      */       
/* 2778 */       List<HierarchicalConfiguration> handlerconfs = dwconfig.configurationsAt("instance");
/*      */       
/* 2780 */       if (getInstance() < handlerconfs.size())
/*      */       {
/* 2782 */         return handlerconfs.get(getInstance());
/*      */       }
/*      */     } 
/* 2785 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setConStatusConnect() {
/* 2793 */     if (isReady())
/*      */     {
/*      */       
/* 2796 */       synchronized (connected) {
/*      */         
/* 2798 */         if (!connected.booleanValue())
/*      */         {
/* 2800 */           display.syncExec(new Runnable()
/*      */               {
/*      */                 public void run()
/*      */                 {
/* 2804 */                   MainWin.connected = Boolean.valueOf(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/* 2811 */                   MainWin.setItemsConnectionEnabled(true);
/*      */                 }
/*      */               });
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void setItemsConnectionEnabled(boolean en) {
/* 2829 */     MenuItem[] items = menu_tools.getItems();
/*      */     int i;
/* 2831 */     for (i = 0; i < items.length; i++)
/*      */     {
/* 2833 */       items[i].setEnabled(en);
/*      */     }
/*      */     
/* 2836 */     items = menu_config.getItems();
/*      */     
/* 2838 */     for (i = 0; i < items.length; i++)
/*      */     {
/* 2840 */       items[i].setEnabled(en);
/*      */     }
/*      */ 
/*      */     
/* 2844 */     mntmChooseInstance.setEnabled(en);
/*      */     
/* 2846 */     table.setEnabled(en);
/* 2847 */     txtYouCanEnter.setEnabled(en);
/*      */ 
/*      */     
/* 2850 */     mntmInitialConfig.setEnabled(true);
/* 2851 */     mntmUserInterface.setEnabled(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setConStatusError() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setConStatusTrying() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void doDisplayAsync(Runnable runnable) {
/* 2919 */     display.asyncExec(runnable);
/*      */   }
/*      */   
/*      */   protected CTabFolder getTabFolderOutput() {
/* 2923 */     return tabFolderOutput;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setServerConfig(HierarchicalConfiguration serverConfig) {
/* 2929 */     dwconfig = serverConfig;
/*      */     
/* 2931 */     if (getInstanceConfig().getString("DeviceType", "").equals("dummy"))
/*      */     {
/* 2933 */       processClientCmd("/wizard");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void restartServerConn() {
/* 2940 */     if (syncObj != null)
/*      */     {
/* 2942 */       syncObj.die();
/*      */     }
/*      */ 
/*      */     
/* 2946 */     syncObj = new SyncThread();
/* 2947 */     syncThread = new Thread(syncObj);
/* 2948 */     syncThread.setDaemon(true);
/* 2949 */     syncThread.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void applyConfig() {
/* 2957 */     display.asyncExec(new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*      */           public void run()
/*      */           {
/* 2964 */             MainWin.mntmHdbdosTranslation.setSelection(MainWin.getInstanceConfig().getBoolean("HDBDOSMode", false));
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void applyDisks() {
/* 2975 */     display.asyncExec(new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/* 2979 */             MainWin.refreshDiskTable();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DiskDef getCurrentDisk() {
/* 2990 */     return disks[currentDisk];
/*      */   }
/*      */ 
/*      */   
/*      */   public static int getCurrentDiskNo() {
/* 2995 */     return currentDisk;
/*      */   }
/*      */ 
/*      */   
/*      */   public static DiskDef getDiskDef(int dno) {
/* 3000 */     return disks[dno];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setMidiStatus(MIDIStatus serverMidiStatus) {
/* 3010 */     midiStatus = serverMidiStatus;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void applyMIDIStatus() {
/* 3016 */     display.asyncExec(new Runnable()
/*      */         {
/*      */           
/*      */           public void run()
/*      */           {
/* 3021 */             MainWin.updateMidiMenus();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MIDIStatus getMidiStatus() {
/* 3030 */     return midiStatus;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setDisks(DiskDef[] serverDisks) {
/* 3036 */     disks = new DiskDef[serverDisks.length];
/* 3037 */     System.arraycopy(serverDisks, 0, disks, 0, serverDisks.length);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getUIText() {
/* 3043 */     String res = "";
/* 3044 */     for (UITask t : taskman.getTasks()) {
/*      */       
/* 3046 */       res = res + "tid:" + t.getTaskID() + ", stat:" + t.getStatus() + ", txt:" + t.getText() + ", " + t.getCommand();
/* 3047 */       res = res + "\r\n";
/*      */     } 
/*      */ 
/*      */     
/* 3051 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitDiskEvent(int disk, String key, String val) {
/* 3067 */     if (disks[disk] == null) {
/*      */       
/* 3069 */       debug("NULL disk in submitevent: " + disk);
/* 3070 */       disks[disk] = new DiskDef(disk);
/*      */     } 
/*      */     
/* 3073 */     disks[disk].setParam(key, val);
/*      */     
/* 3075 */     if (key.startsWith("*"))
/*      */     {
/* 3077 */       if (key.equals("*insert")) {
/*      */         
/* 3079 */         clearDiskTableEntry(disk);
/*      */         
/* 3081 */         setDiskTableEntryFile(disk, val);
/*      */       
/*      */       }
/* 3084 */       else if (key.equals("*eject")) {
/*      */         
/* 3086 */         clearDiskTableEntry(disk);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3091 */     diskTableUpdater.addUpdate(disk, key, val);
/*      */     
/* 3093 */     if (key.equals("_reads") && !val.equals("0")) {
/*      */       
/* 3095 */       diskTableUpdater.addUpdate(disk, "LED", diskLEDgreen);
/* 3096 */       driveactivity = Boolean.valueOf(true);
/*      */     }
/* 3098 */     else if (key.equals("_writes") && !val.equals("0")) {
/*      */       
/* 3100 */       diskTableUpdater.addUpdate(disk, "LED", diskLEDred);
/* 3101 */       driveactivity = Boolean.valueOf(true);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void setDiskTableEntryFile(final int disk, final String val) {
/* 3110 */     display.syncExec(new Runnable()
/*      */         {
/*      */           
/*      */           public void run()
/*      */           {
/* 3115 */             int filecol = MainWin.getTPIndex("File");
/* 3116 */             if (filecol > -1) {
/* 3117 */               MainWin.table.getItem(disk).setText(filecol, UIUtils.getFilenameFromURI(val));
/*      */             }
/*      */             
/* 3120 */             int loccol = MainWin.getTPIndex("Location");
/* 3121 */             if (loccol > -1) {
/* 3122 */               MainWin.table.getItem(disk).setText(loccol, UIUtils.getLocationFromURI(val));
/*      */             }
/* 3124 */             int ledcol = MainWin.getTPIndex("LED");
/* 3125 */             if (ledcol > -1) {
/* 3126 */               MainWin.table.getItem(disk).setImage(ledcol, MainWin.diskLEDdark);
/*      */             }
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void clearDiskTableEntry(final int disk) {
/* 3139 */     display.syncExec(new Runnable()
/*      */         {
/*      */           public void run() {
/* 3142 */             synchronized (MainWin.table) {
/*      */               
/* 3144 */               int drivecol = MainWin.getTPIndex("Drive");
/* 3145 */               int ledcol = MainWin.getTPIndex("LED");
/*      */               
/* 3147 */               String[] txt = new String[MainWin.table.getColumnCount()];
/* 3148 */               for (int i = 0; i < txt.length; i++) {
/* 3149 */                 txt[i] = "";
/*      */               }
/*      */               
/* 3152 */               while (MainWin.table.getItemCount() < disk + 1) {
/*      */                 
/* 3154 */                 TableItem item = new TableItem(MainWin.table, 0);
/*      */ 
/*      */                 
/* 3157 */                 if (drivecol > -1) {
/* 3158 */                   item.setText(drivecol, disk + "");
/*      */                 }
/*      */               } 
/*      */               
/* 3162 */               MainWin.table.getItem(disk).setText(txt);
/*      */ 
/*      */               
/* 3165 */               if (drivecol > -1) {
/* 3166 */                 MainWin.table.getItem(disk).setText(drivecol, disk + "");
/*      */               }
/*      */               
/* 3169 */               if (ledcol > -1) {
/* 3170 */                 MainWin.table.getItem(disk).setImage(ledcol, null);
/*      */               }
/*      */             } 
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getServerText() {
/* 3185 */     String res = "";
/*      */     
/* 3187 */     for (LogItem e : logItems)
/*      */     {
/* 3189 */       res = res + e.getShortTimestamp() + "," + e.getLevel() + "," + e.getShortSource() + "," + e.getThread() + "," + e.getMessage() + "\r\n";
/*      */     }
/*      */     
/* 3192 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isReady() {
/* 3201 */     return ready;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void updateDiskTableItem(final int item, final String key, final Object object) {
/* 3208 */     if (disks[item].isLoaded())
/*      */     {
/* 3210 */       display.syncExec(new Runnable()
/*      */           {
/*      */             public void run() {
/* 3213 */               int keycol = MainWin.getTPIndex(key);
/*      */               
/* 3215 */               if (keycol > -1)
/*      */               {
/* 3217 */                 if (object.getClass().getSimpleName().equals("String")) {
/* 3218 */                   MainWin.table.getItem(item).setText(keycol, object.toString());
/* 3219 */                 } else if (object.getClass().getSimpleName().equals("Image")) {
/* 3220 */                   MainWin.table.getItem(item).setImage(keycol, (Image)object);
/*      */                 } 
/*      */               }
/*      */             }
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Display getDisplay() {
/* 3231 */     return display;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addDiskTableColumn(String key) {
/* 3240 */     synchronized (table) {
/*      */       
/* 3242 */       if (getTPIndex(key) < 0) {
/*      */         
/* 3244 */         config.addProperty("DiskTable_Items", key);
/*      */         
/* 3246 */         while (table.getColumnCount() > 0)
/*      */         {
/* 3248 */           table.getColumn(0).dispose();
/*      */         }
/*      */         
/* 3251 */         createDiskTableColumns();
/*      */         
/* 3253 */         refreshDiskTable();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void removeDiskTableColumn(String key) {
/* 3262 */     synchronized (table) {
/*      */       
/* 3264 */       if (getTPIndex(key) > -1) {
/*      */         
/* 3266 */         config.clearProperty("DiskTable_Items(" + getTPIndex(key) + ")");
/*      */         
/* 3268 */         while (table.getColumnCount() > 0)
/*      */         {
/* 3270 */           table.getColumn(0).dispose();
/*      */         }
/*      */         
/* 3273 */         createDiskTableColumns();
/*      */         
/* 3275 */         refreshDiskTable();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitServerConfigEvent(String key, Object val) {
/* 3285 */     if (val == null) {
/*      */       
/* 3287 */       dwconfig.clearProperty(key);
/*      */     }
/*      */     else {
/*      */       
/* 3291 */       dwconfig.setProperty(key, val);
/*      */     } 
/*      */     
/* 3294 */     if (serverconfigwin != null && !serverconfigwin.shlServerConfiguration.isDisposed())
/*      */     {
/* 3296 */       serverconfigwin.submitEvent(key, val);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void showError(String t, String s, String d) {
/* 3305 */     showError(new DWError(t, s, d, true));
/*      */   }
/*      */ 
/*      */   
/*      */   public static void showError(String t, String s, String d, boolean gui) {
/* 3310 */     showError(new DWError(t, s, d, !gui));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void showError(final DWError dwerror) {
/* 3317 */     if (dwerror.isGui()) {
/*      */       
/* 3319 */       display.asyncExec(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/* 3323 */               ErrorWin ew = new ErrorWin(MainWin.shell, 2144, dwerror);
/* 3324 */               ew.open();
/*      */             }
/*      */           });
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 3331 */       int tid = taskman.addTask("Error");
/*      */       
/* 3333 */       taskman.updateTask(tid, 2, dwerror.getTextError());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addToServerLog(final LogItem litem) {
/* 3340 */     if (!lowMem || litem.isImportant())
/*      */     {
/* 3342 */       display.asyncExec(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               int lisize;
/*      */               
/* 3348 */               synchronized (MainWin.logItems) {
/*      */                 
/* 3350 */                 MainWin.logItems.add(litem);
/* 3351 */                 lisize = MainWin.logItems.size();
/*      */               } 
/*      */               
/* 3354 */               MainWin.logTable.setItemCount(lisize);
/*      */               
/* 3356 */               if (MainWin.logscroll) {
/* 3357 */                 MainWin.logTable.setTopIndex(lisize);
/*      */               }
/*      */               
/* 3360 */               if (MainWin.tabFolderOutput.getSelectionIndex() != 1)
/*      */               {
/* 3362 */                 if (UIUtils.getLogLevelVal(litem.getLevel()) > MainWin.logNoticeLevel) {
/*      */                   
/* 3364 */                   MainWin.tabFolderOutput.getItems()[1].setImage(SWTResourceManager.getImage(MainWin.class, "/logging/" + litem.getLevel().toLowerCase() + ".png"));
/* 3365 */                   MainWin.logNoticeLevel = UIUtils.getLogLevelVal(litem.getLevel());
/*      */                 } 
/*      */               }
/*      */             }
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitServerStatusEvent(ServerStatusItem ssbuf) {
/* 3381 */     synchronized (serverStatus) {
/*      */       
/* 3383 */       serverStatus = ssbuf;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3389 */       long realfree = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
/*      */       
/* 3391 */       if (!lowMem) {
/*      */         
/* 3393 */         if (realfree < 4194304.0D)
/*      */         {
/*      */           
/* 3396 */           lowMemLogItem.setTimestamp(System.currentTimeMillis());
/* 3397 */           addToServerLog(lowMemLogItem);
/* 3398 */           if (!taskman.hasTask(lowMemWarningTid)) {
/* 3399 */             lowMemWarningTid = taskman.addTask("Free memory status");
/*      */           }
/* 3401 */           taskman.updateTask(lowMemWarningTid, 2, "Due to extremely low free memory, some functions have been disabled.  This includes mounting new disk images.");
/*      */ 
/*      */           
/* 3404 */           lowMem = true;
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 3409 */       else if (realfree > 8388608.0D) {
/*      */         
/* 3411 */         LogItem li = new LogItem();
/* 3412 */         li.setTimestamp(System.currentTimeMillis());
/* 3413 */         li.setLevel("INFO");
/* 3414 */         li.setSource("UI");
/* 3415 */         li.setThread(Thread.currentThread().getName());
/* 3416 */         li.setMessage("Due to increase in free memory, log display has been enabled.");
/* 3417 */         lowMem = false;
/* 3418 */         addToServerLog(li);
/*      */         
/* 3420 */         if (!taskman.hasTask(lowMemWarningTid)) {
/* 3421 */           lowMemWarningTid = taskman.addTask("Free memory status");
/*      */         }
/* 3423 */         taskman.updateTask(lowMemWarningTid, 1, "Free memory has increased, all functions are now enabled.");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3430 */       if (ssbuf.getMagic() != servermagic) {
/*      */ 
/*      */         
/* 3433 */         if (config.getBoolean("LocalServer", true) && !noServer)
/*      */         {
/*      */           
/* 3436 */           if (host.equals("127.0.0.1"))
/*      */           {
/* 3438 */             showError("We may have a problem...", "The server we've just connected to is not the server we just started.", "This usually happens because more than one copy of DriveWire is running.\n\nThe first instance is listening on the UI TCP port, so the second server cannot start it's UI thread.  Meanwhile, the client part of the second DriveWire then opens a connection to the UI port and becomes attached to the server of the *first* DriveWire.\n\nWhat now?\n\nEasy answer: Close this copy of DriveWire and go back to using the first one\n\nIf you actually wanted a second UI connected to the first server, carry on, you've got it.  I won't bother you again.  If that isn't what you wanted but you can't find another copy of DriveWire running, check your task manager/process list, it is likely to be listed there, possibly as simply 'java' or 'java.exe'.", false);
/*      */           }
/*      */         }
/*      */ 
/*      */         
/* 3443 */         servermagic = ssbuf.getMagic();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setDWCmdText(String cmd) {
/* 3451 */     txtYouCanEnter.setText(cmd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void selectUIPage() {
/* 3458 */     if (tabFolderOutput.getSelectionIndex() != 0)
/*      */     {
/* 3460 */       tabFolderOutput.setSelection(0);
/*      */     }
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/MainWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */