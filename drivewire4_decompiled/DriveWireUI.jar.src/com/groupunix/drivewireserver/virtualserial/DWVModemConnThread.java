/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVModemConnThread
/*     */   implements Runnable
/*     */ {
/*  15 */   private static final Logger logger = Logger.getLogger("DWServer.DWVModemConnThread");
/*     */   
/*     */   private Socket skt;
/*  18 */   private String clientHost = "none";
/*  19 */   private int clientPort = -1;
/*  20 */   private int vport = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int handlerno;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int IAC = 255;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SE = 240;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int NOP = 241;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DM = 242;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int BREAK = 243;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int IP = 244;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int AO = 245;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int AYT = 246;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EC = 247;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EL = 248;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int GA = 249;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SB = 250;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int WILL = 251;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int WONT = 252;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DO = 253;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DONT = 254;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWVModemConnThread(int handlerno, int vport, String host, int tcpport) {
/* 124 */     this.vport = vport;
/* 125 */     this.clientHost = host;
/* 126 */     this.clientPort = tcpport;
/* 127 */     this.handlerno = handlerno;
/* 128 */     this.dwVSerialPorts = ((DWProtocolHandler)DriveWireServer.getHandler(this.handlerno)).getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 134 */     Thread.currentThread().setName("mdmconn-" + Thread.currentThread().getId());
/* 135 */     logger.debug("thread run for connection to " + this.clientHost + ":" + this.clientPort);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 140 */       this.skt = new Socket(this.clientHost, this.clientPort);
/*     */       
/* 142 */       this.dwVSerialPorts.markConnected(this.vport);
/* 143 */       this.dwVSerialPorts.setUtilMode(this.vport, 4);
/* 144 */       this.dwVSerialPorts.setPortOutput(this.vport, this.skt.getOutputStream());
/*     */ 
/*     */ 
/*     */       
/* 148 */       int telmode = 0;
/*     */       
/* 150 */       this.dwVSerialPorts.getPortInput(this.vport).write("CONNECT\r\n".getBytes());
/*     */       
/* 152 */       while (this.skt.isConnected())
/*     */       {
/* 154 */         int data = this.skt.getInputStream().read();
/*     */         
/* 156 */         if (DriveWireServer.getHandler(this.handlerno).getConfig().getBoolean("LogVPortBytes")) {
/* 157 */           logger.debug("VMODEM to CoCo: " + data + "  (" + (char)data + ")");
/*     */         }
/* 159 */         if (data >= 0) {
/*     */ 
/*     */           
/* 162 */           if (telmode == 1) {
/*     */             
/* 164 */             switch (data) {
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
/*     */               case 251:
/* 181 */                 data = this.skt.getInputStream().read();
/* 182 */                 this.skt.getOutputStream().write(255);
/* 183 */                 this.skt.getOutputStream().write(254);
/* 184 */                 this.skt.getOutputStream().write(data);
/*     */                 break;
/*     */               
/*     */               case 252:
/*     */               case 254:
/* 189 */                 data = this.skt.getInputStream().read();
/*     */                 break;
/*     */               
/*     */               case 253:
/* 193 */                 data = this.skt.getInputStream().read();
/* 194 */                 this.skt.getOutputStream().write(255);
/* 195 */                 this.skt.getOutputStream().write(252);
/* 196 */                 this.skt.getOutputStream().write(data);
/*     */                 break;
/*     */             } 
/*     */ 
/*     */             
/* 201 */             telmode = 0;
/*     */           } 
/* 203 */           switch (data) {
/*     */             
/*     */             case 255:
/* 206 */               telmode = 1;
/*     */               continue;
/*     */           } 
/*     */ 
/*     */           
/* 211 */           this.dwVSerialPorts.getPortInput(this.vport).write((byte)data);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 221 */         logger.info("end of stream from TCP client at " + this.clientHost + ":" + this.clientPort);
/* 222 */         if (this.skt.isConnected())
/*     */         {
/* 224 */           logger.debug("closing socket");
/* 225 */           this.skt.close();
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 232 */     catch (IOException e) {
/*     */       
/* 234 */       logger.warn("IO error in connection to " + this.clientHost + ":" + this.clientPort + " = " + e.getMessage());
/*     */     }
/* 236 */     catch (DWPortNotValidException e) {
/*     */       
/* 238 */       logger.warn(e.getMessage());
/*     */     }
/*     */     finally {
/*     */       
/* 242 */       if (this.vport > -1) {
/*     */         
/* 244 */         this.dwVSerialPorts.markDisconnected(this.vport);
/*     */ 
/*     */         
/*     */         try {
/* 248 */           this.dwVSerialPorts.getPortInput(this.vport).write("\r\n\r\nNO CARRIER\r\n".getBytes());
/*     */         }
/* 250 */         catch (IOException e) {
/*     */           
/* 252 */           logger.warn(e.getMessage());
/*     */         }
/* 254 */         catch (DWPortNotValidException e) {
/*     */           
/* 256 */           logger.warn(e.getMessage());
/*     */         } 
/*     */       } 
/* 259 */       logger.debug("thread exiting");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVModemConnThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */