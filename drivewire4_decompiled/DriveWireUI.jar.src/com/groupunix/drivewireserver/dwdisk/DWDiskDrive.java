/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import java.io.IOException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDiskDrive
/*     */ {
/*  15 */   private static final Logger logger = Logger.getLogger("DWServer.DWDiskDrive");
/*     */   
/*     */   private int driveno;
/*     */   private boolean loaded = false;
/*  19 */   private DWDisk disk = null;
/*     */ 
/*     */   
/*     */   private DWDiskDrives drives;
/*     */ 
/*     */   
/*     */   public DWDiskDrive(DWDiskDrives drives, int driveno) {
/*  26 */     this.drives = drives;
/*  27 */     this.driveno = driveno;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDriveNo() {
/*  34 */     return this.driveno;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoaded() {
/*  40 */     return this.loaded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDisk getDisk() throws DWDriveNotLoadedException {
/*  47 */     if (this.loaded)
/*     */     {
/*  49 */       return this.disk;
/*     */     }
/*     */ 
/*     */     
/*  53 */     throw new DWDriveNotLoadedException("No disk in drive " + getDriveNo());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void eject() throws DWDriveNotLoadedException {
/*  62 */     if (this.disk == null)
/*     */     {
/*  64 */       throw new DWDriveNotLoadedException("There is no disk in drive " + this.driveno);
/*     */     }
/*     */     
/*  67 */     synchronized (this.disk) {
/*     */ 
/*     */       
/*     */       try {
/*  71 */         this.disk.eject();
/*     */       }
/*  73 */       catch (IOException e) {
/*     */         
/*  75 */         logger.warn("Ejecting from drive " + getDriveNo() + ": " + e.getMessage());
/*     */       } 
/*     */       
/*  78 */       this.loaded = false;
/*  79 */       this.disk = null;
/*  80 */       submitEvent("*eject", "");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void insert(DWDisk disk) {
/*  87 */     this.disk = disk;
/*  88 */     this.loaded = true;
/*  89 */     submitEvent("*insert", this.disk.getFilePath());
/*  90 */     this.disk.insert(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int lsn) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveNotLoadedException {
/*  97 */     if (this.disk == null) {
/*  98 */       throw new DWDriveNotLoadedException("No disk in drive " + this.driveno);
/*     */     }
/* 100 */     synchronized (this.disk) {
/*     */       
/* 102 */       this.disk.seekSector(lsn);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException, DWImageFormatException {
/* 110 */     if (this.disk == null) {
/* 111 */       throw new IOException("Disk is null");
/*     */     }
/* 113 */     synchronized (this.disk) {
/*     */       
/* 115 */       return this.disk.readSector();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 122 */     if (this.disk == null) {
/* 123 */       throw new IOException("Disk is null");
/*     */     }
/* 125 */     synchronized (this.disk) {
/*     */       
/* 127 */       this.disk.writeSector(data);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(String key, String val) {
/* 134 */     if (this.drives != null) {
/* 135 */       this.drives.submitEvent(this.driveno, key, val);
/*     */     }
/*     */   }
/*     */   
/*     */   public DWDiskDrives getDiskDrives() {
/* 140 */     return this.drives;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskDrive.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */