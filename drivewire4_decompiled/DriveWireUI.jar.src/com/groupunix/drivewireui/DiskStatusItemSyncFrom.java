/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemSyncFrom
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemSyncFrom(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(135);
/* 12 */     setY(84);
/* 13 */     setWidth(60);
/* 14 */     setHeight(32);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_sync_from_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_sync_from_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("sync-from hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 30 */       setButton(diskDef.hasParam("syncfrom"));
/* 31 */       setEnabled(diskDef.isSyncFrom());
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
/*    */   public void handleClick() {
/* 45 */     MainWin.sendCommand("dw disk set " + this.diskwin.currentDisk.getDriveNo() + " syncfrom " + (!isEnabled() ? 1 : 0));
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemSyncFrom.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */