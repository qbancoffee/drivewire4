/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerTerminate
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "terminate";
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerTerminate(DWUIClientThread dwuiClientThread) {
/* 17 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 23 */     return "terminate";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 28 */     DriveWireServer.shutdown();
/* 29 */     return new DWCommandResponse("Server shutdown requested.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 36 */     return "Terminate the server";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 42 */     return "ui server terminate";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 47 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerTerminate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */