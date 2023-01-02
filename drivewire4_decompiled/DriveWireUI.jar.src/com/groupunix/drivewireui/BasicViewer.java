/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ import java.util.Vector;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseWheelListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Drawable;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicViewer
/*     */   extends Composite
/*     */ {
/*  33 */   private int width = 512;
/*  34 */   private int height = 384;
/*     */   
/*  36 */   private int xborder = 5;
/*  37 */   private int yborder = 5;
/*  38 */   private int yfudge = 38;
/*  39 */   private int xfudge = 16;
/*     */   
/*  41 */   private int scols = MainWin.config.getInt("BasicViewer_columns", 32);
/*  42 */   private int srows = MainWin.config.getInt("BasicViewer_rows", 16);
/*     */   
/*     */   private Image cocotext;
/*     */   
/*     */   private Image cocotext2;
/*     */   
/*     */   private Image cocotext3;
/*     */   
/*     */   private Image cocotext3_80;
/*     */   
/*     */   private Image cocotext_trans;
/*     */   
/*     */   private Image cocotext2_trans;
/*     */   private Image cocotext3_trans;
/*     */   private Image cocotext3_80_trans;
/*     */   private Canvas coco;
/*     */   private Image cocoimg;
/*     */   private GC cocogc;
/*  60 */   private int[][] dtext = new int[this.srows][this.scols];
/*     */   
/*     */   private int[][] atext;
/*     */   private int[][] basictxt;
/*     */   private Vector<Integer> linerefs;
/*  65 */   private Color[] curscols = new Color[8];
/*     */ 
/*     */   
/*  68 */   private int noscalex = -1;
/*  69 */   private int noscaley = -1;
/*     */   
/*     */   private boolean[] toggles;
/*  72 */   private int basicpos = 0;
/*     */   
/*     */   private String[] origtxt;
/*  75 */   private String search = null;
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicViewer(Composite parent, int style) {
/*  80 */     super(parent, style);
/*     */     
/*  82 */     this.cocotext = SWTResourceManager.getImage(MainWin.class, "/dw/cocotext.png");
/*  83 */     this.cocotext2 = SWTResourceManager.getImage(MainWin.class, "/dw/cocotext2.png");
/*  84 */     this.cocotext3 = SWTResourceManager.getImage(MainWin.class, "/dw/cocotext3.png");
/*  85 */     this.cocotext3_80 = SWTResourceManager.getImage(MainWin.class, "/dw/cocotext3_80.png");
/*     */ 
/*     */ 
/*     */     
/*  89 */     this.cocotext_trans = new Image((Device)getDisplay(), transConv(this.cocotext.getImageData(), 24));
/*  90 */     this.cocotext2_trans = new Image((Device)getDisplay(), transConv(this.cocotext2.getImageData(), 24));
/*  91 */     this.cocotext3_trans = new Image((Device)getDisplay(), transConv(this.cocotext3.getImageData(), 16));
/*  92 */     this.cocotext3_80_trans = new Image((Device)getDisplay(), transConv(this.cocotext3_80.getImageData(), 16));
/*     */     
/*  94 */     this.toggles = new boolean[10];
/*  95 */     for (int i = 0; i < 10; i++)
/*     */     {
/*  97 */       this.toggles[i] = true;
/*     */     }
/*     */     
/* 100 */     this.scols = MainWin.config.getInt("BasicViewer_columns", this.scols);
/* 101 */     this.srows = MainWin.config.getInt("BasicViewer_rows", this.srows);
/*     */     
/* 103 */     Point hw = getHWForRes(this.scols, this.srows);
/* 104 */     this.width = hw.x;
/* 105 */     this.height = hw.y;
/*     */     
/* 107 */     setText(new String[] { "DriveWire 4.0.9c (03/25/2012)".toUpperCase() });
/*     */     
/* 109 */     createContents();
/*     */   }
/*     */ 
/*     */   
/*     */   private ImageData transConv(ImageData imageData, int tall) {
/* 114 */     int[] lineData = new int[imageData.width];
/*     */     int y;
/* 116 */     for (y = tall - 2; y < imageData.height; y += tall) {
/*     */       
/* 118 */       imageData.getPixels(0, y, imageData.width, lineData, 0);
/*     */       
/* 120 */       for (int x = 0; x < lineData.length; x++) {
/*     */         
/* 122 */         imageData.setPixel(x, y, 15724527);
/* 123 */         imageData.setPixel(x, y + 1, 8947848);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 130 */     return imageData;
/*     */   }
/*     */ 
/*     */   
/*     */   public Vector<Integer> getLineRefs() {
/* 135 */     return this.linerefs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Point getHWForRes(int cols, int row) {
/* 141 */     int w = 512;
/* 142 */     int h = 384;
/*     */     
/* 144 */     if (cols > 32) {
/* 145 */       w = 640;
/*     */     }
/* 147 */     return new Point(w, h);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setText(String[] txt) {
/* 154 */     setText(txt, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setText(String[] txt, boolean redraw) {
/* 161 */     this.origtxt = txt;
/* 162 */     this.basictxt = generateMap(txt);
/* 163 */     this.atext = searchMap(this.search);
/* 164 */     this.basicpos = 0;
/*     */     
/* 166 */     if (redraw && this.coco != null) {
/* 167 */       this.coco.redraw();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void search(String txt) {
/* 175 */     this.search = txt;
/* 176 */     int curpos = this.basicpos;
/* 177 */     setText(this.origtxt);
/*     */     
/* 179 */     int lpos = curpos + 1;
/*     */     
/* 181 */     while (lpos < this.basictxt.length) {
/*     */       
/* 183 */       for (int i = 0; i < this.scols; i++) {
/*     */         
/* 185 */         if (this.atext[lpos][i] == 0) {
/*     */           
/* 187 */           this.basicpos = Math.min(Math.max(0, this.basictxt.length - this.srows), lpos);
/* 188 */           updateImg();
/*     */           return;
/*     */         } 
/*     */       } 
/* 192 */       lpos++;
/*     */     } 
/*     */     
/* 195 */     lpos = 0;
/*     */     
/* 197 */     while (lpos < curpos) {
/*     */       
/* 199 */       for (int i = 0; i < this.scols; i++) {
/*     */         
/* 201 */         if (this.atext[lpos][i] == 0) {
/*     */           
/* 203 */           this.basicpos = Math.min(Math.max(0, this.basictxt.length - this.srows), lpos);
/* 204 */           updateImg();
/*     */           return;
/*     */         } 
/*     */       } 
/* 208 */       lpos++;
/*     */     } 
/*     */ 
/*     */     
/* 212 */     this.basicpos = Math.min(Math.max(0, this.basictxt.length - this.srows), curpos);
/* 213 */     updateImg();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int[][] searchMap(String match) {
/* 219 */     int[][] res = new int[this.basictxt.length][this.scols];
/* 220 */     Vector<Point> mlist = new Vector<Point>();
/*     */     
/* 222 */     if (match == null) {
/*     */       
/* 224 */       for (int y = 0; y < this.basictxt.length; y++)
/*     */       {
/* 226 */         for (int x = 0; x < this.scols; x++)
/*     */         {
/* 228 */           res[y][x] = 255;
/*     */         }
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 234 */       for (int y = 0; y < this.basictxt.length; y++) {
/*     */         
/* 236 */         for (int x = 0; x < this.scols; x++) {
/*     */           
/* 238 */           res[y][x] = 255;
/*     */           
/* 240 */           if (this.basictxt[y][x] == match.charAt(mlist.size())) {
/*     */             
/* 242 */             mlist.add(new Point(x, y));
/*     */             
/* 244 */             if (mlist.size() == match.length())
/*     */             {
/*     */               
/* 247 */               for (int i = 0; i < mlist.size(); i++)
/*     */               {
/* 249 */                 res[((Point)mlist.get(i)).y][((Point)mlist.get(i)).x] = 0;
/*     */               }
/*     */               
/* 252 */               mlist.removeAllElements();
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 257 */             mlist.removeAllElements();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 263 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private int[][] generateMap(String[] txt) {
/* 268 */     int lc = 0;
/*     */     
/* 270 */     this.linerefs = new Vector<Integer>();
/* 271 */     this.dtext = new int[this.srows][this.scols];
/*     */     
/* 273 */     for (String l : txt)
/*     */     {
/* 275 */       lc += l.length() / this.scols + 1;
/*     */     }
/*     */ 
/*     */     
/* 279 */     int[][] res = new int[lc][this.scols];
/*     */     int x;
/* 281 */     for (x = 0; x < this.scols; x++) {
/* 282 */       for (int i = 0; i < lc; i++)
/* 283 */         res[i][x] = 32; 
/*     */     } 
/* 285 */     x = 0;
/* 286 */     int y = 0;
/*     */     
/* 288 */     for (String l : txt) {
/*     */ 
/*     */ 
/*     */       
/* 292 */       BasicLine bl = new BasicLine(l);
/*     */       
/* 294 */       if (bl.getLineno() > -1) {
/* 295 */         this.linerefs.add(Integer.valueOf(bl.getLineno()));
/*     */       }
/* 297 */       byte[] bytes = l.getBytes();
/*     */       
/* 299 */       for (int i = 0; i < l.length(); i++) {
/*     */         
/* 301 */         res[y][x] = bytes[i];
/* 302 */         x++;
/*     */         
/* 304 */         if (x >= this.scols) {
/*     */           
/* 306 */           x = 0;
/* 307 */           y++;
/* 308 */           this.linerefs.add(Integer.valueOf(-1));
/*     */         } 
/*     */       } 
/* 311 */       y++;
/* 312 */       x = 0;
/*     */     } 
/*     */     
/* 315 */     return res;
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
/*     */   private void createContents() {
/* 327 */     setBackground(SWTResourceManager.getColor(5));
/*     */     
/* 329 */     setSize(this.width + this.xfudge + this.xborder * 2, this.height + this.yfudge + this.yborder * 2);
/*     */     
/* 331 */     this.curscols[0] = new Color((Device)getDisplay(), 0, 255, 0);
/* 332 */     this.curscols[1] = new Color((Device)getDisplay(), 255, 255, 0);
/* 333 */     this.curscols[2] = new Color((Device)getDisplay(), 0, 0, 255);
/* 334 */     this.curscols[3] = new Color((Device)getDisplay(), 255, 0, 0);
/* 335 */     this.curscols[4] = new Color((Device)getDisplay(), 255, 255, 255);
/* 336 */     this.curscols[5] = new Color((Device)getDisplay(), 0, 255, 255);
/* 337 */     this.curscols[6] = new Color((Device)getDisplay(), 255, 0, 255);
/* 338 */     this.curscols[7] = new Color((Device)getDisplay(), 255, 128, 0);
/*     */ 
/*     */     
/* 341 */     setLayout((Layout)new FillLayout(256));
/*     */     
/* 343 */     this.coco = new Canvas(this, 536870912);
/*     */     
/* 345 */     this.coco.addMouseWheelListener(new MouseWheelListener()
/*     */         {
/*     */ 
/*     */           
/*     */           public void mouseScrolled(MouseEvent e)
/*     */           {
/* 351 */             if (e.count > 0) {
/*     */ 
/*     */               
/* 354 */               BasicViewer.this.basicpos = Math.max(0, BasicViewer.this.basicpos - e.count);
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             }
/* 360 */             else if (BasicViewer.this.basicpos < BasicViewer.this.basictxt.length) {
/*     */               
/* 362 */               BasicViewer.this.basicpos = Math.min(Math.max(0, BasicViewer.this.basictxt.length - BasicViewer.this.srows), BasicViewer.this.basicpos - e.count);
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 367 */             ((Canvas)e.widget).redraw();
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 372 */     this.coco.addKeyListener(new KeyListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           
/*     */           public void keyPressed(KeyEvent e)
/*     */           {
/* 379 */             if (e.character == 's')
/*     */             {
/* 381 */               System.out.println("test");
/*     */             }
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void keyReleased(KeyEvent e) {}
/*     */         });
/* 395 */     this.coco.setBackground(MainWin.colorGreen);
/* 396 */     this.cocoimg = new Image(null, this.width, this.height);
/* 397 */     this.cocogc = new GC((Drawable)this.cocoimg);
/*     */     
/* 399 */     this.coco.addPaintListener(new PaintListener()
/*     */         {
/*     */           int wx;
/*     */ 
/*     */           
/*     */           int hx;
/*     */ 
/*     */ 
/*     */           
/*     */           public void paintControl(PaintEvent e) {
/* 409 */             e.gc.setTextAntialias(0);
/*     */             
/* 411 */             if (MainWin.config.getBoolean("BasicViewer_antialias", true)) {
/*     */               
/* 413 */               e.gc.setAntialias(1);
/* 414 */               e.gc.setAdvanced(true);
/*     */             }
/*     */             else {
/*     */               
/* 418 */               e.gc.setAntialias(0);
/* 419 */               e.gc.setAdvanced(false);
/*     */             } 
/*     */             
/* 422 */             if (BasicViewer.this.scols != MainWin.config.getInt("BasicViewer_columns", BasicViewer.this.scols)) {
/*     */               
/* 424 */               int curpos = BasicViewer.this.basicpos;
/* 425 */               BasicViewer.this.scols = MainWin.config.getInt("BasicViewer_columns", BasicViewer.this.scols);
/* 426 */               BasicViewer.this.srows = MainWin.config.getInt("BasicViewer_rows", BasicViewer.this.srows);
/*     */               
/* 428 */               Point hw = BasicViewer.this.getHWForRes(BasicViewer.this.scols, BasicViewer.this.srows);
/*     */               
/* 430 */               BasicViewer.this.width = hw.x;
/* 431 */               BasicViewer.this.height = hw.y;
/*     */ 
/*     */ 
/*     */               
/* 435 */               BasicViewer.this.setText(BasicViewer.this.origtxt, false);
/* 436 */               BasicViewer.this.basicpos = curpos;
/*     */               
/* 438 */               BasicViewer.this.cocoimg.dispose();
/* 439 */               BasicViewer.this.cocogc.dispose();
/*     */               
/* 441 */               BasicViewer.this.cocoimg = new Image(null, BasicViewer.this.width, BasicViewer.this.height);
/* 442 */               BasicViewer.this.cocogc = new GC((Drawable)BasicViewer.this.cocoimg);
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 449 */             BasicViewer.this.genCocoimg();
/*     */             
/* 451 */             this.wx = (BasicViewer.this.coco.getBounds()).width - BasicViewer.this.xborder * 2;
/* 452 */             this.hx = (BasicViewer.this.coco.getBounds()).height - BasicViewer.this.yborder * 2;
/*     */ 
/*     */             
/* 455 */             if (this.wx < 64 || this.hx < 64) {
/*     */               
/* 457 */               e.gc.setForeground(SWTResourceManager.getColor(2));
/* 458 */               e.gc.setFont(MainWin.fontGraphLabel);
/* 459 */               e.gc.drawString("I'm small!", 1, 1);
/*     */ 
/*     */             
/*     */             }
/* 463 */             else if ((this.wx == BasicViewer.this.noscalex && this.hx == BasicViewer.this.noscaley) || !BasicViewer.this.toggles[8]) {
/*     */               
/* 465 */               e.gc.drawImage(BasicViewer.this.cocoimg, BasicViewer.this.xborder, BasicViewer.this.yborder);
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 470 */               e.gc.drawImage(BasicViewer.this.cocoimg, 0, 0, BasicViewer.this.width, BasicViewer.this.height, BasicViewer.this.xborder, BasicViewer.this.yborder, this.wx, this.hx);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void genCocoimg() {
/* 484 */     if (!isDisposed()) {
/*     */       
/* 486 */       this.cocogc.setBackground(MainWin.colorGreen);
/* 487 */       this.cocogc.fillRectangle(0, 0, this.width, this.height);
/*     */       
/* 489 */       for (int y = 0; y < this.srows; y++) {
/*     */         
/* 491 */         for (int x = 0; x < this.scols; x++) {
/*     */           
/* 493 */           int chr = 32;
/* 494 */           int alf = 255;
/*     */           
/* 496 */           if (y + this.basicpos < this.basictxt.length) {
/*     */             
/* 498 */             chr = this.basictxt[y + this.basicpos][x];
/* 499 */             alf = this.atext[y + this.basicpos][x];
/*     */           } 
/*     */           
/* 502 */           int xsize = 16;
/* 503 */           int ysize = 24;
/*     */           
/* 505 */           Image charimg = this.cocotext;
/* 506 */           Point p = new Point(0, 0);
/*     */           
/* 508 */           if (this.scols == 32) {
/*     */             
/* 510 */             if (chr < 128)
/*     */             {
/* 512 */               p = getCharPoint(chr);
/*     */               
/* 514 */               if (alf < 255) {
/* 515 */                 charimg = this.cocotext_trans;
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 520 */               if (alf < 255) {
/* 521 */                 charimg = this.cocotext2_trans;
/*     */               } else {
/* 523 */                 charimg = this.cocotext2;
/*     */               } 
/* 525 */               p = getGfxPoint(chr);
/*     */             }
/*     */           
/*     */           }
/* 529 */           else if (this.scols == 40) {
/*     */             
/* 531 */             xsize = 16;
/* 532 */             ysize = 16;
/*     */             
/* 534 */             if (chr > 127) {
/* 535 */               chr -= 127;
/*     */             }
/* 537 */             p = getCharPoint(chr);
/*     */             
/* 539 */             if (alf < 255) {
/* 540 */               charimg = this.cocotext3_trans;
/*     */             } else {
/* 542 */               charimg = this.cocotext3;
/*     */             } 
/* 544 */           } else if (this.scols == 80) {
/*     */             
/* 546 */             xsize = 8;
/* 547 */             ysize = 16;
/*     */             
/* 549 */             if (chr > 127) {
/* 550 */               chr -= 127;
/*     */             }
/* 552 */             p = getCharPoint(chr);
/*     */             
/* 554 */             if (alf < 255) {
/* 555 */               charimg = this.cocotext3_80_trans;
/*     */             } else {
/* 557 */               charimg = this.cocotext3_80;
/*     */             } 
/*     */           } 
/* 560 */           this.cocogc.drawImage(charimg, p.x, p.y, xsize, ysize, x * xsize, y * ysize, xsize, ysize);
/*     */           
/* 562 */           this.dtext[y][x] = chr;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Point getCharPoint(int val) {
/* 574 */     Point res = new Point(0, 0);
/*     */     
/* 576 */     if (val < 32) {
/* 577 */       val = 32;
/*     */     }
/* 579 */     if (this.scols == 32) {
/*     */       
/* 581 */       res.y = val / 64 * 24;
/* 582 */       res.x = val % 64 * 16;
/*     */     }
/* 584 */     else if (this.scols == 40) {
/*     */       
/* 586 */       res.y = val / 32 * 16;
/* 587 */       res.x = val % 32 * 16;
/*     */     }
/* 589 */     else if (this.scols == 80) {
/*     */       
/* 591 */       res.y = val / 32 * 16;
/* 592 */       res.x = val % 32 * 8;
/*     */     } 
/*     */     
/* 595 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Point getGfxPoint(int val) {
/* 600 */     val -= 128;
/*     */     
/* 602 */     Point res = new Point(0, 0);
/*     */     
/* 604 */     res.y = val / 32 * 24;
/* 605 */     res.x = val % 32 * 16;
/*     */     
/* 607 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateImg() {
/* 613 */     this.coco.redraw();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void findLine(int ln) {
/* 619 */     int match = this.linerefs.indexOf(Integer.valueOf(ln));
/*     */     
/* 621 */     if (match > -1) {
/*     */       
/* 623 */       this.basicpos = Math.min(Math.max(0, this.basictxt.length - this.srows), match + 1);
/* 624 */       updateImg();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/BasicViewer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */