/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdServerHelpReload
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerHelpReload(DWProtocol dwProtocol, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProtocol;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 23 */     return "reload";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "Reload help topics";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "dw help reload";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 40 */     return doHelpReload(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doHelpReload(String cmdline) {
/* 48 */     ((DWProtocolHandler)this.dwProto).getHelp().reload();
/*    */     
/* 50 */     return new DWCommandResponse("Reloaded help topics.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerHelpReload.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */