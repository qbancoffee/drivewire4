/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ public class UICmdInstanceAttach
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread clientref;
/*    */   
/*    */   public UICmdInstanceAttach(DWUIClientThread dwuiClientThread) {
/* 15 */     this.clientref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "attach";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "attach to instance #";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "ui instance attach #";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/*    */     try {
/* 43 */       int handler = Integer.parseInt(cmdline);
/*    */       
/* 45 */       if (DriveWireServer.isValidHandlerNo(handler)) {
/*    */ 
/*    */         
/* 48 */         this.clientref.setInstance(handler);
/* 49 */         return new DWCommandResponse("Attached to instance " + handler);
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 54 */       return new DWCommandResponse(false, (byte)-115, "Invalid handler number");
/*    */     
/*    */     }
/* 57 */     catch (NumberFormatException e) {
/*    */       
/* 59 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric instance #");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 67 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceAttach.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */