/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWVDKDiskHeader
/*    */ {
/*    */   private byte[] data;
/*    */   
/*    */   public DWVDKDiskHeader(byte[] hbuff) {
/* 10 */     this.data = new byte[hbuff.length + 4];
/* 11 */     System.arraycopy(hbuff, 0, this.data, 4, hbuff.length);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getVersion() {
/* 17 */     return 0xFF & this.data[4];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getVersionCompatible() {
/* 22 */     return 0xFF & this.data[5];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSourceID() {
/* 27 */     return 0xFF & this.data[6];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSourceVersion() {
/* 32 */     return 0xFF & this.data[7];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTracks() {
/* 37 */     return 0xFF & this.data[8];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSides() {
/* 42 */     return 0xFF & this.data[9];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getFlags() {
/* 47 */     return 0xFF & this.data[10];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getHeaderLen() {
/* 52 */     return this.data.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isWriteProtected() {
/* 57 */     if ((this.data[10] & 0x1) == 1) {
/* 58 */       return true;
/*    */     }
/* 60 */     return false;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWVDKDiskHeader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */