/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdMidi
/*    */   extends DWCommand {
/*    */   static final String command = "midi";
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidi(DWProtocolHandler dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/* 15 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/* 16 */     this.commands.addcommand(new DWCmdMidiStatus(dwProto, this));
/* 17 */     this.commands.addcommand(new DWCmdMidiOutput(dwProto, this));
/* 18 */     this.commands.addcommand(new DWCmdMidiSynth(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "midi";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 30 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     if (cmdline.length() == 0)
/*    */     {
/* 38 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 40 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 48 */     return "Manage the MIDI subsystem";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 54 */     return "dw midi [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 59 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */