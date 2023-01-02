/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.io.StringWriter;
/*    */ import org.apache.commons.configuration.ConfigurationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerConfigWrite
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "write";
/*    */   
/*    */   public String getCommand() {
/* 18 */     return "write";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 24 */     String res = new String();
/*    */     
/* 26 */     StringWriter sw = new StringWriter();
/*    */ 
/*    */     
/*    */     try {
/* 30 */       DriveWireServer.serverconfig.save(sw);
/*    */     }
/* 32 */     catch (ConfigurationException e) {
/*    */ 
/*    */       
/* 35 */       e.printStackTrace();
/*    */     } 
/*    */     
/* 38 */     res = sw.getBuffer().toString();
/*    */     
/* 40 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 47 */     return "Write config xml";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 53 */     return "ui server config write";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfigWrite.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */