/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowLog
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerShowLog(DWUIClientThread dwuiClientThread) {
/* 17 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "log";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 32 */     return "show log buffer";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 38 */     return "ui server show log";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 44 */     String txt = new String();
/*    */     
/* 46 */     ArrayList<String> log = DriveWireServer.getLogEvents(DriveWireServer.getLogEventsSize());
/*    */     
/* 48 */     for (String l : log) {
/* 49 */       txt = txt + l;
/*    */     }
/* 51 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 56 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowLog.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */