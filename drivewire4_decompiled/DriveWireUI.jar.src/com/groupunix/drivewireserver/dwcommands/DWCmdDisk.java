/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdDisk
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "disk";
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdDisk(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */     
/* 17 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/*    */     
/* 19 */     this.commands.addcommand(new DWCmdDiskShow(dwProto, this));
/* 20 */     this.commands.addcommand(new DWCmdDiskEject(dwProto, this));
/* 21 */     this.commands.addcommand(new DWCmdDiskInsert(dwProto, this));
/* 22 */     this.commands.addcommand(new DWCmdDiskReload(dwProto, this));
/* 23 */     this.commands.addcommand(new DWCmdDiskWrite(dwProto, this));
/* 24 */     this.commands.addcommand(new DWCmdDiskCreate(dwProto, this));
/* 25 */     this.commands.addcommand(new DWCmdDiskSet(dwProto, this));
/* 26 */     this.commands.addcommand(new DWCmdDiskDos(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 34 */     return "disk";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     if (cmdline.length() == 0)
/*    */     {
/* 41 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 43 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 48 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 55 */     return "Manage disks and disksets";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 61 */     return "dw disk [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 66 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */