/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmd
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "dw";
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmd(DWProtocol dwProtocol) {
/* 18 */     this.dwProto = dwProtocol;
/*    */     
/* 20 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 21 */     this.commands.addcommand(new DWCmdDisk((DWProtocolHandler)dwProtocol, this));
/* 22 */     this.commands.addcommand(new DWCmdServer(dwProtocol, this));
/* 23 */     this.commands.addcommand(new DWCmdConfig(dwProtocol, this));
/* 24 */     this.commands.addcommand(new DWCmdPort((DWProtocolHandler)dwProtocol, this));
/* 25 */     this.commands.addcommand(new DWCmdLog(dwProtocol, this));
/* 26 */     this.commands.addcommand(new DWCmdNet((DWProtocolHandler)dwProtocol, this));
/* 27 */     this.commands.addcommand(new DWCmdMidi((DWProtocolHandler)dwProtocol, this));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 33 */     return "dw";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 38 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 45 */     if (cmdline.length() == 0)
/*    */     {
/* 47 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/*    */     
/* 50 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 56 */     return "Manage all aspects of the server";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 62 */     return "dw [command]";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 68 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmd.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */