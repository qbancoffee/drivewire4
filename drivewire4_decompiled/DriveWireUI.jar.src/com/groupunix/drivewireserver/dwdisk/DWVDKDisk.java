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
/*     */ public class DWVDKDisk
/*     */   extends DWDisk
/*     */ {
/*     */   public static final int VDK_HEADER_SIZE_MIN = 12;
/*     */   public static final int VDK_HEADER_SIZE_MAX = 256;
/*     */   public static final int VDK_SECTOR_SIZE = 256;
/*     */   public static final int VDK_SECTORS_PER_TRACK = 18;
/* 123 */   private static final Logger logger = Logger.getLogger("DWServer.DWVDKDisk");
/*     */   
/*     */   private DWVDKDiskHeader header;
/*     */ 
/*     */   
/*     */   public DWVDKDisk(FileObject fileobj) throws IOException, DWImageFormatException {
/* 129 */     super(fileobj);
/*     */     
/* 131 */     setParam("_format", "vdk");
/*     */     
/* 133 */     load();
/*     */     
/* 135 */     logger.debug("New VDK disk for " + fileobj.getName().getURI());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskFormat() {
/* 142 */     return 4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load() throws IOException, DWImageFormatException {
/* 152 */     InputStream fis = this.fileobj.getContent().getInputStream();
/*     */     
/* 154 */     setLastModifiedTime(this.fileobj.getContent().getLastModifiedTime());
/*     */ 
/*     */     
/* 157 */     this.header = readHeader(fis);
/*     */ 
/*     */     
/* 160 */     setParam("writeprotect", Boolean.valueOf(this.header.isWriteProtected()));
/* 161 */     setParam("_tracks", Integer.valueOf(this.header.getTracks()));
/* 162 */     setParam("_sides", Integer.valueOf(this.header.getSides()));
/* 163 */     setParam("_sectors", Integer.valueOf(this.header.getTracks() * this.header.getSides() * 18));
/*     */ 
/*     */     
/* 166 */     if (this.fileobj.getContent().getSize() != (this.params.getInt("_sectors") * 256 + this.header.getHeaderLen()))
/*     */     {
/* 168 */       throw new DWImageFormatException("Invalid VDK image, wrong file size");
/*     */     }
/*     */     
/* 171 */     this.sectors.setSize(this.params.getInt("_sectors"));
/*     */     
/* 173 */     byte[] buf = new byte[256];
/*     */ 
/*     */     
/* 176 */     for (int i = 0; i < this.params.getInt("_sectors"); i++) {
/*     */       
/* 178 */       int readres = 0;
/*     */       
/* 180 */       while (readres < 256) {
/* 181 */         readres += fis.read(buf, readres, 256 - readres);
/*     */       }
/* 183 */       this.sectors.set(i, new DWDiskSector(this, i, 256, false));
/* 184 */       ((DWDiskSector)this.sectors.get(i)).setData(buf, false);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 189 */     fis.close();
/*     */     
/* 191 */     setParam("_filesystem", DWUtils.prettyFileSystem(DWDiskDrives.getDiskFSType(this.sectors)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DWVDKDiskHeader readHeader(InputStream fis) throws IOException, DWImageFormatException {
/* 199 */     int readres = 0;
/* 200 */     byte[] hbuff1 = new byte[4];
/*     */     
/* 202 */     while (readres < 4) {
/* 203 */       readres += fis.read(hbuff1, readres, 4 - readres);
/*     */     }
/*     */     
/* 206 */     int headerlen = getHeaderLen(hbuff1);
/*     */ 
/*     */     
/* 209 */     byte[] hbuff2 = new byte[headerlen];
/* 210 */     System.arraycopy(hbuff1, 0, hbuff2, 0, 4);
/*     */     
/* 212 */     while (readres < headerlen) {
/* 213 */       readres += fis.read(hbuff2, readres, headerlen - readres);
/*     */     }
/* 215 */     return new DWVDKDiskHeader(hbuff2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getHeaderLen(byte[] hbuff) throws DWImageFormatException {
/* 224 */     if (hbuff.length < 4) {
/* 225 */       throw new DWImageFormatException("Invalid VDK header: too short to read size bytes");
/*     */     }
/*     */     
/* 228 */     if ((0xFF & hbuff[0]) != 100 || (0xFF & hbuff[1]) != 107) {
/* 229 */       throw new DWImageFormatException("Invalid VDK header: " + hbuff[0] + " " + hbuff[1]);
/*     */     }
/*     */     
/* 232 */     int len = (0xFF & hbuff[2]) + (0xFF & hbuff[3]) * 256;
/*     */     
/* 234 */     if (len > 256) {
/* 235 */       throw new DWImageFormatException("Invalid VDK header: too big for sanity");
/*     */     }
/*     */     
/* 238 */     return len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void seekSector(int newLSN) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException {
/* 246 */     if (newLSN < 0)
/*     */     {
/* 248 */       throw new DWInvalidSectorException("Sector " + newLSN + " is not valid");
/*     */     }
/* 250 */     if (newLSN > this.sectors.size() - 1)
/*     */     {
/* 252 */       throw new DWSeekPastEndOfDeviceException("Attempt to seek beyond end of image");
/*     */     }
/*     */ 
/*     */     
/* 256 */     setParam("_lsn", Integer.valueOf(newLSN));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSector(byte[] data) throws DWDriveWriteProtectedException, IOException {
/* 265 */     if (getWriteProtect())
/*     */     {
/* 267 */       throw new DWDriveWriteProtectedException("Disk is write protected");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 272 */     ((DWDiskSector)this.sectors.get(getLSN())).setData(data);
/*     */     
/* 274 */     incParam("_writes");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readSector() throws IOException {
/* 283 */     incParam("_reads");
/* 284 */     return ((DWDiskSector)this.sectors.get(getLSN())).getData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int considerImage(byte[] hdr, long fobjsize) {
/* 294 */     if (fobjsize >= 12L) {
/*     */       int hdrlen;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 301 */         hdrlen = getHeaderLen(hdr);
/*     */       }
/* 303 */       catch (DWImageFormatException e) {
/*     */         
/* 305 */         return 0;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 310 */       byte[] buf = new byte[hdrlen];
/* 311 */       System.arraycopy(hdr, 0, buf, 0, hdrlen);
/*     */       
/* 313 */       DWVDKDiskHeader header = new DWVDKDiskHeader(buf);
/*     */ 
/*     */       
/* 316 */       if (fobjsize == (header.getHeaderLen() + header.getSides() * header.getTracks() * 256 * 18))
/*     */       {
/* 318 */         return 2;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 323 */     return 0;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWVDKDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */