/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdMidiSynth
/*    */   extends DWCommand {
/*    */   static final String command = "synth";
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynth(DWProtocolHandler dwProto, DWCommand parent) {
/* 13 */     setParentCmd(parent);
/* 14 */     this.dwProto = dwProto;
/* 15 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/* 16 */     this.commands.addcommand(new DWCmdMidiSynthStatus(dwProto, this));
/* 17 */     this.commands.addcommand(new DWCmdMidiSynthShow(dwProto, this));
/* 18 */     this.commands.addcommand(new DWCmdMidiSynthBank(dwProto, this));
/* 19 */     this.commands.addcommand(new DWCmdMidiSynthProfile(dwProto, this));
/* 20 */     this.commands.addcommand(new DWCmdMidiSynthLock(dwProto, this));
/* 21 */     this.commands.addcommand(new DWCmdMidiSynthInstr(dwProto, this));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 27 */     return "synth";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 32 */     return this.commands;
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 37 */     if (cmdline.length() == 0)
/*    */     {
/* 39 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 41 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 49 */     return "Manage the MIDI synth";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 55 */     return "dw midi synth [command]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 60 */     return this.commands.validate(cmdline);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynth.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */