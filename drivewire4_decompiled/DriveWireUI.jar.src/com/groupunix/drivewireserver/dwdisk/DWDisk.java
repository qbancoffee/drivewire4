/*     */ package com.groupunix.drivewireserver.dwdisk;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DWDefs;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWDriveWriteProtectedException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWImageHasNoSourceException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWInvalidSectorException;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWSeekPastEndOfDeviceException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.apache.commons.configuration.event.ConfigurationListener;
/*     */ import org.apache.commons.vfs.Capability;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DWDisk
/*     */ {
/*  25 */   private static final Logger logger = Logger.getLogger("DWServer.DWDisk");
/*     */   
/*     */   protected HierarchicalConfiguration params;
/*  28 */   protected Vector<DWDiskSector> sectors = new Vector<DWDiskSector>();
/*  29 */   protected FileObject fileobj = null;
/*     */   
/*     */   protected DWDiskConfigListener configlistener;
/*     */   
/*     */   protected DWDiskDrive drive;
/*     */ 
/*     */   
/*     */   public abstract void seekSector(int paramInt) throws DWInvalidSectorException, DWSeekPastEndOfDeviceException;
/*     */ 
/*     */   
/*     */   public abstract void writeSector(byte[] paramArrayOfbyte) throws DWDriveWriteProtectedException, IOException;
/*     */   
/*     */   public abstract byte[] readSector() throws IOException, DWImageFormatException;
/*     */   
/*     */   protected abstract void load() throws IOException, DWImageFormatException;
/*     */   
/*     */   public abstract int getDiskFormat();
/*     */   
/*     */   public DWDisk(FileObject fileobj) throws IOException, DWImageFormatException {
/*  48 */     this.fileobj = fileobj;
/*     */     
/*  50 */     this.params = new HierarchicalConfiguration();
/*     */ 
/*     */     
/*  53 */     setParam("_path", fileobj.getName().getURI());
/*     */     
/*  55 */     long lastmodtime = -1L;
/*     */ 
/*     */     
/*     */     try {
/*  59 */       lastmodtime = this.fileobj.getContent().getLastModifiedTime();
/*     */     }
/*  61 */     catch (FileSystemException e) {
/*     */       
/*  63 */       logger.warn(e.getMessage());
/*     */     } 
/*     */     
/*  66 */     setLastModifiedTime(lastmodtime);
/*     */     
/*  68 */     setParam("_reads", Integer.valueOf(0));
/*  69 */     setParam("_writes", Integer.valueOf(0));
/*  70 */     setParam("_lsn", Integer.valueOf(0));
/*     */ 
/*     */ 
/*     */     
/*  74 */     setParam("writeprotect", DWDefs.DISK_DEFAULT_WRITEPROTECT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDisk() {
/*  82 */     this.fileobj = null;
/*     */     
/*  84 */     this.params = new HierarchicalConfiguration();
/*     */ 
/*     */     
/*  87 */     setParam("_path", "");
/*  88 */     setParam("_reads", Integer.valueOf(0));
/*  89 */     setParam("_writes", Integer.valueOf(0));
/*  90 */     setParam("_lsn", Integer.valueOf(0));
/*     */ 
/*     */ 
/*     */     
/*  94 */     setParam("writeprotect", DWDefs.DISK_DEFAULT_WRITEPROTECT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HierarchicalConfiguration getParams() {
/* 101 */     return this.params;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParam(String key, Object val) {
/* 107 */     if (val == null) {
/*     */       
/* 109 */       this.params.clearProperty(key);
/*     */     }
/*     */     else {
/*     */       
/* 113 */       this.params.setProperty(key, val);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void incParam(String key) {
/* 119 */     setParam(key, Integer.valueOf(this.params.getInt(key, 0) + 1));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilePath() {
/* 125 */     if (this.fileobj != null) {
/* 126 */       return this.fileobj.getName().getURI();
/*     */     }
/* 128 */     return "(in memory only)";
/*     */   }
/*     */ 
/*     */   
/*     */   public FileObject getFileObject() {
/* 133 */     return this.fileobj;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastModifiedTime(long lastModifiedTime) {
/* 139 */     this.params.setProperty("_last_modified", Long.valueOf(lastModifiedTime));
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModifiedTime() {
/* 144 */     return this.params.getLong("_last_modified", 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLSN() {
/* 149 */     return this.params.getInt("_lsn", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiskSectors() {
/* 157 */     return this.sectors.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reload() throws IOException, DWImageFormatException {
/* 166 */     if (getFileObject() != null) {
/*     */       
/* 168 */       logger.debug("reloading disk sectors from " + getFilePath());
/*     */       
/* 170 */       this.sectors.clear();
/*     */ 
/*     */       
/* 173 */       load();
/*     */     }
/*     */     else {
/*     */       
/* 177 */       throw new DWImageFormatException("Image is in memory only, so cannot reload.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void eject() throws IOException {
/* 184 */     sync();
/* 185 */     this.sectors = null;
/*     */     
/* 187 */     if (this.fileobj != null) {
/*     */       
/* 189 */       this.fileobj.close();
/* 190 */       this.fileobj = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write() throws IOException, DWImageHasNoSourceException {
/* 203 */     throw new IOException("Image is read only");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(String path) throws IOException {
/* 212 */     FileObject altobj = VFS.getManager().resolveFile(path);
/*     */     
/* 214 */     if (altobj.isWriteable()) {
/*     */       
/* 216 */       if (altobj.getFileSystem().hasCapability(Capability.WRITE_CONTENT))
/*     */       {
/*     */         
/* 219 */         writeSectors(altobj);
/*     */       
/*     */       }
/*     */       else
/*     */       {
/* 224 */         logger.warn("Filesystem is unwritable for path '" + altobj.getName() + "'");
/* 225 */         throw new FileSystemException("Filesystem is unwriteable");
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 230 */       logger.warn("File is unwriteable for path '" + altobj.getName() + "'");
/* 231 */       throw new IOException("File is unwriteable");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeSectors(FileObject fobj) throws IOException {
/* 242 */     logger.debug("Writing out all sectors from cache to " + fobj.getName());
/*     */     
/* 244 */     OutputStream fos = fobj.getContent().getOutputStream();
/*     */ 
/*     */     
/* 247 */     for (int i = 0; i < this.sectors.size(); i++) {
/*     */ 
/*     */       
/* 250 */       if (this.sectors.get(i) != null) {
/*     */         
/* 252 */         fos.write(((DWDiskSector)this.sectors.get(i)).getData(), 0, (((DWDiskSector)this.sectors.get(i)).getData()).length);
/* 253 */         ((DWDiskSector)this.sectors.get(i)).makeClean();
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 258 */         int ss = 256;
/*     */         
/* 260 */         if (getParams().containsKey("_sectorsize")) {
/*     */           
/*     */           try {
/*     */             
/* 264 */             ss = ((Integer)getParam("_sectorsize")).intValue();
/*     */           }
/* 266 */           catch (NumberFormatException e) {}
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 273 */         fos.write(new byte[ss], 0, ss);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 279 */     fos.close();
/*     */     
/* 281 */     if (this.fileobj != null) {
/* 282 */       setLastModifiedTime(this.fileobj.getContent().getLastModifiedTime());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDirtySectors() {
/* 291 */     int drt = 0;
/*     */     
/* 293 */     for (int i = 0; i < this.sectors.size(); i++) {
/*     */       
/* 295 */       if (this.sectors.get(i) != null)
/*     */       {
/* 297 */         if (((DWDiskSector)this.sectors.get(i)).isDirty())
/*     */         {
/* 299 */           drt++;
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 304 */     return drt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DWDiskSector getSector(int no) {
/* 310 */     if (this.sectors.get(no) != null) {
/* 311 */       return this.sectors.get(no);
/*     */     }
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getWriteProtect() {
/* 319 */     return this.params.getBoolean("writeprotect", DWDefs.DISK_DEFAULT_WRITEPROTECT).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getParam(String key) {
/* 324 */     if (this.params.containsKey(key)) {
/* 325 */       return this.params.getProperty(key);
/*     */     }
/* 327 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void insert(DWDiskDrive drive) {
/* 334 */     this.drive = drive;
/*     */ 
/*     */ 
/*     */     
/* 338 */     Iterator<ConfigurationListener> citr = this.params.getConfigurationListeners().iterator();
/* 339 */     while (citr.hasNext())
/*     */     {
/* 341 */       this.params.removeConfigurationListener(citr.next());
/*     */     }
/*     */ 
/*     */     
/* 345 */     this.params.addConfigurationListener(new DWDiskConfigListener(this));
/*     */ 
/*     */ 
/*     */     
/* 349 */     Iterator<String> itr = this.params.getKeys();
/* 350 */     while (itr.hasNext()) {
/*     */       
/* 352 */       String key = itr.next();
/* 353 */       this.drive.submitEvent(key, this.params.getProperty(key).toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(String key, String val) {
/* 360 */     if (this.drive != null)
/*     */     {
/* 362 */       this.drive.submitEvent(key, val);
/*     */     }
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDisk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */