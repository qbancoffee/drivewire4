/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import com.groupunix.drivewireserver.virtualserial.DWVPortTCPConnectionThread;
/*    */ 
/*    */ 
/*    */ public class DWCmdPortOpen
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdPortOpen(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "open";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 26 */     return "Connect port # to tcp host:port";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 32 */     return "dw port open port# host:port";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     String[] args = cmdline.split(" ");
/*    */     
/* 40 */     if (args.length < 2)
/*    */     {
/* 42 */       return new DWCommandResponse(false, (byte)10, "dw port open requires a port # and tcphost:port as an argument");
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 47 */     return doPortOpen(args[0], args[1]);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doPortOpen(String port, String hostport) {
/* 54 */     int portno = 0;
/* 55 */     int tcpport = 0;
/* 56 */     String tcphost = new String();
/*    */ 
/*    */     
/*    */     try {
/* 60 */       portno = Integer.parseInt(port);
/*    */     }
/* 62 */     catch (NumberFormatException e) {
/*    */       
/* 64 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric port #");
/*    */     } 
/*    */     
/* 67 */     String[] tcpargs = hostport.split(":");
/*    */ 
/*    */     
/*    */     try {
/* 71 */       tcpport = Integer.parseInt(tcpargs[1]);
/* 72 */       tcphost = tcpargs[0];
/*    */       
/* 74 */       this.dwProto.getVPorts().openPort(portno);
/*    */       
/* 76 */       Thread cthread = new Thread((Runnable)new DWVPortTCPConnectionThread(this.dwProto, portno, tcphost, tcpport));
/* 77 */       cthread.start();
/*    */ 
/*    */       
/* 80 */       return new DWCommandResponse("Port #" + portno + " open.");
/*    */ 
/*    */     
/*    */     }
/* 84 */     catch (NumberFormatException e) {
/*    */       
/* 86 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric tcp port");
/*    */     }
/* 88 */     catch (DWPortNotValidException e) {
/*    */       
/* 90 */       return new DWCommandResponse(false, (byte)-116, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdPortOpen.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */