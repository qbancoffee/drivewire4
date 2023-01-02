/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemExpand
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemExpand(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(372);
/* 12 */     setY(352);
/* 13 */     setWidth(57);
/* 14 */     setHeight(24);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_expand_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_expand_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("expand hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded() && diskDef.isExpand()) {
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


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemExpand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */