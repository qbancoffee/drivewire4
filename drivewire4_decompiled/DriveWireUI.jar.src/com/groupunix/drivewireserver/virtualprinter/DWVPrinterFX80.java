/*     */ package com.groupunix.drivewireserver.virtualprinter;
/*     */ 
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
/*     */ import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;
/*     */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*     */ import com.groupunix.drivewireserver.virtualserial.DWVSerialCircularBuffer;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import javax.imageio.ImageIO;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWVPrinterFX80
/*     */   implements DWVPrinterDriver
/*     */ {
/*  25 */   private DWVSerialCircularBuffer printBuffer = new DWVSerialCircularBuffer(-1, true);
/*     */   
/*     */   private HierarchicalConfiguration config;
/*     */   
/*     */   private double DEF_XSIZE;
/*     */   
/*     */   private double DEF_YSIZE;
/*     */   
/*     */   private double SZ_PICA;
/*     */   
/*     */   private double SZ_ELITE;
/*     */   
/*     */   private double SZ_COMPRESSED;
/*     */   
/*     */   private boolean m_expanded = false;
/*     */   
/*     */   private boolean m_pica = true;
/*     */   private boolean m_elite = false;
/*     */   private boolean m_compressed = false;
/*     */   private boolean m_doublestrike = false;
/*     */   private boolean m_emphasized = false;
/*     */   private boolean m_escape = false;
/*     */   private double line_height;
/*     */   private double char_width;
/*  49 */   private DWVPrinterFX80CharacterSet charset = new DWVPrinterFX80CharacterSet();
/*     */   
/*     */   private Graphics2D rGraphic;
/*     */   
/*     */   private File printDir;
/*     */   
/*     */   private File printFile;
/*     */   
/*     */   private double xpos;
/*     */   
/*     */   private double ypos;
/*     */   
/*     */   private BufferedImage rImage;
/*     */ 
/*     */   
/*     */   public DWVPrinterFX80(HierarchicalConfiguration config) {
/*  65 */     this.config = config;
/*  66 */     this.DEF_XSIZE = config.getDouble("DPI", 300.0D) * 8.5D;
/*  67 */     this.DEF_YSIZE = config.getDouble("DPI", 300.0D) * 11.0D;
/*  68 */     this.SZ_PICA = this.DEF_XSIZE / 80.0D;
/*  69 */     this.SZ_ELITE = this.DEF_XSIZE / 96.0D;
/*  70 */     this.SZ_COMPRESSED = this.DEF_XSIZE / 132.0D;
/*  71 */     this.line_height = this.DEF_YSIZE / config.getInt("Lines", 66);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addByte(byte data) throws IOException {
/*  78 */     this.printBuffer.getOutputStream().write(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDriverName() {
/*  85 */     return "FX80";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws NumberFormatException, IOException, DWPrinterNotDefinedException, DWPrinterFileError {
/*  94 */     loadCharacter(this.config.getString("CharacterFile", "default.chars"));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     this.rImage = new BufferedImage((int)this.DEF_XSIZE, (int)this.DEF_YSIZE, 13);
/*     */     
/* 101 */     this.rGraphic = (Graphics2D)this.rImage.getGraphics();
/*     */ 
/*     */     
/* 104 */     this.rGraphic.setColor(Color.WHITE);
/* 105 */     this.rGraphic.fillRect(0, 0, (int)this.DEF_XSIZE, (int)this.DEF_YSIZE);
/*     */ 
/*     */     
/* 108 */     this.char_width = getCPI();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 113 */     this.xpos = 0.0D;
/* 114 */     this.ypos = this.line_height;
/*     */     
/* 116 */     while (this.printBuffer.getAvailable() > 0) {
/*     */       
/* 118 */       char c = (char)this.printBuffer.getInputStream().read();
/*     */       
/* 120 */       int cc = c;
/*     */ 
/*     */       
/* 123 */       if (this.m_escape) {
/*     */         
/* 125 */         switch (cc) {
/*     */ 
/*     */           
/*     */           case 64:
/* 129 */             reset_printer();
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 69:
/* 135 */             this.m_emphasized = true;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 70:
/* 140 */             this.m_emphasized = false;
/*     */ 
/*     */           
/*     */           case 71:
/* 144 */             this.m_doublestrike = true;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 72:
/* 149 */             this.m_doublestrike = false;
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 77:
/* 155 */             this.m_elite = true;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 80:
/* 160 */             this.m_elite = false;
/*     */             break;
/*     */         } 
/*     */ 
/*     */         
/* 165 */         this.char_width = getCPI();
/*     */         
/* 167 */         this.m_escape = false;
/*     */         continue;
/*     */       } 
/* 170 */       if (cc < 32 || cc == 127 || (cc > 127 && cc < 160) || cc == 255) {
/*     */ 
/*     */         
/* 173 */         switch (cc) {
/*     */ 
/*     */           
/*     */           case 9:
/* 177 */             this.xpos += 8.0D * this.char_width;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 13:
/* 182 */             this.xpos = 0.0D;
/* 183 */             newline();
/*     */             break;
/*     */ 
/*     */           
/*     */           case 14:
/* 188 */             this.m_expanded = true;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 15:
/* 193 */             this.m_compressed = true;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 18:
/* 198 */             this.m_compressed = false;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 20:
/* 203 */             this.m_expanded = false;
/*     */             break;
/*     */ 
/*     */           
/*     */           case 27:
/* 208 */             this.m_escape = true;
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 214 */         this.char_width = getCPI();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 219 */       drawCharacter(c, this.xpos, this.ypos);
/*     */ 
/*     */       
/* 222 */       if (this.m_expanded) {
/*     */         
/* 224 */         this.xpos += this.char_width * 2.0D;
/*     */       }
/*     */       else {
/*     */         
/* 228 */         this.xpos += this.char_width;
/*     */       } 
/*     */       
/* 231 */       if (this.xpos >= this.DEF_XSIZE) {
/*     */ 
/*     */         
/* 234 */         this.xpos = 0.0D;
/* 235 */         newline();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 248 */       this.printFile = getPrinterFile();
/*     */ 
/*     */ 
/*     */       
/* 252 */       ImageIO.write(this.rImage, this.config.getString("ImageFormat", "PNG"), this.printFile);
/*     */ 
/*     */     
/*     */     }
/* 256 */     catch (IOException ex) {}
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
/*     */   private void reset_printer() {
/* 270 */     this.m_expanded = false;
/* 271 */     this.m_pica = true;
/* 272 */     this.m_elite = false;
/* 273 */     this.m_compressed = false;
/* 274 */     this.m_doublestrike = false;
/* 275 */     this.m_emphasized = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private double getCPI() {
/*     */     double sz;
/* 282 */     if (this.m_elite) {
/*     */ 
/*     */       
/* 285 */       sz = this.SZ_ELITE;
/*     */     }
/* 287 */     else if (this.m_compressed) {
/*     */       
/* 289 */       sz = this.SZ_COMPRESSED;
/*     */     }
/*     */     else {
/*     */       
/* 293 */       sz = this.SZ_PICA;
/*     */     } 
/*     */     
/* 296 */     return sz;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void newline() throws DWPrinterNotDefinedException, DWPrinterFileError {
/* 302 */     this.ypos += this.line_height;
/*     */     
/* 304 */     if (this.ypos >= this.DEF_YSIZE) {
/*     */ 
/*     */       
/* 307 */       this.ypos = this.line_height;
/*     */ 
/*     */       
/*     */       try {
/* 311 */         this.printFile = getPrinterFile();
/* 312 */         ImageIO.write(this.rImage, this.config.getString("ImageFormat", "PNG"), this.printFile);
/*     */ 
/*     */ 
/*     */         
/* 316 */         this.rImage = new BufferedImage((int)this.DEF_XSIZE, (int)this.DEF_YSIZE, 11);
/* 317 */         this.rGraphic = (Graphics2D)this.rImage.getGraphics();
/*     */         
/* 319 */         this.rGraphic.setColor(Color.WHITE);
/* 320 */         this.rGraphic.fillRect(0, 0, (int)this.DEF_XSIZE, (int)this.DEF_YSIZE);
/*     */       }
/* 322 */       catch (IOException ex) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getFileExtension(String set) {
/* 333 */     if (set.equalsIgnoreCase("JPEG") || set.equalsIgnoreCase("JPG")) {
/* 334 */       return ".jpg";
/*     */     }
/* 336 */     if (set.equalsIgnoreCase("GIF")) {
/* 337 */       return ".gif";
/*     */     }
/* 339 */     if (set.equalsIgnoreCase("BMP")) {
/* 340 */       return ".bmp";
/*     */     }
/* 342 */     if (set.equalsIgnoreCase("WBMP")) {
/* 343 */       return ".bmp";
/*     */     }
/*     */     
/* 346 */     return ".png";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void drawCharacter(int ch, double xpos, double ypos) {
/* 353 */     for (int i = 0; i < 12; i++) {
/*     */       
/* 355 */       int lbits = this.charset.getCharacterCol(ch, i);
/*     */       
/* 357 */       drawCharCol(lbits, xpos, ypos);
/*     */       
/* 359 */       if (this.m_doublestrike)
/*     */       {
/* 361 */         drawCharCol(lbits, xpos, ypos + this.DEF_YSIZE / 66.0D / 24.0D);
/*     */       }
/*     */       
/* 364 */       if (this.m_expanded) {
/*     */         
/* 366 */         xpos += this.char_width / 6.0D;
/* 367 */         drawCharCol(lbits, xpos, ypos);
/*     */         
/* 369 */         if (this.m_doublestrike)
/*     */         {
/* 371 */           drawCharCol(lbits, xpos, ypos + this.DEF_YSIZE / 66.0D / 24.0D);
/*     */         
/*     */         }
/*     */       }
/* 375 */       else if (this.m_emphasized) {
/*     */         
/* 377 */         xpos += this.char_width / 12.0D;
/* 378 */         drawCharCol(lbits, xpos, ypos);
/*     */         
/* 380 */         if (this.m_doublestrike)
/*     */         {
/* 382 */           drawCharCol(lbits, xpos, ypos + this.DEF_YSIZE / 66.0D / 24.0D);
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 387 */         xpos += this.char_width / 12.0D;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void drawCharCol(int lbits, double xpos, double ypos) {
/* 398 */     for (int i = 0; i < 9; i++) {
/*     */       
/* 400 */       if ((lbits & (int)Math.pow(2.0D, i)) == Math.pow(2.0D, i)) {
/*     */ 
/*     */ 
/*     */         
/* 404 */         int r = (int)(this.char_width / 5.0D);
/*     */         
/* 406 */         int x = (int)xpos - r / 2;
/*     */         
/* 408 */         int y = (int)ypos - r / 2;
/*     */         
/* 410 */         int[] pdx = new int[4];
/* 411 */         int[] pdy = new int[4];
/*     */         
/* 413 */         int[] sdx = new int[8];
/* 414 */         int[] sdy = new int[8];
/*     */         
/* 416 */         int ix = (int)xpos;
/* 417 */         int iy = (int)ypos;
/*     */         
/* 419 */         pdx[0] = ix - 2;
/* 420 */         pdy[0] = iy;
/* 421 */         pdx[1] = ix;
/* 422 */         pdy[1] = iy - 2;
/* 423 */         pdx[2] = ix + 2;
/* 424 */         pdy[2] = iy;
/* 425 */         pdx[3] = ix;
/* 426 */         pdy[3] = iy + 2;
/*     */         
/* 428 */         sdx[0] = ix - 2;
/* 429 */         sdy[0] = iy - 1;
/*     */         
/* 431 */         sdx[1] = ix - 1;
/* 432 */         sdy[1] = iy - 2;
/*     */         
/* 434 */         sdx[2] = ix + 1;
/* 435 */         sdy[2] = iy - 2;
/*     */         
/* 437 */         sdx[3] = ix + 2;
/* 438 */         sdy[3] = iy - 1;
/*     */         
/* 440 */         sdx[4] = ix + 2;
/* 441 */         sdy[4] = iy + 1;
/*     */         
/* 443 */         sdx[5] = ix + 1;
/* 444 */         sdy[5] = iy + 2;
/*     */         
/* 446 */         sdx[6] = ix - 1;
/* 447 */         sdy[6] = iy + 2;
/*     */         
/* 449 */         sdx[7] = ix - 2;
/* 450 */         sdy[7] = iy + 1;
/*     */ 
/*     */         
/* 453 */         this.rGraphic.setColor(Color.GRAY);
/* 454 */         this.rGraphic.drawPolygon(sdx, sdy, 8);
/*     */         
/* 456 */         this.rGraphic.setColor(Color.BLACK);
/* 457 */         this.rGraphic.fillPolygon(pdx, pdy, 4);
/*     */         
/* 459 */         this.rGraphic.setColor(Color.DARK_GRAY);
/* 460 */         this.rGraphic.drawPolygon(pdx, pdy, 4);
/*     */ 
/*     */ 
/*     */         
/* 464 */         this.rGraphic.setColor(Color.BLACK);
/*     */         
/* 466 */         r = (int)(this.char_width / 6.0D);
/* 467 */         x = (int)xpos - r / 2;
/* 468 */         y = (int)ypos - r / 2;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 473 */       ypos -= this.line_height / 10.0D;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadCharacter(String fname) throws NumberFormatException, IOException {
/* 481 */     int curline = 0;
/* 482 */     int curchar = -1;
/* 483 */     int curpos = -1;
/* 484 */     int[] charbits = new int[12];
/* 485 */     int prop = 0;
/*     */ 
/*     */ 
/*     */     
/* 489 */     FileInputStream fstream = new FileInputStream(fname);
/* 490 */     DataInputStream in = new DataInputStream(fstream);
/*     */     
/* 492 */     BufferedReader br = new BufferedReader(new InputStreamReader(in));
/*     */ 
/*     */     
/*     */     String strLine;
/*     */     
/* 497 */     while ((strLine = br.readLine()) != null) {
/*     */       
/* 499 */       curline++;
/*     */       
/* 501 */       if (strLine.startsWith("#") || strLine.length() == 0) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 509 */       if (strLine.startsWith("c")) {
/*     */         
/* 511 */         if (curchar > -1) {
/*     */ 
/*     */           
/* 514 */           this.charset.setCharacter(curchar, charbits, prop);
/*     */ 
/*     */           
/* 517 */           curpos = -1;
/* 518 */           curchar = -1;
/* 519 */           charbits = new int[12];
/*     */         } 
/*     */ 
/*     */         
/* 523 */         int tmpint = Integer.parseInt(strLine.substring(1));
/*     */         
/* 525 */         if (tmpint < 0 || tmpint > 255) {
/*     */           
/* 527 */           System.err.println("Error at line " + curline + ": invalid character number, must be 0-255 ");
/*     */           
/*     */           continue;
/*     */         } 
/* 531 */         curpos = 0;
/* 532 */         prop = 12;
/* 533 */         curchar = tmpint;
/*     */         continue;
/*     */       } 
/* 536 */       if (strLine.startsWith("p")) {
/*     */ 
/*     */         
/* 539 */         int tmpint = Integer.parseInt(strLine.substring(1));
/*     */         
/* 541 */         if (tmpint < 1 || tmpint > 12) {
/*     */           
/* 543 */           System.err.println("Error at line " + curline + ": invalid proportional length, must be 1-12 ");
/*     */           
/*     */           continue;
/*     */         } 
/* 547 */         prop = tmpint;
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/* 554 */       if (strLine.length() == 9) {
/*     */ 
/*     */         
/* 557 */         int j = 0;
/*     */         
/* 559 */         for (int i = 0; i < 9; i++) {
/*     */           
/* 561 */           char c = strLine.charAt(i);
/* 562 */           if (c == '1') {
/*     */             
/* 564 */             j = (int)(j + Math.pow(2.0D, i));
/*     */           }
/* 566 */           else if (c != '0') {
/*     */             
/* 568 */             System.err.println("Error at line " + curline + " (in character " + curchar + "): boolean values must contain only 0 or 1");
/*     */           } 
/*     */         } 
/*     */         
/* 572 */         charbits[curpos] = j;
/* 573 */         curpos++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 578 */       int tmpval = Integer.parseInt(strLine);
/* 579 */       if (tmpval < 0 || tmpval > 511) {
/*     */         
/* 581 */         tmpval = 0;
/* 582 */         System.err.println("Error at line " + curline + " (in character " + curchar + "): decimal values must be 0-511");
/*     */       } 
/* 584 */       charbits[curpos] = tmpval;
/* 585 */       curpos++;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 595 */     in.close();
/*     */ 
/*     */     
/* 598 */     this.charset.setCharacter(curchar, charbits, prop);
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
/*     */   public File getPrinterFile() throws IOException, DWPrinterNotDefinedException, DWPrinterFileError {
/* 610 */     if (this.config.containsKey("OutputFile")) {
/*     */       
/* 612 */       if (DWUtils.FileExistsOrCreate(this.config.getString("OutputFile")))
/*     */       {
/* 614 */         return new File(this.config.getString("OutputFile"));
/*     */       }
/*     */ 
/*     */       
/* 618 */       throw new DWPrinterFileError("Cannot find or create the output file '" + this.config.getString("OutputFile") + "'");
/*     */     } 
/*     */ 
/*     */     
/* 622 */     if (this.config.containsKey("OutputDir")) {
/*     */       
/* 624 */       if (DWUtils.DirExistsOrCreate(this.config.getString("OutputDir")))
/*     */       {
/* 626 */         return File.createTempFile("dw_fx80_", getFileExtension(this.config.getString("ImageFormat", "PNG")), new File(this.config.getString("OutputDir")));
/*     */       }
/*     */ 
/*     */       
/* 630 */       throw new DWPrinterFileError("Cannot find or create the output directory '" + this.config.getString("OutputDir") + "'");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 636 */     throw new DWPrinterFileError("No OutputFile or OutputDir defined in config");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPrinterName() {
/* 644 */     return this.config.getString("[@name]", "?noname?");
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/virtualprinter/DWVPrinterFX80.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */