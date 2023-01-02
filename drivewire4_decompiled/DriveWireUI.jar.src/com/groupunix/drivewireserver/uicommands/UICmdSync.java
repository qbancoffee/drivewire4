/*     */ package com.groupunix.drivewireserver.uicommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWEvent;
/*     */ import com.groupunix.drivewireserver.DWUIClientThread;
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*     */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*     */ import java.io.IOException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UICmdSync
/*     */   extends DWCommand
/*     */ {
/*     */   static final String command = "sync";
/*  18 */   private static final Logger logger = Logger.getLogger("DWServer.DWUtilUIThread");
/*     */   
/*     */   private DWUIClientThread dwuiref;
/*  21 */   private DWEvent lastevt = new DWEvent((byte)0);
/*     */ 
/*     */   
/*     */   public UICmdSync(DWUIClientThread dwuiClientThread) {
/*  25 */     this.dwuiref = dwuiClientThread;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  30 */     return "sync";
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  35 */     boolean wanttodie = false;
/*     */     
/*  37 */     logger.debug("adding status sync client");
/*     */ 
/*     */     
/*     */     try {
/*  41 */       this.dwuiref.getOutputStream().write(13);
/*     */ 
/*     */ 
/*     */       
/*  45 */       sendEvent(DriveWireServer.getServerStatusEvent());
/*     */       
/*  47 */       for (DWEvent e : DriveWireServer.getLogCache())
/*     */       {
/*  49 */         sendEvent(e);
/*     */       }
/*     */ 
/*     */       
/*  53 */       this.dwuiref.setDropLog(false);
/*     */     }
/*  55 */     catch (IOException e1) {
/*     */       
/*  57 */       logger.debug("immediate I/O error: " + e1.getMessage());
/*  58 */       wanttodie = true;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     while (!wanttodie && !this.dwuiref.getSocket().isClosed()) {
/*     */ 
/*     */       
/*     */       try {
/*  68 */         sendEvent(this.dwuiref.getEventQueue().take());
/*     */       }
/*  70 */       catch (InterruptedException e) {
/*     */         
/*  72 */         wanttodie = true;
/*     */       }
/*  74 */       catch (IOException e) {
/*     */         
/*  76 */         wanttodie = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  81 */     logger.debug("removing status sync client");
/*     */     
/*  83 */     return new DWCommandResponse(false, (byte)-1, "Sync closed");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendEvent(DWEvent msg) throws IOException {
/*  89 */     for (String key : msg.getParamKeys()) {
/*     */ 
/*     */ 
/*     */       
/*  93 */       if (!this.lastevt.hasParam(key) || !this.lastevt.getParam(key).equals(msg.getParam(key))) {
/*     */         
/*  95 */         this.dwuiref.getOutputStream().write((key + ':' + msg.getParam(key)).getBytes());
/*  96 */         this.dwuiref.getOutputStream().write(13);
/*  97 */         this.lastevt.setParam(key, msg.getParam(key));
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     this.dwuiref.getOutputStream().write(msg.getEventType());
/* 102 */     this.dwuiref.getOutputStream().write(13);
/* 103 */     this.dwuiref.getOutputStream().flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/* 109 */     return "Sync status (real time)";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/* 115 */     return "ui sync";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 120 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdSync.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */