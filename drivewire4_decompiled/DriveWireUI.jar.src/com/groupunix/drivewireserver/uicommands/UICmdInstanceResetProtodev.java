/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdInstanceResetProtodev
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "protodev";
/*    */   private DWUIClientThread uiref;
/*    */   
/*    */   public UICmdInstanceResetProtodev(DWUIClientThread dwuiClientThread) {
/* 17 */     this.uiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "protodev";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 27 */     String res = "Resetting protocol device in instance " + this.uiref.getInstance();
/*    */     
/* 29 */     DriveWireServer.getHandler(this.uiref.getInstance()).resetProtocolDevice();
/*    */     
/* 31 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 38 */     return "Reset protocol device";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 44 */     return "ui instance reset protodev";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceResetProtodev.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */