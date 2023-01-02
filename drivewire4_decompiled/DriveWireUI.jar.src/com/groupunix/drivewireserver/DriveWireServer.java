/*      */ package com.groupunix.drivewireserver;
/*      */ 
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDiskLazyWriter;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWPlatformUnknownException;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.MCXProtocolHandler;
/*      */ import gnu.io.CommPort;
/*      */ import gnu.io.CommPortIdentifier;
/*      */ import gnu.io.SerialPort;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.List;
/*      */ import java.util.Vector;
/*      */ import org.apache.commons.cli.CommandLine;
/*      */ import org.apache.commons.cli.GnuParser;
/*      */ import org.apache.commons.cli.HelpFormatter;
/*      */ import org.apache.commons.cli.Options;
/*      */ import org.apache.commons.cli.ParseException;
/*      */ import org.apache.commons.configuration.ConfigurationException;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.commons.configuration.XMLConfiguration;
/*      */ import org.apache.log4j.Appender;
/*      */ import org.apache.log4j.ConsoleAppender;
/*      */ import org.apache.log4j.FileAppender;
/*      */ import org.apache.log4j.Layout;
/*      */ import org.apache.log4j.Level;
/*      */ import org.apache.log4j.Logger;
/*      */ import org.apache.log4j.PatternLayout;
/*      */ import org.apache.log4j.lf5.LF5Appender;
/*      */ import org.apache.log4j.spi.LoggingEvent;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DriveWireServer
/*      */ {
/*      */   public static final String DWServerVersion = "4.0.8f";
/*      */   public static final String DWServerVersionDate = "03/25/2012";
/*   53 */   private static Logger logger = Logger.getLogger(DriveWireServer.class);
/*      */   private static ConsoleAppender consoleAppender;
/*      */   private static DWLogAppender dwAppender;
/*      */   private static FileAppender fileAppender;
/*   57 */   private static PatternLayout logLayout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %m%n");
/*      */   
/*      */   public static XMLConfiguration serverconfig;
/*      */   
/*   61 */   public static int configserial = 0;
/*      */   
/*   63 */   private static Vector<Thread> dwProtoHandlerThreads = new Vector<Thread>();
/*   64 */   private static Vector<DWProtocol> dwProtoHandlers = new Vector<DWProtocol>();
/*      */   
/*      */   private static Thread lazyWriterT;
/*      */   
/*      */   private static DWUIThread uiObj;
/*      */   
/*      */   private static Thread uiT;
/*      */   private static boolean wanttodie = false;
/*   72 */   private static String configfile = "config.xml";
/*      */   
/*      */   private static boolean ready = false;
/*      */   private static boolean useLF5 = false;
/*      */   private static LF5Appender lf5appender;
/*      */   private static boolean useBackup = false;
/*      */   private static SerialPort testSerialPort;
/*   79 */   private static DWEvent statusEvent = new DWEvent((byte)64);
/*   80 */   private static long lastMemoryUpdate = 0L;
/*   81 */   private static ArrayList<DWEvent> logcache = new ArrayList<DWEvent>();
/*      */   private static boolean useDebug = false;
/*   83 */   private static long magic = System.currentTimeMillis();
/*   84 */   private static DWEvent evt = new DWEvent((byte)64);
/*      */   
/*      */   private static DWEvent fevt;
/*      */   
/*      */   private static boolean noMIDI = false;
/*      */   
/*      */   private static boolean noMount = false;
/*      */   private static boolean noUI = false;
/*      */   private static boolean noServer = false;
/*      */   private static boolean configFreeze = false;
/*      */   private static boolean restart_logging = false;
/*      */   private static boolean restart_ui = false;
/*      */   
/*      */   public static void main(String[] args) throws ConfigurationException {
/*   98 */     Thread.setDefaultUncaughtExceptionHandler(new DWExceptionHandler());
/*      */     
/*  100 */     init(args);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  106 */     logger.debug("ready...");
/*      */     
/*  108 */     ready = true;
/*  109 */     while (!wanttodie) {
/*      */ 
/*      */       
/*      */       try {
/*      */         
/*  114 */         Thread.sleep(serverconfig.getInt("StatusInterval", 1000));
/*      */         
/*  116 */         checkHandlerHealth();
/*      */         
/*  118 */         submitServerStatus();
/*      */       
/*      */       }
/*  121 */       catch (InterruptedException e) {
/*      */         
/*  123 */         logger.debug("I've been interrupted, now I want to die");
/*  124 */         wanttodie = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  129 */     serverShutdown();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void checkHandlerHealth() {
/*  138 */     for (int i = 0; i < dwProtoHandlers.size(); i++) {
/*      */       
/*  140 */       if (dwProtoHandlers.get(i) != null && ((DWProtocol)dwProtoHandlers.get(i)).isReady() && !((DWProtocol)dwProtoHandlers.get(i)).isDying())
/*      */       {
/*      */ 
/*      */         
/*  144 */         if (dwProtoHandlerThreads.get(i) == null) {
/*      */           
/*  146 */           logger.error("Null thread for handler #" + i);
/*      */ 
/*      */         
/*      */         }
/*  150 */         else if (!((Thread)dwProtoHandlerThreads.get(i)).isAlive()) {
/*      */           
/*  152 */           logger.error("Handler #" + i + " has died. RIP.");
/*      */           
/*  154 */           if (((DWProtocol)dwProtoHandlers.get(i)).getConfig().getBoolean("ZombieResurrection", true)) {
/*      */             
/*  156 */             logger.info("Arise chicken! Reanimating handler #" + i + ": " + ((DWProtocol)dwProtoHandlers.get(i)).getConfig().getString("[@name]", "unnamed"));
/*      */ 
/*      */ 
/*      */             
/*  160 */             List<HierarchicalConfiguration> handlerconfs = serverconfig.configurationsAt("instance");
/*      */             
/*  162 */             dwProtoHandlers.set(i, new DWProtocolHandler(i, handlerconfs.get(i)));
/*      */             
/*  164 */             dwProtoHandlerThreads.set(i, new Thread((Runnable)dwProtoHandlers.get(i)));
/*  165 */             ((Thread)dwProtoHandlerThreads.get(i)).start();
/*      */           } 
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
/*      */   private static void submitServerStatus() {
/*  180 */     long ticktime = System.currentTimeMillis();
/*      */     
/*  182 */     if (uiObj != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  188 */       evt.setParam("!", getMagic() + "");
/*  189 */       evt.setParam("0", serverconfig.getInt("StatusInterval", 1000) + "");
/*  190 */       evt.setParam("6", getNumHandlers() + "");
/*  191 */       evt.setParam("7", getNumHandlersAlive() + "");
/*  192 */       evt.setParam("8", DWUtils.getRootThreadGroup().activeCount() + "");
/*  193 */       evt.setParam("9", uiObj.getNumUIClients() + "");
/*      */ 
/*      */       
/*  196 */       evt.setParam("3", getTotalOps() + "");
/*  197 */       evt.setParam("4", getDiskOps() + "");
/*  198 */       evt.setParam("5", getVSerialOps() + "");
/*      */ 
/*      */       
/*  201 */       if (ticktime - lastMemoryUpdate > 5000L) {
/*      */ 
/*      */         
/*  204 */         evt.setParam("1", (Runtime.getRuntime().totalMemory() / 1024L) + "");
/*  205 */         evt.setParam("2", (Runtime.getRuntime().freeMemory() / 1024L) + "");
/*  206 */         lastMemoryUpdate = ticktime;
/*      */       } 
/*      */ 
/*      */       
/*  210 */       fevt = new DWEvent((byte)64);
/*      */       
/*  212 */       for (String key : evt.getParamKeys()) {
/*      */         
/*  214 */         if (!statusEvent.hasParam(key) || !statusEvent.getParam(key).equals(evt.getParam(key))) {
/*      */           
/*  216 */           fevt.setParam(key, evt.getParam(key));
/*  217 */           statusEvent.setParam(key, evt.getParam(key));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  222 */       if (fevt.getParamKeys().size() > 0) {
/*  223 */         uiObj.submitEvent(fevt);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DWEvent getServerStatusEvent() {
/*  232 */     return statusEvent;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static long getTotalOps() {
/*  238 */     long res = 0L;
/*      */     
/*  240 */     for (DWProtocol p : dwProtoHandlers) {
/*      */       
/*  242 */       if (p != null) {
/*  243 */         res += p.getNumOps();
/*      */       }
/*      */     } 
/*  246 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static long getDiskOps() {
/*  252 */     long res = 0L;
/*      */     
/*  254 */     for (DWProtocol p : dwProtoHandlers) {
/*      */       
/*  256 */       if (p != null) {
/*  257 */         res += p.getNumDiskOps();
/*      */       }
/*      */     } 
/*  260 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static long getVSerialOps() {
/*  266 */     long res = 0L;
/*      */     
/*  268 */     for (DWProtocol p : dwProtoHandlers) {
/*      */       
/*  270 */       if (p != null) {
/*  271 */         res += p.getNumVSerialOps();
/*      */       }
/*      */     } 
/*  274 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void init(String[] args) {
/*  282 */     Thread.currentThread().setName("dwserver-" + Thread.currentThread().getId());
/*      */ 
/*      */     
/*  285 */     doCmdLineArgs(args);
/*      */ 
/*      */     
/*  288 */     initLogging();
/*      */     
/*  290 */     logger.info("DriveWire Server v4.0.8f starting");
/*  291 */     logger.debug("Heap max: " + (Runtime.getRuntime().maxMemory() / 1024L / 1024L) + "MB " + " cur: " + (Runtime.getRuntime().totalMemory() / 1024L / 1024L) + "MB");
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  296 */       serverconfig = new XMLConfiguration(configfile);
/*      */ 
/*      */       
/*  299 */       if (useBackup) {
/*  300 */         backupConfig(configfile);
/*      */       }
/*      */     }
/*  303 */     catch (ConfigurationException e1) {
/*      */       
/*  305 */       logger.fatal(e1.getMessage());
/*  306 */       System.exit(-1);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  311 */     applyLoggingSettings();
/*      */ 
/*      */ 
/*      */     
/*  315 */     if (serverconfig.getBoolean("LoadRXTX", true))
/*      */     {
/*  317 */       loadRXTX();
/*      */     }
/*      */ 
/*      */     
/*  321 */     if (serverconfig.getBoolean("UseRXTX", true) && !checkRXTXLoaded()) {
/*      */       
/*  323 */       logger.fatal("UseRXTX is set, but RXTX native libraries could not be loaded");
/*  324 */       logger.fatal("Please see http://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=Installation");
/*  325 */       System.exit(-1);
/*      */     } 
/*      */ 
/*      */     
/*  329 */     serverconfig.addConfigurationListener(new DWServerConfigListener());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  334 */     if (serverconfig.getBoolean("ConfigAutosave", true)) {
/*      */       
/*  336 */       logger.debug("Auto save of configuration is enabled");
/*  337 */       serverconfig.setAutoSave(true);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  343 */     startProtoHandlers();
/*      */ 
/*      */     
/*  346 */     startLazyWriter();
/*      */ 
/*      */     
/*  349 */     applyUISettings();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void startProtoHandlers() {
/*  359 */     List<HierarchicalConfiguration> handlerconfs = serverconfig.configurationsAt("instance");
/*      */     
/*  361 */     dwProtoHandlers.ensureCapacity(handlerconfs.size());
/*  362 */     dwProtoHandlerThreads.ensureCapacity(handlerconfs.size());
/*      */     
/*  364 */     int hno = 0;
/*      */     
/*  366 */     for (HierarchicalConfiguration hconf : handlerconfs) {
/*      */       
/*  368 */       if (hconf.containsKey("Protocol")) {
/*      */         
/*  370 */         if (hconf.getString("Protocol").equals("DriveWire"))
/*      */         {
/*  372 */           dwProtoHandlers.add(new DWProtocolHandler(hno, hconf));
/*      */         }
/*  374 */         else if (hconf.getString("Protocol").equals("MCX"))
/*      */         {
/*  376 */           dwProtoHandlers.add(new MCXProtocolHandler(hno, hconf));
/*      */         }
/*      */         else
/*      */         {
/*  380 */           logger.error("Unknown protocol '" + hconf.getString("Protocol") + "' in handler.");
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  386 */         dwProtoHandlers.add(new DWProtocolHandler(hno, hconf));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  391 */       if (hconf.getBoolean("AutoStart", true)) {
/*      */         
/*  393 */         logger.info("Starting handler #" + hno + ": " + hconf.getString("[@name]", "unnamed") + " (" + ((DWProtocol)dwProtoHandlers.get(hno)).getClass().getSimpleName() + ")");
/*  394 */         dwProtoHandlerThreads.add(new Thread((Runnable)dwProtoHandlers.get(hno)));
/*  395 */         ((Thread)dwProtoHandlerThreads.get(dwProtoHandlerThreads.size() - 1)).start();
/*      */       } 
/*      */       
/*  398 */       hno++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean checkRXTXLoaded() {
/*  409 */     PrintStream ops = System.out;
/*  410 */     PrintStream eps = System.err;
/*      */     
/*  412 */     ByteArrayOutputStream rxtxbaos = new ByteArrayOutputStream();
/*  413 */     ByteArrayOutputStream rxtxbaes = new ByteArrayOutputStream();
/*      */     
/*  415 */     PrintStream rxtxout = new PrintStream(rxtxbaos);
/*  416 */     PrintStream rxtxerr = new PrintStream(rxtxbaes);
/*      */     
/*  418 */     System.setOut(rxtxout);
/*  419 */     System.setErr(rxtxerr);
/*      */     
/*  421 */     boolean res = DWUtils.testClassPath("gnu.io.RXTXCommDriver");
/*      */     
/*  423 */     for (String l : rxtxbaes.toString().trim().split("\n")) {
/*      */       
/*  425 */       if (!l.equals("")) {
/*  426 */         logger.warn(l);
/*      */       }
/*      */     } 
/*  429 */     for (String l : rxtxbaos.toString().trim().split("\n")) {
/*      */ 
/*      */       
/*  432 */       if (!l.equals("WARNING:  RXTX Version mismatch") && !l.equals("")) {
/*  433 */         logger.debug(l);
/*      */       }
/*      */     } 
/*  436 */     System.setOut(ops);
/*  437 */     System.setErr(eps);
/*      */     
/*  439 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void loadRXTX() {
/*      */     try {
/*      */       String rxtxpath;
/*  452 */       if (!serverconfig.getString("LoadRXTXPath", "").equals("")) {
/*      */         
/*  454 */         rxtxpath = serverconfig.getString("LoadRXTXPath");
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  459 */         File curdir = new File(".");
/*  460 */         rxtxpath = curdir.getCanonicalPath();
/*      */ 
/*      */         
/*  463 */         String[] osparts = System.getProperty("os.name").split(" ");
/*      */         
/*  465 */         if (osparts.length < 1)
/*      */         {
/*  467 */           throw new DWPlatformUnknownException("No native dir for os '" + System.getProperty("os.name") + "' arch '" + System.getProperty("os.arch") + "'");
/*      */         }
/*      */         
/*  470 */         rxtxpath = rxtxpath + File.separator + "native" + File.separator + osparts[0] + File.separator + System.getProperty("os.arch");
/*      */       } 
/*      */       
/*  473 */       File testrxtxpath = new File(rxtxpath);
/*  474 */       logger.debug("Using rxtx lib path: " + rxtxpath);
/*      */       
/*  476 */       if (!testrxtxpath.exists())
/*      */       {
/*  478 */         throw new DWPlatformUnknownException("No native dir for os '" + System.getProperty("os.name") + "' arch '" + System.getProperty("os.arch") + "'");
/*      */       }
/*      */ 
/*      */       
/*  482 */       System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + rxtxpath);
/*      */ 
/*      */ 
/*      */       
/*  486 */       Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
/*  487 */       sysPathsField.setAccessible(true);
/*  488 */       sysPathsField.set(null, null);
/*      */     
/*      */     }
/*  491 */     catch (Exception e) {
/*      */       
/*  493 */       logger.fatal(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
/*      */       
/*  495 */       if (useDebug) {
/*      */         
/*  497 */         System.out.println("--------------------------------------------------------------------------------");
/*  498 */         e.printStackTrace();
/*  499 */         System.out.println("--------------------------------------------------------------------------------");
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
/*      */   private static void initLogging() {
/*  513 */     Logger.getRootLogger().removeAllAppenders();
/*  514 */     consoleAppender = new ConsoleAppender((Layout)logLayout);
/*  515 */     Logger.getRootLogger().addAppender((Appender)consoleAppender);
/*      */     
/*  517 */     if (useLF5) {
/*  518 */       Logger.getRootLogger().addAppender((Appender)lf5appender);
/*      */     }
/*      */     
/*  521 */     if (useDebug) {
/*  522 */       Logger.getRootLogger().setLevel(Level.ALL);
/*      */     } else {
/*  524 */       Logger.getRootLogger().setLevel(Level.INFO);
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
/*      */   private static void doCmdLineArgs(String[] args) {
/*  536 */     Options cmdoptions = new Options();
/*      */     
/*  538 */     cmdoptions.addOption("config", true, "configuration file (defaults to config.xml)");
/*  539 */     cmdoptions.addOption("backup", false, "make a backup of config at server start");
/*  540 */     cmdoptions.addOption("help", false, "display command line argument help");
/*  541 */     cmdoptions.addOption("logviewer", false, "open GUI log viewer at server start");
/*  542 */     cmdoptions.addOption("debug", false, "log extra info to console");
/*  543 */     cmdoptions.addOption("nomidi", false, "disable MIDI");
/*  544 */     cmdoptions.addOption("nomount", false, "do not remount disks from last run");
/*  545 */     cmdoptions.addOption("noui", false, "do not start user interface");
/*  546 */     cmdoptions.addOption("noserver", false, "do not start server");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  551 */     GnuParser gnuParser = new GnuParser();
/*      */     
/*      */     try {
/*  554 */       CommandLine line = gnuParser.parse(cmdoptions, args);
/*      */ 
/*      */       
/*  557 */       if (line.hasOption("help")) {
/*      */         
/*  559 */         HelpFormatter formatter = new HelpFormatter();
/*  560 */         formatter.printHelp("java -jar DriveWire.jar [OPTIONS]", cmdoptions);
/*  561 */         System.exit(0);
/*      */       } 
/*      */       
/*  564 */       if (line.hasOption("config"))
/*      */       {
/*  566 */         configfile = line.getOptionValue("config");
/*      */       }
/*      */       
/*  569 */       if (line.hasOption("backup"))
/*      */       {
/*  571 */         useBackup = true;
/*      */       }
/*      */       
/*  574 */       if (line.hasOption("debug"))
/*      */       {
/*  576 */         useDebug = true;
/*      */       }
/*      */       
/*  579 */       if (line.hasOption("logviewer")) {
/*      */         
/*  581 */         useLF5 = true;
/*  582 */         lf5appender = new LF5Appender();
/*  583 */         lf5appender.setName("DriveWire 4 Server Log");
/*      */       } 
/*      */ 
/*      */       
/*  587 */       if (line.hasOption("nomidi"))
/*      */       {
/*  589 */         noMIDI = true;
/*      */       }
/*      */       
/*  592 */       if (line.hasOption("nomount"))
/*      */       {
/*  594 */         noMount = true;
/*      */       }
/*      */       
/*  597 */       if (line.hasOption("noui"))
/*      */       {
/*  599 */         noUI = true;
/*      */       }
/*      */       
/*  602 */       if (line.hasOption("noserver"))
/*      */       {
/*  604 */         noServer = true;
/*      */ 
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  610 */     catch (ParseException exp) {
/*      */       
/*  612 */       System.err.println("Could not parse command line: " + exp.getMessage());
/*  613 */       System.exit(-1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void backupConfig(String cfile) {
/*      */     try {
/*  624 */       DWUtils.copyFile(cfile, cfile + ".bak");
/*  625 */       logger.debug("Backed up config to " + cfile + ".bak");
/*      */     }
/*  627 */     catch (IOException e) {
/*      */       
/*  629 */       logger.error("Could not create config backup: " + e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void serverShutdown() {
/*  638 */     logger.info("server shutting down...");
/*      */     
/*  640 */     if (dwProtoHandlerThreads != null) {
/*      */       
/*  642 */       logger.debug("stopping protocol handler(s)...");
/*      */       
/*  644 */       for (DWProtocol p : dwProtoHandlers) {
/*      */         
/*  646 */         if (p != null)
/*      */         {
/*  648 */           p.shutdown();
/*      */         }
/*      */       } 
/*      */       
/*  652 */       for (Thread t : dwProtoHandlerThreads) {
/*      */         
/*  654 */         if (t.isAlive()) {
/*      */           
/*      */           try {
/*      */             
/*  658 */             t.interrupt();
/*  659 */             t.join();
/*      */           }
/*  661 */           catch (InterruptedException e) {
/*      */             
/*  663 */             logger.warn(e.getMessage());
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  670 */     if (lazyWriterT != null) {
/*      */       
/*  672 */       logger.debug("stopping lazy writer...");
/*      */       
/*  674 */       lazyWriterT.interrupt();
/*      */       
/*      */       try {
/*  677 */         lazyWriterT.join();
/*      */       }
/*  679 */       catch (InterruptedException e) {
/*      */         
/*  681 */         logger.warn(e.getMessage());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  686 */     if (uiObj != null) {
/*      */       
/*  688 */       logger.debug("stopping UI thread...");
/*  689 */       uiObj.die();
/*      */       
/*      */       try {
/*  692 */         uiT.join();
/*      */       }
/*  694 */       catch (InterruptedException e) {
/*      */         
/*  696 */         logger.warn(e.getMessage());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  701 */     logger.info("server shutdown complete");
/*  702 */     logger.removeAllAppenders();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void startLazyWriter() {
/*  710 */     lazyWriterT = new Thread((Runnable)new DWDiskLazyWriter());
/*  711 */     lazyWriterT.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void applyUISettings() {
/*  719 */     if (uiT != null && uiT.isAlive()) {
/*      */       
/*  721 */       uiObj.die();
/*  722 */       uiT.interrupt();
/*      */       
/*      */       try {
/*  725 */         uiT.join();
/*      */       }
/*  727 */       catch (InterruptedException e) {
/*      */         
/*  729 */         logger.warn(e.getMessage());
/*      */       } 
/*      */     } 
/*      */     
/*  733 */     if (serverconfig.getBoolean("UIEnabled", false)) {
/*      */ 
/*      */       
/*  736 */       uiObj = new DWUIThread(serverconfig.getInt("UIPort", 6800));
/*  737 */       uiT = new Thread(uiObj);
/*  738 */       uiT.start();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void applyLoggingSettings() {
/*  749 */     if (!serverconfig.getString("LogFormat", "").equals(""))
/*      */     {
/*  751 */       logLayout = new PatternLayout(serverconfig.getString("LogFormat"));
/*      */     }
/*      */     
/*  754 */     Logger.getRootLogger().removeAllAppenders();
/*      */     
/*  756 */     dwAppender = new DWLogAppender((Layout)logLayout);
/*  757 */     Logger.getRootLogger().addAppender((Appender)dwAppender);
/*      */     
/*  759 */     if (useLF5) {
/*  760 */       Logger.getRootLogger().addAppender((Appender)lf5appender);
/*      */     }
/*  762 */     if (serverconfig.getBoolean("LogToConsole", true) || useDebug) {
/*      */       
/*  764 */       consoleAppender = new ConsoleAppender((Layout)logLayout);
/*  765 */       Logger.getRootLogger().addAppender((Appender)consoleAppender);
/*      */     } 
/*      */ 
/*      */     
/*  769 */     if (serverconfig.getBoolean("LogToFile", false) && serverconfig.containsKey("LogFile")) {
/*      */       
/*      */       try {
/*      */ 
/*      */         
/*  774 */         fileAppender = new FileAppender((Layout)logLayout, serverconfig.getString("LogFile"), true, false, 128);
/*  775 */         Logger.getRootLogger().addAppender((Appender)fileAppender);
/*      */       }
/*  777 */       catch (IOException e) {
/*      */         
/*  779 */         logger.error("Cannot log to file '" + serverconfig.getString("LogFile") + "': " + e.getMessage());
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  784 */     if (useDebug) {
/*  785 */       Logger.getRootLogger().setLevel(Level.ALL);
/*      */     } else {
/*  787 */       Logger.getRootLogger().setLevel(Level.toLevel(serverconfig.getString("LogLevel", "INFO")));
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
/*      */   public static DWProtocol getHandler(int handlerno) {
/*  799 */     if (handlerno < dwProtoHandlers.size() && handlerno > -1) {
/*  800 */       return dwProtoHandlers.get(handlerno);
/*      */     }
/*  802 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static ArrayList<String> getLogEvents(int num) {
/*  808 */     return dwAppender.getLastEvents(num);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getLogEventsSize() {
/*  815 */     return dwAppender.getEventsSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getNumHandlers() {
/*  823 */     return dwProtoHandlers.size();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getNumHandlersAlive() {
/*  829 */     int res = 0;
/*      */     
/*  831 */     for (DWProtocol p : dwProtoHandlers) {
/*      */       
/*  833 */       if (p != null)
/*      */       {
/*  835 */         if (!p.isDying() && p.isReady()) {
/*  836 */           res++;
/*      */         }
/*      */       }
/*      */     } 
/*  840 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidHandlerNo(int handler) {
/*  846 */     if (handler < dwProtoHandlers.size() && handler >= 0)
/*      */     {
/*  848 */       if (dwProtoHandlers.get(handler) != null)
/*      */       {
/*  850 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  854 */     return false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void restartHandler(int handler) {
/*  887 */     logger.error("instance restart not implemented!");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean handlerIsAlive(int h) {
/*  898 */     if (dwProtoHandlers.get(h) != null)
/*      */     {
/*  900 */       if (!((DWProtocol)dwProtoHandlers.get(h)).isDying() && dwProtoHandlerThreads.get(h) != null && ((Thread)dwProtoHandlerThreads.get(h)).isAlive())
/*      */       {
/*  902 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  906 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getHandlerName(int handlerno) {
/*  914 */     if (isValidHandlerNo(handlerno))
/*      */     {
/*  916 */       return ((DWProtocol)dwProtoHandlers.get(handlerno)).getConfig().getString("[@name]", "unnamed instance " + handlerno);
/*      */     }
/*      */     
/*  919 */     return "null handler " + handlerno;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void saveServerConfig() throws ConfigurationException {
/*  925 */     serverconfig.save();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static ArrayList<String> getAvailableSerialPorts() {
/*  931 */     ArrayList<String> h = new ArrayList<String>();
/*      */     
/*  933 */     Enumeration<CommPortIdentifier> thePorts = CommPortIdentifier.getPortIdentifiers();
/*  934 */     while (thePorts.hasMoreElements()) {
/*      */ 
/*      */       
/*      */       try {
/*  938 */         CommPortIdentifier com = thePorts.nextElement();
/*  939 */         if (com.getPortType() == 1) {
/*  940 */           h.add(com.getName());
/*      */         }
/*  942 */       } catch (Exception e) {
/*      */         
/*  944 */         logger.error("While detecting serial devices: " + e.getMessage());
/*      */         
/*  946 */         if (useDebug) {
/*      */           
/*  948 */           System.out.println("--------------------------------------------------------------------------------");
/*  949 */           e.printStackTrace();
/*  950 */           System.out.println("--------------------------------------------------------------------------------");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  956 */     return h;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getSerialPortStatus(String port) {
/*  964 */     String res = "";
/*      */ 
/*      */     
/*      */     try {
/*  968 */       CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(port);
/*      */       
/*  970 */       if (pi.isCurrentlyOwned())
/*      */       {
/*  972 */         res = "In use by " + pi.getCurrentOwner();
/*      */       }
/*      */       else
/*      */       {
/*  976 */         CommPort commPort = pi.open("DriveWireServer", 2000);
/*      */         
/*  978 */         if (commPort instanceof SerialPort) {
/*      */           
/*  980 */           res = "Available";
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  985 */           res = "Not a serial port";
/*      */         } 
/*      */         
/*  988 */         commPort.close();
/*      */       }
/*      */     
/*      */     }
/*  992 */     catch (Exception e) {
/*      */ 
/*      */       
/*  995 */       res = e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
/*      */       
/*  997 */       if (useDebug) {
/*      */         
/*  999 */         System.out.println("--------------------------------------------------------------------------------");
/* 1000 */         e.printStackTrace();
/* 1001 */         System.out.println("--------------------------------------------------------------------------------");
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1006 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void shutdown() {
/* 1012 */     logger.info("server shutdown requested");
/* 1013 */     wanttodie = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitServerConfigEvent(String propertyName, String propertyValue) {
/* 1021 */     if (uiObj != null) {
/*      */       
/* 1023 */       DWEvent evt = new DWEvent((byte)67);
/*      */       
/* 1025 */       evt.setParam("k", propertyName);
/* 1026 */       evt.setParam("v", propertyValue);
/*      */       
/* 1028 */       uiObj.submitEvent(evt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitInstanceConfigEvent(int instance, String propertyName, String propertyValue) {
/* 1035 */     if (uiObj != null) {
/*      */       
/* 1037 */       DWEvent evt = new DWEvent((byte)73);
/*      */       
/* 1039 */       evt.setParam("i", String.valueOf(instance));
/* 1040 */       evt.setParam("k", propertyName);
/* 1041 */       evt.setParam("v", propertyValue);
/*      */       
/* 1043 */       uiObj.submitEvent(evt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitDiskEvent(int instance, int diskno, String key, String val) {
/* 1050 */     if (uiObj != null) {
/*      */       
/* 1052 */       DWEvent evt = new DWEvent((byte)68);
/*      */       
/* 1054 */       evt.setParam("i", String.valueOf(instance));
/* 1055 */       evt.setParam("d", String.valueOf(diskno));
/* 1056 */       evt.setParam("k", key);
/* 1057 */       evt.setParam("v", val);
/*      */       
/* 1059 */       uiObj.submitEvent(evt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void submitMIDIEvent(int instance, String key, String val) {
/* 1066 */     if (uiObj != null) {
/*      */       
/* 1068 */       DWEvent evt = new DWEvent((byte)77);
/*      */       
/* 1070 */       evt.setParam("i", String.valueOf(instance));
/* 1071 */       evt.setParam("k", key);
/* 1072 */       evt.setParam("v", val);
/*      */       
/* 1074 */       uiObj.submitEvent(evt);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void submitLogEvent(LoggingEvent event) {
/* 1080 */     DWEvent evt = new DWEvent((byte)76);
/*      */     
/* 1082 */     evt.setParam("l", event.getLevel().toString());
/* 1083 */     evt.setParam("t", event.getTimeStamp() + "");
/* 1084 */     evt.setParam("m", event.getMessage().toString());
/* 1085 */     evt.setParam("r", event.getThreadName());
/* 1086 */     evt.setParam("s", event.getLoggerName());
/*      */     
/* 1088 */     synchronized (logcache) {
/*      */       
/* 1090 */       logcache.add(evt);
/* 1091 */       if (logcache.size() > 500) {
/* 1092 */         logcache.remove(0);
/*      */       }
/*      */     } 
/* 1095 */     if (uiObj != null)
/*      */     {
/* 1097 */       uiObj.submitEvent(evt);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isReady() {
/* 1106 */     return ready;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean testSerialPort_Open(String device) throws Exception {
/*      */     try {
/* 1117 */       CommPortIdentifier pi = CommPortIdentifier.getPortIdentifier(device);
/*      */       
/* 1119 */       if (pi.isCurrentlyOwned())
/*      */       {
/* 1121 */         throw new Exception("In use by " + pi.getCurrentOwner());
/*      */       }
/*      */ 
/*      */       
/* 1125 */       CommPort commPort = pi.open("DriveWireTest", 2000);
/*      */       
/* 1127 */       if (commPort instanceof SerialPort) {
/*      */ 
/*      */         
/* 1130 */         testSerialPort = (SerialPort)commPort;
/*      */         
/* 1132 */         testSerialPort.setFlowControlMode(0);
/* 1133 */         testSerialPort.enableReceiveThreshold(1);
/* 1134 */         testSerialPort.enableReceiveTimeout(3000);
/*      */         
/* 1136 */         return true;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1141 */       throw new Exception("Not a serial port");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1147 */     catch (Exception e) {
/*      */ 
/*      */       
/* 1150 */       throw new Exception(e.getLocalizedMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean testSerialPort_setParams(int rate) throws Exception {
/*      */     try {
/* 1161 */       testSerialPort.setSerialPortParams(rate, 8, 1, 0);
/* 1162 */       return true;
/*      */     }
/* 1164 */     catch (Exception e) {
/*      */ 
/*      */       
/* 1167 */       throw new Exception(e.getLocalizedMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int testSerialPort_read() throws Exception {
/*      */     try {
/* 1178 */       return testSerialPort.getInputStream().read();
/*      */     }
/* 1180 */     catch (Exception e) {
/*      */ 
/*      */       
/* 1183 */       throw new Exception(e.getLocalizedMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void testSerialPort_close() {
/*      */     try {
/* 1194 */       testSerialPort.close();
/*      */     }
/* 1196 */     catch (Exception e) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ArrayList<DWEvent> getLogCache() {
/* 1207 */     return logcache;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isConsoleLogging() {
/* 1215 */     return serverconfig.getBoolean("LogToConsole", false);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isDebug() {
/* 1220 */     return useDebug;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void handleUncaughtException(Thread thread, Throwable thrw) {
/* 1228 */     String msg = "Exception in thread " + thread.getName();
/*      */     
/* 1230 */     if (thrw.getClass().getSimpleName() != null) {
/* 1231 */       msg = msg + ": " + thrw.getClass().getSimpleName();
/*      */     }
/* 1233 */     if (thrw.getMessage() != null) {
/* 1234 */       msg = msg + ": " + thrw.getMessage();
/*      */     }
/* 1236 */     if (logger != null) {
/*      */       
/* 1238 */       logger.error(msg);
/* 1239 */       logger.info(getStackTrace(thrw));
/*      */     } 
/*      */     
/* 1242 */     System.out.println("--------------------------------------------------------------------------------");
/* 1243 */     System.out.println(msg);
/* 1244 */     System.out.println("--------------------------------------------------------------------------------");
/* 1245 */     thrw.printStackTrace();
/*      */   }
/*      */ 
/*      */   
/*      */   public static String getStackTrace(Throwable aThrowable) {
/* 1250 */     Writer result = new StringWriter();
/* 1251 */     PrintWriter printWriter = new PrintWriter(result);
/* 1252 */     aThrowable.printStackTrace(printWriter);
/* 1253 */     return result.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getMagic() {
/* 1260 */     return magic;
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean getNoMIDI() {
/* 1265 */     return noMIDI;
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean getNoMount() {
/* 1270 */     return noMount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setConfigFreeze(boolean b) {
/* 1278 */     configFreeze = b;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isConfigFreeze() {
/* 1284 */     return configFreeze;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setLoggingRestart() {
/* 1292 */     restart_logging = true;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void setUIRestart() {
/* 1297 */     restart_ui = true;
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DriveWireServer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */