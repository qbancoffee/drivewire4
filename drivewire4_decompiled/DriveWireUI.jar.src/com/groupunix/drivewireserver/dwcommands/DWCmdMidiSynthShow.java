/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthShow
/*    */   extends DWCommand
/*    */ {
/*    */   private DWCommandList commands;
/*    */   private DWProtocolHandler dwProto;
/*    */   
/*    */   public DWCmdMidiSynthShow(DWProtocolHandler dwProto, DWCommand parent) {
/* 14 */     setParentCmd(parent);
/* 15 */     this.dwProto = dwProto;
/* 16 */     this.commands = new DWCommandList((DWProtocol)this.dwProto, this.dwProto.getCMDCols());
/* 17 */     this.commands.addcommand(new DWCmdMidiSynthShowChannels(dwProto, this));
/* 18 */     this.commands.addcommand(new DWCmdMidiSynthShowInstr(dwProto, this));
/* 19 */     this.commands.addcommand(new DWCmdMidiSynthShowProfiles(this));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 24 */     return "show";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandList getCommandList() {
/* 29 */     return this.commands;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 35 */     return "View details about the synth";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 41 */     return "dw midi synth show [item]";
/*    */   }
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 46 */     if (cmdline.length() == 0)
/*    */     {
/* 48 */       return new DWCommandResponse(this.commands.getShortHelp());
/*    */     }
/* 50 */     return this.commands.parse(cmdline);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 55 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthShow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */