/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortTCPConnectionThread
/*     */   implements Runnable
/*     */ {
/*  15 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortTCPConnectionThread");
/*     */   
/*  17 */   private int vport = -1;
/*  18 */   private int tcpport = -1;
/*  19 */   private String tcphost = null;
/*     */   
/*     */   private Socket skt;
/*     */   
/*     */   private boolean wanttodie = false;
/*     */   
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */   
/*     */   public DWVPortTCPConnectionThread(DWProtocolHandler dwProto, int vport, String tcphostin, int tcpportin) {
/*  28 */     logger.debug("init tcp connection thread");
/*  29 */     this.vport = vport;
/*  30 */     this.tcpport = tcpportin;
/*  31 */     this.tcphost = tcphostin;
/*     */     
/*  33 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  40 */     Thread.currentThread().setName("tcpconn-" + Thread.currentThread().getId());
/*  41 */     Thread.currentThread().setPriority(5);
/*     */     
/*  43 */     logger.debug("run");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  49 */       this.skt = new Socket(this.tcphost, this.tcpport);
/*     */     }
/*  51 */     catch (UnknownHostException e) {
/*     */       
/*  53 */       logger.debug("unknown host " + this.tcphost);
/*     */       
/*     */       try {
/*  56 */         this.dwVSerialPorts.sendUtilityFailResponse(this.vport, (byte)122, "Unknown host '" + this.tcphost + "'");
/*     */       }
/*  58 */       catch (DWPortNotValidException e1) {
/*     */         
/*  60 */         logger.warn(e1.getMessage());
/*     */       } 
/*  62 */       this.wanttodie = true;
/*     */     }
/*  64 */     catch (IOException e1) {
/*     */       
/*  66 */       logger.debug("IO error: " + e1.getMessage());
/*     */       
/*     */       try {
/*  69 */         this.dwVSerialPorts.sendUtilityFailResponse(this.vport, (byte)121, e1.getMessage());
/*     */       }
/*  71 */       catch (DWPortNotValidException e) {
/*     */         
/*  73 */         logger.warn(e1.getMessage());
/*     */       } 
/*  75 */       this.wanttodie = true;
/*     */     } 
/*     */     
/*  78 */     if (!this.wanttodie) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/*  83 */         this.dwVSerialPorts.sendUtilityOKResponse(this.vport, "Connected to " + this.tcphost + ":" + this.tcpport);
/*  84 */         this.dwVSerialPorts.markConnected(this.vport);
/*  85 */         this.dwVSerialPorts.setUtilMode(this.vport, 3);
/*  86 */         logger.debug("Connected to " + this.tcphost + ":" + this.tcpport);
/*  87 */         this.dwVSerialPorts.setPortOutput(this.vport, this.skt.getOutputStream());
/*     */       
/*     */       }
/*  90 */       catch (DWPortNotValidException e2) {
/*     */         
/*  92 */         logger.warn(e2.getMessage());
/*  93 */         this.wanttodie = true;
/*     */       }
/*  95 */       catch (IOException e1) {
/*     */         
/*  97 */         logger.error("IO Error setting output: " + e1.getMessage());
/*  98 */         this.wanttodie = true;
/*     */       } 
/*     */       
/* 101 */       while (!this.wanttodie && !this.skt.isClosed() && this.dwVSerialPorts.isOpen(this.vport)) {
/*     */ 
/*     */         
/*     */         try {
/*     */           
/* 106 */           int databyte = this.skt.getInputStream().read();
/* 107 */           if (databyte == -1) {
/*     */             
/* 109 */             logger.debug("got -1 in input stream");
/* 110 */             this.wanttodie = true;
/*     */             
/*     */             continue;
/*     */           } 
/* 114 */           this.dwVSerialPorts.writeToCoco(this.vport, (byte)databyte);
/*     */ 
/*     */         
/*     */         }
/* 118 */         catch (IOException e) {
/*     */           
/* 120 */           logger.debug("IO error reading tcp: " + e.getMessage());
/* 121 */           this.wanttodie = true;
/*     */         }
/* 123 */         catch (DWPortNotValidException e) {
/*     */           
/* 125 */           logger.error(e.getMessage());
/*     */           
/* 127 */           this.wanttodie = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 133 */       if (this.wanttodie) {
/* 134 */         logger.debug("exit because wanttodie");
/*     */       }
/* 136 */       if (this.skt.isClosed()) {
/* 137 */         logger.debug("exit because skt isClosed");
/*     */       }
/* 139 */       if (!this.dwVSerialPorts.isOpen(this.vport)) {
/* 140 */         logger.debug("exit because port is not open");
/*     */       }
/* 142 */       this.dwVSerialPorts.markDisconnected(this.vport);
/* 143 */       this.dwVSerialPorts.setPortOutput(this.vport, null);
/*     */       
/* 145 */       if (!this.skt.isClosed()) {
/*     */         
/*     */         try {
/*     */ 
/*     */           
/* 150 */           this.skt.close();
/*     */         }
/* 152 */         catch (IOException e) {
/*     */           
/* 154 */           logger.debug("error closing socket: " + e.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 164 */     if (this.skt != null) {
/*     */       
/* 166 */       if (this.skt.isConnected()) {
/*     */ 
/*     */         
/* 169 */         logger.debug("exit stage 1, flush buffer");
/*     */ 
/*     */         
/*     */         try {
/* 173 */           while (this.dwVSerialPorts.bytesWaiting(this.vport) > 0 && this.dwVSerialPorts.isOpen(this.vport))
/*     */           {
/* 175 */             logger.debug("pause for the cause: " + this.dwVSerialPorts.bytesWaiting(this.vport) + " bytes left");
/* 176 */             Thread.sleep(100L);
/*     */           }
/*     */         
/* 179 */         } catch (InterruptedException e) {
/*     */           
/* 181 */           logger.error(e.getMessage());
/*     */         }
/* 183 */         catch (DWPortNotValidException e) {
/*     */           
/* 185 */           logger.error(e.getMessage());
/*     */         } 
/*     */         
/* 188 */         logger.debug("exit stage 2, send peer signal");
/*     */ 
/*     */         
/*     */         try {
/* 192 */           this.dwVSerialPorts.closePort(this.vport);
/*     */         }
/* 194 */         catch (DWPortNotValidException e) {
/*     */           
/* 196 */           logger.error("in close port: " + e.getMessage());
/*     */         } 
/*     */       } 
/*     */       
/* 200 */       logger.debug("thread exiting");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortTCPConnectionThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */