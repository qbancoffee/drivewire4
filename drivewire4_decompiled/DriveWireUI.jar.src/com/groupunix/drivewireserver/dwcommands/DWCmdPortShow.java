/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdPortShow
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdPortShow(DWProtocolHandler dwProto, DWCommand parent) {
/* 15 */     setParentCmd(parent);
/* 16 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 21 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 27 */     return "Show port status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 33 */     return "dw port show";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     return doPortShow();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doPortShow() {
/* 44 */     String text = new String();
/*    */     
/* 46 */     text = text + "\r\nCurrent port status:\r\n\n";
/*    */     
/* 48 */     for (int i = 0; i < 15; i++) {
/*    */       
/* 50 */       text = text + String.format("%6s", new Object[] { this.dwProto.getVPorts().prettyPort(i) });
/*    */ 
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 56 */         if (this.dwProto.getVPorts().isOpen(i)) {
/*    */           
/* 58 */           text = text + String.format(" %-8s", new Object[] { "open(" + this.dwProto.getVPorts().getOpen(i) + ")" });
/*    */ 
/*    */ 
/*    */           
/* 62 */           text = text + String.format(" %-9s", new Object[] { "buf: " + this.dwProto.getVPorts().bytesWaiting(i) });
/*    */         
/*    */         }
/*    */         else {
/*    */           
/* 67 */           text = text + " closed";
/*    */         } 
/*    */ 
/*    */         
/* 71 */         if (this.dwProto.getVPorts().getUtilMode(i) != 0) {
/* 72 */           text = text + DWUtils.prettyUtilMode(this.dwProto.getVPorts().getUtilMode(i));
/*    */         
/*    */         }
/*    */       }
/* 76 */       catch (DWPortNotValidException e) {
/*    */         
/* 78 */         text = text + " Error: " + e.getMessage();
/*    */       } 
/*    */ 
/*    */       
/* 82 */       text = text + "\r\n";
/*    */     } 
/*    */     
/* 85 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 91 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdPortShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */