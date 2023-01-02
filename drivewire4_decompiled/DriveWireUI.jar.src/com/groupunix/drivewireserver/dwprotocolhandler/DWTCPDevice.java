/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ public class DWTCPDevice
/*     */   implements DWProtocolDevice
/*     */ {
/*  15 */   private static final Logger logger = Logger.getLogger("DWServer.DWTCPDevice");
/*     */   private int tcpport;
/*     */   private int handlerno;
/*     */   private ServerSocket srvr;
/*  19 */   private Socket skt = null;
/*     */   
/*     */   private boolean bytelog = false;
/*     */   
/*     */   public DWTCPDevice(int handlerno, int tcpport) throws IOException {
/*  24 */     this.handlerno = handlerno;
/*  25 */     this.tcpport = tcpport;
/*     */     
/*  27 */     this.bytelog = DriveWireServer.getHandler(this.handlerno).getConfig().getBoolean("LogDeviceBytes", false);
/*     */     
/*  29 */     logger.debug("init tcp device server on port " + tcpport + " for handler #" + handlerno + " (logging bytes: " + this.bytelog + ")");
/*     */ 
/*     */ 
/*     */     
/*  33 */     if (DriveWireServer.getHandler(this.handlerno).getConfig().containsKey("ListenAddress")) {
/*     */       
/*  35 */       this.srvr = new ServerSocket(this.tcpport, 0, InetAddress.getByName(DriveWireServer.getHandler(this.handlerno).getConfig().getString("ListenAddress")));
/*     */     }
/*     */     else {
/*     */       
/*  39 */       this.srvr = new ServerSocket(this.tcpport, 0);
/*     */     } 
/*     */     
/*  42 */     logger.info("listening on port " + this.srvr.getLocalPort());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  49 */     logger.info("closing tcp device in handler #" + this.handlerno);
/*     */     
/*  51 */     closeClient();
/*     */ 
/*     */     
/*     */     try {
/*  55 */       this.srvr.close();
/*     */     }
/*  57 */     catch (IOException e) {
/*     */       
/*  59 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeClient() {
/*  68 */     logger.info("closing client connection");
/*     */     
/*  70 */     if (this.skt != null && !this.skt.isClosed()) {
/*     */       
/*     */       try {
/*     */         
/*  74 */         this.skt.close();
/*     */       }
/*  76 */       catch (IOException e) {
/*     */         
/*  78 */         logger.warn(e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/*  82 */     this.skt = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] comRead(int len) throws IOException {
/*  89 */     byte[] buf = new byte[len];
/*     */     
/*  91 */     for (int i = 0; i < len; i++)
/*     */     {
/*  93 */       buf[i] = (byte)comRead1(true);
/*     */     }
/*     */     
/*  96 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int comRead1(boolean timeout) throws IOException {
/* 103 */     int data = -1;
/*     */     
/* 105 */     if (this.skt == null)
/*     */     {
/* 107 */       getClient();
/*     */     }
/*     */     
/* 110 */     if (this.skt != null) {
/*     */ 
/*     */       
/*     */       try {
/* 114 */         data = this.skt.getInputStream().read();
/*     */       }
/* 116 */       catch (IOException e) {
/*     */ 
/*     */         
/* 119 */         closeClient();
/*     */       } 
/*     */       
/* 122 */       if (data < 0) {
/*     */ 
/*     */ 
/*     */         
/* 126 */         logger.info("socket error reading device");
/*     */         
/* 128 */         closeClient();
/*     */ 
/*     */         
/* 131 */         return comRead1(timeout);
/*     */       } 
/*     */       
/* 134 */       if (this.bytelog) {
/* 135 */         logger.debug("TCPREAD: " + data);
/*     */       }
/*     */     } 
/* 138 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void comWrite(byte[] data, int len, boolean prefix) {
/*     */     try {
/* 147 */       this.skt.getOutputStream().write(data, 0, len);
/*     */       
/* 149 */       if (this.bytelog)
/*     */       {
/* 151 */         String tmps = new String();
/*     */         
/* 153 */         for (int i = 0; i < data.length; i++)
/*     */         {
/* 155 */           tmps = tmps + " " + (data[i] & 0xFF);
/*     */         }
/*     */         
/* 158 */         logger.debug("WRITE " + data.length + ":" + tmps);
/*     */       }
/*     */     
/* 161 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 164 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void comWrite1(int data, boolean prefix) {
/*     */     try {
/* 174 */       this.skt.getOutputStream().write((byte)data);
/*     */       
/* 176 */       if (this.bytelog) {
/* 177 */         logger.debug("TCPWRITE1: " + data);
/*     */       }
/*     */     }
/* 180 */     catch (IOException e) {
/*     */ 
/*     */       
/* 183 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean connected() {
/* 192 */     if (this.skt == null) {
/* 193 */       return false;
/*     */     }
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 202 */     close();
/*     */   }
/*     */ 
/*     */   
/*     */   private void getClient() {
/* 207 */     logger.info("waiting for client...");
/*     */     
/*     */     try {
/* 210 */       this.skt = this.srvr.accept();
/*     */     }
/* 212 */     catch (IOException e1) {
/*     */       
/* 214 */       logger.info("IO error while getting client: " + e1.getMessage());
/*     */       
/*     */       return;
/*     */     } 
/* 218 */     logger.info("new client connect from " + this.skt.getInetAddress().getCanonicalHostName());
/*     */     
/*     */     try {
/* 221 */       this.skt.setTcpNoDelay(true);
/*     */     }
/* 223 */     catch (SocketException e) {
/*     */       
/* 225 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRate() {
/* 232 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceName() {
/* 239 */     return "listen:" + this.tcpport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceType() {
/* 246 */     return "tcp";
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWTCPDevice.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */