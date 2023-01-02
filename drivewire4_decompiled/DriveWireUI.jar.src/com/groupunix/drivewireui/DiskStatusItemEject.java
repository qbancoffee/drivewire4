/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemEject
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemEject(DiskWin diskwin) {
/* 10 */     super(diskwin);
/*    */     
/* 12 */     setX(364);
/* 13 */     setY(156);
/* 14 */     setWidth(30);
/* 15 */     setHeight(15);
/*    */     
/* 17 */     setDisabledImage(null);
/* 18 */     setEnabledImage("/disk/disk_eject_enabled.png");
/* 19 */     setClickImage("/animals/alien.png");
/*    */     
/* 21 */     setButton(false);
/* 22 */     setHovertext("eject hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 29 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 31 */       setButton(true);
/* 32 */       setEnabled(true);
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
/* 46 */     MainWin.sendCommand("dw disk eject " + this.diskwin.currentDisk.getDriveNo());
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemEject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */