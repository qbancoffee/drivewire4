/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdServerFile
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "file";
/* 12 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */ 
/*    */   
/*    */   public UICmdServerFile(DWUIClientThread dwuiClientThread) {
/* 17 */     this.commands.addcommand(new UICmdServerFileRoots(dwuiClientThread));
/* 18 */     this.commands.addcommand(new UICmdServerFileDefaultDir(dwuiClientThread));
/* 19 */     this.commands.addcommand(new UICmdServerFileDir(dwuiClientThread));
/* 20 */     this.commands.addcommand(new UICmdServerFileInfo(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "file";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 37 */     return "File commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 43 */     return "ui server file [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 48 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */