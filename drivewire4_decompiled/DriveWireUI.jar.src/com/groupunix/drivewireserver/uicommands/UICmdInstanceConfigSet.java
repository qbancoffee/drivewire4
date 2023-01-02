/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ public class UICmdInstanceConfigSet
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "set";
/*    */   private DWUIClientThread uiref;
/*    */   
/*    */   public UICmdInstanceConfigSet(DWUIClientThread dwuiClientThread) {
/* 16 */     this.uiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 21 */     return "set";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 27 */     if (cmdline.length() == 0)
/*    */     {
/* 29 */       return new DWCommandResponse(false, (byte)10, "Must specify item");
/*    */     }
/*    */     
/* 32 */     String[] args = cmdline.split(" ");
/*    */     
/* 34 */     if (args.length == 1)
/*    */     {
/* 36 */       return doSetConfig(args[0]);
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 42 */     return doSetConfig(args[0], cmdline.substring(args[0].length() + 1));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 50 */     return "Set instance configuration item";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 56 */     return "ui instance config set [item] [value]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item) {
/* 68 */     if (DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().containsKey(item)) {
/*    */       
/* 70 */       synchronized (DriveWireServer.serverconfig) {
/*    */         
/* 72 */         DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().clearProperty(item);
/*    */       } 
/* 74 */       return new DWCommandResponse("Item '" + item + "' removed from config.");
/*    */     } 
/*    */ 
/*    */     
/* 78 */     return new DWCommandResponse("Item '" + item + "' is not set.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item, String value) {
/* 87 */     synchronized (DriveWireServer.serverconfig) {
/*    */       
/* 89 */       DriveWireServer.getHandler(this.uiref.getInstance()).getConfig().setProperty(item, value);
/*    */     } 
/* 91 */     return new DWCommandResponse("Item '" + item + "' set to '" + value + "'.");
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceConfigSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */