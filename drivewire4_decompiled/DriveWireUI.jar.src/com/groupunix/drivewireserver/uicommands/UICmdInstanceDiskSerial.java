/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdInstanceDiskSerial
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "serial";
/*    */   private DWUIClientThread uiref;
/*    */   
/*    */   public UICmdInstanceDiskSerial(DWUIClientThread dwuiClientThread) {
/* 18 */     this.uiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 23 */     return "serial";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 28 */     String res = new String();
/*    */ 
/*    */     
/* 31 */     DWProtocolHandler dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.uiref.getInstance());
/*    */     
/* 33 */     if (dwProto != null && dwProto.getDiskDrives() != null) {
/*    */       
/* 35 */       res = "" + dwProto.getDiskDrives().getDiskDriveSerial();
/*    */     }
/*    */     else {
/*    */       
/* 39 */       res = "-1";
/*    */     } 
/*    */     
/* 42 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 48 */     return "Show current disks";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 54 */     return "ui instance disk show";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 59 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceDiskSerial.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */