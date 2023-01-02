/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerFileDefaultDir
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "defaultdir";
/*    */   private DWUIClientThread dwuiref;
/*    */   
/*    */   public UICmdServerFileDefaultDir(DWUIClientThread dwuiClientThread) {
/* 20 */     this.dwuiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "defaultdir";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 31 */     return new DWCommandResponse(DWUtils.getFileDescriptor(new File(DriveWireServer.serverconfig.getString("LocalDiskDir", "."))) + "|false");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 38 */     return "Show default dir dir";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 44 */     return "ui server file defaultdir";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerFileDefaultDir.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */