/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWDMKDiskTrack
/*    */ {
/*    */   private byte[] trackdata;
/*    */   
/*    */   public DWDMKDiskTrack(byte[] data) {
/* 10 */     this.trackdata = new byte[data.length];
/* 11 */     System.arraycopy(data, 0, this.trackdata, 0, data.length);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DWDMKDiskIDAM getIDAM(int index) {
/* 18 */     byte msb = this.trackdata[index * 2 + 1];
/* 19 */     byte lsb = this.trackdata[index * 2];
/*    */     
/* 21 */     byte[] ibuf = new byte[6];
/* 22 */     System.arraycopy(this.trackdata, getIDAMPtr(msb, lsb) + 1, ibuf, 0, 6);
/*    */     
/* 24 */     return new DWDMKDiskIDAM(msb, lsb, ibuf);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getIDAMPtr(byte msb, byte lsb) {
/* 30 */     return (0x3F & msb) * 256 + (0xFF & lsb);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] getData() {
/* 36 */     return this.trackdata;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumSectors() {
/* 42 */     int ns = 0;
/* 43 */     for (int i = 0; i < 64; i++) {
/*    */       
/* 45 */       if (getIDAM(i).getPtr() != 0)
/* 46 */         ns++; 
/*    */     } 
/* 48 */     return ns;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDMKDiskTrack.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */