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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChooseServerWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlChooseServer;
/*     */   private Combo cmbHost;
/*     */   
/*     */   public ChooseServerWin(Shell parent, int style) {
/*  27 */     super(parent, style);
/*  28 */     setText("SWT Dialog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  36 */     createContents();
/*     */     
/*  38 */     loadServerHistory();
/*     */     
/*  40 */     this.shlChooseServer.open();
/*  41 */     this.shlChooseServer.layout();
/*  42 */     Display display = getParent().getDisplay();
/*     */     
/*  44 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shlChooseServer.getBounds()).width / 2;
/*  45 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shlChooseServer.getBounds()).height / 2;
/*     */     
/*  47 */     this.shlChooseServer.setLocation(x, y);
/*     */     
/*  49 */     while (!this.shlChooseServer.isDisposed()) {
/*  50 */       if (!display.readAndDispatch()) {
/*  51 */         display.sleep();
/*     */       }
/*     */     } 
/*  54 */     return this.result;
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadServerHistory() {
/*  59 */     List<String> sh = MainWin.getServerHistory();
/*     */     
/*  61 */     if (sh != null)
/*     */     {
/*  63 */       for (int i = sh.size() - 1; i > -1; i--)
/*     */       {
/*  65 */         this.cmbHost.add(sh.get(i));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  75 */     this.shlChooseServer = new Shell(getParent(), getStyle());
/*  76 */     this.shlChooseServer.setSize(325, 151);
/*  77 */     this.shlChooseServer.setText("Choose Server...");
/*     */     
/*  79 */     Button btnOk = new Button((Composite)this.shlChooseServer, 0);
/*  80 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  84 */             if (ChooseServerWin.this.cmbHost.getText().contains(":")) {
/*     */               
/*  86 */               String[] hp = ChooseServerWin.this.cmbHost.getText().split(":");
/*     */               
/*  88 */               if (UIUtils.validateNum(hp[1], 1, 65535))
/*     */               {
/*  90 */                 MainWin.addServerToHistory(ChooseServerWin.this.cmbHost.getText());
/*  91 */                 MainWin.setHost(hp[0]);
/*  92 */                 MainWin.setPort(hp[1]);
/*  93 */                 MainWin.restartServerConn();
/*     */ 
/*     */                 
/*  96 */                 e.display.getActiveShell().close();
/*     */               }
/*     */               else
/*     */               {
/* 100 */                 MainWin.showError("Invalid server entry", "The port entered for server address is not valid.", "Valid TCP port range is 1-65535.");
/*     */               }
/*     */             
/*     */             } else {
/*     */               
/* 105 */               MainWin.showError("Invalid server entry", "The data entered for server address is not valid.", "Please enter a server address and port in the form host:port.\r\n\nFor example: 127.0.0.1:6800");
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 110 */     btnOk.setBounds(120, 91, 82, 25);
/* 111 */     btnOk.setText("Ok");
/*     */     
/* 113 */     Button btnCancel = new Button((Composite)this.shlChooseServer, 0);
/* 114 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 118 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/* 122 */     btnCancel.setBounds(222, 91, 75, 25);
/* 123 */     btnCancel.setText("Cancel");
/*     */     
/* 125 */     this.cmbHost = new Combo((Composite)this.shlChooseServer, 0);
/*     */     
/* 127 */     this.cmbHost.setBounds(22, 40, 275, 23);
/* 128 */     this.cmbHost.setText(MainWin.getHost() + ":" + MainWin.getPort());
/*     */     
/* 130 */     Label lblEnterServerAddress = new Label((Composite)this.shlChooseServer, 0);
/* 131 */     lblEnterServerAddress.setBounds(22, 19, 275, 15);
/* 132 */     lblEnterServerAddress.setText("Enter server address in the form host:port");
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ChooseServerWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */