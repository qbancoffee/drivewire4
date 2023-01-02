/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemOffset
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemOffset(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(20);
/* 12 */     setY(352);
/* 13 */     setWidth(56);
/* 14 */     setHeight(24);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_offset_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_offset_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("offset hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded() && diskDef.getOffset() > 0) {
/*    */       
/* 30 */       setButton(false);
/* 31 */       setEnabled(true);
/*    */     
/*    */     }
/*    */     else {
/*    */ 
/*    */       
/* 37 */       setButton(false);
/* 38 */       setEnabled(false);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void handleClick() {}
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemOffset.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */