/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowVersion
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 13 */     return "version";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 19 */     return "show server version";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 25 */     return "ui server show version";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     String txt = new String();
/*    */     
/* 33 */     txt = "DriveWire version 4.0.7a (03/22/2012)";
/*    */     
/* 35 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 40 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowVersion.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */