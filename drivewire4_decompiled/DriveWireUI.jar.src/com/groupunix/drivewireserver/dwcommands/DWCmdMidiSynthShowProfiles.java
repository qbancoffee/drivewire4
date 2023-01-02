/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCmdMidiSynthShowProfiles
/*    */   extends DWCommand
/*    */ {
/*    */   public DWCmdMidiSynthShowProfiles(DWCommand parent) {
/* 15 */     setParentCmd(parent);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommand() {
/* 20 */     return "profiles";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 26 */     return "Show internal synth profiles";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 32 */     return "dw midi synth show profiles";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     String text = new String();
/*    */     
/* 40 */     text = "\r\nAvailable sound translation profiles:\r\n\n";
/*    */     
/* 42 */     List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
/*    */     
/* 44 */     for (Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext(); ) {
/*    */       
/* 46 */       HierarchicalConfiguration mprof = it.next();
/*    */       
/* 48 */       text = text + String.format("%-10s: %s", new Object[] { mprof.getString("[@name]"), mprof.getString("[@desc]") });
/* 49 */       text = text + "\r\n";
/*    */     } 
/*    */     
/* 52 */     text = text + "\r\n";
/*    */     
/* 54 */     return new DWCommandResponse(text);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 60 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCmdMidiSynthShowProfiles.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */