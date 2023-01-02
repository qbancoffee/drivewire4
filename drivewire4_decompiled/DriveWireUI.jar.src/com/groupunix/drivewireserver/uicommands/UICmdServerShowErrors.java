/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowErrors
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerShowErrors(DWUIClientThread dwuiClientThread) {
/* 18 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "errors";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 33 */     return "show error descriptions";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 39 */     return "ui server show errors";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 45 */     String res = "";
/*    */     
/* 47 */     List<String> rconfs = DriveWireServer.getHandler(this.dwuiref.getInstance()).getHelp().getSectionTopics("resultcode");
/*    */     
/* 49 */     if (rconfs != null) {
/*    */       
/* 51 */       for (String rc : rconfs) {
/*    */ 
/*    */         
/*    */         try {
/* 55 */           res = res + rc.substring(1) + "|" + DriveWireServer.getHandler(this.dwuiref.getInstance()).getHelp().getTopicText("resultcode." + rc).trim() + "\r\n";
/*    */         }
/* 57 */         catch (DWHelpTopicNotFoundException e) {}
/*    */       } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 64 */       return new DWCommandResponse(res);
/*    */     } 
/*    */     
/* 67 */     return new DWCommandResponse(false, (byte)-26, "No error descriptions available from server");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 73 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowErrors.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */