/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ public class DWCmdLog
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "log";
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdLog(DWProtocol dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/*    */     
/* 16 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 17 */     this.commands.addcommand(new DWCmdLogShow(this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "log";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 29 */     return this.commands;
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 34 */     if (cmdline.length() == 0)
/*    */     {
/* 36 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 38 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 45 */     return "View the server log";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 51 */     return "dw log [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 56 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdLog.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */