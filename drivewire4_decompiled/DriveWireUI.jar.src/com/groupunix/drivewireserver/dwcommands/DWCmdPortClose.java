/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdPortClose
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdPortClose(DWProtocolHandler dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 19 */     return "close";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 24 */     return "Close port #";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 30 */     return "dw port close #";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 35 */     if (cmdline.length() == 0)
/*    */     {
/* 37 */       return new DWCommandResponse(false, (byte)10, "dw port close requires a port # as an argument");
/*    */     }
/* 39 */     return doPortClose(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doPortClose(String port) {
/*    */     try {
/* 49 */       int portno = Integer.parseInt(port);
/*    */       
/* 51 */       this.dwProto.getVPorts().closePort(portno);
/*    */       
/* 53 */       return new DWCommandResponse("Port #" + portno + " closed.");
/*    */     
/*    */     }
/* 56 */     catch (NumberFormatException e) {
/*    */       
/* 58 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric port #");
/*    */     }
/* 60 */     catch (DWPortNotValidException e) {
/*    */       
/* 62 */       return new DWCommandResponse(false, (byte)-116, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 69 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdPortClose.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */