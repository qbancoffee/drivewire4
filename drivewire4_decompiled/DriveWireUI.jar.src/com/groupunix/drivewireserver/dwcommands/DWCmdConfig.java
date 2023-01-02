/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ 
/*    */ public class DWCmdConfig
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "config";
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdConfig(DWProtocol dwProtocol, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProtocol;
/* 16 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 17 */     this.commands.addcommand(new DWCmdConfigShow(dwProtocol, this));
/* 18 */     this.commands.addcommand(new DWCmdConfigSet(dwProtocol, this));
/* 19 */     this.commands.addcommand(new DWCmdConfigSave(dwProtocol, this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "config";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 31 */     return this.commands;
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     if (cmdline.length() == 0)
/*    */     {
/* 38 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 40 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 48 */     return "Commands to manipulate the config";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 54 */     return "dw config [command]";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 60 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdConfig.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */