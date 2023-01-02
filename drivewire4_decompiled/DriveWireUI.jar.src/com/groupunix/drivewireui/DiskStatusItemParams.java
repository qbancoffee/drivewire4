/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskStatusItemParams
/*    */   extends DiskStatusItem
/*    */ {
/*    */   public DiskStatusItemParams(DiskWin diskwin) {
/* 12 */     super(diskwin);
/* 13 */     setX(175);
/* 14 */     setY(352);
/* 15 */     setWidth(79);
/* 16 */     setHeight(29);
/*    */     
/* 18 */     setDisabledImage("/disk/disk_edit_params_disabled.png");
/* 19 */     setEnabledImage("/disk/disk_edit_params_enabled.png");
/* 20 */     setClickImage("/animals/alien.png");
/*    */     
/* 22 */     setButton(false);
/* 23 */     setHovertext("edit params hover text");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDisk(DiskDef diskDef) {
/* 30 */     if (diskDef != null && diskDef.isLoaded()) {
/*    */       
/* 32 */       setButton(true);
/* 33 */       setEnabled(true);
/*    */     
/*    */     }
/*    */     else {
/*    */ 
/*    */       
/* 39 */       setButton(false);
/* 40 */       setEnabled(false);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleClick() {
/* 48 */     if (this.diskwin.currentDisk.hasParamwin()) {
/*    */       
/* 50 */       (this.diskwin.currentDisk.getParamwin()).shell.setActive();
/*    */     }
/*    */     else {
/*    */       
/* 54 */       this.diskwin.currentDisk.setParamwin(new DiskAdvancedWin(this.diskwin.shlDwDrive, 2144, this.diskwin.currentDisk));
/* 55 */       this.diskwin.currentDisk.getParamwin().open();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItemParams.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */