/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import javax.sound.midi.MidiDevice;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.MidiUnavailableException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowMIDIDevs
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 17 */     return "mididevs";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 23 */     return "show available MIDI devices";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 28 */     return "ui server show mididevs";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 34 */     String res = new String();
/*    */ 
/*    */     
/* 37 */     if (DriveWireServer.getHandler(0).getConfig().getBoolean("UseMIDI", true)) {
/*    */ 
/*    */ 
/*    */       
/* 41 */       MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
/*    */       
/* 43 */       for (int i = 0; i < infos.length; i++)
/*    */       {
/*    */         try
/*    */         {
/* 47 */           MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
/* 48 */           res = res + i + " " + device.getDeviceInfo().getName() + "\n";
/*    */         
/*    */         }
/* 51 */         catch (MidiUnavailableException e)
/*    */         {
/* 53 */           return new DWCommandResponse(false, (byte)-105, "MIDI unavailable during UI device listing");
/*    */         }
/*    */       
/*    */       }
/*    */     
/*    */     } else {
/*    */       
/* 60 */       return new DWCommandResponse(false, (byte)-105, "MIDI is disabled.");
/*    */     } 
/*    */     
/* 63 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 68 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowMIDIDevs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */