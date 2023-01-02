/*    */ package com.groupunix.drivewireserver.uicommands;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommand;
/*    */ import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UICmdServerShowSynthProfiles
/*    */   extends DWCommand
/*    */ {
/*    */   public String getCommand() {
/* 18 */     return "synthprofiles";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getShortHelp() {
/* 25 */     return "show MIDI synth profiles";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsage() {
/* 31 */     return "ui server show synthprofiles";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse parse(String cmdline) {
/* 38 */     String res = new String();
/*    */     
/* 40 */     List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");
/*    */     
/* 42 */     for (Iterator<HierarchicalConfiguration> it = profiles.iterator(); it.hasNext(); ) {
/*    */ 
/*    */       
/* 45 */       HierarchicalConfiguration mprof = it.next();
/* 46 */       res = res + mprof.getString("name") + " " + mprof.getString("desc") + "\n";
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 53 */     return new DWCommandResponse(res);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean validate(String cmdline) {
/* 58 */     return true;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/uicommands/UICmdServerShowSynthProfiles.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */