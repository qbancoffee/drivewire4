/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemWriteProtect
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemWriteProtect(DiskWin diskwin) {
/* 10 */     super(diskwin);
/*    */     
/* 12 */     setX(75);
/* 13 */     setY(225);
/* 14 */     setWidth(41);
/* 15 */     setHeight(24);
/*    */     
/* 17 */     setDisabledImage("/disk/disk_writeprotect_disabled.png");
/* 18 */     setEnabledImage("/disk/disk_writeprotect_enabled.png");
/* 19 */     setClickImage("/animals/alien.png");
/*    */     
/* 21 */     setButton(false);
/* 22 */     setHovertext("wp hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 29 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 31 */       setButton(true);
/* 32 */       setEnabled(diskDef.isWriteProtect());
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 37 */       setButton(false);
/* 38 */       setEnabled(false);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleClick() {
/* 46 */     MainWin.sendCommand("dw disk set " + this.diskwin.currentDisk.getDriveNo() + " writeprotect " + (!isEnabled() ? 1 : 0));
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemWriteProtect.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */