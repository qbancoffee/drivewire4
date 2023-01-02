/*     */ package com.groupunix.drivewireui;
/*     */ 
import com.swtdesigner.SWTResourceManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.vfs.FileObject;
/*     */ import org.apache.commons.vfs.FileSystemException;
/*     */ import org.apache.commons.vfs.VFS;
/*     */ import org.eclipse.swt.browser.Browser;
/*     */ import org.eclipse.swt.browser.LocationAdapter;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.eclipse.swt.widgets.ToolBar;
/*     */ import org.eclipse.swt.widgets.ToolItem;
/*     */ import swing2swt.layout.BorderLayout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DWBrowser
/*     */   extends Composite
/*     */ {
/*     */   public static final int LTYPE_LOCAL_ROOT = 0;
/*     */   public static final int LTYPE_LOCAL_FOLDER = 1;
/*     */   public static final int LTYPE_LOCAL_ENTRY = 2;
/*     */   public static final int LTYPE_NET_ROOT = 10;
/*     */   public static final int LTYPE_NET_FOLDER = 11;
/*     */   public static final int LTYPE_NET_ENTRY = 12;
/*     */   public static final int LTYPE_CLOUD_ROOT = 20;
/*     */   public static final int LTYPE_CLOUD_FOLDER = 21;
/*     */   public static final int LTYPE_CLOUD_ENTRY = 22;
/*     */   private Browser browser;
/*     */   private Composite header;
/*     */   private ToolItem tltmBack;
/*     */   private ToolItem tltmForward;
/*     */   private ToolItem tltmReload;
/*     */   private Combo comboURL;
/*     */   private Spinner spinnerDrive;
/*     */   private Canvas canvas;
/*     */   
/*     */   public DWBrowser(final CTabItem comp, String url) {
/*  68 */     super((Composite)comp.getParent(), 0);
/*     */ 
/*     */     
/*  71 */     setLayout((Layout)new BorderLayout(0, 0));
/*     */ 
/*     */ 
/*     */     
/*  75 */     this.header = new Composite(this, 0);
/*  76 */     this.header.setLayoutData("North");
/*  77 */     this.header.setLayout((Layout)new FormLayout());
/*     */     
/*  79 */     GradientHelper.applyVerticalGradientBG(this.header, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*     */     
/*  81 */     this.header.addListener(11, new Listener()
/*     */         {
/*     */           
/*     */           public void handleEvent(Event event)
/*     */           {
/*  86 */             GradientHelper.applyVerticalGradientBG(DWBrowser.this.header, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*  91 */     this.header.setBackgroundMode(2);
/*     */     
/*  93 */     ToolBar toolBar = new ToolBar(this.header, 8519680);
/*     */     
/*  95 */     FormData fd_toolBar = new FormData();
/*  96 */     fd_toolBar.top = new FormAttachment(0, 5);
/*  97 */     fd_toolBar.left = new FormAttachment(1, 5);
/*  98 */     toolBar.setLayoutData(fd_toolBar);
/*     */     
/* 100 */     this.tltmBack = new ToolItem(toolBar, 0);
/* 101 */     this.tltmBack.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 104 */             DWBrowser.this.browser.back();
/*     */           }
/*     */         });
/* 107 */     this.tltmBack.setWidth(30);
/* 108 */     this.tltmBack.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-left-3.png"));
/*     */     
/* 110 */     this.tltmForward = new ToolItem(toolBar, 0);
/* 111 */     this.tltmForward.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 114 */             DWBrowser.this.browser.forward();
/*     */           }
/*     */         });
/* 117 */     this.tltmForward.setWidth(30);
/* 118 */     this.tltmForward.setImage(SWTResourceManager.getImage(DWBrowser.class, "/toolbar/arrow-right-3.png"));
/*     */     
/* 120 */     this.tltmReload = new ToolItem(toolBar, 0);
/* 121 */     this.tltmReload.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 124 */             DWBrowser.this.browser.refresh();
/*     */           }
/*     */         });
/* 127 */     this.tltmReload.setWidth(30);
/* 128 */     this.tltmReload.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/view-refresh-7.png"));
/*     */     
/* 130 */     this.comboURL = new Combo(this.header, 0);
/* 131 */     this.comboURL.setBackground(new Color((Device)MainWin.getDisplay(), 255, 255, 255));
/* 132 */     this.comboURL.addKeyListener((KeyListener)new KeyAdapter()
/*     */         {
/*     */           public void keyPressed(KeyEvent e) {
/* 135 */             if (e.keyCode == 13) {
/*     */               
/* 137 */               DWBrowser.this.browser.setUrl(DWBrowser.this.comboURL.getText());
/*     */             }
/* 139 */             else if (e.keyCode == 16777217 || e.keyCode == 16777218) {
/*     */               
/* 141 */               e.doit = false;
/*     */             } 
/*     */           }
/*     */         });
/* 145 */     this.comboURL.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 149 */             DWBrowser.this.browser.setUrl(DWBrowser.this.comboURL.getText());
/*     */           }
/*     */         });
/* 152 */     fd_toolBar.right = new FormAttachment((Control)this.comboURL, -6);
/*     */     
/* 154 */     FormData fd_comboURL = new FormData();
/* 155 */     fd_comboURL.left = new FormAttachment(0, 90);
/* 156 */     fd_comboURL.right = new FormAttachment(100, -90);
/* 157 */     fd_comboURL.bottom = new FormAttachment(100, -5);
/* 158 */     fd_comboURL.top = new FormAttachment(2, 5);
/* 159 */     this.comboURL.setLayoutData(fd_comboURL);
/*     */     
/* 161 */     this.canvas = new Canvas(this.header, 0);
/* 162 */     FormData fd_canvas = new FormData();
/* 163 */     fd_canvas.left = new FormAttachment(100, -75);
/* 164 */     fd_canvas.right = new FormAttachment(100, -55);
/* 165 */     fd_canvas.top = new FormAttachment(2, 6);
/* 166 */     fd_canvas.bottom = new FormAttachment(2, 28);
/* 167 */     this.canvas.setLayoutData(fd_canvas);
/*     */     
/* 169 */     this.spinnerDrive = new Spinner(this.header, 2048);
/* 170 */     this.spinnerDrive.setBackground(new Color((Device)MainWin.getDisplay(), 255, 255, 255));
/* 171 */     this.spinnerDrive.setToolTipText("Working drive");
/* 172 */     FormData fd_spinnerDrive = new FormData();
/* 173 */     fd_spinnerDrive.bottom = new FormAttachment(100, -5);
/* 174 */     fd_spinnerDrive.right = new FormAttachment(100, -5);
/* 175 */     fd_spinnerDrive.left = new FormAttachment(100, -50);
/* 176 */     this.spinnerDrive.setLayoutData(fd_spinnerDrive);
/* 177 */     this.spinnerDrive.setMaximum(255);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 182 */     this.browser = new Browser(this, 0);
/*     */ 
/*     */     
/* 185 */     this.browser.addLocationListener((LocationListener)new LocationAdapter()
/*     */         {
/*     */           public void changed(LocationEvent event) {
/* 188 */             DWBrowser.this.comboURL.setText(event.location);
/*     */ 
/*     */ 
/*     */             
/* 192 */             if (DWBrowser.this.browser.isBackEnabled()) {
/* 193 */               DWBrowser.this.tltmBack.setEnabled(true);
/*     */             } else {
/* 195 */               DWBrowser.this.tltmBack.setEnabled(false);
/*     */             } 
/* 197 */             if (DWBrowser.this.browser.isForwardEnabled()) {
/* 198 */               DWBrowser.this.tltmForward.setEnabled(true);
/*     */             } else {
/* 200 */               DWBrowser.this.tltmForward.setEnabled(false);
/*     */             } 
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void changing(LocationEvent event) {
/* 209 */             if (DWBrowser.this.isCocoLink(event.location)) {
/*     */               
/* 211 */               event.doit = false;
/*     */               
/* 213 */               DWBrowser.this.doCoCoLink(event.location);
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 218 */     this.browser.addTitleListener(new TitleListener() {
/*     */           public void changed(TitleEvent event) {
/* 220 */             comp.setText(event.title);
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 226 */     if (url != null) {
/*     */       
/* 228 */       this.browser.setUrl(url);
/*     */     }
/*     */     else {
/*     */       
/* 232 */       this.browser.setUrl(MainWin.config.getString("Browser_homepage", "http://cococoding.com/cloud"));
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
/*     */ 
/*     */   
/*     */   protected void doCoCoLink(String url) {
/* 249 */     String filename = getFilename(url);
/*     */     
/* 251 */     if (filename != null) {
/*     */       
/* 253 */       String extension = getExtension(filename);
/*     */       
/* 255 */       if (extension != null)
/*     */       {
/*     */         
/* 258 */         if (isExtension("Archive", extension)) {
/*     */           
/*     */           try
/*     */           {
/*     */             
/* 263 */             FileObject fileobj = VFS.getManager().resolveFile("zip:" + url + "!/");
/*     */             
/* 265 */             int disk = 0;
/* 266 */             int dos = 0;
/* 267 */             int os9 = 0;
/*     */             
/* 269 */             if (fileobj.exists() && fileobj.isReadable())
/*     */             {
/* 271 */               for (FileObject f : fileobj.getChildren()) {
/*     */                 
/* 273 */                 String ext = getExtension(f.getName().getURI());
/*     */                 
/* 275 */                 if (isExtension("Disk", ext)) {
/* 276 */                   disk++;
/*     */                 }
/* 278 */                 if (isExtension("DOS", ext)) {
/* 279 */                   dos++;
/*     */                 }
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 287 */               if (disk + dos + os9 == 0) {
/*     */                 
/* 289 */                 showError("No usable files found", "The archive does not contain any files with known extensions.");
/*     */               }
/* 291 */               else if (disk > 0 && dos == 0 && os9 == 0) {
/*     */                 
/* 293 */                 if (disk > 1) {
/*     */ 
/*     */                   
/* 296 */                   MessageBox messageBox = new MessageBox(MainWin.getDisplay().getActiveShell(), 196);
/* 297 */                   messageBox.setMessage("There are " + disk + " images in this archive.  Would you like to load them into drives " + this.spinnerDrive.getSelection() + " through " + (this.spinnerDrive.getSelection() + disk - 1) + "?");
/* 298 */                   messageBox.setText("Multiple disk images found");
/* 299 */                   int rc = messageBox.open();
/*     */                   
/* 301 */                   if (rc == 64)
/*     */                   {
/* 303 */                     List<String> cmds = new ArrayList<String>();
/* 304 */                     int off = 0;
/*     */                     
/* 306 */                     for (FileObject f : fileobj.getChildren()) {
/*     */ 
/*     */                       
/* 309 */                       String ext = getExtension(f.getName().getURI());
/*     */                       
/* 311 */                       if (isExtension("Disk", ext)) {
/*     */                         
/* 313 */                         cmds.add("dw disk insert " + (this.spinnerDrive.getSelection() + off) + " " + f.getName().getURI());
/* 314 */                         off++;
/*     */                       } 
/*     */                     } 
/*     */ 
/*     */ 
/*     */                     
/* 320 */                     sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
/*     */                   
/*     */                   }
/*     */                 
/*     */                 }
/*     */                 else {
/*     */                   
/* 327 */                   for (FileObject f : fileobj.getChildren())
/*     */                   {
/* 329 */                     String ext = getExtension(f.getName().getURI());
/*     */                     
/* 331 */                     if (isExtension("Disk", ext))
/*     */                     {
/* 333 */                       List<String> cmds = new ArrayList<String>();
/* 334 */                       cmds.add("dw disk insert " + this.spinnerDrive.getSelection() + " " + f.getName().getURI());
/*     */                       
/* 336 */                       sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
/*     */                     }
/*     */                   
/*     */                   }
/*     */                 
/*     */                 } 
/* 342 */               } else if (disk == 0 && dos > 0 && os9 == 0) {
/*     */ 
/*     */ 
/*     */                 
/* 346 */                 String title = "Appending file(s) to image...";
/* 347 */                 String msg = "Please wait while the server appends to the image in drive " + this.spinnerDrive.getSelection() + ".";
/* 348 */                 List<String> cmds = new ArrayList<String>();
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
/* 360 */                 for (FileObject f : fileobj.getChildren()) {
/*     */                   
/* 362 */                   String ext = getExtension(f.getName().getURI());
/*     */                   
/* 364 */                   if (isExtension("DOS", ext))
/*     */                   {
/* 366 */                     cmds.add("dw disk dos add " + this.spinnerDrive.getSelection() + " " + f.getName().getURI());
/*     */                   }
/*     */                 } 
/*     */ 
/*     */                 
/* 371 */                 sendCommandDialog(cmds, title, msg);
/*     */               
/*     */               }
/*     */ 
/*     */             
/*     */             }
/*     */           
/*     */           }
/* 379 */           catch (FileSystemException e)
/*     */           {
/*     */             
/* 382 */             e.printStackTrace();
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/* 388 */         else if (isExtension("Disk", extension)) {
/*     */           
/* 390 */           List<String> cmds = new ArrayList<String>();
/* 391 */           cmds.add("dw disk insert " + this.spinnerDrive.getSelection() + " " + url);
/*     */           
/* 393 */           sendCommandDialog(cmds, "Loading disk image..", "Please wait while the server loads the disk image.");
/*     */ 
/*     */         
/*     */         }
/* 397 */         else if (isExtension("DOS", extension)) {
/*     */           
/* 399 */           String title = "Appending file to image...";
/* 400 */           String msg = "Please wait while the server appends the file to the image in drive " + this.spinnerDrive.getSelection() + ".";
/* 401 */           List<String> cmds = new ArrayList<String>();
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
/* 413 */           cmds.add("dw disk dos add " + this.spinnerDrive.getSelection() + " " + url);
/*     */           
/* 415 */           sendCommandDialog(cmds, title, msg);
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
/*     */   
/*     */   private void showError(String title, String msg) {
/* 428 */     MessageBox messageBox = new MessageBox(MainWin.getDisplay().getActiveShell(), 33);
/* 429 */     messageBox.setMessage(msg);
/* 430 */     messageBox.setText(title);
/* 431 */     messageBox.open();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isCocoLink(String location) {
/* 440 */     String filename = getFilename(location);
/*     */     
/* 442 */     if (filename != null) {
/*     */       
/* 444 */       String extension = getExtension(filename);
/*     */       
/* 446 */       if (extension != null && isCoCoExtension(extension))
/*     */       {
/* 448 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 453 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String getExtension(String filename) {
/* 459 */     String ext = null;
/*     */     
/* 461 */     int lastdot = filename.lastIndexOf('.');
/*     */ 
/*     */     
/* 464 */     if (lastdot > 0 && lastdot < filename.length() - 2)
/*     */     {
/* 466 */       ext = filename.substring(lastdot + 1);
/*     */     }
/*     */     
/* 469 */     return ext;
/*     */   }
/*     */ 
/*     */   
/*     */   private String getFilename(String location) {
/* 474 */     String filename = null;
/*     */     
/* 476 */     int lastslash = location.lastIndexOf('/');
/*     */ 
/*     */     
/* 479 */     if (lastslash > 0 && lastslash < location.length() - 2)
/*     */     {
/* 481 */       filename = location.substring(lastslash + 1);
/*     */     }
/*     */     
/* 484 */     return filename;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isCoCoExtension(String ext) {
/* 493 */     if (isExtension("Disk", ext)) {
/* 494 */       return true;
/*     */     }
/* 496 */     if (isExtension("DOS", ext)) {
/* 497 */       return true;
/*     */     }
/* 499 */     if (isExtension("OS9", ext)) {
/* 500 */       return true;
/*     */     }
/* 502 */     if (isExtension("Archive", ext)) {
/* 503 */       return true;
/*     */     }
/* 505 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isExtension(String exttype, String ext) {
/* 513 */     if (MainWin.config.containsKey(exttype + "Extensions")) {
/*     */ 
/*     */       
/* 516 */       List<String> exts = MainWin.config.getList(exttype + "Extensions");
/*     */       
/* 518 */       if (exts.contains(ext.toLowerCase()))
/* 519 */         return true; 
/*     */     } 
/* 521 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void checkSubclass() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sendCommandDialog(final List<String> cmd, final String title, final String message) {
/* 534 */     final Shell shell = MainWin.getDisplay().getActiveShell();
/*     */     
/* 536 */     getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 540 */             SendCommandWin win = new SendCommandWin(shell, 2144, cmd, title, message);
/* 541 */             win.open();
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWBrowser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */