/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ public class ChooseInstanceWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlChooseAnInstance;
/*     */   private Combo cmbInstance;
/*     */   
/*     */   public ChooseInstanceWin(Shell parent, int style) {
/*  29 */     super(parent, style);
/*  30 */     setText("SWT Dialog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() throws IOException, DWUIOperationFailedException {
/*  40 */     createContents();
/*     */     
/*  42 */     loadInstances(this.cmbInstance);
/*  43 */     this.cmbInstance.select(0);
/*     */     
/*  45 */     this.shlChooseAnInstance.open();
/*  46 */     this.shlChooseAnInstance.layout();
/*  47 */     Display display = getParent().getDisplay();
/*     */     
/*  49 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shlChooseAnInstance.getBounds()).width / 2;
/*  50 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shlChooseAnInstance.getBounds()).height / 2;
/*     */     
/*  52 */     this.shlChooseAnInstance.setLocation(x, y);
/*     */     
/*  54 */     while (!this.shlChooseAnInstance.isDisposed()) {
/*  55 */       if (!display.readAndDispatch()) {
/*  56 */         display.sleep();
/*     */       }
/*     */     } 
/*  59 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/*  66 */     this.shlChooseAnInstance = new Shell(getParent(), getStyle());
/*  67 */     this.shlChooseAnInstance.setSize(323, 146);
/*  68 */     this.shlChooseAnInstance.setText("Choose an instance...");
/*     */     
/*  70 */     this.cmbInstance = new Combo((Composite)this.shlChooseAnInstance, 8);
/*  71 */     this.cmbInstance.setBounds(70, 35, 229, 23);
/*     */     
/*  73 */     Label lblInstance = new Label((Composite)this.shlChooseAnInstance, 0);
/*  74 */     lblInstance.setAlignment(131072);
/*  75 */     lblInstance.setBounds(10, 38, 54, 15);
/*  76 */     lblInstance.setText("Instance:");
/*     */     
/*  78 */     Button btnOk = new Button((Composite)this.shlChooseAnInstance, 0);
/*  79 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  83 */             MainWin.setInstance(ChooseInstanceWin.this.cmbInstance.getSelectionIndex());
/*  84 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*  87 */     btnOk.setBounds(123, 80, 75, 25);
/*  88 */     btnOk.setText("Ok");
/*     */     
/*  90 */     Button btnCancel = new Button((Composite)this.shlChooseAnInstance, 0);
/*  91 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*  95 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/*  99 */     btnCancel.setBounds(224, 80, 75, 25);
/* 100 */     btnCancel.setText("Cancel");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadInstances(Combo cmb) throws IOException, DWUIOperationFailedException {
/* 107 */     List<String> inst = UIUtils.loadList("ui server show instances");
/*     */     
/* 109 */     for (int i = 0; i < inst.size(); i++) {
/*     */       
/* 111 */       System.out.println("adding " + (String)inst.get(i));
/* 112 */       cmb.add(inst.get(i));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ChooseInstanceWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */