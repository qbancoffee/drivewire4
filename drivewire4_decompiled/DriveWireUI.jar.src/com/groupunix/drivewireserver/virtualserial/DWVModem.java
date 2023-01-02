/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVModem
/*     */ {
/*  14 */   private static final Logger logger = Logger.getLogger("DWServer.DWVModem");
/*     */   
/*     */   private int vport;
/*     */   
/*     */   private boolean vmodem_echo = false;
/*     */   
/*     */   private boolean vmodem_verbose = true;
/*     */   private boolean vmodem_quiet = false;
/*  22 */   private int[] vmodem_registers = new int[256];
/*  23 */   private String vmodem_lastcommand = new String();
/*  24 */   private String vmodem_dialstring = new String();
/*     */   
/*     */   private static final int RESP_OK = 0;
/*     */   
/*     */   private static final int RESP_CONNECT = 1;
/*     */   
/*     */   private static final int RESP_RING = 2;
/*     */   
/*     */   private static final int RESP_NOCARRIER = 3;
/*     */   
/*     */   private static final int RESP_ERROR = 4;
/*     */   
/*     */   private static final int RESP_NODIALTONE = 6;
/*     */   
/*     */   private static final int RESP_BUSY = 7;
/*     */   private static final int RESP_NOANSWER = 8;
/*     */   private static final int REG_ANSWERONRING = 0;
/*     */   private static final int REG_RINGS = 1;
/*     */   private static final int REG_ESCCHAR = 2;
/*     */   private static final int REG_CR = 3;
/*     */   private static final int REG_LF = 4;
/*     */   private static final int REG_BS = 5;
/*     */   private static final int REG_GUARDTIME = 12;
/*     */   private Thread tcpthread;
/*     */   private int handlerno;
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */   
/*     */   public DWVModem(DWProtocolHandler dwProto, int port) {
/*  52 */     this.vport = port;
/*  53 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */ 
/*     */     
/*  56 */     doCommandReset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void processCommand(String cmd) {
/*  63 */     int errors = 0;
/*     */ 
/*     */     
/*  66 */     if (cmd.length() == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (cmd.equalsIgnoreCase("A/")) {
/*     */       
/*  74 */       cmd = this.vmodem_lastcommand;
/*     */     }
/*     */     else {
/*     */       
/*  78 */       this.vmodem_lastcommand = cmd;
/*     */     } 
/*     */ 
/*     */     
/*  82 */     if (cmd.toUpperCase().startsWith("AT")) {
/*     */ 
/*     */       
/*  85 */       if (cmd.length() == 2) {
/*     */         
/*  87 */         sendResponse(0);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */ 
/*     */       
/*  94 */       boolean registers = false;
/*  95 */       boolean extended = false;
/*  96 */       boolean extendedpart = false;
/*  97 */       boolean dialing = false;
/*     */       
/*  99 */       String thiscmd = new String();
/* 100 */       String thisarg = new String();
/* 101 */       String thisreg = new String();
/*     */       
/* 103 */       for (int i = 2; i < cmd.length(); i++) {
/*     */         
/* 105 */         extendedpart = false;
/*     */         
/* 107 */         if (dialing) {
/*     */           
/* 109 */           thisarg = thisarg + cmd.substring(i, i + 1);
/*     */         }
/*     */         else {
/*     */           
/* 113 */           switch (cmd.toUpperCase().charAt(i)) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case '&':
/*     */             case 'B':
/*     */             case 'D':
/*     */             case 'E':
/*     */             case 'F':
/*     */             case 'I':
/*     */             case 'L':
/*     */             case 'M':
/*     */             case 'N':
/*     */             case 'Q':
/*     */             case 'S':
/*     */             case 'V':
/*     */             case 'X':
/*     */             case 'Z':
/* 132 */               if (extended)
/*     */               {
/* 134 */                 switch (cmd.toUpperCase().charAt(i)) {
/*     */                   
/*     */                   case 'F':
/*     */                   case 'V':
/* 138 */                     extendedpart = true;
/*     */                     break;
/*     */                 } 
/*     */               
/*     */               }
/* 143 */               if (cmd.toUpperCase().charAt(i) == '&') {
/* 144 */                 extended = true;
/*     */               }
/* 146 */               if (cmd.toUpperCase().charAt(i) == 'D') {
/* 147 */                 dialing = true;
/*     */               }
/* 149 */               if (extendedpart) {
/*     */                 
/* 151 */                 thiscmd = thiscmd + cmd.substring(i, i + 1);
/*     */                 
/*     */                 break;
/*     */               } 
/*     */               
/* 156 */               if (thiscmd.length() != 0)
/*     */               {
/* 158 */                 errors += doCommand(thiscmd, thisreg, thisarg);
/*     */               }
/*     */ 
/*     */               
/* 162 */               thiscmd = cmd.substring(i, i + 1);
/* 163 */               thisarg = new String();
/* 164 */               thisreg = new String();
/*     */ 
/*     */ 
/*     */               
/* 168 */               if (thiscmd.equalsIgnoreCase("S")) {
/* 169 */                 registers = true; break;
/*     */               } 
/* 171 */               registers = false;
/*     */               break;
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case '=':
/* 178 */               registers = false;
/*     */               break;
/*     */ 
/*     */             
/*     */             case '?':
/* 183 */               thisarg = "?";
/*     */               break;
/*     */ 
/*     */             
/*     */             case ' ':
/*     */               break;
/*     */ 
/*     */             
/*     */             case '0':
/*     */             case '1':
/*     */             case '2':
/*     */             case '3':
/*     */             case '4':
/*     */             case '5':
/*     */             case '6':
/*     */             case '7':
/*     */             case '8':
/*     */             case '9':
/* 201 */               if (registers) {
/*     */                 
/* 203 */                 thisreg = thisreg + cmd.substring(i, i + 1);
/*     */                 
/*     */                 break;
/*     */               } 
/* 207 */               thisarg = thisarg + cmd.substring(i, i + 1);
/*     */               break;
/*     */ 
/*     */ 
/*     */             
/*     */             default:
/* 213 */               errors++;
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         } 
/*     */       } 
/* 221 */       if (thiscmd.length() != 0)
/*     */       {
/* 223 */         errors += doCommand(thiscmd, thisreg, thisarg);
/*     */       
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 229 */       errors++;
/*     */     } 
/*     */ 
/*     */     
/* 233 */     if (errors >= 0)
/*     */     {
/* 235 */       if (errors > 0) {
/*     */         
/* 237 */         sendResponse(4);
/*     */       }
/*     */       else {
/*     */         
/* 241 */         sendResponse(0);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int doCommand(String thiscmd, String thisreg, String thisarg) {
/* 249 */     int errors = 0;
/* 250 */     int val = 0;
/* 251 */     int regval = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 257 */       val = Integer.parseInt(thisarg);
/*     */     
/*     */     }
/* 260 */     catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 267 */       regval = Integer.parseInt(thisreg);
/*     */     }
/* 269 */     catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 275 */     logger.debug("vmodem doCommand: " + thiscmd + "  reg: " + thisreg + " (" + regval + ")  arg: " + thisarg + " (" + val + ")");
/*     */     
/* 277 */     switch (thiscmd.toUpperCase().charAt(0))
/*     */     
/*     */     { 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 'B':
/*     */       case 'L':
/*     */       case 'M':
/*     */       case 'N':
/*     */       case 'X':
/* 434 */         return errors;case 'Z': doCommandReset();case 'E': if (val > 1) { errors++; } else if (val == 0) { this.vmodem_echo = false; } else { this.vmodem_echo = true; } case 'Q': if (val > 1) { errors++; } else if (val == 0) { this.vmodem_quiet = false; } else { this.vmodem_quiet = true; } case 'V': if (val > 1) { errors++; } else if (val == 0) { this.vmodem_verbose = false; } else { this.vmodem_verbose = true; } case 'I': switch (val) { case 0: write("\n\rDWVM " + this.dwVSerialPorts.prettyPort(this.vport) + "\r\n");
/*     */           case 1: case 3: write("\n\rDriveWire 4.0.7a Virtual Modem on port " + this.dwVSerialPorts.prettyPort(this.vport) + "\r\n");
/*     */           case 2: try { write("\n\rConnected to " + this.dwVSerialPorts.getHostIP(this.vport) + ":" + this.dwVSerialPorts.getHostPort(this.vport) + "\n\r"); } catch (DWPortNotValidException e) { logger.error(e.getMessage()); write(e.getMessage()); } catch (DWConnectionNotValidException e) { logger.error(e.getMessage()); write(e.getMessage()); } 
/*     */           case 4: doCommandShowProfile(); }  errors++;
/*     */       case '&': if (thiscmd.length() > 1) { switch (thiscmd.toUpperCase().charAt(1)) { case 'F': doCommandReset();
/*     */             case 'V': doCommandShowProfile(); }  errors++; } else { errors++; } 
/*     */       case 'S': if (regval < 256 && val < 256) { if (thisarg.equals("?")) { write("\n\r" + this.vmodem_registers[regval] + "\n\r"); } else { this.vmodem_registers[regval] = val; }  } else { errors++; } 
/*     */       case 'D': if (thisarg.length() != 0) { if (!thisarg.equalsIgnoreCase("L"))
/*     */             this.vmodem_dialstring = thisarg;  if (doDial() == 0)
/* 443 */             sendResponse(8);  errors = -1; return errors; }  errors++; }  errors++; return 0;
} private int doDial() { String tcphost; int tcpport; String[] dparts = this.vmodem_dialstring.split(":");
/*     */     
/* 445 */     if (dparts.length == 1) {
/*     */       
/* 447 */       tcphost = dparts[0];
/* 448 */       tcpport = 23;
/*     */     }
/* 450 */     else if (dparts.length == 2) {
/*     */       
/* 452 */       tcphost = dparts[0];
/* 453 */       tcpport = Integer.parseInt(dparts[1]);
/*     */     }
/*     */     else {
/*     */       
/* 457 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 461 */     this.tcpthread = new Thread(new DWVModemConnThread(this.handlerno, this.vport, tcphost, tcpport));
/* 462 */     this.tcpthread.setDaemon(true);
/*     */ 
/*     */     
/* 465 */     this.tcpthread.start();
/*     */     
/* 467 */     return 1; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doCommandShowProfile() {
/* 473 */     write("Active profile:" + getCRLF() + getCRLF());
/*     */     
/* 475 */     write("E" + onoff(this.vmodem_echo) + " ");
/* 476 */     write("Q" + onoff(this.vmodem_quiet) + " ");
/* 477 */     write("V" + onoff(this.vmodem_verbose) + " ");
/*     */     
/* 479 */     write(getCRLF() + getCRLF());
/*     */ 
/*     */     
/* 482 */     for (int i = 0; i < 38; i++) {
/*     */       
/* 484 */       write(String.format("S%03d=%03d  ", new Object[] { Integer.valueOf(i), Integer.valueOf(this.vmodem_registers[i]) }));
/* 485 */       Thread.yield();
/*     */     } 
/*     */     
/* 488 */     write(getCRLF() + getCRLF());
/*     */   }
/*     */ 
/*     */   
/*     */   private String onoff(boolean val) {
/* 493 */     if (val) {
/* 494 */       return "1";
/*     */     }
/* 496 */     return "0";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void doCommandReset() {
/* 502 */     this.vmodem_echo = false;
/* 503 */     this.vmodem_lastcommand = new String();
/* 504 */     this.vmodem_quiet = false;
/* 505 */     this.vmodem_verbose = true;
/*     */ 
/*     */     
/* 508 */     this.vmodem_registers[0] = 0;
/* 509 */     this.vmodem_registers[1] = 0;
/* 510 */     this.vmodem_registers[2] = 43;
/* 511 */     this.vmodem_registers[3] = 13;
/* 512 */     this.vmodem_registers[4] = 10;
/* 513 */     this.vmodem_registers[5] = 8;
/* 514 */     this.vmodem_registers[12] = 50;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getCRLF() {
/* 521 */     return Character.toString((char)this.vmodem_registers[3]) + Character.toString((char)this.vmodem_registers[4]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendResponse(int resp) {
/* 529 */     if (!this.vmodem_quiet)
/*     */     {
/*     */       
/* 532 */       if (this.vmodem_verbose) {
/*     */         
/* 534 */         write(getVerboseResponse(resp) + getCRLF());
/*     */       }
/*     */       else {
/*     */         
/* 538 */         write(resp + getCRLF());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getVerboseResponse(int resp) {
    String msg="";
/* 547 */    /* 547 */     switch (resp)
/*     */     
/*     */     { case 0:
/* 550 */         msg = "OK";
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
/* 577 */         return msg;case 1: msg = "CONNECT"; return msg;case 2: msg = "RING"; return msg;case 3: msg = "NO CARRIER"; return msg;case 4: msg = "ERROR"; return msg;case 6: msg = "NO DIAL TONE"; return msg;case 7: msg = "BUSY"; return msg;case 8: msg = "NO ANSWER"; return msg; }
/*     */  return "UKNOWN";
   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void write(String str) {
/*     */     try {
/* 584 */       this.dwVSerialPorts.writeToCoco(this.vport, str);
/*     */     }
/* 586 */     catch (DWPortNotValidException e) {
/*     */       
/* 588 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEcho() {
/* 595 */     return this.vmodem_echo;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCR() {
/* 601 */     return this.vmodem_registers[3];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLF() {
/* 606 */     return this.vmodem_registers[4];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBS() {
/* 611 */     return this.vmodem_registers[5];
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVModem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */