/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdServerConfig
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "config";
/* 12 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */ 
/*    */   
/*    */   public UICmdServerConfig(DWUIClientThread dwuiClientThread) {
/* 17 */     this.commands.addcommand(new UICmdServerConfigShow());
/* 18 */     this.commands.addcommand(new UICmdServerConfigSet());
/* 19 */     this.commands.addcommand(new UICmdServerConfigSerial());
/* 20 */     this.commands.addcommand(new UICmdServerConfigWrite());
/* 21 */     this.commands.addcommand(new UICmdServerConfigFreeze());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 27 */     return "config";
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
/* 38 */     return "Configuration commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 44 */     return "ui server config [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfig.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */