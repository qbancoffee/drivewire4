/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
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
/*    */ 
/*    */ public class UICmdInstanceConfigShow
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "show";
/*    */   private DWUIClientThread uiref;
/*    */   
/*    */   public UICmdInstanceConfigShow(DWUIClientThread dwuiClientThread) {
/* 22 */     this.uiref = dwuiClientThread;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 27 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 33 */     String res = new String();
/*    */     
/* 35 */     int instance = this.uiref.getInstance();
/*    */     
/* 37 */     if (cmdline.length() == 0) {
/*    */       
/* 39 */       for (Iterator<String> i = DriveWireServer.getHandler(instance).getConfig().getKeys(); i.hasNext(); )
/*    */       {
/* 41 */         String key = i.next();
/* 42 */         String value = StringUtils.join((Object[])DriveWireServer.getHandler(instance).getConfig().getStringArray(key), ", ");
/*    */         
/* 44 */         res = res + key + " = " + value + "\r\n";
/*    */       }
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 50 */       if (DriveWireServer.getHandler(instance).getConfig().containsKey(cmdline)) {
/*    */         
/* 52 */         String value = StringUtils.join((Object[])DriveWireServer.getHandler(instance).getConfig().getStringArray(cmdline), ", ");
/* 53 */         return new DWCommandResponse(value);
/*    */       } 
/*    */ 
/*    */       
/* 57 */       return new DWCommandResponse(false, (byte)-114, "Key '" + cmdline + "' is not set.");
/*    */     } 
/*    */ 
/*    */     
/* 61 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 68 */     return "Show instance configuration";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 74 */     return "ui instance config show [item]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 79 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceConfigShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */