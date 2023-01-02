/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdNetShow
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdNetShow(DWProtocolHandler dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 19 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 25 */     return "Show networking status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 31 */     return "dw net show";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     return doNetShow();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doNetShow() {
/* 42 */     String text = new String();
/*    */     
/* 44 */     text = text + "\r\nDriveWire Network Connections:\r\n\n";
/*    */     
/*    */     int i;
/* 47 */     for (i = 0; i < 256; i++) {
/*    */ 
/*    */       
/*    */       try {
/*    */ 
/*    */         
/* 53 */         text = text + "Connection " + i + ": " + this.dwProto.getVPorts().getListenerPool().getConn(i).getInetAddress().getHostName() + ":" + this.dwProto.getVPorts().getListenerPool().getConn(i).getPort() + " (connected to port " + this.dwProto.getVPorts().prettyPort(this.dwProto.getVPorts().getListenerPool().getConnPort(i)) + ")\r\n";
/*    */       }
/* 55 */       catch (DWConnectionNotValidException e) {}
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 61 */     text = text + "\r\n";
/*    */     
/* 63 */     for (i = 0; i < 64; i++) {
/*    */       
/* 65 */       if (this.dwProto.getVPorts().getListenerPool().getListener(i) != null)
/*    */       {
/* 67 */         text = text + "Listener " + i + ": TCP port " + this.dwProto.getVPorts().getListenerPool().getListener(i).getLocalPort() + " (control port " + this.dwProto.getVPorts().prettyPort(this.dwProto.getVPorts().getListenerPool().getListenerPort(i)) + ")\r\n";
/*    */       }
/*    */     } 
/*    */     
/* 71 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 78 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdNetShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */