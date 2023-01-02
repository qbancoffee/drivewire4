/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortTCPServerThread
/*     */   implements Runnable
/*     */ {
/*  17 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortTCPServerThread");
/*     */   
/*  19 */   private int vport = -1;
/*     */   private Socket skt;
/*     */   private int conno;
/*     */   private boolean wanttodie = false;
/*  23 */   private int mode = 0;
/*     */   
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */   
/*     */   private static final int MODE_TELNET = 1;
/*     */   
/*     */   private static final int MODE_TERM = 3;
/*     */   
/*     */   public DWVPortTCPServerThread(DWProtocolHandler dwProto, int vport, int conno) throws DWConnectionNotValidException {
/*  32 */     logger.debug("init tcp server thread for conn " + conno);
/*  33 */     this.vport = vport;
/*  34 */     this.conno = conno;
/*     */     
/*  36 */     this.dwVSerialPorts = dwProto.getVPorts();
/*  37 */     this.mode = this.dwVSerialPorts.getListenerPool().getMode(conno);
/*  38 */     this.skt = this.dwVSerialPorts.getListenerPool().getConn(conno);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  44 */     Thread.currentThread().setName("tcpserv-" + Thread.currentThread().getId());
/*  45 */     Thread.currentThread().setPriority(5);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  51 */       this.dwVSerialPorts.getListenerPool().setConnPort(this.conno, this.vport);
/*     */       
/*  53 */       this.dwVSerialPorts.setConn(this.vport, this.conno);
/*     */ 
/*     */       
/*  56 */       logger.debug("run for conn " + this.conno);
/*     */       
/*  58 */       if (this.skt == null) {
/*     */         
/*  60 */         logger.warn("got a null socket, bailing out");
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*  65 */       this.dwVSerialPorts.markConnected(this.vport);
/*  66 */       this.dwVSerialPorts.setUtilMode(this.vport, 5);
/*  67 */       this.dwVSerialPorts.setPortOutput(this.vport, this.skt.getOutputStream());
/*     */       
/*  69 */       int lastbyte = -1;
/*     */       
/*  71 */       while (!this.wanttodie && !this.skt.isClosed() && (this.dwVSerialPorts.isOpen(this.vport) || this.mode == 3)) {
/*     */ 
/*     */         
/*  74 */         int databyte = this.skt.getInputStream().read();
/*  75 */         if (databyte == -1) {
/*     */           
/*  77 */           this.wanttodie = true;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/*  82 */         if ((this.mode == 1 || this.mode == 3) && (this.dwVSerialPorts.getPD_INT(this.vport) != 0 || this.dwVSerialPorts.getPD_QUT(this.vport) != 0)) {
/*     */ 
/*     */ 
/*     */           
/*  86 */           if (lastbyte != 13 || (databyte != 10 && databyte != 0)) {
/*     */ 
/*     */ 
/*     */             
/*  90 */             this.dwVSerialPorts.writeToCoco(this.vport, (byte)databyte);
/*  91 */             lastbyte = databyte;
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/*  96 */         this.dwVSerialPorts.writeToCoco(this.vport, (byte)databyte);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 101 */       this.dwVSerialPorts.markDisconnected(this.vport);
/* 102 */       this.dwVSerialPorts.setPortOutput(this.vport, null);
/*     */       
/* 104 */       if (!this.skt.isClosed())
/*     */       {
/* 106 */         this.skt.close();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 111 */       if (this.skt != null && this.mode != 3)
/*     */       {
/* 113 */         if (this.skt.isConnected())
/*     */         {
/*     */           
/* 116 */           logger.debug("exit stage 1, flush buffer");
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/* 121 */             while (this.dwVSerialPorts.bytesWaiting(this.vport) > 0 && this.dwVSerialPorts.isOpen(this.vport))
/*     */             {
/* 123 */               logger.debug("pause for the cause: " + this.dwVSerialPorts.bytesWaiting(this.vport) + " bytes left");
/* 124 */               Thread.sleep(100L);
/*     */             }
/*     */           
/* 127 */           } catch (InterruptedException e) {
/*     */ 
/*     */             
/* 130 */             e.printStackTrace();
/*     */           } 
/*     */           
/* 133 */           logger.debug("exit stage 2, send peer signal");
/*     */           
/* 135 */           this.dwVSerialPorts.closePort(this.vport);
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 143 */     catch (DWPortNotValidException e) {
/*     */       
/* 145 */       logger.error(e.getMessage());
/*     */     }
/* 147 */     catch (IOException e) {
/*     */       
/* 149 */       logger.error(e.getMessage());
/*     */     }
/* 151 */     catch (DWConnectionNotValidException e) {
/*     */       
/* 153 */       logger.error(e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 158 */       this.dwVSerialPorts.getListenerPool().clearConn(this.conno);
/*     */     }
/* 160 */     catch (DWConnectionNotValidException e) {
/*     */       
/* 162 */       logger.error(e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/* 166 */     logger.debug("thread exiting");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 174 */     logger.debug("shutting down");
/* 175 */     this.wanttodie = true;
/*     */     
/*     */     try {
/* 178 */       this.skt.close();
/*     */     }
/* 180 */     catch (IOException e) {
/*     */       
/* 182 */       logger.warn("IOException while closing socket: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortTCPServerThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */