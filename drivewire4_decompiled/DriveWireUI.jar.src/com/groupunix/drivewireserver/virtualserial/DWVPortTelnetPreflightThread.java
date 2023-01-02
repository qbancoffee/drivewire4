/*     */ package com.groupunix.drivewireserver.virtualserial;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPortTelnetPreflightThread
/*     */   implements Runnable
/*     */ {
/*  21 */   private static final Logger logger = Logger.getLogger("DWServer.DWVPortTelnetPreflightThread");
/*     */   
/*     */   private Socket skt;
/*     */   
/*     */   private int vport;
/*     */   
/*     */   private boolean banner = false;
/*     */   
/*     */   private boolean telnet = false;
/*     */   
/*     */   private DWVSerialPorts dwVSerialPorts;
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWVPortTelnetPreflightThread(DWProtocolHandler dwProto, int vport, Socket skt, boolean doTelnet, boolean doBanner) {
/*  36 */     this.vport = vport;
/*  37 */     this.skt = skt;
/*     */     
/*  39 */     this.banner = doBanner;
/*  40 */     this.telnet = doTelnet;
/*  41 */     this.dwProto = dwProto;
/*  42 */     this.dwVSerialPorts = dwProto.getVPorts();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  48 */     Thread.currentThread().setName("tcppre-" + Thread.currentThread().getId());
/*  49 */     Thread.currentThread().setPriority(1);
/*     */     
/*  51 */     logger.info("preflight checks for new connection from " + this.skt.getInetAddress().getHostName());
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  56 */       if (this.telnet) {
/*  57 */         this.skt.getOutputStream().write("DriveWire Telnet Server 4.0.7a\r\n\n".getBytes());
/*     */       }
/*     */       
/*  60 */       if (this.telnet == true) {
/*     */ 
/*     */         
/*  63 */         byte[] buf = new byte[9];
/*     */         
/*  65 */         buf[0] = -1;
/*  66 */         buf[1] = -5;
/*  67 */         buf[2] = 1;
/*  68 */         buf[3] = -1;
/*  69 */         buf[4] = -5;
/*  70 */         buf[5] = 3;
/*  71 */         buf[6] = -1;
/*  72 */         buf[7] = -3;
/*  73 */         buf[8] = -13;
/*     */ 
/*     */         
/*  76 */         this.skt.getOutputStream().write(buf, 0, 9);
/*     */ 
/*     */ 
/*     */         
/*  80 */         for (int i = 0; i < 9; i++)
/*     */         {
/*  82 */           this.skt.getInputStream().read();
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  87 */       if (this.skt.isClosed()) {
/*     */ 
/*     */         
/*  90 */         logger.debug("thread exiting after auth");
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*  95 */       if (this.dwProto.getConfig().containsKey("TelnetBannerFile") && this.banner == true)
/*     */       {
/*  97 */         displayFile(this.skt.getOutputStream(), this.dwProto.getConfig().getString("TelnetBannerFile"));
/*     */       
/*     */       }
/*     */     }
/* 101 */     catch (IOException e) {
/*     */       
/* 103 */       logger.warn("IOException: " + e.getMessage());
/*     */       
/* 105 */       if (this.skt.isConnected()) {
/*     */         
/* 107 */         logger.debug("closing socket");
/*     */         
/*     */         try {
/* 110 */           this.skt.close();
/* 111 */         } catch (IOException e1) {
/*     */           
/* 113 */           logger.warn(e1.getMessage());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     if (!this.skt.isClosed()) {
/*     */ 
/*     */       
/* 124 */       logger.debug("Preflight success for " + this.skt.getInetAddress().getHostName());
/*     */ 
/*     */       
/* 127 */       int conno = this.dwVSerialPorts.getListenerPool().addConn(this.vport, this.skt, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 133 */         this.dwVSerialPorts.sendConnectionAnnouncement(this.vport, conno, this.skt.getLocalPort(), this.skt.getInetAddress().getHostAddress());
/*     */       }
/* 135 */       catch (DWPortNotValidException e) {
/*     */         
/* 137 */         logger.error("in announce: " + e.getMessage());
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 142 */     logger.debug("exiting");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void displayFile(OutputStream outputStream, String fname) {
/*     */     try {
/* 162 */       FileInputStream fstream = new FileInputStream(fname);
/*     */       
/* 164 */       DataInputStream in = new DataInputStream(fstream);
/*     */       
/* 166 */       BufferedReader br = new BufferedReader(new InputStreamReader(in));
/*     */ 
/*     */ 
/*     */       
/* 170 */       logger.debug("sending file '" + fname + "' to telnet client");
/*     */       String strLine;
/* 172 */       while ((strLine = br.readLine()) != null) {
/*     */         
/* 174 */         outputStream.write(strLine.getBytes());
/* 175 */         outputStream.write("\r\n".getBytes());
/*     */       } 
/*     */       
/* 178 */       fstream.close();
/*     */     
/*     */     }
/* 181 */     catch (FileNotFoundException e) {
/*     */       
/* 183 */       logger.warn("File not found: " + fname);
/*     */     }
/* 185 */     catch (IOException e1) {
/*     */       
/* 187 */       logger.warn(e1.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualserial/DWVPortTelnetPreflightThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */