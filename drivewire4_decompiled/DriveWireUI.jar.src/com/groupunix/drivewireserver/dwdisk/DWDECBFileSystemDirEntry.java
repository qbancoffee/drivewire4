/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDECBFileSystemDirEntry
/*     */ {
/*  23 */   byte[] data = new byte[32];
/*     */ 
/*     */   
/*     */   public DWDECBFileSystemDirEntry(byte[] buf) {
/*  27 */     System.arraycopy(buf, 0, this.data, 0, 32);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getFileName() {
/*  32 */     return (new String(this.data)).substring(0, 8);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getFileExt() {
/*  37 */     return (new String(this.data)).substring(8, 11);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUsed() {
/*  42 */     if (this.data[0] == -1) {
/*  43 */       return false;
/*     */     }
/*  45 */     if (this.data[0] == 0) {
/*  46 */       return false;
/*     */     }
/*  48 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isKilled() {
/*  53 */     if (this.data[0] == 0) {
/*  54 */       return true;
/*     */     }
/*  56 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getFileType() {
/*  63 */     return this.data[11];
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPrettyFileType() {
/*  68 */     String res = "unknown";
/*     */     
/*  70 */     switch (this.data[11]) {
/*     */       
/*     */       case 0:
/*  73 */         return "BASIC";
/*     */       case 1:
/*  75 */         return "BASIC data";
/*     */       case 2:
/*  77 */         return "Machine language";
/*     */       case 3:
/*  79 */         return "Text";
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  84 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getFileFlag() {
/*  89 */     return this.data[12];
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPrettyFileFlag() {
/*  94 */     String res = "unknown";
/*     */     
/*  96 */     if ((this.data[12] & 0xFF) == 255)
/*  97 */       return "ASCII"; 
/*  98 */     if (this.data[12] == 0) {
/*  99 */       return "Binary";
/*     */     }
/* 101 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getFirstGranule() {
/* 106 */     return this.data[13];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBytesInLastSector() throws DWDECBFileSystemInvalidFATException {
/* 111 */     int res = (0xFF & this.data[14]) * 256 + (0xFF & this.data[15]);
/*     */     
/* 113 */     if (res > 256) {
/* 114 */       throw new DWDECBFileSystemInvalidFATException("file " + getFileName() + "." + getFileExt() + " claims to use " + res + " bytes in last sector?");
/*     */     }
/* 116 */     return res;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDECBFileSystemDirEntry.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */