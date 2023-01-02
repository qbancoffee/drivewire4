/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.vfs.RandomAccessContent;
/*     */ import org.apache.commons.vfs.util.RandomAccessMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDiskSector
/*     */ {
/*     */   private int LSN;
/*     */   private byte[] data;
/*     */   private boolean dirty = false;
/*     */   private int sectorsize;
/*     */   private DWDisk disk;
/*     */   private boolean direct;
/*     */   private RandomAccessContent raf;
/*     */   
/*     */   public DWDiskSector(DWDisk disk, int lsn, int sectorsize, boolean direct) {
/*  22 */     this.LSN = lsn;
/*  23 */     this.sectorsize = sectorsize;
/*  24 */     this.direct = direct;
/*  25 */     if (!direct) {
/*  26 */       this.data = new byte[sectorsize];
/*     */     }
/*  28 */     this.disk = disk;
/*     */   }
/*     */   
/*     */   public int getLSN() {
/*  32 */     return this.LSN;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setData(byte[] newdata) {
/*  37 */     if (this.direct)
/*     */     {
/*  39 */       this.data = new byte[newdata.length];
/*     */     }
/*     */     
/*  42 */     this.dirty = true;
/*  43 */     System.arraycopy(newdata, 0, this.data, 0, this.sectorsize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setData(byte[] newdata, boolean dirty) {
/*  49 */     this.dirty = dirty;
/*     */     
/*  51 */     if (this.direct) {
/*     */       
/*  53 */       this.data = new byte[newdata.length];
/*  54 */       this.dirty = true;
/*     */     } 
/*     */     
/*  57 */     System.arraycopy(newdata, 0, this.data, 0, this.sectorsize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized byte[] getData() throws IOException {
/*  63 */     if (this.data != null)
/*  64 */       return this.data; 
/*  65 */     if (this.direct)
/*     */     {
/*  67 */       return getFileSector();
/*     */     }
/*     */     
/*  70 */     return new byte[this.sectorsize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void makeClean() {
/*  77 */     if (this.dirty) {
/*     */       
/*  79 */       if (this.direct)
/*     */       {
/*  81 */         this.data = null;
/*     */       }
/*  83 */       this.dirty = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getFileSector() throws IOException {
/*  91 */     this.raf = this.disk.getFileObject().getContent().getRandomAccessContent(RandomAccessMode.READ);
/*  92 */     long pos = (this.LSN * this.sectorsize);
/*  93 */     this.raf.seek(pos);
/*  94 */     byte[] buf = new byte[this.sectorsize];
/*  95 */     this.raf.readFully(buf);
/*  96 */     this.raf.close();
/*  97 */     this.raf = null;
/*  98 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean isDirty() {
/* 105 */     return this.dirty;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDataByte(int i, byte b) throws IOException {
/* 110 */     if (this.data == null)
/*     */     {
/* 112 */       this.data = getFileSector();
/*     */     }
/*     */     
/* 115 */     this.data[i] = b;
/* 116 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeDirty() {
/* 122 */     this.dirty = true;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskSector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */