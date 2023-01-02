/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthProfile
/*    */   extends DWCommand
/*    */ {
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthProfile(DWProtocolHandler dwProto, DWCommand parent) {
/* 12 */     setParentCmd(parent);
/* 13 */     this.dwProto = dwProto;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 18 */     return "profile";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 25 */     return "Load synth translation profile";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 31 */     return "dw midi synth profile name";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 36 */     if (cmdline.length() == 0)
/*    */     {
/* 38 */       return new DWCommandResponse(false, (byte)10, "dw midi synth profile requires a profile name as an argument");
/*    */     }
/*    */     
/* 41 */     return doMidiSynthProfile(cmdline);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private DWCommandResponse doMidiSynthProfile(String path) {
/* 48 */     if (this.dwProto.getVPorts().setMidiProfile(path))
/*    */     {
/* 50 */       return new DWCommandResponse("Set translation profile to '" + path + "'");
/*    */     }
/*    */ 
/*    */     
/* 54 */     return new DWCommandResponse(false, (byte)-100, "Invalid translation profile '" + path + "'");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 60 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthProfile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */