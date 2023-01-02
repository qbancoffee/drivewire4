/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ 
/*    */ public class UICmdInstanceStatus
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread clientref;
/*    */   
/*    */   public UICmdInstanceStatus(DWUIClientThread dwuiClientThread) {
/* 16 */     this.clientref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "status";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 27 */     return "show instance status";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 32 */     return "ui instance status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     String txt = "";
/*    */     
/* 40 */     DWProtocolHandler ph = (DWProtocolHandler)DriveWireServer.getHandler(this.clientref.getInstance());
/*    */     
/* 42 */     txt = "name: " + ph.getConfig().getString("Name", "not set") + "\n";
/*    */     
/* 44 */     txt = txt + "connected: " + ph.connected() + "\n";
/*    */     
/* 46 */     txt = txt + "devicetype: " + ph.getProtoDev().getDeviceType() + "\n";
/* 47 */     txt = txt + "devicerate: " + ph.getProtoDev().getRate() + "\n";
/* 48 */     txt = txt + "devicename: " + ph.getProtoDev().getDeviceName() + "\n";
/* 49 */     txt = txt + "deviceconnected: " + ph.getProtoDev().connected() + "\n";
/*    */     
/* 51 */     txt = txt + "lastopcode: " + DWUtils.prettyOP(ph.getLastOpcode()) + "\n";
/* 52 */     txt = txt + "lastgetstat: " + DWUtils.prettySS(ph.getLastGetStat()) + "\n";
/* 53 */     txt = txt + "lastsetstat: " + DWUtils.prettySS(ph.getLastSetStat()) + "\n";
/* 54 */     txt = txt + "lastlsn: " + DWUtils.int3(ph.getLastLSN()) + "\n";
/* 55 */     txt = txt + "lastdrive: " + ph.getLastDrive() + "\n";
/* 56 */     txt = txt + "lasterror: " + ph.getLastError() + "\n";
/* 57 */     txt = txt + "lastchecksum: " + ph.getLastChecksum() + "\n";
/*    */ 
/*    */ 
/*    */     
/* 61 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 67 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */