/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWTCPClientDevice
/*     */   implements DWProtocolDevice
/*     */ {
/*  13 */   private static final Logger logger = Logger.getLogger("DWServer.DWTCPClientDev");
/*     */   
/*     */   private int tcpport;
/*     */   
/*     */   private String tcphost;
/*     */   private int handlerno;
/*     */   private Socket sock;
/*     */   private boolean bytelog = false;
/*     */   
/*     */   public DWTCPClientDevice(int handlerno, String tcphost, int tcpport) throws IOException {
/*  23 */     this.handlerno = handlerno;
/*  24 */     this.tcpport = tcpport;
/*  25 */     this.tcphost = tcphost;
/*     */     
/*  27 */     this.bytelog = DriveWireServer.getHandler(this.handlerno).getConfig().getBoolean("LogDeviceBytes", false);
/*     */     
/*  29 */     logger.debug("init tcp device client to " + tcphost + " port " + tcpport + " for handler #" + handlerno + " (logging bytes: " + this.bytelog + ")");
/*     */ 
/*     */ 
/*     */     
/*  33 */     this.sock = new Socket(this.tcphost, this.tcpport);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  43 */     logger.info("closing tcp client device in handler #" + this.handlerno);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  49 */       this.sock.close();
/*     */     }
/*  51 */     catch (IOException e) {
/*     */       
/*  53 */       logger.warn(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] comRead(int len) throws IOException {
/*  64 */     byte[] buf = new byte[len];
/*     */     
/*  66 */     for (int i = 0; i < len; i++)
/*     */     {
/*  68 */       buf[i] = (byte)comRead1(true);
/*     */     }
/*     */     
/*  71 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int comRead1(boolean timeout) throws IOException {
/*  78 */     int data = -1;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  83 */       data = this.sock.getInputStream().read();
/*     */     }
/*  85 */     catch (IOException e) {
/*     */       
/*  87 */       e.printStackTrace();
/*     */     } 
/*     */ 
/*     */     
/*  91 */     if (data < 0) {
/*     */ 
/*     */ 
/*     */       
/*  95 */       logger.info("socket error reading device");
/*     */       
/*  97 */       return -1;
/*     */     } 
/*     */ 
/*     */     
/* 101 */     if (this.bytelog) {
/* 102 */       logger.debug("TCPREAD: " + data);
/*     */     }
/* 104 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void comWrite(byte[] data, int len, boolean prefix) {
/*     */     try {
/* 114 */       this.sock.getOutputStream().write(data, 0, len);
/*     */       
/* 116 */       if (this.bytelog)
/*     */       {
/* 118 */         String tmps = new String();
/*     */         
/* 120 */         for (int i = 0; i < data.length; i++)
/*     */         {
/* 122 */           tmps = tmps + " " + (data[i] & 0xFF);
/*     */         }
/*     */         
/* 125 */         logger.debug("WRITE " + data.length + ":" + tmps);
/*     */       }
/*     */     
/* 128 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 131 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void comWrite1(int data, boolean prefix) {
/*     */     try {
/* 143 */       this.sock.getOutputStream().write((byte)data);
/*     */       
/* 145 */       if (this.bytelog) {
/* 146 */         logger.debug("TCP-C-WRITE1: " + data);
/*     */       }
/*     */     }
/* 149 */     catch (IOException e) {
/*     */ 
/*     */       
/* 152 */       logger.error(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean connected() {
/* 161 */     if (this.sock == null) {
/* 162 */       return false;
/*     */     }
/* 164 */     if (this.sock.isClosed()) {
/* 165 */       return false;
/*     */     }
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 173 */     close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRate() {
/* 181 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceName() {
/* 188 */     return this.tcphost + ":" + this.tcpport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDeviceType() {
/* 195 */     return "tcp-client";
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWTCPClientDevice.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */