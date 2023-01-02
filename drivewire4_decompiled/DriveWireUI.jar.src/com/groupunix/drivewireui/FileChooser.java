/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileChooser
/*    */ {
/*    */   private String fname;
/*    */   private String desc;
/*    */   private boolean dir;
/*    */   
/*    */   public FileChooser(String fname, String desc, boolean dir) {
/* 16 */     this.fname = fname;
/* 17 */     this.desc = desc;
/* 18 */     this.dir = dir;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getFile() {
/*    */     DWServerFileChooser fileChooser;
/* 29 */     if (this.fname == null || this.fname.equals("")) {
/*    */       
/* 31 */       fileChooser = new DWServerFileChooser();
/*    */     }
/*    */     else {
/*    */       
/* 35 */       fileChooser = new DWServerFileChooser(this.fname);
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     fileChooser.setFileHidingEnabled(false);
/* 42 */     fileChooser.setMultiSelectionEnabled(false);
/* 43 */     if (this.dir) {
/*    */       
/* 45 */       fileChooser.setFileSelectionMode(1);
/*    */     }
/*    */     else {
/*    */       
/* 49 */       fileChooser.setFileSelectionMode(0);
/*    */     } 
/* 51 */     fileChooser.setDialogType(1);
/* 52 */     fileChooser.setSelectedFile(this.fname);
/*    */ 
/*    */     
/* 55 */     int answer = fileChooser.showDialog(fileChooser, this.desc);
/*    */ 
/*    */     
/* 58 */     if (answer == 0) {
/*    */       
/* 60 */       File selected = fileChooser.getSelectedFile();
/*    */       
/* 62 */       return selected.getPath();
/*    */     } 
/*    */ 
/*    */     
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/FileChooser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */