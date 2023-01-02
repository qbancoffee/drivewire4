/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemBadFilenameException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFileNotFoundException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFullException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWDECBFileSystem
/*     */ {
/*     */   private Vector<DWDiskSector> sectors;
/*     */   
/*     */   public DWDECBFileSystem(DWDisk disk) {
/*  25 */     if (disk.sectors != null) {
/*  26 */       this.sectors = disk.sectors;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDECBFileSystem(Vector<DWDiskSector> sectors) {
/*  33 */     this.sectors = sectors;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<DWDECBFileSystemDirEntry> getDirectory() throws IOException {
/*  39 */     List<DWDECBFileSystemDirEntry> dir = new ArrayList<DWDECBFileSystemDirEntry>();
/*     */     
/*  41 */     for (int i = 0; i < 9; i++) {
/*     */       
/*  43 */       for (int j = 0; j < 8; j++) {
/*     */         
/*  45 */         byte[] buf = new byte[32];
/*  46 */         System.arraycopy(((DWDiskSector)this.sectors.get(i + 308)).getData(), 32 * j, buf, 0, 32);
/*  47 */         DWDECBFileSystemDirEntry entry = new DWDECBFileSystemDirEntry(buf);
/*  48 */         dir.add(entry);
/*     */       } 
/*     */     } 
/*     */     
/*  52 */     return dir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasFile(String filename) throws IOException {
/*  59 */     for (DWDECBFileSystemDirEntry e : getDirectory()) {
/*     */       
/*  61 */       if ((e.getFileName().trim() + "." + e.getFileExt()).equalsIgnoreCase(filename))
/*     */       {
/*  63 */         return true;
/*     */       }
/*     */     } 
/*     */     
/*  67 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<DWDiskSector> getFileSectors(String filename) throws DWDECBFileSystemFileNotFoundException, DWDECBFileSystemInvalidFATException, IOException {
/*  73 */     return (new DWDECBFileSystemFAT(this.sectors.get(307))).getFileSectors(this.sectors, getDirEntry(filename).getFirstGranule());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDECBFileSystemDirEntry getDirEntry(String filename) throws DWDECBFileSystemFileNotFoundException, IOException {
/*  80 */     for (DWDECBFileSystemDirEntry e : getDirectory()) {
/*     */       
/*  82 */       if ((e.getFileName().trim() + "." + e.getFileExt()).equalsIgnoreCase(filename))
/*     */       {
/*  84 */         return e;
/*     */       }
/*     */     } 
/*     */     
/*  88 */     throw new DWDECBFileSystemFileNotFoundException("File '" + filename + "' not found in DOS directory.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileContents(String filename) throws DWDECBFileSystemFileNotFoundException, DWDECBFileSystemInvalidFATException, IOException {
/*  94 */     String res = "";
/*     */     
/*  96 */     ArrayList<DWDiskSector> sectors = getFileSectors(filename);
/*     */     
/*  98 */     if (sectors != null && sectors.size() > 0)
/*     */     {
/* 100 */       for (int i = 0; i < sectors.size() - 1; i++)
/*     */       {
/* 102 */         res = res + new String(((DWDiskSector)sectors.get(i)).getData());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 108 */     int bl = getDirEntry(filename).getBytesInLastSector();
/*     */     
/* 110 */     if (bl > 0) {
/*     */ 
/*     */       
/* 113 */       byte[] buf = new byte[bl];
/*     */       
/* 115 */       System.arraycopy(((DWDiskSector)sectors.get(sectors.size() - 1)).getData(), 0, buf, 0, bl);
/*     */       
/* 117 */       res = res + new String(buf);
/*     */     } 
/*     */     
/* 120 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFile(String filename, byte[] filecontents) throws DWDECBFileSystemFullException, DWDECBFileSystemBadFilenameException, DWDECBFileSystemFileNotFoundException, DWDECBFileSystemInvalidFATException, IOException {
/* 126 */     DWDECBFileSystemFAT fat = new DWDECBFileSystemFAT(this.sectors.get(307));
/*     */     
/* 128 */     byte firstgran = fat.allocate(filecontents.length);
/*     */ 
/*     */     
/* 131 */     addDirectoryEntry(filename, firstgran, (byte)(filecontents.length % 256));
/*     */ 
/*     */     
/* 134 */     ArrayList<DWDiskSector> sectors = getFileSectors(filename);
/*     */     
/* 136 */     int byteswritten = 0;
/* 137 */     byte[] buf = new byte[256];
/*     */     
/* 139 */     for (DWDiskSector sector : sectors) {
/*     */       
/* 141 */       if (filecontents.length - byteswritten >= 256) {
/*     */         
/* 143 */         System.arraycopy(filecontents, byteswritten, buf, 0, 256);
/* 144 */         byteswritten += 256;
/*     */       }
/*     */       else {
/*     */         
/* 148 */         System.arraycopy(filecontents, byteswritten, buf, 0, filecontents.length - byteswritten);
/*     */         
/* 150 */         for (int i = filecontents.length - byteswritten; i < 256; i++)
/* 151 */           buf[i] = 0; 
/* 152 */         byteswritten += filecontents.length - byteswritten;
/*     */       } 
/*     */       
/* 155 */       sector.setData(buf);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addDirectoryEntry(String filename, byte firstgran, byte leftovers) throws DWDECBFileSystemFullException, DWDECBFileSystemBadFilenameException, IOException {
/* 166 */     List<DWDECBFileSystemDirEntry> dr = getDirectory();
/*     */     
/* 168 */     int dirsize = 0;
/*     */     
/* 170 */     for (DWDECBFileSystemDirEntry d : dr) {
/*     */       
/* 172 */       if (d.isUsed()) {
/* 173 */         dirsize++;
/*     */       }
/*     */     } 
/* 176 */     if (dirsize > 67) {
/* 177 */       throw new DWDECBFileSystemFullException("No free directory entries");
/*     */     }
/* 179 */     byte[] buf = new byte[32];
/* 180 */     byte[] secdata = new byte[256];
/*     */     
/* 182 */     DWDiskSector sec = this.sectors.get(dirsize / 8 + 308);
/*     */     
/* 184 */     secdata = sec.getData();
/*     */     
/* 186 */     String[] fileparts = filename.split("\\.");
/*     */     
/* 188 */     if (fileparts.length != 2) {
/* 189 */       throw new DWDECBFileSystemBadFilenameException("Invalid filename (parts) '" + filename + "' " + fileparts.length);
/*     */     }
/* 191 */     String name = fileparts[0];
/* 192 */     String ext = fileparts[1];
/*     */     
/* 194 */     if (name.length() < 1 || name.length() > 8) {
/* 195 */       throw new DWDECBFileSystemBadFilenameException("Invalid filename (name) '" + filename + "'");
/*     */     }
/* 197 */     if (ext.length() != 3) {
/* 198 */       throw new DWDECBFileSystemBadFilenameException("Invalid filename (ext) '" + filename + "'");
/*     */     }
/* 200 */     while (name.length() < 8) {
/* 201 */       name = name + " ";
/*     */     }
/* 203 */     System.arraycopy(name.getBytes(), 0, buf, 0, 8);
/* 204 */     System.arraycopy(ext.getBytes(), 0, buf, 8, 3);
/*     */ 
/*     */ 
/*     */     
/* 208 */     DWDECBFileSystemDirExtensionMapping mapping = new DWDECBFileSystemDirExtensionMapping(ext, (byte)0, (byte)2);
/*     */     
/* 210 */     if (DriveWireServer.serverconfig.getMaxIndex("DECBExtensionMapping") > -1)
/*     */     {
/* 212 */       for (int i = 0; i <= DriveWireServer.serverconfig.getMaxIndex("DECBExtensionMapping"); i++) {
/*     */         
/* 214 */         String kp = "DECBExtensionMapping(" + i + ")";
/*     */         
/* 216 */         if (DriveWireServer.serverconfig.containsKey(kp + "[@extension]") && DriveWireServer.serverconfig.containsKey(kp + "[@ascii]") && DriveWireServer.serverconfig.containsKey(kp + "[@filetype]"))
/*     */         {
/* 218 */           if (DriveWireServer.serverconfig.getString(kp + "[@extension]").equalsIgnoreCase(ext)) {
/*     */ 
/*     */ 
/*     */             
/* 222 */             mapping.setType(DriveWireServer.serverconfig.getByte(kp + "[@filetype]"));
/*     */             
/* 224 */             if (DriveWireServer.serverconfig.getBoolean(kp + "[@ascii]")) {
/* 225 */               mapping.setFlag((byte)-1);
/*     */             } else {
/* 227 */               mapping.setFlag((byte)0);
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 239 */     buf[11] = mapping.getType();
/* 240 */     buf[12] = mapping.getFlag();
/*     */ 
/*     */ 
/*     */     
/* 244 */     buf[13] = firstgran;
/*     */     
/* 246 */     buf[14] = 0;
/* 247 */     buf[15] = leftovers;
/*     */     
/* 249 */     System.arraycopy(buf, 0, secdata, dirsize % 8 * 32, 32);
/*     */     
/* 251 */     sec.setData(secdata);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void format(DWDisk disk) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException {
/* 259 */     byte[] buf = new byte[256];
/*     */     int i;
/* 261 */     for (i = 0; i < 256; i++) {
/* 262 */       buf[i] = -1;
/*     */     }
/* 264 */     for (i = 0; i < 630; i++) {
/*     */       
/* 266 */       disk.seekSector(i);
/* 267 */       disk.writeSector(buf);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dumpFat() throws IOException {
/* 276 */     DWDECBFileSystemFAT fat = new DWDECBFileSystemFAT(this.sectors.get(307));
/*     */     
/* 278 */     return fat.dumpFat();
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDECBFileSystem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */