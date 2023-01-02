/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RemoteFileBrowser
/*    */   implements Runnable
/*    */ {
/*    */   String startpath;
/*    */   boolean dir;
/*    */   boolean save;
/*    */   String title;
/*    */   String buttontext;
/*    */   String[] fileext;
/* 14 */   String selected = null;
/*    */ 
/*    */   
/*    */   public RemoteFileBrowser(boolean save, boolean dir, String startpath, String title, String buttontext, String[] fileext) {
/* 18 */     this.startpath = startpath;
/* 19 */     this.dir = dir;
/* 20 */     this.save = save;
/* 21 */     this.title = title;
/* 22 */     this.buttontext = buttontext;
/* 23 */     this.fileext = fileext;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     DWServerFileChooser fileChooser;
/* 33 */     if (!this.startpath.equals("")) {
/*    */       
/* 35 */       fileChooser = new DWServerFileChooser(this.startpath);
/*    */     }
/*    */     else {
/*    */       
/* 39 */       fileChooser = new DWServerFileChooser();
/*    */     } 
/*    */ 
/*    */     
/* 43 */     fileChooser.setFileHidingEnabled(false);
/* 44 */     fileChooser.setMultiSelectionEnabled(false);
/*    */     
/* 46 */     if (this.dir) {
/* 47 */       fileChooser.setFileSelectionMode(1);
/*    */     } else {
/* 49 */       fileChooser.setFileSelectionMode(0);
/*    */     } 
/* 51 */     if (this.save) {
/* 52 */       fileChooser.setDialogType(1);
/*    */     } else {
/* 54 */       fileChooser.setDialogType(0);
/*    */     } 
/* 56 */     fileChooser.setDialogTitle(this.title);
/*    */     
/* 58 */     if (!this.startpath.equals(""))
/*    */     {
/* 60 */       fileChooser.setSelectedFile(new DWServerFile(this.startpath));
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 65 */     fileChooser.setAcceptAllFileFilterUsed(true);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 85 */     int answer = fileChooser.showDialog(fileChooser, this.buttontext);
/*    */ 
/*    */ 
/*    */     
/* 89 */     if (answer == 0)
/*    */     {
/* 91 */       this.selected = fileChooser.getSelectedFile().getPath();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSelected() {
/* 98 */     return this.selected;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/RemoteFileBrowser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */