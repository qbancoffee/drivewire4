/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MIDIProfile
/*    */ {
/*    */   private String name;
/*    */   private String desc;
/*    */   
/*    */   public MIDIProfile(String name, String desc) {
/* 11 */     setName(name);
/* 12 */     setDesc(desc);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setName(String name) {
/* 17 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 22 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setDesc(String desc) {
/* 27 */     this.desc = desc;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDesc() {
/* 32 */     return this.desc;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/MIDIProfile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */