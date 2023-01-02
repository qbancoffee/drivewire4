/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.util.Iterator;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerConfigShow
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "show";
/*    */   
/*    */   public String getCommand() {
/* 19 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 25 */     String res = new String();
/*    */ 
/*    */     
/* 28 */     if (cmdline.length() == 0) {
/*    */       
/* 30 */       for (Iterator<String> i = DriveWireServer.serverconfig.getKeys(); i.hasNext(); )
/*    */       {
/* 32 */         String key = i.next();
/* 33 */         String value = StringUtils.join((Object[])DriveWireServer.serverconfig.getStringArray(key), ", ");
/*    */         
/* 35 */         res = res + key + " = " + value + "\r\n";
/*    */       }
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 41 */       if (DriveWireServer.serverconfig.containsKey(cmdline)) {
/*    */         
/* 43 */         String value = StringUtils.join((Object[])DriveWireServer.serverconfig.getStringArray(cmdline), ", ");
/* 44 */         return new DWCommandResponse(value);
/*    */       } 
/*    */ 
/*    */       
/* 48 */       return new DWCommandResponse(false, (byte)-114, "Key '" + cmdline + "' is not set.");
/*    */     } 
/*    */ 
/*    */     
/* 52 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 58 */     return "Show server configuration";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 64 */     return "ui server config show [item]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 69 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerConfigShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */