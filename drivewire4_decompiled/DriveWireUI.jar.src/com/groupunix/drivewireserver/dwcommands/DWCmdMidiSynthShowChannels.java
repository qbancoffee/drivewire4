/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import javax.sound.midi.Instrument;
/*    */ import javax.sound.midi.MidiChannel;
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthShowChannels
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthShowChannels(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "channels";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 26 */     return "Show internal synth channel status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 32 */     return "dw midi synth show channels";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 37 */     String text = new String();
/*    */     
/* 39 */     text = "\r\nInternal synthesizer channel status:\r\n\n";
/*    */     
/* 41 */     if (this.dwProto.getVPorts().getMidiSynth() != null) {
/*    */       
/* 43 */       MidiChannel[] midchans = this.dwProto.getVPorts().getMidiSynth().getChannels();
/*    */       
/* 45 */       Instrument[] instruments = this.dwProto.getVPorts().getMidiSynth().getLoadedInstruments();
/*    */       
/* 47 */       text = text + "Chan#  Instr#  Orig#   Instrument\r\n";
/* 48 */       text = text + "-----------------------------------------------------------------------------\r\n";
/*    */       
/* 50 */       for (int i = 0; i < midchans.length; i++) {
/*    */         
/* 52 */         if (midchans[i] != null)
/*    */         {
/* 54 */           text = text + String.format(" %2d      %-3d    %-3d    ", new Object[] { Integer.valueOf(i + 1), Integer.valueOf(midchans[i].getProgram()), Integer.valueOf(this.dwProto.getVPorts().getGMInstrumentCache(i)) });
/*    */           
/* 56 */           if (midchans[i].getProgram() < instruments.length) {
/*    */             
/* 58 */             text = text + instruments[midchans[i].getProgram()].getName();
/*    */           }
/*    */           else {
/*    */             
/* 62 */             text = text + "(unknown instrument or no soundbank loaded)";
/*    */           } 
/* 64 */           text = text + "\r\n";
/*    */         }
/*    */       
/*    */       } 
/*    */     } else {
/*    */       
/* 70 */       text = text + "MIDI is disabled.\r\n";
/*    */     } 
/* 72 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 77 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthShowChannels.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */