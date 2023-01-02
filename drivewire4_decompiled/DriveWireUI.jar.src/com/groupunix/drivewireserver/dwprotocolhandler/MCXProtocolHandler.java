/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import com.groupunix.drivewireserver.dwhelp.DWHelp;
/*     */ import com.groupunix.drivewireserver.virtualprinter.DWVPrinter;
/*     */ import gnu.io.NoSuchPortException;
/*     */ import gnu.io.PortInUseException;
/*     */ import gnu.io.UnsupportedCommOperationException;
/*     */ import java.io.IOException;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.TooManyListenersException;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MCXProtocolHandler
/*     */   implements Runnable, DWProtocol
/*     */ {
/*  31 */   private final Logger logger = Logger.getLogger("DWServer.MCXProtocolHandler");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   private byte lastDrive = 0;
/*  37 */   private int readRetries = 0;
/*  38 */   private int writeRetries = 0;
/*  39 */   private int sectorsRead = 0;
/*  40 */   private int sectorsWritten = 0;
/*  41 */   private byte lastOpcode = -1;
/*  42 */   private int lastChecksum = 0;
/*  43 */   private int lastError = 0;
/*  44 */   private byte[] lastLSN = new byte[3];
/*     */   
/*  46 */   private GregorianCalendar dwinitTime = new GregorianCalendar();
/*     */ 
/*     */   
/*     */   private DWProtocolDevice protodev;
/*     */ 
/*     */   
/*     */   private DWVPrinter vprinter;
/*     */ 
/*     */   
/*     */   private DWDiskDrives diskDrives;
/*     */ 
/*     */   
/*     */   private boolean wanttodie = false;
/*     */ 
/*     */   
/*     */   private int handlerno;
/*     */ 
/*     */   
/*     */   private HierarchicalConfiguration config;
/*     */ 
/*     */   
/*     */   public MCXProtocolHandler(int handlerno, HierarchicalConfiguration hconf) {
/*  68 */     this.handlerno = handlerno;
/*  69 */     this.config = hconf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HierarchicalConfiguration getConfig() {
/*  78 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*  84 */     DoOP_RESET();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean connected() {
/*  90 */     return this.protodev.connected();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/*  97 */     this.logger.info("handler #" + this.handlerno + ": shutdown requested");
/*     */     
/*  99 */     this.wanttodie = true;
/* 100 */     this.protodev.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 106 */     int opcodeint = -1;
/* 107 */     int alertcodeint = -1;
/*     */     
/* 109 */     Thread.currentThread().setName("mcxproto-" + this.handlerno + "-" + Thread.currentThread().getId());
/*     */     
/* 111 */     Thread.currentThread().setPriority(10);
/*     */     
/* 113 */     setupProtocolDevice();
/*     */ 
/*     */     
/* 116 */     if (!this.wanttodie) {
/*     */       
/* 118 */       this.logger.info("handler #" + this.handlerno + ": starting...");
/*     */ 
/*     */ 
/*     */       
/* 122 */       this.vprinter = new DWVPrinter(this);
/*     */     } 
/*     */ 
/*     */     
/* 126 */     this.logger.info("handler #" + this.handlerno + ": ready");
/*     */ 
/*     */     
/* 129 */     while (!this.wanttodie) {
/*     */ 
/*     */ 
/*     */       
/* 133 */       if (this.protodev != null) {
/*     */ 
/*     */         
/*     */         try {
/* 137 */           alertcodeint = this.protodev.comRead1(false);
/* 138 */           opcodeint = this.protodev.comRead1(false);
/*     */ 
/*     */           
/* 141 */           if (alertcodeint == 33) {
/*     */ 
/*     */             
/* 144 */             switch (opcodeint) {
/*     */               
/*     */               case 70:
/* 147 */                 DoOP_DIRFILEREQUEST();
/*     */                 continue;
/*     */               
/*     */               case 68:
/* 151 */                 DoOP_DIRNAMEREQUEST();
/*     */                 continue;
/*     */               
/*     */               case 71:
/* 155 */                 DoOP_GETDATABLOCK();
/*     */                 continue;
/*     */               
/*     */               case 76:
/* 159 */                 DoOP_LOADFILE();
/*     */                 continue;
/*     */               
/*     */               case 79:
/* 163 */                 DoOP_OPENDATAFILE();
/*     */                 continue;
/*     */               
/*     */               case 78:
/* 167 */                 DoOP_PREPARENEXTBLOCK();
/*     */                 continue;
/*     */               
/*     */               case 36:
/* 171 */                 DoOP_RETRIEVENAME();
/*     */                 continue;
/*     */               
/*     */               case 83:
/* 175 */                 DoOP_SAVEFILE();
/*     */                 continue;
/*     */               
/*     */               case 67:
/* 179 */                 DoOP_SETCURRENTDIR();
/*     */                 continue;
/*     */               
/*     */               case 87:
/* 183 */                 DoOP_WRITEBLOCK();
/*     */                 continue;
/*     */             } 
/*     */             
/* 187 */             this.logger.warn("UNKNOWN OPCODE: " + opcodeint);
/*     */ 
/*     */             
/*     */             continue;
/*     */           } 
/*     */ 
/*     */           
/* 194 */           this.logger.warn("Got non alert code when expected alert code: " + alertcodeint);
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 199 */         catch (IOException e) {
/*     */ 
/*     */           
/* 202 */           this.logger.error(e.getMessage());
/* 203 */           opcodeint = -1;
/* 204 */         } catch (DWCommTimeOutException e) {
/*     */ 
/*     */           
/* 207 */           e.printStackTrace();
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 212 */       this.logger.debug("cannot access the device.. maybe it has not been configured or maybe it does not exist");
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 217 */         Thread.sleep(this.config.getInt("FailedPortRetryTime", 1000));
/* 218 */         resetProtocolDevice();
/*     */       
/*     */       }
/* 221 */       catch (InterruptedException e) {
/*     */         
/* 223 */         this.logger.error("Interrupted during failed port delay.. giving up on this situation");
/* 224 */         this.wanttodie = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 232 */     this.logger.info("handler #" + this.handlerno + ": exiting");
/*     */ 
/*     */ 
/*     */     
/* 236 */     if (this.diskDrives != null)
/*     */     {
/* 238 */       this.diskDrives.shutdown();
/*     */     }
/*     */ 
/*     */     
/* 242 */     if (this.protodev != null)
/*     */     {
/* 244 */       this.protodev.shutdown();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_LOADFILE() {
/* 260 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 262 */       this.logger.info("DoOP_LOADFILE");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_GETDATABLOCK() {
/* 269 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 271 */       this.logger.info("DoOP_GETDATABLOCK");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void DoOP_PREPARENEXTBLOCK() {
/* 277 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 279 */       this.logger.info("DoOP_PREPARENEXTBLOCK");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void DoOP_SAVEFILE() {
/* 285 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 287 */       this.logger.info("DoOP_SAVEFILE");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void DoOP_WRITEBLOCK() {
/* 293 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 295 */       this.logger.info("DoOP_WRITEBLOCK");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void DoOP_OPENDATAFILE() {
/* 301 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 303 */       this.logger.info("DoOP_OPENDATAFILE");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_DIRFILEREQUEST() {
/* 311 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 313 */       this.logger.info("DoOP_DIRFILEREQUEST");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 320 */       int flag = this.protodev.comRead1(true);
/*     */ 
/*     */       
/* 323 */       int arglen = this.protodev.comRead1(true);
/*     */ 
/*     */       
/* 326 */       byte[] buf = new byte[arglen];
/* 327 */       buf = this.protodev.comRead(arglen);
/*     */       
/* 329 */       this.logger.debug("DIRFILEREQUEST fl: " + flag + "  arg: " + new String(buf));
/*     */ 
/*     */       
/* 332 */       if (flag == 0)
/*     */       {
/* 334 */         this.protodev.comWrite1(0, false);
/* 335 */         this.protodev.comWrite1(4, false);
/*     */       }
/*     */       else
/*     */       {
/* 339 */         this.protodev.comWrite1(0, false);
/* 340 */         this.protodev.comWrite1(0, false);
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 345 */     catch (IOException e) {
/*     */       
/* 347 */       this.logger.warn(e.getMessage());
/* 348 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 351 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RETRIEVENAME() {
/* 361 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 363 */       this.logger.info("DoOP_RETRIEVENAME");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 368 */       int arglen = this.protodev.comRead1(true);
/*     */       
/* 370 */       if (arglen == 4)
/*     */       {
/* 372 */         this.protodev.comWrite1(84, false);
/* 373 */         this.protodev.comWrite1(101, false);
/* 374 */         this.protodev.comWrite1(115, false);
/* 375 */         this.protodev.comWrite1(50, false);
/*     */       }
/*     */     
/*     */     }
/* 379 */     catch (IOException e) {
/*     */       
/* 381 */       this.logger.warn(e.getMessage());
/* 382 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 385 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_DIRNAMEREQUEST() {
/* 392 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 394 */       this.logger.info("DoOP_DIRNAMEREQUEST");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void DoOP_SETCURRENTDIR() {
/* 400 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 402 */       this.logger.info("DoOP_SETCURRENTDIR");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RESET() {
/* 416 */     this.lastDrive = 0;
/* 417 */     this.readRetries = 0;
/* 418 */     this.writeRetries = 0;
/* 419 */     this.sectorsRead = 0;
/* 420 */     this.sectorsWritten = 0;
/* 421 */     this.lastOpcode = -1;
/* 422 */     this.lastChecksum = 0;
/* 423 */     this.lastError = 0;
/*     */     
/* 425 */     this.lastLSN = new byte[3];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 430 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 432 */       this.logger.info("DoOP_RESET");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_WRITE(byte opcode) {
/* 442 */     byte[] cocosum = new byte[2];
/* 443 */     byte[] responsebuf = new byte[262];
/* 444 */     byte response = 0;
/* 445 */     byte[] sector = new byte[256];
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 450 */       responsebuf = this.protodev.comRead(262);
/*     */       
/* 452 */       this.lastDrive = responsebuf[0];
/* 453 */       System.arraycopy(responsebuf, 1, this.lastLSN, 0, 3);
/* 454 */       System.arraycopy(responsebuf, 4, sector, 0, 256);
/* 455 */       System.arraycopy(responsebuf, 260, cocosum, 0, 2);
/*     */ 
/*     */ 
/*     */       
/* 459 */       this.lastChecksum = computeChecksum(sector, 256);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 464 */     catch (IOException e1) {
/*     */ 
/*     */       
/* 467 */       this.logger.error("DoOP_WRITE error: " + e1.getMessage());
/*     */ 
/*     */       
/*     */       return;
/* 471 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 474 */       e.printStackTrace();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 480 */     if (this.lastChecksum != DWUtils.int2(cocosum)) {
/*     */ 
/*     */       
/* 483 */       this.protodev.comWrite1(-13, false);
/*     */       
/* 485 */       this.logger.warn("DoOP_WRITE: Bad checksum, drive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN) + " CocoSum: " + DWUtils.int2(cocosum) + " ServerSum: " + this.lastChecksum);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 492 */     response = 0;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 497 */       this.diskDrives.seekSector(this.lastDrive, DWUtils.int3(this.lastLSN));
/*     */       
/* 499 */       this.diskDrives.writeSector(this.lastDrive, sector);
/*     */     }
/* 501 */     catch (DWDriveNotLoadedException e1) {
/*     */ 
/*     */       
/* 504 */       response = -10;
/* 505 */       this.logger.warn(e1.getMessage());
/*     */     }
/* 507 */     catch (DWDriveNotValidException e2) {
/*     */ 
/*     */       
/* 510 */       response = -10;
/* 511 */       this.logger.warn(e2.getMessage());
/*     */     }
/* 513 */     catch (DWDriveWriteProtectedException e3) {
/*     */ 
/*     */       
/* 516 */       response = -14;
/* 517 */       this.logger.warn(e3.getMessage());
/*     */     }
/* 519 */     catch (IOException e4) {
/*     */ 
/*     */       
/* 522 */       response = -11;
/* 523 */       this.logger.error(e4.getMessage());
/*     */     }
/* 525 */     catch (DWInvalidSectorException e5) {
/*     */       
/* 527 */       response = -11;
/* 528 */       this.logger.warn(e5.getMessage());
/*     */     }
/* 530 */     catch (DWSeekPastEndOfDeviceException e6) {
/*     */       
/* 532 */       response = -11;
/* 533 */       this.logger.warn(e6.getMessage());
/*     */     } 
/*     */ 
/*     */     
/* 537 */     if (response != 0) {
/* 538 */       this.lastError = response;
/*     */     }
/*     */     
/* 541 */     this.protodev.comWrite1(response, false);
/*     */ 
/*     */     
/* 544 */     if (response == 0) {
/* 545 */       this.sectorsWritten++;
/*     */     }
/* 547 */     if (opcode == 119) {
/*     */       
/* 549 */       this.writeRetries++;
/* 550 */       if (this.config.getBoolean("LogOpCode", false))
/*     */       {
/* 552 */         this.logger.info("DoOP_REWRITE lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 557 */     else if (this.config.getBoolean("LogOpCode", false)) {
/*     */       
/* 559 */       this.logger.info("DoOP_WRITE lastDrive: " + this.lastDrive + " LSN: " + DWUtils.int3(this.lastLSN));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_PRINT() {
/*     */     try {
/* 580 */       int tmpint = this.protodev.comRead1(true);
/*     */       
/* 582 */       if (this.config.getBoolean("LogOpCode", false))
/*     */       {
/* 584 */         this.logger.info("DoOP_PRINT: byte " + tmpint);
/*     */       }
/*     */       
/* 587 */       this.vprinter.addByte((byte)tmpint);
/*     */     }
/* 589 */     catch (IOException e) {
/*     */       
/* 591 */       this.logger.error("IO exception reading print byte: " + e.getMessage());
/* 592 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 595 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_PRINTFLUSH() {
/* 604 */     if (this.config.getBoolean("LogOpCode", false))
/*     */     {
/* 606 */       this.logger.info("DoOP_PRINTFLUSH");
/*     */     }
/*     */     
/* 609 */     this.vprinter.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int computeChecksum(byte[] data, int numbytes) {
/* 619 */     int lastChecksum = 0;
/*     */ 
/*     */     
/* 622 */     while (numbytes > 0) {
/*     */       
/* 624 */       numbytes--;
/* 625 */       lastChecksum += data[numbytes] & 0xFF;
/*     */     } 
/*     */     
/* 628 */     return lastChecksum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getLastDrive() {
/* 635 */     return this.lastDrive;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReadRetries() {
/* 640 */     return this.readRetries;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWriteRetries() {
/* 645 */     return this.writeRetries;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSectorsRead() {
/* 650 */     return this.sectorsRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSectorsWritten() {
/* 655 */     return this.sectorsWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getLastOpcode() {
/* 660 */     return this.lastOpcode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLastChecksum() {
/* 667 */     return this.lastChecksum;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLastError() {
/* 672 */     return this.lastError;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getLastLSN() {
/* 677 */     return this.lastLSN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GregorianCalendar getInitTime() {
/* 684 */     return this.dwinitTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDying() {
/* 691 */     return this.wanttodie;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWProtocolDevice getProtoDev() {
/* 699 */     return this.protodev;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetProtocolDevice() {
/* 708 */     if (!this.wanttodie) {
/*     */       
/* 710 */       this.logger.warn("resetting protocol device");
/*     */       
/* 712 */       setupProtocolDevice();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setupProtocolDevice() {
/* 720 */     if (this.protodev != null) {
/* 721 */       this.protodev.shutdown();
/*     */     }
/* 723 */     if (this.config.getString("DeviceType", "serial").equalsIgnoreCase("serial")) {
/*     */ 
/*     */ 
/*     */       
/* 727 */       if (this.config.containsKey("SerialDevice")) {
/*     */ 
/*     */         
/*     */         try {
/*     */           
/* 732 */           this.protodev = new DWSerialDevice(this);
/*     */         }
/* 734 */         catch (NoSuchPortException e1) {
/*     */ 
/*     */           
/* 737 */           this.logger.error("handler #" + this.handlerno + ": Serial device '" + this.config.getString("SerialDevice") + "' not found");
/*     */         }
/* 739 */         catch (PortInUseException e2) {
/*     */ 
/*     */           
/* 742 */           this.logger.error("handler #" + this.handlerno + ": Serial device '" + this.config.getString("SerialDevice") + "' in use");
/*     */         }
/* 744 */         catch (UnsupportedCommOperationException e3) {
/*     */ 
/*     */           
/* 747 */           this.logger.error("handler #" + this.handlerno + ": Unsupported comm operation while opening serial port '" + this.config.getString("SerialDevice") + "'");
/* 748 */         } catch (IOException e) {
/*     */ 
/*     */           
/* 751 */           e.printStackTrace();
/* 752 */         } catch (TooManyListenersException e) {
/*     */ 
/*     */           
/* 755 */           e.printStackTrace();
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 760 */         this.logger.error("Serial mode requires SerialDevice to be set, cannot use this configuration");
/*     */       }
/*     */     
/*     */     }
/* 764 */     else if (this.config.getString("DeviceType").equalsIgnoreCase("tcp")) {
/*     */ 
/*     */       
/* 767 */       if (this.config.containsKey("TCPDevicePort")) {
/*     */         
/*     */         try
/*     */         {
/* 771 */           this.protodev = new DWTCPDevice(this.handlerno, this.config.getInt("TCPDevicePort"));
/*     */         }
/* 773 */         catch (IOException e)
/*     */         {
/*     */           
/* 776 */           this.logger.error("handler #" + this.handlerno + ": " + e.getMessage());
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 781 */         this.logger.error("TCP mode requires TCPDevicePort to be set, cannot use this configuration");
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 786 */     else if (this.config.getString("DeviceType").equalsIgnoreCase("tcp-client")) {
/*     */ 
/*     */       
/* 789 */       if (this.config.containsKey("TCPClientPort") && this.config.containsKey("TCPClientHost")) {
/*     */         
/*     */         try
/*     */         {
/* 793 */           this.protodev = new DWTCPClientDevice(this.handlerno, this.config.getString("TCPClientHost"), this.config.getInt("TCPClientPort"));
/*     */         }
/* 795 */         catch (IOException e)
/*     */         {
/*     */           
/* 798 */           this.logger.error("handler #" + this.handlerno + ": " + e.getMessage());
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 803 */         this.logger.error("TCP mode requires TCPClientPort and TCPClientHost to be set, cannot use this configuration");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStatusText() {
/* 814 */     String text = new String();
/*     */     
/* 816 */     text = text + "Last OpCode:   " + DWUtils.prettyOP(getLastOpcode()) + "\r\n";
/* 817 */     text = text + "Last Drive:    " + getLastDrive() + "\r\n";
/* 818 */     text = text + "Last LSN:      " + getLastLSN() + "\r\n";
/* 819 */     text = text + "Last Error:    " + (getLastError() & 0xFF) + "\r\n";
/*     */     
/* 821 */     return text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void syncStorage() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHandlerNo() {
/* 839 */     return this.handlerno;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getLogger() {
/* 846 */     return this.logger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCMDCols() {
/* 853 */     return 32;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWHelp getHelp() {
/* 860 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 867 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitConfigEvent(String propertyName, String string) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNumOps() {
/* 883 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNumDiskOps() {
/* 891 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNumVSerialOps() {
/* 899 */     return 0L;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/MCXProtocolHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */