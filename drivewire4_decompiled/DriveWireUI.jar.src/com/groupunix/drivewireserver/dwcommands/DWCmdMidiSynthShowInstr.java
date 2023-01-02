/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import javax.sound.midi.Instrument;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthShowInstr
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthShowInstr(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "instr";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 27 */     return "Show internal synth instruments";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 33 */     return "dw midi synth show instr";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     String text = new String();
/*    */     
/* 40 */     text = "\r\nInternal synthesizer instrument list:\r\n\n";
/*    */     
/* 42 */     if (this.dwProto.getVPorts().getMidiSynth() != null) {
/*    */       
/* 44 */       Instrument[] instruments = this.dwProto.getVPorts().getMidiSynth().getLoadedInstruments();
/*    */       
/* 46 */       if (instruments.length == 0)
/*    */       {
/* 48 */         text = text + "No instruments found, you may need to load a soundbank.\r\n";
/*    */       }
/*    */       
/* 51 */       for (int i = 0; i < instruments.length; i++) {
/*    */         
/* 53 */         text = text + String.format("%3d:%-15s", new Object[] { Integer.valueOf(i), instruments[i].getName() });
/*    */         
/* 55 */         if (i % 4 == 0)
/*    */         {
/* 57 */           text = text + "\r\n";
/*    */         }
/*    */       } 
/*    */ 
/*    */       
/* 62 */       text = text + "\r\n";
/*    */     }
/*    */     else {
/*    */       
/* 66 */       text = text + "MIDI is disabled.\r\n";
/*    */     } 
/* 68 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 73 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthShowInstr.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */