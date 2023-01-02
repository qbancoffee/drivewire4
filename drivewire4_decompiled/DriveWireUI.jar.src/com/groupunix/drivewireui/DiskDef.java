/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Drawable;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ 
/*     */ public class DiskDef
/*     */   implements Cloneable
/*     */ {
/*  15 */   private int drive = -1;
/*     */   
/*     */   private boolean loaded = false;
/*     */   
/*     */   private boolean changed = true;
/*     */   
/*     */   private boolean diskchanged = false;
/*     */   
/*     */   private HierarchicalConfiguration params;
/*     */   
/*  25 */   private Image diskgraph = new Image(null, 410, 52);
/*  26 */   private HashMap<Integer, Integer> sectors = new HashMap<Integer, Integer>();
/*     */ 
/*     */   
/*  29 */   private double graphscale = 1.0D;
/*     */   
/*     */   private GC gc;
/*  32 */   private int lastread = 0;
/*  33 */   private int lastwrite = 0;
/*  34 */   private int lastclean = 0;
/*  35 */   private int coin = 0;
/*     */   
/*  37 */   private DiskWin diskwin = null;
/*     */   private boolean graphchanged = true;
/*  39 */   private DiskAdvancedWin paramwin = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DiskDef(int driveno) {
/*  46 */     this.drive = driveno;
/*  47 */     this.params = new HierarchicalConfiguration();
/*  48 */     this.gc = new GC((Drawable)this.diskgraph);
/*  49 */     makeNewDGraph();
/*     */     
/*  51 */     if (MainWin.config.getBoolean("DiskWin_" + driveno + "_open", false)) {
/*     */       
/*  53 */       final DiskDef ref = this;
/*  54 */       MainWin.getDisplay().asyncExec(new Runnable()
/*     */           {
/*     */             public void run() {
/*  57 */               DiskDef.this.setDiskwin(new DiskWin(ref, (MainWin.getDiskWinInitPos(DiskDef.this.drive)).x, (MainWin.getDiskWinInitPos(DiskDef.this.drive)).y));
/*  58 */               DiskDef.this.getDiskwin().open(MainWin.getDisplay());
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void makeNewDGraph() {
/*  70 */     synchronized (this.diskgraph) {
/*     */ 
/*     */       
/*  73 */       this.gc.setAdvanced(true);
/*     */       
/*  75 */       this.gc.setBackground(DiskWin.colorDiskBG);
/*  76 */       this.gc.fillRectangle(0, 0, 410, 52);
/*     */       
/*  78 */       this.gc.setTextAntialias(1);
/*     */ 
/*     */       
/*  81 */       this.sectors = new HashMap<Integer, Integer>();
/*  82 */       this.lastclean = 0;
/*  83 */       this.lastread = 0;
/*  84 */       this.lastwrite = 0;
/*  85 */       this.graphchanged = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParam(final String key, final Object val) {
/*  96 */     if (key.startsWith("*")) {
/*     */ 
/*     */       
/*  99 */       if (key.equals("*clean"))
/*     */       {
/* 101 */         markGraphClean(Integer.parseInt(val.toString()));
/*     */       }
/* 103 */       else if (key.equals("*insert"))
/*     */       {
/* 105 */         this.loaded = true;
/* 106 */         makeNewDGraph();
/*     */       
/*     */       }
/* 109 */       else if (key.equals("*eject"))
/*     */       {
/* 111 */         this.loaded = false;
/* 112 */         makeNewDGraph();
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 119 */       this.params.setProperty(key, val);
/*     */ 
/*     */ 
/*     */       
/* 123 */       if (key.equals("_reads") && !val.equals("0") && this.params.containsKey("_reads")) {
/*     */         
/* 125 */         markGraphRead(getLsn());
/*     */       }
/* 127 */       else if (key.equals("_writes") && !val.equals("0") && this.params.containsKey("_writes")) {
/*     */         
/* 129 */         markGraphWrite(getLsn());
/*     */       }
/* 131 */       else if (key.equals("_sectors")) {
/*     */         
/* 133 */         this.graphchanged = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     if (hasParamwin())
/*     */     {
/* 142 */       MainWin.getDisplay().asyncExec(new Runnable()
/*     */           {
/*     */             public void run() {
/* 145 */               if (DiskDef.this.hasParamwin()) {
/* 146 */                 if (val == null) {
/* 147 */                   DiskDef.this.paramwin.submitEvent(key, "");
/*     */                 } else {
/* 149 */                   DiskDef.this.paramwin.submitEvent(key, val.toString());
/*     */                 } 
/*     */               }
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/* 157 */     if (hasDiskwin())
/*     */     {
/* 159 */       MainWin.getDisplay().asyncExec(new Runnable()
/*     */           {
/*     */             public void run() {
/* 162 */               if (DiskDef.this.hasDiskwin())
/*     */               {
/* 164 */                 if (val == null) {
/* 165 */                   DiskDef.this.diskwin.submitEvent(key, "");
/*     */                 } else {
/* 167 */                   DiskDef.this.diskwin.submitEvent(key, val.toString());
/*     */                 } 
/*     */               }
/*     */             }
/*     */           });
/*     */     }
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
/*     */   private void markGraphClean(int sector) {}
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
/*     */   private void markGraphWrite(int lsn) {
/* 198 */     this.lastwrite = lsn;
/* 199 */     this.sectors.put(Integer.valueOf(lsn), Integer.valueOf(255));
/* 200 */     this.graphchanged = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void markGraphRead(int lsn) {
/* 210 */     this.lastread = lsn;
/* 211 */     this.sectors.put(Integer.valueOf(lsn), Integer.valueOf(-255));
/* 212 */     this.graphchanged = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getParam(String key) {
/* 220 */     return this.params.getProperty(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 226 */     return this.params.getString("_path", "");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSectors() {
/* 232 */     return this.params.getInt("_sectors", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLsn() {
/* 238 */     return this.params.getInt("_lsn", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getReads() {
/* 244 */     return this.params.getInt("_reads", 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWrites() {
/* 249 */     return this.params.getInt("_writes", 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLoaded(boolean loaded) {
/* 256 */     this.loaded = loaded;
/*     */     
/* 258 */     if (loaded != this.loaded)
/*     */     {
/* 260 */       this.changed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoaded() {
/* 267 */     return this.loaded;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChanged() {
/* 275 */     return this.changed;
/*     */   }
/*     */   
/*     */   public void setChanged(boolean changed) {
/* 279 */     this.changed = changed;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDiskChanged() {
/* 284 */     return this.diskchanged;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDiskChanged(boolean b) {
/* 289 */     this.diskchanged = b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<String> getParams() {
/* 299 */     return this.params.getKeys();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSyncTo() {
/* 305 */     return this.params.getBoolean("syncto", false);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSyncFrom() {
/* 310 */     return this.params.getBoolean("syncfrom", false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasParam(String key) {
/* 316 */     return this.params.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getFileName() {
/* 321 */     String res = "";
/*     */     
/* 323 */     if (this.params.containsKey("_path")) {
/*     */       
/* 325 */       res = UIUtils.getFilenameFromURI(this.params.getString("_path"));
/*     */       
/* 327 */       if (res.indexOf('/') > -1 && res.indexOf('/') < res.length() - 1) {
/* 328 */         res = res.substring(res.lastIndexOf('/') + 1);
/*     */       }
/*     */     } else {
/*     */       
/* 332 */       res = "(in memory)";
/*     */     } 
/*     */     
/* 335 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormat() {
/* 341 */     return this.params.getString("_format", "unknown");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteProtect() {
/* 346 */     return this.params.getBoolean("writeprotect", false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOffset() {
/* 351 */     return this.params.getInt("offset", 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDiskgraph(Image diskgraph) {
/* 356 */     this.diskgraph = diskgraph;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Image getDiskGraph() {
/* 367 */     if (this.graphchanged) {
/*     */       
/* 369 */       this.graphscale = 410.0D / (getSectors() - getOffset());
/*     */ 
/*     */ 
/*     */       
/* 373 */       int[] tri = new int[6];
/*     */       
/* 375 */       this.gc.setFont(DiskWin.fontDiskGraph);
/* 376 */       this.gc.setBackground(DiskWin.colorDiskBG);
/* 377 */       this.gc.fillRectangle(0, 0, 410, 15);
/*     */ 
/*     */       
/* 380 */       this.gc.setForeground(DiskWin.colorDiskGraphFG);
/* 381 */       this.gc.setBackground(DiskWin.colorDiskBG);
/*     */       
/* 383 */       this.gc.drawText("" + getOffset(), 1, 0, true);
/* 384 */       this.gc.drawText("" + getSectors(), 410 - (this.gc.textExtent("" + getSectors())).x, 0, true);
/*     */       
/* 386 */       int p1 = 102;
/* 387 */       int p2 = 265;
/*     */       
/* 389 */       int readpos = p1;
/* 390 */       int writepos = p2;
/*     */ 
/*     */       
/* 393 */       String tmp = "LAST READ ";
/* 394 */       Point textsize = this.gc.textExtent(tmp);
/*     */       
/* 396 */       this.gc.drawText(tmp, readpos - textsize.x, 0, true);
/* 397 */       this.gc.drawText(this.lastread + "", readpos + 10, 0, true);
/*     */ 
/*     */       
/* 400 */       tri[0] = readpos;
/* 401 */       tri[1] = (textsize.y - 9) / 2;
/* 402 */       tri[2] = tri[0];
/* 403 */       tri[3] = tri[1] + 9;
/* 404 */       tri[4] = tri[0] + 7;
/* 405 */       tri[5] = tri[1] + 5;
/*     */       
/* 407 */       this.gc.setBackground(MainWin.colorGreen);
/* 408 */       this.gc.fillPolygon(tri);
/*     */ 
/*     */       
/* 411 */       tmp = "LAST WRITE ";
/* 412 */       this.gc.drawText(tmp, writepos - (this.gc.textExtent(tmp)).x, 0, true);
/* 413 */       this.gc.drawText(this.lastwrite + "", writepos + 10, 0, true);
/*     */       
/* 415 */       tri[0] = writepos;
/* 416 */       tri[2] = tri[0];
/* 417 */       tri[4] = tri[0] + 7;
/*     */       
/* 419 */       this.gc.setBackground(DiskWin.colorDiskDirty);
/* 420 */       this.gc.fillPolygon(tri);
/*     */       
/* 422 */       this.coin++;
/*     */       
/* 424 */       int lastchange = 0;
/* 425 */       int changes = 0;
/*     */       
/* 427 */       this.gc.setBackground(DiskWin.colorBlack);
/* 428 */       this.gc.setForeground(DiskWin.colorDiskBG);
/*     */ 
/*     */       
/* 431 */       for (int i = 0; i < 410; i++) {
/*     */         
/* 433 */         Color curcol = getGraphColorFor(i);
/*     */         
/* 435 */         if (!curcol.equals(this.gc.getBackground())) {
/*     */           
/* 437 */           this.gc.fillGradientRectangle(lastchange, 20, lastchange + i, 17, true);
/* 438 */           this.gc.setBackground(curcol);
/* 439 */           lastchange = i;
/* 440 */           changes++;
/*     */         } 
/*     */       } 
/*     */       
/* 444 */       this.gc.fillGradientRectangle(lastchange, 20, 410 - lastchange, 17, true);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 449 */       this.gc.setBackground(DiskWin.colorDiskBG);
/* 450 */       this.gc.fillRectangle(0, 37, 410, 52);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 455 */       tri[0] = (int)(this.graphscale * this.lastread) + 1;
/* 456 */       tri[1] = 37;
/* 457 */       tri[2] = tri[0] + 7;
/* 458 */       tri[3] = 45;
/* 459 */       tri[4] = tri[0] - 7;
/* 460 */       tri[5] = 45;
/*     */       
/* 462 */       this.gc.setBackground(MainWin.colorGreen);
/* 463 */       this.gc.fillPolygon(tri);
/*     */       
/* 465 */       tri[0] = (int)(this.graphscale * this.lastwrite) + 1;
/* 466 */       tri[2] = tri[0] + 7;
/* 467 */       tri[4] = tri[0] - 7;
/*     */       
/* 469 */       this.gc.setBackground(DiskWin.colorDiskDirty);
/* 470 */       this.gc.fillPolygon(tri);
/*     */ 
/*     */       
/* 473 */       this.graphchanged = false;
/*     */     } 
/*     */ 
/*     */     
/* 477 */     return this.diskgraph;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Color getGraphColorFor(int x) {
/* 486 */     int elsn = (int)Math.round(x / this.graphscale);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 491 */     if (this.sectors.containsKey(Integer.valueOf(elsn))) {
/*     */ 
/*     */ 
/*     */       
/* 495 */       int scache = ((Integer)this.sectors.get(Integer.valueOf(elsn))).intValue();
/*     */       
/* 497 */       if (scache < 0) {
/* 498 */         return MainWin.colorGreen;
/*     */       }
/* 500 */       if (scache > 0) {
/* 501 */         return DiskWin.colorDiskClean;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 506 */     return DiskWin.colorDiskGraphBG;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getGraphAlphaFor(int x) {
/* 514 */     int elsn = (int)Math.round(x / this.graphscale);
/*     */ 
/*     */     
/* 517 */     if (this.sectors.containsKey(Integer.valueOf(elsn))) {
/*     */       
/* 519 */       int scache = ((Integer)this.sectors.get(Integer.valueOf(elsn))).intValue();
/*     */       
/* 521 */       if (scache < 0) {
/*     */         
/* 523 */         this.sectors.put(Integer.valueOf(elsn), Integer.valueOf(((Integer)this.sectors.get(Integer.valueOf(elsn))).intValue() + 1));
/* 524 */         return Math.abs(scache);
/*     */       } 
/*     */       
/* 527 */       if (scache > 0) {
/*     */         
/* 529 */         this.sectors.put(Integer.valueOf(elsn), Integer.valueOf(((Integer)this.sectors.get(Integer.valueOf(elsn))).intValue() - 1));
/* 530 */         return scache;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 535 */     return 255;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExpand() {
/* 542 */     return this.params.getBoolean("expand", false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLimit() {
/* 548 */     return this.params.getInt("sizelimit", -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDiskwin(DiskWin diskwin) {
/* 555 */     this.diskwin = diskwin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DiskWin getDiskwin() {
/* 562 */     return this.diskwin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasDiskwin() {
/* 569 */     if (this.diskwin == null) {
/* 570 */       return false;
/*     */     }
/* 572 */     if (this.diskwin.shlDwDrive == null) {
/* 573 */       return false;
/*     */     }
/* 575 */     if (this.diskwin.shlDwDrive.isDisposed()) {
/* 576 */       return false;
/*     */     }
/* 578 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDriveNo() {
/* 585 */     return this.drive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParamwin(DiskAdvancedWin win) {
/* 592 */     this.paramwin = win;
/*     */   }
/*     */ 
/*     */   
/*     */   public DiskAdvancedWin getParamwin() {
/* 597 */     return this.paramwin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasParamwin() {
/* 604 */     if (this.paramwin == null) {
/* 605 */       return false;
/*     */     }
/* 607 */     if (this.paramwin.shell == null) {
/* 608 */       return false;
/*     */     }
/* 610 */     if (this.paramwin.shell.isDisposed()) {
/* 611 */       return false;
/*     */     }
/* 613 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGraphchanged() {
/* 620 */     return this.graphchanged;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskDef.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */