/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdServer
/*    */   extends DWCommand {
/*    */   static final String command = "server";
/* 11 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */   
/*    */   public UICmdServer(DWUIClientThread dwuiClientThread) {
/* 15 */     this.commands.addcommand(new UICmdServerShow(dwuiClientThread));
/* 16 */     this.commands.addcommand(new UICmdServerConfig(dwuiClientThread));
/* 17 */     this.commands.addcommand(new UICmdServerTerminate(dwuiClientThread));
/* 18 */     this.commands.addcommand(new UICmdServerFile(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "server";
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
/* 35 */     return "Server commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 41 */     return "ui server [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 46 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */