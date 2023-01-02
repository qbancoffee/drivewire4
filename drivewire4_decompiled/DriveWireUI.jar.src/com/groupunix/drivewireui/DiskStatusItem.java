/*     */ package com.groupunix.drivewireui;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseTrackAdapter;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.events.MouseTrackListener;
/*     */ 
/*     */ public abstract class DiskStatusItem {
/*  15 */   private int x = 0;
/*  16 */   private int y = 0;
/*  17 */   private int width = 0;
/*  18 */   private int height = 0;
/*     */   
/*     */   private String disabledImage;
/*     */   private String enabledImage;
/*     */   private String clickImage;
/*     */   private boolean button = false;
/*  24 */   private String hovertext = "No hover text";
/*     */   
/*     */   private boolean mouseDown = false;
/*     */   
/*     */   private boolean enabled = false;
/*     */   
/*     */   protected DiskWin diskwin;
/*     */   protected Canvas canvas;
/*     */   
/*     */   public DiskStatusItem(DiskWin diskwin) {
/*  34 */     this.diskwin = diskwin;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void setDisk(DiskDef paramDiskDef);
/*     */ 
/*     */   
/*     */   public abstract void handleClick();
/*     */   
/*     */   public void setX(int x) {
/*  44 */     this.x = x;
/*     */   }
/*     */   
/*     */   public int getX() {
/*  48 */     return this.x;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setY(int y) {
/*  53 */     this.y = y;
/*     */   }
/*     */   
/*     */   public int getY() {
/*  57 */     return this.y;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setButton(boolean button) {
/*  62 */     this.button = button;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isButton() {
/*  67 */     return this.button;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDisabledImage(String disabledImage) {
/*  72 */     this.disabledImage = disabledImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisabledImage() {
/*  77 */     return this.disabledImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnabledImage(String enabledImage) {
/*  82 */     this.enabledImage = enabledImage;
/*     */   }
/*     */   
/*     */   public String getEnabledImage() {
/*  86 */     return this.enabledImage;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClickImage(String clickImage) {
/*  91 */     this.clickImage = clickImage;
/*     */   }
/*     */   
/*     */   public String getClickImage() {
/*  95 */     return this.clickImage;
/*     */   }
/*     */   
/*     */   public void setWidth(int width) {
/*  99 */     this.width = width;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 103 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setHeight(int height) {
/* 107 */     this.height = height;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 111 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHovertext(String hovertext) {
/* 115 */     this.hovertext = hovertext;
/*     */   }
/*     */   
/*     */   public String getHovertext() {
/* 119 */     return this.hovertext;
/*     */   }
/*     */   
/*     */   public void setMouseDown(boolean b) {
/* 123 */     this.mouseDown = b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMouseDown() {
/* 129 */     return this.mouseDown;
/*     */   }
/*     */ 
/*     */   
/*     */   public void createCanvas(final Composite composite) {
/* 134 */     this.canvas = new Canvas(composite, 0);
/*     */     
/* 136 */     this.canvas.setBackground(DiskWin.colorDiskBG);
/* 137 */     this.canvas.setLocation(getX(), getY());
/* 138 */     this.canvas.setSize(getWidth(), getHeight());
/*     */     
/* 140 */     this.canvas.addPaintListener(new PaintListener()
/*     */         {
/*     */           public void paintControl(PaintEvent event)
/*     */           {
/* 144 */             if (DiskStatusItem.this.getCurrentImage() != null) {
/* 145 */               event.gc.drawImage(SWTResourceManager.getImage(MainWin.class, DiskStatusItem.this.getCurrentImage()), 0, 0);
/*     */             }
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 152 */     this.canvas.addMouseListener((MouseListener)new MouseAdapter()
/*     */         {
/*     */           
/*     */           public void mouseDown(MouseEvent e)
/*     */           {
/* 157 */             if (DiskStatusItem.this.isButton())
/*     */             {
/* 159 */               DiskStatusItem.this.canvas.setLocation(DiskStatusItem.this.getX() + 2, DiskStatusItem.this.getY() + 2);
/*     */             }
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void mouseUp(MouseEvent e) {
/* 166 */             if (DiskStatusItem.this.isButton()) {
/*     */               
/* 168 */               DiskStatusItem.this.canvas.setLocation(DiskStatusItem.this.getX(), DiskStatusItem.this.getY());
/* 169 */               composite.setCursor(new Cursor((Device)composite.getDisplay(), 0));
/*     */               
/* 171 */               DiskStatusItem.this.handleClick();
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     this.canvas.addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter()
/*     */         {
/*     */           
/*     */           public void mouseEnter(MouseEvent e)
/*     */           {
/* 185 */             if (DiskStatusItem.this.isButton())
/*     */             {
/* 187 */               composite.setCursor(new Cursor((Device)composite.getDisplay(), 21));
/*     */             }
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void mouseExit(MouseEvent e) {
/* 195 */             if (DiskStatusItem.this.isButton())
/*     */             {
/* 197 */               composite.setCursor(new Cursor((Device)composite.getDisplay(), 0));
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
/*     */           public void mouseHover(MouseEvent e) {}
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
/*     */   public String getCurrentImage() {
/* 220 */     if (this.enabled) {
/* 221 */       return getEnabledImage();
/*     */     }
/* 223 */     return getDisabledImage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnabled(boolean b) {
/* 230 */     this.enabled = b;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled() {
/* 235 */     return this.enabled;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void redraw() {
/* 241 */     if (this.canvas != null && !this.canvas.isDisposed()) {
/* 242 */       this.canvas.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setVisible(boolean b) {
/* 247 */     this.canvas.setVisible(b);
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskStatusItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */