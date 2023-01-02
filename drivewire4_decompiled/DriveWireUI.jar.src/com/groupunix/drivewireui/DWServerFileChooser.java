/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.io.File;
/*    */ import javax.swing.JFileChooser;
/*    */ import javax.swing.filechooser.FileSystemView;
/*    */ 
/*    */ public class DWServerFileChooser
/*    */   extends JFileChooser
/*    */ {
/*    */   private static final long serialVersionUID = -1032049099726544597L;
/* 11 */   private String lastdir = ".";
/* 12 */   private String separator = "/";
/* 13 */   private File[] rootcache = null;
/*    */ 
/*    */ 
/*    */   
/*    */   public DWServerFileChooser() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public DWServerFileChooser(String text) {
/* 22 */     super(text);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FileSystemView getFileSystemView() {
/* 29 */     return new DWServerFileSystemView(this);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setLastDirectory(String lastdir) {
/* 36 */     this.lastdir = lastdir;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getLastDirectory() {
/* 41 */     return this.lastdir;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSeparator(String separator) {
/* 46 */     this.separator = separator;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSeparator() {
/* 51 */     return this.separator;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSelectedFile(String fname) {
/* 57 */     setSelectedFile(new DWServerFile(fname));
/*    */   }
/*    */ 
/*    */   
/*    */   public File[] getRootCache() {
/* 62 */     return this.rootcache;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setRootCache(File[] rc) {
/* 67 */     this.rootcache = rc;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWServerFileChooser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */