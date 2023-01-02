/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdInstanceReset
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "reset";
/* 12 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */ 
/*    */   
/*    */   public UICmdInstanceReset(DWUIClientThread dwuiClientThread) {
/* 17 */     this.commands.addcommand(new UICmdInstanceResetProtodev(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "reset";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 29 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 35 */     return "Restart commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 41 */     return "ui instance reset [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 46 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceReset.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */