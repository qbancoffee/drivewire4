/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ import java.io.IOException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
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
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InitialConfigWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected static Shell shlInitialConfiguration;
/*     */   private Text textPort;
/*     */   private Text textHost;
/*     */   static Composite compPage1;
/*     */   static Composite compPage2;
/*     */   static Composite compPage3;
/*     */   private Combo comboSerialDev;
/*     */   private Text textConnTest;
/*     */   private Label labelOK;
/*     */   private Label labelERR;
/*     */   private Label labelHost;
/*     */   private Label labelWait;
/*     */   private Button btnBack;
/*     */   private Button btnNext;
/*     */   private Combo comboCocoModel;
/*     */   private Label lblLocalimg;
/*     */   private boolean connTested = false;
/*  48 */   private int page = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private static Composite composite;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InitialConfigWin(Shell parent, int style) {
/*  58 */     super(parent, style);
/*  59 */     setText("SWT Dialog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  67 */     createContents();
/*     */     
/*  69 */     applyFont();
/*     */     
/*  71 */     updatePages();
/*     */     
/*  73 */     this.labelOK.setVisible(false);
/*  74 */     this.labelERR.setVisible(false);
/*  75 */     this.labelWait.setVisible(false);
/*     */     
/*  77 */     this.labelHost.setVisible(false);
/*  78 */     this.textHost.setVisible(false);
/*     */ 
/*     */     
/*  81 */     shlInitialConfiguration.open();
/*  82 */     shlInitialConfiguration.layout();
/*  83 */     Display display = getParent().getDisplay();
/*  84 */     while (!shlInitialConfiguration.isDisposed()) {
/*  85 */       if (!display.readAndDispatch()) {
/*  86 */         display.sleep();
/*     */       }
/*     */     } 
/*  89 */     return this.result;
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
/*     */   private void createContents() {
/* 136 */     shlInitialConfiguration = new Shell(getParent(), getStyle());
/* 137 */     shlInitialConfiguration.setSize(453, 409);
/* 138 */     shlInitialConfiguration.setText("Initial Configuration");
/* 139 */     shlInitialConfiguration.setLayout(null);
/*     */     
/* 141 */     this.btnBack = new Button((Composite)shlInitialConfiguration, 0);
/* 142 */     this.btnBack.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 146 */             if (InitialConfigWin.this.page == 0) {
/*     */               
/* 148 */               InitialConfigWin.this.page = 1;
/* 149 */               InitialConfigWin.this.updatePages();
/*     */             }
/* 151 */             else if (InitialConfigWin.this.page == 1) {
/*     */               
/* 153 */               InitialConfigWin.this.page = 0;
/* 154 */               InitialConfigWin.this.updatePages();
/*     */             
/*     */             }
/* 157 */             else if (InitialConfigWin.this.page == 2) {
/*     */               
/* 159 */               InitialConfigWin.this.page = 1;
/* 160 */               InitialConfigWin.this.updatePages();
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 165 */     this.btnBack.setBounds(10, 346, 75, 25);
/* 166 */     this.btnBack.setText("<< Back");
/*     */     
/* 168 */     this.btnNext = new Button((Composite)shlInitialConfiguration, 0);
/* 169 */     this.btnNext.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 173 */             if (InitialConfigWin.this.page == 0) {
/*     */               
/* 175 */               InitialConfigWin.this.page = 1;
/* 176 */               InitialConfigWin.this.updatePages();
/*     */             }
/* 178 */             else if (InitialConfigWin.this.page == 1) {
/*     */               
/* 180 */               InitialConfigWin.this.page = 2;
/* 181 */               InitialConfigWin.this.updatePages();
/*     */             }
/* 183 */             else if (InitialConfigWin.this.page == 2) {
/*     */               
/* 185 */               if (!InitialConfigWin.this.comboSerialDev.getText().equals("")) {
/*     */                 
/* 187 */                 InitialConfigWin.this.applyConfig();
/*     */               }
/*     */               else {
/*     */                 
/* 191 */                 MainWin.showError("We need a serial device", "In order to finish this wizard, we have to specify a serial device", "Please choose a valid serial device if possible.  If the desired device is not available, please exit this wizard and sort that out before continuing with DriveWire.\r\n\r\nIf you wanted to use some connection method other than serial, you'll have to use the regular instance config dialog.  This simple wizard only knows how to set up regular serial connections.");
/*     */               } 
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 198 */     this.btnNext.setBounds(183, 346, 75, 25);
/* 199 */     this.btnNext.setText("Next >>");
/*     */     
/* 201 */     Button btnCancel = new Button((Composite)shlInitialConfiguration, 0);
/* 202 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 206 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/* 209 */     btnCancel.setBounds(361, 346, 75, 25);
/* 210 */     btnCancel.setText("Cancel");
/*     */ 
/*     */     
/* 213 */     compPage1 = new Composite((Composite)shlInitialConfiguration, 0);
/* 214 */     compPage1.setBounds(10, 10, 426, 314);
/*     */     
/* 216 */     Label lblThisWizardWill = new Label(compPage1, 0);
/* 217 */     lblThisWizardWill.setBounds(10, 10, 406, 18);
/* 218 */     lblThisWizardWill.setText("This wizard will help you configure DriveWire 4.");
/*     */     
/* 220 */     Label lblFirstWeNeed = new Label(compPage1, 64);
/* 221 */     lblFirstWeNeed.setBounds(10, 34, 406, 43);
/* 222 */     lblFirstWeNeed.setText("First, we need to know how to communicate with the DriveWire server.  Where does the server run?");
/*     */     
/* 224 */     composite = new Composite(compPage1, 0);
/* 225 */     composite.setBounds(10, 83, 319, 55);
/*     */     
/* 227 */     Button btnLocalServer = new Button(composite, 16);
/* 228 */     btnLocalServer.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 232 */             InitialConfigWin.this.labelHost.setVisible(false);
/* 233 */             InitialConfigWin.this.textHost.setVisible(false);
/* 234 */             InitialConfigWin.this.textHost.setText("127.0.0.1");
/* 235 */             InitialConfigWin.this.lblLocalimg.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/my_computer.png"));
/*     */           }
/*     */         });
/* 238 */     btnLocalServer.setSelection(true);
/* 239 */     btnLocalServer.setBounds(10, 0, 309, 26);
/* 240 */     btnLocalServer.setText("The server runs on this computer");
/*     */     
/* 242 */     Button btnRemoteServer = new Button(composite, 16);
/* 243 */     btnRemoteServer.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 247 */             InitialConfigWin.this.labelHost.setVisible(true);
/* 248 */             InitialConfigWin.this.textHost.setVisible(true);
/* 249 */             InitialConfigWin.this.lblLocalimg.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/network-local.png"));
/*     */           }
/*     */         });
/*     */     
/* 253 */     btnRemoteServer.setBounds(10, 32, 309, 23);
/* 254 */     btnRemoteServer.setText("The server runs on another computer");
/*     */     
/* 256 */     this.labelHost = new Label(compPage1, 0);
/* 257 */     this.labelHost.setAlignment(131072);
/* 258 */     this.labelHost.setBounds(10, 160, 239, 18);
/* 259 */     this.labelHost.setText("What is the IP address of the server?");
/*     */     
/* 261 */     this.textHost = new Text(compPage1, 2048);
/* 262 */     this.textHost.setBounds(254, 157, 144, 21);
/* 263 */     this.textHost.setText("127.0.0.1");
/*     */     
/* 265 */     Label lblByDefaultThe = new Label(compPage1, 64);
/* 266 */     lblByDefaultThe.setBounds(27, 206, 389, 64);
/* 267 */     lblByDefaultThe.setText("By default, the server listens for clients on TCP port 6800.  \r\n\r\nIf you have not changed this setting, then just hit Next.\r\n");
/*     */     
/* 269 */     Label lblWhatTcpPort = new Label(compPage1, 0);
/* 270 */     lblWhatTcpPort.setAlignment(131072);
/* 271 */     lblWhatTcpPort.setBounds(10, 279, 239, 18);
/* 272 */     lblWhatTcpPort.setText("What TCP port does the server use?");
/*     */     
/* 274 */     this.textPort = new Text(compPage1, 2048);
/* 275 */     this.textPort.setBounds(254, 276, 55, 21);
/* 276 */     this.textPort.setText("6800");
/*     */     
/* 278 */     compPage2 = new Composite((Composite)shlInitialConfiguration, 524288);
/* 279 */     compPage2.setBounds(10, 10, 426, 314);
/*     */     
/* 281 */     Label lblOkNowLets = new Label(compPage2, 0);
/* 282 */     lblOkNowLets.setBounds(10, 10, 406, 15);
/* 283 */     lblOkNowLets.setText("Ok, now let's make sure we can communicate with the server.");
/*     */     
/* 285 */     Label lblIfYouHavent = new Label(compPage2, 64);
/* 286 */     lblIfYouHavent.setBounds(10, 43, 406, 151);
/* 287 */     lblIfYouHavent.setText("If you haven't started the server, please start it now.  \r\n\r\nIf you aren't sure whether the server is running, there is no harm in using the \"Test Connection\" button here to find out.  \r\n\r\nIf the connection test is successful, you will see a DriveWire logo and the current server version will be displayed.");
/*     */     
/* 289 */     this.lblLocalimg = new Label(compPage1, 0);
/* 290 */     this.lblLocalimg.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/my_computer.png"));
/* 291 */     this.lblLocalimg.setBounds(335, 78, 64, 64);
/*     */ 
/*     */     
/* 294 */     final Button btnTestConnection = new Button(compPage2, 0);
/* 295 */     btnTestConnection.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 299 */             InitialConfigWin.this.labelOK.setVisible(false);
/* 300 */             InitialConfigWin.this.labelERR.setVisible(false);
/*     */             
/* 302 */             InitialConfigWin.this.textConnTest.setText("");
/*     */ 
/*     */ 
/*     */             
/* 306 */             if (UIUtils.validateNum(InitialConfigWin.this.textPort.getText(), 1, 65535)) {
/*     */               
/* 308 */               InitialConfigWin.this.textConnTest.setText("Trying " + InitialConfigWin.this.textHost.getText() + ":" + InitialConfigWin.this.textPort.getText());
/* 309 */               InitialConfigWin.this.labelWait.setVisible(true);
/* 310 */               btnTestConnection.setEnabled(false);
/*     */               
/* 312 */               InitialConfigWin.compPage2.redraw();
/* 313 */               InitialConfigWin.shlInitialConfiguration.update();
/*     */ 
/*     */               
/*     */               try {
/* 317 */                 Connection conn = new Connection(InitialConfigWin.this.textHost.getText(), Integer.parseInt(InitialConfigWin.this.textPort.getText()), 0);
/*     */                 
/* 319 */                 List<String> res = new ArrayList<String>();
/*     */                 
/* 321 */                 conn.Connect();
/* 322 */                 res = conn.loadList(-1, "ui server show version");
/* 323 */                 conn.close();
/*     */                 
/* 325 */                 InitialConfigWin.this.textConnTest.setText(res.get(0));
/* 326 */                 InitialConfigWin.this.labelOK.setVisible(true);
/* 327 */                 InitialConfigWin.this.connTested = true;
/*     */ 
/*     */ 
/*     */               
/*     */               }
/* 332 */               catch (IOException e1) {
/*     */                 
/* 334 */                 InitialConfigWin.this.labelERR.setVisible(true);
/* 335 */                 InitialConfigWin.this.textConnTest.setText(e1.getMessage());
/*     */               }
/* 337 */               catch (NumberFormatException e2) {
/*     */                 
/* 339 */                 InitialConfigWin.this.labelERR.setVisible(true);
/* 340 */                 InitialConfigWin.this.textConnTest.setText("Port number must be numeric.");
/*     */               }
/* 342 */               catch (DWUIOperationFailedException e3) {
/*     */                 
/* 344 */                 InitialConfigWin.this.labelERR.setVisible(true);
/* 345 */                 InitialConfigWin.this.textConnTest.setText(e3.getMessage());
/*     */               } 
/*     */               
/* 348 */               btnTestConnection.setEnabled(true);
/*     */             }
/*     */             else {
/*     */               
/* 352 */               InitialConfigWin.this.labelERR.setVisible(true);
/* 353 */               InitialConfigWin.this.textConnTest.setText("Valid TCP port range is 1 - 65535.");
/*     */             } 
/*     */             
/* 356 */             InitialConfigWin.this.labelWait.setVisible(false);
/* 357 */             InitialConfigWin.this.btnNext.setEnabled(InitialConfigWin.this.connTested);
/*     */           }
/*     */         });
/*     */     
/* 361 */     btnTestConnection.setBounds(140, 200, 135, 25);
/* 362 */     btnTestConnection.setText("Test Connection");
/*     */     
/* 364 */     this.labelOK = new Label(compPage2, 32);
/* 365 */     this.labelOK.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/dw4logo2.gif"));
/* 366 */     this.labelOK.setBounds(27, 239, 64, 64);
/*     */     
/* 368 */     this.labelWait = new Label(compPage2, 32);
/* 369 */     this.labelWait.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/Hourglass.png"));
/* 370 */     this.labelWait.setBounds(27, 239, 64, 64);
/*     */     
/* 372 */     this.textConnTest = new Text(compPage2, 16777288);
/* 373 */     this.textConnTest.setEnabled(false);
/* 374 */     this.textConnTest.setBackground(SWTResourceManager.getColor(22));
/* 375 */     this.textConnTest.setFont(SWTResourceManager.getFont("Segoe UI", 9, 1));
/* 376 */     this.textConnTest.setBounds(97, 250, 217, 54);
/*     */     
/* 378 */     this.labelERR = new Label(compPage2, 32);
/* 379 */     this.labelERR.setImage(SWTResourceManager.getImage(InitialConfigWin.class, "/wizard/dialog-error-4.png"));
/* 380 */     this.labelERR.setBounds(27, 239, 64, 64);
/*     */     
/* 382 */     compPage3 = new Composite((Composite)shlInitialConfiguration, 0);
/* 383 */     compPage3.setBounds(10, 10, 426, 314);
/*     */     
/* 385 */     Label lblFinallyWeNeed = new Label(compPage3, 64);
/* 386 */     lblFinallyWeNeed.setBounds(10, 10, 406, 35);
/* 387 */     lblFinallyWeNeed.setText("Almost done.  We just need to tell the server how it will communicate with the CoCo.\r\n");
/*     */     
/* 389 */     Label lblTheServerCan = new Label(compPage3, 64);
/* 390 */     lblTheServerCan.setBounds(10, 59, 406, 49);
/* 391 */     lblTheServerCan.setText("The server can try to detect attached serial devices, but it isn't perfect on every platform.  Press \"Detect\" to give it a shot.  You can also enter the device name manually.");
/*     */     
/* 393 */     this.comboSerialDev = new Combo(compPage3, 0);
/* 394 */     this.comboSerialDev.setBounds(120, 127, 160, 23);
/*     */     
/* 396 */     Label lblSerialDevice = new Label(compPage3, 0);
/* 397 */     lblSerialDevice.setAlignment(131072);
/* 398 */     lblSerialDevice.setBounds(10, 130, 104, 20);
/* 399 */     lblSerialDevice.setText("Serial device:");
/*     */     
/* 401 */     Button btnDetect = new Button(compPage3, 0);
/* 402 */     btnDetect.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/*     */             try {
/* 407 */               MainWin.setHost(InitialConfigWin.this.textHost.getText());
/* 408 */               MainWin.setPort(InitialConfigWin.this.textPort.getText());
/* 409 */               MainWin.setInstance(0);
/*     */ 
/*     */               
/* 412 */               List<String> ports = UIUtils.loadList("ui server show serialdevs");
/*     */               
/* 414 */               InitialConfigWin.this.comboSerialDev.removeAll();
/*     */               
/* 416 */               for (int i = 0; i < ports.size(); i++) {
/* 417 */                 InitialConfigWin.this.comboSerialDev.add(ports.get(i));
/*     */               }
/* 419 */               if (ports.size() > 0)
/*     */               {
/*     */                 
/* 422 */                 if (MainWin.getInstanceConfig() != null && MainWin.getInstanceConfig().containsKey("SerialDevice") && InitialConfigWin.this.comboSerialDev.indexOf(MainWin.getInstanceConfig().getString("SerialDevice")) > -1)
/*     */                 {
/* 424 */                   InitialConfigWin.this.comboSerialDev.select(InitialConfigWin.this.comboSerialDev.indexOf(MainWin.getInstanceConfig().getString("SerialDevice")));
/*     */                 }
/*     */                 else
/*     */                 {
/* 428 */                   InitialConfigWin.this.comboSerialDev.select(0);
/*     */                 
/*     */                 }
/*     */               
/*     */               }
/*     */             }
/* 434 */             catch (IOException e1) {
/*     */               
/* 436 */               MainWin.showError("Error listing serial devices", e1.getMessage(), UIUtils.getStackTrace(e1));
/*     */             }
/* 438 */             catch (DWUIOperationFailedException e1) {
/*     */               
/* 440 */               MainWin.showError("Error listing serial devices", e1.getMessage(), UIUtils.getStackTrace(e1));
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 447 */     btnDetect.setBounds(286, 125, 75, 25);
/* 448 */     btnDetect.setText("Detect..");
/*     */     
/* 450 */     Label lblFinallyWeNeed_1 = new Label(compPage3, 64);
/* 451 */     lblFinallyWeNeed_1.setBounds(10, 178, 406, 56);
/* 452 */     lblFinallyWeNeed_1.setText("Finally, we need to know what speed to use for communications with the CoCo.   The rate is determined by the type of CoCo you have.");
/*     */     
/* 454 */     this.comboCocoModel = new Combo(compPage3, 8);
/* 455 */     this.comboCocoModel.setItems(new String[] { "CoCo 1: 38400 bps", "CoCo 2: 57600 bps", "CoCo 3: 115200 bps" });
/* 456 */     this.comboCocoModel.setBounds(120, 250, 160, 23);
/* 457 */     this.comboCocoModel.select(2);
/*     */     
/* 459 */     Label lblCocoModel = new Label(compPage3, 0);
/* 460 */     lblCocoModel.setAlignment(131072);
/* 461 */     lblCocoModel.setBounds(10, 253, 104, 15);
/* 462 */     lblCocoModel.setText("CoCo model:");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyConfig() {
/* 468 */     MainWin.setHost(this.textHost.getText());
/* 469 */     MainWin.setPort(this.textPort.getText());
/* 470 */     MainWin.setInstance(0);
/*     */ 
/*     */     
/* 473 */     HashMap<String, String> vals = new HashMap<String, String>();
/*     */     
/* 475 */     vals.put("Name", "CoCo " + (this.comboCocoModel.getSelectionIndex() + 1) + " on " + this.comboSerialDev.getText());
/* 476 */     vals.put("CocoModel", (this.comboCocoModel.getSelectionIndex() + 1) + "");
/* 477 */     vals.put("SerialDevice", this.comboSerialDev.getText());
/* 478 */     vals.put("DeviceType", "serial");
/* 479 */     vals.put("AutoStart", "true");
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 484 */       UIUtils.setInstanceSettings(0, vals);
/*     */       
/* 486 */       MainWin.sendCommand("ui instance reset protodev");
/*     */ 
/*     */ 
/*     */       
/* 490 */       shlInitialConfiguration.close();
/*     */     
/*     */     }
/* 493 */     catch (UnknownHostException e) {
/*     */       
/* 495 */       MainWin.showError("Error sending configuration", e.getMessage(), UIUtils.getStackTrace(e));
/*     */     }
/* 497 */     catch (IOException e) {
/*     */       
/* 499 */       MainWin.showError("Error sending configuration", e.getMessage(), UIUtils.getStackTrace(e));
/*     */     }
/* 501 */     catch (DWUIOperationFailedException e) {
/*     */       
/* 503 */       MainWin.showError("Error sending configuration", e.getMessage(), UIUtils.getStackTrace(e));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void updatePages() {
/* 510 */     this.labelERR.setVisible(false);
/* 511 */     this.labelOK.setVisible(false);
/* 512 */     this.textConnTest.setText("");
/*     */     
/* 514 */     if (this.page == 0) {
/*     */       
/* 516 */       this.btnBack.setEnabled(false);
/* 517 */       this.btnNext.setEnabled(true);
/* 518 */       this.btnNext.setText("Next >>");
/*     */       
/* 520 */       compPage1.setVisible(true);
/* 521 */       compPage2.setVisible(false);
/* 522 */       compPage3.setVisible(false);
/*     */     
/*     */     }
/* 525 */     else if (this.page == 1) {
/*     */       
/* 527 */       this.btnBack.setEnabled(true);
/* 528 */       this.btnNext.setEnabled(this.connTested);
/* 529 */       this.btnNext.setText("Next >>");
/*     */ 
/*     */       
/* 532 */       compPage1.setVisible(false);
/* 533 */       compPage2.setVisible(true);
/* 534 */       compPage3.setVisible(false);
/*     */     }
/* 536 */     else if (this.page == 2) {
/*     */       
/* 538 */       this.btnBack.setEnabled(true);
/* 539 */       this.btnNext.setEnabled(true);
/* 540 */       this.btnNext.setText("Finish");
/*     */       
/* 542 */       compPage1.setVisible(false);
/* 543 */       compPage2.setVisible(false);
/* 544 */       compPage3.setVisible(true);
/*     */     } 
/*     */   }
/*     */   protected Composite getComposite() {
/* 548 */     return composite;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/InitialConfigWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */