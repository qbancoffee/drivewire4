/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
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
/*     */ public class DWRFMFD
/*     */ {
/*     */   private byte ATT;
/*     */   private byte[] OWN;
/*     */   private byte[] DAT;
/*     */   private byte LNK;
/*     */   private byte[] SIZ;
/*     */   private byte[] Creat;
/*     */   private String pathstr;
/*     */   private FileSystemManager fsManager;
/*     */   private FileObject fileobj;
/*  59 */   private static final Logger logger = Logger.getLogger("DWServer.DWRFMFD");
/*     */ 
/*     */ 
/*     */   
/*     */   public DWRFMFD(String pathstr) throws FileSystemException {
/*  64 */     this.pathstr = pathstr;
/*     */     
/*  66 */     this.fsManager = VFS.getManager();
/*  67 */     this.fileobj = this.fsManager.resolveFile(pathstr);
/*     */     
/*  69 */     logger.info("New FD for '" + pathstr + "'");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getFD() {
/*  76 */     byte[] b = new byte[256];
/*     */     
/*  78 */     for (int i = 0; i < 256; i++)
/*     */     {
/*  80 */       b[i] = 0;
/*     */     }
/*     */     
/*  83 */     b[0] = getATT();
/*  84 */     System.arraycopy(getOWN(), 0, b, 1, 2);
/*  85 */     System.arraycopy(getDAT(), 0, b, 3, 5);
/*  86 */     b[8] = getLNK();
/*  87 */     System.arraycopy(getSIZ(), 0, b, 9, 4);
/*  88 */     System.arraycopy(getCreat(), 0, b, 13, 3);
/*     */     
/*  90 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFD(byte[] fd) {
/*  96 */     byte[] b = new byte[5];
/*     */     
/*  98 */     setATT(fd[0]);
/*     */     
/* 100 */     System.arraycopy(fd, 1, b, 0, 2);
/* 101 */     setOWN(b);
/*     */     
/* 103 */     System.arraycopy(fd, 3, b, 0, 5);
/* 104 */     setDAT(b);
/*     */     
/* 106 */     setLNK(fd[8]);
/*     */     
/* 108 */     System.arraycopy(fd, 9, b, 0, 4);
/* 109 */     setSIZ(b);
/*     */     
/* 111 */     System.arraycopy(fd, 13, b, 0, 3);
/* 112 */     setCreat(b);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFD() {}
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
/*     */   public void readFD() throws FileSystemException {
/* 176 */     setOWN(new byte[] { 0, 0 });
/* 177 */     setLNK((byte)1);
/*     */     
/* 179 */     if (this.fileobj.exists()) {
/*     */ 
/*     */ 
/*     */       
/* 183 */       byte tmpmode = 0;
/*     */ 
/*     */ 
/*     */       
/* 187 */       if (this.fileobj.isReadable()) {
/* 188 */         tmpmode = (byte)(tmpmode + 9);
/*     */       }
/* 190 */       if (this.fileobj.isWriteable()) {
/* 191 */         tmpmode = (byte)(tmpmode + 18);
/*     */       }
/*     */       
/* 194 */       tmpmode = (byte)(tmpmode + 36);
/*     */       
/* 196 */       if (this.fileobj.getType() == FileType.FOLDER) {
/* 197 */         tmpmode = (byte)(tmpmode - 128);
/*     */       }
/*     */       
/* 200 */       setATT(tmpmode);
/*     */ 
/*     */ 
/*     */       
/* 204 */       setDAT(timeToBytes(this.fileobj.getContent().getLastModifiedTime()));
/*     */ 
/*     */       
/* 207 */       setSIZ(lengthToBytes(this.fileobj.getContent().getSize()));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 212 */       setCreat(new byte[] { 0, 0, 0 });
/*     */     }
/*     */     else {
/*     */       
/* 216 */       logger.error("attempt to read FD for non existant file '" + this.pathstr + "'");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] lengthToBytes(long length) {
/* 225 */     double maxlen = Math.pow(256.0D, 4.0D) / 2.0D;
/*     */     
/* 227 */     if (length > maxlen) {
/*     */       
/* 229 */       logger.error("File too big: " + length + " bytes in '" + this.pathstr + "' (max " + maxlen + ")");
/* 230 */       return new byte[] { 0, 0, 0, 0 };
/*     */     } 
/*     */     
/* 233 */     byte[] b = new byte[4];
/* 234 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 236 */       b[3 - i] = (byte)(int)(length >>> i * 8);
/*     */     }
/*     */     
/* 239 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] timeToBytes(long time) {
/* 246 */     GregorianCalendar c = new GregorianCalendar();
/*     */     
/* 248 */     c.setTime(new Date(time));
/*     */     
/* 250 */     byte[] b = new byte[5];
/*     */     
/* 252 */     b[0] = (byte)(c.get(1) - 108);
/* 253 */     b[1] = (byte)(c.get(2) + 1);
/* 254 */     b[2] = (byte)c.get(5);
/* 255 */     b[3] = (byte)c.get(11);
/* 256 */     b[4] = (byte)c.get(12);
/*     */     
/* 258 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long bytesToTime(byte[] b) {
/* 265 */     GregorianCalendar c = new GregorianCalendar();
/*     */     
/* 267 */     c.set(1, b[0] + 108);
/* 268 */     c.set(2, b[1] - 1);
/* 269 */     c.set(5, b[2]);
/* 270 */     c.set(11, b[3]);
/* 271 */     c.set(12, b[4]);
/*     */     
/* 273 */     return c.getTimeInMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setATT(byte aTT) {
/* 280 */     this.ATT = aTT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getATT() {
/* 287 */     return this.ATT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOWN(byte[] oWN) {
/* 294 */     this.OWN = oWN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getOWN() {
/* 301 */     return this.OWN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDAT(byte[] dAT) {
/* 308 */     this.DAT = dAT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getDAT() {
/* 315 */     return this.DAT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLNK(byte lNK) {
/* 322 */     this.LNK = lNK;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getLNK() {
/* 329 */     return this.LNK;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSIZ(byte[] sIZ) {
/* 336 */     this.SIZ = sIZ;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getSIZ() {
/* 343 */     return this.SIZ;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreat(byte[] creat) {
/* 350 */     this.Creat = creat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getCreat() {
/* 357 */     return this.Creat;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWRFMFD.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */