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
/*     */ public class DWJVCDisk
/*     */   extends DWDisk
/*     */ {
/*  18 */   private static final Logger logger = Logger.getLogger("DWServer.DWJVCDisk");
/*     */   
/*     */   private DWJVCDiskHeader header;
/*     */ 
/*     */   
/*     */   public DWJVCDisk(FileObject fileobj) throws IOException, DWImageFormatException {
/*  24 */     super(fileobj);
/*     */     
/*  26 */     setParam("_format", "jvc");
/*     */     
/*  28 */     load();
/*     */     
/*  30 */     logger.debug("New JVC disk for " + fileobj.getName().getURI());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskFormat() {
/*  37 */     return 3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() throws IOException, DWImageFormatException {
/*  47 */     InputStream fis = this.fileobj.getContent().getInputStream();
/*     */     
/*  49 */     this.header = new DWJVCDiskHeader();
/*     */     
/*  51 */     int filelen = (int)this.fileobj.getContent().getSize();
/*  52 */     int headerlen = filelen % 256;
/*     */     
/*  54 */     if (headerlen > 0) {
/*     */       
/*  56 */       int readres = 0;
/*  57 */       byte[] arrayOfByte = new byte[headerlen];
/*     */       
/*  59 */       while (readres < headerlen) {
/*  60 */         readres += fis.read(arrayOfByte, readres, headerlen - readres);
/*     */       }
/*  62 */       this.header.setData(arrayOfByte);
/*     */     } 
/*     */     
/*  65 */     if (this.header.getSectorAttributes() > 0)
/*     */     {
/*  67 */       throw new DWImageFormatException("JVC with sector attributes not supported");
/*     */     }
/*     */     
/*  70 */     setParam("_secpertrack", Integer.valueOf(this.header.getSectorsPerTrack()));
/*  71 */     setParam("_sides", Integer.valueOf(this.header.getSides()));
/*  72 */     setParam("_sectorsize", Integer.valueOf(this.header.getSectorSize()));
/*  73 */     setParam("_firstsector", Integer.valueOf(this.header.getFirstSector()));
/*     */     
/*  75 */     int tracks = (filelen - headerlen) / this.header.getSectorsPerTrack() * this.header.getSectorSize() / this.header.getSides();
/*  76 */     setParam("_tracks", Integer.valueOf(tracks));
/*     */     
/*  78 */     setParam("_sectors", Integer.valueOf(tracks * this.header.getSides() * this.header.getSectorsPerTrack()));
/*     */ 
/*     */     
/*  81 */     this.sectors.setSize(this.params.getInt("_sectors"));
/*     */     
/*  83 */     byte[] buf = new byte[this.header.getSectorSize()];
/*     */ 
/*     */     
/*  86 */     for (int i = 0; i < this.params.getInt("_sectors"); i++) {
/*     */       
/*  88 */       int readres = 0;
/*     */       
/*  90 */       while (readres < this.header.getSectorSize()) {
/*  91 */         readres += fis.read(buf, readres, this.header.getSectorSize() - readres);
/*     */       }
/*  93 */       this.sectors.set(i, new DWDiskSector(this, i, this.header.getSectorSize(), false));
/*  94 */       ((DWDiskSector)this.sectors.get(i)).setData(buf, false);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  99 */     fis.close();
/*     */     
/* 101 */     setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
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
/*     */   public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/* 113 */     if (newLSN < 0)
/*     */     {
/* 115 */       throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
/*     */     }
/* 117 */     if (newLSN > this.sectors.size() - 1)
/*     */     {
/* 119 */       throw new DWSeekPastEndOfDeviceException("Attempt to seek beyond end of image");
/*     */     }
/*     */ 
/*     */     
/* 123 */     setParam("_lsn", Integer.valueOf(newLSN));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 132 */     if (getWriteProtect())
/*     */     {
/* 134 */       throw new DWDriveWriteProtectedException("Disk is write protected");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 139 */     ((DWDiskSector)this.sectors.get(getLSN())).setData(data);
/*     */     
/* 141 */     incParam("_writes");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException {
/* 150 */     incParam("_reads");
/* 151 */     return ((DWDiskSector)this.sectors.get(getLSN())).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int considerImage(byte[] hdr, long fobjsize) {
/* 161 */     DWJVCDiskHeader header = new DWJVCDiskHeader();
/*     */     
/* 163 */     int headerlen = (int)(fobjsize % 256L);
/*     */ 
/*     */     
/* 166 */     if (headerlen > 0) {
/*     */       
/* 168 */       byte[] buf = new byte[headerlen];
/* 169 */       System.arraycopy(hdr, 0, buf, 0, headerlen);
/* 170 */       header.setData(buf);
/*     */ 
/*     */       
/* 173 */       if (header.getSectorAttributes() == 0)
/*     */       {
/*     */         
/* 176 */         if (header.getSectorSize() > 127 && fobjsize >= header.getSectorSize())
/*     */         {
/* 178 */           if ((fobjsize - headerlen) % header.getSectorSize() == 0L) {
/* 179 */             return 1;
/*     */           }
/*     */         }
/*     */       }
/*     */     } 
/* 184 */     return 0;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWJVCDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */