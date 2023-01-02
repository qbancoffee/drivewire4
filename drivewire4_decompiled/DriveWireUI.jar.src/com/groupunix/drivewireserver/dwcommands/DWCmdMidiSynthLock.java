/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ public class DWCmdMidiSynthLock
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthLock(DWProtocolHandler dwProto, DWCommand parent) {
/* 11 */     setParentCmd(parent);
/* 12 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 17 */     return "lock";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 24 */     return "Toggle instrument lock";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 30 */     return "dw midi synth lock";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 35 */     return doMidiSynthLock();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doMidiSynthLock() {
/* 41 */     if (this.dwProto.getVPorts().getMidiVoicelock()) {
/*    */       
/* 43 */       this.dwProto.getVPorts().setMidiVoicelock(false);
/* 44 */       return new DWCommandResponse("Unlocked MIDI instruments, program changes will be processed");
/*    */     } 
/*    */ 
/*    */     
/* 48 */     this.dwProto.getVPorts().setMidiVoicelock(true);
/* 49 */     return new DWCommandResponse("Locked MIDI instruments, progam changes will be ignored");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 55 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthLock.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */