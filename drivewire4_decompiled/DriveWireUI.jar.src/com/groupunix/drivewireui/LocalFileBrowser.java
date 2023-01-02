/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import org.eclipse.swt.widgets.DirectoryDialog;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.eclipse.swt.widgets.FileDialog;
/*    */ 
/*    */ 
/*    */ public class LocalFileBrowser
/*    */   implements Runnable
/*    */ {
/*    */   String startpath;
/*    */   boolean dir;
/*    */   boolean save;
/*    */   String title;
/*    */   String buttontext;
/*    */   String[] fileext;
/* 17 */   String selected = null;
/*    */ 
/*    */   
/*    */   public LocalFileBrowser(boolean save, boolean dir, String startpath, String title, String buttontext, String[] fileext) {
/* 21 */     this.startpath = startpath;
/* 22 */     this.dir = dir;
/* 23 */     this.save = save;
/* 24 */     this.title = title;
/* 25 */     this.buttontext = buttontext;
/* 26 */     this.fileext = fileext;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 33 */     if (!this.dir) {
/*    */       FileDialog fd;
/*    */ 
/*    */       
/* 37 */       if (this.save) {
/* 38 */         fd = new FileDialog(Display.getCurrent().getActiveShell(), 8192);
/*    */       } else {
/* 40 */         fd = new FileDialog(Display.getCurrent().getActiveShell(), 4096);
/*    */       } 
/* 42 */       fd.setText(this.title);
/* 43 */       fd.setFilterPath(this.startpath);
/*    */       
/* 45 */       if (this.fileext != null) {
/* 46 */         fd.setFilterExtensions(this.fileext);
/*    */       } else {
/* 48 */         fd.setFilterExtensions(new String[] { "*.*" });
/*    */       } 
/* 50 */       this.selected = fd.open();
/*    */     }
/*    */     else {
/*    */       
/* 54 */       DirectoryDialog dd = new DirectoryDialog(Display.getCurrent().getActiveShell());
/*    */       
/* 56 */       dd.setFilterPath(this.startpath);
/* 57 */       dd.setText(this.title);
/* 58 */       dd.setMessage(this.buttontext);
/*    */       
/* 60 */       this.selected = dd.open();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSelected() {
/* 67 */     return this.selected;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/LocalFileBrowser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */