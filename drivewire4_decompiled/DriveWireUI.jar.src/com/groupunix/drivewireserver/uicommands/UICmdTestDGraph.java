/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdTestDGraph
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "dgraph";
/*    */   DWUIClientThread clientref;
/*    */   
/*    */   public UICmdTestDGraph(DWUIClientThread dwuiClientThread) {
/* 15 */     this.clientref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "dgraph";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 25 */     DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_sectors", "630");
/*    */     
/* 27 */     for (int i = 0; i < 630; i++) {
/*    */       
/* 29 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_lsn", i + "");
/*    */       
/* 31 */       if (i % 2 == 0) {
/* 32 */         DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_writes", i + "");
/*    */       } else {
/* 34 */         DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 0, "_reads", i + "");
/*    */       } 
/*    */       
/* 37 */       int rndsec = (int)(Math.random() * 630.0D);
/* 38 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 1, "_lsn", rndsec + "");
/* 39 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 1, "_reads", "1");
/*    */       
/* 41 */       rndsec = (int)(Math.random() * 630.0D);
/* 42 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 2, "_lsn", rndsec + "");
/* 43 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 2, "_reads", rndsec + "");
/*    */       
/* 45 */       rndsec = (int)(Math.random() * 630.0D);
/* 46 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 3, "_lsn", rndsec + "");
/* 47 */       DriveWireServer.submitDiskEvent(this.clientref.getInstance(), 3, "_reads", "1");
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 52 */         Thread.sleep(15L);
/* 53 */       } catch (InterruptedException e) {
/*    */ 
/*    */         
/* 56 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 62 */     return new DWCommandResponse("Test data submitted at: " + System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 68 */     return "Test disk graphics";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 74 */     return "ui test dgraph";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 79 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdTestDGraph.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */