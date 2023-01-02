/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWCCBDisk
/*     */   extends DWDisk
/*     */ {
/*  18 */   private static final Logger logger = Logger.getLogger("DWServer.DWCCBDisk");
/*     */   
/*     */   private static final long CCB_FILE_SIZE_MIN = 3L;
/*     */   
/*     */   public DWCCBDisk(FileObject fileobj) throws IOException, DWImageFormatException {
/*  23 */     super(fileobj);
/*     */     
/*  25 */     setParam("_format", "ccb");
/*     */     
/*  27 */     load();
/*     */     
/*  29 */     logger.debug("New CCB disk for " + fileobj.getName().getURI());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskFormat() {
/*  36 */     return 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() throws IOException, DWImageFormatException {
/*  46 */     InputStream fis = this.fileobj.getContent().getInputStream();
/*     */     
/*  48 */     long fobjsize = this.fileobj.getContent().getSize();
/*     */     
/*  50 */     if (fobjsize > 2147483647L) {
/*  51 */       throw new DWImageFormatException("Image is too large.");
/*     */     }
/*  53 */     this.sectors.setSize((int)(fobjsize / 256L + 1L));
/*     */     
/*  55 */     byte[] buf = new byte[256];
/*  56 */     int readres = 0;
/*  57 */     int secres = 0;
/*  58 */     int sec = 0;
/*     */     
/*  60 */     while (readres > -1) {
/*     */       
/*  62 */       int sz = (int)Math.min((256 - secres), fobjsize - (sec * 256));
/*     */       
/*  64 */       readres = fis.read(buf, readres, sz);
/*     */ 
/*     */       
/*  67 */       if (readres == -1) {
/*     */         
/*  69 */         this.sectors.set(sec, new DWDiskSector(this, sec, 256, false));
/*  70 */         for (int i = secres; i < 256; i++)
/*  71 */           buf[i] = 0; 
/*  72 */         ((DWDiskSector)this.sectors.get(sec)).setData(buf, false);
/*  73 */         sec++;
/*     */         
/*     */         continue;
/*     */       } 
/*  77 */       secres += readres;
/*  78 */       readres = 0;
/*     */       
/*  80 */       if (secres == 256) {
/*     */         
/*  82 */         this.sectors.set(sec, new DWDiskSector(this, sec, 256, false));
/*  83 */         ((DWDiskSector)this.sectors.get(sec)).setData(buf, false);
/*  84 */         secres = 0;
/*     */         
/*  86 */         sec++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     setParam("_sectors", Integer.valueOf(sec));
/*  94 */     setParam("_filesystem", DWUtils.prettyFileSystem(3));
/*     */     
/*  96 */     fis.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/* 106 */     if (newLSN < 0)
/*     */     {
/* 108 */       throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
/*     */     }
/* 110 */     if (newLSN > this.sectors.size() - 1)
/*     */     {
/* 112 */       throw new DWSeekPastEndOfDeviceException("Attempt to seek beyond end of image");
/*     */     }
/*     */ 
/*     */     
/* 116 */     setParam("_lsn", Integer.valueOf(newLSN));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 125 */     if (getWriteProtect())
/*     */     {
/* 127 */       throw new DWDriveWriteProtectedException("Disk is write protected");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 132 */     ((DWDiskSector)this.sectors.get(getLSN())).setData(data);
/*     */     
/* 134 */     incParam("_writes");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException {
/* 143 */     incParam("_reads");
/* 144 */     return ((DWDiskSector)this.sectors.get(getLSN())).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int considerImage(byte[] hdr, long fobjsize) {
/* 154 */     if (fobjsize >= 3L)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 159 */       if (hdr[0] == 99 && hdr[1] == 99 && hdr[2] == 98) {
/*     */ 
/*     */         
/* 162 */         if (fobjsize % 256L == 0L)
/*     */         {
/* 164 */           return 2;
/*     */         }
/*     */         
/* 167 */         return 1;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 174 */     return 0;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWCCBDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */