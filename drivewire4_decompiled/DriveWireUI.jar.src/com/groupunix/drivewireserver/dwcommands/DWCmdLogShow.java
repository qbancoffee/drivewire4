/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdLogShow
/*    */   extends DWCommand
/*    */ {
/*    */   public DWCmdLogShow(DWCommand parent) {
/* 13 */     setParentCmd(parent);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 18 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 24 */     return "Show last 20 (or #) log entries";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 30 */     return "dw log show [#]";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 35 */     if (cmdline.length() == 0)
/*    */     {
/* 37 */       return doShowLog("20");
/*    */     }
/* 39 */     return doShowLog(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doShowLog(String strlines) {
/* 45 */     String text = new String();
/*    */ 
/*    */     
/*    */     try {
/* 49 */       int lines = Integer.parseInt(strlines);
/*    */       
/* 51 */       text = text + "\r\nDriveWire Server Log (" + DriveWireServer.getLogEventsSize() + " events in buffer):\r\n\n";
/*    */       
/* 53 */       ArrayList<String> loglines = DriveWireServer.getLogEvents(lines);
/*    */       
/* 55 */       for (int i = 0; i < loglines.size(); i++)
/*    */       {
/* 57 */         text = text + (String)loglines.get(i);
/*    */       }
/*    */ 
/*    */       
/* 61 */       return new DWCommandResponse(text);
/*    */     
/*    */     }
/* 64 */     catch (NumberFormatException e) {
/*    */       
/* 66 */       return new DWCommandResponse(false, (byte)10, "Syntax error: non numeric # of log lines");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 74 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdLogShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */