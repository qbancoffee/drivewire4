/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdInstance
/*    */   extends DWCommand {
/*    */   static final String command = "instance";
/* 11 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */   
/*    */   public UICmdInstance(DWUIClientThread dwuiClientThread) {
/* 15 */     this.commands.addcommand(new UICmdInstanceAttach(dwuiClientThread));
/* 16 */     this.commands.addcommand(new UICmdInstanceConfig(dwuiClientThread));
/* 17 */     this.commands.addcommand(new UICmdInstanceDisk(dwuiClientThread));
/* 18 */     this.commands.addcommand(new UICmdInstanceReset(dwuiClientThread));
/* 19 */     this.commands.addcommand(new UICmdInstanceStatus(dwuiClientThread));
/* 20 */     this.commands.addcommand(new UICmdInstanceMIDIStatus(dwuiClientThread));
/* 21 */     this.commands.addcommand(new UICmdInstancePrinterStatus(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 27 */     return "instance";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 32 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 38 */     return "Instance commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 44 */     return "ui instance [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstance.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */