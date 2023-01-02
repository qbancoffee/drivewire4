/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ public class MIDIDevice
/*    */ {
/*    */   private String name;
/*    */   private String desc;
/*    */   private String vendor;
/*    */   private String version;
/*    */   private String type;
/*    */   private int devnum;
/*    */   
/*    */   public MIDIDevice(int devnum, String type, String name, String desc, String vendor, String version) {
/* 14 */     setDevnum(devnum);
/* 15 */     setName(name);
/* 16 */     setDesc(desc);
/* 17 */     setVendor(vendor);
/* 18 */     setVersion(version);
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 22 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setDesc(String desc) {
/* 30 */     this.desc = desc;
/*    */   }
/*    */   
/*    */   public String getDesc() {
/* 34 */     return this.desc;
/*    */   }
/*    */   
/*    */   public void setVendor(String vendor) {
/* 38 */     this.vendor = vendor;
/*    */   }
/*    */   
/*    */   public String getVendor() {
/* 42 */     return this.vendor;
/*    */   }
/*    */   
/*    */   public void setVersion(String version) {
/* 46 */     this.version = version;
/*    */   }
/*    */   
/*    */   public String getVersion() {
/* 50 */     return this.version;
/*    */   }
/*    */   
/*    */   public void setDevnum(int devnum) {
/* 54 */     this.devnum = devnum;
/*    */   }
/*    */   
/*    */   public int getDevnum() {
/* 58 */     return this.devnum;
/*    */   }
/*    */   
/*    */   public void setType(String type) {
/* 62 */     this.type = type;
/*    */   }
/*    */   
/*    */   public String getType() {
/* 66 */     return this.type;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/MIDIDevice.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */