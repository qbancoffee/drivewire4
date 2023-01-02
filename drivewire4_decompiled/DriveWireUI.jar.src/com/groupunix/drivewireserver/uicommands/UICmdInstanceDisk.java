/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdInstanceDisk
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "disk";
/* 12 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */ 
/*    */   
/*    */   public UICmdInstanceDisk(DWUIClientThread dwuiClientThread) {
/* 17 */     this.commands.addcommand(new UICmdInstanceDiskShow(dwuiClientThread));
/* 18 */     this.commands.addcommand(new UICmdInstanceDiskSerial(dwuiClientThread));
/* 19 */     this.commands.addcommand(new UICmdInstanceDiskStatus(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "disk";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 30 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 37 */     return "Instance disk commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 43 */     return "ui instance disk [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 48 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */