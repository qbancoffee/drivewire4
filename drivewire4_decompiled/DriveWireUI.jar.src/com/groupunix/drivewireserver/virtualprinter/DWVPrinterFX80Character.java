/*    */ package com.groupunix.drivewireserver.virtualprinter;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWVPrinterFX80Character
/*    */ {
/*    */   private int[] bits;
/*    */   private int len;
/*    */   
/*    */   public DWVPrinterFX80Character(int[] bits, int len) {
/* 11 */     this.bits = bits;
/* 12 */     this.len = len;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCol(int col) {
/* 19 */     return this.bits[col];
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLen() {
/* 24 */     return this.len;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinterFX80Character.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */