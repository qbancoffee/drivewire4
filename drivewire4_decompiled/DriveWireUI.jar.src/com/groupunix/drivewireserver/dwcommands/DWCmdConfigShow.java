/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import java.util.Iterator;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdConfigShow
/*    */   extends DWCommand
/*    */ {
/*    */   DWProtocol dwProto;
/*    */   
/*    */   public DWCmdConfigShow(DWProtocol dwProtocol, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProtocol;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "Show current instance config (or item)";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "dw config show [item]";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 40 */     if (cmdline.length() > 0)
/*    */     {
/* 42 */       return doShowConfig(cmdline);
/*    */     }
/* 44 */     return doShowConfig();
/*    */   }
/*    */ 
/*    */   
/*    */   private DWCommandResponse doShowConfig(String item) {
/* 49 */     String text = new String();
/*    */     
/* 51 */     if (this.dwProto.getConfig().containsKey(item)) {
/*    */       
/* 53 */       String key = item;
/* 54 */       String value = StringUtils.join((Object[])this.dwProto.getConfig().getStringArray(key), ", ");
/*    */       
/* 56 */       text = text + key + " = " + value;
/* 57 */       return new DWCommandResponse(text);
/*    */     } 
/*    */ 
/*    */     
/* 61 */     return new DWCommandResponse(false, (byte)-114, "Key '" + item + "' is not set.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 71 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doShowConfig() {
/* 78 */     String text = new String();
/*    */     
/* 80 */     text = text + "Current protocol handler configuration:\r\n\n";
/*    */     
/* 82 */     for (Iterator<String> i = this.dwProto.getConfig().getKeys(); i.hasNext(); ) {
/*    */       
/* 84 */       String key = i.next();
/* 85 */       String value = StringUtils.join((Object[])this.dwProto.getConfig().getStringArray(key), ", ");
/*    */       
/* 87 */       text = text + key + " = " + value + "\r\n";
/*    */     } 
/*    */ 
/*    */     
/* 91 */     return new DWCommandResponse(text);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdConfigShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */