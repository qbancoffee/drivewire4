/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.sound.midi.InvalidMidiDataException;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.Soundbank;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthBank
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthBank(DWProtocolHandler dwProto, DWCommand parent) {
/* 19 */     setParentCmd(parent);
/* 20 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 25 */     return "bank";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 31 */     return "Load soundbank file";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 37 */     return "dw midi synth bank filepath";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 42 */     if (cmdline.length() == 0)
/*    */     {
/* 44 */       return new DWCommandResponse(false, (byte)10, "dw midi synth bank requires a file path as an argument");
/*    */     }
/*    */     
/* 47 */     return doMidiSynthBank(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doMidiSynthBank(String path) {
/* 53 */     Soundbank soundbank = null;
/*    */     
/* 55 */     if (this.dwProto.getConfig().getBoolean("UseMIDI", true)) {
/*    */       
/* 57 */       File file = new File(path);
/*    */       
/*    */       try {
/* 60 */         soundbank = MidiSystem.getSoundbank(file);
/*    */       }
/* 62 */       catch (InvalidMidiDataException e) {
/*    */         
/* 64 */         return new DWCommandResponse(false, (byte)-103, e.getMessage());
/*    */       }
/* 66 */       catch (IOException e) {
/*    */         
/* 68 */         return new DWCommandResponse(false, (byte)-54, e.getMessage());
/*    */       } 
/*    */       
/* 71 */       if (this.dwProto.getVPorts().isSoundbankSupported(soundbank)) {
/*    */         
/* 73 */         if (this.dwProto.getVPorts().setMidiSoundbank(soundbank, path))
/*    */         {
/* 75 */           return new DWCommandResponse("Soundbank loaded without error");
/*    */         }
/*    */ 
/*    */         
/* 79 */         return new DWCommandResponse(false, (byte)-102, "Failed to load soundbank");
/*    */       } 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 85 */       return new DWCommandResponse(false, (byte)-101, "Soundbank not supported");
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 90 */     return new DWCommandResponse(false, (byte)-105, "MIDI is disabled.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthBank.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */