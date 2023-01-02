/*      */ package com.groupunix.drivewireui;
/*      */ 
/*      */ import com.groupunix.drivewireserver.DriveWireServer;
import com.swtdesigner.SWTResourceManager;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseTrackAdapter;
/*      */ import org.eclipse.swt.events.MouseTrackListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Cursor;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Combo;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Link;
/*      */ import org.eclipse.swt.widgets.MessageBox;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableColumn;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UITaskCompositeWizard
/*      */   extends UITaskComposite
/*      */ {
/*   40 */   private Pattern unixstyle = Pattern.compile("/dev/.+");
/*   41 */   private Pattern winstyle = Pattern.compile("COM\\d+");
/*      */   
/*      */   private Font introFont;
/*      */   
/*      */   private Font smallFont;
/*      */   
/*      */   private Font testStatusFont;
/*      */   private int tid;
/*   49 */   private int state = 0;
/*      */   
/*      */   private int[] sashform_orig;
/*      */   
/*   53 */   private Integer cocomodel = Integer.valueOf(3);
/*   54 */   private String device = null;
/*      */   private boolean usemidi = false;
/*   56 */   private String printertype = "Text";
/*   57 */   private String printerdir = "cocoprints";
/*   58 */   private int fpgarate = 115200;
/*      */   
/*   60 */   private Table portlist = null;
/*      */   
/*      */   private boolean monitorPorts = false;
/*   63 */   private Boolean updatingPorts = Boolean.valueOf(false);
/*      */   
/*   65 */   private ArrayList<String> manualPorts = new ArrayList<String>();
/*      */   
/*   67 */   private int width = 488;
/*   68 */   private int height = 480;
/*   69 */   private int top = 0;
/*      */   
/*      */   private Composite master;
/*      */   
/*      */   private StyledText testStatusText;
/*      */   
/*      */   private Thread testT;
/*      */   private Button doit;
/*      */   private boolean intesting = false;
/*      */   protected String cocodevname;
/*      */   
/*      */   public UITaskCompositeWizard(Composite master, int style, int tid) {
/*   81 */     super(master, style, tid);
/*   82 */     this.tid = tid;
/*   83 */     this.master = master;
/*      */     
/*   85 */     HashMap<String, Integer> fontmap = new HashMap<String, Integer>();
/*      */     
/*   87 */     fontmap.put("Droid Serif", Integer.valueOf(0));
/*   88 */     this.introFont = UIUtils.findFont(master.getDisplay(), fontmap, "Welcome", 56, 19);
/*   89 */     this.smallFont = UIUtils.findFont(master.getDisplay(), fontmap, "Welcome", 52, 13);
/*   90 */     this.testStatusFont = UIUtils.findFont(master.getDisplay(), fontmap, "Welcome", 61, 21);
/*      */ 
/*      */     
/*   93 */     this.status.setData("survive", "please");
/*   94 */     this.status.setVisible(false);
/*      */     
/*   96 */     this.portlist = new Table(this, 65536);
/*   97 */     this.portlist.setVisible(false);
/*   98 */     this.portlist.setData("survive", "please");
/*   99 */     this.portlist.setHeaderVisible(false);
/*  100 */     this.portlist.setLinesVisible(false);
/*      */     
/*  102 */     TableColumn tcPort = new TableColumn(this.portlist, 0);
/*  103 */     tcPort.setText("Port");
/*  104 */     tcPort.setResizable(true);
/*  105 */     tcPort.setWidth((this.width - 150) / 2);
/*      */     
/*  107 */     TableColumn tcStatus = new TableColumn(this.portlist, 0);
/*  108 */     tcStatus.setText("Status");
/*  109 */     tcStatus.setResizable(true);
/*  110 */     tcStatus.setWidth((this.width - 150) / 2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  116 */     createContents(master);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void createContents(Composite master) {
/*  123 */     MainWin.selectUIPage();
/*      */ 
/*      */     
/*  126 */     this.sashform_orig = MainWin.getSashformWeights();
/*  127 */     MainWin.setSashformWeights(new int[] { 0, this.sashform_orig[0] + this.sashform_orig[1] });
/*      */     
/*  129 */     addDisposeListener(new DisposeListener()
/*      */         {
/*      */           
/*      */           public void widgetDisposed(DisposeEvent e)
/*      */           {
/*  134 */             UITaskCompositeWizard.this.monitorPorts = false;
/*  135 */             MainWin.setSashformWeights(UITaskCompositeWizard.this.sashform_orig);
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/*  141 */     setBackground(SWTResourceManager.getColor(1));
/*      */     
/*  143 */     setBounds(((MainWin.scrolledComposite.getBounds()).width - this.width) / 2, this.top, this.width, this.height);
/*      */ 
/*      */ 
/*      */     
/*  147 */     drawControls();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawControls() {
/*  157 */     setRedraw(false);
/*      */     
/*  159 */     this.monitorPorts = false;
/*  160 */     this.status.setVisible(false);
/*  161 */     this.portlist.setVisible(false);
/*      */     
/*  163 */     Control[] controls = getChildren();
/*      */     
/*  165 */     for (Control c : controls) {
/*      */ 
/*      */       
/*  168 */       if (c.getData("survive") == null) {
/*  169 */         c.dispose();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  174 */     if (this.state == 0) {
/*      */       
/*  176 */       drawInitControls();
/*      */     
/*      */     }
/*  179 */     else if (this.state == 1) {
/*      */       
/*  181 */       drawCoCoSelectControls();
/*      */     }
/*  183 */     else if (this.state == 2) {
/*      */       
/*  185 */       drawSerialSelectControls();
/*      */     }
/*  187 */     else if (this.state == 3) {
/*      */       
/*  189 */       drawMIDISelectControls();
/*      */     }
/*  191 */     else if (this.state == 4) {
/*      */       
/*  193 */       drawPrinterSelectControls();
/*      */     }
/*  195 */     else if (this.state == 5) {
/*      */       
/*  197 */       if (this.cocomodel.intValue() == 4) {
/*      */         
/*  199 */         drawFPGABaudChooseControls();
/*      */       } else {
/*      */         
/*  202 */         drawCommTestChooseControls();
/*      */       } 
/*  204 */     } else if (this.state == 6) {
/*      */       
/*  206 */       drawCommTest1Controls();
/*      */     }
/*  208 */     else if (this.state == 7) {
/*      */       
/*  210 */       drawCommTest2Controls();
/*  211 */       startCommTest();
/*      */     }
/*  213 */     else if (this.state == 9) {
/*      */       
/*  215 */       drawWizardSuccessControls();
/*      */     } 
/*      */     
/*  218 */     setRedraw(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawFPGABaudChooseControls() {
/*  225 */     int y = 20;
/*      */     
/*  227 */     Label cocoman3 = new Label(this, 0);
/*  228 */     cocoman3.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman3.png"));
/*  229 */     cocoman3.setBounds(this.width / 2 - 144, y, 288, 160);
/*      */     
/*  231 */     y += 180;
/*      */     
/*  233 */     StyledText intro = new StyledText(this, 74);
/*      */     
/*  235 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/*  236 */     intro.setEditable(false);
/*  237 */     intro.setEnabled(false);
/*      */     
/*  239 */     intro.setBounds(0, y, this.width, 130);
/*  240 */     intro.setForeground(DiskWin.colorDiskBG);
/*  241 */     intro.setFont(this.introFont);
/*  242 */     intro.setText("We're almost finished!");
/*  243 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*      */     
/*  245 */     intro.append("An " + this.cocodevname + " running CoCo3FPGA can communicate at several different speeds.  Please select the rate you wish to use with the " + this.cocodevname + " (you must also configure this rate on the " + this.cocodevname + " itself):");
/*      */     
/*  247 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/*  249 */     y += 40 + (intro.getBounds()).height;
/*      */     
/*  251 */     final Combo fpgaspeed = new Combo(this, 8);
/*  252 */     fpgaspeed.setItems(new String[] { "115200", "230400", "460800", "921600" });
/*  253 */     fpgaspeed.select(0);
/*      */     
/*  255 */     fpgaspeed.setBounds(this.width / 2 - 50, y, 100, 40);
/*      */ 
/*      */ 
/*      */     
/*  259 */     fpgaspeed.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  264 */             if (fpgaspeed.getSelectionIndex() > -1)
/*      */             {
/*  266 */               UITaskCompositeWizard.this.fpgarate = Integer.parseInt(fpgaspeed.getItem(fpgaspeed.getSelectionIndex()));
/*      */             }
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  281 */     Button doit = new Button(this, 0);
/*  282 */     doit.setText("Apply configuration..");
/*      */     
/*  284 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*  285 */     doit.setEnabled(true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  290 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  295 */             UITaskCompositeWizard.this.applyConfig();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  308 */     Button nothanks = new Button(this, 0);
/*  309 */     nothanks.setText("Cancel wizard");
/*  310 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/*  312 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  318 */             UITaskCompositeWizard.this.state = -1;
/*  319 */             UITaskCompositeWizard.this.drawControls();
/*  320 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void startCommTest() {
/*  337 */     this.stat = 0;
/*      */     
/*  339 */     Runnable testThread = new Runnable()
/*      */       {
/*      */         boolean portopen = false;
/*  342 */         int attempt = 0;
/*      */         
/*      */         boolean wanttodie = false;
/*      */         
/*      */         boolean success = false;
/*      */         
/*      */         public void run() {
/*  349 */           while (!this.portopen && !UITaskCompositeWizard.this.isDisposed() && !this.wanttodie && UITaskCompositeWizard.this.state == 7) {
/*      */             
/*  351 */             this.attempt++;
/*  352 */             UITaskCompositeWizard.this.setTestStatusText("Trying to open " + UITaskCompositeWizard.this.device + "...  (Attempt #" + this.attempt + " of 10)");
/*      */ 
/*      */             
/*      */             try {
/*  356 */               this.portopen = DriveWireServer.testSerialPort_Open(UITaskCompositeWizard.this.device);
/*      */ 
/*      */               
/*  359 */               int rate = 115200;
/*      */               
/*  361 */               if (UITaskCompositeWizard.this.cocomodel.intValue() == 1) {
/*  362 */                 rate = 38400;
/*  363 */               } else if (UITaskCompositeWizard.this.cocomodel.intValue() == 2) {
/*  364 */                 rate = 57600;
/*      */               } 
/*  366 */               this.portopen = DriveWireServer.testSerialPort_setParams(rate);
/*      */             }
/*  368 */             catch (Exception e1) {
/*      */               
/*  370 */               if (this.attempt < 9) {
/*  371 */                 UITaskCompositeWizard.this.setTestStatusText("Failed to open " + UITaskCompositeWizard.this.device + ": " + e1.getMessage() + "  (will retry)");
/*      */               } else {
/*      */                 
/*  374 */                 UITaskCompositeWizard.this.setTestStatusText("Failed to open " + UITaskCompositeWizard.this.device + ": " + e1.getMessage());
/*  375 */                 this.wanttodie = true;
/*      */               } 
/*      */             } 
/*      */             
/*  379 */             if (!this.portopen && !this.wanttodie) {
/*      */               
/*      */               try {
/*      */                 
/*  383 */                 Thread.sleep(3000L);
/*      */               }
/*  385 */               catch (InterruptedException e) {
/*      */                 
/*  387 */                 this.wanttodie = true;
/*      */               } 
/*      */             }
/*      */           } 
/*      */ 
/*      */           
/*  393 */           if (this.portopen && !UITaskCompositeWizard.this.isDisposed()) {
/*      */             
/*  395 */             UITaskCompositeWizard.this.setTestStatusText(UITaskCompositeWizard.this.device + " open.  Turn on your CoCo now.");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             try {
/*  402 */               int ticks = 0;
/*      */               
/*  404 */               while (!this.wanttodie && !UITaskCompositeWizard.this.isDisposed() && UITaskCompositeWizard.this.state == 7)
/*      */               {
/*  406 */                 int data = DriveWireServer.testSerialPort_read();
/*      */                 
/*  408 */                 switch (data) {
/*      */                   
/*      */                   case -1:
/*  411 */                     ticks++;
/*  412 */                     UITaskCompositeWizard.this.appendTestStatusText(".");
/*  413 */                     if (ticks == 33) {
/*      */                       
/*  415 */                       UITaskCompositeWizard.this.setTestStatusText("No data received after 100 seconds, giving up.");
/*  416 */                       this.wanttodie = true;
/*      */                     } 
/*      */                     continue;
/*      */                   case 248:
/*      */                   case 254:
/*      */                   case 255:
/*  422 */                     this.success = true;
/*  423 */                     this.wanttodie = true;
/*  424 */                     UITaskCompositeWizard.this.setTestStatusText("Success! Received the proper value!");
/*      */                     continue;
/*      */                 } 
/*  427 */                 UITaskCompositeWizard.this.setTestStatusText("Received strange value (" + data + ").");
/*  428 */                 this.wanttodie = true;
/*      */ 
/*      */               
/*      */               }
/*      */ 
/*      */             
/*      */             }
/*  435 */             catch (Exception e) {
/*      */               
/*  437 */               UITaskCompositeWizard.this.setTestStatusText("Error while reading: " + e.getMessage());
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  444 */           if (this.portopen)
/*      */           {
/*  446 */             DriveWireServer.testSerialPort_close();
/*      */           }
/*      */ 
/*      */           
/*  450 */           if (!UITaskCompositeWizard.this.isDisposed())
/*      */           {
/*  452 */             if (this.success) {
/*      */ 
/*      */               
/*  455 */               UITaskCompositeWizard.this.getDisplay().asyncExec(new Runnable()
/*      */                   {
/*      */ 
/*      */                     
/*      */                     public void run()
/*      */                     {
/*  461 */                       UITaskCompositeWizard.this.doDisplayTestSuccess();
/*      */                     }
/*      */                   });
/*      */             }
/*      */             else {
/*      */               
/*  467 */               UITaskCompositeWizard.this.getDisplay().asyncExec(new Runnable()
/*      */                   {
/*      */ 
/*      */                     
/*      */                     public void run()
/*      */                     {
/*  473 */                       UITaskCompositeWizard.this.doDisplayTestFailed();
/*      */                     }
/*      */                   });
/*      */             } 
/*      */           }
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  485 */     this.testT = new Thread(testThread);
/*  486 */     this.testT.start();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doDisplayTestFailed() {
/*  492 */     this.stat = 2;
/*      */     
/*  494 */     StyledText done = new StyledText(this, 74);
/*      */     
/*  496 */     done.setCursor(new Cursor((Device)getDisplay(), 0));
/*  497 */     done.setEditable(false);
/*  498 */     done.setEnabled(false);
/*      */     
/*  500 */     int y = this.height - 300;
/*      */     
/*  502 */     done.setBounds(0, y, this.width, 40);
/*  503 */     done.setForeground(DiskWin.colorDiskBG);
/*  504 */     done.setFont(this.introFont);
/*  505 */     done.setText("We seem to have a problem.  Please check that your cable is tightly connected (and plugged into the correct port).  You also might need to correct one of your previous choices.");
/*  506 */     done.setBounds(0, y, this.width, (done.getTextBounds(0, done.getCharCount() - 1)).height);
/*      */     
/*  508 */     y += (done.getBounds()).height + 15;
/*      */     
/*  510 */     Button tryagain = new Button(this, 0);
/*  511 */     tryagain.setText("Try the test again...");
/*  512 */     tryagain.setBounds(this.width / 2 - 100, y, 200, 24);
/*      */     
/*  514 */     tryagain.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  519 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  530 */     y += 35;
/*      */     
/*  532 */     Button chgport = new Button(this, 0);
/*  533 */     chgport.setText("Change serial port...");
/*  534 */     chgport.setBounds(this.width / 2 - 100, y, 200, 24);
/*      */     
/*  536 */     chgport.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  543 */             UITaskCompositeWizard.this.intesting = true;
/*  544 */             UITaskCompositeWizard.this.state = 2;
/*  545 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  556 */     y += 35;
/*      */     
/*  558 */     Button chgcoco = new Button(this, 0);
/*  559 */     chgcoco.setText("Change CoCo model...");
/*  560 */     chgcoco.setBounds(this.width / 2 - 100, y, 200, 24);
/*      */     
/*  562 */     chgcoco.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  569 */             UITaskCompositeWizard.this.intesting = true;
/*  570 */             UITaskCompositeWizard.this.state = 1;
/*  571 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doDisplayTestSuccess() {
/*  587 */     this.stat = 1;
/*      */     
/*  589 */     this.doit.setText("Apply configuration...");
/*      */     
/*  591 */     Label cocoman = new Label(this, 0);
/*  592 */     cocoman.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman6.png"));
/*  593 */     cocoman.setBounds(this.width / 2 - 171, this.height - 335, 342, 231);
/*      */     
/*  595 */     StyledText done = new StyledText(this, 74);
/*      */     
/*  597 */     done.setCursor(new Cursor((Device)getDisplay(), 0));
/*  598 */     done.setEditable(false);
/*  599 */     done.setEnabled(false);
/*      */     
/*  601 */     done.setBounds(0, this.height - 90, this.width, 40);
/*  602 */     done.setForeground(DiskWin.colorDiskBG);
/*  603 */     done.setFont(this.introFont);
/*  604 */     done.setText("The connection appears to be working fine, so let's finish by applying this configuration!");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setTestStatusText(final String txt) {
/*  612 */     if (!isDisposed()) {
/*  613 */       getDisplay().asyncExec(new Runnable()
/*      */           {
/*      */             
/*      */             public void run()
/*      */             {
/*  618 */               if (UITaskCompositeWizard.this.testStatusText != null && !UITaskCompositeWizard.this.testStatusText.isDisposed()) {
/*  619 */                 UITaskCompositeWizard.this.testStatusText.setText(txt);
/*      */               }
/*      */             }
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void appendTestStatusText(final String txt) {
/*  630 */     if (!isDisposed()) {
/*  631 */       getDisplay().asyncExec(new Runnable()
/*      */           {
/*      */             
/*      */             public void run()
/*      */             {
/*  636 */               if (UITaskCompositeWizard.this.testStatusText != null && !UITaskCompositeWizard.this.testStatusText.isDisposed()) {
/*  637 */                 UITaskCompositeWizard.this.testStatusText.append(txt);
/*      */               }
/*      */             }
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawCommTest2Controls() {
/*  647 */     int y = 10;
/*      */     
/*  649 */     StyledText intro = new StyledText(this, 74);
/*      */     
/*  651 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/*  652 */     intro.setEditable(false);
/*  653 */     intro.setEnabled(false);
/*      */     
/*  655 */     intro.setBounds(0, y, this.width, 130);
/*  656 */     intro.setForeground(DiskWin.colorDiskBG);
/*  657 */     intro.setFont(this.introFont);
/*  658 */     intro.setText("The wizard will now look for a special signal on " + this.device + ". This signal is generated by the CoCo every time it is powered on (or reset).");
/*  659 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/*  661 */     y += (intro.getBounds()).height + 25;
/*      */ 
/*      */     
/*  664 */     this.testStatusText = new StyledText(this, 74);
/*      */     
/*  666 */     this.testStatusText.setCursor(new Cursor((Device)getDisplay(), 0));
/*  667 */     this.testStatusText.setEditable(false);
/*  668 */     this.testStatusText.setEnabled(false);
/*      */     
/*  670 */     this.testStatusText.setBounds(40, y, this.width - 40, 60);
/*  671 */     this.testStatusText.setForeground(DiskWin.colorDiskBG);
/*  672 */     this.testStatusText.setFont(this.testStatusFont);
/*  673 */     this.testStatusText.setText("Waiting for test thread...");
/*      */     
/*  675 */     this.status.setVisible(true);
/*  676 */     this.status.setBounds(0, y, 32, 32);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  682 */     this.doit = new Button(this, 0);
/*  683 */     this.doit.setText("Finish without test");
/*      */     
/*  685 */     this.doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*  686 */     this.doit.setEnabled(true);
/*      */     
/*  688 */     this.doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  693 */             UITaskCompositeWizard.this.applyConfig();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  706 */     Button nothanks = new Button(this, 0);
/*  707 */     nothanks.setText("Cancel wizard");
/*  708 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/*  710 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  716 */             UITaskCompositeWizard.this.state = -1;
/*  717 */             UITaskCompositeWizard.this.drawControls();
/*  718 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawCommTest1Controls() {
/*  737 */     int y = 10;
/*      */     
/*  739 */     StyledText intro = new StyledText(this, 74);
/*      */     
/*  741 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/*  742 */     intro.setEditable(false);
/*  743 */     intro.setEnabled(false);
/*      */     
/*  745 */     intro.setBounds(0, y, this.width, 130);
/*  746 */     intro.setForeground(DiskWin.colorDiskBG);
/*  747 */     intro.setFont(this.introFont);
/*  748 */     intro.setText("OK! Before we start the test, first please turn off your " + this.cocodevname + ".");
/*  749 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*  750 */     intro.append("Next, connect a serial cable from the CoCo's bitbanger port (the port labeled \"SERIAL I/O\") to this computer's " + this.device + " port.");
/*  751 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*  752 */     intro.append("When you have the cable connected, click the button below:");
/*      */     
/*  754 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/*  756 */     y += (intro.getBounds()).height + 15;
/*      */     
/*  758 */     Button begin = new Button(this, 0);
/*      */     
/*  760 */     begin.setText("Begin the test!");
/*  761 */     begin.setBounds(this.width / 2 - 100, y, 200, 24);
/*      */     
/*  763 */     begin.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  768 */             UITaskCompositeWizard.this.state = 7;
/*  769 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  783 */     Label cocoman1 = new Label(this, 0);
/*  784 */     cocoman1.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman7.png"));
/*      */     
/*  786 */     cocoman1.setBounds(this.width / 2 - 178, this.height - 310, 356, 258);
/*      */ 
/*      */     
/*  789 */     Button doit = new Button(this, 0);
/*  790 */     doit.setText("Finish without test");
/*      */     
/*  792 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*  793 */     doit.setEnabled(true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  798 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  803 */             UITaskCompositeWizard.this.applyConfig();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  816 */     Button nothanks = new Button(this, 0);
/*  817 */     nothanks.setText("Cancel wizard");
/*  818 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/*  820 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  826 */             UITaskCompositeWizard.this.state = -1;
/*  827 */             UITaskCompositeWizard.this.drawControls();
/*  828 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawPrinterSelectControls() {
/*  845 */     int y = 10;
/*      */     
/*  847 */     StyledText intro = new StyledText(this, 74);
/*      */     
/*  849 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/*  850 */     intro.setEditable(false);
/*  851 */     intro.setEnabled(false);
/*      */     
/*  853 */     intro.setBounds(0, y, this.width, 130);
/*  854 */     intro.setForeground(DiskWin.colorDiskBG);
/*  855 */     intro.setFont(this.introFont);
/*  856 */     intro.setText("There are two different types of printer output available.  You can switch at any time, but DriveWire will default to the type you choose here.");
/*  857 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*  858 */     intro.append("The 'text' option will produce files containing plain text.  No interpretation of the contents is performed.  These files can be read by any text editor. ");
/*  859 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*  860 */     intro.append("The 'FX80' option will produce image files containing the output of a simulated Epson FX-80 printer.  FX-80 control codes are interpretted.  These files can be viewed with an image viewer.");
/*      */     
/*  862 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/*  864 */     y += (intro.getBounds()).height + 15;
/*      */     
/*  866 */     final Button text = new Button(this, 16);
/*  867 */     text.setText("Default to text printer");
/*  868 */     text.setBackground(SWTResourceManager.getColor(1));
/*      */     
/*  870 */     text.setBounds(this.width / 2 - 200, y, 200, 24);
/*      */     
/*  872 */     Button fx80 = new Button(this, 16);
/*  873 */     fx80.setText("Default to FX-80 printer");
/*  874 */     fx80.setBackground(SWTResourceManager.getColor(1));
/*      */     
/*  876 */     fx80.setBounds(this.width / 2, y, 200, 24);
/*      */     
/*  878 */     text.setSelection(true);
/*      */     
/*  880 */     y += 60;
/*      */     
/*  882 */     Label cocoman3 = new Label(this, 0);
/*  883 */     cocoman3.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman5.png"));
/*  884 */     cocoman3.setBounds(0, y, 192, 179);
/*      */     
/*  886 */     StyledText more1 = new StyledText(this, 74);
/*      */     
/*  888 */     more1.setCursor(new Cursor((Device)getDisplay(), 0));
/*  889 */     more1.setEditable(false);
/*  890 */     more1.setEnabled(false);
/*      */     
/*  892 */     more1.setBounds(210, y, this.width - 210, 130);
/*  893 */     more1.setForeground(DiskWin.colorDiskBG);
/*  894 */     more1.setFont(this.introFont);
/*  895 */     more1.setText("When the printer is flushed, DriveWire will create a file containing the output.  Please choose where you would like these files to be created:");
/*      */ 
/*      */     
/*  898 */     more1.setBounds(210, y, this.width - 210, (more1.getTextBounds(0, more1.getCharCount() - 1)).height);
/*      */     
/*  900 */     y += (more1.getBounds()).height + 15;
/*      */     
/*  902 */     final Text pdir = new Text(this, 2056);
/*  903 */     pdir.setText("cocoprints");
/*  904 */     pdir.setBounds(210, y, this.width - 220, 24);
/*      */     
/*  906 */     y += 30;
/*      */     
/*  908 */     Button getdir = new Button(this, 0);
/*  909 */     getdir.setText("Choose a directory...");
/*  910 */     getdir.setBounds(210 + (this.width - 210) / 2 - 90, y, 180, 24);
/*      */     
/*  912 */     getdir.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  918 */             String res = MainWin.getFile(false, true, pdir.getText(), "Choose a directory for printer output..", "Open");
/*      */             
/*  920 */             if (res != null)
/*      */             {
/*  922 */               pdir.setText(res);
/*      */             }
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  941 */     Button doit = new Button(this, 0);
/*  942 */     doit.setText("Continue...");
/*      */     
/*  944 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*  945 */     doit.setEnabled(true);
/*      */ 
/*      */     
/*  948 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  953 */             UITaskCompositeWizard.this.printerdir = pdir.getText();
/*      */             
/*  955 */             if (text.getSelection()) {
/*  956 */               UITaskCompositeWizard.this.printertype = "Text";
/*      */             } else {
/*  958 */               UITaskCompositeWizard.this.printertype = "FX80";
/*      */             } 
/*  960 */             UITaskCompositeWizard.this.state = 5;
/*  961 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  973 */     Button nothanks = new Button(this, 0);
/*  974 */     nothanks.setText("Cancel wizard");
/*  975 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/*  977 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  983 */             UITaskCompositeWizard.this.state = -1;
/*  984 */             UITaskCompositeWizard.this.drawControls();
/*  985 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawMIDISelectControls() {
/* 1001 */     int y = 0;
/*      */     
/* 1003 */     Label cocoman3 = new Label(this, 0);
/* 1004 */     cocoman3.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman4.png"));
/* 1005 */     cocoman3.setBounds(this.width / 2 - 196, y, 391, 277);
/*      */     
/* 1007 */     y += 295;
/*      */     
/* 1009 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 1011 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1012 */     intro.setEditable(false);
/* 1013 */     intro.setEnabled(false);
/*      */     
/* 1015 */     intro.setBounds(0, y, this.width, 130);
/* 1016 */     intro.setForeground(DiskWin.colorDiskBG);
/* 1017 */     intro.setFont(this.introFont);
/* 1018 */     intro.setText("DriveWire 4 can provide a virtual MIDI device which can be used with any OS-9 software running on your CoCo.  The device is not accessible from DECB at this time.");
/* 1019 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/* 1020 */     intro.append("Would you like to enable MIDI support?");
/* 1021 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/* 1023 */     y += (intro.getBounds()).height + 15;
/*      */     
/* 1025 */     final Button yes = new Button(this, 16);
/* 1026 */     yes.setText("Yes");
/* 1027 */     yes.setBackground(SWTResourceManager.getColor(1));
/*      */     
/* 1029 */     yes.setBounds(this.width / 2 - 100, y, 100, 24);
/*      */     
/* 1031 */     Button no = new Button(this, 16);
/* 1032 */     no.setText("No Thanks");
/* 1033 */     no.setBackground(SWTResourceManager.getColor(1));
/*      */     
/* 1035 */     no.setBounds(this.width / 2, y, 100, 24);
/*      */     
/* 1037 */     yes.setSelection(true);
/*      */ 
/*      */     
/* 1040 */     Button doit = new Button(this, 0);
/* 1041 */     doit.setText("Continue...");
/*      */     
/* 1043 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/* 1044 */     doit.setEnabled(true);
/*      */ 
/*      */     
/* 1047 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1052 */             if (yes.getSelection()) {
/*      */               
/* 1054 */               UITaskCompositeWizard.this.usemidi = true;
/*      */             }
/*      */             else {
/*      */               
/* 1058 */               UITaskCompositeWizard.this.usemidi = false;
/*      */             } 
/* 1060 */             UITaskCompositeWizard.this.state = 4;
/* 1061 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1073 */     Button nothanks = new Button(this, 0);
/* 1074 */     nothanks.setText("Cancel wizard");
/* 1075 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/* 1077 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1083 */             UITaskCompositeWizard.this.state = -1;
/* 1084 */             UITaskCompositeWizard.this.drawControls();
/* 1085 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawWizardSuccessControls() {
/* 1104 */     this.stat = 1;
/*      */     
/* 1106 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 1108 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1109 */     intro.setEditable(false);
/* 1110 */     intro.setEnabled(false);
/* 1111 */     intro.setAlignment(16777216);
/* 1112 */     intro.setBounds(0, 4, this.width, 20);
/* 1113 */     intro.setForeground(DiskWin.colorDiskBG);
/* 1114 */     intro.setFont(this.introFont);
/*      */     
/* 1116 */     intro.setText("Congratulations, DriveWire is now ready to use with your " + this.cocodevname + "!");
/*      */     
/* 1118 */     MainWin.setSashformWeights(this.sashform_orig);
/*      */     
/* 1120 */     MainWin.taskman.updateTask(this.tid, 1, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawCommTestChooseControls() {
/* 1130 */     int y = 20;
/*      */     
/* 1132 */     Label cocoman3 = new Label(this, 0);
/* 1133 */     cocoman3.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman3.png"));
/* 1134 */     cocoman3.setBounds(this.width / 2 - 144, y, 288, 160);
/*      */     
/* 1136 */     y += 180;
/*      */     
/* 1138 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 1140 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1141 */     intro.setEditable(false);
/* 1142 */     intro.setEnabled(false);
/*      */     
/* 1144 */     intro.setBounds(0, y, this.width, 130);
/* 1145 */     intro.setForeground(DiskWin.colorDiskBG);
/* 1146 */     intro.setFont(this.introFont);
/* 1147 */     intro.setText("We're almost finished!  In fact, you can choose to complete the wizard now if you do not wish to test the configuration.");
/*      */     
/* 1149 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/*      */     
/* 1151 */     intro.append("In order to test the connection between your " + this.cocodevname + " and this computer, ");
/* 1152 */     intro.append("you must have a serial cable connected between " + this.device + " and the CoCo's bitbanger port.  ");
/* 1153 */     intro.append("You do not need a DriveWire ROM or any special software on the CoCo to perform the test.");
/*      */     
/* 1155 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/* 1157 */     y += 20 + (intro.getBounds()).height;
/*      */     
/* 1159 */     Button testit = new Button(this, 0);
/* 1160 */     testit.setText("Test Configuration...");
/* 1161 */     testit.setBounds(this.width / 2 - 100, y, 200, 24);
/* 1162 */     testit.setEnabled(true);
/*      */     
/* 1164 */     testit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1170 */             UITaskCompositeWizard.this.state = 6;
/* 1171 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1182 */     y += 45;
/*      */     
/* 1184 */     Link link = new Link(this, 0);
/* 1185 */     link.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1189 */             MainWin.doDisplayAsync(new Runnable()
/*      */                 {
/*      */                   
/*      */                   public void run()
/*      */                   {
/* 1194 */                     MainWin.openURL(getClass(), "http://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=DriveWire_cables");
/*      */                   }
/*      */                 });
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/* 1202 */     link.setBounds(this.width / 2 - 120, y, 240, 40);
/*      */     
/* 1204 */     link.setText("<a>Yikes!  I don't have a DriveWire cable!</a>");
/* 1205 */     link.setBackground(SWTResourceManager.getColor(1));
/* 1206 */     link.setFont(this.introFont);
/*      */ 
/*      */ 
/*      */     
/* 1210 */     Button doit = new Button(this, 0);
/* 1211 */     doit.setText("Finish without test");
/*      */     
/* 1213 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/* 1214 */     doit.setEnabled(true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1219 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1224 */             UITaskCompositeWizard.this.applyConfig();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1237 */     Button nothanks = new Button(this, 0);
/* 1238 */     nothanks.setText("Cancel wizard");
/* 1239 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/* 1241 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1247 */             UITaskCompositeWizard.this.state = -1;
/* 1248 */             UITaskCompositeWizard.this.drawControls();
/* 1249 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void applyConfig() {
/* 1269 */     Thread t = new Thread(new Runnable()
/*      */         {
/*      */ 
/*      */           
/*      */           public void run()
/*      */           {
/* 1275 */             boolean fail = false;
/*      */ 
/*      */             
/*      */             try {
/* 1279 */               int rate = UITaskCompositeWizard.this.fpgarate;
/*      */               
/* 1281 */               if (UITaskCompositeWizard.this.cocomodel.intValue() == 1) {
/* 1282 */                 rate = 38400;
/* 1283 */               } else if (UITaskCompositeWizard.this.cocomodel.intValue() == 2) {
/* 1284 */                 rate = 57600;
/*      */               } 
/* 1286 */               UIUtils.simpleConfigServer(rate, UITaskCompositeWizard.this.cocodevname, UITaskCompositeWizard.this.device, UITaskCompositeWizard.this.usemidi, UITaskCompositeWizard.this.printertype, UITaskCompositeWizard.this.printerdir);
/*      */             }
/* 1288 */             catch (DWUIOperationFailedException e1) {
/*      */ 
/*      */               
/* 1291 */               fail = true;
/*      */             }
/* 1293 */             catch (IOException e1) {
/*      */               
/* 1295 */               fail = true;
/*      */             } 
/*      */             
/* 1298 */             if (!fail)
/*      */             {
/*      */               
/* 1301 */               UITaskCompositeWizard.this.getDisplay().asyncExec(new Runnable()
/*      */                   {
/*      */                     
/*      */                     public void run()
/*      */                     {
/* 1306 */                       UITaskCompositeWizard.this.state = 9;
/* 1307 */                       UITaskCompositeWizard.this.drawControls();
/*      */                     }
/*      */                   });
/*      */             }
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1318 */     t.start();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void startSerialMonitor() {
/* 1324 */     Runnable portMonitor;
/*      */     
/* 1418 */
    portMonitor = new Runnable()
    /*      */       {
        /* 1326 */         ArrayList<String> ports = new ArrayList<String>();
        /*      */
        /*      */
        /*      */         public void run() {
            /* 1330 */           if (UITaskCompositeWizard.this.monitorPorts && !UITaskCompositeWizard.this.isDisposed() && UITaskCompositeWizard.this.portlist != null && UITaskCompositeWizard.this.manualPorts != null && !UITaskCompositeWizard.this.updatingPorts.booleanValue()) {
                /*      */
                /*      */
                /* 1333 */             synchronized (UITaskCompositeWizard.this.updatingPorts) {
                    /*      */
                    /* 1335 */               UITaskCompositeWizard.this.updatingPorts = Boolean.valueOf(true);
                /*      */             }
                /*      */
                /* 1338 */             TableItem[] items = UITaskCompositeWizard.this.portlist.getItems();
                /*      */
                /* 1340 */             for (TableItem i : items) {
                    /*      */
                    /* 1342 */               if (!this.ports.contains(i.getText(0)) && !UITaskCompositeWizard.this.manualPorts.contains(i.getText(0)))
                    /*      */               {
                        /* 1344 */                 i.setText(1, "Failed, uplugged adapter?");
                    /*      */               }
                /*      */             }
                /*      */
                /*      */
                /* 1349 */             for (String p : this.ports) {
                    /*      */
                    /* 1351 */               if (UITaskCompositeWizard.this.manualPorts.contains(p)) {
                        /* 1352 */                 UITaskCompositeWizard.this.manualPorts.remove(p);
                    /*      */               }
                /*      */             }
                /*      */
                /* 1356 */             Runnable updater;
                /*      */
                /*      */
                /*      */
                /* 1406 */
                updater = new Runnable()
                /*      */               {
                    private ArrayList<String> ports;
                    /*      */
                    /*      */
                    /*      */
                    /*      */
                    /*      */                 public void run()
                    /*      */                 {
                        /* 1364 */                   this.ports = DriveWireServer.getAvailableSerialPorts();
                        
                        /*      */
                        /* 1366 */                   if (!UITaskCompositeWizard.this.isDisposed())
                            /* 1367 */                     for (String p : this.ports) {
                        /*      */
                        /* 1369 */                       final String port = p;
                        /* 1370 */                       final String stat = DriveWireServer.getSerialPortStatus(port);
                        /*      */
                        /* 1372 */                       if (!UITaskCompositeWizard.this.isDisposed()) {
                            /* 1373 */                         UITaskCompositeWizard.this.master.getDisplay().syncExec(new Runnable()
                            /*      */                             {
                                /*      */                               public void run()
                                /*      */                               {
                                    /* 1377 */                                 UITaskCompositeWizard.this.updatePortEntry(port, stat);
                                /*      */                               }
                            /*      */                             });
                        /*      */                       }
                    /*      */                     }
                            /* 1382 */                   if (!UITaskCompositeWizard.this.isDisposed())
                                /* 1383 */                     for (String p : UITaskCompositeWizard.this.manualPorts) {
                                    /*      */
                                    /* 1385 */                       final String port = p;
                                    /* 1386 */                       final String stat = DriveWireServer.getSerialPortStatus(port);
                                    /* 1387 */                       if (!UITaskCompositeWizard.this.isDisposed()) {
                                        /* 1388 */                         UITaskCompositeWizard.this.master.getDisplay().syncExec(new Runnable()
                                        /*      */                             {
                                            /*      */                               public void run()
                                            /*      */                               {
                                                /* 1392 */                                 UITaskCompositeWizard.this.updatePortEntry(port, stat);
                                            /*      */                               }
                                        /*      */                             });
                                    /*      */                       }
                                /*      */                     }
                            /* 1397 */                   synchronized (UITaskCompositeWizard.this.updatingPorts) {
                                /*      */
                                /* 1399 */                     UITaskCompositeWizard.this.updatingPorts = Boolean.valueOf(false);
                            /*      */                   }
                    /*      */                 }
                /*      */               };
                Thread t = new Thread(updater);
                /* 1407 */             t.start();
            /*      */           }
            /*      */
            /*      */
            /*      */
            /* 1412 */           if (!UITaskCompositeWizard.this.isDisposed() && UITaskCompositeWizard.this.state == 2) {
                /* 1413 */             UITaskCompositeWizard.this.getDisplay().timerExec(2000, this);
            /*      */           }
        /*      */         }
    /*      */       };
 getDisplay().timerExec(2000, portMonitor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawSerialSelectControls() {
/* 1426 */     this.monitorPorts = true;
/*      */     
/* 1428 */     int y = 10;
/*      */     
/* 1430 */     startSerialMonitor();
/*      */ 
/*      */     
/* 1433 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 1435 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1436 */     intro.setEditable(false);
/* 1437 */     intro.setEnabled(false);
/*      */     
/* 1439 */     intro.setBounds(0, y, this.width, 130);
/* 1440 */     intro.setForeground(DiskWin.colorDiskBG);
/* 1441 */     intro.setFont(this.introFont);
/* 1442 */     intro.setText("Next, we must choose which serial port to use when communicating with your " + this.cocodevname + ".  If you're not sure which is which, don't worry!  We will test things later and make any changes necessary." + intro.getLineDelimiter() + intro.getLineDelimiter());
/* 1443 */     intro.append("If your computer does not have a serial port built in, you may want to acquire an inexpensive USB or Bluetooth serial adapter." + intro.getLineDelimiter() + intro.getLineDelimiter());
/*      */     
/* 1445 */     intro.append("DriveWire has detected the following ports:");
/*      */     
/* 1447 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */ 
/*      */     
/* 1450 */     y += (intro.getBounds()).height;
/*      */     
/* 1452 */     Label cocoman2 = new Label(this, 0);
/* 1453 */     cocoman2.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman2.png"));
/* 1454 */     cocoman2.setBounds(this.width - 129, y, 119, 97);
/*      */     
/* 1456 */     y += 15;
/*      */     
/* 1458 */     this.portlist.setBounds(0, y, this.width - 130, 97);
/*      */     
/* 1460 */     this.portlist.setVisible(true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1465 */     y += (this.portlist.getBounds()).height + 10;
/*      */     
/* 1467 */     this.stat = 0;
/* 1468 */     this.status.setBounds(0, y, 32, 32);
/* 1469 */     this.status.setVisible(true);
/*      */ 
/*      */     
/* 1472 */     StyledText more1 = new StyledText(this, 74);
/*      */     
/* 1474 */     more1.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1475 */     more1.setEditable(false);
/* 1476 */     more1.setEnabled(false);
/*      */     
/* 1478 */     more1.setBounds(42, y + 5, this.width - 42, 32);
/* 1479 */     more1.setForeground(DiskWin.colorDiskBG);
/* 1480 */     more1.setFont(this.smallFont);
/* 1481 */     more1.setText("The wizard is watching for any changes.  If you will be using an adapter, please plug it into the computer now.");
/* 1482 */     more1.setBounds(42, y + (32 - (more1.getTextBounds(0, more1.getCharCount() - 1)).height) / 2, this.width - 42, (more1.getTextBounds(0, more1.getCharCount() - 1)).height);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1488 */     y += Math.max(57, (more1.getBounds()).height + 25);
/*      */     
/* 1490 */     StyledText more2 = new StyledText(this, 74);
/*      */     
/* 1492 */     more2.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1493 */     more2.setEditable(false);
/* 1494 */     more2.setEnabled(false);
/*      */     
/* 1496 */     more2.setBounds(0, y, this.width, 50);
/* 1497 */     more2.setForeground(DiskWin.colorDiskBG);
/* 1498 */     more2.setFont(this.introFont);
/*      */     
/* 1500 */     more2.setText("If DriveWire does not detect your serial port, you can enter the name below:");
/* 1501 */     more2.setBounds(0, y, this.width, (more2.getTextBounds(0, more2.getCharCount() - 1)).height);
/*      */     
/* 1503 */     y += (more2.getBounds()).height + 15;
/*      */     
/* 1505 */     int mo = Math.max((more2.getTextBounds(0, more2.getCharCount() - 1)).width, 270);
/*      */ 
/*      */     
/* 1508 */     final Text mandev = new Text(this, 2048);
/* 1509 */     mandev.setBounds(mo - 260, y, 140, 24);
/*      */     
/* 1511 */     Button manadd = new Button(this, 0);
/* 1512 */     manadd.setText("Add port");
/* 1513 */     manadd.setBounds(mo - 100, y, 100, 24);
/*      */     
/* 1515 */     manadd.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1520 */             if (mandev.getText() != null && !mandev.getText().equals("") && UITaskCompositeWizard.this.validatePortName(mandev.getText())) {
/*      */               
/* 1522 */               UITaskCompositeWizard.this.manualPorts.add(mandev.getText());
/* 1523 */               mandev.setText("");
/*      */             } 
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1537 */     y += 80;
/*      */     
/* 1539 */     StyledText more3 = new StyledText(this, 74);
/*      */     
/* 1541 */     more3.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1542 */     more3.setEditable(false);
/* 1543 */     more3.setEnabled(false);
/*      */     
/* 1545 */     more3.setBounds(0, y, this.width, 50);
/* 1546 */     more3.setForeground(DiskWin.colorDiskBG);
/* 1547 */     more3.setFont(this.introFont);
/*      */     
/* 1549 */     more3.setText("Select a serial port with the status 'Available' from the list above to continue.");
/* 1550 */     more3.setBounds(0, this.height - (more3.getTextBounds(0, more3.getCharCount() - 1)).height - 50, this.width, (more3.getTextBounds(0, more3.getCharCount() - 1)).height);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1556 */     final Button doit = new Button(this, 0);
/* 1557 */     doit.setText("Choose a port");
/*      */     
/* 1559 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/* 1560 */     doit.setEnabled(false);
/*      */ 
/*      */     
/* 1563 */     this.portlist.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1570 */             if (!doit.isDisposed())
/*      */             {
/* 1572 */               if (UITaskCompositeWizard.this.portlist.getItem(UITaskCompositeWizard.this.portlist.getSelectionIndex()).getText(1).equals("Available")) {
/*      */                 
/* 1574 */                 UITaskCompositeWizard.this.device = UITaskCompositeWizard.this.portlist.getItem(UITaskCompositeWizard.this.portlist.getSelectionIndex()).getText(0);
/* 1575 */                 doit.setText("Use " + UITaskCompositeWizard.this.shortDevName(UITaskCompositeWizard.this.device) + "...");
/* 1576 */                 doit.setEnabled(true);
/*      */ 
/*      */               
/*      */               }
/*      */               else {
/*      */ 
/*      */                 
/* 1583 */                 doit.setEnabled(false);
/*      */               } 
/*      */             }
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1598 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1603 */             if (UITaskCompositeWizard.this.intesting) {
/* 1604 */               UITaskCompositeWizard.this.state = 6;
/*      */             } else {
/* 1606 */               UITaskCompositeWizard.this.state = 3;
/* 1607 */             }  UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1620 */     Button nothanks = new Button(this, 0);
/* 1621 */     nothanks.setText("Cancel wizard");
/* 1622 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/* 1624 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1630 */             UITaskCompositeWizard.this.state = -1;
/* 1631 */             UITaskCompositeWizard.this.drawControls();
/* 1632 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String shortDevName(String dev) {
/* 1653 */     if (dev.lastIndexOf('/') > -1 && dev.lastIndexOf('/') < dev.length() - 2)
/*      */     {
/* 1655 */       return dev.substring(dev.lastIndexOf('/') + 1);
/*      */     }
/*      */     
/* 1658 */     return dev;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean validatePortName(String text) {
/* 1665 */     Matcher m = this.unixstyle.matcher(text);
/*      */     
/* 1667 */     if (m.matches()) {
/* 1668 */       return true;
/*      */     }
/* 1670 */     m = this.winstyle.matcher(text);
/*      */     
/* 1672 */     if (m.matches()) {
/* 1673 */       return true;
/*      */     }
/*      */     
/* 1676 */     MessageBox messageBox = new MessageBox(getShell(), 200);
/* 1677 */     messageBox.setMessage("The name '" + text + "' does not look like a typical device name." + System.getProperty("line.separator") + System.getProperty("line.separator") + "On Windows computers, device names look like 'COM1'." + System.getProperty("line.separator") + System.getProperty("line.separator") + "On *nix and Mac systems they look like '/dev/ttyS0'." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Are you sure this is a valid device name?");
/*      */ 
/*      */ 
/*      */     
/* 1681 */     messageBox.setText("Unexpected device name");
/* 1682 */     int rc = messageBox.open();
/*      */     
/* 1684 */     if (rc == 64) {
/* 1685 */       return true;
/*      */     }
/* 1687 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updatePortEntry(String port, String status) {
/* 1694 */     if (this.portlist != null && !this.portlist.isDisposed()) {
/*      */ 
/*      */ 
/*      */       
/* 1698 */       if (status.equals("NoSuchPortException: null")) {
/* 1699 */         status = "This port does not exist";
/* 1700 */       } else if (status.startsWith("PortInUseException: ") && status.length() > 20) {
/* 1701 */         status = "In use by " + status.substring(20);
/* 1702 */       } else if (status.equals("In use by DriveWire")) {
/* 1703 */         status = "Available";
/*      */       } 
/* 1705 */       TableItem[] items = this.portlist.getItems();
/*      */       
/* 1707 */       for (TableItem tableItem : items) {
/*      */         
/* 1709 */         if (tableItem.getText(0).equals(port)) {
/*      */           
/* 1711 */           tableItem.setText(1, status);
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/* 1716 */       TableItem ti = new TableItem(this.portlist, 0);
/* 1717 */       ti.setText(0, port);
/* 1718 */       ti.setText(1, status);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawCoCoSelectControls() {
/* 1728 */     int y = 20;
/*      */     
/* 1730 */     Label cocoman = new Label(this, 0);
/* 1731 */     cocoman.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/cocoman1.png"));
/*      */     
/* 1733 */     cocoman.setBounds(this.width - 170, y, 170, 177);
/*      */     
/* 1735 */     y += 30;
/*      */     
/* 1737 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 1739 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1740 */     intro.setEditable(false);
/* 1741 */     intro.setEnabled(false);
/*      */     
/* 1743 */     intro.setBounds(0, y, this.width - 180, 60);
/* 1744 */     intro.setForeground(DiskWin.colorDiskBG);
/* 1745 */     intro.setFont(this.introFont);
/* 1746 */     intro.setText("This wizard will help you generate a basic configuration.  It assumes you wish to run everything on a single PC and connect via a serial port to a CoCo.  For other options, please use the server configuration tool in the Config menu.");
/* 1747 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/* 1748 */     intro.append("Let's get started!");
/* 1749 */     intro.append(intro.getLineDelimiter() + intro.getLineDelimiter());
/* 1750 */     intro.append("First, we need to know what type of CoCo you would like to connect.  Please choose your model from the options below:");
/* 1751 */     intro.setBounds(0, y, this.width - 180, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/* 1753 */     y += 215;
/*      */     
/* 1755 */     int bwidth = 105;
/* 1756 */     int bheight = 81;
/* 1757 */     int gap = 15;
/* 1758 */     int loff = this.width / 2 - bwidth + bwidth + gap + gap / 2;
/* 1759 */     int toff = y;
/* 1760 */     int txtoff = toff + bheight + 5;
/* 1761 */     int bstyle = 2;
/* 1762 */     final Color tcolor = DiskWin.colorDiskGraphBG;
/* 1763 */     final Color tacolor = SWTResourceManager.getColor(2);
/*      */     
/* 1765 */     final Button coco1 = new Button(this, bstyle);
/* 1766 */     coco1.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/coco1.png"));
/* 1767 */     coco1.setBounds(loff, toff, bwidth, bheight);
/*      */ 
/*      */ 
/*      */     
/* 1771 */     final StyledText coco1txt = new StyledText(this, 64);
/* 1772 */     coco1txt.setAlignment(16777216);
/* 1773 */     coco1txt.setBounds(loff, txtoff, bwidth, 20);
/* 1774 */     coco1txt.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1775 */     coco1txt.setEditable(false);
/* 1776 */     coco1txt.setEnabled(false);
/* 1777 */     coco1txt.setFont(this.introFont);
/* 1778 */     coco1txt.setForeground(tcolor);
/* 1779 */     coco1txt.setText("CoCo 1");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1785 */     final Button coco2 = new Button(this, bstyle);
/* 1786 */     coco2.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/coco2.png"));
/* 1787 */     coco2.setBounds(loff + bwidth + gap, toff, bwidth, bheight);
/*      */     
/* 1789 */     final StyledText coco2txt = new StyledText(this, 64);
/* 1790 */     coco2txt.setAlignment(16777216);
/* 1791 */     coco2txt.setBounds(loff + bwidth + gap, txtoff, bwidth, 20);
/* 1792 */     coco2txt.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1793 */     coco2txt.setEditable(false);
/* 1794 */     coco2txt.setEnabled(false);
/* 1795 */     coco2txt.setFont(this.introFont);
/* 1796 */     coco2txt.setForeground(tcolor);
/* 1797 */     coco2txt.setText("CoCo 2");
/*      */     
/* 1799 */     final Button coco3 = new Button(this, bstyle);
/* 1800 */     coco3.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/coco3.png"));
/* 1801 */     coco3.setBounds(loff + bwidth + gap + bwidth + gap, toff, bwidth, bheight);
/* 1802 */     coco3.setSelection(true);
/*      */     
/* 1804 */     final StyledText coco3txt = new StyledText(this, 64);
/* 1805 */     coco3txt.setAlignment(16777216);
/* 1806 */     coco3txt.setBounds(loff + bwidth + gap + bwidth + gap, txtoff, bwidth, 20);
/* 1807 */     coco3txt.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1808 */     coco3txt.setEditable(false);
/* 1809 */     coco3txt.setEnabled(false);
/* 1810 */     coco3txt.setFont(this.introFont);
/* 1811 */     coco3txt.setForeground(tacolor);
/* 1812 */     coco3txt.setText("CoCo 3");
/*      */ 
/*      */     
/* 1815 */     final Button fpga = new Button(this, bstyle);
/* 1816 */     fpga.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/fpga.png"));
/* 1817 */     fpga.setBounds(loff + bwidth + gap + bwidth + gap + bwidth + gap, toff, bwidth, bheight);
/*      */     
/* 1819 */     final StyledText fpgatxt = new StyledText(this, 64);
/* 1820 */     fpgatxt.setAlignment(16777216);
/* 1821 */     fpgatxt.setBounds(loff + bwidth + gap + bwidth + gap + bwidth + gap, txtoff, bwidth, 20);
/* 1822 */     fpgatxt.setCursor(new Cursor((Device)getDisplay(), 0));
/* 1823 */     fpgatxt.setEditable(false);
/* 1824 */     fpgatxt.setEnabled(false);
/* 1825 */     fpgatxt.setFont(this.introFont);
/* 1826 */     fpgatxt.setForeground(tcolor);
/* 1827 */     fpgatxt.setText("CoCo3FPGA");
/*      */ 
/*      */     
/* 1830 */     final Button doit = new Button(this, 0);
/* 1831 */     doit.setText("Choose CoCo 3..");
/* 1832 */     this.cocodevname = "CoCo 3";
/*      */     
/* 1834 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*      */     
/* 1836 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 1841 */             if (UITaskCompositeWizard.this.intesting) {
/* 1842 */               UITaskCompositeWizard.this.state = 6;
/*      */             } else {
/* 1844 */               UITaskCompositeWizard.this.state = 2;
/* 1845 */             }  UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 1860 */     coco1.setData("cocomodel", Integer.valueOf(1));
/* 1861 */     coco1.setData("cocodevname", "CoCo 1");
/*      */     
/* 1863 */     coco2.setData("cocomodel", Integer.valueOf(2));
/* 1864 */     coco2.setData("cocodevname", "CoCo 2");
/*      */     
/* 1866 */     coco3.setData("cocomodel", Integer.valueOf(3));
/* 1867 */     coco3.setData("cocodevname", "CoCo 3");
/*      */     
/* 1869 */     fpga.setData("cocomodel", Integer.valueOf(4));
/* 1870 */     fpga.setData("cocodevname", "FPGA board");
/*      */ 
/*      */     
/* 1873 */     SelectionListener cocorg = new SelectionListener()
/*      */       {
/*      */ 
/*      */         
/*      */         public void widgetSelected(SelectionEvent e)
/*      */         {
/* 1879 */           synchronized (UITaskCompositeWizard.this.cocomodel) {
/*      */             
/* 1881 */             coco1.setSelection(false);
/* 1882 */             coco2.setSelection(false);
/* 1883 */             coco3.setSelection(false);
/* 1884 */             fpga.setSelection(false);
/* 1885 */             coco1txt.setForeground(tcolor);
/* 1886 */             coco2txt.setForeground(tcolor);
/* 1887 */             coco3txt.setForeground(tcolor);
/* 1888 */             fpgatxt.setForeground(tcolor);
/*      */             
/* 1890 */             UITaskCompositeWizard.this.cocomodel = Integer.valueOf(Integer.parseInt(e.widget.getData("cocomodel").toString()));
/* 1891 */             UITaskCompositeWizard.this.cocodevname = e.widget.getData("cocodevname").toString();
/* 1892 */             doit.setText("Choose " + UITaskCompositeWizard.this.cocodevname + "..");
/*      */             
/* 1894 */             if (UITaskCompositeWizard.this.cocomodel.intValue() == 1) {
/*      */               
/* 1896 */               coco1.setSelection(true);
/* 1897 */               coco1txt.setForeground(tacolor);
/*      */             }
/* 1899 */             else if (UITaskCompositeWizard.this.cocomodel.intValue() == 2) {
/*      */               
/* 1901 */               coco2.setSelection(true);
/* 1902 */               coco2txt.setForeground(tacolor);
/*      */             }
/* 1904 */             else if (UITaskCompositeWizard.this.cocomodel.intValue() == 3) {
/*      */               
/* 1906 */               coco3.setSelection(true);
/* 1907 */               coco3txt.setForeground(tacolor);
/*      */             }
/* 1909 */             else if (UITaskCompositeWizard.this.cocomodel.intValue() == 4) {
/*      */               
/* 1911 */               fpga.setSelection(true);
/* 1912 */               fpgatxt.setForeground(tacolor);
/*      */             } 
/*      */           } 
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         public void widgetDefaultSelected(SelectionEvent e) {}
/*      */       };
/* 1927 */     coco1.addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter()
/*      */         {
/*      */           
/*      */           public void mouseEnter(MouseEvent e)
/*      */           {
/* 1932 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 21));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void mouseExit(MouseEvent e) {
/* 1939 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 0));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1944 */     coco2.addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter()
/*      */         {
/*      */           
/*      */           public void mouseEnter(MouseEvent e)
/*      */           {
/* 1949 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 21));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void mouseExit(MouseEvent e) {
/* 1956 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 0));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1961 */     coco3.addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter()
/*      */         {
/*      */           
/*      */           public void mouseEnter(MouseEvent e)
/*      */           {
/* 1966 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 21));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void mouseExit(MouseEvent e) {
/* 1973 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 0));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1978 */     fpga.addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter()
/*      */         {
/*      */           
/*      */           public void mouseEnter(MouseEvent e)
/*      */           {
/* 1983 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 21));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void mouseExit(MouseEvent e) {
/* 1990 */             UITaskCompositeWizard.this.setCursor(new Cursor((Device)UITaskCompositeWizard.this.getDisplay(), 0));
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/* 1996 */     coco1.addSelectionListener(cocorg);
/* 1997 */     coco2.addSelectionListener(cocorg);
/* 1998 */     coco3.addSelectionListener(cocorg);
/* 1999 */     fpga.addSelectionListener(cocorg);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2005 */     Button nothanks = new Button(this, 0);
/* 2006 */     nothanks.setText("Cancel wizard");
/* 2007 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/* 2009 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 2014 */             UITaskCompositeWizard.this.state = -1;
/* 2015 */             UITaskCompositeWizard.this.drawControls();
/* 2016 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawInitControls() {
/* 2037 */     int y = 70;
/*      */     
/* 2039 */     Label hello = new Label(this, 0);
/* 2040 */     hello.setBackground(SWTResourceManager.getColor(1));
/* 2041 */     hello.setImage(SWTResourceManager.getImage(MainWin.class, "/wizard/hello_dw4.png"));
/* 2042 */     hello.setBounds(this.width / 2 - 144, y, 288, 220);
/*      */     
/* 2044 */     StyledText intro = new StyledText(this, 74);
/*      */     
/* 2046 */     intro.setCursor(new Cursor((Device)getDisplay(), 0));
/* 2047 */     intro.setEditable(false);
/* 2048 */     intro.setEnabled(false);
/*      */     
/* 2050 */     y += 244;
/*      */     
/* 2052 */     intro.setBounds(0, y, this.width, 130);
/* 2053 */     intro.setForeground(DiskWin.colorDiskBG);
/* 2054 */     intro.setFont(this.introFont);
/* 2055 */     intro.setText("To get started, we need to introduce your CoCo and your DriveWire computer to each other." + intro.getLineDelimiter() + intro.getLineDelimiter());
/* 2056 */     intro.append("This wizard can walk you through that process now if you would like.  You can also run this wizard at any time by choosing 'Simple config wizard' from the Config menu above.");
/*      */     
/* 2058 */     intro.setBounds(0, y, this.width, (intro.getTextBounds(0, intro.getCharCount() - 1)).height);
/*      */     
/* 2060 */     y += (intro.getBounds()).height + 20;
/*      */     
/* 2062 */     Button doit = new Button(this, 0);
/* 2063 */     doit.setText("Use setup wizard..");
/*      */     
/* 2065 */     doit.setBounds(0, this.height - 30, this.width / 2 - 10, 24);
/*      */     
/* 2067 */     doit.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 2072 */             UITaskCompositeWizard.this.state = 1;
/* 2073 */             UITaskCompositeWizard.this.drawControls();
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/* 2086 */     Button nothanks = new Button(this, 0);
/* 2087 */     nothanks.setText("Leave me alone");
/* 2088 */     nothanks.setBounds(this.width / 2 + 9, this.height - 30, this.width - this.width / 2 + 9, 24);
/*      */     
/* 2090 */     nothanks.addSelectionListener(new SelectionListener()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/* 2095 */             UITaskCompositeWizard.this.state = -1;
/* 2096 */             UITaskCompositeWizard.this.drawControls();
/* 2097 */             MainWin.taskman.removeTask(UITaskCompositeWizard.this.tid);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHeight() {
/* 2113 */     if (this.stat != 1)
/*      */     {
/* 2115 */       return 500;
/*      */     }
/*      */ 
/*      */     
/* 2119 */     return 40;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setStatus(int stat) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDetails(String text) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCommand(String text) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTop(int y) {
/* 2139 */     setBounds((getBounds()).x, y, (getBounds()).width, (getBounds()).height);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBottom(int y) {
/* 2144 */     setBounds((getBounds()).x, (getBounds()).y, (getBounds()).width, y - (getBounds()).y);
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UITaskCompositeWizard.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */