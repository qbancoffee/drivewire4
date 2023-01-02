/*      */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*      */ 
/*      */ import com.groupunix.drivewireserver.DriveWireServer;
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotOpenException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*      */ import com.groupunix.drivewireserver.dwhelp.DWHelp;
/*      */ import com.groupunix.drivewireserver.virtualprinter.DWVPrinter;
/*      */ import com.groupunix.drivewireserver.virtualserial.DWVPortTermThread;
/*      */ import com.groupunix.drivewireserver.virtualserial.DWVSerialPorts;
/*      */ import gnu.io.NoSuchPortException;
/*      */ import gnu.io.PortInUseException;
/*      */ import gnu.io.UnsupportedCommOperationException;
/*      */ import java.io.IOException;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.TooManyListenersException;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.log4j.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DWProtocolHandler
/*      */   implements Runnable, DWProtocol
/*      */ {
/*   37 */   private final Logger logger = Logger.getLogger("DWServer.DWProtocolHandler");
/*      */ 
/*      */ 
/*      */   
/*   41 */   private int lastDrive = 0;
/*   42 */   private int readRetries = 0;
/*   43 */   private int writeRetries = 0;
/*   44 */   private int sectorsRead = 0;
/*   45 */   private int sectorsWritten = 0;
/*   46 */   private byte lastOpcode = -1;
/*   47 */   private byte lastGetStat = -1;
/*   48 */   private byte lastSetStat = -1;
/*   49 */   private int lastChecksum = 0;
/*   50 */   private int lastError = 0;
/*   51 */   private byte[] lastLSN = new byte[3];
/*   52 */   private long total_ops = 0L;
/*   53 */   private long disk_ops = 0L;
/*   54 */   private long vserial_ops = 0L;
/*      */   private boolean inOp = false;
/*   56 */   private int syncSkipped = 0;
/*      */   
/*   58 */   private GregorianCalendar dwinitTime = new GregorianCalendar();
/*      */ 
/*      */ 
/*      */   
/*   62 */   private DWProtocolDevice protodev = null;
/*      */ 
/*      */   
/*      */   private DWVPrinter vprinter;
/*      */ 
/*      */   
/*      */   private DWDiskDrives diskDrives;
/*      */ 
/*      */   
/*      */   private boolean wanttodie = false;
/*      */ 
/*      */   
/*      */   private DWRFMHandler rfmhandler;
/*      */ 
/*      */   
/*      */   private int handlerno;
/*      */   
/*      */   private HierarchicalConfiguration config;
/*      */   
/*      */   private Thread termT;
/*      */   
/*      */   private DWVSerialPorts dwVSerialPorts;
/*      */   
/*      */   private DWVPortTermThread termHandler;
/*      */   
/*      */   private DWHelp dwhelp;
/*      */   
/*      */   private boolean ready = false;
/*      */   
/*      */   private boolean resetPending = false;
/*      */ 
/*      */   
/*      */   public DWProtocolHandler(int handlerno, HierarchicalConfiguration hconf) {
/*   95 */     this.handlerno = handlerno;
/*   96 */     this.config = hconf;
/*      */     
/*   98 */     this.config.addConfigurationListener(new DWProtocolConfigListener(this));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HierarchicalConfiguration getConfig() {
/*  105 */     return this.config;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void reset() {
/*  111 */     DoOP_RESET();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean connected() {
/*  117 */     if (this.protodev != null)
/*  118 */       return this.protodev.connected(); 
/*  119 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdown() {
/*  125 */     this.logger.debug("handler #" + this.handlerno + ": shutdown requested");
/*      */     
/*  127 */     this.wanttodie = true;
/*      */     
/*  129 */     if (this.protodev != null) {
/*  130 */       this.protodev.shutdown();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void run() {
/*  136 */     int opcodeint = -1;
/*      */     
/*  138 */     Thread.currentThread().setName("dwproto-" + this.handlerno + "-" + Thread.currentThread().getId());
/*      */ 
/*      */ 
/*      */     
/*  142 */     Thread.currentThread().setPriority(10);
/*      */ 
/*      */     
/*  145 */     if (this.protodev == null) {
/*  146 */       setupProtocolDevice();
/*      */     }
/*      */     
/*  149 */     if (!this.wanttodie) {
/*      */       
/*  151 */       this.logger.debug("handler #" + this.handlerno + ": setting up...");
/*      */ 
/*      */       
/*  154 */       this.diskDrives = new DWDiskDrives(this);
/*      */ 
/*      */       
/*  157 */       this.dwVSerialPorts = new DWVSerialPorts(this);
/*  158 */       this.dwVSerialPorts.resetAllPorts();
/*      */ 
/*      */       
/*  161 */       this.vprinter = new DWVPrinter(this);
/*      */ 
/*      */       
/*  164 */       this.rfmhandler = new DWRFMHandler(this.handlerno);
/*      */ 
/*      */       
/*  167 */       if (this.config.containsKey("TermPort")) {
/*      */         
/*  169 */         this.logger.debug("handler #" + this.handlerno + ": starting term device listener thread");
/*  170 */         this.termHandler = new DWVPortTermThread(this, this.config.getInt("TermPort"));
/*  171 */         this.termT = new Thread((Runnable)this.termHandler);
/*  172 */         this.termT.setDaemon(true);
/*  173 */         this.termT.start();
/*      */       } 
/*      */ 
/*      */       
/*  177 */       if (this.config.containsKey("HelpFile")) {
/*      */         
/*  179 */         this.dwhelp = new DWHelp(this.config.getString("HelpFile"));
/*      */       }
/*      */       else {
/*      */         
/*  183 */         this.dwhelp = new DWHelp(this);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  188 */     this.ready = true;
/*  189 */     long optime = 0L;
/*      */     
/*  191 */     this.logger.info("handler #" + this.handlerno + " is ready");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  198 */       while (!this.wanttodie) {
/*      */         
/*  200 */         opcodeint = -1;
/*      */ 
/*      */         
/*  203 */         if (this.protodev != null && !this.resetPending) {
/*      */           
/*      */           try {
/*      */             
/*  207 */             opcodeint = this.protodev.comRead1(false);
/*      */           
/*      */           }
/*  210 */           catch (IOException e) {
/*      */             
/*  212 */             this.logger.error("Strange result in proto read loop: " + e.getMessage());
/*  213 */           } catch (DWCommTimeOutException e) {
/*      */ 
/*      */             
/*  216 */             this.logger.error("Timeout in proto read loop: " + e.getMessage());
/*      */           } 
/*      */         }
/*      */         
/*  220 */         if (opcodeint > -1 && this.protodev != null) {
/*      */           
/*  222 */           ((DWSerialDevice)this.protodev).resetReadtime();
/*  223 */           optime = System.currentTimeMillis();
/*  224 */           this.inOp = true;
/*  225 */           this.lastOpcode = (byte)opcodeint;
/*  226 */           this.total_ops++;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/*  233 */             if (this.lastOpcode >= Byte.MIN_VALUE && this.lastOpcode <= -114) {
/*      */               
/*  235 */               DoOP_FASTSERWRITE(this.lastOpcode);
/*  236 */               this.vserial_ops++;
/*      */ 
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */               
/*  243 */               switch (this.lastOpcode) {
/*      */                 
/*      */                 case -8:
/*      */                 case -2:
/*      */                 case -1:
/*  248 */                   DoOP_RESET();
/*      */                   break;
/*      */                 
/*      */                 case 90:
/*  252 */                   DoOP_DWINIT();
/*      */                   break;
/*      */                 
/*      */                 case 73:
/*  256 */                   DoOP_INIT();
/*      */                   break;
/*      */                 
/*      */                 case 84:
/*  260 */                   DoOP_TERM();
/*      */                   break;
/*      */                 
/*      */                 case 82:
/*      */                 case 114:
/*  265 */                   DoOP_READ(this.lastOpcode);
/*  266 */                   this.disk_ops++;
/*      */                   break;
/*      */                 
/*      */                 case -46:
/*      */                 case -14:
/*  271 */                   DoOP_READEX(this.lastOpcode);
/*  272 */                   this.disk_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 87:
/*      */                 case 119:
/*  277 */                   DoOP_WRITE(this.lastOpcode);
/*  278 */                   this.disk_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 71:
/*      */                 case 83:
/*  283 */                   DoOP_STAT(this.lastOpcode);
/*  284 */                   this.disk_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 35:
/*  288 */                   DoOP_TIME();
/*      */                   break;
/*      */                 
/*      */                 case 80:
/*  292 */                   DoOP_PRINT();
/*      */                   break;
/*      */                 
/*      */                 case 70:
/*  296 */                   DoOP_PRINTFLUSH();
/*      */                   break;
/*      */                 
/*      */                 case 99:
/*  300 */                   DoOP_SERREADM();
/*  301 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 67:
/*  305 */                   DoOP_SERREAD();
/*  306 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case -61:
/*  310 */                   DoOP_SERWRITE();
/*  311 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 100:
/*  315 */                   DoOP_SERWRITEM();
/*  316 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case -60:
/*  320 */                   DoOP_SERSETSTAT();
/*  321 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 68:
/*  325 */                   DoOP_SERGETSTAT();
/*  326 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case 69:
/*  330 */                   DoOP_SERINIT();
/*  331 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case -59:
/*  335 */                   DoOP_SERTERM();
/*  336 */                   this.vserial_ops++;
/*      */                   break;
/*      */                 
/*      */                 case -26:
/*      */                 case 0:
/*  341 */                   DoOP_NOP();
/*      */                   break;
/*      */                 
/*      */                 case -42:
/*  345 */                   DoOP_RFM();
/*      */                   break;
/*      */                 
/*      */                 case -3:
/*  349 */                   DoOP_230K115K();
/*      */                   break;
/*      */                 
/*      */                 case 1:
/*  353 */                   DoOP_NAMEOBJ_MOUNT();
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 default:
/*  358 */                   this.logger.warn("UNKNOWN OPCODE: " + opcodeint);
/*      */                   break;
/*      */               } 
/*      */             
/*      */             } 
/*  363 */           } catch (IOException e) {
/*      */             
/*  365 */             this.logger.error("IOError in proto op: " + e.getMessage());
/*      */           }
/*  367 */           catch (DWCommTimeOutException e) {
/*      */             
/*  369 */             this.logger.warn("Timed out reading from CoCo in " + DWUtils.prettyOP(this.lastOpcode));
/*      */           }
/*  371 */           catch (DWPortNotValidException e) {
/*      */             
/*  373 */             this.logger.warn("Invalid port # from CoCo in " + DWUtils.prettyOP(this.lastOpcode) + ": " + e.getMessage());
/*      */           } 
/*      */           
/*  376 */           this.inOp = false;
/*      */           
/*  378 */           if (System.currentTimeMillis() - optime > 200L) {
/*  379 */             this.logger.warn(DWUtils.prettyOP(this.lastOpcode) + " took " + (System.currentTimeMillis() - optime) + "ms.  Server loaded or low on ram?"); continue;
/*  380 */           }  if (this.config.getBoolean("LogTiming", false)) {
/*  381 */             this.logger.debug(DWUtils.prettyOP(this.lastOpcode) + " took " + (System.currentTimeMillis() - optime) + "ms, serial read delay was " + ((DWSerialDevice)this.protodev).getReadtime());
/*      */           }
/*      */           continue;
/*      */         } 
/*  385 */         if (!this.wanttodie) {
/*      */           
/*  387 */           if (this.resetPending) {
/*      */             
/*  389 */             this.logger.debug("device is resetting...");
/*      */ 
/*      */             
/*  392 */             if (this.protodev != null) {
/*  393 */               this.protodev.shutdown();
/*      */             }
/*  395 */           } else if (!this.config.getString("DeviceType", "").equals("dummy")) {
/*  396 */             this.logger.debug("device unavailable, will retry in " + this.config.getInt("DeviceFailRetryTime", 6000) + "ms");
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/*  402 */             Thread.sleep(this.config.getInt("DeviceFailRetryTime", 6000));
/*      */             
/*  404 */             setupProtocolDevice();
/*      */           
/*      */           }
/*  407 */           catch (InterruptedException e) {
/*      */             
/*  409 */             this.logger.error("Interrupted during failed port delay.. giving up on this crazy situation");
/*  410 */             this.wanttodie = true;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  418 */       this.logger.debug("handler #" + this.handlerno + ": exiting");
/*      */ 
/*      */       
/*  421 */       if (this.dwVSerialPorts != null)
/*      */       {
/*  423 */         this.dwVSerialPorts.shutdown();
/*      */       }
/*      */       
/*  426 */       if (this.diskDrives != null)
/*      */       {
/*  428 */         this.diskDrives.shutdown();
/*      */       }
/*      */       
/*  431 */       if (this.termT != null)
/*      */       {
/*  433 */         this.termHandler.shutdown();
/*  434 */         this.termT.interrupt();
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*  441 */     catch (Exception e) {
/*      */ 
/*      */       
/*  444 */       System.out.println("\n\n");
/*      */       
/*  446 */       e.printStackTrace();
/*      */       
/*  448 */       System.out.println("\n\n");
/*      */       
/*  450 */       this.logger.error(e.getMessage());
/*      */     }
/*      */     finally {
/*      */       
/*  454 */       if (this.protodev != null)
/*      */       {
/*  456 */         this.protodev.shutdown();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_NAMEOBJ_MOUNT() throws DWCommTimeOutException, IOException {
/*  465 */     long starttime = System.currentTimeMillis();
/*      */     
/*  467 */     int namesize = this.protodev.comRead1(true);
/*  468 */     byte[] namebuf = new byte[namesize];
/*  469 */     namebuf = this.protodev.comRead(namesize);
/*  470 */     String objname = new String(namebuf);
/*      */ 
/*      */     
/*  473 */     int result = this.diskDrives.nameObjMount(objname);
/*      */ 
/*      */     
/*  476 */     if (this.config.containsKey("NameObjMountDelay")) {
/*      */       
/*      */       try {
/*      */         
/*  480 */         this.logger.debug("named object mount delay " + this.config.getLong("NameObjMountDelay") + " ms...");
/*  481 */         Thread.sleep(this.config.getLong("NameObjMountDelay"));
/*      */       }
/*  483 */       catch (InterruptedException e) {
/*      */         
/*  485 */         this.logger.warn("Interrupted during mount delay");
/*      */       } 
/*      */     }
/*      */     
/*  489 */     this.protodev.comWrite1(result, false);
/*      */     
/*  491 */     if (this.config.getBoolean("LogOpCode", false)) {
/*      */       
/*  493 */       long delay = System.currentTimeMillis() - starttime;
/*  494 */       this.logger.info("DoOP_NAMEOBJ_MOUNT for '" + objname + "' result: " + result + ", call took " + delay + "ms");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_230K115K() {
/*  503 */     if (this.config.getBoolean("DetectDATurbo", false)) {
/*      */       
/*      */       try {
/*      */         
/*  507 */         ((DWSerialDevice)this.protodev).enableDATurbo();
/*  508 */         this.logger.info("Detected switch to 230k mode");
/*      */       }
/*  510 */       catch (UnsupportedCommOperationException e) {
/*      */         
/*  512 */         this.logger.error("comm port did not make the switch to 230k mode: " + e.getMessage());
/*  513 */         this.logger.error("bail out!");
/*  514 */         this.wanttodie = true;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_FASTSERWRITE(byte opcode) throws DWCommTimeOutException, IOException {
/*  523 */     int port = opcode - -128;
/*      */     
/*      */     try {
/*  526 */       int databyte = this.protodev.comRead1(true);
/*      */       
/*  528 */       this.dwVSerialPorts.serWrite(port, databyte);
/*      */       
/*  530 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/*  532 */         this.logger.info("DoOP_FASTSERWRITE to port " + port + ": " + databyte);
/*      */       
/*      */       }
/*      */     }
/*  536 */     catch (DWPortNotOpenException e1) {
/*      */       
/*  538 */       this.logger.error(e1.getMessage());
/*      */     }
/*  540 */     catch (DWPortNotValidException e2) {
/*      */       
/*  542 */       this.logger.error(e2.getMessage());
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
/*      */   private void DoOP_DWINIT() throws DWCommTimeOutException, IOException {
/*  557 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/*  559 */       this.logger.info("DoOP_DWINIT");
/*      */     }
/*      */     
/*  562 */     int drv_version = this.protodev.comRead1(true);
/*      */ 
/*      */     
/*  565 */     if (!this.config.getBoolean("DW3Only", false)) {
/*      */ 
/*      */       
/*  568 */       this.protodev.comWrite1(4, true);
/*      */ 
/*      */       
/*  571 */       if (drv_version <= 63) {
/*      */         
/*  573 */         this.logger.debug("DWINIT from NitrOS9! Implementation variety type # " + drv_version);
/*      */       }
/*  575 */       else if (drv_version >= 64 && drv_version <= 79) {
/*      */         
/*  577 */         this.logger.debug("DWINIT from CoCoBoot! Implementation variety type # " + (drv_version - 64));
/*      */       }
/*  579 */       else if (drv_version >= 96 && drv_version <= 111) {
/*      */         
/*  581 */         this.logger.debug("DWINIT from LWOS/LWBASIC! Implementation variety type # " + (drv_version - 96));
/*      */       }
/*      */       else {
/*      */         
/*  585 */         this.logger.info("DWINIT got unknown driver version # " + drv_version);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  590 */       if (drv_version < 128)
/*      */       {
/*  592 */         if (this.config.getBoolean("HDBDOSMode", false)) {
/*      */           
/*  594 */           this.logger.debug("Disabling HDBDOS mode due to non HDBDOS DWINIT");
/*  595 */           this.config.setProperty("HDBDOSMode", Boolean.valueOf(false));
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  601 */       this.dwinitTime = new GregorianCalendar();
/*      */ 
/*      */       
/*  604 */       this.dwVSerialPorts.resetAllPorts();
/*      */     }
/*      */     else {
/*      */       
/*  608 */       this.logger.debug("DWINIT received, ignoring due to DW3Only setting");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_NOP() {
/*  616 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/*  618 */       this.logger.info("DoOP_NOP");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_RFM() throws DWCommTimeOutException, IOException {
/*  628 */     int rfm_op = this.protodev.comRead1(true);
/*  629 */     this.logger.info("DoOP_RFM call " + rfm_op);
/*      */     
/*  631 */     this.rfmhandler.DoRFMOP(this.protodev, rfm_op);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_TERM() {
/*  642 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/*  644 */       this.logger.info("DoOP_TERM");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void DoOP_INIT() {
/*  650 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/*  652 */       this.logger.info("DoOP_INIT");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_RESET() {
/*  662 */     this.lastDrive = 0;
/*  663 */     this.readRetries = 0;
/*  664 */     this.writeRetries = 0;
/*  665 */     this.sectorsRead = 0;
/*  666 */     this.sectorsWritten = 0;
/*  667 */     this.lastOpcode = -1;
/*  668 */     this.lastGetStat = -1;
/*  669 */     this.lastSetStat = -1;
/*  670 */     this.lastChecksum = 0;
/*  671 */     this.lastError = 0;
/*      */     
/*  673 */     this.lastLSN = new byte[3];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  678 */     this.dwVSerialPorts.resetAllPorts();
/*      */     
/*  680 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/*  682 */       this.logger.info("DoOP_RESET");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_WRITE(byte opcode) throws DWCommTimeOutException, IOException {
/*  691 */     byte[] cocosum = new byte[2];
/*  692 */     byte[] responsebuf = new byte[getConfig().getInt("DiskSectorSize", 256) + 6];
/*  693 */     byte response = 0;
/*  694 */     byte[] sector = new byte[getConfig().getInt("DiskSectorSize", 256)];
/*      */ 
/*      */     
/*  697 */     responsebuf = this.protodev.comRead(getConfig().getInt("DiskSectorSize", 256) + 6);
/*      */     
/*  699 */     this.lastDrive = responsebuf[0] & 0xFF;
/*  700 */     System.arraycopy(responsebuf, 1, this.lastLSN, 0, 3);
/*  701 */     System.arraycopy(responsebuf, 4, sector, 0, getConfig().getInt("DiskSectorSize", 256));
/*  702 */     System.arraycopy(responsebuf, getConfig().getInt("DiskSectorSize", 256) + 4, cocosum, 0, 2);
/*      */ 
/*      */ 
/*      */     
/*  706 */     this.lastChecksum = computeChecksum(sector, getConfig().getInt("DiskSectorSize", 256));
/*      */ 
/*      */ 
/*      */     
/*  710 */     if (this.lastChecksum != DWUtils.int2(cocosum)) {
/*      */ 
/*      */       
/*  713 */       this.protodev.comWrite1(-13, true);
/*      */       
/*  715 */       this.logger.warn("DoOP_WRITE: Bad checksum, drive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN) + " CocoSum: " + DWUtils.int2(cocosum) + " ServerSum: " + this.lastChecksum);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*  722 */     response = 0;
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  727 */       this.diskDrives.seekSector(this.lastDrive, DWUtils.int3(this.lastLSN));
/*      */       
/*  729 */       this.diskDrives.writeSector(this.lastDrive, sector);
/*      */     }
/*  731 */     catch (DWDriveNotLoadedException e1) {
/*      */ 
/*      */       
/*  734 */       response = -10;
/*  735 */       this.logger.warn(e1.getMessage());
/*      */     }
/*  737 */     catch (DWDriveNotValidException e2) {
/*      */ 
/*      */       
/*  740 */       response = -10;
/*  741 */       this.logger.warn(e2.getMessage());
/*      */     }
/*  743 */     catch (DWDriveWriteProtectedException e3) {
/*      */ 
/*      */       
/*  746 */       response = -14;
/*  747 */       this.logger.warn(e3.getMessage());
/*      */     }
/*  749 */     catch (DWInvalidSectorException e5) {
/*      */       
/*  751 */       response = -11;
/*  752 */       this.logger.warn(e5.getMessage());
/*      */     }
/*  754 */     catch (DWSeekPastEndOfDeviceException e6) {
/*      */       
/*  756 */       response = -11;
/*  757 */       this.logger.warn(e6.getMessage());
/*      */     } 
/*      */ 
/*      */     
/*  761 */     if (response != 0) {
/*  762 */       this.lastError = response;
/*      */     }
/*      */     
/*  765 */     this.protodev.comWrite1(response, true);
/*      */ 
/*      */     
/*  768 */     if (response == 0) {
/*  769 */       this.sectorsWritten++;
/*      */     }
/*  771 */     if (opcode == 119) {
/*      */       
/*  773 */       this.writeRetries++;
/*  774 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/*  776 */         this.logger.warn("DoOP_REWRITE lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  781 */     else if (this.config.getBoolean("LogOpCode", false)) {
/*      */       
/*  783 */       this.logger.info("DoOP_WRITE lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
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
/*      */   private void DoOP_READ(int opcode) throws IOException, DWCommTimeOutException {
/*  795 */     byte[] mysum = new byte[2];
/*  796 */     byte[] responsebuf = new byte[4];
/*  797 */     byte[] sector = new byte[getConfig().getInt("DiskSectorSize", 256)];
/*  798 */     byte result = 0;
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  803 */       responsebuf = this.protodev.comRead(4);
/*      */ 
/*      */       
/*  806 */       this.lastDrive = responsebuf[0] & 0xFF;
/*  807 */       System.arraycopy(responsebuf, 1, this.lastLSN, 0, 3);
/*      */ 
/*      */       
/*  810 */       this.diskDrives.seekSector(this.lastDrive, DWUtils.int3(this.lastLSN));
/*      */ 
/*      */       
/*  813 */       sector = this.diskDrives.readSector(this.lastDrive);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  818 */     catch (DWDriveNotLoadedException e1) {
/*      */       
/*  820 */       this.logger.warn("DoOP_READ: " + e1.getMessage());
/*  821 */       result = -10;
/*      */     }
/*  823 */     catch (DWDriveNotValidException e2) {
/*      */       
/*  825 */       this.logger.warn("DoOP_READ: " + e2.getMessage());
/*  826 */       result = -10;
/*      */     }
/*  828 */     catch (DWInvalidSectorException e5) {
/*      */       
/*  830 */       this.logger.error("DoOP_READ: " + e5.getMessage());
/*  831 */       result = -12;
/*      */     }
/*  833 */     catch (DWSeekPastEndOfDeviceException e6) {
/*      */       
/*  835 */       this.logger.error("DoOP_READ: " + e6.getMessage());
/*  836 */       result = -12;
/*      */     }
/*  838 */     catch (DWImageFormatException e7) {
/*      */       
/*  840 */       this.logger.error("DoOP_READ: " + e7.getMessage());
/*  841 */       result = -12;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  846 */     this.protodev.comWrite1(result, true);
/*      */ 
/*      */     
/*  849 */     if (result == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  854 */       this.protodev.comWrite(sector, getConfig().getInt("DiskSectorSize", 256), true);
/*      */ 
/*      */       
/*  857 */       this.lastChecksum = computeChecksum(sector, getConfig().getInt("DiskSectorSize", 256));
/*      */       
/*  859 */       mysum[0] = (byte)(this.lastChecksum >> 8 & 0xFF);
/*  860 */       mysum[1] = (byte)(this.lastChecksum << 0 & 0xFF);
/*      */ 
/*      */       
/*  863 */       this.protodev.comWrite(mysum, 2, true);
/*      */ 
/*      */       
/*  866 */       this.sectorsRead++;
/*      */       
/*  868 */       if (opcode == 114) {
/*      */         
/*  870 */         this.readRetries++;
/*  871 */         this.logger.warn("DoOP_REREAD lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */ 
/*      */       
/*      */       }
/*  875 */       else if (this.config.getBoolean("LogOpCode", false)) {
/*      */         
/*  877 */         this.logger.info("DoOP_READ lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
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
/*      */   private void DoOP_READEX(int opcode) throws IOException, DWCommTimeOutException {
/*  889 */     byte[] cocosum = new byte[2];
/*  890 */     byte[] mysum = new byte[2];
/*  891 */     byte[] responsebuf = new byte[4];
/*  892 */     byte[] sector = new byte[getConfig().getInt("DiskSectorSize", 256)];
/*  893 */     byte result = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  899 */       responsebuf = this.protodev.comRead(4);
/*      */       
/*  901 */       this.lastDrive = responsebuf[0] & 0xFF;
/*  902 */       System.arraycopy(responsebuf, 1, this.lastLSN, 0, 3);
/*      */ 
/*      */       
/*  905 */       this.diskDrives.seekSector(this.lastDrive, DWUtils.int3(this.lastLSN));
/*      */ 
/*      */       
/*  908 */       sector = this.diskDrives.readSector(this.lastDrive);
/*      */     
/*      */     }
/*  911 */     catch (DWDriveNotLoadedException e1) {
/*      */ 
/*      */       
/*  914 */       sector = this.diskDrives.nullSector();
/*  915 */       this.logger.warn("DoOP_READEX: " + e1.getMessage());
/*  916 */       result = -10;
/*      */     }
/*  918 */     catch (DWDriveNotValidException e2) {
/*      */ 
/*      */       
/*  921 */       sector = this.diskDrives.nullSector();
/*  922 */       this.logger.warn("DoOP_READEX: " + e2.getMessage());
/*  923 */       result = -10;
/*      */     }
/*  925 */     catch (DWInvalidSectorException e5) {
/*      */       
/*  927 */       sector = this.diskDrives.nullSector();
/*  928 */       this.logger.error("DoOP_READEX: " + e5.getMessage());
/*  929 */       result = -12;
/*      */     }
/*  931 */     catch (DWSeekPastEndOfDeviceException e6) {
/*      */       
/*  933 */       sector = this.diskDrives.nullSector();
/*  934 */       this.logger.error("DoOP_READEX: " + e6.getMessage());
/*  935 */       result = -12;
/*      */     }
/*  937 */     catch (DWImageFormatException e7) {
/*      */       
/*  939 */       sector = this.diskDrives.nullSector();
/*  940 */       this.logger.error("DoOP_READEX: " + e7.getMessage());
/*  941 */       result = -12;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  946 */     if (this.config.containsKey("ReadDelay")) {
/*      */       
/*      */       try {
/*      */         
/*  950 */         this.logger.debug("read delay " + this.config.getLong("ReadDelay") + " ms...");
/*  951 */         Thread.sleep(this.config.getLong("ReadDelay"));
/*      */       }
/*  953 */       catch (InterruptedException e) {
/*      */         
/*  955 */         this.logger.warn("Interrupted during read delay");
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  960 */     this.protodev.comWrite(sector, getConfig().getInt("DiskSectorSize", 256), true);
/*      */     
/*  962 */     if (!this.config.getBoolean("ProtocolDisableReadChecksum", false)) {
/*      */ 
/*      */       
/*  965 */       this.lastChecksum = computeChecksum(sector, getConfig().getInt("DiskSectorSize", 256));
/*      */       
/*  967 */       mysum[0] = (byte)(this.lastChecksum >> 8 & 0xFF);
/*  968 */       mysum[1] = (byte)(this.lastChecksum << 0 & 0xFF);
/*      */ 
/*      */ 
/*      */       
/*  972 */       cocosum = this.protodev.comRead(2);
/*      */       
/*  974 */       if ((mysum[0] == cocosum[0] && mysum[1] == cocosum[1]) || this.config.getBoolean("ProtocolLieAboutCRC", false)) {
/*      */ 
/*      */         
/*  977 */         this.sectorsRead++;
/*      */         
/*  979 */         if (opcode == -14)
/*      */         {
/*  981 */           this.readRetries++;
/*  982 */           this.logger.warn("DoOP_REREADEX lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */ 
/*      */         
/*      */         }
/*  986 */         else if (this.config.getBoolean("LogOpCode", false))
/*      */         {
/*  988 */           this.logger.info("DoOP_READEX lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/*  997 */         result = -13;
/*      */         
/*  999 */         if (opcode == -14) {
/*      */           
/* 1001 */           this.readRetries++;
/* 1002 */           this.logger.warn("DoOP_REREADEX CRC check failed, lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */         }
/*      */         else {
/*      */           
/* 1006 */           this.logger.warn("DoOP_READEX CRC check failed, lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */ 
/*      */           
/*      */           try {
/* 1010 */             this.diskDrives.getDisk(this.lastDrive).incParam("_read_errors");
/*      */           }
/* 1012 */           catch (DWDriveNotLoadedException e) {
/*      */             
/* 1014 */             this.logger.warn(e.getMessage());
/*      */           }
/* 1016 */           catch (DWDriveNotValidException e) {
/*      */             
/* 1018 */             this.logger.warn(e.getMessage());
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1028 */     this.protodev.comWrite1(result, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_STAT(byte opcode) throws IOException, DWCommTimeOutException {
/* 1035 */     byte[] responsebuf = new byte[2];
/*      */ 
/*      */ 
/*      */     
/* 1039 */     responsebuf = this.protodev.comRead(2);
/*      */     
/* 1041 */     this.lastDrive = responsebuf[0] & 0xFF;
/*      */ 
/*      */     
/* 1044 */     if (opcode == 71) {
/*      */       
/* 1046 */       this.lastGetStat = responsebuf[1];
/* 1047 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1049 */         this.logger.info("DoOP_GETSTAT: " + DWUtils.prettySS(responsebuf[1]) + " lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1054 */       this.lastSetStat = responsebuf[1];
/* 1055 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1057 */         this.logger.info("DoOP_SETSTAT " + DWUtils.prettySS(responsebuf[1]) + " lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_TIME() {
/* 1065 */     GregorianCalendar c = (GregorianCalendar)Calendar.getInstance();
/* 1066 */     byte[] buf = new byte[7];
/*      */     
/* 1068 */     buf[0] = (byte)(c.get(1) - 108);
/* 1069 */     buf[1] = (byte)(c.get(2) + 1);
/* 1070 */     buf[2] = (byte)c.get(5);
/* 1071 */     buf[3] = (byte)c.get(11);
/* 1072 */     buf[4] = (byte)c.get(12);
/* 1073 */     buf[5] = (byte)c.get(13);
/* 1074 */     buf[6] = (byte)c.get(7);
/*      */ 
/*      */     
/* 1077 */     if (this.config.getBoolean("OpTimeSendsDOW", false)) {
/*      */       
/* 1079 */       this.protodev.comWrite(buf, 7, true);
/*      */     }
/*      */     else {
/*      */       
/* 1083 */       this.protodev.comWrite(buf, 6, true);
/*      */     } 
/*      */ 
/*      */     
/* 1087 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/* 1089 */       this.logger.info("DoOP_TIME");
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
/*      */   private void DoOP_SERGETSTAT() throws IOException, DWCommTimeOutException {
/* 1101 */     byte[] responsebuf = new byte[2];
/*      */ 
/*      */ 
/*      */     
/* 1105 */     responsebuf = this.protodev.comRead(2);
/* 1106 */     if (responsebuf[1] != 1)
/*      */     {
/* 1108 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1110 */         this.logger.info("DoOP_SERGETSTAT: " + DWUtils.prettySS(responsebuf[1]) + " port: " + responsebuf[0] + "(" + this.dwVSerialPorts.prettyPort(responsebuf[0]) + ")");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_SERSETSTAT() throws IOException, DWCommTimeOutException {
/* 1119 */     byte[] responsebuf = new byte[2];
/*      */ 
/*      */     
/*      */     try {
/*      */       byte[] devdescr;
/*      */ 
/*      */       
/* 1126 */       responsebuf = this.protodev.comRead(2);
/*      */       
/* 1128 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1130 */         this.logger.info("DoOP_SERSETSTAT: " + DWUtils.prettySS(responsebuf[1]) + " port: " + responsebuf[0] + "(" + this.dwVSerialPorts.prettyPort(responsebuf[0]) + ")");
/*      */       }
/*      */       
/* 1133 */       if (!this.dwVSerialPorts.isValid(responsebuf[0])) {
/*      */         
/* 1135 */         this.logger.debug("Invalid port '" + responsebuf[0] + "' in sersetstat, ignored.");
/*      */         
/*      */         return;
/*      */       } 
/* 1139 */       switch (responsebuf[1]) {
/*      */ 
/*      */         
/*      */         case 40:
/* 1143 */           devdescr = new byte[26];
/* 1144 */           devdescr = this.protodev.comRead(26);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1151 */           this.dwVSerialPorts.setDD(responsebuf[0], devdescr);
/*      */ 
/*      */           
/* 1154 */           if (this.dwVSerialPorts.getPD_INT(responsebuf[0]) != devdescr[16])
/*      */           {
/* 1156 */             this.dwVSerialPorts.setPD_INT(responsebuf[0], devdescr[16]);
/*      */           }
/*      */ 
/*      */           
/* 1160 */           if (this.dwVSerialPorts.getPD_QUT(responsebuf[0]) != devdescr[17])
/*      */           {
/* 1162 */             this.dwVSerialPorts.setPD_QUT(responsebuf[0], devdescr[17]);
/*      */           }
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 41:
/* 1170 */           this.dwVSerialPorts.openPort(responsebuf[0]);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 42:
/* 1175 */           this.dwVSerialPorts.closePort(responsebuf[0]);
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/* 1181 */     } catch (DWPortNotValidException e) {
/*      */       
/* 1183 */       this.logger.error(e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_SERINIT() throws IOException, DWCommTimeOutException, DWPortNotValidException {
/* 1190 */     byte[] responsebuf = new byte[2];
/*      */ 
/*      */ 
/*      */     
/* 1194 */     responsebuf = this.protodev.comRead(1);
/*      */     
/* 1196 */     int portnum = responsebuf[0];
/*      */ 
/*      */ 
/*      */     
/* 1200 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/* 1202 */       this.logger.info("DoOP_SERINIT for port " + this.dwVSerialPorts.prettyPort(portnum));
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
/*      */   private void DoOP_SERTERM() throws IOException, DWCommTimeOutException, DWPortNotValidException {
/* 1214 */     int portnum = this.protodev.comRead1(true);
/*      */ 
/*      */ 
/*      */     
/* 1218 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/* 1220 */       this.logger.info("DoOP_SERTERM for port " + portnum);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_SERREAD() {
/* 1228 */     byte[] result = new byte[2];
/*      */     
/* 1230 */     result = this.dwVSerialPorts.serRead();
/*      */     
/* 1232 */     this.protodev.comWrite(result, 2, true);
/*      */ 
/*      */     
/* 1235 */     if (this.config.getBoolean("LogOpCodePolls", false))
/*      */     {
/* 1237 */       this.logger.info("DoOP_SERREAD response " + (result[0] & 0xFF) + ":" + (result[1] & 0xFF));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void DoOP_SERWRITE() throws IOException, DWCommTimeOutException {
/* 1243 */     byte[] cmdpacket = new byte[2];
/*      */     
/*      */     try {
/* 1246 */       cmdpacket = this.protodev.comRead(2);
/*      */       
/* 1248 */       this.dwVSerialPorts.serWrite(cmdpacket[0], cmdpacket[1]);
/*      */       
/* 1250 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1252 */         this.logger.debug("DoOP_SERWRITE to port " + cmdpacket[0]);
/*      */       
/*      */       }
/*      */     }
/* 1256 */     catch (DWPortNotOpenException e1) {
/*      */       
/* 1258 */       this.logger.error(e1.getMessage());
/*      */     }
/* 1260 */     catch (DWPortNotValidException e2) {
/*      */       
/* 1262 */       this.logger.error(e2.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_SERREADM() throws IOException, DWCommTimeOutException {
/* 1269 */     byte[] cmdpacket = new byte[2];
/* 1270 */     byte[] data = new byte[256];
/*      */     
/*      */     try {
/* 1273 */       cmdpacket = this.protodev.comRead(2);
/*      */       
/* 1275 */       if (this.config.getBoolean("LogOpCode", false)) {
/*      */         
/*      */         try {
/*      */           
/* 1279 */           this.logger.info("DoOP_SERREADM for " + (cmdpacket[1] & 0xFF) + " bytes on port " + cmdpacket[0] + " (" + this.dwVSerialPorts.getPortOutput(cmdpacket[0]).available() + " bytes in buffer)");
/*      */         }
/* 1281 */         catch (IOException e) {
/*      */           
/* 1283 */           this.logger.warn("While logging: " + e.getMessage());
/*      */         } 
/*      */       }
/*      */       
/* 1287 */       data = this.dwVSerialPorts.serReadM(cmdpacket[0], cmdpacket[1] & 0xFF);
/*      */       
/* 1289 */       this.protodev.comWrite(data, cmdpacket[1] & 0xFF, true);
/*      */     
/*      */     }
/* 1292 */     catch (DWPortNotOpenException e1) {
/*      */       
/* 1294 */       this.logger.error(e1.getMessage());
/*      */     }
/* 1296 */     catch (DWPortNotValidException e2) {
/*      */       
/* 1298 */       this.logger.error(e2.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_SERWRITEM() throws IOException, DWCommTimeOutException {
/* 1306 */     byte[] cmdpacket = new byte[2];
/*      */     
/*      */     try {
/* 1309 */       cmdpacket = this.protodev.comRead(2);
/*      */       
/* 1311 */       byte[] data = new byte[cmdpacket[1]];
/*      */       
/* 1313 */       data = this.protodev.comRead(cmdpacket[1]);
/*      */       
/* 1315 */       this.dwVSerialPorts.serWriteM(cmdpacket[0], data);
/*      */       
/* 1317 */       if (this.config.getBoolean("LogOpCode", false))
/*      */       {
/* 1319 */         this.logger.debug("DoOP_SERWRITEM to port " + cmdpacket[0] + ", " + cmdpacket[1] + " bytes");
/*      */ 
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 1325 */     catch (DWPortNotOpenException e1) {
/*      */       
/* 1327 */       this.logger.error(e1.getMessage());
/*      */     }
/* 1329 */     catch (DWPortNotValidException e2) {
/*      */       
/* 1331 */       this.logger.error(e2.getMessage());
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
/*      */   private void DoOP_PRINT() throws IOException, DWCommTimeOutException {
/* 1345 */     int tmpint = this.protodev.comRead1(true);
/*      */     
/* 1347 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/* 1349 */       this.logger.info("DoOP_PRINT: byte " + tmpint);
/*      */     }
/*      */     
/* 1352 */     this.vprinter.addByte((byte)tmpint);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void DoOP_PRINTFLUSH() {
/* 1358 */     if (this.config.getBoolean("LogOpCode", false))
/*      */     {
/* 1360 */       this.logger.info("DoOP_PRINTFLUSH");
/*      */     }
/*      */     
/* 1363 */     this.vprinter.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int computeChecksum(byte[] data, int numbytes) {
/* 1373 */     int lastChecksum = 0;
/*      */ 
/*      */     
/* 1376 */     while (numbytes > 0) {
/*      */       
/* 1378 */       numbytes--;
/* 1379 */       lastChecksum += data[numbytes] & 0xFF;
/*      */     } 
/*      */     
/* 1382 */     return lastChecksum;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLastDrive() {
/* 1389 */     return this.lastDrive;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getReadRetries() {
/* 1394 */     return this.readRetries;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getWriteRetries() {
/* 1399 */     return this.writeRetries;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSectorsRead() {
/* 1404 */     return this.sectorsRead;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSectorsWritten() {
/* 1409 */     return this.sectorsWritten;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getLastOpcode() {
/* 1414 */     return this.lastOpcode;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getLastGetStat() {
/* 1419 */     return this.lastGetStat;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getLastSetStat() {
/* 1424 */     return this.lastSetStat;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getLastChecksum() {
/* 1429 */     return this.lastChecksum;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getLastError() {
/* 1434 */     return this.lastError;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte[] getLastLSN() {
/* 1439 */     return this.lastLSN;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GregorianCalendar getInitTime() {
/* 1446 */     return this.dwinitTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DWDiskDrives getDiskDrives() {
/* 1453 */     return this.diskDrives;
/*      */   }
/*      */   
/*      */   public DWVSerialPorts getVPorts() {
/* 1457 */     return this.dwVSerialPorts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDying() {
/* 1465 */     return this.wanttodie;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DWProtocolDevice getProtoDev() {
/* 1472 */     return this.protodev;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void resetProtocolDevice() {
/* 1481 */     if (!this.wanttodie) {
/*      */       
/* 1483 */       this.logger.info("requesting protocol device reset");
/*      */ 
/*      */       
/* 1486 */       this.resetPending = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setupProtocolDevice() {
/* 1497 */     if (this.protodev != null && !this.resetPending) {
/* 1498 */       this.protodev.shutdown();
/*      */     }
/*      */ 
/*      */     
/* 1502 */     if (this.config.getString("DeviceType", "dummy").equalsIgnoreCase("dummy")) {
/*      */       
/* 1504 */       this.resetPending = false;
/*      */     }
/* 1506 */     else if (this.config.getString("DeviceType").equalsIgnoreCase("serial")) {
/*      */ 
/*      */ 
/*      */       
/* 1510 */       if (this.config.containsKey("SerialDevice") && this.config.containsKey("SerialRate")) {
/*      */         
/*      */         try
/*      */         {
/* 1514 */           this.protodev = new DWSerialDevice(this);
/* 1515 */           this.resetPending = false;
/*      */         }
/* 1517 */         catch (NoSuchPortException e1)
/*      */         {
/*      */           
/* 1520 */           this.logger.error("handler #" + this.handlerno + ": Serial device '" + this.config.getString("SerialDevice") + "' not found");
/*      */         }
/* 1522 */         catch (PortInUseException e2)
/*      */         {
/*      */           
/* 1525 */           this.logger.error("handler #" + this.handlerno + ": Serial device '" + this.config.getString("SerialDevice") + "' in use");
/*      */         }
/* 1527 */         catch (UnsupportedCommOperationException e3)
/*      */         {
/*      */           
/* 1530 */           this.logger.error("handler #" + this.handlerno + ": Unsupported comm operation while opening serial port '" + this.config.getString("SerialDevice") + "'");
/*      */         }
/* 1532 */         catch (IOException e)
/*      */         {
/*      */           
/* 1535 */           this.logger.error("handler #" + this.handlerno + ": IO exception while opening serial port '" + this.config.getString("SerialDevice") + "'");
/*      */         }
/* 1537 */         catch (TooManyListenersException e)
/*      */         {
/*      */           
/* 1540 */           this.logger.error("handler #" + this.handlerno + ": Too many listeneres while opening serial port '" + this.config.getString("SerialDevice") + "'");
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1545 */         this.logger.error("Serial mode requires both SerialDevice and SerialRate to be set, please configure this instance.");
/*      */       }
/*      */     
/*      */     }
/* 1549 */     else if (this.config.getString("DeviceType").equalsIgnoreCase("tcp") || this.config.getString("DeviceType").equalsIgnoreCase("tcp-server")) {
/*      */ 
/*      */       
/* 1552 */       if (this.config.containsKey("TCPServerPort")) {
/*      */         
/*      */         try
/*      */         {
/* 1556 */           this.protodev = new DWTCPDevice(this.handlerno, this.config.getInt("TCPServerPort"));
/*      */         }
/* 1558 */         catch (IOException e)
/*      */         {
/*      */           
/* 1561 */           this.logger.error("handler #" + this.handlerno + ": " + e.getMessage());
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1566 */         this.logger.error("TCP server mode requires TCPServerPort to be set, cannot use this configuration");
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 1571 */     else if (this.config.getString("DeviceType").equalsIgnoreCase("tcp-client")) {
/*      */ 
/*      */       
/* 1574 */       if (this.config.containsKey("TCPClientPort") && this.config.containsKey("TCPClientHost")) {
/*      */         
/*      */         try
/*      */         {
/* 1578 */           this.protodev = new DWTCPClientDevice(this.handlerno, this.config.getString("TCPClientHost"), this.config.getInt("TCPClientPort"));
/*      */         }
/* 1580 */         catch (IOException e)
/*      */         {
/*      */           
/* 1583 */           this.logger.error("handler #" + this.handlerno + ": " + e.getMessage());
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1588 */         this.logger.error("TCP client mode requires TCPClientPort and TCPClientHost to be set, cannot use this configuration");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getStatusText() {
/* 1599 */     String text = new String();
/*      */     
/* 1601 */     text = text + "Last OpCode:   " + DWUtils.prettyOP(getLastOpcode()) + "\r\n";
/* 1602 */     text = text + "Last GetStat:  " + DWUtils.prettySS(getLastGetStat()) + "\r\n";
/* 1603 */     text = text + "Last SetStat:  " + DWUtils.prettySS(getLastSetStat()) + "\r\n";
/* 1604 */     text = text + "Last Drive:    " + getLastDrive() + "\r\n";
/* 1605 */     text = text + "Last LSN:      " + DWUtils.int3(getLastLSN()) + "\r\n";
/* 1606 */     text = text + "Last Error:    " + (getLastError() & 0xFF) + "\r\n";
/*      */     
/* 1608 */     text = text + "\r\n";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1616 */     return text;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/* 1624 */     return this.config.getString("[@name]", "Unnamed #" + this.handlerno);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHandlerNo() {
/* 1630 */     return this.handlerno;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void syncStorage() {
/* 1637 */     if (isInOp() && this.syncSkipped < 1) {
/*      */       
/* 1639 */       this.logger.debug("Ignoring sync request because we are processing a protocol operation (" + (this.syncSkipped + 1) + " of " + '\001' + ")");
/* 1640 */       this.syncSkipped++;
/*      */     }
/* 1642 */     else if (this.diskDrives != null) {
/*      */       
/* 1644 */       this.diskDrives.sync();
/* 1645 */       this.syncSkipped = 0;
/*      */     }
/*      */     else {
/*      */       
/* 1649 */       this.logger.debug("handler is alive, but disk drive object is null, probably startup taking a while.. skipping");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public DWVPrinter getVPrinter() {
/* 1656 */     return this.vprinter;
/*      */   }
/*      */ 
/*      */   
/*      */   public Logger getLogger() {
/* 1661 */     return this.logger;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCMDCols() {
/* 1667 */     return getConfig().getInt("DWCommandOutputWidth", 80);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public DWHelp getHelp() {
/* 1673 */     return this.dwhelp;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReady() {
/* 1680 */     return this.ready;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void submitConfigEvent(String key, String val) {
/* 1687 */     DriveWireServer.submitInstanceConfigEvent(this.handlerno, key, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getNumOps() {
/* 1695 */     return this.total_ops;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getNumDiskOps() {
/* 1703 */     return this.disk_ops;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getNumVSerialOps() {
/* 1711 */     return this.vserial_ops;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isInOp() {
/* 1716 */     return this.inOp;
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWProtocolHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */