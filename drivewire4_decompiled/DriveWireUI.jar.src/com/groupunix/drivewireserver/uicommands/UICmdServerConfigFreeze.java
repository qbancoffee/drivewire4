/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerConfigFreeze
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "freeze";
/*    */   
/*    */   public String getCommand() {
/* 15 */     return "freeze";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 22 */     String[] args = cmdline.split(" ");
/*    */     
/* 24 */     if (args.length == 1)
/*    */     {
/* 26 */       return doSetFreeze(args[0]);
/*    */     }
/*    */ 
/*    */     
/* 30 */     return new DWCommandResponse(false, (byte)10, "Must specify freeze state");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 38 */     return "Set server configuration item";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 44 */     return "ui server config freeze [boolean]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetFreeze(String state) {
/* 55 */     if (state.equalsIgnoreCase("true")) {
/*    */       
/* 57 */       DriveWireServer.setConfigFreeze(true);
/* 58 */       return new DWCommandResponse("Config freeze set.");
/*    */     } 
/*    */ 
/*    */     
/* 62 */     DriveWireServer.setConfigFreeze(false);
/* 63 */     return new DWCommandResponse("Config freeze unset.");
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfigFreeze.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */