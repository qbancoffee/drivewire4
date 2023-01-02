/*    */ package com.groupunix.drivewireserver.virtualprinter;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWVPrinterFX80CharacterSet
/*    */ {
/*  7 */   private DWVPrinterFX80Character[] characters = new DWVPrinterFX80Character[256];
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCharacter(int charnum, int[] bits, int len) {
/* 12 */     this.characters[charnum] = new DWVPrinterFX80Character(bits, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCharacterCol(int charnum, int colnum) {
/* 17 */     return this.characters[charnum].getCol(colnum);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinterFX80CharacterSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */