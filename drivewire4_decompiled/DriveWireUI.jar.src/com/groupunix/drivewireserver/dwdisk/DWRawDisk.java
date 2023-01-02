/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWDefs;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.vfs.Capability;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.RandomAccessContent;
/*     */ import org.apache.commons.vfs.util.RandomAccessMode;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWRawDisk
/*     */   extends DWDisk
/*     */ {
/*  25 */   private static final Logger logger = Logger.getLogger("DWServer.DWRawDisk");
/*     */ 
/*     */ 
/*     */   
/*     */   public DWRawDisk(FileObject fileobj, int sectorsize, int maxsectors) throws IOException, DWImageFormatException {
/*  30 */     super(fileobj);
/*     */ 
/*     */     
/*  33 */     setParam("syncfrom", DWDefs.DISK_DEFAULT_SYNCFROM);
/*  34 */     setParam("syncto", DWDefs.DISK_DEFAULT_SYNCTO);
/*     */     
/*  36 */     setDefaultOptions(sectorsize, maxsectors);
/*     */     
/*  38 */     load();
/*     */     
/*  40 */     logger.debug("New DWRawDisk for '" + getFilePath() + "'");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWRawDisk(int sectorsize, int maxsectors) {
/*  49 */     setDefaultOptions(sectorsize, maxsectors);
/*     */     
/*  51 */     logger.debug("New DWRawDisk (in memory only)");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setDefaultOptions(int sectorsize, int maxsectors) {
/*  59 */     setParam("_sectorsize", Integer.valueOf(sectorsize));
/*  60 */     setParam("_maxsectors", Integer.valueOf(maxsectors));
/*  61 */     setParam("_format", "raw");
/*     */ 
/*     */     
/*  64 */     setParam("offset", Integer.valueOf(0));
/*  65 */     setParam("offsetdrv", Integer.valueOf(0));
/*  66 */     setParam("sizelimit", Integer.valueOf(-1));
/*  67 */     setParam("expand", DWDefs.DISK_DEFAULT_EXPAND);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskFormat() {
/*  73 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/*  81 */     if (newLSN < 0 || newLSN > getMaxSectors())
/*     */     {
/*  83 */       throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
/*     */     }
/*     */     
/*  86 */     if (newLSN >= getDiskSectors() && !this.params.getBoolean("expand", DWDefs.DISK_DEFAULT_EXPAND).booleanValue())
/*     */     {
/*  88 */       throw new DWSeekPastEndOfDeviceException("Sector " + newLSN + " is beyond end of file, and expansion is not allowed");
/*     */     }
/*  90 */     if (getSizelimit() > -1 && newLSN >= getSizelimit())
/*     */     {
/*  92 */       throw new DWSeekPastEndOfDeviceException("Sector " + newLSN + " is beyond specified sector size limit");
/*     */     }
/*     */ 
/*     */     
/*  96 */     setParam("_lsn", Integer.valueOf(newLSN));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() throws IOException, DWImageFormatException {
/* 107 */     boolean local = false;
/* 108 */     int sector = 0;
/* 109 */     int sectorsize = getSectorSize();
/*     */     
/* 111 */     long filesize = this.fileobj.getContent().getSize();
/*     */     
/* 113 */     if (filesize > 2147483647L || filesize / getSectorSize() > 16777215L) {
/* 114 */       throw new DWImageFormatException("Image file is too large");
/*     */     }
/*     */     
/* 117 */     if (this.fileobj.getName().toString().startsWith("file://")) {
/* 118 */       local = true;
/*     */     }
/* 120 */     if (!local) {
/*     */       
/* 122 */       logger.debug("Caching " + this.fileobj.getName() + " in memory");
/* 123 */       long memfree = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
/* 124 */       if (filesize > memfree)
/*     */       {
/* 126 */         throw new DWImageFormatException("Image file will not fit in memory (" + (memfree / 1024L) + " Kbytes free)");
/*     */       }
/*     */       
/* 129 */       BufferedInputStream fis = new BufferedInputStream(this.fileobj.getContent().getInputStream());
/*     */       
/* 131 */       int readres = 0;
/* 132 */       int bytesRead = 0;
/* 133 */       byte[] buffer = new byte[sectorsize];
/*     */       
/* 135 */       this.sectors.setSize((int)(filesize / sectorsize));
/*     */       
/* 137 */       readres = fis.read(buffer, 0, sectorsize);
/*     */       
/* 139 */       while (readres > -1) {
/*     */ 
/*     */         
/* 142 */         bytesRead += readres;
/*     */         
/* 144 */         if (bytesRead == sectorsize) {
/*     */ 
/*     */           
/* 147 */           this.sectors.set(sector, new DWDiskSector(this, sector, sectorsize, false));
/* 148 */           ((DWDiskSector)this.sectors.get(sector)).setData(buffer, false);
/*     */           
/* 150 */           sector++;
/* 151 */           bytesRead = 0;
/*     */         } 
/*     */         
/* 154 */         readres = fis.read(buffer, bytesRead, sectorsize - bytesRead);
/*     */       } 
/*     */       
/* 157 */       if (bytesRead > 0)
/*     */       {
/*     */         
/* 160 */         throw new DWImageFormatException("Incomplete sector data on sector " + sector);
/*     */       }
/*     */       
/* 163 */       logger.debug("read " + sector + " sectors from '" + this.fileobj.getName() + "'");
/* 164 */       fis.close();
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 169 */       int sz = 0;
/*     */       
/* 171 */       this.sectors.setSize((int)(filesize / sectorsize));
/*     */       
/* 173 */       while (sz < filesize) {
/*     */         
/* 175 */         this.sectors.set(sector, new DWDiskSector(this, sector, sectorsize, true));
/* 176 */         sector++;
/* 177 */         sz += sectorsize;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 182 */     long lastmodtime = -1L;
/*     */ 
/*     */     
/*     */     try {
/* 186 */       lastmodtime = this.fileobj.getContent().getLastModifiedTime();
/*     */     }
/* 188 */     catch (FileSystemException e) {
/*     */       
/* 190 */       logger.warn(e.getMessage());
/*     */     } 
/*     */     
/* 193 */     setLastModifiedTime(lastmodtime);
/*     */     
/* 195 */     setParam("_sectors", Integer.valueOf(sector));
/*     */     
/* 197 */     setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException, DWImageFormatException {
/* 209 */     incParam("_reads");
/*     */ 
/*     */ 
/*     */     
/* 213 */     if (isSyncFrom() && this.fileobj != null)
/*     */     {
/* 215 */       if (this.fileobj.getContent().getLastModifiedTime() != getLastModifiedTime())
/*     */       {
/*     */         
/* 218 */         if (getDirtySectors() > 0) {
/*     */ 
/*     */           
/* 221 */           logger.warn("Sync conflict on " + getFilePath() + ", both the source and our cached image have changed.  Source will be overwritten!");
/*     */           
/*     */           try {
/* 224 */             write();
/*     */           }
/* 226 */           catch (DWImageHasNoSourceException e) {}
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 233 */           logger.info("Disk source " + getFilePath() + " has changed, reloading");
/* 234 */           reload();
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 240 */     int effLSN = getLSN() + getOffset();
/*     */ 
/*     */     
/* 243 */     if (effLSN >= this.sectors.size() || this.sectors.get(effLSN) == null) {
/*     */       
/* 245 */       logger.debug("request for undefined sector, effLSN: " + effLSN + "  rawLSN: " + getLSN() + "  curSize: " + (this.sectors.size() - 1));
/*     */ 
/*     */       
/* 248 */       return new byte[getSectorSize()];
/*     */     } 
/*     */ 
/*     */     
/* 252 */     return ((DWDiskSector)this.sectors.get(effLSN)).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void expandDisk(int target) {
/* 261 */     int start = this.sectors.size();
/* 262 */     int sectorsize = getSectorSize();
/* 263 */     DWDisk disk = this;
/*     */     
/* 265 */     this.sectors.setSize(target);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 292 */     setParam("_sectors", Integer.valueOf(target + 1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 300 */     if (getWriteProtect())
/*     */     {
/* 302 */       throw new DWDriveWriteProtectedException("Disk is write protected");
/*     */     }
/*     */ 
/*     */     
/* 306 */     int effLSN = getLSN() + getOffset();
/*     */ 
/*     */     
/* 309 */     if (effLSN >= this.sectors.size()) {
/*     */ 
/*     */       
/* 312 */       expandDisk(effLSN);
/* 313 */       this.sectors.add(effLSN, new DWDiskSector(this, effLSN, getSectorSize(), false));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 318 */     if (this.sectors.get(effLSN) == null) {
/* 319 */       this.sectors.set(effLSN, new DWDiskSector(this, effLSN, getSectorSize(), false));
/*     */     }
/* 321 */     ((DWDiskSector)this.sectors.get(effLSN)).setData(data);
/*     */     
/* 323 */     incParam("_writes");
/*     */   }
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
/*     */   public void write() throws IOException, DWImageHasNoSourceException {
/* 343 */     if (this.fileobj == null)
/*     */     {
/* 345 */       throw new DWImageHasNoSourceException("The image has no source object, must specify write path.");
/*     */     }
/*     */     
/* 348 */     if (this.fileobj.isWriteable()) {
/*     */       
/* 350 */       if (this.fileobj.getFileSystem().hasCapability(Capability.RANDOM_ACCESS_WRITE))
/*     */       {
/*     */         
/* 353 */         syncSectors();
/*     */       }
/* 355 */       else if (this.fileobj.getFileSystem().hasCapability(Capability.WRITE_CONTENT))
/*     */       {
/*     */         
/* 358 */         writeSectors(this.fileobj);
/*     */       
/*     */       }
/*     */       else
/*     */       {
/* 363 */         throw new FileSystemException("Filesystem is unwriteable");
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 368 */       throw new FileSystemException("File is unwriteable");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void syncSectors() {
/* 378 */     long sectorswritten = 0L;
/* 379 */     long starttime = System.currentTimeMillis();
/* 380 */     long sleeptime = 0L;
/*     */ 
/*     */     
/*     */     try {
/* 384 */       RandomAccessContent raf = this.fileobj.getContent().getRandomAccessContent(RandomAccessMode.READWRITE);
/*     */       
/* 386 */       for (int i = 0; i < this.sectors.size(); i++) {
/*     */         
/* 388 */         if (getSector(i) != null)
/*     */         {
/* 390 */           if (getSector(i).isDirty()) {
/*     */             
/* 392 */             if (this.drive.getDiskDrives().getDWProtocolHandler().isInOp()) {
/*     */               
/*     */               try {
/*     */                 
/* 396 */                 long sleepstart = System.currentTimeMillis();
/* 397 */                 Thread.sleep(40L);
/* 398 */                 sleeptime += System.currentTimeMillis() - sleepstart;
/*     */               }
/* 400 */               catch (InterruptedException e) {
/*     */ 
/*     */                 
/* 403 */                 e.printStackTrace();
/*     */               } 
/*     */             }
/*     */             
/* 407 */             long pos = (i * getSectorSize());
/* 408 */             raf.seek(pos);
/* 409 */             raf.write(getSector(i).getData());
/* 410 */             sectorswritten++;
/* 411 */             getSector(i).makeClean();
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 417 */       raf.close();
/* 418 */       this.fileobj.close();
/* 419 */       setLastModifiedTime(this.fileobj.getContent().getLastModifiedTime());
/*     */     }
/* 421 */     catch (IOException e) {
/*     */       
/* 423 */       logger.error("Error writing sectors in " + getFilePath() + ": " + e.getMessage());
/*     */     } 
/*     */     
/* 426 */     if (sectorswritten > 0L) {
/* 427 */       logger.debug("wrote " + sectorswritten + " sectors in " + (System.currentTimeMillis() - starttime) + " ms (" + sleeptime + "ms sleep), to " + getFilePath());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getMaxSectors() {
/* 436 */     return this.params.getInt("_maxsectors", 16777215);
/*     */   }
/*     */ 
/*     */   
/*     */   private int getSectorSize() {
/* 441 */     return this.params.getInt("_sectorsize", 256);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isSyncFrom() {
/* 447 */     return this.params.getBoolean("syncfrom", DWDefs.DISK_DEFAULT_SYNCFROM).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isSyncTo() {
/* 452 */     return this.params.getBoolean("syncto", DWDefs.DISK_DEFAULT_SYNCTO).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getOffset() {
/* 459 */     return this.params.getInt("offset", 0) + this.params.getInt("offsetdrv", 0) * 630;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getSizelimit() {
/* 466 */     return this.params.getInt("sizelimit", -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() throws IOException {
/* 474 */     if (isSyncTo() && 
/* 475 */       this.fileobj != null && 
/* 476 */       getDirtySectors() > 0) {
/*     */       
/*     */       try {
/* 479 */         write();
/*     */       }
/* 481 */       catch (DWImageHasNoSourceException e) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int considerImage(byte[] header, long fobjsize) {
/* 492 */     if (fobjsize % 256L == 0L) {
/*     */ 
/*     */ 
/*     */       
/* 496 */       if (fobjsize > 3L)
/*     */       {
/*     */         
/* 499 */         if (fobjsize == (((0xFF & header[0]) * 65535 + (0xFF & header[1]) * 256 + (0xFF & header[2])) * 256))
/*     */         {
/*     */           
/* 502 */           return 2;
/*     */         }
/*     */       }
/*     */ 
/*     */       
/* 507 */       return 1;
/*     */     } 
/*     */ 
/*     */     
/* 511 */     return 0;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWRawDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */