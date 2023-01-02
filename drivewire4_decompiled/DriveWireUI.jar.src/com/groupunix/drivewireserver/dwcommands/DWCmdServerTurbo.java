/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWSerialDevice;
/*    */ import gnu.io.UnsupportedCommOperationException;
/*    */ 
/*    */ 
/*    */ public class DWCmdServerTurbo
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerTurbo(DWProtocol dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "turbo";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 26 */     return "Turn on DATurbo mode (testing only)";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 32 */     return "dw server turbo";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 37 */     return doServerTurbo();
/*    */   }
/*    */ 
/*    */   
/*    */   private DWCommandResponse doServerTurbo() {
/* 42 */     String text = new String();
/*    */ 
/*    */     
/* 45 */     DWSerialDevice serdev = (DWSerialDevice)this.dwProto.getProtoDev();
/*    */ 
/*    */     
/*    */     try {
/* 49 */       serdev.enableDATurbo();
/* 50 */       text = "Device is now in DATurbo mode";
/*    */     }
/* 52 */     catch (UnsupportedCommOperationException e) {
/*    */       
/* 54 */       text = "Failed to enable DATurbo mode: " + e.getMessage();
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 59 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 65 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerTurbo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */