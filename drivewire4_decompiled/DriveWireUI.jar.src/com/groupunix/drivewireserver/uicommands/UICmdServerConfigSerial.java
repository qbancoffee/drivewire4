/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ public class UICmdServerConfigSerial
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "serial";
/*    */   
/*    */   public String getCommand() {
/* 14 */     return "serial";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 20 */     String res = new String();
/*    */     
/* 22 */     res = DriveWireServer.configserial + "";
/*    */     
/* 24 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 30 */     return "Show server config serial#";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 36 */     return "ui server config serial";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 41 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfigSerial.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */