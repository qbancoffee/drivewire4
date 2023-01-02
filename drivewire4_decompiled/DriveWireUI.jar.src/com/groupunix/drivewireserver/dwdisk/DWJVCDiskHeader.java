/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ 
/*    */ public class DWJVCDiskHeader
/*    */ {
/*    */   private byte[] data;
/*    */   
/*    */   public void setData(byte[] dat) {
/*  9 */     this.data = new byte[dat.length];
/* 10 */     System.arraycopy(dat, 0, this.data, 0, dat.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSectorsPerTrack() {
/* 15 */     if (this.data == null || this.data.length < 1) {
/* 16 */       return 18;
/*    */     }
/* 18 */     return 0xFF & this.data[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSides() {
/* 23 */     if (this.data == null || this.data.length < 2) {
/* 24 */       return 1;
/*    */     }
/* 26 */     return 0xFF & this.data[1];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSectorSize() {
/* 32 */     if (this.data == null || this.data.length < 3) {
/* 33 */       return 256;
/*    */     }
/* 35 */     return 128 << this.data[2];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getFirstSector() {
/* 41 */     if (this.data == null || this.data.length < 4) {
/* 42 */       return 1;
/*    */     }
/* 44 */     return 0xFF & this.data[3];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSectorAttributes() {
/* 49 */     if (this.data == null || this.data.length < 5) {
/* 50 */       return 0;
/*    */     }
/* 52 */     return 0xFF & this.data[4];
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWJVCDiskHeader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */