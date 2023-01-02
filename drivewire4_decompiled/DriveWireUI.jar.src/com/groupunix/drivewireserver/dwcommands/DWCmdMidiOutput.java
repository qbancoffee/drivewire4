/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import javax.sound.midi.MidiDevice;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.MidiUnavailableException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiOutput
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiOutput(DWProtocolHandler dwProto, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "output";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 28 */     return "Set midi output to device #";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 34 */     return "dw midi output #";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 39 */     if (cmdline.length() == 0)
/*    */     {
/* 41 */       return new DWCommandResponse(false, (byte)10, "Syntax error: dw midi output requires a device # as an argument");
/*    */     }
/*    */     
/* 44 */     return doMidiOutput(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doMidiOutput(String devstr) {
/* 50 */     if (this.dwProto.getConfig().getBoolean("UseMIDI", true)) {
/*    */       
/*    */       try {
/*    */ 
/*    */         
/* 55 */         int devno = Integer.parseInt(devstr);
/*    */         
/* 57 */         MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
/*    */         
/* 59 */         if (devno < 0 || devno > infos.length)
/*    */         {
/* 61 */           return new DWCommandResponse(false, (byte)-104, "Invalid device number for dw midi output.");
/*    */         }
/*    */         
/* 64 */         this.dwProto.getVPorts().setMIDIDevice(MidiSystem.getMidiDevice(infos[devno]));
/*    */         
/* 66 */         return new DWCommandResponse("Set MIDI output device: " + MidiSystem.getMidiDevice(infos[devno]).getDeviceInfo().getName());
/*    */       
/*    */       }
/* 69 */       catch (NumberFormatException e) {
/*    */         
/* 71 */         return new DWCommandResponse(false, (byte)10, "dw midi device requires a numeric device # as an argument");
/*    */       
/*    */       }
/* 74 */       catch (MidiUnavailableException e) {
/*    */         
/* 76 */         return new DWCommandResponse(false, (byte)-105, e.getMessage());
/*    */       }
/* 78 */       catch (IllegalArgumentException e) {
/*    */         
/* 80 */         return new DWCommandResponse(false, (byte)-104, e.getMessage());
/*    */       } 
/*    */     }
/*    */ 
/*    */     
/* 85 */     return new DWCommandResponse(false, (byte)-105, "MIDI is disabled.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 92 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiOutput.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */