/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ public class DWCmdServer
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "server";
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServer(DWProtocol dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/* 15 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 16 */     this.commands.addcommand(new DWCmdServerStatus(dwProto, this));
/* 17 */     this.commands.addcommand(new DWCmdServerShow(dwProto, this));
/* 18 */     this.commands.addcommand(new DWCmdServerList(this));
/* 19 */     this.commands.addcommand(new DWCmdServerDir(this));
/* 20 */     this.commands.addcommand(new DWCmdServerTurbo(dwProto, this));
/* 21 */     this.commands.addcommand(new DWCmdServerPrint(dwProto, this));
/* 22 */     this.commands.addcommand(new DWCmdServerHelp(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 34 */     return "server";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 39 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 45 */     if (cmdline.length() == 0)
/*    */     {
/* 47 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 49 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 54 */     return "Various server based tools";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 60 */     return "dw server [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 65 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */