/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthInstr
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthInstr(DWProtocolHandler dwProto, DWCommand parent) {
/* 12 */     setParentCmd(parent);
/* 13 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 18 */     return "instr";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 24 */     return "Manually set chan X to instrument Y";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 30 */     return "dw midi synth instr #X #Y";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/*    */     int channel, instr;
/* 36 */     String[] args = cmdline.split(" ");
/*    */     
/* 38 */     if (args.length != 2)
/*    */     {
/* 40 */       return new DWCommandResponse(false, (byte)10, "dw midi synth instr requires a channel # and an instrument # as arguments");
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 48 */       channel = Integer.parseInt(args[0]) - 1;
/* 49 */       instr = Integer.parseInt(args[1]);
/*    */     }
/* 51 */     catch (NumberFormatException e) {
/*    */       
/* 53 */       return new DWCommandResponse(false, (byte)10, "dw midi synth instr requires a channel # and an instrument # as arguments");
/*    */     } 
/*    */     
/* 56 */     if (this.dwProto.getVPorts().setMIDIInstr(channel, instr))
/*    */     {
/* 58 */       return new DWCommandResponse("Set MIDI channel " + (channel + 1) + " to instrument " + instr);
/*    */     }
/*    */ 
/*    */     
/* 62 */     return new DWCommandResponse(false, (byte)-106, "Failed to set instrument");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 69 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthInstr.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */