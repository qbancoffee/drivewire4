/*     */ package com.groupunix.drivewireui;
/*     */ 
import com.swtdesigner.SWTResourceManager;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.eclipse.swt.widgets.Text;

/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreateDiskWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected static Shell shlCreateANew;
/*     */   private Text textPath;
/*     */   private Spinner spinnerDrive;
/*     */   private Button btnFile;
/*     */   
/*     */   public CreateDiskWin(Shell parent, int style) {
/*  31 */     super(parent, style);
/*  32 */     setText("SWT Dialog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  40 */     createContents();
/*     */     
/*  42 */     applyFont();
/*     */     
/*  44 */     shlCreateANew.open();
/*  45 */     shlCreateANew.layout();
/*  46 */     Display display = getParent().getDisplay();
/*     */     
/*  48 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (shlCreateANew.getBounds()).width / 2;
/*  49 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (shlCreateANew.getBounds()).height / 2;
/*     */     
/*  51 */     shlCreateANew.setLocation(x, y);
/*     */     
/*  53 */     Label lblNewLabel = new Label((Composite)shlCreateANew, 0);
/*  54 */     lblNewLabel.setImage(SWTResourceManager.getImage(CreateDiskWin.class, "/wizard/new-disk-32.png"));
/*  55 */     lblNewLabel.setBounds(319, 18, 32, 32);
/*     */     
/*  57 */     if (MainWin.getCurrentDiskNo() > -1)
/*     */     {
/*  59 */       this.spinnerDrive.setSelection(MainWin.getCurrentDiskNo());
/*     */     }
/*     */     
/*  62 */     while (!shlCreateANew.isDisposed()) {
/*  63 */       if (!display.readAndDispatch()) {
/*  64 */         display.sleep();
/*     */       }
/*     */     } 
/*  67 */     return this.result;
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
/*     */   private static void applyFont() {}
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
/*     */   private void createContents() {
/*  92 */     shlCreateANew = new Shell(getParent(), getStyle());
/*  93 */     shlCreateANew.setSize(380, 206);
/*  94 */     shlCreateANew.setText("Create a new disk image...");
/*  95 */     shlCreateANew.setLayout(null);
/*     */     
/*  97 */     this.spinnerDrive = new Spinner((Composite)shlCreateANew, 2048);
/*  98 */     this.spinnerDrive.setBounds(202, 26, 47, 22);
/*  99 */     this.spinnerDrive.setMaximum(255);
/*     */     
/* 101 */     Label lblInsertNewDisk = new Label((Composite)shlCreateANew, 0);
/* 102 */     lblInsertNewDisk.setBounds(22, 29, 174, 19);
/* 103 */     lblInsertNewDisk.setAlignment(131072);
/* 104 */     lblInsertNewDisk.setText("Create new disk for drive:");
/*     */     
/* 106 */     Label lblPath = new Label((Composite)shlCreateANew, 0);
/* 107 */     lblPath.setBounds(22, 68, 277, 19);
/* 108 */     lblPath.setText("File for new disk image (optional):");
/*     */     
/* 110 */     this.textPath = new Text((Composite)shlCreateANew, 2048);
/* 111 */     this.textPath.setBounds(21, 89, 298, 21);
/*     */     
/* 113 */     if (MainWin.getInstanceConfig().containsKey("LocalDiskDir")) {
/* 114 */       this.textPath.setText(MainWin.getInstanceConfig().getString("LocalDiskDir") + System.getProperty("file.separator"));
/*     */     }
/* 116 */     this.btnFile = new Button((Composite)shlCreateANew, 0);
/* 117 */     this.btnFile.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 121 */             final String curpath = CreateDiskWin.this.textPath.getText();
/*     */             
/* 123 */             SwingUtilities.invokeLater(new Runnable()
/*     */                 {
/*     */                   
/*     */                   public void run()
/*     */                   {
/* 128 */                     final String filename = MainWin.getFile(true, false, curpath, "File for new disk image..", "Create");
/*     */                     
/* 130 */                     if (filename != null)
/*     */                     {
/* 132 */                       if (!CreateDiskWin.this.textPath.isDisposed())
/*     */                       {
/* 134 */                         CreateDiskWin.shlCreateANew.getDisplay().asyncExec(new Runnable()
/*     */                             {
/*     */                               
/*     */                               public void run()
/*     */                               {
/* 139 */                                 CreateDiskWin.this.textPath.setText(filename);
/*     */                               }
/*     */                             });
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 150 */     this.btnFile.setBounds(322, 88, 29, 23);
/* 151 */     this.btnFile.setText("...");
/*     */     
/* 153 */     Button btnCreate = new Button((Composite)shlCreateANew, 0);
/* 154 */     btnCreate.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 158 */             MainWin.sendCommand("dw disk create " + CreateDiskWin.this.spinnerDrive.getSelection() + " " + CreateDiskWin.this.textPath.getText());
/*     */             
/* 160 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/* 164 */     btnCreate.setBounds(141, 137, 90, 30);
/* 165 */     btnCreate.setText("Create");
/*     */     
/* 167 */     Button btnCancel = new Button((Composite)shlCreateANew, 0);
/* 168 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 172 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/* 175 */     btnCancel.setBounds(276, 137, 75, 30);
/* 176 */     btnCancel.setText("Cancel");
/*     */   }
/*     */ 
/*     */   
/*     */   protected Spinner getSpinner() {
/* 181 */     return this.spinnerDrive;
/*     */   }
/*     */   protected Button getBtnFile() {
/* 184 */     return this.btnFile;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/CreateDiskWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */