/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ 
/*    */ public class DWDECBFileSystemDirExtensionMapping
/*    */ {
/*    */   private String extension;
/*    */   private byte flag;
/*    */   private byte type;
/*    */   
/*    */   public DWDECBFileSystemDirExtensionMapping(String ext, byte flag, byte type) {
/* 11 */     this.extension = ext;
/* 12 */     this.flag = flag;
/* 13 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setExtension(String extension) {
/* 18 */     this.extension = extension;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getExtension() {
/* 23 */     return this.extension;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setFlag(byte flag) {
/* 28 */     this.flag = flag;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getFlag() {
/* 33 */     return this.flag;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setType(byte type) {
/* 38 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getType() {
/* 43 */     return this.type;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDECBFileSystemDirExtensionMapping.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */