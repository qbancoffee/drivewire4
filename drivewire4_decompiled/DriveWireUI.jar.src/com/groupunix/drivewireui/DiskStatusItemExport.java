/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemExport
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemExport(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(345);
/* 12 */     setY(84);
/* 13 */     setWidth(70);
/* 14 */     setHeight(32);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_export_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_export_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("export hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 28 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 30 */       setButton(true);
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
/*    */   public void handleClick() {
/* 45 */     MainWin.writeDiskTo(this.diskwin.currentDisk.getDriveNo());
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemExport.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */