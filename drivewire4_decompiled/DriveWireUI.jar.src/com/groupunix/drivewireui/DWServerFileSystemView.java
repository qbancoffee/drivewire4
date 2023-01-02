/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ 
/*     */ public class DWServerFileSystemView
/*     */   extends FileSystemView
/*     */ {
/*  11 */   private String lastdir = ".";
/*  12 */   private DWServerFileChooser chooser = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public DWServerFileSystemView(DWServerFileChooser chooser) {
/*  17 */     this.chooser = chooser;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public File createNewFolder(File arg0) throws IOException {
/*  23 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File[] getRoots() {
/*  30 */     if (this.chooser.getRootCache() == null) {
/*     */       
/*     */       try {
/*     */ 
/*     */         
/*  35 */         this.chooser.setRootCache((File[])UIUtils.getFileArray("ui server file roots"));
/*     */       }
/*  37 */       catch (IOException e) {
/*     */         
/*  39 */         MainWin.showError("An IO error occured", "Unable to retrieve a list of file system roots.", e.getMessage());
/*     */       }
/*  41 */       catch (DWUIOperationFailedException e) {
/*     */         
/*  43 */         MainWin.showError("A DW error occured", "Unable to retrieve a list of file system roots.", e.getMessage());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  48 */     return this.chooser.getRootCache();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWServerFile getDefaultDirectory() {
/*  54 */     DWServerFile[] dd = null;
/*     */     
/*     */     try {
/*  57 */       dd = UIUtils.getFileArray("ui server file defaultdir");
/*     */       
/*  59 */       if (dd != null && dd.length == 1)
/*     */       {
/*  61 */         return dd[0];
/*     */       
/*     */       }
/*     */     
/*     */     }
/*  66 */     catch (IOException e) {
/*     */       
/*  68 */       MainWin.showError("An IO error occured", "Unable to retrieve default directory.", e.getMessage());
/*     */     
/*     */     }
/*  71 */     catch (DWUIOperationFailedException e) {
/*     */       
/*  73 */       MainWin.showError("A DW error occured", "Unable to retrieve default directory.", e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWServerFile getHomeDirectory() {
/*  84 */     return getDefaultDirectory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWServerFile[] getFiles(File dir, boolean useFileHiding) {
/*  92 */     this.lastdir = dir.getPath();
/*  93 */     this.chooser.setLastDirectory(this.lastdir);
/*  94 */     DWServerFile[] res = null;
/*     */     
/*     */     try {
/*  97 */       res = UIUtils.getFileArray("ui server file dir " + dir.getPath());
/*     */     }
/*  99 */     catch (IOException e) {
/*     */       
/* 101 */       MainWin.showError("An IO error occured", "Unable to retrieve a list of files.", e.getMessage());
/*     */     }
/* 103 */     catch (DWUIOperationFailedException e) {
/*     */       
/* 105 */       MainWin.showError("A DW error occured", "Unable to retrieve a list of files.", e.getMessage());
/*     */     } 
/*     */     
/* 108 */     if (res != null && res.length > 0)
/*     */     {
/* 110 */       this.chooser.setSeparator(res[0].getSeparator());
/*     */     }
/* 112 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWServerFile createFileObject(DWServerFile dir, String filename) {
/* 119 */     DWServerFile f = new DWServerFile(dir.getAbsolutePath() + dir.getSeparator() + filename);
/* 120 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File createFileObject(String path) {
/* 127 */     DWServerFile f = null;
/*     */     
/* 129 */     if (path == null || this.chooser == null || this.chooser.getSeparator() == null)
/*     */     {
/* 131 */       return new DWServerFile("THERE IS NO FILE YOU INSENSITIVE CLOD");
/*     */     }
/*     */ 
/*     */     
/* 135 */     if (path.indexOf(this.chooser.getSeparator()) > -1) {
/*     */       
/* 137 */       f = new DWServerFile(path);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 142 */       f = new DWServerFile(this.chooser.getLastDirectory() + this.chooser.getSeparator() + path);
/*     */     } 
/*     */ 
/*     */     
/* 146 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public File getChild(File parent, String fileName) {
/* 152 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public File getParentDirectory(File dir) {
/* 158 */     if (dir.getParent() == null)
/* 159 */       return dir; 
/* 160 */     return new DWServerFile(dir.getParent());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSystemDisplayName(File f) {
/* 167 */     return f.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Icon getSystemIcon(File f) {
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSystemTypeDescription(File f) {
/* 177 */     return "description";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isComputerNode(File dir) {
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDrive(File dir) {
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFileSystem(File f) {
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFileSystemRoot(File dir) {
/* 198 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFloppyDrive(File dir) {
/* 203 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isHiddenFile(File f) {
/* 208 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParent(File folder, File file) {
/* 213 */     if (file.getPath().startsWith(folder.getPath()))
/* 214 */       return true; 
/* 215 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRoot(DWServerFile f) {
/* 221 */     if (f == null) {
/* 222 */       return false;
/*     */     }
/* 224 */     return f.isRoot();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean isTraversable(File f) {
/* 231 */     if (f.isDirectory()) {
/* 232 */       return Boolean.valueOf(true);
/*     */     }
/* 234 */     return Boolean.valueOf(false);
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWServerFileSystemView.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */