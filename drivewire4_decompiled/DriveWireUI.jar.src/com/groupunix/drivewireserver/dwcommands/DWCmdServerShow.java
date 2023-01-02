/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdServerShow
/*    */   extends DWCommand
/*    */ {
/*    */   private DWCommandList commands;
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerShow(DWProtocol dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/* 16 */     this.commands = new DWCommandList(this.dwProto, this.dwProto.getCMDCols());
/* 17 */     this.commands.addcommand(new DWCmdServerShowThreads(this));
/* 18 */     this.commands.addcommand(new DWCmdServerShowHandlers(this));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 23 */     return "show";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 28 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 34 */     return "Show various server information";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 40 */     return "dw server show [option]";
/*    */   }
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
/*    */   public boolean validate(String cmdline) {
/* 54 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */