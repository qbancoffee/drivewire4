/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemLED
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemLED(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(25);
/* 12 */     setY(215);
/* 13 */     setWidth(41);
/* 14 */     setHeight(30);
/*    */     
/* 16 */     setDisabledImage("/disk/diskdrive-leddark-big.png");
/* 17 */     setEnabledImage("/disk/diskdrive-ledgreeen-big.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("led hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 30 */       setButton(false);
/* 31 */       setEnabled(true);
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 36 */       setButton(false);
/* 37 */       setEnabled(false);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleClick() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void redraw() {
/* 52 */     if (this.canvas != null && !this.canvas.isDisposed())
/*    */     {
/* 54 */       this.canvas.redraw();
/*    */     }
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemLED.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */