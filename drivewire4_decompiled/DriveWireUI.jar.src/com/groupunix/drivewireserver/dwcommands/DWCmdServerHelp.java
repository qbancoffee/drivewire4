/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdServerHelp
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "help";
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerHelp(DWProtocol dwProtocol, DWCommand parent) {
/* 15 */     setParentCmd(parent);
/* 16 */     this.dwProto = dwProtocol;
/* 17 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 18 */     this.commands.addcommand(new DWCmdServerHelpShow(dwProtocol, this));
/* 19 */     this.commands.addcommand(new DWCmdServerHelpReload(dwProtocol, this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "help";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     if (cmdline.length() == 0)
/*    */     {
/* 33 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 35 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 40 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 46 */     return "Manage the help system";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 52 */     return "dw help [command]";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerHelp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */