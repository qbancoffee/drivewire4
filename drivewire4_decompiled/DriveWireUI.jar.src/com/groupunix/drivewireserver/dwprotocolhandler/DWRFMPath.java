/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.DriveWireServer;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.FileSystemManager;
/*     */ import org.apache.commons.vfs.FileType;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWRFMPath
/*     */ {
/*  20 */   private static final Logger logger = Logger.getLogger("DWServer.DWRFMPath");
/*     */   
/*     */   private int pathno;
/*     */   
/*     */   private String pathstr;
/*     */   
/*     */   private String localroot;
/*     */   
/*     */   private int seekpos;
/*     */   private int handlerno;
/*     */   private boolean dirmode = false;
/*     */   private byte[] dirbuffer;
/*     */   private FileSystemManager fsManager;
/*     */   private FileObject fileobj;
/*     */   
/*     */   public DWRFMPath(int handlerno, int pathno) throws FileSystemException {
/*  36 */     setPathno(pathno);
/*  37 */     setSeekpos(0);
/*  38 */     logger.debug("new path " + pathno);
/*     */     
/*  40 */     this.fsManager = VFS.getManager();
/*  41 */     setLocalroot(DriveWireServer.getHandler(this.handlerno).getConfig().getString("RFMRoot", "/"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPathno(int pathno) {
/*  47 */     logger.debug("set path to " + pathno);
/*  48 */     this.pathno = pathno;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPathno() {
/*  53 */     return this.pathno;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPathstr(String pathstr) {
/*  58 */     this.pathstr = "/";
/*  59 */     logger.debug("set pathstr to " + this.pathstr);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPathstr() {
/*  65 */     return this.pathstr;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  70 */     logger.debug("closing path " + this.pathno + " to " + this.pathstr);
/*     */     
/*     */     try {
/*  73 */       this.fileobj.close();
/*  74 */     } catch (FileSystemException e) {
/*     */       
/*  76 */       logger.warn("error closing file: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSeekpos(int seekpos) {
/*  82 */     this.seekpos = seekpos;
/*  83 */     logger.debug("seek to " + seekpos + " on path " + this.pathno);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSeekpos() {
/*  88 */     return this.seekpos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int openFile(int modebyte) {
/*     */     try {
/*  97 */       this.fileobj = this.fsManager.resolveFile(this.localroot + this.pathstr);
/*     */       
/*  99 */       if (((byte)modebyte & Byte.MIN_VALUE) == -128) {
/*     */ 
/*     */         
/* 102 */         if (this.fileobj.isReadable()) {
/*     */           
/* 104 */           if (this.fileobj.getType() == FileType.FOLDER) {
/*     */             
/* 106 */             this.dirmode = true;
/* 107 */             genDirBuffer();
/*     */             
/* 109 */             logger.debug("directory open: modebyte " + modebyte);
/*     */             
/* 111 */             return 0;
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 116 */           this.fileobj.close();
/* 117 */           return 214;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 122 */         this.fileobj.close();
/* 123 */         return 216;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 130 */       if (this.fileobj.isReadable())
/*     */       {
/* 132 */         return 0;
/*     */       }
/*     */ 
/*     */       
/* 136 */       this.fileobj.close();
/* 137 */       return 216;
/*     */ 
/*     */     
/*     */     }
/* 141 */     catch (FileSystemException e) {
/*     */       
/* 143 */       logger.warn("open failed: " + e.getMessage());
/* 144 */       return 216;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void genDirBuffer() throws FileSystemException {
/* 150 */     FileObject[] childs = this.fileobj.getChildren();
/*     */     
/* 152 */     if (childs.length > 0) {
/*     */       
/* 154 */       this.dirbuffer = new byte[childs.length * 32 + 64];
/*     */ 
/*     */       
/* 157 */       this.dirbuffer[0] = 46;
/* 158 */       this.dirbuffer[1] = -82;
/* 159 */       this.dirbuffer[32] = -82;
/*     */ 
/*     */       
/* 162 */       for (int i = 2; i < childs.length + 2; i++)
/*     */       {
/* 164 */         String fname = childs[i - 2].getName().getBaseName();
/* 165 */         if (fname.length() <= 29)
/*     */         {
/* 167 */           System.arraycopy(fname.getBytes(), 0, this.dirbuffer, i * 32, fname.length());
/*     */ 
/*     */           
/* 170 */           this.dirbuffer[i * 32 + fname.length() - 1] = (byte)(this.dirbuffer[i * 32 + fname.length() - 1] + 128);
/*     */ 
/*     */         
/*     */         }
/*     */         else
/*     */         {
/*     */           
/* 177 */           logger.debug("cannot add long named file '" + childs[i - 2].getName() + "'");
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 184 */       logger.debug("empty directory");
/* 185 */       this.dirbuffer = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocalroot(String localroot) {
/* 192 */     this.localroot = localroot;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLocalroot() {
/* 197 */     return this.localroot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int createFile() {
/*     */     try {
/* 206 */       this.fileobj = this.fsManager.resolveFile(this.localroot + this.pathstr);
/*     */ 
/*     */       
/* 209 */       if (this.fileobj.exists()) {
/*     */ 
/*     */         
/* 212 */         this.fileobj.close();
/* 213 */         return 218;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 218 */       this.fileobj.createFile();
/* 219 */       return 0;
/*     */     }
/* 221 */     catch (FileSystemException e) {
/*     */       
/* 223 */       logger.warn("create failed: " + e.getMessage());
/* 224 */       return 245;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBytesAvail(int maxbytes) {
/* 231 */     if (this.dirmode)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 236 */       return this.dirbuffer.length - this.seekpos;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 244 */     File f = new File(this.localroot + this.pathstr);
/* 245 */     if (f.exists()) {
/*     */ 
/*     */       
/* 248 */       int tmpsize = (int)f.length() - this.seekpos;
/*     */ 
/*     */       
/* 251 */       if (tmpsize > 127)
/*     */       {
/* 253 */         tmpsize = 127;
/*     */       }
/*     */       
/* 256 */       if (tmpsize > maxbytes)
/*     */       {
/* 258 */         return maxbytes;
/*     */       }
/* 260 */       return tmpsize;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     return 0;
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
/*     */   public byte[] getBytes(int availbytes) {
/* 278 */     byte[] buf = new byte[availbytes];
/*     */     
/* 280 */     if (this.dirmode) {
/*     */       
/* 282 */       System.arraycopy(this.dirbuffer, this.seekpos, buf, 0, availbytes);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 287 */       RandomAccessFile inFile = null;
/*     */       
/* 289 */       File f = new File(this.localroot + this.pathstr);
/* 290 */       if (f.exists()) {
/*     */         
/* 292 */         logger.debug("FILE: asked for " + availbytes);
/*     */ 
/*     */         
/*     */         try {
/* 296 */           inFile = new RandomAccessFile(f, "r");
/*     */           
/* 298 */           inFile.seek(this.seekpos);
/*     */ 
/*     */ 
/*     */           
/* 302 */           inFile.read(buf);
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 307 */         catch (FileNotFoundException e) {
/*     */ 
/*     */           
/* 310 */           e.printStackTrace();
/*     */         }
/* 312 */         catch (IOException e) {
/*     */ 
/*     */           
/* 315 */           e.printStackTrace();
/*     */         } finally {
/*     */ 
/*     */           
/*     */           try {
/*     */             
/* 321 */             inFile.close();
/*     */           }
/* 323 */           catch (IOException e) {
/*     */ 
/*     */             
/* 326 */             e.printStackTrace();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 332 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void incSeekpos(int bytes) {
/* 338 */     this.seekpos += bytes;
/* 339 */     logger.debug("incSeekpos to " + this.seekpos);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFd(byte[] buf) throws FileSystemException {
/* 344 */     DWRFMFD fd = new DWRFMFD(DriveWireServer.getHandler(this.handlerno).getConfig().getString("RFMRoot", "/") + this.pathstr);
/*     */     
/* 346 */     fd.readFD();
/*     */     
/* 348 */     byte[] fdtmp = fd.getFD();
/*     */     
/* 350 */     System.arraycopy(buf, 0, fdtmp, 0, buf.length);
/*     */     
/* 352 */     fd.setFD(fdtmp);
/*     */     
/* 354 */     fd.writeFD();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getFd(int size) throws FileSystemException {
/* 360 */     byte[] b = new byte[size];
/*     */     
/* 362 */     DWRFMFD fd = new DWRFMFD(DriveWireServer.getHandler(this.handlerno).getConfig().getString("RFMRoot", "/") + this.pathstr);
/*     */     
/* 364 */     fd.readFD();
/*     */     
/* 366 */     System.arraycopy(fd.getFD(), 0, b, 0, size);
/* 367 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBytes(byte[] buf, int maxbytes) {
/* 373 */     RandomAccessFile inFile = null;
/*     */     
/* 375 */     File f = new File(this.localroot + this.pathstr);
/* 376 */     if (f.exists()) {
/*     */       
/*     */       try
/*     */       {
/* 380 */         inFile = new RandomAccessFile(f, "rw");
/*     */         
/* 382 */         inFile.seek(this.seekpos);
/*     */ 
/*     */ 
/*     */         
/* 386 */         inFile.write(buf);
/*     */       
/*     */       }
/* 389 */       catch (FileNotFoundException e)
/*     */       {
/*     */         
/* 392 */         e.printStackTrace();
/*     */       }
/* 394 */       catch (IOException e)
/*     */       {
/*     */         
/* 397 */         e.printStackTrace();
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 404 */       logger.error("write to non existent file");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWRFMPath.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */