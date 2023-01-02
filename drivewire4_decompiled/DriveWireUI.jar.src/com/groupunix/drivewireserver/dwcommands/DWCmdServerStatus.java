/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ 
/*    */ public class DWCmdServerStatus
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerStatus(DWProtocol dwProto, DWCommand parent) {
/* 12 */     setParentCmd(parent);
/* 13 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 18 */     return "status";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 25 */     return "Show server status information";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 31 */     return "dw server status";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     return doServerStatus();
/*    */   }
/*    */ 
/*    */   
/*    */   private DWCommandResponse doServerStatus() {
/* 41 */     String text = new String();
/*    */     
/* 43 */     text = text + "DriveWire version 4.0.7a (03/22/2012) status:\r\n\n";
/*    */     
/* 45 */     text = text + "Total memory:  " + (Runtime.getRuntime().totalMemory() / 1024L) + " KB";
/* 46 */     text = text + "\r\nFree memory:   " + (Runtime.getRuntime().freeMemory() / 1024L) + " KB";
/*    */     
/* 48 */     if (this.dwProto.getConfig().getString("DeviceType", "serial").equalsIgnoreCase("serial")) {
/*    */       
/* 50 */       if (this.dwProto.getProtoDev() != null)
/*    */       {
/* 52 */         text = text + "\r\n\nDevice:        " + this.dwProto.getConfig().getString("SerialDevice", "unknown");
/* 53 */         text = text + " (" + this.dwProto.getProtoDev().getRate() + " bps)\r\n";
/* 54 */         text = text + "CoCo Type:     " + this.dwProto.getConfig().getInt("CocoModel", 0) + "\r\n";
/*    */       }
/*    */       else
/*    */       {
/* 58 */         text = text + "\r\n\nDevice:        Serial, not started\r\n";
/*    */       }
/*    */     
/*    */     } else {
/*    */       
/* 63 */       text = text + "\r\n\nDevice:        TCP, listening on port " + this.dwProto.getConfig().getString("TCPDevicePort", "unknown");
/* 64 */       text = text + "\r\n";
/*    */     } 
/* 66 */     text = text + "\r\n";
/*    */     
/* 68 */     text = text + this.dwProto.getStatusText();
/*    */     
/* 70 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 76 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */