/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandList;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ 
/*    */ public class UICmdServerShow
/*    */   extends DWCommand
/*    */ {
/*    */   static final String command = "show";
/* 12 */   private DWCommandList commands = new DWCommandList(null);
/*    */ 
/*    */   
/*    */   public UICmdServerShow(DWUIClientThread dwuiClientThread) {
/* 16 */     this.commands.addcommand(new UICmdServerShowVersion());
/* 17 */     this.commands.addcommand(new UICmdServerShowInstances());
/* 18 */     this.commands.addcommand(new UICmdServerShowMIDIDevs());
/* 19 */     this.commands.addcommand(new UICmdServerShowSynthProfiles());
/* 20 */     this.commands.addcommand(new UICmdServerShowLocalDisks());
/* 21 */     this.commands.addcommand(new UICmdServerShowSerialDevs());
/* 22 */     this.commands.addcommand(new UICmdServerShowStatus());
/* 23 */     this.commands.addcommand(new UICmdServerShowNet());
/* 24 */     this.commands.addcommand(new UICmdServerShowLog(dwuiClientThread));
/* 25 */     this.commands.addcommand(new UICmdServerShowTopics(dwuiClientThread));
/* 26 */     this.commands.addcommand(new UICmdServerShowHelp(dwuiClientThread));
/* 27 */     this.commands.addcommand(new UICmdServerShowErrors(dwuiClientThread));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 33 */     return "show";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 45 */     return "Informational commands";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 51 */     return "ui server show [item]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 56 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */