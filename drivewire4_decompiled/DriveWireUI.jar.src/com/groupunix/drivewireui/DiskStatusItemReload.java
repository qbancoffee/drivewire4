/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemReload
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemReload(DiskWin diskwin) {
/* 10 */     super(diskwin);
/* 11 */     setX(240);
/* 12 */     setY(84);
/* 13 */     setWidth(70);
/* 14 */     setHeight(32);
/*    */     
/* 16 */     setDisabledImage("/disk/disk_reload_disabled.png");
/* 17 */     setEnabledImage("/disk/disk_reload_enabled.png");
/* 18 */     setClickImage("/animals/alien.png");
/*    */     
/* 20 */     setButton(false);
/* 21 */     setHovertext("reload hover text");
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
/* 45 */     MainWin.sendCommand("dw disk reload " + this.diskwin.currentDisk.getDriveNo());
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemReload.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */