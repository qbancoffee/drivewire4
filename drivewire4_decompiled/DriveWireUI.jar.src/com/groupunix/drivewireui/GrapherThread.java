/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Drawable;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ 
/*     */ public class GrapherThread implements Runnable {
/*  10 */   private long lastdisk = 0L;
/*  11 */   private long lastvser = 0L;
/*     */   
/*  13 */   private int interval = 2000;
/*  14 */   private int samples = 200;
/*     */   
/*     */   private boolean wanttodie = false;
/*  17 */   private int topgap = 20;
/*  18 */   private int ylabel = 30;
/*  19 */   private int xlabel = 70;
/*     */   
/*  21 */   private int[] disksamp = new int[this.samples];
/*  22 */   private int[] vsersamp = new int[this.samples];
/*  23 */   private long[] memfsamp = new long[this.samples];
/*  24 */   private long[] memtsamp = new long[this.samples];
/*     */   
/*  26 */   private int diskmax = 0;
/*  27 */   private int vsmax = 0;
/*     */   
/*  29 */   private int pos = 0;
/*     */   
/*     */   private Color colorGraphBGH;
/*     */   
/*     */   private Color colorGraphBGL;
/*     */   
/*     */   private Color colorLabel;
/*     */   private Color colorDiskOps;
/*     */   private Color colorMemGraphTotal;
/*     */   private Color colorMemGraphUsed;
/*     */   
/*     */   public void run() {
/*  41 */     Thread.currentThread().setName("dwuiGrapher-" + Thread.currentThread().getId());
/*     */ 
/*     */     
/*  44 */     this.colorGraphBGH = new Color((Device)MainWin.getDisplay(), 144, 144, 144);
/*  45 */     this.colorGraphBGL = new Color((Device)MainWin.getDisplay(), 48, 48, 48);
/*  46 */     this.colorLabel = new Color((Device)MainWin.getDisplay(), 181, 181, 181);
/*  47 */     this.colorDiskOps = new Color((Device)MainWin.getDisplay(), 128, 128, 181);
/*  48 */     this.colorMemGraphTotal = new Color((Device)MainWin.getDisplay(), 128, 181, 128);
/*  49 */     this.colorMemGraphUsed = new Color((Device)MainWin.getDisplay(), 181, 128, 128);
/*     */ 
/*     */ 
/*     */     
/*  53 */     initGraph(MainWin.graphMemUse);
/*  54 */     initGraph(MainWin.graphDiskOps);
/*  55 */     initGraph(MainWin.graphVSerialOps);
/*     */ 
/*     */ 
/*     */     
/*  59 */     for (int i = 0; i < this.samples; i++) {
/*     */       
/*  61 */       this.disksamp[i] = 0;
/*  62 */       this.vsersamp[i] = 0;
/*  63 */       this.memfsamp[i] = 0L;
/*  64 */       this.memtsamp[i] = 0L;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  69 */     while (MainWin.serverStatus == null && !this.wanttodie) {
/*     */ 
/*     */       
/*     */       try {
/*  73 */         Thread.sleep(this.interval);
/*     */       }
/*  75 */       catch (InterruptedException e) {
/*     */         
/*  77 */         this.wanttodie = true;
/*     */       } 
/*     */     } 
/*     */     
/*  81 */     if (!this.wanttodie) {
/*     */ 
/*     */       
/*  84 */       this.lastdisk = MainWin.serverStatus.getDiskops();
/*  85 */       this.lastvser = MainWin.serverStatus.getVserialops();
/*     */ 
/*     */       
/*     */       try {
/*  89 */         Thread.sleep(this.interval);
/*     */       }
/*  91 */       catch (InterruptedException e) {
/*     */         
/*  93 */         this.wanttodie = true;
/*     */       } 
/*     */       
/*  96 */       while (MainWin.serverStatus != null && !this.wanttodie) {
/*     */ 
/*     */         
/*  99 */         synchronized (MainWin.serverStatus) {
/*     */           
/* 101 */           this.disksamp[this.pos] = (int)(MainWin.serverStatus.getDiskops() - this.lastdisk);
/* 102 */           this.vsersamp[this.pos] = (int)(MainWin.serverStatus.getVserialops() - this.lastvser);
/*     */           
/* 104 */           this.memfsamp[this.pos] = MainWin.serverStatus.getMemfree();
/* 105 */           this.memtsamp[this.pos] = MainWin.serverStatus.getMemtotal();
/*     */           
/* 107 */           this.lastdisk = MainWin.serverStatus.getDiskops();
/* 108 */           this.lastvser = MainWin.serverStatus.getVserialops();
/*     */         } 
/*     */         
/* 111 */         if (this.disksamp[this.pos] > this.diskmax) {
/* 112 */           this.diskmax = this.disksamp[this.pos];
/*     */         }
/* 114 */         if (this.vsersamp[this.pos] > this.vsmax) {
/* 115 */           this.vsmax = this.vsersamp[this.pos];
/*     */         }
/*     */ 
/*     */         
/* 119 */         drawMemGraph();
/* 120 */         drawDiskOpsGraph();
/* 121 */         drawVSerialOpsGraph();
/*     */ 
/*     */         
/* 124 */         MainWin.doDisplayAsync(new Runnable()
/*     */             {
/*     */               
/*     */               public void run()
/*     */               {
/* 129 */                 if (!MainWin.getDisplay().isDisposed() && MainWin.canvasMemUse != null && !MainWin.canvasMemUse.isDisposed())
/*     */                 {
/* 131 */                   MainWin.canvasMemUse.redraw();
/*     */                 }
/*     */                 
/* 134 */                 if (!MainWin.getDisplay().isDisposed() && MainWin.canvasDiskOps != null && !MainWin.canvasDiskOps.isDisposed())
/*     */                 {
/* 136 */                   MainWin.canvasDiskOps.redraw();
/*     */                 }
/*     */                 
/* 139 */                 if (!MainWin.getDisplay().isDisposed() && MainWin.canvasVSerialOps != null && !MainWin.canvasVSerialOps.isDisposed())
/*     */                 {
/* 141 */                   MainWin.canvasVSerialOps.redraw();
/*     */                 }
/*     */               }
/*     */             });
/*     */ 
/*     */ 
/*     */         
/* 148 */         this.pos++;
/* 149 */         if (this.pos == this.samples) {
/* 150 */           this.pos = 0;
/*     */         }
/*     */         
/*     */         try {
/* 154 */           Thread.sleep(this.interval);
/*     */         }
/* 156 */         catch (InterruptedException e) {
/*     */           
/* 158 */           this.wanttodie = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initGraph(Image img) {
/* 169 */     GC gc = new GC((Drawable)img);
/* 170 */     int width = (MainWin.graphMemUse.getImageData()).width;
/* 171 */     int height = (MainWin.graphMemUse.getImageData()).height;
/* 172 */     gc.setForeground(this.colorGraphBGH);
/* 173 */     gc.setBackground(this.colorGraphBGL);
/* 174 */     gc.fillGradientRectangle(0, 0, width, height, true);
/* 175 */     gc.dispose();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void drawVSerialOpsGraph() {
/* 183 */     GC gc = new GC((Drawable)MainWin.graphVSerialOps);
/* 184 */     int width = (MainWin.graphVSerialOps.getImageData()).width - this.xlabel;
/* 185 */     int height = (MainWin.graphVSerialOps.getImageData()).height - this.ylabel - this.topgap;
/*     */ 
/*     */     
/* 188 */     long maxops = 0L;
/*     */     
/* 190 */     for (int i = 0; i < this.samples; i++) {
/*     */       
/* 192 */       if (this.vsersamp[i] > maxops) {
/* 193 */         maxops = this.vsersamp[i];
/*     */       }
/*     */     } 
/* 196 */     double vscale = Double.valueOf(height).doubleValue() / Double.valueOf(maxops).doubleValue();
/* 197 */     double hscale = (width / this.samples);
/*     */ 
/*     */     
/* 200 */     gc.setForeground(this.colorGraphBGH);
/* 201 */     gc.setBackground(this.colorGraphBGL);
/* 202 */     gc.fillGradientRectangle(0, 0, width + this.xlabel, height + this.ylabel + this.topgap, true);
/*     */     int j;
/* 204 */     for (j = 0; j < this.samples; j++) {
/*     */ 
/*     */       
/* 207 */       int samp = this.pos + 1 + j;
/* 208 */       if (samp > this.samples - 1) {
/* 209 */         samp -= this.samples;
/*     */       }
/* 211 */       if (this.vsersamp[samp] > 0) {
/*     */         
/* 213 */         gc.setBackground(this.colorDiskOps);
/*     */         
/* 215 */         double top = this.vsersamp[samp] * vscale;
/* 216 */         gc.fillRectangle(j, height - (int)top + this.topgap, (int)hscale, (int)top);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 222 */     gc.setFont(MainWin.fontGraphLabel);
/*     */     
/* 224 */     for (j = 0; j < 5; j++) {
/*     */       
/* 226 */       int y = j * height / 5;
/* 227 */       double mb = Double.valueOf((height - y)).doubleValue() / vscale;
/* 228 */       gc.setForeground(this.colorLabel);
/* 229 */       gc.drawString((int)Math.rint(mb) + "", width + 4, y - 5 + this.topgap, true);
/*     */       
/* 231 */       gc.setLineStyle(3);
/* 232 */       gc.setForeground(this.colorGraphBGL);
/* 233 */       gc.drawLine(0, y + this.topgap, width, y + this.topgap);
/*     */     } 
/*     */ 
/*     */     
/* 237 */     gc.setForeground(this.colorLabel);
/* 238 */     gc.drawString(String.format("Virtual serial operations/sec:  %d   Max:  %d", new Object[] { Integer.valueOf(this.vsersamp[this.pos]), Integer.valueOf(this.vsmax) }), 5, height + 5 + this.topgap, true);
/*     */     
/* 240 */     gc.dispose();
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
/*     */   private void drawDiskOpsGraph() {
/* 252 */     GC gc = new GC((Drawable)MainWin.graphDiskOps);
/* 253 */     int width = (MainWin.graphDiskOps.getImageData()).width - this.xlabel;
/* 254 */     int height = (MainWin.graphDiskOps.getImageData()).height - this.ylabel - this.topgap;
/*     */ 
/*     */     
/* 257 */     long maxdisk = 0L;
/*     */     
/* 259 */     for (int i = 0; i < this.samples; i++) {
/*     */       
/* 261 */       if (this.disksamp[i] > maxdisk) {
/* 262 */         maxdisk = this.disksamp[i];
/*     */       }
/*     */     } 
/* 265 */     double vscale = Double.valueOf(height).doubleValue() / Double.valueOf(maxdisk).doubleValue();
/* 266 */     double hscale = (width / this.samples);
/*     */ 
/*     */     
/* 269 */     gc.setForeground(this.colorGraphBGH);
/* 270 */     gc.setBackground(this.colorGraphBGL);
/* 271 */     gc.fillGradientRectangle(0, 0, width + this.xlabel, height + this.ylabel + this.topgap, true);
/*     */     int j;
/* 273 */     for (j = 0; j < this.samples; j++) {
/*     */ 
/*     */       
/* 276 */       int samp = this.pos + 1 + j;
/* 277 */       if (samp > this.samples - 1) {
/* 278 */         samp -= this.samples;
/*     */       }
/* 280 */       if (this.disksamp[samp] > 0) {
/*     */         
/* 282 */         gc.setBackground(this.colorDiskOps);
/*     */         
/* 284 */         double top = this.disksamp[samp] * vscale;
/* 285 */         gc.fillRectangle(j, height - (int)top + this.topgap, (int)hscale, (int)top);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 291 */     gc.setFont(MainWin.fontGraphLabel);
/*     */     
/* 293 */     for (j = 0; j < 5; j++) {
/*     */       
/* 295 */       int y = j * height / 5;
/* 296 */       double mb = Double.valueOf((height - y)).doubleValue() / vscale;
/* 297 */       gc.setForeground(this.colorLabel);
/* 298 */       gc.drawString((int)Math.rint(mb) + "", width + 4, y - 5 + this.topgap, true);
/*     */       
/* 300 */       gc.setLineStyle(3);
/* 301 */       gc.setForeground(this.colorGraphBGL);
/* 302 */       gc.drawLine(0, y + this.topgap, width, y + this.topgap);
/*     */     } 
/*     */ 
/*     */     
/* 306 */     gc.setForeground(this.colorLabel);
/* 307 */     gc.drawString(String.format("Disk operations/sec:  %d   Max:  %d", new Object[] { Integer.valueOf(this.disksamp[this.pos]), Integer.valueOf(this.diskmax) }), 5, height + 5 + this.topgap, true);
/*     */     
/* 309 */     gc.dispose();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void drawMemGraph() {
/* 316 */     GC gc = new GC((Drawable)MainWin.graphMemUse);
/* 317 */     int width = (MainWin.graphMemUse.getImageData()).width - this.xlabel;
/* 318 */     int height = (MainWin.graphMemUse.getImageData()).height - this.ylabel - this.topgap;
/*     */ 
/*     */     
/* 321 */     long maxmem = 0L;
/*     */     
/* 323 */     for (int i = 0; i < this.samples; i++) {
/*     */       
/* 325 */       if (this.memtsamp[i] > maxmem) {
/* 326 */         maxmem = this.memtsamp[i];
/*     */       }
/*     */     } 
/* 329 */     double vscale = Double.valueOf(height).doubleValue() / Double.valueOf(maxmem).doubleValue();
/* 330 */     double hscale = (width / this.samples);
/*     */ 
/*     */     
/* 333 */     gc.setForeground(this.colorGraphBGH);
/* 334 */     gc.setBackground(this.colorGraphBGL);
/* 335 */     gc.fillGradientRectangle(0, 0, width + this.xlabel, height + this.ylabel + this.topgap, true);
/*     */     int j;
/* 337 */     for (j = 0; j < this.samples; j++) {
/*     */ 
/*     */       
/* 340 */       int samp = this.pos + 1 + j;
/* 341 */       if (samp > this.samples - 1) {
/* 342 */         samp -= this.samples;
/*     */       }
/* 344 */       if (this.memtsamp[samp] > 0L) {
/*     */         
/* 346 */         gc.setBackground(this.colorMemGraphTotal);
/*     */         
/* 348 */         double top = this.memtsamp[samp] * vscale;
/* 349 */         gc.fillRectangle(j, height - (int)top + this.topgap, 1, 2);
/*     */         
/* 351 */         gc.setBackground(this.colorMemGraphUsed);
/* 352 */         top = (this.memtsamp[samp] - this.memfsamp[samp]) * vscale;
/* 353 */         gc.fillRectangle(j, height - (int)top + this.topgap, (int)hscale, (int)top);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 359 */     gc.setFont(MainWin.fontGraphLabel);
/* 360 */     gc.setForeground(this.colorLabel);
/*     */     
/* 362 */     for (j = 0; j < 5; j++) {
/*     */       
/* 364 */       int y = j * height / 5;
/* 365 */       double mb = Double.valueOf((height - y)).doubleValue() / vscale / 1024.0D;
/* 366 */       gc.setForeground(this.colorLabel);
/* 367 */       gc.drawString((int)Math.rint(mb) + " MB", width + 4, y - 5 + this.topgap, true);
/*     */       
/* 369 */       gc.setLineStyle(3);
/* 370 */       gc.setForeground(this.colorGraphBGL);
/* 371 */       gc.drawLine(0, y + this.topgap, width, y + this.topgap);
/*     */     } 
/*     */     
/* 374 */     gc.setForeground(this.colorLabel);
/* 375 */     gc.drawString(String.format("Total: %.1f MB   Used: %.1f MB   Free: %.1f MB", new Object[] { Double.valueOf(this.memtsamp[this.pos] / 1024.0D), Double.valueOf((this.memtsamp[this.pos] - this.memfsamp[this.pos]) / 1024.0D), Double.valueOf(this.memfsamp[this.pos] / 1024.0D) }), 5, height + 5 + this.topgap, true);
/*     */     
/* 377 */     gc.dispose();
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/GrapherThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */