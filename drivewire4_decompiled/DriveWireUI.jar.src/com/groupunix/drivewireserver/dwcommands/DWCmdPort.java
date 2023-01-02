/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdPort
/*    */   extends DWCommand {
/*    */   static final String command = "port";
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdPort(DWProtocolHandler dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/* 15 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/* 16 */     this.commands.addcommand(new DWCmdPortShow(dwProto, this));
/* 17 */     this.commands.addcommand(new DWCmdPortClose(dwProto, this));
/* 18 */     this.commands.addcommand(new DWCmdPortOpen(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "port";
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
/*    */   public String getShortHelp() {
/* 44 */     return "Manage virtual serial ports";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 50 */     return "dw port [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 55 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdPort.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */