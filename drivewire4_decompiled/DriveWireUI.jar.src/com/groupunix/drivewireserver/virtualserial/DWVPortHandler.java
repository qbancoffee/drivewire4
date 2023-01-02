/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortHandler
/*     */ {
/*  17 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortHandler");
/*     */   
/*  19 */   private String port_command = new String();
/*     */   private int vport;
/*     */   private DWVModem vModem;
/*     */   private Thread utilthread;
/*     */   private DWVSerialPorts dwVSerialPorts;
/*  24 */   private DWVSerialCircularBuffer inputBuffer = new DWVSerialCircularBuffer(1024, true);
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWVPortHandler(DWProtocolHandler dwProto, int port) {
/*  29 */     this.vport = port;
/*  30 */     this.vModem = new DWVModem(dwProto, port);
/*  31 */     this.dwProto = dwProto;
/*  32 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void takeInput(int databyte) {
/*  41 */     if (this.vModem.isEcho()) {
/*     */       
/*     */       try {
/*     */         
/*  45 */         this.dwVSerialPorts.write1(this.vport, (byte)databyte);
/*     */ 
/*     */         
/*  48 */         if (databyte == this.vModem.getCR())
/*     */         {
/*  50 */           this.dwVSerialPorts.write1(this.vport, (byte)this.vModem.getLF());
/*     */         
/*     */         }
/*     */       }
/*  54 */       catch (IOException e) {
/*     */         
/*  56 */         logger.warn("in takeinput: " + e.getMessage());
/*     */       }
/*  58 */       catch (DWPortNotValidException e) {
/*     */         
/*  60 */         logger.warn("in takeinput: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     if (databyte == this.vModem.getCR()) {
/*     */       
/*  73 */       logger.debug("port command '" + this.port_command + "'");
/*     */       
/*  75 */       processCommand(this.port_command);
/*     */       
/*  77 */       this.port_command = new String();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  83 */     else if (databyte == this.vModem.getBS() && this.port_command.length() > 0) {
/*     */       
/*  85 */       this.port_command = this.port_command.substring(0, this.port_command.length() - 1);
/*     */     }
/*  87 */     else if (databyte > 0) {
/*     */ 
/*     */       
/*  90 */       this.port_command += Character.toString((char)databyte);
/*     */ 
/*     */       
/*  93 */       if (this.port_command.equals("MThd")) {
/*     */ 
/*     */         
/*     */         try {
/*     */ 
/*     */ 
/*     */           
/* 100 */           this.inputBuffer.getOutputStream().write("MThd".getBytes());
/*     */         }
/* 102 */         catch (IOException e) {
/*     */           
/* 104 */           logger.warn(e.getMessage());
/*     */         } 
/* 106 */         this.dwProto.getVPorts().setPortOutput(this.vport, this.inputBuffer.getOutputStream());
/* 107 */         this.dwProto.getVPorts().markConnected(this.vport);
/*     */         
/* 109 */         logger.info("MIDI file detected on handler # " + this.dwProto.getHandlerNo() + " port " + this.vport);
/*     */         
/* 111 */         this.utilthread = new Thread(new DWVPortMIDIPlayerThread(this.dwProto, this.vport, this.inputBuffer));
/* 112 */         this.utilthread.start();
/*     */         
/* 114 */         this.port_command = new String();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processCommand(String cmd) {
/* 125 */     if (cmd.length() == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 131 */     if (cmd.toUpperCase().startsWith("AT") || cmd.toUpperCase().startsWith("A/")) {
/*     */       
/* 133 */       this.vModem.processCommand(cmd);
/*     */     }
/*     */     else {
/*     */       
/* 137 */       processAPICommand(cmd);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processAPICommand(String cmd) {
/* 146 */     String[] cmdparts = cmd.split("\\s+");
/*     */     
/* 148 */     if (cmdparts.length > 0) {
/*     */       
/* 150 */       if (cmdparts[0].equalsIgnoreCase("tcp"))
/*     */       {
/* 152 */         if (cmdparts.length == 4 && cmdparts[1].equalsIgnoreCase("connect"))
/*     */         {
/* 154 */           doTCPConnect(cmdparts[2], cmdparts[3]);
/*     */         }
/* 156 */         else if (cmdparts.length >= 3 && cmdparts[1].equalsIgnoreCase("listen"))
/*     */         {
/* 158 */           doTCPListen(cmdparts);
/*     */         
/*     */         }
/* 161 */         else if (cmdparts.length == 3 && cmdparts[1].equalsIgnoreCase("listentelnet"))
/*     */         {
/* 163 */           doTCPListen(cmdparts[2], 1);
/*     */         }
/* 165 */         else if (cmdparts.length == 3 && cmdparts[1].equalsIgnoreCase("join"))
/*     */         {
/* 167 */           doTCPJoin(cmdparts[2]);
/*     */         }
/* 169 */         else if (cmdparts.length == 3 && cmdparts[1].equalsIgnoreCase("kill"))
/*     */         {
/* 171 */           doTCPKill(cmdparts[2]);
/*     */         }
/*     */         else
/*     */         {
/* 175 */           respondFail((byte)10, "Syntax error in TCP command");
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 180 */       else if (cmdparts[0].equalsIgnoreCase("dw"))
/*     */       {
/*     */ 
/*     */         
/* 184 */         this.utilthread = new Thread(new DWUtilDWThread(this.dwProto, this.vport, cmd));
/* 185 */         this.utilthread.start();
/*     */       }
/* 187 */       else if (cmdparts[0].equalsIgnoreCase("log"))
/*     */       {
/*     */         
/* 190 */         logger.info("coco " + cmd);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 195 */       logger.debug("got empty command?");
/* 196 */       respondFail((byte)10, "Syntax error: no command?");
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
/*     */   private void doTCPJoin(String constr) {
/*     */     int conno;
/*     */     try {
/* 210 */       conno = Integer.parseInt(constr);
/*     */     }
/* 212 */     catch (NumberFormatException e) {
/*     */       
/* 214 */       respondFail((byte)10, "non-numeric port in tcp join command");
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*     */     try {
/* 221 */       this.dwVSerialPorts.getListenerPool().validateConn(conno);
/* 222 */       respondOk("attaching to connection " + conno);
/*     */ 
/*     */       
/* 225 */       this.utilthread = new Thread(new DWVPortTCPServerThread(this.dwProto, this.vport, conno));
/* 226 */       this.utilthread.start();
/*     */     }
/* 228 */     catch (DWConnectionNotValidException e) {
/*     */       
/* 230 */       respondFail((byte)123, "invalid connection number");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doTCPKill(String constr) {
/*     */     int conno;
/*     */     try {
/* 243 */       conno = Integer.parseInt(constr);
/*     */     }
/* 245 */     catch (NumberFormatException e) {
/*     */       
/* 247 */       respondFail((byte)10, "non-numeric port in tcp kill command");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 252 */     logger.warn("Killing connection " + conno);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 257 */       this.dwVSerialPorts.getListenerPool().killConn(conno);
/* 258 */       respondOk("killed connection " + conno);
/*     */     }
/* 260 */     catch (DWConnectionNotValidException e) {
/*     */       
/* 262 */       respondFail((byte)123, "invalid connection number");
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
/*     */   private void doTCPConnect(String tcphost, String tcpportstr) {
/*     */     int tcpport;
/*     */     try {
/* 278 */       tcpport = Integer.parseInt(tcpportstr);
/*     */     }
/* 280 */     catch (NumberFormatException e) {
/*     */       
/* 282 */       respondFail((byte)10, "non-numeric port in tcp connect command");
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 289 */     this.utilthread = new Thread(new DWVPortTCPConnectionThread(this.dwProto, this.vport, tcphost, tcpport));
/* 290 */     this.utilthread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doTCPListen(String strport, int mode) {
/*     */     int tcpport;
/*     */     try {
/* 301 */       tcpport = Integer.parseInt(strport);
/*     */     }
/* 303 */     catch (NumberFormatException e) {
/*     */       
/* 305 */       respondFail((byte)10, "non-numeric port in tcp listen command");
/*     */       return;
/*     */     } 
/* 308 */     DWVPortTCPListenerThread listener = new DWVPortTCPListenerThread(this.dwProto, this.vport, tcpport);
/*     */ 
/*     */ 
/*     */     
/* 312 */     listener.setMode(mode);
/* 313 */     listener.setDo_banner(true);
/* 314 */     listener.setDo_telnet(true);
/*     */ 
/*     */     
/* 317 */     Thread listenThread = new Thread(listener);
/* 318 */     listenThread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doTCPListen(String[] cmdparts) {
/*     */     int tcpport;
/*     */     try {
/* 329 */       tcpport = Integer.parseInt(cmdparts[2]);
/*     */     }
/* 331 */     catch (NumberFormatException e) {
/*     */       
/* 333 */       respondFail((byte)10, "non-numeric port in tcp listen command");
/*     */       return;
/*     */     } 
/* 336 */     DWVPortTCPListenerThread listener = new DWVPortTCPListenerThread(this.dwProto, this.vport, tcpport);
/*     */ 
/*     */     
/* 339 */     if (cmdparts.length > 3)
/*     */     {
/* 341 */       for (int i = 3; i < cmdparts.length; i++) {
/*     */         
/* 343 */         if (cmdparts[i].equalsIgnoreCase("telnet")) {
/*     */           
/* 345 */           listener.setDo_telnet(true);
/*     */         }
/* 347 */         else if (cmdparts[i].equalsIgnoreCase("httpd")) {
/*     */           
/* 349 */           listener.setMode(2);
/*     */         }
/* 351 */         else if (cmdparts[i].equalsIgnoreCase("banner")) {
/*     */           
/* 353 */           listener.setDo_banner(true);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 361 */     Thread listenThread = new Thread(listener);
/* 362 */     listenThread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void respondOk(String txt) {
/* 369 */     logger.debug("command ok: " + txt);
/*     */     
/*     */     try {
/* 372 */       this.dwVSerialPorts.writeToCoco(this.vport, "OK " + txt + '\r');
/*     */     }
/* 374 */     catch (DWPortNotValidException e) {
/*     */       
/* 376 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void respondFail(byte errno, String txt) {
/* 383 */     String perrno = String.format("%03d", new Object[] { Integer.valueOf(errno & 0xFF) });
/* 384 */     logger.debug("command failed: " + perrno + " " + txt);
/*     */     
/*     */     try {
/* 387 */       this.dwVSerialPorts.writeToCoco(this.vport, "FAIL " + perrno + " " + txt + '\r');
/*     */     }
/* 389 */     catch (DWPortNotValidException e) {
/*     */       
/* 391 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void announceConnection(int conno, int localport, String hostaddr) {
/*     */     try {
/* 399 */       this.dwVSerialPorts.writeToCoco(this.vport, conno + " " + localport + " " + hostaddr + '\r');
/*     */     }
/* 401 */     catch (DWPortNotValidException e) {
/*     */ 
/*     */       
/* 404 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */