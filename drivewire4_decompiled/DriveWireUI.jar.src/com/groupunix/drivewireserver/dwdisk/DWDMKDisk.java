/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDMKDisk
/*     */   extends DWDisk
/*     */ {
/*  19 */   private static final Logger logger = Logger.getLogger("DWServer.DWDMKDisk");
/*     */   
/*  21 */   private ArrayList<DWDMKDiskTrack> tracks = new ArrayList<DWDMKDiskTrack>();
/*     */   
/*     */   private DWDMKDiskHeader header;
/*     */ 
/*     */   
/*     */   public DWDMKDisk(FileObject fileobj) throws IOException, DWImageFormatException {
/*  27 */     super(fileobj);
/*  28 */     setParam("_format", "dmk");
/*     */     
/*  30 */     load();
/*     */     
/*  32 */     logger.debug("New DMK disk for " + fileobj.getName().getURI());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskFormat() {
/*  39 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() throws IOException, DWImageFormatException {
/*  49 */     InputStream fis = this.fileobj.getContent().getInputStream();
/*     */ 
/*     */     
/*  52 */     int readres = 0;
/*  53 */     byte[] hbuff = new byte[16];
/*     */     
/*  55 */     while (readres < 16) {
/*  56 */       readres += fis.read(hbuff, readres, 16 - readres);
/*     */     }
/*  58 */     this.header = new DWDMKDiskHeader(hbuff);
/*     */     
/*  60 */     setParam("writeprotect", Boolean.valueOf(this.header.isWriteProtected()));
/*  61 */     setParam("_tracks", Integer.valueOf(this.header.getTracks()));
/*  62 */     setParam("_sides", Integer.valueOf(this.header.getSides()));
/*  63 */     setParam("_density", this.header.getDensity());
/*     */ 
/*     */     
/*  66 */     if (!this.header.isSingleSided() || this.header.isSingleDensity()) {
/*     */       
/*  68 */       String format = "";
/*     */       
/*  70 */       if (this.header.isSingleSided()) {
/*  71 */         format = format + "SS";
/*     */       } else {
/*  73 */         format = format + "DS";
/*     */       } 
/*     */       
/*  76 */       if (this.header.isSingleDensity()) {
/*  77 */         format = format + "SD";
/*     */       } else {
/*  79 */         format = format + "DD";
/*     */       } 
/*     */       
/*  82 */       fis.close();
/*  83 */       throw new DWImageFormatException("Unsupported DMK format " + format + ", only SSDD is supported at this time");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     for (int i = 0; i < this.header.getTracks(); i++) {
/*     */ 
/*     */ 
/*     */       
/*  93 */       byte[] tbuf = new byte[this.header.getTrackLength()];
/*  94 */       readres = 0;
/*     */       
/*  96 */       while (readres < this.header.getTrackLength() && readres > -1) {
/*     */         
/*  98 */         int res = fis.read(tbuf, readres, this.header.getTrackLength() - readres);
/*  99 */         if (res > -1) {
/* 100 */           readres += res; continue;
/*     */         } 
/* 102 */         readres = -1;
/*     */       } 
/*     */       
/* 105 */       if (readres == -1) {
/*     */         
/* 107 */         fis.close();
/* 108 */         throw new DWImageFormatException("DMK format appears corrupt, incomplete data for track " + i);
/*     */       } 
/*     */       
/* 111 */       DWDMKDiskTrack dmktrack = new DWDMKDiskTrack(tbuf);
/*     */       
/* 113 */       if (dmktrack.getNumSectors() != 18) {
/*     */         
/* 115 */         fis.close();
/* 116 */         throw new DWImageFormatException("Unsupported DMK format, only 18 sectors per track is supported at this time");
/*     */       } 
/*     */       
/* 119 */       this.tracks.add(dmktrack);
/*     */     } 
/*     */     
/* 122 */     fis.close();
/*     */ 
/*     */     
/* 125 */     loadSectors();
/*     */     
/* 127 */     setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadSectors() throws DWImageFormatException {
/* 134 */     this.sectors.clear();
/*     */     
/* 136 */     this.sectors.setSize(this.header.getTracks() * 18);
/* 137 */     setParam("_sectors", Integer.valueOf(this.header.getTracks() * 18));
/*     */     
/* 139 */     for (int t = 0; t < this.header.getTracks(); t++) {
/*     */       
/* 141 */       int spt = 0;
/*     */ 
/*     */       
/* 144 */       for (int i = 0; i < 64; i++) {
/*     */         
/* 146 */         DWDMKDiskIDAM idam = ((DWDMKDiskTrack)this.tracks.get(t)).getIDAM(i);
/*     */ 
/*     */         
/* 149 */         if (idam.getPtr() != 0) {
/*     */           
/* 151 */           if (idam.getTrack() != t)
/*     */           {
/* 153 */             System.out.println("mismatch track in IDAM?");
/*     */           }
/*     */           
/* 156 */           spt++;
/*     */           
/* 158 */           addSectorFrom(idam, t);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addSectorFrom(DWDMKDiskIDAM idam, int track) throws DWImageFormatException {
/* 169 */     int lsn = calcLSN(idam);
/*     */     
/* 171 */     if (lsn > -1 && lsn < this.sectors.size()) {
/*     */       
/* 173 */       this.sectors.set(lsn, new DWDiskSector(this, lsn, idam.getSectorSize(), false));
/* 174 */       ((DWDiskSector)this.sectors.get(lsn)).setData(getSectorDataFrom(idam, track), false);
/*     */     }
/*     */     else {
/*     */       
/* 178 */       throw new DWImageFormatException("Invalid LSN " + lsn + " while adding sector from DMK!");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] getSectorDataFrom(DWDMKDiskIDAM idam, int track) throws DWImageFormatException {
/* 186 */     byte[] buf = new byte[idam.getSectorSize()];
/* 187 */     int loc = idam.getPtr() + 7;
/* 188 */     int gap = 43;
/*     */ 
/*     */     
/* 191 */     boolean sync = false;
/*     */     
/* 193 */     if (this.header.isSingleDensity()) {
/* 194 */       gap = 30;
/*     */     }
/* 196 */     while (gap > 0) {
/*     */       
/* 198 */       if ((0xFF & ((DWDMKDiskTrack)this.tracks.get(track)).getData()[loc]) >= 248 && (0xFF & ((DWDMKDiskTrack)this.tracks.get(track)).getData()[loc]) <= 251)
/*     */       {
/* 200 */         if (this.header.isSingleDensity() || sync) {
/*     */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */       
/* 206 */       if (!this.header.isSingleDensity())
/*     */       {
/* 208 */         if ((0xFF & ((DWDMKDiskTrack)this.tracks.get(track)).getData()[loc]) == 161) {
/*     */           
/* 210 */           sync = true;
/*     */         }
/*     */         else {
/*     */           
/* 214 */           sync = false;
/*     */         } 
/*     */       }
/*     */       
/* 218 */       loc++;
/* 219 */       gap--;
/*     */     } 
/*     */     
/* 222 */     if (gap > 0) {
/*     */ 
/*     */       
/* 225 */       System.arraycopy(((DWDMKDiskTrack)this.tracks.get(track)).getData(), loc + 1, buf, 0, idam.getSectorSize());
/*     */     }
/*     */     else {
/*     */       
/* 229 */       throw new DWImageFormatException("Sector data missing for track " + track + " sector " + idam.getSector());
/*     */     } 
/*     */     
/* 232 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int calcLSN(DWDMKDiskIDAM idam) {
/* 239 */     int t = idam.getTrack() * 18;
/*     */     
/* 241 */     if (!this.header.isSingleSided()) {
/* 242 */       t *= 2;
/*     */     }
/* 244 */     t += idam.getSector() - 1 + 18 * idam.getSide();
/*     */     
/* 246 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/* 255 */     if (newLSN < 0)
/*     */     {
/* 257 */       throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
/*     */     }
/* 259 */     if (newLSN > this.sectors.size() - 1)
/*     */     {
/* 261 */       throw new DWSeekPastEndOfDeviceException("Attempt to seek beyond end of image");
/*     */     }
/*     */ 
/*     */     
/* 265 */     setParam("_lsn", Integer.valueOf(newLSN));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 274 */     if (getWriteProtect())
/*     */     {
/* 276 */       throw new DWDriveWriteProtectedException("Disk is write protected");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 281 */     ((DWDiskSector)this.sectors.get(getLSN())).setData(data);
/*     */     
/* 283 */     incParam("_writes");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException {
/* 293 */     incParam("_reads");
/* 294 */     return ((DWDiskSector)this.sectors.get(getLSN())).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int considerImage(byte[] hdr, long fobjsize) {
/* 305 */     if (fobjsize > 16L) {
/*     */ 
/*     */ 
/*     */       
/* 309 */       DWDMKDiskHeader header = new DWDMKDiskHeader(hdr);
/*     */ 
/*     */       
/* 312 */       if (fobjsize == (16 + header.getSides() * header.getTracks() * header.getTrackLength()))
/*     */       {
/*     */         
/* 315 */         return 2;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 320 */     return 0;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDMKDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */