/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ 
/*    */ public class DWCmdServerShowHandlers
/*    */   extends DWCommand
/*    */ {
/*    */   DWCmdServerShowHandlers(DWCommand parent) {
/*  9 */     setParentCmd(parent);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 14 */     return "handlers";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 21 */     return "Show handler instances";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 27 */     return "dw server show handlers";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 32 */     String text = new String();
/*    */     
/* 34 */     text = text + "DriveWire protocol handler instances:\r\n";
/*    */     
/* 36 */     for (int i = 0; i < DriveWireServer.getNumHandlers(); i++) {
/*    */       
/* 38 */       if (DriveWireServer.getHandler(i) != null)
/*    */       {
/* 40 */         text = text + "\r\nHandler #" + i + ": Device " + DriveWireServer.getHandler(i).getConfig().getString("SerialDevice") + " CocoModel " + DriveWireServer.getHandler(i).getConfig().getString("CocoModel") + "\r\n";
/*    */       }
/*    */     } 
/*    */     
/* 44 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 49 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdServerShowHandlers.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */