/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortTermThread
/*     */   implements Runnable
/*     */ {
/*  17 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortTermThread");
/*     */   
/*     */   private int tcpport;
/*     */   private boolean wanttodie = false;
/*  21 */   private int vport = 0;
/*     */   
/*     */   private static final int TERM_PORT = 0;
/*     */   
/*     */   private static final int MODE_TERM = 3;
/*     */   
/*     */   private static final int BACKLOG = 0;
/*     */   
/*     */   private Thread connthread;
/*     */   private DWVPortTCPServerThread connobj;
/*     */   private int conno;
/*     */   private DWProtocolHandler dwProto;
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */   private ServerSocket srvr;
/*     */   
/*     */   public DWVPortTermThread(DWProtocolHandler dwProto, int tcpport) {
/*  37 */     logger.debug("init term device thread on port " + tcpport);
/*  38 */     this.tcpport = tcpport;
/*  39 */     this.dwProto = dwProto;
/*  40 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  47 */     Thread.currentThread().setName("termdev-" + Thread.currentThread().getId());
/*     */     
/*  49 */     logger.debug("run");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  54 */       this.dwVSerialPorts.resetPort(0);
/*     */     }
/*  56 */     catch (DWPortNotValidException e3) {
/*     */       
/*  58 */       logger.warn("while resetting term port: " + e3.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*  62 */     this.srvr = null;
/*     */ 
/*     */     
/*     */     try {
/*  66 */       this.dwVSerialPorts.openPort(0);
/*     */ 
/*     */       
/*  69 */       if (this.dwProto.getConfig().containsKey("ListenAddress")) {
/*     */         
/*  71 */         this.srvr = new ServerSocket(this.tcpport, 0, InetAddress.getByName(this.dwProto.getConfig().getString("ListenAddress")));
/*     */       }
/*     */       else {
/*     */         
/*  75 */         this.srvr = new ServerSocket(this.tcpport, 0);
/*     */       } 
/*  77 */       logger.info("listening on port " + this.srvr.getLocalPort());
/*     */     
/*     */     }
/*  80 */     catch (IOException e2) {
/*     */       
/*  82 */       logger.error("Error opening socket on port " + this.tcpport + ": " + e2.getMessage());
/*     */       
/*     */       return;
/*  85 */     } catch (DWPortNotValidException e) {
/*     */       
/*  87 */       logger.error("Error opening term port: " + e.getMessage());
/*     */       
/*     */       return;
/*     */     } 
/*  91 */     while (!this.wanttodie && !this.srvr.isClosed()) {
/*     */       
/*  93 */       logger.debug("waiting for connection");
/*  94 */       Socket skt = new Socket();
/*     */ 
/*     */       
/*     */       try {
/*  98 */         skt = this.srvr.accept();
/*     */       }
/* 100 */       catch (IOException e1) {
/*     */         
/* 102 */         logger.info("IO error: " + e1.getMessage());
/* 103 */         this.wanttodie = true;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 108 */       logger.info("new connection from " + skt.getInetAddress().getHostAddress());
/*     */       
/* 110 */       if (this.connthread != null) {
/*     */         
/* 112 */         if (this.connthread.isAlive()) {
/*     */ 
/*     */           
/* 115 */           logger.debug("term connection already in use");
/*     */           
/*     */           try {
/* 118 */             skt.getOutputStream().write(("The term device is already connected to a session (from " + this.dwVSerialPorts.getListenerPool().getConn(this.conno).getInetAddress().getHostName() + ")\r\n").getBytes());
/* 119 */             skt.close();
/*     */           }
/* 121 */           catch (IOException e) {
/*     */             
/* 123 */             logger.debug("io error closing socket: " + e.getMessage());
/*     */           }
/* 125 */           catch (DWConnectionNotValidException e) {
/*     */             
/* 127 */             logger.error(e.getMessage());
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/* 132 */         startConn(skt);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 137 */       startConn(skt);
/*     */     } 
/*     */ 
/*     */     
/* 141 */     if (this.srvr != null) {
/*     */       
/*     */       try {
/*     */         
/* 145 */         this.srvr.close();
/*     */       }
/* 147 */       catch (IOException e) {
/*     */         
/* 149 */         logger.error("error closing server socket: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/* 153 */     logger.debug("exiting");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void startConn(Socket skt) {
/* 159 */     byte[] buf = new byte[9];
/*     */     
/* 161 */     buf[0] = -1;
/* 162 */     buf[1] = -5;
/* 163 */     buf[2] = 1;
/* 164 */     buf[3] = -1;
/* 165 */     buf[4] = -5;
/* 166 */     buf[5] = 3;
/* 167 */     buf[6] = -1;
/* 168 */     buf[7] = -3;
/* 169 */     buf[8] = -13;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 174 */       skt.getOutputStream().write(buf, 0, 9);
/* 175 */       for (int i = 0; i < 9; i++)
/*     */       {
/* 177 */         skt.getInputStream().read();
/*     */       }
/*     */     }
/* 180 */     catch (IOException e) {
/*     */       
/* 182 */       logger.error(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 188 */       this.conno = this.dwVSerialPorts.getListenerPool().addConn(this.vport, skt, 3);
/* 189 */       this.connobj = new DWVPortTCPServerThread(this.dwProto, 0, this.conno);
/* 190 */       this.connthread = new Thread(this.connobj);
/* 191 */       this.connthread.start();
/*     */     
/*     */     }
/* 194 */     catch (DWConnectionNotValidException e) {
/*     */       
/* 196 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 206 */     logger.debug("shutting down");
/* 207 */     this.wanttodie = true;
/*     */     
/* 209 */     if (this.connobj != null) {
/*     */       
/* 211 */       this.connobj.shutdown();
/* 212 */       this.connthread.interrupt();
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 217 */       this.srvr.close();
/*     */     
/*     */     }
/* 220 */     catch (IOException e) {
/*     */       
/* 222 */       logger.warn("IOException closing server socket: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortTermThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */