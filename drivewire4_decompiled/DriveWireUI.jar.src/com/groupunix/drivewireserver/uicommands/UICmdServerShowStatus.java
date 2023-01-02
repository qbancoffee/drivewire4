/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowStatus
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 13 */     return "status";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 20 */     return "show server status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 26 */     return "ui server show status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 32 */     String text = new String();
/*    */     
/* 34 */     text = text + "version: 4.0.7a\n";
/* 35 */     text = text + "versiondate: 03/22/2012\n";
/*    */     
/* 37 */     text = text + "totmem: " + (Runtime.getRuntime().totalMemory() / 1024L) + "\n";
/* 38 */     text = text + "freemem: " + (Runtime.getRuntime().freeMemory() / 1024L) + "\n";
/*    */     
/* 40 */     text = text + "instances: " + DriveWireServer.getNumHandlers() + "\n";
/* 41 */     text = text + "configpath: " + DriveWireServer.serverconfig.getBasePath() + "\n";
/*    */     
/* 43 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 48 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */