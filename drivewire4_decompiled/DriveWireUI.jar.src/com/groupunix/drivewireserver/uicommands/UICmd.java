/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmd
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "ui";
/*    */   private DWCommandList commands;
/*    */   
/*    */   public UICmd(DWUIClientThread ct) {
/* 18 */     this.commands = new DWCommandList(null);
/* 19 */     this.commands.addcommand(new UICmdInstance(ct));
/* 20 */     this.commands.addcommand(new UICmdServer(ct));
/* 21 */     this.commands.addcommand(new UICmdSync(ct));
/* 22 */     this.commands.addcommand(new UICmdTest(ct));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 28 */     return "ui";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 33 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 45 */     return this.commands.getShortHelp();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 51 */     return "ui [command]";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 57 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */