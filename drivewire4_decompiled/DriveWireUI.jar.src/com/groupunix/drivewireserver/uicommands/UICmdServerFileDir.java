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
/*    */ public class UICmdServerFileDir
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "dir";
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerFileDir(DWUIClientThread dwuiClientThread) {
/* 19 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "dir";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 30 */     File dir = new File(cmdline);
/*    */     
/* 32 */     String text = "";
/*    */     
/* 34 */     File[] contents = dir.listFiles();
/*    */     
/* 36 */     if (contents != null) {
/*    */       
/* 38 */       for (File f : contents) {
/*    */         
/* 40 */         if (f.isDirectory()) {
/* 41 */           text = text + DWUtils.getFileDescriptor(f) + "|false\n";
/*    */         }
/*    */       } 
/* 44 */       for (File f : contents) {
/*    */         
/* 46 */         if (!f.isDirectory()) {
/* 47 */           text = text + DWUtils.getFileDescriptor(f) + "|false\n";
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 52 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 59 */     return "List directory contents";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 65 */     return "ui server file dir [path]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 70 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerFileDir.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */