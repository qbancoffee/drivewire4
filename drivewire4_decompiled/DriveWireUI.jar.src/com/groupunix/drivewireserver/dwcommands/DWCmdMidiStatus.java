/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import javax.sound.midi.MidiDevice;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.MidiUnavailableException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiStatus
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiStatus(DWProtocolHandler dwProto, DWCommand parent) {
/* 16 */     setParentCmd(parent);
/* 17 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 22 */     return "status";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 29 */     return "Show MIDI status";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 35 */     return "dw midi status";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 40 */     return doMidiStatus();
/*    */   }
/*    */ 
/*    */   
/*    */   private DWCommandResponse doMidiStatus() {
/* 45 */     String text = new String();
/*    */     
/* 47 */     text = text + "\r\nDriveWire MIDI status:\r\n\n";
/*    */     
/* 49 */     if (this.dwProto.getConfig().getBoolean("UseMIDI", true)) {
/*    */       
/* 51 */       text = text + "Devices:\r\n";
/*    */ 
/*    */       
/* 54 */       MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
/*    */       
/* 56 */       for (int i = 0; i < infos.length; i++) {
/*    */ 
/*    */         
/*    */         try {
/* 60 */           MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
/* 61 */           text = text + "[" + i + "] ";
/* 62 */           text = text + device.getDeviceInfo().getName() + " (" + device.getClass().getSimpleName() + ")\r\n";
/* 63 */           text = text + "    " + device.getDeviceInfo().getDescription() + ", ";
/* 64 */           text = text + device.getDeviceInfo().getVendor() + " ";
/* 65 */           text = text + device.getDeviceInfo().getVersion() + "\r\n";
/*    */         
/*    */         }
/* 68 */         catch (MidiUnavailableException e) {
/*    */           
/* 70 */           return new DWCommandResponse(false, (byte)-105, e.getMessage());
/*    */         } 
/*    */       } 
/*    */ 
/*    */       
/* 75 */       text = text + "\r\nCurrent MIDI output device: ";
/*    */       
/* 77 */       if (this.dwProto.getVPorts().getMidiDeviceInfo() == null)
/*    */       {
/*    */         
/* 80 */         text = text + "none\r\n";
/*    */       }
/*    */       else
/*    */       {
/* 84 */         text = text + this.dwProto.getVPorts().getMidiDeviceInfo().getName() + "\r\n";
/*    */       }
/*    */     
/*    */     } else {
/*    */       
/* 89 */       text = text + "MIDI is disabled.\r\n";
/*    */     } 
/*    */     
/* 92 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */