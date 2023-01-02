/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import org.apache.commons.configuration.ConfigurationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdConfigSave
/*    */   extends DWCommand
/*    */ {
/*    */   DWProtocol dwProto;
/*    */   
/*    */   public DWCmdConfigSave(DWProtocol dwProtocol, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProtocol;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "save";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "Save configuration)";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "dw config save";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/*    */     try {
/* 43 */       DriveWireServer.saveServerConfig();
/*    */     }
/* 45 */     catch (ConfigurationException e) {
/*    */       
/* 47 */       return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */     } 
/*    */     
/* 50 */     return new DWCommandResponse("Configuration saved.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdConfigSave.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */