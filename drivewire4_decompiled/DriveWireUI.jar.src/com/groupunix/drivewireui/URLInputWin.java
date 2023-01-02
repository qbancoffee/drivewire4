/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ 
/*     */ public class URLInputWin
/*     */   extends Dialog {
/*  17 */   protected String result = null;
/*     */ 
/*     */   
/*     */   protected Shell shlEnterURL;
/*     */ 
/*     */   
/*     */   private Combo cmbURL;
/*     */   
/*     */   private int diskno;
/*     */ 
/*     */   
/*     */   public URLInputWin(Shell parent, int diskno) {
/*  29 */     super(parent, 2144);
/*  30 */     this.diskno = diskno;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String open() {
/*  38 */     createContents();
/*     */     
/*  40 */     loadURLHistory();
/*     */     
/*  42 */     this.shlEnterURL.open();
/*  43 */     this.shlEnterURL.layout();
/*  44 */     Display display = getParent().getDisplay();
/*     */     
/*  46 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shlEnterURL.getBounds()).width / 2;
/*  47 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shlEnterURL.getBounds()).height / 2;
/*     */     
/*  49 */     this.shlEnterURL.setLocation(x, y);
/*     */     
/*  51 */     while (!this.shlEnterURL.isDisposed()) {
/*  52 */       if (!display.readAndDispatch()) {
/*  53 */         display.sleep();
/*     */       }
/*     */     } 
/*  56 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadURLHistory() {
/*  63 */     List<String> dhist = MainWin.getDiskHistory();
/*  64 */     for (String d : dhist)
/*     */     {
/*  66 */       this.cmbURL.add(d, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  74 */     this.shlEnterURL = new Shell(getParent(), getStyle());
/*  75 */     this.shlEnterURL.setSize(452, 151);
/*  76 */     this.shlEnterURL.setText("Enter URL for disk image...");
/*     */     
/*  78 */     Button btnOk = new Button((Composite)this.shlEnterURL, 0);
/*  79 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  83 */             URLInputWin.this.result = URLInputWin.this.cmbURL.getText();
/*  84 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*  87 */     btnOk.setBounds(248, 88, 82, 25);
/*  88 */     btnOk.setText("Ok");
/*     */     
/*  90 */     Button btnCancel = new Button((Composite)this.shlEnterURL, 0);
/*  91 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  95 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/*  99 */     btnCancel.setBounds(350, 88, 75, 25);
/* 100 */     btnCancel.setText("Cancel");
/*     */     
/* 102 */     this.cmbURL = new Combo((Composite)this.shlEnterURL, 0);
/*     */     
/* 104 */     this.cmbURL.setBounds(22, 40, 403, 23);
/*     */     
/* 106 */     Label lblEnterURL = new Label((Composite)this.shlEnterURL, 0);
/* 107 */     lblEnterURL.setBounds(22, 19, 275, 15);
/* 108 */     lblEnterURL.setText("Enter a URL to load image for drive " + this.diskno + " from:");
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/URLInputWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */