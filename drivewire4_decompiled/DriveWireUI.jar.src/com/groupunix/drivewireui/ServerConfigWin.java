/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.eclipse.swt.widgets.TabFolder;
/*     */ import org.eclipse.swt.widgets.TabItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerConfigWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shlServerConfiguration;
/*     */   private Button btnLogToConsole;
/*     */   private Button btnLogToFile;
/*     */   private Combo comboLogLevel;
/*     */   private Text textLogFile;
/*     */   private Text textLogFormat;
/*     */   private Button btnUIEnabled;
/*     */   private Spinner textUIPort;
/*     */   private Spinner textLazyWrite;
/*     */   private Text textLocalDiskDir;
/*     */   private static Composite grpLogging;
/*     */   private static Composite grpMiscellaneous;
/*     */   private static Composite grpUserInterfaceSupport;
/*     */   private Button btnLogUIConnections;
/*     */   private static Button btnApply;
/*     */   private Button btnUseRxtx;
/*     */   private Button btnLoadRxtx;
/*     */   private boolean dataReady = false;
/*     */   private static Composite grpRxtx;
/*     */   private Composite composite;
/*     */   private TabFolder tabFolder;
/*     */   private TabItem tbtmLogging;
/*     */   private TabItem tbtmUISupport;
/*     */   private TabItem tbtmDisk;
/*     */   private TabItem tbtmRXTX;
/*     */   
/*     */   public ServerConfigWin(Shell parent, int style) {
/*  68 */     super(parent, style);
/*  69 */     setText("SWT Dialog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() throws DWUIOperationFailedException, IOException {
/*  79 */     createContents();
/*  80 */     displaySettings();
/*     */     
/*  82 */     this.shlServerConfiguration.open();
/*  83 */     this.shlServerConfiguration.layout();
/*  84 */     Display display = getParent().getDisplay();
/*  85 */     this.dataReady = true;
/*  86 */     while (!this.shlServerConfiguration.isDisposed()) {
/*  87 */       if (!display.readAndDispatch()) {
/*  88 */         display.sleep();
/*     */       }
/*     */     } 
/*  91 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateApply() {
/* 100 */     if (this.dataReady) {
/*     */       
/* 102 */       HashMap<String, String> res = getChangedValues();
/*     */       
/* 104 */       if (res.size() > 0) {
/*     */         
/* 106 */         btnApply.setEnabled(true);
/*     */       }
/*     */       else {
/*     */         
/* 110 */         btnApply.setEnabled(false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HashMap<String, String> getChangedValues() {
/* 120 */     HashMap<String, String> res = new HashMap<String, String>();
/*     */     
/* 122 */     addIfChanged(res, "LogToConsole", UIUtils.bTos(this.btnLogToConsole.getSelection()));
/* 123 */     addIfChanged(res, "LogToFile", UIUtils.bTos(this.btnLogToFile.getSelection()));
/* 124 */     addIfChanged(res, "UIEnabled", UIUtils.bTos(this.btnUIEnabled.getSelection()));
/* 125 */     addIfChanged(res, "LogFile", this.textLogFile.getText());
/* 126 */     addIfChanged(res, "LogFormat", this.textLogFormat.getText());
/* 127 */     addIfChanged(res, "UIPort", this.textUIPort.getText());
/* 128 */     addIfChanged(res, "DiskLazyWriteInterval", this.textLazyWrite.getText());
/* 129 */     addIfChanged(res, "LocalDiskDir", this.textLocalDiskDir.getText());
/* 130 */     addIfChanged(res, "LogLevel", this.comboLogLevel.getItem(this.comboLogLevel.getSelectionIndex()));
/* 131 */     addIfChanged(res, "LogUIConnections", UIUtils.bTos(this.btnLogUIConnections.getSelection()));
/* 132 */     addIfChanged(res, "UseRXTX", UIUtils.bTos(this.btnUseRxtx.getSelection()));
/* 133 */     addIfChanged(res, "LoadRXTX", UIUtils.bTos(this.btnLoadRxtx.getSelection()));
/*     */     
/* 135 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addIfChanged(HashMap<String, String> map, String key, String value) {
/* 140 */     if (!MainWin.dwconfig.containsKey(key) || !MainWin.dwconfig.getProperty(key).equals(value))
/*     */     {
/* 142 */       map.put(key, value);
/*     */     }
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
/*     */   private void displaySettings() {
/* 156 */     this.btnLogToConsole.setSelection(MainWin.dwconfig.getBoolean("LogToConsole", true));
/* 157 */     this.btnLogToFile.setSelection(MainWin.dwconfig.getBoolean("LogToFile", false));
/* 158 */     this.btnUIEnabled.setSelection(MainWin.dwconfig.getBoolean("UIEnabled", false));
/*     */     
/* 160 */     this.textLogFile.setText(MainWin.dwconfig.getString("LogFile", ""));
/*     */     
/* 162 */     this.textLogFormat.setText(MainWin.dwconfig.getString("LogFormat", "%d{dd MMM yyyy HH:mm:ss} %-5p [%-14t] %26.26C: %m%n"));
/* 163 */     this.textLocalDiskDir.setText(MainWin.dwconfig.getString("LocalDiskDir", ""));
/* 164 */     this.comboLogLevel.select(this.comboLogLevel.indexOf(MainWin.dwconfig.getString("LogLevel", "WARN")));
/* 165 */     this.btnLogUIConnections.setSelection(MainWin.dwconfig.getBoolean("LogUIConnections", false));
/*     */     
/* 167 */     this.btnLoadRxtx.setSelection(MainWin.dwconfig.getBoolean("LoadRXTX", true));
/* 168 */     this.btnUseRxtx.setSelection(MainWin.dwconfig.getBoolean("UseRXTX", true));
/*     */     
/* 170 */     this.textLazyWrite.setSelection(MainWin.dwconfig.getInt("DiskLazyWriteInterval", 15000));
/* 171 */     this.textUIPort.setSelection(MainWin.dwconfig.getInt("UIPort", 6800));
/* 172 */     updateApply();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/* 179 */     this.shlServerConfiguration = new Shell(getParent(), getStyle());
/* 180 */     this.shlServerConfiguration.setSize(449, 290);
/*     */ 
/*     */     
/* 183 */     this.shlServerConfiguration.setText("Server Configuration");
/* 184 */     this.shlServerConfiguration.setLayout((Layout)new FormLayout());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     this.tabFolder = new TabFolder((Composite)this.shlServerConfiguration, 0);
/* 191 */     FormData fd_tabFolder = new FormData();
/* 192 */     fd_tabFolder.right = new FormAttachment(100, -10);
/* 193 */     fd_tabFolder.bottom = new FormAttachment(0, 212);
/* 194 */     fd_tabFolder.top = new FormAttachment(0, 10);
/* 195 */     fd_tabFolder.left = new FormAttachment(0, 10);
/* 196 */     this.tabFolder.setLayoutData(fd_tabFolder);
/* 197 */     this.tabFolder.setBounds(5, 5, 440, 200);
/*     */     
/* 199 */     this.tbtmLogging = new TabItem(this.tabFolder, 0);
/* 200 */     this.tbtmLogging.setText("Logging");
/*     */     
/* 202 */     grpLogging = new Composite((Composite)this.tabFolder, 0);
/* 203 */     this.tbtmLogging.setControl((Control)grpLogging);
/* 204 */     grpLogging.setLayout(null);
/*     */     
/* 206 */     this.btnLogToConsole = new Button(grpLogging, 32);
/* 207 */     this.btnLogToConsole.setBounds(240, 16, 165, 23);
/* 208 */     this.btnLogToConsole.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 211 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 214 */     this.btnLogToConsole.setText("Log to console");
/*     */     
/* 216 */     this.btnLogToFile = new Button(grpLogging, 32);
/* 217 */     this.btnLogToFile.setBounds(240, 40, 165, 23);
/* 218 */     this.btnLogToFile.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 221 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 224 */     this.btnLogToFile.setText("Log to file");
/*     */     
/* 226 */     this.comboLogLevel = new Combo(grpLogging, 8);
/* 227 */     this.comboLogLevel.setBounds(101, 17, 112, 23);
/* 228 */     this.comboLogLevel.setLocation(101, 22);
/* 229 */     this.comboLogLevel.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 231 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 234 */     this.comboLogLevel.setItems(new String[] { "ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL" });
/*     */     
/* 236 */     Label lblLogLevel = new Label(grpLogging, 0);
/* 237 */     lblLogLevel.setBounds(20, 20, 75, 24);
/* 238 */     lblLogLevel.setLocation(20, 24);
/* 239 */     lblLogLevel.setAlignment(131072);
/* 240 */     lblLogLevel.setText("Log level:");
/*     */     
/* 242 */     Label lblLogFile = new Label(grpLogging, 0);
/* 243 */     lblLogFile.setBounds(10, 92, 75, 18);
/* 244 */     lblLogFile.setAlignment(131072);
/* 245 */     lblLogFile.setText("Log file:");
/*     */     
/* 247 */     this.textLogFile = new Text(grpLogging, 2048);
/* 248 */     this.textLogFile.setBounds(91, 89, 259, 24);
/* 249 */     this.textLogFile.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 251 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/*     */     
/* 255 */     Button button = new Button(grpLogging, 0);
/* 256 */     button.setBounds(356, 83, 40, 32);
/* 257 */     button.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 261 */             final FileChooser fc = new FileChooser(ServerConfigWin.this.textLogFile.getText(), "Choose a log file..", false);
/*     */             
/* 263 */             SwingUtilities.invokeLater(new Runnable()
/*     */                 {
/*     */                   
/*     */                   public void run()
/*     */                   {
/* 268 */                     final String fname = fc.getFile();
/*     */                     
/* 270 */                     if (fname != null && !ServerConfigWin.this.textLogFile.isDisposed())
/*     */                     {
/* 272 */                       Display.getDefault().asyncExec(new Runnable()
/*     */                           {
/*     */ 
/*     */                             
/*     */                             public void run()
/*     */                             {
/* 278 */                               ServerConfigWin.this.textLogFile.setText(fname);
/*     */                             }
/*     */                           });
/*     */                     }
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/* 286 */     button.setText("...");
/*     */     
/* 288 */     Label lblLineFormat = new Label(grpLogging, 0);
/* 289 */     lblLineFormat.setBounds(10, 124, 75, 15);
/* 290 */     lblLineFormat.setAlignment(131072);
/* 291 */     lblLineFormat.setText("Log format:");
/*     */     
/* 293 */     this.textLogFormat = new Text(grpLogging, 2048);
/* 294 */     this.textLogFormat.setBounds(101, 121, 301, 24);
/* 295 */     this.textLogFormat.setBounds(91, 121, 304, 24);
/* 296 */     this.textLogFormat.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 298 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 303 */     this.tbtmUISupport = new TabItem(this.tabFolder, 0);
/* 304 */     this.tbtmUISupport.setToolTipText("UI Support");
/* 305 */     this.tbtmUISupport.setText("UI Support");
/*     */ 
/*     */ 
/*     */     
/* 309 */     grpUserInterfaceSupport = new Composite((Composite)this.tabFolder, 0);
/* 310 */     this.tbtmUISupport.setControl((Control)grpUserInterfaceSupport);
/*     */     
/* 312 */     this.btnUIEnabled = new Button(grpUserInterfaceSupport, 32);
/* 313 */     this.btnUIEnabled.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 316 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 319 */     this.btnUIEnabled.setBounds(30, 30, 138, 19);
/* 320 */     this.btnUIEnabled.setText("UI Enabled");
/*     */     
/* 322 */     this.textUIPort = new Spinner(grpUserInterfaceSupport, 2048);
/* 323 */     this.textUIPort.setPageIncrement(1000);
/* 324 */     this.textUIPort.setMaximum(65535);
/* 325 */     this.textUIPort.setMinimum(1);
/* 326 */     this.textUIPort.setBounds(311, 29, 79, 24);
/*     */     
/* 328 */     Label lblListenOnTcp = new Label(grpUserInterfaceSupport, 0);
/* 329 */     lblListenOnTcp.setAlignment(131072);
/* 330 */     lblListenOnTcp.setBounds(174, 32, 131, 18);
/* 331 */     lblListenOnTcp.setText("Listen on TCP port:");
/*     */     
/* 333 */     this.btnLogUIConnections = new Button(grpUserInterfaceSupport, 32);
/* 334 */     this.btnLogUIConnections.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 337 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 340 */     this.btnLogUIConnections.setBounds(30, 55, 225, 16);
/* 341 */     this.btnLogUIConnections.setText("Log UI Connections");
/*     */ 
/*     */     
/* 344 */     this.tbtmDisk = new TabItem(this.tabFolder, 0);
/* 345 */     this.tbtmDisk.setText("Disk");
/*     */ 
/*     */     
/* 348 */     grpMiscellaneous = new Composite((Composite)this.tabFolder, 0);
/* 349 */     this.tbtmDisk.setControl((Control)grpMiscellaneous);
/*     */     
/* 351 */     Label lblDiskSyncLazy = new Label(grpMiscellaneous, 0);
/* 352 */     lblDiskSyncLazy.setAlignment(131072);
/* 353 */     lblDiskSyncLazy.setBounds(22, 49, 200, 18);
/* 354 */     lblDiskSyncLazy.setText("Disk sync lazy write interval (ms):");
/*     */     
/* 356 */     this.textLazyWrite = new Spinner(grpMiscellaneous, 2048);
/* 357 */     this.textLazyWrite.setIncrement(100);
/* 358 */     this.textLazyWrite.setPageIncrement(1000);
/* 359 */     this.textLazyWrite.setMaximum(60000);
/* 360 */     this.textLazyWrite.setMinimum(1000);
/* 361 */     this.textLazyWrite.setBounds(228, 46, 79, 24);
/*     */     
/* 363 */     this.textLocalDiskDir = new Text(grpMiscellaneous, 2048);
/* 364 */     this.textLocalDiskDir.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 366 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 369 */     this.textLocalDiskDir.setBounds(22, 105, 317, 24);
/*     */     
/* 371 */     Label lblLocalDiskDirectory = new Label(grpMiscellaneous, 0);
/* 372 */     lblLocalDiskDirectory.setBounds(22, 86, 171, 18);
/* 373 */     lblLocalDiskDirectory.setText("Default disk directory:");
/*     */     
/* 375 */     Button btnChooseDiskDir = new Button(grpMiscellaneous, 0);
/* 376 */     btnChooseDiskDir.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 381 */             final FileChooser fc = new FileChooser(ServerConfigWin.this.textLocalDiskDir.getText(), "Choose disk directory...", true);
/*     */             
/* 383 */             SwingUtilities.invokeLater(new Runnable()
/*     */                 {
/*     */                   
/*     */                   public void run()
/*     */                   {
/* 388 */                     final String fname = fc.getFile();
/* 389 */                     if (fname != null && !ServerConfigWin.this.textLocalDiskDir.isDisposed())
/*     */                     {
/* 391 */                       Display.getDefault().asyncExec(new Runnable()
/*     */                           {
/*     */ 
/*     */                             
/*     */                             public void run()
/*     */                             {
/* 397 */                               ServerConfigWin.this.textLocalDiskDir.setText(fname);
/*     */                             }
/*     */                           });
/*     */                     }
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */     
/* 406 */     btnChooseDiskDir.setBounds(345, 100, 40, 32);
/* 407 */     btnChooseDiskDir.setText("...");
/*     */ 
/*     */     
/* 410 */     this.tbtmRXTX = new TabItem(this.tabFolder, 0);
/* 411 */     this.tbtmRXTX.setText("RXTX");
/*     */ 
/*     */     
/* 414 */     grpRxtx = new Composite((Composite)this.tabFolder, 0);
/* 415 */     this.tbtmRXTX.setControl((Control)grpRxtx);
/*     */     
/* 417 */     this.btnUseRxtx = new Button(grpRxtx, 32);
/* 418 */     this.btnUseRxtx.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 421 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 424 */     this.btnUseRxtx.setBounds(28, 47, 363, 16);
/* 425 */     this.btnUseRxtx.setText("Use RXTX (needed for any serial connections)");
/*     */     
/* 427 */     this.btnLoadRxtx = new Button(grpRxtx, 32);
/* 428 */     this.btnLoadRxtx.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 431 */             ServerConfigWin.this.updateApply();
/*     */           }
/*     */         });
/* 434 */     this.btnLoadRxtx.setBounds(28, 76, 363, 16);
/* 435 */     this.btnLoadRxtx.setText("Load internal RXTX (disable if provided by system)");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 441 */     this.composite = new Composite((Composite)this.shlServerConfiguration, 0);
/* 442 */     FormData fd_composite = new FormData();
/* 443 */     fd_composite.right = new FormAttachment(100, -13);
/* 444 */     fd_composite.left = new FormAttachment(0, 166);
/* 445 */     fd_composite.bottom = new FormAttachment(100, -10);
/* 446 */     fd_composite.top = new FormAttachment(0, 225);
/* 447 */     this.composite.setLayoutData(fd_composite);
/*     */     
/* 449 */     FillLayout fl_composite = new FillLayout(256);
/* 450 */     fl_composite.spacing = 10;
/* 451 */     this.composite.setLayout((Layout)fl_composite);
/*     */     
/* 453 */     Button btnOk = new Button(this.composite, 0);
/* 454 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*     */             try {
/* 465 */               UIUtils.setServerSettings(ServerConfigWin.this.getChangedValues());
/* 466 */               e.display.getActiveShell().close();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             }
/* 475 */             catch (IOException e1) {
/*     */               
/* 477 */               MainWin.showError("Error sending updated config", e1.getMessage(), UIUtils.getStackTrace(e1));
/*     */             }
/* 479 */             catch (DWUIOperationFailedException e2) {
/*     */               
/* 481 */               MainWin.showError("Error sending updated config", e2.getMessage(), UIUtils.getStackTrace(e2));
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 488 */     btnOk.setText("Ok");
/*     */ 
/*     */     
/* 491 */     Button btnCancel = new Button(this.composite, 0);
/* 492 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 496 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/*     */     
/* 500 */     btnCancel.setText("Cancel");
/*     */     
/* 502 */     btnApply = new Button(this.composite, 0);
/* 503 */     btnApply.setEnabled(false);
/* 504 */     btnApply.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*     */             try {
/* 510 */               UIUtils.setServerSettings(ServerConfigWin.this.getChangedValues());
/* 511 */               ServerConfigWin.this.updateApply();
/*     */             }
/* 513 */             catch (IOException e1) {
/*     */               
/* 515 */               MainWin.showError("Failed to apply settings", "One or more items could not be set", e1.getMessage());
/*     */             }
/* 517 */             catch (DWUIOperationFailedException e1) {
/*     */               
/* 519 */               MainWin.showError("Failed to apply settings", "One or more items could not be set", e1.getMessage());
/*     */             } 
/*     */           }
/*     */         });
/* 523 */     btnApply.setText("Apply");
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
/*     */   public void submitEvent(String key, Object val) {
/* 535 */     System.out.println("ci: " + key + " / " + val);
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ServerConfigWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */