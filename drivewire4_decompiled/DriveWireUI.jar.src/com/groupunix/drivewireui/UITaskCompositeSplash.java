/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ 
/*     */ 
/*     */ public class UITaskCompositeSplash
/*     */   extends UITaskComposite
/*     */ {
/*     */   private Image logoi;
/*     */   private Canvas logoc;
/*     */   private ImageData logodat;
/*     */   
/*     */   public UITaskCompositeSplash(Composite master, int style, int tid) {
/*  21 */     super(master, style, tid);
/*     */     
/*  23 */     setData("splash", "indeed");
/*     */     
/*  25 */     createContents(master);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents(Composite master) {
/*  35 */     this.status.dispose();
/*     */     
/*  37 */     setBackground(MainWin.colorWhite);
/*     */ 
/*     */     
/*  40 */     this.logodat = SWTResourceManager.getImage(MainWin.class, "/dw/anim0.png").getImageData();
/*     */     
/*  42 */     this.logodat.alpha = 0;
/*     */     
/*  44 */     this.logoi = new Image((Device)getDisplay(), this.logodat);
/*  45 */     this.logoc = new Canvas(this, 536870912);
/*  46 */     this.logoc.setBackground(MainWin.colorWhite);
/*     */     
/*  48 */     setBounds(0, 0, (master.getClientArea()).width, this.logodat.height + 40);
/*  49 */     this.logoc.setBounds((getClientArea()).width / 2 - this.logodat.width / 2, (getClientArea()).height / 2 - this.logodat.height / 2, this.logodat.width, this.logodat.height);
/*     */ 
/*     */     
/*  52 */     this.logoc.addPaintListener(new PaintListener()
/*     */         {
/*     */           public void paintControl(PaintEvent e) {
/*  55 */             e.gc.drawImage(UITaskCompositeSplash.this.logoi, 0, 0);
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/*  61 */     this.details.setBounds((this.logoc.getBounds()).x, this.logodat.height + 12, (this.logoc.getBounds()).width - 10, this.logodat.height + 28);
/*     */     
/*  63 */     this.details.setAlignment(131072);
/*  64 */     this.details.setVisible(false);
/*     */     
/*  66 */     this.details.setFont(UITaskMaster.versionFont);
/*  67 */     this.details.setForeground(MainWin.colorCmdTxt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showVer() {
/*  74 */     this.details.setVisible(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  80 */     return (getBounds()).height;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(int stat) {}
/*     */ 
/*     */   
/*     */   public void setDetails(String text) {
/*  89 */     this.det = text;
/*  90 */     this.details.setText(this.det.trim());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTop(int y) {
/*  96 */     setBounds(0, y, (getBounds()).width, (getBounds()).height);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBottom(int y) {
/* 101 */     setBounds(0, (getBounds()).y, (getBounds()).width, y - (getBounds()).y);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean doAnim() {
/* 107 */     if (this.logodat.alpha < 255) {
/*     */       
/* 109 */       this.logodat.alpha = Math.min(255, this.logodat.alpha + 5);
/* 110 */       this.logoi = new Image((Device)getDisplay(), this.logodat);
/* 111 */       this.logoc.redraw();
/*     */       
/* 113 */       return true;
/*     */     } 
/*     */     
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doAnim2(int i) {
/* 122 */     this.logodat = SWTResourceManager.getImage(MainWin.class, "/dw/anim" + i + ".png").getImageData();
/* 123 */     this.logoi = new Image((Device)getDisplay(), this.logodat);
/* 124 */     this.logoc.redraw();
/* 125 */     update();
/*     */ 
/*     */     
/*     */     try {
/* 129 */       Thread.sleep(15L);
/* 130 */     } catch (InterruptedException e) {}
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UITaskCompositeSplash.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */