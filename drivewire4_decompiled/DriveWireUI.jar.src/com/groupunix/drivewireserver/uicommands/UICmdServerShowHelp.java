/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowHelp
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerShowHelp(DWUIClientThread dwuiClientThread) {
/* 16 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "help";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 31 */     return "show help";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 37 */     return "ui server show help topic";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/*    */     try {
/* 46 */       return new DWCommandResponse(DriveWireServer.getHandler(this.dwuiref.getInstance()).getHelp().getTopicText(cmdline));
/*    */     }
/* 48 */     catch (DWHelpTopicNotFoundException e) {
/*    */       
/* 50 */       return new DWCommandResponse(false, (byte)-26, e.getLocalizedMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowHelp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */