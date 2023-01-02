/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWDMKDiskIDAM
/*    */ {
/*    */   private byte[] data;
/*    */   private byte idamptr_msb;
/*    */   private byte idamptr_lsb;
/*    */   
/*    */   public DWDMKDiskIDAM(byte msb, byte lsb, byte[] data) {
/* 13 */     this.data = data;
/* 14 */     this.idamptr_msb = msb;
/* 15 */     this.idamptr_lsb = lsb;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTrack() {
/* 20 */     return 0xFF & this.data[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSide() {
/* 25 */     return 0xFF & this.data[1];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSector() {
/* 30 */     return 0xFF & this.data[2];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSectorSize() {
/* 36 */     return (int)Math.pow(2.0D, (7 + this.data[3]));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDoubleDensity() {
/* 41 */     return ((0x3F & this.idamptr_msb) == 63);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getPtr() {
/* 46 */     return (0x3F & this.idamptr_msb) * 256 + (0xFF & this.idamptr_lsb);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDMKDiskIDAM.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */