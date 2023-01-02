/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowInstances
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 13 */     return "instances";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 20 */     return "show available instances";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 26 */     return "ui server show instances";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 32 */     String txt = new String();
/*    */     
/* 34 */     for (int i = 0; i < DriveWireServer.getNumHandlers(); i++)
/*    */     {
/* 36 */       txt = txt + DriveWireServer.getHandlerName(i) + "\n";
/*    */     }
/*    */ 
/*    */     
/* 40 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 45 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowInstances.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */