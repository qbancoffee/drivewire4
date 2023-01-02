/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.ShellAdapter;
/*     */ import org.eclipse.swt.events.ShellEvent;
/*     */ import org.eclipse.swt.events.ShellListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import com.swtdesigner.SWTResourceManager;
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
/*     */ public class DiskWin
/*     */ {
/*     */   public static final int DGRAPH_WIDTH = 410;
/*     */   public static final int DGRAPH_HEIGHT = 52;
/*     */   DiskDef currentDisk;
/*     */   protected Shell shlDwDrive;
/*     */   private Display display;
/*     */   private Composite compositeDisk;
/*  52 */   public static Color colorWhite = new Color((Device)Display.getDefault(), 255, 255, 255);
/*  53 */   public static Color colorRed = new Color((Device)Display.getDefault(), 255, 0, 0);
/*  54 */   public static Color colorGreen = new Color((Device)Display.getDefault(), 0, 255, 0);
/*  55 */   public static Color colorBlack = new Color((Device)Display.getDefault(), 0, 0, 0);
/*  56 */   public static Color colorDiskBG = new Color((Device)Display.getDefault(), 73, 72, 72);
/*     */   
/*  58 */   public static Color colorDiskFG = new Color((Device)Display.getDefault(), 181, 181, 181);
/*  59 */   public static Color colorDiskGraphFG = new Color((Device)Display.getDefault(), 165, 165, 165);
/*  60 */   public static Color colorDiskGraphBG = new Color((Device)Display.getDefault(), 137, 137, 137);
/*     */   
/*  62 */   public static Color colorDiskDirty = new Color((Device)Display.getDefault(), 255, 0, 0);
/*  63 */   public static Color colorDiskClean = new Color((Device)Display.getDefault(), 150, 0, 0);
/*  64 */   public static Color colorShadow = new Color((Device)Display.getDefault(), 49, 49, 49);
/*     */   
/*     */   public static Font fontDiskNumber;
/*     */   
/*     */   public static Font fontDiskGraph;
/*  69 */   private Boolean driveactivity = Boolean.valueOf(false);
/*     */   
/*     */   private Combo comboDiskPath;
/*     */   
/*     */   protected Vector<DiskStatusItem> diskStatusItems;
/*     */   private Label diskStatusLED;
/*     */   private Canvas diskGraph;
/*  76 */   private List<String> displayUpdateItems = Arrays.asList(new String[] { "_path", "syncto", "syncfrom", "writeprotect", "expand", "sizelimt", "offset", "_last_modified", "*insert", "*eject" });
/*     */   
/*     */   private int initx;
/*     */   
/*     */   private int inity;
/*     */   
/*     */   protected static Image background;
/*     */   private Button btnDiskFile;
/*     */   
/*     */   public DiskWin(DiskDef cdisk, int x, int y) {
/*  86 */     this.currentDisk = cdisk;
/*     */     
/*  88 */     this.initx = x;
/*  89 */     this.inity = y;
/*     */     
/*  91 */     background = SWTResourceManager.getImage(DiskWin.class, "/disk/driveinfo_bg.png");
/*     */ 
/*     */ 
/*     */     
/*  95 */     if (fontDiskNumber == null) {
/*     */       
/*  97 */       HashMap<String, Integer> fontmap = new HashMap<String, Integer>();
/*     */       
/*  99 */       fontmap.put("Droid Sans", Integer.valueOf(1));
/* 100 */       fontDiskNumber = UIUtils.findFont(this.display, fontmap, "255", 81, 57);
/*     */     } 
/*     */ 
/*     */     
/* 104 */     if (fontDiskGraph == null) {
/*     */       
/* 106 */       HashMap<String, Integer> fontmap = new HashMap<String, Integer>();
/*     */       
/* 108 */       fontmap.put("Droid Sans", Integer.valueOf(0));
/* 109 */       fontDiskGraph = UIUtils.findFont(this.display, fontmap, "255", 20, 15);
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
/*     */   public void open(final Display display) {
/* 123 */     this.display = display;
/* 124 */     createContents();
/*     */     
/* 126 */     createDiskStatusItems();
/*     */     
/* 128 */     this.shlDwDrive.setLocation(this.initx, this.inity);
/*     */     
/* 130 */     this.shlDwDrive.open();
/* 131 */     this.shlDwDrive.layout();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     Runnable drivelightoff = new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/* 140 */           if (!DiskWin.this.shlDwDrive.isDisposed()) {
/*     */             
/* 142 */             synchronized (DiskWin.this.driveactivity) {
/*     */               
/* 144 */               if (DiskWin.this.driveactivity.booleanValue() == true)
/*     */               {
/* 146 */                 DiskWin.this.getDiskStatusLED().setImage(MainWin.diskBigLEDdark);
/*     */               }
/*     */ 
/*     */               
/* 150 */               DiskWin.this.driveactivity = Boolean.valueOf(false);
/*     */             } 
/*     */             
/* 153 */             display.timerExec(1000, this);
/*     */           } 
/*     */         }
/*     */       };
/*     */     
/* 158 */     display.timerExec(1000, drivelightoff);
/*     */ 
/*     */     
/* 161 */     while (!this.shlDwDrive.isDisposed()) {
/* 162 */       if (!display.readAndDispatch()) {
/* 163 */         display.sleep();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createDiskStatusItems() {
/* 172 */     this.diskStatusLED = new Label(getCompositeDisk(), 0);
/* 173 */     getDiskStatusLED().setBackground(colorDiskBG);
/* 174 */     getDiskStatusLED().setImage(SWTResourceManager.getImage(MainWin.class, "/disk/diskdrive-leddark-big.png"));
/* 175 */     getDiskStatusLED().setLocation(25, 220);
/* 176 */     getDiskStatusLED().setSize(41, 30);
/*     */     
/* 178 */     this.diskStatusItems = new Vector<DiskStatusItem>();
/*     */     
/* 180 */     this.diskStatusItems.add(new DiskStatusItemSyncTo(this));
/* 181 */     this.diskStatusItems.add(new DiskStatusItemSyncFrom(this));
/* 182 */     this.diskStatusItems.add(new DiskStatusItemReload(this));
/* 183 */     this.diskStatusItems.add(new DiskStatusItemExport(this));
/* 184 */     this.diskStatusItems.add(new DiskStatusItemEject(this));
/* 185 */     this.diskStatusItems.add(new DiskStatusItemWriteProtect(this));
/* 186 */     this.diskStatusItems.add(new DiskStatusItemOffset(this));
/* 187 */     this.diskStatusItems.add(new DiskStatusItemExpand(this));
/* 188 */     this.diskStatusItems.add(new DiskStatusItemLimit(this));
/* 189 */     this.diskStatusItems.add(new DiskStatusItemParams(this));
/*     */     
/* 191 */     for (DiskStatusItem dsi : this.diskStatusItems)
/*     */     {
/* 193 */       dsi.createCanvas(getCompositeDisk());
/*     */     }
/*     */ 
/*     */     
/* 197 */     this.diskGraph = new Canvas(getCompositeDisk(), 0);
/* 198 */     getDiskGraph().setLocation(20, 284);
/* 199 */     getDiskGraph().setSize(410, 52);
/* 200 */     getDiskGraph().setBackground(colorDiskBG);
/*     */ 
/*     */     
/* 203 */     getDiskGraph().addPaintListener(new PaintListener()
/*     */         {
/*     */ 
/*     */           
/*     */           public void paintControl(PaintEvent event)
/*     */           {
/* 209 */             if (DiskWin.this.currentDisk.isLoaded()) {
/*     */               
/* 211 */               if (DiskWin.this.diskGraph.getData("buff") == null)
/*     */               {
/*     */                 
/* 214 */                 DiskWin.this.diskGraph.setData("buff", DiskWin.this.currentDisk.getDiskGraph());
/*     */               }
/*     */               
/* 217 */               event.gc.drawImage((Image)DiskWin.this.diskGraph.getData("buff"), 0, 0);
/*     */ 
/*     */               
/* 220 */               if (DiskWin.this.currentDisk.isGraphchanged()) {
/* 221 */                 DiskWin.this.display.asyncExec(new Runnable()
/*     */                     {
/*     */                       
/*     */                       public void run()
/*     */                       {
/* 226 */                         if (!DiskWin.this.getDiskGraph().isDisposed())
/*     */                         {
/* 228 */                           DiskWin.this.getDiskGraph().setData("buff", DiskWin.this.currentDisk.getDiskGraph());
/* 229 */                           if (!DiskWin.this.currentDisk.isGraphchanged()) {
/* 230 */                             DiskWin.this.getDiskGraph().redraw();
/*     */                           
/*     */                           }
/*     */                         
/*     */                         }
/*     */                       
/*     */                       }
/*     */                     });
/*     */               }
/*     */             }
/*     */             else {
/*     */               
/* 242 */               event.gc.setBackground(DiskWin.colorDiskBG);
/* 243 */               event.gc.fillRectangle(0, 0, 410, 52);
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
/*     */ 
/*     */   
/*     */   protected void createContents() {
/* 259 */     this.shlDwDrive = new Shell(536873056);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 264 */     this.shlDwDrive.setImage(SWTResourceManager.getImage(DiskWin.class, "/dw/dw4square.jpg"));
/* 265 */     this.shlDwDrive.addShellListener((ShellListener)new ShellAdapter()
/*     */         {
/*     */           public void shellClosed(ShellEvent e)
/*     */           {
/* 269 */             MainWin.config.setProperty("DiskWin_" + DiskWin.this.currentDisk.getDriveNo() + "_x", Integer.valueOf((DiskWin.this.shlDwDrive.getLocation()).x));
/* 270 */             MainWin.config.setProperty("DiskWin_" + DiskWin.this.currentDisk.getDriveNo() + "_y", Integer.valueOf((DiskWin.this.shlDwDrive.getLocation()).y));
/* 271 */             MainWin.config.setProperty("DiskWin_" + DiskWin.this.currentDisk.getDriveNo() + "_open", Boolean.valueOf(false));
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 276 */     updateTitlebar();
/*     */     
/* 278 */     this.shlDwDrive.setLayout((Layout)new FillLayout(256));
/*     */     
/* 280 */     this.shlDwDrive.setMinimumSize((background.getImageData()).width, (background.getImageData()).height);
/*     */     
/* 282 */     setCompositeDisk(new Composite((Composite)this.shlDwDrive, 536870912));
/*     */ 
/*     */ 
/*     */     
/* 286 */     Label spacer = new Label(getCompositeDisk(), 0);
/*     */     
/* 288 */     spacer.setBounds((background.getImageData()).width, (background.getImageData()).height, 0, 0);
/*     */ 
/*     */     
/* 291 */     getCompositeDisk().addPaintListener(new PaintListener()
/*     */         {
/*     */ 
/*     */           
/*     */           public void paintControl(PaintEvent e)
/*     */           {
/* 297 */             e.gc.drawImage(DiskWin.background, 0, 0);
/*     */ 
/*     */             
/* 300 */             e.gc.setTextAntialias(1);
/* 301 */             e.gc.setAntialias(1);
/* 302 */             e.gc.setFont(DiskWin.fontDiskNumber);
/*     */             
/* 304 */             if (DiskWin.this.currentDisk.isLoaded()) {
/*     */               
/* 306 */               e.gc.setForeground(DiskWin.colorShadow);
/* 307 */               e.gc.drawText(DiskWin.this.currentDisk.getDriveNo() + "", 75, 138, true);
/*     */             } 
/*     */             
/* 310 */             e.gc.setForeground(DiskWin.colorDiskFG);
/* 311 */             e.gc.drawText(DiskWin.this.currentDisk.getDriveNo() + "", 72, 135, true);
/*     */ 
/*     */ 
/*     */             
/* 315 */             if (DiskWin.this.diskStatusItems != null)
/*     */             {
/* 317 */               for (DiskStatusItem dsi : DiskWin.this.diskStatusItems) {
/*     */ 
/*     */                 
/* 320 */                 dsi.setDisk(DiskWin.this.currentDisk);
/* 321 */                 if (dsi.getCurrentImage() == null) {
/* 322 */                   dsi.setVisible(false);
/*     */                 } else {
/* 324 */                   dsi.setVisible(true);
/*     */                 } 
/* 326 */                 dsi.redraw();
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/* 331 */             if (DiskWin.this.currentDisk.isLoaded()) {
/*     */               
/* 333 */               e.gc.drawImage(SWTResourceManager.getImage(DiskWin.class, "/disk/disk_lever_inserted.png"), 320, 152);
/*     */             }
/*     */             else {
/*     */               
/* 337 */               e.gc.drawImage(SWTResourceManager.getImage(DiskWin.class, "/disk/disk_lever_ejected.png"), 320, 152);
/*     */               
/* 339 */               if (DiskWin.this.diskStatusItems != null)
/*     */               {
/* 341 */                 for (DiskStatusItem dsi : DiskWin.this.diskStatusItems)
/*     */                 {
/*     */                   
/* 344 */                   dsi.setDisk(null);
/*     */                 }
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 352 */     getCompositeDisk().setBackground(colorDiskBG);
/* 353 */     getCompositeDisk().setLayout(null);
/*     */     
/* 355 */     this.comboDiskPath = new Combo(getCompositeDisk(), 0);
/* 356 */     this.comboDiskPath.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 360 */             DiskWin.this.loadDisk(DiskWin.this.comboDiskPath.getText());
/*     */           }
/*     */         });
/*     */     
/* 364 */     this.comboDiskPath.addKeyListener((KeyListener)new KeyAdapter()
/*     */         {
/*     */           public void keyPressed(KeyEvent e) {
/* 367 */             if (e.keyCode == 13) {
/*     */               
/* 369 */               DiskWin.this.loadDisk(DiskWin.this.comboDiskPath.getText());
/*     */             }
/* 371 */             else if (e.keyCode == 16777217 || e.keyCode == 16777218) {
/*     */               
/* 373 */               e.doit = false;
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 378 */     this.comboDiskPath.setBounds(25, 25, 365, 23);
/*     */ 
/*     */     
/* 381 */     updatePathCombo();
/*     */     
/* 383 */     this.btnDiskFile = new Button(getCompositeDisk(), 25165824);
/* 384 */     this.btnDiskFile.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 387 */             MainWin.quickInDisk(DiskWin.this.shlDwDrive, DiskWin.this.currentDisk.getDriveNo());
/*     */           }
/*     */         });
/* 390 */     this.btnDiskFile.setBounds(396, 24, 30, 26);
/* 391 */     this.btnDiskFile.setBackground(colorDiskBG);
/* 392 */     this.btnDiskFile.setText("...");
/* 393 */     this.btnDiskFile.setFocus();
/* 394 */     this.shlDwDrive.pack();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updatePathCombo() {
/* 401 */     this.comboDiskPath.setRedraw(false);
/* 402 */     String spath = UIUtils.shortenLocalURI(this.currentDisk.getPath());
/*     */ 
/*     */     
/* 405 */     this.comboDiskPath.removeAll();
/* 406 */     List<String> dhist = MainWin.getDiskHistory();
/* 407 */     for (String d : dhist)
/*     */     {
/* 409 */       this.comboDiskPath.add(d, 0);
/*     */     }
/*     */     
/* 412 */     if (this.currentDisk.isLoaded()) {
/*     */       
/* 414 */       if (this.comboDiskPath.indexOf(spath) > -1)
/*     */       {
/* 416 */         this.comboDiskPath.select(this.comboDiskPath.indexOf(spath));
/*     */       }
/*     */       else
/*     */       {
/* 420 */         this.comboDiskPath.setText(spath);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 425 */       this.comboDiskPath.setText("");
/*     */     } 
/*     */ 
/*     */     
/* 429 */     if (this.btnDiskFile != null) {
/* 430 */       this.btnDiskFile.setFocus();
/*     */     }
/* 432 */     this.comboDiskPath.setRedraw(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadDisk(String path) {
/* 440 */     List<String> cmds = new ArrayList<String>();
/* 441 */     cmds.add("dw disk insert " + this.currentDisk.getDriveNo() + " " + path);
/*     */     
/* 443 */     sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
/* 444 */     MainWin.addDiskFileToHistory(path);
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
/*     */   void updateTitlebar() {
/*     */     final String title;
/* 458 */     if (this.currentDisk.isLoaded()) {
/* 459 */       title = "DW4 / Drive " + this.currentDisk.getDriveNo() + " / " + this.currentDisk.getFileName();
/*     */     } else {
/* 461 */       title = "DW4 / Drive " + this.currentDisk.getDriveNo() + " / " + "No Disk";
/*     */     } 
/* 463 */     this.display.asyncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 467 */             if (!DiskWin.this.shlDwDrive.isDisposed()) {
/* 468 */               DiskWin.this.shlDwDrive.setText(title);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Label getDiskStatusLED() {
/* 485 */     return this.diskStatusLED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Canvas getDiskGraph() {
/* 494 */     return this.diskGraph;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCompositeDisk(Composite compositeDisk) {
/* 502 */     this.compositeDisk = compositeDisk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Composite getCompositeDisk() {
/* 510 */     return this.compositeDisk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void refreshdisplay() {
/* 518 */     this.compositeDisk.redraw();
/* 519 */     updateTitlebar();
/* 520 */     this.diskGraph.redraw();
/*     */     
/* 522 */     updatePathCombo();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(String key, String val) {
/* 531 */     if (this.displayUpdateItems.contains(key))
/*     */     {
/* 533 */       refreshdisplay();
/*     */     }
/*     */ 
/*     */     
/* 537 */     if (key.equals("_reads") && !val.equals("0")) {
/*     */       
/* 539 */       this.diskStatusLED.setImage(MainWin.diskBigLEDgreen);
/* 540 */       this.driveactivity = Boolean.valueOf(true);
/* 541 */       this.diskGraph.redraw();
/*     */     }
/* 543 */     else if (key.equals("_writes") && !val.equals("0")) {
/*     */       
/* 545 */       this.diskStatusLED.setImage(MainWin.diskBigLEDred);
/* 546 */       this.driveactivity = Boolean.valueOf(true);
/* 547 */       this.diskGraph.redraw();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLocX() {
/* 558 */     return (this.shlDwDrive.getLocation()).x;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocY() {
/* 563 */     return (this.shlDwDrive.getLocation()).y;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 571 */     this.display.syncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 575 */             DiskWin.this.shlDwDrive.close();
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
/*     */   protected void sendCommandDialog(final List<String> cmd, final String title, final String message) {
/* 619 */     final Shell shell = this.shlDwDrive;
/*     */     
/* 621 */     this.display.asyncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 625 */             SendCommandWin win = new SendCommandWin(shell, 2144, cmd, title, message);
/* 626 */             win.open();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Button getBtnDiskFile() {
/* 636 */     return this.btnDiskFile;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */