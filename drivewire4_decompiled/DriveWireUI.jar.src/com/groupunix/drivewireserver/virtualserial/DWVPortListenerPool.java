/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortListenerPool
/*     */ {
/*     */   public static final int MAX_CONN = 256;
/*     */   public static final int MAX_LISTEN = 64;
/*  15 */   private Socket[] sockets = new Socket[256];
/*  16 */   private ServerSocket[] server_sockets = new ServerSocket[64];
/*  17 */   private int[] serversocket_ports = new int[64];
/*  18 */   private int[] socket_ports = new int[256];
/*  19 */   private int[] modes = new int[256];
/*     */   
/*  21 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortListenerPool");
/*     */ 
/*     */   
/*     */   public int addConn(int port, Socket skt, int mode) {
/*  25 */     for (int i = 0; i < 256; i++) {
/*     */       
/*  27 */       if (this.sockets[i] == null) {
/*     */         
/*  29 */         this.sockets[i] = skt;
/*  30 */         this.modes[i] = mode;
/*  31 */         this.socket_ports[i] = port;
/*  32 */         return i;
/*     */       } 
/*     */     } 
/*     */     
/*  36 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket getConn(int conno) throws DWConnectionNotValidException {
/*  41 */     validateConn(conno);
/*  42 */     return this.sockets[conno];
/*     */   }
/*     */ 
/*     */   
/*     */   public void validateConn(int conno) throws DWConnectionNotValidException {
/*  47 */     if (conno < 0 || conno > 256 || this.sockets[conno] == null)
/*     */     {
/*  49 */       throw new DWConnectionNotValidException("Invalid connection #" + conno);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setConnPort(int conno, int port) throws DWConnectionNotValidException {
/*  55 */     validateConn(conno);
/*  56 */     this.socket_ports[conno] = port;
/*     */   }
/*     */ 
/*     */   
/*     */   public int addListener(int port, ServerSocket srvskt) {
/*  61 */     for (int i = 0; i < 64; i++) {
/*     */       
/*  63 */       if (this.server_sockets[i] == null) {
/*     */         
/*  65 */         this.server_sockets[i] = srvskt;
/*  66 */         this.serversocket_ports[i] = port;
/*  67 */         return i;
/*     */       } 
/*     */     } 
/*     */     
/*  71 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSocket getListener(int conno) {
/*  76 */     return this.server_sockets[conno];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closePortServerSockets(int port) {
/*  82 */     logger.debug("closing listener sockets for port " + port + "...");
/*  83 */     for (int i = 0; i < 64; i++) {
/*     */       
/*  85 */       if (getListener(i) != null)
/*     */       {
/*  87 */         if (this.serversocket_ports[i] == port) {
/*     */           
/*     */           try {
/*     */             
/*  91 */             killListener(i);
/*     */           }
/*  93 */           catch (DWConnectionNotValidException e) {
/*     */             
/*  95 */             logger.error(e.getMessage());
/*     */           } 
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closePortConnectionSockets(int port) {
/* 105 */     for (int i = 0; i < 256; i++) {
/*     */ 
/*     */       
/*     */       try {
/* 109 */         if (this.sockets[i] != null)
/*     */         {
/*     */           
/* 112 */           if (getMode(i) != 3)
/*     */           {
/*     */             
/* 115 */             killConn(i);
/*     */           }
/*     */         }
/*     */       }
/* 119 */       catch (DWConnectionNotValidException e) {
/*     */         
/* 121 */         logger.error("close sockets: " + e.getMessage());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode(int conno) throws DWConnectionNotValidException {
/* 131 */     validateConn(conno);
/* 132 */     return this.modes[conno];
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearConn(int conno) throws DWConnectionNotValidException {
/* 137 */     validateConn(conno);
/* 138 */     this.sockets[conno] = null;
/* 139 */     this.socket_ports[conno] = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearListener(int conno) {
/* 144 */     this.server_sockets[conno] = null;
/* 145 */     this.serversocket_ports[conno] = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void killConn(int conno) throws DWConnectionNotValidException {
/* 150 */     validateConn(conno);
/*     */ 
/*     */     
/*     */     try {
/* 154 */       this.sockets[conno].close();
/* 155 */       logger.debug("killed conn #" + conno);
/*     */     }
/* 157 */     catch (IOException e) {
/*     */       
/* 159 */       logger.debug("IO error closing conn #" + conno + ": " + e.getMessage());
/*     */     } 
/*     */     
/* 162 */     clearConn(conno);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void killListener(int conno) throws DWConnectionNotValidException {
/*     */     try {
/* 172 */       this.server_sockets[conno].close();
/* 173 */       logger.debug("killed listener #" + conno);
/*     */     }
/* 175 */     catch (IOException e) {
/*     */       
/* 177 */       logger.debug("IO error closing listener #" + conno + ": " + e.getMessage());
/*     */     } 
/*     */     
/* 180 */     clearListener(conno);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getListenerPort(int i) {
/* 186 */     return this.serversocket_ports[i];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getConnPort(int i) {
/* 191 */     return this.socket_ports[i];
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortListenerPool.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */