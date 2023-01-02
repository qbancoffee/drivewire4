/*     */ package com.groupunix.drivewireserver;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCmd;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*     */ import com.groupunix.drivewireserver.uicommands.UICmd;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.util.LinkedList;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class DWUIClientThread
/*     */   implements Runnable
/*     */ {
/*  19 */   private static final Logger logger = Logger.getLogger("DWUIClientThread");
/*     */   
/*     */   private Socket skt;
/*     */   private boolean wanttodie = false;
/*  23 */   private int instance = -1;
/*     */   
/*     */   private DWCommandList commands;
/*     */   
/*  27 */   private LinkedBlockingQueue<DWEvent> eventQueue = new LinkedBlockingQueue<DWEvent>();
/*     */   
/*     */   private LinkedList<DWUIClientThread> clientThreads;
/*     */   
/*     */   private BufferedOutputStream bufferedout;
/*     */   
/*     */   private boolean droplog = true;
/*     */   
/*  35 */   private String tname = "not set";
/*     */   
/*  37 */   private String curcmd = "not set";
/*     */   
/*  39 */   private String state = "not set";
/*     */ 
/*     */ 
/*     */   
/*     */   public DWUIClientThread(Socket skt, LinkedList<DWUIClientThread> clientThreads) {
/*  44 */     this.skt = skt;
/*  45 */     this.clientThreads = clientThreads;
/*     */     
/*  47 */     this.commands = new DWCommandList(null);
/*  48 */     this.commands.addcommand((DWCommand)new UICmd(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  55 */     this.state = "add to client threads";
/*  56 */     synchronized (this.clientThreads) {
/*     */       
/*  58 */       this.clientThreads.add(this);
/*     */     } 
/*     */ 
/*     */     
/*  62 */     this.tname = "dwUIcliIn-" + Thread.currentThread().getId();
/*     */     
/*  64 */     Thread.currentThread().setName(this.tname);
/*  65 */     Thread.currentThread().setPriority(5);
/*     */     
/*  67 */     if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false)) {
/*  68 */       logger.debug("run for client at " + this.skt.getInetAddress().getHostAddress());
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  73 */       this.state = "get output stream";
/*  74 */       this.bufferedout = new BufferedOutputStream(this.skt.getOutputStream());
/*     */ 
/*     */ 
/*     */       
/*  78 */       String cmd = new String();
/*     */       
/*  80 */       while (!this.skt.isClosed() && !this.wanttodie) {
/*     */         
/*  82 */         this.state = "read from output stream";
/*  83 */         int databyte = this.skt.getInputStream().read();
/*     */         
/*  85 */         if (databyte == -1) {
/*     */ 
/*     */           
/*  88 */           this.wanttodie = true;
/*     */           
/*     */           continue;
/*     */         } 
/*  92 */         if (databyte == 10) {
/*     */           
/*  94 */           if (cmd.length() > 0) {
/*     */             
/*  96 */             this.state = "do cmd";
/*  97 */             doCmd(cmd.trim());
/*  98 */             this.wanttodie = true;
/*  99 */             cmd = "";
/*     */           }  continue;
/*     */         } 
/* 102 */         if (databyte > -1 && databyte != 13)
/*     */         {
/* 104 */           cmd = cmd + Character.toString((char)databyte);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 110 */       this.bufferedout.close();
/* 111 */       this.skt.close();
/*     */       
/* 113 */       this.state = "close socket";
/*     */     
/*     */     }
/* 116 */     catch (IOException e) {
/*     */       
/* 118 */       logger.debug("IO Exception: " + e.getMessage());
/*     */     } 
/*     */     
/* 121 */     this.state = "remove from client threads";
/* 122 */     synchronized (this.clientThreads) {
/*     */       
/* 124 */       this.clientThreads.remove(this);
/*     */     } 
/*     */     
/* 127 */     if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false)) {
/* 128 */       logger.debug("exit");
/*     */     }
/* 130 */     this.state = "exit";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doCmd(String cmd) throws IOException {
/* 137 */     this.curcmd = cmd;
/*     */ 
/*     */     
/* 140 */     int div = cmd.indexOf(0);
/*     */ 
/*     */     
/* 143 */     if (div < 1) {
/*     */       
/* 145 */       sendUIresponse(new DWCommandResponse(false, (byte)-35, "Malformed UI request (no instance.. old UI?)"));
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 153 */       setInstance(Integer.parseInt(cmd.substring(0, div)));
/*     */     }
/* 155 */     catch (NumberFormatException e) {
/*     */       
/* 157 */       sendUIresponse(new DWCommandResponse(false, (byte)-35, "Malformed UI request (bad instance)"));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 162 */     if (cmd.length() < div + 2) {
/*     */       
/* 164 */       sendUIresponse(new DWCommandResponse(false, (byte)-35, "Malformed UI request (no command)"));
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 171 */     cmd = cmd.substring(div + 1);
/*     */     
/* 173 */     if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false)) {
/* 174 */       logger.debug("UI command '" + cmd + "' for instance " + this.instance);
/*     */     }
/*     */     
/* 177 */     int waits = 0;
/* 178 */     while (!DriveWireServer.isReady() && waits < 3000L) {
/*     */ 
/*     */       
/*     */       try {
/* 182 */         Thread.sleep(200L);
/* 183 */         waits = (int)(waits + 200L);
/*     */       }
/* 185 */       catch (InterruptedException e) {
/*     */         
/* 187 */         logger.warn("Interrupted while waiting for server to be ready");
/* 188 */         sendUIresponse(new DWCommandResponse(false, (byte)-51, "Interrupted while waiting for server to be ready"));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 193 */     if (!DriveWireServer.isReady()) {
/*     */       
/* 195 */       logger.warn("Timed out waiting for server to be ready");
/* 196 */       sendUIresponse(new DWCommandResponse(false, (byte)-51, "Timed out waiting for server to be ready"));
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 202 */     if (this.instance > -1) {
/*     */       
/* 204 */       waits = 0;
/* 205 */       while (!DriveWireServer.getHandler(this.instance).isReady() && waits < 3000L) {
/*     */ 
/*     */         
/*     */         try {
/* 209 */           Thread.sleep(200L);
/* 210 */           waits = (int)(waits + 200L);
/*     */         }
/* 212 */         catch (InterruptedException e) {
/*     */           
/* 214 */           logger.warn("Interrupted while waiting for instance ready");
/* 215 */           sendUIresponse(new DWCommandResponse(false, (byte)-50, "Interrupted while waiting for instance ready"));
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 220 */       if (!DriveWireServer.getHandler(this.instance).isReady()) {
/*     */         
/* 222 */         logger.warn("Timed out waiting for instance #" + this.instance + " to be ready");
/* 223 */         sendUIresponse(new DWCommandResponse(false, (byte)-50, "Timed out waiting for instance #" + this.instance + " to be ready"));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 229 */     DWCommandResponse resp = this.commands.parse(cmd);
/*     */     
/* 231 */     sendUIresponse(resp);
/*     */     
/* 233 */     this.bufferedout.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendUIresponse(DWCommandResponse resp) throws IOException {
/* 239 */     if (DriveWireServer.serverconfig.getBoolean("LogUIConnections", false))
/*     */     {
/* 241 */       if (resp.getResponseCode() == 0) {
/* 242 */         logger.debug("UI command success");
/*     */       } else {
/* 244 */         logger.debug("UI command failed: #" + (255 + resp.getResponseCode()) + ": " + resp.getResponseText());
/*     */       } 
/*     */     }
/*     */     
/* 248 */     this.bufferedout.write(0);
/* 249 */     this.bufferedout.write(resp.getResponseCode() & 0xFF);
/* 250 */     this.bufferedout.write(0);
/*     */ 
/*     */     
/* 253 */     if (resp.isUsebytes() && resp.getResponseBytes() != null) {
/* 254 */       this.bufferedout.write(resp.getResponseBytes());
/* 255 */     } else if (resp.getResponseText() != null) {
/* 256 */       this.bufferedout.write(resp.getResponseText().getBytes());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInstance(int handler) {
/* 263 */     this.instance = handler;
/*     */ 
/*     */     
/* 266 */     if (handler > -1)
/*     */     {
/* 268 */       if (!this.commands.validate("dw")) {
/* 269 */         this.commands.addcommand((DWCommand)new DWCmd(DriveWireServer.getHandler(handler)));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getInstance() {
/* 275 */     return this.instance;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedOutputStream getOutputStream() throws IOException {
/* 280 */     return this.bufferedout;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedInputStream getInputStream() throws IOException {
/* 285 */     return new BufferedInputStream(this.skt.getInputStream());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket getSocket() {
/* 291 */     return this.skt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void die() {
/* 298 */     this.wanttodie = true;
/* 299 */     if (this.skt != null) {
/*     */       
/*     */       try {
/*     */         
/* 303 */         this.skt.close();
/*     */       }
/* 305 */       catch (IOException e) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedBlockingQueue<DWEvent> getEventQueue() {
/* 313 */     return this.eventQueue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDropLog() {
/* 321 */     return this.droplog;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDropLog(boolean b) {
/* 326 */     this.droplog = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getThreadName() {
/* 333 */     return this.tname;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurCmd() {
/* 338 */     return this.curcmd;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getState() {
/* 343 */     return this.state;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWUIClientThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */