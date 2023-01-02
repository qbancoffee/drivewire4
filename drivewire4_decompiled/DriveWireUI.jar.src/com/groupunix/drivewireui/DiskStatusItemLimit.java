/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemLimit
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemLimit(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(305);
/* 12 */     setY(352);
/* 13 */     setWidth(46);
/* 14 */     setHeight(24);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_sizelimit_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_sizelimit_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("limit hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded() && diskDef.getLimit() > -1) {
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


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemLimit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */