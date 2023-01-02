/*     */ package com.groupunix.drivewireserver.dwprotocolhandler;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWRFMHandler
/*     */ {
/*  13 */   private static final Logger logger = Logger.getLogger("DWServer.DWRFMHandler");
/*     */   
/*     */   public static final byte RFM_OP_CREATE = 1;
/*     */   
/*     */   public static final byte RFM_OP_OPEN = 2;
/*     */   
/*     */   public static final byte RFM_OP_MAKDIR = 3;
/*     */   
/*     */   public static final byte RFM_OP_CHGDIR = 4;
/*     */   public static final byte RFM_OP_DELETE = 5;
/*     */   public static final byte RFM_OP_SEEK = 6;
/*     */   public static final byte RFM_OP_READ = 7;
/*     */   public static final byte RFM_OP_WRITE = 8;
/*     */   public static final byte RFM_OP_READLN = 9;
/*     */   public static final byte RFM_OP_WRITLN = 10;
/*     */   public static final byte RFM_OP_GETSTT = 11;
/*     */   public static final byte RFM_OP_SETSTT = 12;
/*     */   public static final byte RFM_OP_CLOSE = 13;
/*  31 */   private DWRFMPath[] paths = new DWRFMPath[256];
/*     */   
/*     */   private int handlerno;
/*     */ 
/*     */   
/*     */   public DWRFMHandler(int handlerno) {
/*  37 */     logger.debug("init for handler #" + handlerno);
/*  38 */     this.handlerno = handlerno;
/*     */   }
/*     */ 
/*     */   
/*     */   public void DoRFMOP(DWProtocolDevice protodev, int rfm_op) {
/*  43 */     switch (rfm_op) {
/*     */       
/*     */       case 1:
/*  46 */         DoOP_RFM_CREATE(protodev);
/*     */         break;
/*     */       case 2:
/*  49 */         DoOP_RFM_OPEN(protodev);
/*     */         break;
/*     */       case 3:
/*  52 */         DoOP_RFM_MAKDIR();
/*     */         break;
/*     */       case 4:
/*  55 */         DoOP_RFM_CHGDIR();
/*     */         break;
/*     */       case 5:
/*  58 */         DoOP_RFM_DELETE();
/*     */         break;
/*     */       case 6:
/*  61 */         DoOP_RFM_SEEK(protodev);
/*     */         break;
/*     */       case 7:
/*  64 */         DoOP_RFM_READ(protodev);
/*     */         break;
/*     */       case 8:
/*  67 */         DoOP_RFM_WRITE();
/*     */         break;
/*     */       case 9:
/*  70 */         DoOP_RFM_READLN(protodev);
/*     */         break;
/*     */       case 10:
/*  73 */         DoOP_RFM_WRITLN(protodev);
/*     */         break;
/*     */       case 11:
/*  76 */         DoOP_RFM_GETSTT(protodev);
/*     */         break;
/*     */       case 12:
/*  79 */         DoOP_RFM_SETSTT(protodev);
/*     */         break;
/*     */       case 13:
/*  82 */         DoOP_RFM_CLOSE(protodev);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_CLOSE(DWProtocolDevice protodev) {
/*  91 */     logger.debug("CLOSE");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  96 */       int pathno = protodev.comRead1(true);
/*     */       
/*  98 */       if (this.paths[pathno] == null) {
/*     */         
/* 100 */         logger.error("close on null path: " + pathno);
/*     */       }
/*     */       else {
/*     */         
/* 104 */         this.paths[pathno].close();
/* 105 */         this.paths[pathno] = null;
/*     */       } 
/*     */ 
/*     */       
/* 109 */       protodev.comWrite1(0, true);
/*     */     
/*     */     }
/* 112 */     catch (IOException e) {
/*     */ 
/*     */       
/* 115 */       e.printStackTrace();
/* 116 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 119 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_SETSTT(DWProtocolDevice protodev) {
/* 126 */     logger.debug("SETSTT");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 131 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 134 */       int call = protodev.comRead1(true);
/*     */       
/* 136 */       logger.debug("SETSTT path " + pathno + " call " + call);
/*     */       
/* 138 */       switch (call) {
/*     */         
/*     */         case 15:
/* 141 */           setSTT_FD(protodev, pathno);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/* 147 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 150 */       e.printStackTrace();
/* 151 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 154 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_GETSTT(DWProtocolDevice protodev) {
/* 162 */     logger.debug("GETSTT");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 167 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 170 */       int call = protodev.comRead1(true);
/*     */       
/* 172 */       logger.debug("GETSTT path " + pathno + " call " + call);
/*     */       
/* 174 */       switch (call) {
/*     */         
/*     */         case 15:
/* 177 */           getSTT_FD(protodev, pathno);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 184 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 187 */       e.printStackTrace();
/* 188 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 191 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void getSTT_FD(DWProtocolDevice protodev, int pathno) {
/* 199 */     logger.debug("getstt_fd");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 205 */       int size = DWUtils.int2(protodev.comRead(2));
/*     */       
/* 207 */       byte[] buf = new byte[size];
/*     */ 
/*     */       
/*     */       try {
/* 211 */         buf = this.paths[pathno].getFd(size);
/* 212 */       } catch (FileSystemException e) {
/*     */         
/* 214 */         logger.error("Failed to get FD for path " + pathno);
/*     */       } 
/*     */ 
/*     */       
/* 218 */       protodev.comWrite(buf, size, true);
/*     */       
/* 220 */       logger.debug("sent " + size + " bytes of FD for path " + pathno);
/*     */     }
/* 222 */     catch (IOException e) {
/*     */ 
/*     */       
/* 225 */       e.printStackTrace();
/* 226 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 229 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setSTT_FD(DWProtocolDevice protodev, int pathno) {
/* 237 */     logger.debug("getstt_fd");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 243 */       int size = DWUtils.int2(protodev.comRead(2));
/*     */       
/* 245 */       byte[] buf = new byte[size];
/*     */       
/* 247 */       buf = protodev.comRead(size);
/*     */ 
/*     */       
/*     */       try {
/* 251 */         this.paths[pathno].setFd(buf);
/* 252 */       } catch (FileSystemException e) {
/*     */         
/* 254 */         logger.error("Failed to set FD on path " + pathno);
/*     */       } 
/*     */       
/* 257 */       logger.debug("read " + size + " bytes of FD for path " + pathno);
/*     */     }
/* 259 */     catch (IOException e) {
/*     */ 
/*     */       
/* 262 */       e.printStackTrace();
/* 263 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 266 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_WRITLN(DWProtocolDevice protodev) {
/* 275 */     logger.debug("WRITLN");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 280 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 283 */       byte[] maxbytesb = new byte[2];
/*     */       
/* 285 */       maxbytesb = protodev.comRead(2);
/*     */       
/* 287 */       int maxbytes = DWUtils.int2(maxbytesb);
/*     */ 
/*     */       
/* 290 */       byte[] buf = new byte[maxbytes];
/*     */       
/* 292 */       buf = protodev.comRead(maxbytes);
/*     */ 
/*     */       
/* 295 */       this.paths[pathno].writeBytes(buf, maxbytes);
/* 296 */       this.paths[pathno].incSeekpos(maxbytes);
/*     */       
/* 298 */       logger.debug("writln on path " + pathno + " bytes: " + maxbytes);
/*     */     }
/* 300 */     catch (IOException e) {
/*     */ 
/*     */       
/* 303 */       e.printStackTrace();
/* 304 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 307 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_READLN(DWProtocolDevice protodev) {
/* 315 */     logger.debug("READLN");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 320 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 323 */       byte[] maxbytesb = new byte[2];
/*     */       
/* 325 */       maxbytesb = protodev.comRead(2);
/*     */       
/* 327 */       int maxbytes = DWUtils.int2(maxbytesb);
/*     */       
/* 329 */       int availbytes = this.paths[pathno].getBytesAvail(maxbytes);
/*     */       
/* 331 */       logger.debug("initial AB: " + availbytes);
/*     */       
/* 333 */       byte[] buf = new byte[availbytes];
/*     */       
/* 335 */       System.arraycopy(this.paths[pathno].getBytes(availbytes), 0, buf, 0, availbytes);
/*     */ 
/*     */       
/* 338 */       int x = 0;
/* 339 */       while (x < availbytes) {
/*     */         
/* 341 */         if (buf[x] == 13)
/*     */         {
/* 343 */           availbytes = x + 1;
/*     */         }
/* 345 */         x++;
/*     */       } 
/*     */       
/* 348 */       logger.debug("adjusted AB: " + availbytes);
/*     */       
/* 350 */       protodev.comWrite1(availbytes, true);
/*     */       
/* 352 */       if (availbytes > 0) {
/*     */ 
/*     */         
/* 355 */         protodev.comWrite(buf, availbytes, false);
/* 356 */         this.paths[pathno].incSeekpos(availbytes);
/*     */       } 
/*     */       
/* 359 */       logger.debug("readln on path " + pathno + " maxbytes: " + maxbytes + " availbytes: " + availbytes);
/*     */     }
/* 361 */     catch (IOException e) {
/*     */ 
/*     */       
/* 364 */       e.printStackTrace();
/* 365 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 368 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_WRITE() {
/* 377 */     logger.debug("WRITE");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_READ(DWProtocolDevice protodev) {
/* 383 */     logger.debug("READ");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 388 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 391 */       byte[] maxbytesb = new byte[2];
/*     */       
/* 393 */       maxbytesb = protodev.comRead(2);
/*     */       
/* 395 */       int maxbytes = DWUtils.int2(maxbytesb);
/*     */       
/* 397 */       int availbytes = this.paths[pathno].getBytesAvail(maxbytes);
/*     */       
/* 399 */       if (maxbytes > availbytes)
/*     */       {
/* 401 */         maxbytes = availbytes;
/*     */       }
/*     */       
/* 404 */       byte[] buf = new byte[maxbytes];
/*     */       
/* 406 */       System.arraycopy(this.paths[pathno].getBytes(maxbytes), 0, buf, 0, maxbytes);
/*     */       
/* 408 */       protodev.comWrite1(maxbytes, true);
/*     */       
/* 410 */       if (maxbytes > 0) {
/*     */ 
/*     */         
/* 413 */         protodev.comWrite(buf, maxbytes, false);
/* 414 */         this.paths[pathno].incSeekpos(maxbytes);
/*     */         
/* 416 */         logger.debug("buf: " + DWUtils.byteArrayToHexString(buf));
/*     */       } 
/*     */ 
/*     */       
/* 420 */       logger.debug("read on path " + pathno + " maxbytes: " + maxbytes);
/*     */     }
/* 422 */     catch (IOException e) {
/*     */ 
/*     */       
/* 425 */       e.printStackTrace();
/* 426 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 429 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_SEEK(DWProtocolDevice protodev) {
/* 439 */     logger.debug("SEEK");
/*     */     
/*     */     try {
/* 442 */       int pathno = protodev.comRead1(true);
/*     */ 
/*     */       
/* 445 */       byte[] seekpos = new byte[4];
/*     */ 
/*     */       
/* 448 */       seekpos = protodev.comRead(4);
/*     */       
/* 450 */       this.paths[pathno].setSeekpos(DWUtils.int4(seekpos));
/*     */ 
/*     */       
/* 453 */       protodev.comWrite1(0, true);
/*     */     
/*     */     }
/* 456 */     catch (IOException e) {
/*     */ 
/*     */       
/* 459 */       e.printStackTrace();
/* 460 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 463 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_DELETE() {
/* 471 */     logger.debug("DELETE");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_CHGDIR() {
/* 477 */     logger.debug("CHGDIR");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_MAKDIR() {
/* 483 */     logger.debug("MAKDIR");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_CREATE(DWProtocolDevice protodev) {
/* 489 */     logger.debug("CREATE");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 494 */       int pathno = protodev.comRead1(true);
/* 495 */       int modebyte = protodev.comRead1(true);
/*     */       
/* 497 */       modebyte &= 0xFF;
/*     */ 
/*     */       
/* 500 */       String pathstr = new String();
/*     */       
/* 502 */       int nchar = protodev.comRead1(true);
/* 503 */       while (nchar != 13) {
/*     */         
/* 505 */         pathstr = pathstr + Character.toString((char)nchar);
/* 506 */         nchar = protodev.comRead1(true);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 512 */       this.paths[pathno] = new DWRFMPath(this.handlerno, pathno);
/* 513 */       this.paths[pathno].setPathstr(pathstr);
/*     */       
/* 515 */       int result = this.paths[pathno].createFile();
/*     */       
/* 517 */       protodev.comWrite1(result, true);
/*     */       
/* 519 */       logger.debug("create path " + pathno + " mode " + modebyte + ", to " + pathstr + ": result " + result);
/*     */     }
/* 521 */     catch (IOException e) {
/*     */ 
/*     */       
/* 524 */       e.printStackTrace();
/*     */     }
/* 526 */     catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 529 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void DoOP_RFM_OPEN(DWProtocolDevice protodev) {
/* 536 */     logger.debug("OPEN");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 541 */       int pathno = protodev.comRead1(true);
/* 542 */       int modebyte = protodev.comRead1(true);
/*     */       
/* 544 */       modebyte &= 0xFF;
/*     */ 
/*     */       
/* 547 */       String pathstr = new String();
/*     */       
/* 549 */       int nchar = protodev.comRead1(true);
/* 550 */       while (nchar != 13) {
/*     */         
/* 552 */         pathstr = pathstr + Character.toString((char)nchar);
/* 553 */         nchar = protodev.comRead1(true);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 560 */       this.paths[pathno] = new DWRFMPath(this.handlerno, pathno);
/* 561 */       this.paths[pathno].setPathstr(pathstr);
/*     */       
/* 563 */       int result = this.paths[pathno].openFile(modebyte);
/* 564 */       protodev.comWrite1(result, true);
/*     */       
/* 566 */       logger.debug("open path " + pathno + " mode " + modebyte + ", to " + pathstr + ": result " + result);
/*     */     
/*     */     }
/* 569 */     catch (IOException e) {
/*     */ 
/*     */       
/* 572 */       e.printStackTrace();
/* 573 */     } catch (DWCommTimeOutException e) {
/*     */ 
/*     */       
/* 576 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwprotocolhandler/DWRFMHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */