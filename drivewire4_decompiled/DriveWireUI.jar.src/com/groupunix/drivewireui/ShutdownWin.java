/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ShutdownWin
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlShuttingDown;
/*     */   protected Label lblPleaseWaitA;
/*     */   protected Label lblStatus;
/*     */   protected ProgressBar progressBar;
/*     */   private int x;
/*     */   private int y;
/*     */   private Shell parent;
/*     */   
/*     */   public ShutdownWin(Shell parent, int style) {
/*  24 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/*  31 */     createContents();
/*  32 */     this.x = (this.parent.getLocation()).x + (this.parent.getSize()).x / 2 - 180;
/*  33 */     this.y = (this.parent.getLocation()).y + (this.parent.getSize()).y / 2 - 90;
/*  34 */     this.shlShuttingDown.setLocation(this.x, this.y);
/*  35 */     this.shlShuttingDown.open();
/*  36 */     this.shlShuttingDown.layout();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  47 */     this.shlShuttingDown = new Shell(Display.getCurrent(), 2144);
/*  48 */     this.shlShuttingDown.setSize(369, 181);
/*  49 */     this.shlShuttingDown.setText("Shutting down...");
/*     */     
/*  51 */     this.lblPleaseWaitA = new Label((Composite)this.shlShuttingDown, 64);
/*  52 */     this.lblPleaseWaitA.setBounds(10, 21, 343, 36);
/*  53 */     this.lblPleaseWaitA.setText("Please wait a moment for DriveWire to save the configuration and shutdown cleanly.");
/*     */     
/*  55 */     this.progressBar = new ProgressBar((Composite)this.shlShuttingDown, 65536);
/*  56 */     this.progressBar.setBounds(31, 81, 295, 17);
/*  57 */     this.progressBar.setMinimum(0);
/*  58 */     this.progressBar.setMaximum(100);
/*  59 */     this.progressBar.setSelection(0);
/*     */ 
/*     */     
/*  62 */     this.lblStatus = new Label((Composite)this.shlShuttingDown, 64);
/*  63 */     this.lblStatus.setBounds(31, 110, 295, 36);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(String txt, int prog) {
/*  73 */     if (this.progressBar != null && !this.progressBar.isDisposed())
/*     */     {
/*  75 */       setProgress(prog);
/*     */     }
/*     */ 
/*     */     
/*  79 */     if (this.lblStatus != null && !this.lblStatus.isDisposed()) {
/*     */       
/*  81 */       this.lblStatus.setText(txt);
/*     */     }
/*     */     else {
/*     */       
/*  85 */       System.out.println("shutdown: " + txt + " (" + prog + ")");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProgress(int i) {
/*  94 */     if (this.progressBar != null && !this.progressBar.isDisposed())
/*     */     {
/*  96 */       if (this.progressBar.getSelection() != i) {
/*     */         
/*  98 */         this.progressBar.setSelection(i);
/*     */         
/* 100 */         this.progressBar.redraw();
/* 101 */         this.lblPleaseWaitA.redraw();
/* 102 */         this.lblStatus.redraw();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ShutdownWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */