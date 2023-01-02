/*    */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*    */ 
/*    */ 
/*    */ public class DWRFMPathDescriptor
/*    */ {
/*    */   private byte[] pdbytes;
/*    */   
/*    */   public DWRFMPathDescriptor(byte[] responsebuf) {
/*  9 */     setPdbytes(responsebuf);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setPdbytes(byte[] pdbytes) {
/* 14 */     this.pdbytes = pdbytes;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getPdbytes() {
/* 19 */     return this.pdbytes;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getPD() {
/* 24 */     return this.pdbytes[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getMOD() {
/* 29 */     return this.pdbytes[1];
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getCNT() {
/* 34 */     return this.pdbytes[2];
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getDEV() {
/* 39 */     byte[] tmp = new byte[2];
/*    */     
/* 41 */     tmp[0] = this.pdbytes[3];
/* 42 */     tmp[1] = this.pdbytes[4];
/*    */     
/* 44 */     return tmp;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getCPR() {
/* 49 */     return this.pdbytes[5];
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getRGS() {
/* 54 */     byte[] tmp = new byte[2];
/*    */     
/* 56 */     tmp[0] = this.pdbytes[6];
/* 57 */     tmp[1] = this.pdbytes[7];
/*    */     
/* 59 */     return tmp;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getBUF() {
/* 64 */     byte[] tmp = new byte[2];
/*    */     
/* 66 */     tmp[0] = this.pdbytes[8];
/* 67 */     tmp[1] = this.pdbytes[9];
/*    */     
/* 69 */     return tmp;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getFST() {
/* 74 */     byte[] tmp = new byte[32];
/* 75 */     System.arraycopy(this.pdbytes, 10, tmp, 0, 32);
/* 76 */     return tmp;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWRFMPathDescriptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */