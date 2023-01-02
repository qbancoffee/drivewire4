/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DWCommand
/*    */ {
/*  8 */   private DWCommand parentcmd = null;
/*    */ 
/*    */   
/*    */   public abstract String getCommand();
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 15 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 20 */     return new DWCommandResponse(false, (byte)-52, "Not implemented (yet?).");
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommand getParentCmd() {
/* 25 */     return this.parentcmd;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setParentCmd(DWCommand cmd) {
/* 30 */     this.parentcmd = cmd;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 36 */     return "Not implemented.";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 41 */     return "Not implemented.";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 46 */     return false;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCommand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */