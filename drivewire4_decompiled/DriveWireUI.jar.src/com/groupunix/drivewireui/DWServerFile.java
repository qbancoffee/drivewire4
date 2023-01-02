/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class DWServerFile
/*     */   extends File
/*     */ {
/*     */   private static final long serialVersionUID = -5842864119672365113L;
/*     */   private String pathname;
/*     */   private String aseparator;
/*     */   private String parent;
/*  17 */   private long length = 0L;
/*  18 */   private long lastmodified = 0L;
/*     */   
/*     */   private boolean directory = false;
/*     */   private boolean root = false;
/*     */   
/*     */   public DWServerFile(String pathname) {
/*  24 */     super(pathname);
/*  25 */     this.pathname = pathname;
/*     */     
/*  27 */     if (pathname != null && !pathname.equals(".")) {
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/*  33 */         String vals = UIUtils.loadList("ui server file info " + pathname).get(0);
/*  34 */         setVals(vals);
/*     */       }
/*  36 */       catch (IOException e) {
/*     */ 
/*     */         
/*  39 */         e.printStackTrace();
/*     */       }
/*  41 */       catch (DWUIOperationFailedException e) {
/*     */ 
/*     */         
/*  44 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVals(String vals) {
/*  52 */     String[] parts = vals.split("\\|");
/*  53 */     if (parts.length == 7) {
/*     */       
/*  55 */       this.aseparator = parts[0];
/*  56 */       this.pathname = parts[1];
/*  57 */       this.parent = parts[2];
/*  58 */       this.length = Long.parseLong(parts[3]);
/*  59 */       this.lastmodified = Long.parseLong(parts[4]);
/*  60 */       this.directory = Boolean.parseBoolean(parts[5]);
/*  61 */       this.root = Boolean.parseBoolean(parts[6]);
/*     */     }
/*     */     else {
/*     */       
/*  65 */       System.out.println("Possible error in remote file browse response: " + vals);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canRead() {
/*  72 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canWrite() {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean createNewFile() {
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static File createTempFile(String prefix, String suffix) {
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static File createTempFile(String prefix, String suffix, File directory) {
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean delete() {
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteOnExit() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exists() {
/* 111 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getAbsoluteFile() {
/* 116 */     return new DWServerFile(this.pathname);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAbsolutePath() {
/* 122 */     return this.pathname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getCanonicalFile() {
/* 129 */     return new DWServerFile(this.pathname);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCanonicalPath() {
/* 135 */     return this.pathname;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 140 */     if (this.pathname == null) {
/* 141 */       return null;
/*     */     }
/* 143 */     String sep = this.aseparator;
/*     */ 
/*     */     
/* 146 */     if (sep.equals("\\"))
/*     */     {
/* 148 */       sep = "\\\\";
/*     */     }
/*     */     
/* 151 */     String[] parts = this.pathname.split(sep);
/* 152 */     if (parts.length > 1) {
/* 153 */       return parts[parts.length - 1];
/*     */     }
/* 155 */     return this.pathname;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getParent() {
/* 160 */     if (this.parent == null || this.parent.equals("null"))
/*     */     {
/* 162 */       return null;
/*     */     }
/* 164 */     return this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getParentFile() {
/* 169 */     if (this.parent == null || this.parent.equals("null"))
/*     */     {
/* 171 */       return null;
/*     */     }
/* 173 */     return new DWServerFile(this.parent);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 178 */     return this.pathname;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 183 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAbsolute() {
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 193 */     return this.directory;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFile() {
/* 199 */     if (this.directory)
/* 200 */       return false; 
/* 201 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isHidden() {
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long lastModified() {
/* 211 */     return this.lastmodified;
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 216 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] list() {
/* 222 */     System.out.println("list");
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] list(FilenameFilter filter) {
/* 228 */     System.out.println("list2");
/* 229 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public File[] listFiles() {
/* 235 */     System.out.println("listfiles");
/* 236 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public File[] listFiles(FileFilter filter) {
/* 241 */     System.out.println("listfiles2");
/* 242 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public File[] listFiles(FilenameFilter filter) {
/* 247 */     System.out.println("listfiles3");
/* 248 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static File[] listRoots() {
/* 253 */     System.out.println("listroots");
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean mkdir() {
/* 259 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mkdirs() {
/* 265 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(File dest) {
/* 270 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setLastModified(long time) {
/* 275 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/* 281 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 286 */     return this.pathname;
/*     */   }
/*     */ 
/*     */   
/*     */   public URI toURI() {
/* 291 */     System.out.println("touri");
/* 292 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public URL toURL() {
/* 297 */     System.out.println("tourl");
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRoot() {
/* 303 */     return this.root;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSeparator() {
/* 308 */     return this.aseparator;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWServerFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */