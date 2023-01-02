/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ public class DWDMKDiskHeader
/*    */ {
/*  5 */   private byte[] header = new byte[16];
/*    */ 
/*    */   
/*    */   public DWDMKDiskHeader(byte[] hdr) {
/*  9 */     System.arraycopy(hdr, 0, this.header, 0, 16);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isWriteProtected() {
/* 15 */     if (this.header[0] == 255)
/* 16 */       return true; 
/* 17 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getTracks() {
/* 23 */     return 0xFF & this.header[1];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getTrackLength() {
/* 29 */     return (0xFF & this.header[3]) * 256 + (0xFF & this.header[2]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getOptions() {
/* 35 */     return 0xFF & this.header[4];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isSingleSided() {
/* 41 */     return ((0x10 & this.header[4]) == 16);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isSingleDensity() {
/* 47 */     return ((0x40 & this.header[4]) == 64);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isRealDisk() {
/* 56 */     if ((0xFF & this.header[12]) == 18 && (0xFF & this.header[13]) == 52 && (0xFF & this.header[14]) == 86 && (0xFF & this.header[15]) == 120) {
/* 57 */       return true;
/*    */     }
/* 59 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSides() {
/* 64 */     if (isSingleSided()) {
/* 65 */       return 1;
/*    */     }
/* 67 */     return 2;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getDensity() {
/* 73 */     if (isSingleDensity()) {
/* 74 */       return Integer.valueOf(1);
/*    */     }
/* 76 */     return Integer.valueOf(2);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDMKDiskHeader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */