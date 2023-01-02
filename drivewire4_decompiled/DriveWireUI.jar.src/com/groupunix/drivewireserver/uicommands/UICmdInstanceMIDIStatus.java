/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DWUIClientThread;
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import javax.sound.midi.MidiDevice;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.MidiUnavailableException;
/*    */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdInstanceMIDIStatus
/*    */   extends DWCommand
/*    */ {
/*    */   private DWUIClientThread dwuithread;
/*    */   
/*    */   public UICmdInstanceMIDIStatus(DWUIClientThread dwuiClientThread) {
/* 24 */     this.dwuithread = dwuiClientThread;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 31 */     return "midistatus";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 37 */     return "show MIDI status";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 42 */     return "ui instance midistatus";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 48 */     String res = "enabled|false";
/*    */     
/* 50 */     if (this.dwuithread.getInstance() > -1) {
/*    */       
/* 52 */       DWProtocolHandler dwProto = (DWProtocolHandler)DriveWireServer.getHandler(this.dwuithread.getInstance());
/*    */ 
/*    */ 
/*    */       
/* 56 */       if (dwProto != null && dwProto.getVPorts() != null && dwProto.getVPorts().getMidiDeviceInfo() != null) {
/*    */         
/*    */         try {
/*    */           
/* 60 */           res = "enabled|" + DriveWireServer.getHandler(this.dwuithread.getInstance()).getConfig().getBoolean("UseMIDI", false) + "\r\n";
/* 61 */           res = res + "cdevice|" + dwProto.getVPorts().getMidiDeviceInfo().getName() + "\r\n";
/* 62 */           res = res + "cprofile|" + dwProto.getVPorts().getMidiProfileName() + "\r\n";
/*    */           
/* 64 */           MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
/*    */           
/* 66 */           for (int j = 0; j < infos.length; j++) {
/*    */             
/* 68 */             MidiDevice.Info i = infos[j];
/* 69 */             MidiDevice dev = MidiSystem.getMidiDevice(i);
/*    */             
/* 71 */             res = res + "device|" + j + "|" + dev.getClass().getSimpleName() + "|" + i.getName() + "|" + i.getDescription() + "|" + i.getVendor() + "|" + i.getVersion() + "\r\n";
/*    */           } 
/*    */ 
/*    */ 
/*    */           
/* 76 */           List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
/*    */           
/* 78 */           for (Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext(); )
/*    */           {
/* 80 */             HierarchicalConfiguration mprof = it.next();
/*    */             
/* 82 */             res = res + "profile|" + mprof.getString("[@name]") + "|" + mprof.getString("[@desc]") + "\r\n";
/*    */           }
/*    */         
/* 85 */         } catch (MidiUnavailableException e) {
/*    */           
/* 87 */           res = "enabled|false\n\n";
/*    */         } 
/*    */       }
/*    */     } 
/*    */     
/* 92 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdInstanceMIDIStatus.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */