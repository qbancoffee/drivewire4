/*     */ package com.groupunix.drivewireserver.dwcommands;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.Soundbank;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCmdMidiSynthStatus
/*     */   extends DWCommand
/*     */ {
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*     */   public DWCmdMidiSynthStatus(DWProtocolHandler dwProto, DWCommand parent) {
/*  20 */     setParentCmd(parent);
/*  21 */     this.dwProto = dwProto;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/*  26 */     return "status";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShortHelp() {
/*  32 */     return "Show internal synth status";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUsage() {
/*  38 */     return "dw midi synth status";
/*     */   }
/*     */ 
/*     */   
/*     */   public DWCommandResponse parse(String cmdline) {
/*  43 */     return doSynthStatus();
/*     */   }
/*     */ 
/*     */   
/*     */   private DWCommandResponse doSynthStatus() {
/*  48 */     String text = new String();
/*     */ 
/*     */     
/*  51 */     text = "\r\nInternal synthesizer status:\r\n\n";
/*     */     
/*  53 */     if (this.dwProto.getVPorts().getMidiSynth() != null) {
/*     */       
/*  55 */       MidiDevice.Info midiinfo = this.dwProto.getVPorts().getMidiSynth().getDeviceInfo();
/*     */       
/*  57 */       text = text + "Device:\r\n";
/*  58 */       text = text + midiinfo.getVendor() + ", " + midiinfo.getName() + ", " + midiinfo.getVersion() + "\r\n";
/*  59 */       text = text + midiinfo.getDescription() + "\r\n";
/*     */       
/*  61 */       text = text + "\r\n";
/*     */       
/*  63 */       text = text + "Soundbank: ";
/*     */       
/*  65 */       if (this.dwProto.getVPorts().getMidiSoundbankFilename() == null) {
/*     */         
/*  67 */         Soundbank sbank = this.dwProto.getVPorts().getMidiSynth().getDefaultSoundbank();
/*     */         
/*  69 */         if (sbank != null)
/*     */         {
/*  71 */           text = text + " (default)\r\n";
/*  72 */           text = text + sbank.getVendor() + ", " + sbank.getName() + ", " + sbank.getVersion() + "\r\n";
/*  73 */           text = text + sbank.getDescription() + "\r\n";
/*     */         }
/*     */         else
/*     */         {
/*  77 */           text = text + " none\r\n";
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/*  82 */         File file = new File(this.dwProto.getVPorts().getMidiSoundbankFilename());
/*     */         
/*     */         try {
/*  85 */           Soundbank sbank = MidiSystem.getSoundbank(file);
/*     */           
/*  87 */           text = text + " (" + this.dwProto.getVPorts().getMidiSoundbankFilename() + ")\r\n";
/*  88 */           text = text + sbank.getVendor() + ", " + sbank.getName() + ", " + sbank.getVersion() + "\r\n";
/*  89 */           text = text + sbank.getDescription() + "\r\n";
/*     */         
/*     */         }
/*  92 */         catch (InvalidMidiDataException e) {
/*     */           
/*  94 */           return new DWCommandResponse(false, (byte)-103, e.getMessage());
/*     */         }
/*  96 */         catch (IOException e) {
/*     */           
/*  98 */           return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*     */         } 
/*     */       } 
/*     */       
/* 102 */       text = text + "\r\n";
/*     */       
/* 104 */       text = text + "Latency:   " + this.dwProto.getVPorts().getMidiSynth().getLatency() + "\r\n";
/* 105 */       text = text + "Polyphony: " + this.dwProto.getVPorts().getMidiSynth().getMaxPolyphony() + "\r\n";
/* 106 */       text = text + "Position:  " + this.dwProto.getVPorts().getMidiSynth().getMicrosecondPosition() + "\r\n\n";
/* 107 */       text = text + "Profile:   " + this.dwProto.getVPorts().getMidiProfileName() + "\r\n";
/* 108 */       text = text + "Instrlock: " + this.dwProto.getVPorts().getMidiVoicelock() + "\r\n";
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 113 */       text = text + "MIDI is disabled.\r\n";
/*     */     } 
/*     */ 
/*     */     
/* 117 */     return new DWCommandResponse(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean validate(String cmdline) {
/* 122 */     return true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */