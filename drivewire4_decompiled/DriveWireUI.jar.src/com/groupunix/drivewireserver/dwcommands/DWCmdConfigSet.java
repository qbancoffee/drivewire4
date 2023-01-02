/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ 
/*    */ 
/*    */ public class DWCmdConfigSet
/*    */   extends DWCommand
/*    */ {
/*    */   DWProtocol dwProto;
/*    */   
/*    */   public DWCmdConfigSet(DWProtocol dwProtocol, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProtocol;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 19 */     return "set";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 25 */     return "Set config item, omit value to remove item";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 31 */     return "dw config set item [value]";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     if (cmdline.length() == 0)
/*    */     {
/* 38 */       return new DWCommandResponse(false, (byte)10, "Syntax error: dw config set requires an item and value as arguments");
/*    */     }
/*    */     
/* 41 */     String[] args = cmdline.split(" ");
/*    */     
/* 43 */     if (args.length == 1)
/*    */     {
/* 45 */       return doSetConfig(args[0]);
/*    */     }
/*    */ 
/*    */     
/* 49 */     return doSetConfig(args[0], cmdline.substring(args[0].length() + 1));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item) {
/* 57 */     if (this.dwProto.getConfig().containsKey(item))
/*    */     {
/* 59 */       synchronized (DriveWireServer.serverconfig) {
/*    */         
/* 61 */         this.dwProto.getConfig().clearProperty(item);
/*    */       } 
/*    */     }
/*    */     
/* 65 */     return new DWCommandResponse("Item '" + item + "' removed from config");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doSetConfig(String item, String value) {
/* 72 */     synchronized (DriveWireServer.serverconfig) {
/*    */       
/* 74 */       this.dwProto.getConfig().setProperty(item, value);
/*    */     } 
/* 76 */     return new DWCommandResponse("Item '" + item + "' set to '" + value + "'");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 83 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdConfigSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */