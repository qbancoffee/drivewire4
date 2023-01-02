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
/*    */ public class UICmdServerFileInfo
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "info";
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerFileInfo(DWUIClientThread dwuiClientThread) {
/* 19 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "info";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 30 */     return new DWCommandResponse(DWUtils.getFileDescriptor(new File(cmdline)) + "|false");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 37 */     return "Show file details";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 43 */     return "ui server file info";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 48 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerFileInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */