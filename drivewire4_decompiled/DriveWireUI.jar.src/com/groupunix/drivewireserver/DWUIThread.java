/*     */ package com.groupunix.drivewireserver;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ public class DWUIThread
/*     */   implements Runnable
/*     */ {
/*  16 */   private static final Logger logger = Logger.getLogger("DWUIThread");
/*     */   
/*     */   private int tcpport;
/*     */   private boolean wanttodie = false;
/*  20 */   private ServerSocket srvr = null;
/*     */   
/*  22 */   private LinkedList<DWUIClientThread> clientThreads = new LinkedList<DWUIClientThread>();
/*     */   
/*  24 */   private int dropppedevents = 0;
/*     */   
/*     */   private int lastQueueSize;
/*     */ 
/*     */   
/*     */   public DWUIThread(int port) {
/*  30 */     this.tcpport = port;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void die() {
/*  36 */     this.wanttodie = true;
/*     */     
/*     */     try {
/*  39 */       for (DWUIClientThread ct : this.clientThreads)
/*     */       {
/*     */         
/*  42 */         ct.die();
/*     */       }
/*     */ 
/*     */       
/*  46 */       if (this.srvr != null)
/*     */       {
/*  48 */         this.srvr.close();
/*     */       }
/*     */     }
/*  51 */     catch (IOException e) {
/*     */       
/*  53 */       logger.warn("IO Error closing socket: " + e.getMessage());
/*     */     }
/*  55 */     catch (ConcurrentModificationException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  65 */     Thread.currentThread().setName("dwUIserver-" + Thread.currentThread().getId());
/*  66 */     Thread.currentThread().setPriority(5);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  74 */       this.srvr = new ServerSocket(this.tcpport);
/*  75 */       logger.info("UI listening on port " + this.srvr.getLocalPort());
/*     */     
/*     */     }
/*  78 */     catch (IOException e2) {
/*     */       
/*  80 */       logger.error("Error opening UI socket: " + e2.getClass().getSimpleName() + " " + e2.getMessage());
/*  81 */       this.wanttodie = true;
/*     */ 
/*     */       
/*  84 */       if (DriveWireServer.serverconfig.getBoolean("UIorBust", true))
/*     */       {
/*  86 */         DriveWireServer.shutdown();
/*     */       }
/*     */     } 
/*     */     
/*  90 */     while (!this.wanttodie && !this.srvr.isClosed()) {
/*     */ 
/*     */       
/*  93 */       Socket skt = null;
/*     */       
/*     */       try {
/*  96 */         skt = this.srvr.accept();
/*     */         
/*  98 */         if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false)) {
/*  99 */           logger.debug("new UI connection from " + skt.getInetAddress().getHostAddress());
/*     */         }
/* 101 */         Thread uiclientthread = new Thread(new DWUIClientThread(skt, this.clientThreads));
/* 102 */         uiclientthread.setDaemon(true);
/* 103 */         uiclientthread.start();
/*     */ 
/*     */       
/*     */       }
/* 107 */       catch (IOException e1) {
/*     */         
/* 109 */         if (this.wanttodie) {
/* 110 */           logger.debug("IO error (while dying): " + e1.getMessage());
/*     */         } else {
/* 112 */           logger.warn("IO error: " + e1.getMessage());
/*     */         } 
/* 114 */         this.wanttodie = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 119 */     if (this.srvr != null) {
/*     */       
/*     */       try {
/*     */         
/* 123 */         this.srvr.close();
/*     */       }
/* 125 */       catch (IOException e) {
/*     */         
/* 127 */         logger.error("error closing server socket: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 132 */     logger.debug("exiting");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(DWEvent evt) {
/* 142 */     synchronized (this.clientThreads) {
/*     */       
/* 144 */       Iterator<DWUIClientThread> itr = this.clientThreads.iterator();
/*     */       
/* 146 */       while (itr.hasNext()) {
/*     */         
/* 148 */         DWUIClientThread client = itr.next();
/* 149 */         LinkedBlockingQueue<DWEvent> queue = client.getEventQueue();
/*     */         
/* 151 */         synchronized (queue) {
/*     */           
/* 153 */           if (queue != null && (!client.isDropLog() || evt.getEventType() != 76)) {
/*     */             
/* 155 */             this.lastQueueSize = queue.size();
/* 156 */             if (queue.size() < 500) {
/*     */               
/* 158 */               queue.add(evt);
/*     */             }
/* 160 */             else if (queue.size() < 800 && evt.getEventType() != 76) {
/*     */               
/* 162 */               queue.add(evt);
/*     */             }
/*     */             else {
/*     */               
/* 166 */               this.dropppedevents++;
/* 167 */               System.out.println("queue drop: " + queue.size() + "/" + this.dropppedevents + "  " + evt.getEventType() + " thr " + client.getThreadName() + " cmd " + client.getCurCmd() + " state " + client.getState());
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumUIClients() {
/* 179 */     return this.clientThreads.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getQueueSize() {
/* 187 */     return this.lastQueueSize;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWUIThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */