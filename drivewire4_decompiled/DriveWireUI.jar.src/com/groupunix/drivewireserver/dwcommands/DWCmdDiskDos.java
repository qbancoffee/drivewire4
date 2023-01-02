/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdDiskDos
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "dos";
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDiskDos(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */     
/* 17 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/*    */     
/* 19 */     this.commands.addcommand(new DWCmdDiskDosDir(dwProto, this));
/* 20 */     this.commands.addcommand(new DWCmdDiskDosList(dwProto, this));
/* 21 */     this.commands.addcommand(new DWCmdDiskDosFormat(dwProto, this));
/* 22 */     this.commands.addcommand(new DWCmdDiskDosAdd(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 28 */     return "dos";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 33 */     if (cmdline.length() == 0)
/*    */     {
/* 35 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 37 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 42 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 49 */     return "Manage DOS disks";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 55 */     return "dw disk dos [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 60 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDiskDos.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */