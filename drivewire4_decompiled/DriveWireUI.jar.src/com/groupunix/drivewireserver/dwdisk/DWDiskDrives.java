/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveAlreadyLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.commons.vfs.FileContent;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.FileSystemManager;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDiskDrives
/*     */ {
/*     */   private DWDiskDrive[] diskDrives;
/*  39 */   private static final Logger logger = Logger.getLogger("DWServer.DWDiskDrives");
/*     */   
/*     */   private DWProtocolHandler dwProto;
/*     */   
/*  43 */   private int diskDriveSerial = -1;
/*     */   private FileSystemManager fsManager;
/*  45 */   private int hdbdosdrive = 0;
/*     */ 
/*     */   
/*     */   public DWDiskDrives(DWProtocolHandler dwProto) {
/*  49 */     logger.debug("disk drives init for handler #" + dwProto.getHandlerNo());
/*  50 */     this.dwProto = dwProto;
/*  51 */     this.diskDrives = new DWDiskDrive[getMaxDrives()];
/*     */     
/*  53 */     for (int i = 0; i < getMaxDrives(); i++) {
/*     */       
/*  55 */       this.diskDrives[i] = new DWDiskDrive(this, i);
/*     */       
/*  57 */       if (!DriveWireServer.getNoMount() && dwProto.getConfig().getBoolean("RestoreDrivePaths", true) && dwProto.getConfig().getString("Drive" + i + "Path", null) != null) {
/*     */         
/*     */         try {
/*     */           
/*  61 */           logger.debug("Restoring drive " + i + " from " + dwProto.getConfig().getString("Drive" + i + "Path"));
/*  62 */           LoadDiskFromFile(i, dwProto.getConfig().getString("Drive" + i + "Path"));
/*     */         }
/*  64 */         catch (DWDriveNotValidException e) {
/*     */           
/*  66 */           logger.warn("Restoring drive " + i + ": " + e.getMessage());
/*     */         }
/*  68 */         catch (DWDriveAlreadyLoadedException e) {
/*     */           
/*  70 */           logger.warn("Restoring drive " + i + ": " + e.getMessage());
/*     */         }
/*  72 */         catch (IOException e) {
/*     */           
/*  74 */           logger.warn("Restoring drive " + i + ": " + e.getMessage());
/*     */         }
/*  76 */         catch (DWImageFormatException e) {
/*     */           
/*  78 */           logger.warn("Restoring drive " + i + ": " + e.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDisk getDisk(int driveno) throws DWDriveNotLoadedException, DWDriveNotValidException {
/*  90 */     if (!isDriveNo(driveno))
/*     */     {
/*  92 */       throw new DWDriveNotValidException("There is no drive " + driveno + ". Valid drives numbers are 0 - " + (this.dwProto.getConfig().getInt("DiskMaxDrives", 256) - 1));
/*     */     }
/*     */     
/*  95 */     return this.diskDrives[driveno].getDisk();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(int driveno, byte[] data) throws DWDriveNotLoadedException, DWDriveNotValidException, DWDriveWriteProtectedException, IOException {
/* 102 */     if (this.dwProto.getConfig().getBoolean("HDBDOSMode", false))
/*     */     {
/* 104 */       driveno = this.hdbdosdrive;
/*     */     }
/*     */     
/* 107 */     this.diskDrives[driveno].writeSector(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] readSector(int driveno) throws DWDriveNotLoadedException, DWDriveNotValidException, IOException, DWImageFormatException {
/* 112 */     if (this.dwProto.getConfig().getBoolean("HDBDOSMode", false))
/*     */     {
/* 114 */       driveno = this.hdbdosdrive;
/*     */     }
/*     */     
/* 117 */     return this.diskDrives[driveno].readSector();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int driveno, int lsn) throws DWDriveNotLoadedException, DWDriveNotValidException, DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/* 123 */     if (this.dwProto.getConfig().getBoolean("HDBDOSMode", false)) {
/*     */ 
/*     */ 
/*     */       
/* 127 */       int newdriveno = lsn / 630;
/* 128 */       int newlsn = lsn % 630;
/*     */       
/* 130 */       if (lsn != newlsn || driveno != newdriveno)
/*     */       {
/* 132 */         logger.debug("HDBDOSMode maps seek from drv " + driveno + " sector " + lsn + " to drv " + newdriveno + " sector " + newlsn);
/*     */       }
/*     */       
/* 135 */       lsn = newlsn;
/* 136 */       driveno = newdriveno;
/*     */       
/* 138 */       this.hdbdosdrive = newdriveno;
/*     */     } 
/*     */ 
/*     */     
/* 142 */     this.diskDrives[driveno].seekSector(lsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDriveNo(int driveno) {
/* 149 */     if (driveno < 0 || driveno >= getMaxDrives())
/*     */     {
/* 151 */       return false;
/*     */     }
/*     */     
/* 154 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoaded(int driveno) {
/* 161 */     return this.diskDrives[driveno].isLoaded();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReLoadDisk(int driveno) throws DWDriveNotLoadedException, IOException, DWDriveNotValidException, DWImageFormatException {
/* 167 */     getDisk(driveno).reload();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReLoadAllDisks() throws IOException, DWImageFormatException {
/* 173 */     for (int i = 0; i < getMaxDrives(); i++) {
/*     */ 
/*     */       
/*     */       try {
/* 177 */         if (isLoaded(i)) {
/* 178 */           getDisk(i).reload();
/*     */         }
/* 180 */       } catch (DWDriveNotLoadedException e) {
/*     */         
/* 182 */         logger.warn(e.getMessage());
/*     */       }
/* 184 */       catch (DWDriveNotValidException e) {
/*     */         
/* 186 */         logger.warn(e.getMessage());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void EjectDisk(int driveno) throws DWDriveNotValidException, DWDriveNotLoadedException {
/* 194 */     this.diskDrives[driveno].eject();
/*     */     
/* 196 */     if (this.dwProto.getConfig().getBoolean("SaveDrivePaths", true)) {
/* 197 */       this.dwProto.getConfig().setProperty("Drive" + driveno + "Path", null);
/*     */     }
/* 199 */     logger.info("ejected disk from drive " + driveno);
/* 200 */     incDiskDriveSerial();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void EjectAllDisks() {
/* 206 */     for (int i = 0; i < getMaxDrives(); i++) {
/*     */       
/* 208 */       if (isLoaded(i)) {
/*     */         
/*     */         try {
/*     */           
/* 212 */           EjectDisk(i);
/*     */         }
/* 214 */         catch (DWDriveNotValidException e) {
/*     */           
/* 216 */           logger.warn(e.getMessage());
/*     */         }
/* 218 */         catch (DWDriveNotLoadedException e) {
/*     */           
/* 220 */           logger.warn(e.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeDisk(int driveno) throws IOException, DWDriveNotLoadedException, DWDriveNotValidException, DWImageHasNoSourceException {
/* 230 */     getDisk(driveno).write();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeDisk(int driveno, String path) throws IOException, DWDriveNotLoadedException, DWDriveNotValidException {
/* 236 */     getDisk(driveno).writeTo(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DWDisk DiskFromFile(FileObject fileobj) throws DWImageFormatException, IOException {
/* 243 */     FileContent fc = fileobj.getContent();
/* 244 */     long fobjsize = fc.getSize();
/*     */ 
/*     */     
/* 247 */     if (fobjsize > 2147483647L) {
/* 248 */       throw new DWImageFormatException("Image too big, maximum size is 2147483647 bytes.");
/*     */     }
/*     */     
/* 251 */     int hdrsize = (int)Math.min(256L, fobjsize);
/*     */     
/* 253 */     byte[] header = new byte[hdrsize];
/*     */     
/* 255 */     if (hdrsize > 0) {
/*     */       
/* 257 */       int readres = 0;
/* 258 */       InputStream fis = fc.getInputStream();
/*     */       
/* 260 */       while (readres < hdrsize) {
/* 261 */         readres += fis.read(header, readres, hdrsize - readres);
/*     */       }
/* 263 */       fis.close();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 268 */     Hashtable<Integer, Integer> votes = new Hashtable<Integer, Integer>();
/*     */     
/* 270 */     votes.put(Integer.valueOf(2), Integer.valueOf(DWDMKDisk.considerImage(header, fobjsize)));
/* 271 */     votes.put(Integer.valueOf(1), Integer.valueOf(DWRawDisk.considerImage(header, fobjsize)));
/* 272 */     votes.put(Integer.valueOf(4), Integer.valueOf(DWVDKDisk.considerImage(header, fobjsize)));
/* 273 */     votes.put(Integer.valueOf(3), Integer.valueOf(DWJVCDisk.considerImage(header, fobjsize)));
/* 274 */     votes.put(Integer.valueOf(5), Integer.valueOf(DWCCBDisk.considerImage(header, fobjsize)));
/*     */     
/* 276 */     int format = getBestFormat(votes);
/*     */     
/* 278 */     switch (format) {
/*     */       
/*     */       case 2:
/* 281 */         return new DWDMKDisk(fileobj);
/*     */       
/*     */       case 4:
/* 284 */         return new DWVDKDisk(fileobj);
/*     */       
/*     */       case 3:
/* 287 */         return new DWJVCDisk(fileobj);
/*     */       
/*     */       case 5:
/* 290 */         return new DWCCBDisk(fileobj);
/*     */       
/*     */       case 1:
/* 293 */         return new DWRawDisk(fileobj, 256, 16777215);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 299 */     throw new DWImageFormatException("Unsupported image format");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void LoadDiskFromFile(int driveno, String path) throws DWDriveNotValidException, DWDriveAlreadyLoadedException, IOException, DWImageFormatException {
/* 309 */     this.fsManager = VFS.getManager();
/*     */ 
/*     */     
/*     */     try {
/* 313 */       FileObject fileobj = this.fsManager.resolveFile(path);
/*     */       
/* 315 */       if (fileobj.exists() && fileobj.isReadable())
/*     */       {
/* 317 */         LoadDisk(driveno, DiskFromFile(fileobj));
/*     */       
/*     */       }
/*     */       else
/*     */       {
/* 322 */         logger.error("Unreadable path '" + path + "'");
/* 323 */         throw new IOException("Unreadable path");
/*     */       }
/*     */     
/* 326 */     } catch (FileSystemException e) {
/*     */       
/* 328 */       logger.error("FileSystemException: " + e.getMessage());
/* 329 */       throw new IOException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getBestFormat(Hashtable<Integer, Integer> votes) throws DWImageFormatException {
/* 340 */     int res = 0;
/*     */ 
/*     */     
/* 343 */     if (votes.containsValue(Integer.valueOf(2))) {
/*     */       
/* 345 */       if (Collections.frequency(votes.values(), Integer.valueOf(2)) > 1)
/*     */       {
/* 347 */         throw new DWImageFormatException("Multiple formats claim this image?");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 352 */       for (Map.Entry<Integer, Integer> entry : votes.entrySet())
/*     */       {
/* 354 */         if (((Integer)entry.getValue()).equals(Integer.valueOf(2)))
/*     */         {
/* 356 */           return ((Integer)entry.getKey()).intValue();
/*     */         
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 362 */     else if (votes.containsValue(Integer.valueOf(1))) {
/*     */       
/* 364 */       if (Collections.frequency(votes.values(), Integer.valueOf(1)) > 1)
/*     */       {
/*     */         
/* 367 */         throw new DWImageFormatException("Multiple formats might read this image?");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 372 */       for (Map.Entry<Integer, Integer> entry : votes.entrySet()) {
/*     */         
/* 374 */         if (((Integer)entry.getValue()).equals(Integer.valueOf(1)))
/*     */         {
/* 376 */           return ((Integer)entry.getKey()).intValue();
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 383 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void LoadDisk(int driveno, DWDisk disk) throws DWDriveNotValidException, DWDriveAlreadyLoadedException {
/* 390 */     if (isLoaded(driveno)) {
/*     */       
/*     */       try {
/*     */         
/* 394 */         this.diskDrives[driveno].eject();
/*     */       }
/* 396 */       catch (DWDriveNotLoadedException e) {
/*     */         
/* 398 */         logger.warn("Loaded but not loaded.. well what is this about then?");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 403 */     this.diskDrives[driveno].insert(disk);
/*     */     
/* 405 */     if (this.dwProto.getConfig().getBoolean("SaveDrivePaths", true)) {
/* 406 */       this.dwProto.getConfig().setProperty("Drive" + driveno + "Path", disk.getFilePath());
/*     */     }
/* 408 */     logger.info("loaded disk '" + disk.getFilePath() + "' in drive " + driveno);
/* 409 */     incDiskDriveSerial();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] nullSector() {
/* 419 */     byte[] tmp = new byte[this.dwProto.getConfig().getInt("DiskSectorSize", 256)];
/*     */     
/* 421 */     for (int i = 0; i < this.dwProto.getConfig().getInt("DiskSectorSize", 256); i++) {
/* 422 */       tmp[i] = 0;
/*     */     }
/* 424 */     return tmp;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 448 */     logger.debug("shutting down");
/*     */ 
/*     */     
/* 451 */     sync();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() {
/* 459 */     for (int driveno = 0; driveno < getMaxDrives(); driveno++) {
/*     */       
/* 461 */       if (isLoaded(driveno)) {
/*     */         
/*     */         try {
/*     */           
/* 465 */           getDisk(driveno).sync();
/*     */         }
/* 467 */         catch (DWDriveNotLoadedException e) {
/*     */           
/* 469 */           logger.warn(e.getMessage());
/*     */         }
/* 471 */         catch (DWDriveNotValidException e) {
/*     */           
/* 473 */           logger.warn(e.getMessage());
/*     */         }
/* 475 */         catch (IOException e) {
/*     */           
/* 477 */           logger.warn(e.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxDrives() {
/* 487 */     return this.dwProto.getConfig().getInt("DiskMaxDrives", 256);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFreeDriveNo() {
/* 493 */     int res = getMaxDrives() - 1;
/*     */     
/* 495 */     while (isLoaded(res) && res > 0)
/*     */     {
/* 497 */       res--;
/*     */     }
/*     */     
/* 500 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void incDiskDriveSerial() {
/* 507 */     this.diskDriveSerial++;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDiskDriveSerial() {
/* 512 */     return this.diskDriveSerial;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDriveNoFromString(String str) throws DWDriveNotValidException {
/* 518 */     int res = -1;
/*     */ 
/*     */     
/*     */     try {
/* 522 */       res = Integer.parseInt(str);
/*     */     }
/* 524 */     catch (NumberFormatException e) {
/*     */       
/* 526 */       throw new DWDriveNotValidException("Drive numbers must be numeric");
/*     */     } 
/*     */     
/* 529 */     isDriveNo(res);
/*     */     
/* 531 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(int driveno, String key, String val) {
/* 537 */     DriveWireServer.submitDiskEvent(this.dwProto.getHandlerNo(), driveno, key, val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HierarchicalConfiguration getConfig() {
/* 544 */     return this.dwProto.getConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void createDisk(int driveno) throws DWDriveAlreadyLoadedException {
/* 551 */     if (isLoaded(driveno)) {
/* 552 */       throw new DWDriveAlreadyLoadedException("Already a disk in drive " + driveno);
/*     */     }
/* 554 */     this.diskDrives[driveno].insert(new DWRawDisk(256, 16777215));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void formatDOSFS(int driveno) throws DWDriveNotLoadedException, DWDriveNotValidException, DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException {
/* 560 */     if (!isLoaded(driveno)) {
/* 561 */       throw new DWDriveNotLoadedException("No disk in drive " + driveno);
/*     */     }
/* 563 */     DWDECBFileSystem.format(getDisk(driveno));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getDiskFSType(Vector<DWDiskSector> sectors) {
/*     */     try {
/* 573 */       if (!sectors.isEmpty()) {
/*     */ 
/*     */         
/* 576 */         if (((DWDiskSector)sectors.get(0)).getData()[3] == 18 && ((DWDiskSector)sectors.get(0)).getData()[73] == 18 && ((DWDiskSector)sectors.get(0)).getData()[75] == 18)
/*     */         {
/* 578 */           return 0;
/*     */         }
/*     */ 
/*     */         
/* 582 */         byte[] lwfs = new byte[4];
/* 583 */         System.arraycopy(((DWDiskSector)sectors.get(0)).getData(), 0, lwfs, 0, 4);
/*     */         
/* 585 */         if ((new String(lwfs)).equals("LWFS") || (new String(lwfs)).equals("LW16"))
/*     */         {
/* 587 */           return 2;
/*     */         }
/*     */ 
/*     */         
/* 591 */         if (((DWDiskSector)sectors.get(0)).getData()[0] == 102 && ((DWDiskSector)sectors.get(0)).getData()[1] == 99)
/*     */         {
/* 593 */           return 3;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 598 */         if (sectors.size() == 630)
/*     */         {
/* 600 */           DWDECBFileSystem fs = new DWDECBFileSystem(sectors);
/*     */           
/* 602 */           List<DWDECBFileSystemDirEntry> dir = fs.getDirectory();
/*     */ 
/*     */           
/* 605 */           boolean wacky = false;
/* 606 */           for (DWDECBFileSystemDirEntry e : dir) {
/*     */ 
/*     */             
/* 609 */             if (e.getFirstGranule() > 68) {
/*     */ 
/*     */               
/* 612 */               wacky = true;
/*     */ 
/*     */ 
/*     */               
/*     */               continue;
/*     */             } 
/*     */ 
/*     */             
/* 620 */             if (e.getFileFlag() != 0 && e.getFileFlag() != -1)
/*     */             {
/*     */               
/* 623 */               wacky = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 628 */           for (int i = 0; i < 68; i++) {
/*     */             
/* 630 */             int val = 0xFF & ((DWDiskSector)sectors.get(307)).getData()[i];
/*     */             
/* 632 */             if (val > 68 && val < 192)
/*     */             {
/*     */               
/* 635 */               wacky = true;
/*     */             }
/*     */             
/* 638 */             if (val > 207 && val < 255)
/*     */             {
/*     */               
/* 641 */               wacky = true;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 647 */           if (!wacky) {
/* 648 */             return 1;
/*     */           }
/*     */         }
/*     */       
/*     */       } 
/* 653 */     } catch (IOException e) {
/*     */       
/* 655 */       logger.debug("While checking FS type: " + e.getMessage());
/*     */     } 
/*     */     
/* 658 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWProtocolHandler getDWProtocolHandler() {
/* 664 */     return this.dwProto;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nameObjMount(String objname) {
/* 672 */     String fn = getObjPath(objname);
/*     */ 
/*     */     
/*     */     try {
/* 676 */       FileObject fileobj = this.fsManager.resolveFile(fn);
/*     */ 
/*     */       
/* 679 */       for (int i = 0; i < getMaxDrives(); i++) {
/*     */ 
/*     */         
/*     */         try {
/* 683 */           if (this.diskDrives[i] != null && this.diskDrives[i].isLoaded() && (this.diskDrives[i].getDisk().getFilePath().equals(fn) || this.diskDrives[i].getDisk().getFilePath().equals(fileobj.getName().getFriendlyURI()) || this.diskDrives[i].getDisk().getFilePath().equals(fileobj.getName().getURI())))
/*     */           {
/* 685 */             return i;
/*     */           
/*     */           }
/*     */         }
/* 689 */         catch (DWDriveNotLoadedException e) {}
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 695 */       int drv = getFreeDriveNo();
/* 696 */       LoadDiskFromFile(drv, fn);
/* 697 */       return drv;
/*     */     }
/* 699 */     catch (DWDriveNotValidException e) {
/*     */       
/* 701 */       logger.debug("namedobjmount of '" + fn + "' failed with: " + e.getMessage());
/*     */     }
/* 703 */     catch (DWDriveAlreadyLoadedException e) {
/*     */       
/* 705 */       logger.debug("namedobjmount of '" + fn + "' failed with: " + e.getMessage());
/*     */     }
/* 707 */     catch (IOException e) {
/*     */       
/* 709 */       logger.debug("namedobjmount of '" + fn + "' failed with: " + e.getMessage());
/*     */     }
/* 711 */     catch (DWImageFormatException e) {
/*     */       
/* 713 */       logger.debug("namedobjmount of '" + fn + "' failed with: " + e.getMessage());
/*     */     } 
/*     */     
/* 716 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getObjPath(String objname) {
/* 724 */     List<HierarchicalConfiguration> objs = this.dwProto.getConfig().configurationsAt("NamedObject");
/*     */     
/* 726 */     for (Iterator<HierarchicalConfiguration> it = objs.iterator(); it.hasNext(); ) {
/*     */       
/* 728 */       HierarchicalConfiguration obj = it.next();
/*     */       
/* 730 */       if (obj.containsKey("[@name]") && obj.containsKey("[@path]") && obj.getString("[@name]").equals(objname))
/*     */       {
/* 732 */         return obj.getString("[@path]");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 737 */     return this.dwProto.getConfig().getString("NamedObjectDir", System.getProperty("user.dir")) + "/" + objname;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskDrives.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */