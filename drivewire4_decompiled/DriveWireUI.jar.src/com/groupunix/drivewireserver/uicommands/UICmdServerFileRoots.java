/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerFileRoots
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "roots";
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerFileRoots(DWUIClientThread dwuiClientThread) {
/* 19 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "roots";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     File[] roots = File.listRoots();
/*    */     
/* 33 */     String text = "";
/*    */     
/* 35 */     for (File f : roots)
/*    */     {
/* 37 */       text = text + DWUtils.getFileDescriptor(f) + "|true\n";
/*    */     }
/*    */     
/* 40 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 47 */     return "List filesystem roots";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 53 */     return "ui server file roots";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerFileRoots.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */