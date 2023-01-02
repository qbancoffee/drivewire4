/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdServerHelpShow
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocol dwProto;
/*    */   
/*    */   public DWCmdServerHelpShow(DWProtocol dwProtocol, DWCommand parent) {
/* 19 */     setParentCmd(parent);
/* 20 */     this.dwProto = dwProtocol;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 26 */     return "show";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 32 */     return "Show help topic";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 38 */     return "dw help show [topic]";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 43 */     if (cmdline.length() == 0)
/*    */     {
/* 45 */       return doShowHelp();
/*    */     }
/* 47 */     return doShowHelp(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doShowHelp(String cmdline) {
/* 55 */     String text = "Help for " + cmdline + ":\r\n\r\n";
/*    */ 
/*    */     
/*    */     try {
/* 59 */       text = text + ((DWProtocolHandler)this.dwProto).getHelp().getTopicText(cmdline);
/* 60 */       return new DWCommandResponse(text);
/*    */     }
/* 62 */     catch (DWHelpTopicNotFoundException e) {
/*    */       
/* 64 */       return new DWCommandResponse(false, (byte)-114, e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doShowHelp() {
/* 72 */     String text = new String();
/*    */     
/* 74 */     text = "Help Topics:\r\n\r\n";
/*    */     
/* 76 */     ArrayList<String> tops = ((DWProtocolHandler)this.dwProto).getHelp().getTopics(null);
/*    */     
/* 78 */     Iterator<String> t = tops.iterator();
/* 79 */     while (t.hasNext())
/*    */     {
/* 81 */       text = text + (String)t.next() + "\r\n";
/*    */     }
/* 83 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 89 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerHelpShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */