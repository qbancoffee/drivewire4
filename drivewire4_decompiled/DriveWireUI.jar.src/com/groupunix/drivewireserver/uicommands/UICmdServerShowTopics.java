/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowTopics
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerShowTopics(DWUIClientThread dwuiClientThread) {
/* 17 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "topics";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 32 */     return "show available help topics";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 38 */     return "ui server show topics";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 44 */     String txt = new String();
/*    */     
/* 46 */     ArrayList<String> tops = DriveWireServer.getHandler(this.dwuiref.getInstance()).getHelp().getTopics(null);
/*    */     
/* 48 */     Iterator<String> t = tops.iterator();
/* 49 */     while (t.hasNext())
/*    */     {
/* 51 */       txt = txt + (String)t.next() + "\n";
/*    */     }
/*    */     
/* 54 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 59 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowTopics.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */