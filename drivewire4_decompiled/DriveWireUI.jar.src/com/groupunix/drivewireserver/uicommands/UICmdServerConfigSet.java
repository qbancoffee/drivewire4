/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerConfigSet
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "set";
/*    */   
/*    */   public String getCommand() {
/* 15 */     return "set";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 21 */     if (cmdline.length() == 0)
/*    */     {
/* 23 */       return new DWCommandResponse(false, (byte)10, "Must specify item");
/*    */     }
/*    */     
/* 26 */     String[] args = cmdline.split(" ");
/*    */     
/* 28 */     if (args.length == 1)
/*    */     {
/* 30 */       return doSetConfig(args[0]);
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 36 */     return doSetConfig(args[0], cmdline.substring(args[0].length() + 1));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 43 */     return "Set server configuration item";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 49 */     return "ui server config set [item] [value]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 54 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item) {
/* 61 */     if (DriveWireServer.serverconfig.containsKey(item))
/*    */     {
/* 63 */       synchronized (DriveWireServer.serverconfig) {
/*    */         
/* 65 */         DriveWireServer.serverconfig.setProperty(item, null);
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 70 */     return new DWCommandResponse(item + " unset.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item, String value) {
/* 77 */     synchronized (DriveWireServer.serverconfig) {
/*    */       
/* 79 */       if (DriveWireServer.serverconfig.containsKey(item)) {
/*    */         
/* 81 */         if (!DriveWireServer.serverconfig.getProperty(item).equals(value)) {
/* 82 */           DriveWireServer.serverconfig.setProperty(item, value);
/*    */         }
/*    */       } else {
/*    */         
/* 86 */         DriveWireServer.serverconfig.setProperty(item, value);
/*    */       } 
/*    */     } 
/* 89 */     return new DWCommandResponse(item + " set.");
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfigSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */