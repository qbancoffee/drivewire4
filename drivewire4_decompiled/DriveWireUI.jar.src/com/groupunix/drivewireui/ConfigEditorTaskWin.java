/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigEditorTaskWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shell;
/*     */   private Label lblMessage;
/*     */   private ProgressBar progressBar;
/*     */   private Label labelStatus;
/*     */   private String title;
/*     */   private Button btnOk;
/*     */   private String message;
/*     */   
/*     */   public ConfigEditorTaskWin(Shell parent, int style, String title, String message) {
/*  31 */     super(parent, style);
/*  32 */     this.title = title;
/*  33 */     this.message = message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  40 */     createContents();
/*  41 */     this.btnOk.setVisible(false);
/*     */     
/*  43 */     Rectangle pos = Display.getCurrent().getActiveShell().getBounds();
/*     */     
/*  45 */     this.shell.setBounds(pos.x + pos.width / 2 - 204, pos.y + pos.height / 2 - 76, 408, 152);
/*     */     
/*  47 */     this.shell.open();
/*  48 */     this.shell.layout();
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
/*  60 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setErrorStatus(final String err) {
/*  66 */     this.shell.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/*  70 */             ConfigEditorTaskWin.this.labelStatus.setVisible(false);
/*  71 */             ConfigEditorTaskWin.this.progressBar.setVisible(false);
/*  72 */             ConfigEditorTaskWin.this.lblMessage.setText(err);
/*  73 */             ConfigEditorTaskWin.this.shell.setText("Error while sending commands");
/*  74 */             ConfigEditorTaskWin.this.btnOk.setVisible(true);
/*  75 */             ConfigEditorTaskWin.this.shell.redraw();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(final String msg, final int progress) {
/*  83 */     this.shell.getDisplay().syncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/*  87 */             ConfigEditorTaskWin.this.progressBar.setSelection(ConfigEditorTaskWin.this.progressBar.getSelection() + progress);
/*  88 */             if (msg != null) {
/*  89 */               ConfigEditorTaskWin.this.labelStatus.setText(msg);
/*     */             }
/*  91 */             ConfigEditorTaskWin.this.shell.redraw();
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
/*     */   private void createContents() {
/* 103 */     this.shell = new Shell(getParent(), getStyle());
/* 104 */     this.shell.setSize(408, 165);
/* 105 */     this.shell.setText(this.title);
/*     */     
/* 107 */     this.btnOk = new Button((Composite)this.shell, 0);
/* 108 */     this.btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 111 */             ConfigEditorTaskWin.this.shell.close();
/*     */           }
/*     */         });
/*     */     
/* 115 */     this.btnOk.setBounds(298, 89, 75, 25);
/* 116 */     this.btnOk.setText("Ok");
/*     */     
/* 118 */     this.progressBar = new ProgressBar((Composite)this.shell, 0);
/* 119 */     this.progressBar.setBounds(23, 66, 350, 17);
/*     */     
/* 121 */     this.lblMessage = new Label((Composite)this.shell, 64);
/* 122 */     this.lblMessage.setBounds(23, 23, 343, 40);
/*     */     
/* 124 */     this.lblMessage.setText(this.message);
/*     */     
/* 126 */     this.labelStatus = new Label((Composite)this.shell, 0);
/* 127 */     this.labelStatus.setBounds(23, 92, 350, 22);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeWin() {
/* 137 */     this.shell.getDisplay().asyncExec(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 141 */             ConfigEditorTaskWin.this.shell.dispose();
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ConfigEditorTaskWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */