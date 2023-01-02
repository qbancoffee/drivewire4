/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ import java.util.Random;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
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
/*     */ public class ErrorWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlAnErrorHas;
/*     */   private Text txtSummary;
/*     */   private Button btnClose;
/*     */   private String title;
/*     */   private String summary;
/*     */   private String detail;
/*     */   private Text textDetail;
/*     */   
/*     */   public ErrorWin(Shell parent, int style, String title, String summary, String detail) {
/*  39 */     super(parent, style);
/*  40 */     this.title = title;
/*  41 */     this.summary = summary;
/*  42 */     this.detail = detail;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ErrorWin(Shell parent, int style, DWError dwerror) {
/*  48 */     super(parent, style);
/*  49 */     this.title = dwerror.getTitle();
/*  50 */     this.summary = dwerror.getSummary();
/*  51 */     this.detail = dwerror.getDetail();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  60 */     createContents();
/*     */     
/*  62 */     Display display = getParent().getDisplay();
/*     */     
/*  64 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shlAnErrorHas.getBounds()).width / 2;
/*  65 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shlAnErrorHas.getBounds()).height / 2;
/*     */     
/*  67 */     this.shlAnErrorHas.setLocation(x, y);
/*     */     
/*  69 */     this.shlAnErrorHas.open();
/*  70 */     this.shlAnErrorHas.layout();
/*     */     
/*  72 */     while (!this.shlAnErrorHas.isDisposed()) {
/*  73 */       if (!display.readAndDispatch()) {
/*  74 */         display.sleep();
/*     */       }
/*     */     } 
/*  77 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  87 */     this.shlAnErrorHas = new Shell(getParent(), getStyle());
/*  88 */     this.shlAnErrorHas.setSize(419, 274);
/*  89 */     this.shlAnErrorHas.setText(this.title);
/*     */ 
/*     */ 
/*     */     
/*  93 */     this.btnClose = new Button((Composite)this.shlAnErrorHas, 0);
/*  94 */     this.btnClose.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  99 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/* 103 */     this.btnClose.setBounds(327, 211, 75, 25);
/* 104 */     this.btnClose.setText("Close");
/*     */ 
/*     */ 
/*     */     
/* 108 */     this.txtSummary = new Text((Composite)this.shlAnErrorHas, 72);
/* 109 */     this.txtSummary.setBackground(SWTResourceManager.getColor(22));
/* 110 */     this.txtSummary.setEditable(false);
/* 111 */     this.txtSummary.setBounds(10, 20, 329, 42);
/* 112 */     this.txtSummary.setText(this.summary);
/*     */     
/* 114 */     this.textDetail = new Text((Composite)this.shlAnErrorHas, 2632);
/* 115 */     this.textDetail.setEditable(false);
/* 116 */     this.textDetail.setBounds(10, 67, 392, 127);
/* 117 */     this.textDetail.setText(this.detail);
/*     */     
/* 119 */     Button btnSubmitABug = new Button((Composite)this.shlAnErrorHas, 0);
/* 120 */     btnSubmitABug.setImage(SWTResourceManager.getImage(ErrorWin.class, "/menu/bug.png"));
/* 121 */     btnSubmitABug.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 125 */             BugReportWin brwin = new BugReportWin(ErrorWin.this.shlAnErrorHas, 2144, ErrorWin.this.title, ErrorWin.this.summary, ErrorWin.this.detail);
/* 126 */             brwin.open();
/*     */           }
/*     */         });
/*     */     
/* 130 */     btnSubmitABug.setBounds(10, 211, 185, 25);
/* 131 */     btnSubmitABug.setText("Submit a bug report...");
/*     */     
/* 133 */     Label lblNewLabel = new Label((Composite)this.shlAnErrorHas, 0);
/* 134 */     Random rand = new Random();
/* 135 */     lblNewLabel.setImage(SWTResourceManager.getImage(ErrorWin.class, "/animals/a" + rand.nextInt(22) + ".png"));
/* 136 */     lblNewLabel.setBounds(353, 10, 48, 48);
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ErrorWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */