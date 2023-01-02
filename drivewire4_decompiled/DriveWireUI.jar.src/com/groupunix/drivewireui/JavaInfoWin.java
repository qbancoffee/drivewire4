/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaInfoWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shell;
/*     */   private Text textInfo;
/*     */   Clipboard cb;
/*     */   
/*     */   public JavaInfoWin(Shell parent, int style) {
/*  30 */     super(parent, style);
/*  31 */     setText("Java environment info");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  39 */     createContents();
/*     */     
/*  41 */     loadInfo();
/*     */     
/*  43 */     this.shell.open();
/*  44 */     this.shell.layout();
/*  45 */     Display display = getParent().getDisplay();
/*  46 */     this.cb = new Clipboard(display);
/*  47 */     while (!this.shell.isDisposed()) {
/*  48 */       if (!display.readAndDispatch()) {
/*  49 */         display.sleep();
/*     */       }
/*     */     } 
/*  52 */     return this.result;
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadInfo() {
/*  57 */     String jitmp = new String();
/*  58 */     for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
/*  59 */       jitmp = jitmp + e + "\n";
/*     */     }
/*  61 */     getTextInfo().setText(jitmp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  68 */     this.shell = new Shell(getParent(), getStyle());
/*  69 */     this.shell.setSize(606, 517);
/*  70 */     this.shell.setText(getText());
/*  71 */     this.shell.setLayout(null);
/*     */     
/*  73 */     this.textInfo = new Text((Composite)this.shell, 2624);
/*  74 */     this.textInfo.setBounds(10, 10, 580, 432);
/*     */     
/*  76 */     Button btnClose = new Button((Composite)this.shell, 0);
/*  77 */     btnClose.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/*  80 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*  83 */     btnClose.setBounds(258, 454, 75, 25);
/*  84 */     btnClose.setText("Close");
/*     */     
/*  86 */     Button btnNewButton = new Button((Composite)this.shell, 0);
/*  87 */     btnNewButton.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  92 */             String textData = JavaInfoWin.this.textInfo.getText();
/*  93 */             if (textData.length() > 0) {
/*  94 */               TextTransfer textTransfer = TextTransfer.getInstance();
/*  95 */               JavaInfoWin.this.cb.setContents(new Object[] { textData }, new Transfer[] { (Transfer)textTransfer });
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 101 */     btnNewButton.setBounds(10, 454, 157, 25);
/* 102 */     btnNewButton.setText("Copy to clipboard");
/*     */   }
/*     */   
/*     */   protected Text getTextInfo() {
/* 106 */     return this.textInfo;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/JavaInfoWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */