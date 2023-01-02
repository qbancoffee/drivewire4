/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWDiskFileDescriptor
/*    */ {
/*    */   private ByteBuffer FD;
/*    */   private byte[] fdBytes;
/*    */   
/*    */   public DWDiskFileDescriptor(byte[] sector) {
/* 14 */     this.fdBytes = sector;
/* 15 */     this.FD = ByteBuffer.wrap(this.fdBytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte FD_ATT() {
/* 20 */     return this.fdBytes[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public long FD_OWN() {
/* 25 */     byte[] fd_own = new byte[2];
/* 26 */     this.FD.position(1);
/* 27 */     this.FD.get(fd_own, 0, 2);
/* 28 */     return DWUtils.int2(fd_own);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] FD_DAT() {
/* 33 */     byte[] fd_dat = new byte[5];
/* 34 */     this.FD.position(3);
/* 35 */     this.FD.get(fd_dat, 0, 5);
/* 36 */     return fd_dat;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte FD_LNK() {
/* 41 */     return this.fdBytes[8];
/*    */   }
/*    */ 
/*    */   
/*    */   public long FD_SIZ() {
/* 46 */     byte[] fd_siz = new byte[4];
/* 47 */     this.FD.position(9);
/* 48 */     this.FD.get(fd_siz, 0, 4);
/* 49 */     return DWUtils.int4(fd_siz);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] FD_CREAT() {
/* 54 */     byte[] fd_creat = new byte[3];
/* 55 */     this.FD.position(13);
/* 56 */     this.FD.get(fd_creat, 0, 3);
/* 57 */     return fd_creat;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] FD_SEG() {
/* 62 */     byte[] fd_seg = new byte[240];
/* 63 */     this.FD.position(16);
/* 64 */     this.FD.get(fd_seg, 0, 240);
/* 65 */     return fd_seg;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskFileDescriptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */