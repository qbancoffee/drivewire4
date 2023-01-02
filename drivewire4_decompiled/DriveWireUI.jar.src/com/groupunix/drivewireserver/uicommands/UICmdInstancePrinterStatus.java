/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdInstancePrinterStatus
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuithread;
/*    */   
/*    */   public UICmdInstancePrinterStatus(DWUIClientThread dwuiClientThread) {
/* 20 */     this.dwuithread = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 27 */     return "printerstatus";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 33 */     return "show printer status";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 38 */     return "ui instance printerstatus";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 44 */     String res = "";
/*    */     
/* 46 */     if (this.dwuithread.getInstance() > -1) {
/*    */       
/* 48 */       DWProtocolHandler dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.dwuithread.getInstance());
/*    */ 
/*    */ 
/*    */       
/* 52 */       if (dwProto != null) {
/*    */         
/* 54 */         res = "currentprinter|" + DriveWireServer.getHandler(this.dwuithread.getInstance()).getConfig().getString("CurrentPrinter", "none") + "\r\n";
/*    */ 
/*    */         
/* 57 */         List<HierarchicalConfiguration> profiles = DriveWireServer.getHandler(this.dwuithread.getInstance()).getConfig().configurationsAt("Printer");
/*    */         
/* 59 */         for (Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext(); ) {
/*    */           
/* 61 */           HierarchicalConfiguration mprof = it.next();
/*    */           
/* 63 */           res = res + "printer|" + mprof.getString("[@name]") + "|" + mprof.getString("[@desc]") + "\r\n";
/*    */         } 
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 69 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 74 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstancePrinterStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */