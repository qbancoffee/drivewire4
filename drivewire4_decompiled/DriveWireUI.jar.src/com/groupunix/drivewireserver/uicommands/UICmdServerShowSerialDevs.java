/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowSerialDevs
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 15 */     return "serialdevs";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 23 */     return "show available serial devices";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 33 */     return "ui server show serialdevs";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     String txt = new String();
/*    */     
/* 41 */     ArrayList<String> ports = DriveWireServer.getAvailableSerialPorts();
/*    */     
/* 43 */     for (int i = 0; i < ports.size(); i++) {
/* 44 */       txt = txt + (String)ports.get(i) + "\n";
/*    */     }
/* 46 */     return new DWCommandResponse(txt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 51 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowSerialDevs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */