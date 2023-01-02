/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
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
/*     */ 
/*     */ public class DWVPortTCPListenerThread
/*     */   implements Runnable
/*     */ {
/*  17 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortTCPListenerThread");
/*     */   
/*     */   private int vport;
/*     */   
/*     */   private int tcpport;
/*     */   
/*     */   private DWVSerialPorts dwVSerialPorts;
/*  24 */   private int mode = 0;
/*     */   
/*     */   private boolean do_banner = false;
/*     */   private boolean do_telnet = false;
/*     */   private boolean wanttodie = false;
/*  29 */   private static int BACKLOG = 20;
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */ 
/*     */   
/*     */   public DWVPortTCPListenerThread(DWProtocolHandler dwProto, int vport, int tcpport) {
/*  35 */     logger.debug("init tcp listener thread on port " + tcpport);
/*  36 */     this.vport = vport;
/*  37 */     this.tcpport = tcpport;
/*  38 */     this.dwProto = dwProto;
/*  39 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  48 */     Thread.currentThread().setName("tcplisten-" + Thread.currentThread().getId());
/*  49 */     Thread.currentThread().setPriority(5);
/*     */     
/*  51 */     logger.debug("run");
/*     */ 
/*     */     
/*  54 */     ServerSocket srvr = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*     */       try {
/*  62 */         if (this.dwProto.getConfig().containsKey("ListenAddress")) {
/*     */           
/*  64 */           srvr = new ServerSocket(this.tcpport, BACKLOG, InetAddress.getByName(this.dwProto.getConfig().getString("ListenAddress")));
/*     */         }
/*     */         else {
/*     */           
/*  68 */           srvr = new ServerSocket(this.tcpport, BACKLOG);
/*     */         } 
/*  70 */         logger.info("tcp listening on port " + srvr.getLocalPort());
/*     */       }
/*  72 */       catch (IOException e2) {
/*     */         
/*  74 */         logger.error(e2.getMessage());
/*  75 */         this.dwVSerialPorts.sendUtilityFailResponse(this.vport, (byte)121, e2.getMessage());
/*     */         
/*     */         return;
/*     */       } 
/*  79 */       this.dwVSerialPorts.writeToCoco(this.vport, "OK listening on port " + this.tcpport + Character.MIN_VALUE + '\r');
/*     */       
/*  81 */       this.dwVSerialPorts.setUtilMode(this.vport, 7);
/*     */       
/*  83 */       this.dwVSerialPorts.getListenerPool().addListener(this.vport, srvr);
/*     */ 
/*     */       
/*  86 */       while (!this.wanttodie && this.dwVSerialPorts.isOpen(this.vport) && !srvr.isClosed())
/*     */       {
/*  88 */         logger.debug("waiting for connection");
/*  89 */         Socket skt = null;
/*     */         
/*  91 */         skt = srvr.accept();
/*     */ 
/*     */         
/*  94 */         logger.info("new connection from " + skt.getInetAddress().getHostAddress());
/*     */         
/*  96 */         if (this.mode == 2) {
/*     */ 
/*     */           
/*  99 */           logger.error("HTTP MODE NO LONGER SUPPORTED");
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 105 */         Thread pfthread = new Thread(new DWVPortTelnetPreflightThread(this.dwProto, this.vport, skt, this.do_telnet, this.do_banner));
/* 106 */         pfthread.start();
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/* 113 */     catch (IOException e2) {
/*     */       
/* 115 */       logger.error(e2.getMessage());
/*     */     }
/* 117 */     catch (DWPortNotValidException e) {
/*     */       
/* 119 */       logger.error(e.getMessage());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 124 */     if (srvr != null) {
/*     */       
/*     */       try {
/*     */         
/* 128 */         srvr.close();
/*     */       }
/* 130 */       catch (IOException e) {
/*     */         
/* 132 */         logger.error("error closing server socket: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/* 136 */     logger.debug("tcp listener thread exiting");
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
/*     */   public void setDo_banner(boolean do_banner) {
/* 150 */     this.do_banner = do_banner;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDo_banner() {
/* 155 */     return this.do_banner;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMode(int mode) {
/* 161 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMode() {
/* 166 */     return this.mode;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDo_telnet(boolean b) {
/* 171 */     this.do_telnet = b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDo_telnet() {
/* 177 */     return this.do_telnet;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortTCPListenerThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */