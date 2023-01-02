/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*     */ import org.eclipse.swt.events.MenuAdapter;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.VerifyEvent;
/*     */ import org.eclipse.swt.events.VerifyListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Link;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Spinner;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ 
/*     */ public class DiskAdvancedWin
/*     */   extends Dialog
/*     */ {
/*     */   protected Object result;
/*     */   protected Shell shell;
/*     */   private DiskDef disk;
/*     */   private Table tableParams;
/*     */   private Text textItemTitle;
/*     */   private HierarchicalConfiguration paramDefs;
/*     */   private Text textDescription;
/*  42 */   private String wikiurl = "http://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=Using_DriveWire";
/*     */   
/*     */   private Link linkWiki;
/*     */   
/*     */   private Button btnToggle;
/*     */   
/*     */   private TableColumn tblclmnNewValue;
/*     */   
/*     */   private Button btnApply;
/*     */   
/*     */   private Display display;
/*     */   
/*     */   private Text textInt;
/*     */   
/*     */   private Label lblInt;
/*     */   
/*     */   private MenuItem mntmAddToTable;
/*     */   
/*     */   private MenuItem mntmRemoveFromTable;
/*     */   private MenuItem mntmSetToDefault;
/*     */   private MenuItem mntmWikiHelp;
/*     */   private Spinner spinner;
/*     */   private Text textIntHex;
/*     */   private Label lblD;
/*     */   private Label lblH;
/*     */   private boolean whoa = false;
/*     */   
/*     */   public DiskAdvancedWin(Shell parent, int style, DiskDef disk) {
/*  70 */     super(parent, style);
/*  71 */     setText("Parameters for drive " + disk.getDriveNo());
/*  72 */     this.disk = disk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object open() {
/*  81 */     if (MainWin.master == null || MainWin.master.getMaxIndex("diskparams") < 0) {
/*  82 */       this.paramDefs = new HierarchicalConfiguration();
/*     */     } else {
/*  84 */       this.paramDefs = (HierarchicalConfiguration)MainWin.master.configurationAt("diskparams");
/*     */     } 
/*     */ 
/*     */     
/*  88 */     createContents();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     this.display = getParent().getDisplay();
/*     */     
/*  95 */     int x = (getParent().getBounds()).x + (getParent().getBounds()).width / 2 - (this.shell.getBounds()).width / 2;
/*  96 */     int y = (getParent().getBounds()).y + (getParent().getBounds()).height / 2 - (this.shell.getBounds()).height / 2;
/*     */     
/*  98 */     this.shell.setLocation(x, y);
/*  99 */     this.shell.open();
/* 100 */     this.shell.layout();
/*     */ 
/*     */     
/* 103 */     this.tableParams = new Table((Composite)this.shell, 67584);
/*     */     
/* 105 */     this.tableParams.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 108 */             DiskAdvancedWin.this.displayItem(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0), DiskAdvancedWin.this.tableParams.getSelectionIndex());
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 113 */     this.tableParams.setBounds(10, 10, 496, 284);
/* 114 */     this.tableParams.setHeaderVisible(true);
/* 115 */     this.tableParams.setLinesVisible(true);
/*     */     
/* 117 */     TableColumn tblclmnParameter = new TableColumn(this.tableParams, 0);
/* 118 */     tblclmnParameter.setWidth(100);
/* 119 */     tblclmnParameter.setText("Parameter");
/*     */     
/* 121 */     TableColumn tblclmnValue = new TableColumn(this.tableParams, 0);
/* 122 */     tblclmnValue.setWidth(289);
/* 123 */     tblclmnValue.setText("Current Value");
/*     */     
/* 125 */     this.tblclmnNewValue = new TableColumn(this.tableParams, 0);
/*     */     
/* 127 */     this.tblclmnNewValue.setWidth(83);
/* 128 */     this.tblclmnNewValue.setText("New Value");
/*     */     
/* 130 */     Menu menu = new Menu((Control)this.tableParams);
/* 131 */     menu.addMenuListener((MenuListener)new MenuAdapter()
/*     */         {
/*     */           public void menuShown(MenuEvent e)
/*     */           {
/* 135 */             DiskAdvancedWin.this.mntmAddToTable.setEnabled(false);
/* 136 */             DiskAdvancedWin.this.mntmRemoveFromTable.setEnabled(false);
/* 137 */             DiskAdvancedWin.this.mntmSetToDefault.setEnabled(false);
/* 138 */             DiskAdvancedWin.this.mntmWikiHelp.setEnabled(false);
/* 139 */             DiskAdvancedWin.this.mntmWikiHelp.setText("Wiki help...");
/* 140 */             DiskAdvancedWin.this.mntmAddToTable.setText("Add item to main display");
/*     */             
/* 142 */             if (MainWin.getTPIndex(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0)) > -1) {
/*     */               
/* 144 */               DiskAdvancedWin.this.mntmRemoveFromTable.setEnabled(true);
/*     */             }
/*     */             else {
/*     */               
/* 148 */               DiskAdvancedWin.this.mntmAddToTable.setEnabled(true);
/*     */             } 
/*     */             
/* 151 */             if (DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) != null) {
/*     */               
/* 153 */               DiskAdvancedWin.this.mntmAddToTable.setText("Add " + DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + " to main display");
/* 154 */               if (!DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0).startsWith("_") && DiskAdvancedWin.this.paramDefs.containsKey(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "[@default]"))
/*     */               {
/* 156 */                 if (!DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(1).equals(DiskAdvancedWin.this.paramDefs.getString(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "[@default]")) || !DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(2).equals(""))
/*     */                 {
/* 158 */                   DiskAdvancedWin.this.mntmSetToDefault.setEnabled(true);
/*     */                 }
/*     */               }
/*     */             } 
/*     */             
/* 163 */             if (DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) != null)
/*     */             {
/* 165 */               if (DiskAdvancedWin.this.paramDefs.containsKey(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "[@wikiurl]")) {
/*     */                 
/* 167 */                 DiskAdvancedWin.this.mntmWikiHelp.setText("Wiki help for " + DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "...");
/* 168 */                 DiskAdvancedWin.this.mntmWikiHelp.setEnabled(true);
/*     */               } 
/*     */             }
/*     */           }
/*     */         });
/*     */     
/* 174 */     this.tableParams.setMenu(menu);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     this.mntmSetToDefault = new MenuItem(menu, 0);
/* 180 */     this.mntmSetToDefault.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 184 */             DiskAdvancedWin.this.doToggle(DiskAdvancedWin.this.tableParams.getSelectionIndex(), DiskAdvancedWin.this.paramDefs.getString(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "[@default]", ""));
/* 185 */             DiskAdvancedWin.this.displayItem(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0), DiskAdvancedWin.this.tableParams.getSelectionIndex());
/*     */           }
/*     */         });
/* 188 */     this.mntmSetToDefault.setText("Set to default value");
/*     */ 
/*     */     
/* 191 */     MenuItem spacer = new MenuItem(menu, 2);
/*     */     
/* 193 */     this.mntmWikiHelp = new MenuItem(menu, 0);
/* 194 */     this.mntmWikiHelp.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 198 */             MainWin.openURL(getClass(), DiskAdvancedWin.this.paramDefs.getString(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0) + "[@wikiurl]", ""));
/*     */           }
/*     */         });
/*     */     
/* 202 */     this.mntmWikiHelp.setText("Wiki help...");
/*     */ 
/*     */ 
/*     */     
/* 206 */     MenuItem spacer2 = new MenuItem(menu, 2);
/*     */     
/* 208 */     this.mntmAddToTable = new MenuItem(menu, 0);
/* 209 */     this.mntmAddToTable.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 213 */             MainWin.addDiskTableColumn(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0));
/*     */           }
/*     */         });
/*     */     
/* 217 */     this.mntmAddToTable.setText("Add item to main display");
/*     */     
/* 219 */     this.mntmRemoveFromTable = new MenuItem(menu, 0);
/* 220 */     this.mntmRemoveFromTable.setText("Remove from main display");
/* 221 */     this.mntmRemoveFromTable.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 225 */             MainWin.removeDiskTableColumn(DiskAdvancedWin.this.tableParams.getItem(DiskAdvancedWin.this.tableParams.getSelectionIndex()).getText(0));
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 231 */     this.textItemTitle = new Text((Composite)this.shell, 8);
/* 232 */     this.textItemTitle.setBackground(SWTResourceManager.getColor(22));
/* 233 */     this.textItemTitle.setFont(SWTResourceManager.getBoldFont(this.display.getSystemFont()));
/* 234 */     this.textItemTitle.setBounds(10, 311, 335, 21);
/*     */     
/* 236 */     this.textDescription = new Text((Composite)this.shell, 74);
/* 237 */     this.textDescription.setText("Select an option above to view details or make changes.");
/* 238 */     this.textDescription.setBackground(SWTResourceManager.getColor(22));
/* 239 */     this.textDescription.setBounds(10, 332, 496, 60);
/*     */     
/* 241 */     this.linkWiki = new Link((Composite)this.shell, 0);
/* 242 */     this.linkWiki.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 245 */             MainWin.doDisplayAsync(new Runnable()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   
/*     */                   public void run()
/*     */                   {
/* 252 */                     MainWin.openURL(getClass(), DiskAdvancedWin.this.wikiurl);
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */     
/* 258 */     this.linkWiki.setBounds(14, 430, 70, 15);
/* 259 */     this.linkWiki.setText("<a>Wiki Help..</a>");
/* 260 */     this.linkWiki.setVisible(false);
/*     */     
/* 262 */     this.btnToggle = new Button((Composite)this.shell, 32);
/* 263 */     this.btnToggle.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 267 */             DiskAdvancedWin.this.doToggle(DiskAdvancedWin.this.tableParams.getSelectionIndex(), DiskAdvancedWin.this.btnToggle.getSelection() + "");
/*     */           }
/*     */         });
/* 270 */     this.btnToggle.setBounds(14, 395, 297, 18);
/* 271 */     this.btnToggle.setText("toggle");
/* 272 */     this.btnToggle.setVisible(false);
/*     */     
/* 274 */     this.btnApply = new Button((Composite)this.shell, 0);
/* 275 */     this.btnApply.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 278 */             DiskAdvancedWin.this.saveChanges();
/*     */           }
/*     */         });
/* 281 */     this.btnApply.setEnabled(false);
/* 282 */     this.btnApply.setBounds(431, 425, 75, 25);
/* 283 */     this.btnApply.setText("Apply");
/*     */     
/* 285 */     this.textInt = new Text((Composite)this.shell, 2048);
/* 286 */     this.textInt.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 288 */             DiskAdvancedWin.this.doToggle(DiskAdvancedWin.this.tableParams.getSelectionIndex(), DiskAdvancedWin.this.textInt.getText());
/* 289 */             if (!DiskAdvancedWin.this.whoa)
/* 290 */               DiskAdvancedWin.this.setHexFromInt(((Text)e.getSource()).getText()); 
/*     */           }
/*     */         });
/* 293 */     this.textInt.addVerifyListener(new VerifyListener() {
/*     */           public void verifyText(VerifyEvent e) {
/* 295 */             String string = e.text;
/* 296 */             char[] chars = new char[string.length()];
/* 297 */             string.getChars(0, chars.length, chars, 0);
/* 298 */             for (int i = 0; i < chars.length; i++) {
/* 299 */               if (chars[i] < '0' || chars[i] > '9')
/*     */               {
/*     */                 
/* 302 */                 if (i != 0 || chars[0] != '-' || e.start != 0) {
/*     */                   
/* 304 */                   e.doit = false;
/*     */ 
/*     */                   
/*     */                   return;
/*     */                 } 
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 314 */     this.textInt.setBounds(10, 395, 76, 21);
/* 315 */     this.textInt.setVisible(false);
/*     */     
/* 317 */     this.lblInt = new Label((Composite)this.shell, 0);
/* 318 */     this.lblInt.setBounds(217, 397, 289, 18);
/* 319 */     this.lblInt.setText("Value");
/* 320 */     this.lblInt.setVisible(false);
/*     */     
/* 322 */     this.spinner = new Spinner((Composite)this.shell, 2048);
/* 323 */     this.spinner.setBounds(119, 393, 76, 22);
/* 324 */     this.spinner.setVisible(false);
/*     */     
/* 326 */     this.textIntHex = new Text((Composite)this.shell, 2048);
/* 327 */     this.textIntHex.setBounds(112, 395, 76, 21);
/* 328 */     this.textIntHex.setVisible(false);
/*     */     
/* 330 */     this.textIntHex.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 332 */             if (!DiskAdvancedWin.this.whoa)
/* 333 */               DiskAdvancedWin.this.setIntFromHex(((Text)e.getSource()).getText()); 
/*     */           }
/*     */         });
/* 336 */     this.textIntHex.addVerifyListener(new VerifyListener() {
/*     */           public void verifyText(VerifyEvent e) {
/* 338 */             e.text = e.text.toLowerCase();
/* 339 */             char[] chars = new char[e.text.length()];
/* 340 */             e.text.getChars(0, chars.length, chars, 0);
/* 341 */             for (int i = 0; i < chars.length; i++) {
/* 342 */               if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f'))
/*     */               {
/* 344 */                 if (i != 0 || chars[0] != '-' || e.start != 0) {
/*     */                   
/* 346 */                   e.doit = false;
/*     */ 
/*     */                   
/*     */                   return;
/*     */                 } 
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 356 */     this.lblD = new Label((Composite)this.shell, 0);
/* 357 */     this.lblD.setBounds(89, 397, 17, 18);
/* 358 */     this.lblD.setText("d");
/* 359 */     this.lblD.setVisible(false);
/*     */     
/* 361 */     this.lblH = new Label((Composite)this.shell, 0);
/* 362 */     this.lblH.setBounds(192, 397, 18, 21);
/* 363 */     this.lblH.setText("h");
/* 364 */     this.lblH.setVisible(false);
/*     */     
/* 366 */     this.spinner.addModifyListener(new ModifyListener() {
/*     */           public void modifyText(ModifyEvent e) {
/* 368 */             DiskAdvancedWin.this.doToggle(DiskAdvancedWin.this.tableParams.getSelectionIndex(), DiskAdvancedWin.this.spinner.getSelection() + "");
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 373 */     applySettings();
/* 374 */     applyToggle();
/*     */ 
/*     */     
/* 377 */     while (!this.shell.isDisposed()) {
/* 378 */       if (!this.display.readAndDispatch()) {
/* 379 */         this.display.sleep();
/*     */       }
/*     */     } 
/*     */     
/* 383 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setIntFromHex(String text) {
/* 389 */     this.whoa = true;
/*     */     
/*     */     try {
/* 392 */       if (text != null && text != "")
/*     */       {
/*     */         
/* 395 */         this.textInt.setText(Integer.parseInt(text.toLowerCase(), 16) + "");
/*     */       }
/*     */       else
/*     */       {
/* 399 */         this.textInt.setText("");
/*     */       }
/*     */     
/* 402 */     } catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */     
/* 406 */     this.whoa = false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setHexFromInt(String text) {
/* 411 */     this.whoa = true;
/*     */     
/*     */     try {
/* 414 */       if (text != null && text != "")
/*     */       {
/*     */         
/* 417 */         this.textIntHex.setText(Integer.toString(Integer.parseInt(text), 16));
/*     */       
/*     */       }
/*     */       else
/*     */       {
/*     */         
/* 423 */         this.textIntHex.setText("");
/*     */       }
/*     */     
/* 426 */     } catch (NumberFormatException e) {}
/*     */ 
/*     */ 
/*     */     
/* 430 */     this.whoa = false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doToggle(int sel, String newval) {
/* 435 */     if (this.tableParams.getItem(sel).getText(1).equals(newval)) {
/*     */       
/* 437 */       this.tableParams.getItem(sel).setText(2, "");
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 442 */       this.tableParams.getItem(sel).setText(2, newval);
/*     */     } 
/*     */ 
/*     */     
/* 446 */     applyToggle();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyToggle() {
/*     */     int i;
/* 453 */     for (i = 0; i < this.tableParams.getItemCount(); i++) {
/*     */       
/* 455 */       if (!this.tableParams.getItem(i).getText(2).equals("")) {
/*     */         
/* 457 */         this.btnApply.setEnabled(true);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 462 */     if (i == this.tableParams.getItemCount())
/*     */     {
/* 464 */       this.btnApply.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void displayItem(String key, int index) {
/* 471 */     this.shell.setRedraw(false);
/*     */     
/* 473 */     this.btnToggle.setVisible(false);
/* 474 */     this.linkWiki.setVisible(false);
/* 475 */     this.lblInt.setVisible(false);
/* 476 */     this.textInt.setVisible(false);
/* 477 */     this.spinner.setVisible(false);
/* 478 */     this.textIntHex.setVisible(false);
/* 479 */     this.lblD.setVisible(false);
/* 480 */     this.lblH.setVisible(false);
/*     */     
/* 482 */     if (key == null) {
/*     */ 
/*     */       
/* 485 */       this.textItemTitle.setText("");
/* 486 */       this.textDescription.setText("No disk is inserted in drive " + this.disk.getDriveNo());
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 491 */       String title = key;
/*     */       
/* 493 */       if (key.startsWith("_")) {
/* 494 */         title = title + " (System parameter)";
/*     */       }
/* 496 */       this.textItemTitle.setText(title);
/*     */       
/* 498 */       this.textDescription.setText(this.paramDefs.getString(key + "[@detail]", "No definition for this parameter."));
/*     */       
/* 500 */       if (this.paramDefs.containsKey(key + "[@wikiurl]")) {
/*     */         
/* 502 */         this.wikiurl = this.paramDefs.getString(key + "[@wikiurl]");
/* 503 */         this.linkWiki.setVisible(true);
/*     */       } 
/*     */       
/* 506 */       String type = this.paramDefs.getString(key + "[@type]", "system");
/*     */       
/* 508 */       if (type.equals("boolean")) {
/*     */         
/* 510 */         this.btnToggle.setText(" " + this.paramDefs.getString(key + "[@toggletext]", "Enable option"));
/*     */         
/* 512 */         if (this.tableParams.getItem(index).getText(2).equals("")) {
/* 513 */           this.btnToggle.setSelection(Boolean.parseBoolean(this.disk.getParam(key).toString()));
/*     */         } else {
/* 515 */           this.btnToggle.setSelection(Boolean.parseBoolean(this.tableParams.getItem(index).getText(2)));
/*     */         } 
/* 517 */         this.btnToggle.setVisible(true);
/*     */       }
/* 519 */       else if (type.equals("integer")) {
/*     */         
/* 521 */         this.lblInt.setText(this.paramDefs.getString(key + "[@inputtext]", "Value"));
/*     */         
/* 523 */         if (this.tableParams.getItem(index).getText(2).equals("")) {
/* 524 */           this.textInt.setText(this.disk.getParam(key).toString());
/*     */         } else {
/* 526 */           this.textInt.setText(this.tableParams.getItem(index).getText(2));
/*     */         } 
/* 528 */         this.lblInt.setVisible(true);
/* 529 */         this.textInt.setVisible(true);
/* 530 */         this.textIntHex.setVisible(true);
/* 531 */         this.lblD.setVisible(true);
/* 532 */         this.lblH.setVisible(true);
/*     */       }
/* 534 */       else if (type.equals("spinner")) {
/*     */         
/* 536 */         this.lblInt.setText(this.paramDefs.getString(key + "[@inputtext]", "Value"));
/*     */ 
/*     */         
/*     */         try {
/* 540 */           if (this.paramDefs.containsKey(key + "[@min]"))
/*     */           {
/* 542 */             this.spinner.setMinimum(this.paramDefs.getInt(key + "[@min]"));
/*     */           }
/*     */           
/* 545 */           if (this.paramDefs.containsKey(key + "[@max]"))
/*     */           {
/* 547 */             this.spinner.setMaximum(this.paramDefs.getInt(key + "[@max]"));
/*     */           }
/*     */           
/* 550 */           if (this.tableParams.getItem(index).getText(2).equals("")) {
/* 551 */             this.spinner.setSelection(Integer.parseInt(this.disk.getParam(key).toString()));
/*     */           } else {
/* 553 */             this.spinner.setSelection(Integer.parseInt(this.tableParams.getItem(index).getText(2)));
/*     */           
/*     */           }
/*     */         
/*     */         }
/* 558 */         catch (NumberFormatException e) {
/*     */           
/* 560 */           MainWin.showError("Non numeric value?", "Somehow, we've managed to get a non numeric value into the config in a place where only numbers are allowed.", "This is not normal..  why don't you submit a bug report and let me know how this happened.");
/*     */         } 
/*     */         
/* 563 */         this.lblInt.setVisible(true);
/* 564 */         this.spinner.setVisible(true);
/*     */       } 
/*     */     } 
/*     */     
/* 568 */     this.shell.setRedraw(true);
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
/*     */   private void applySettings() {
/* 587 */     Iterator<String> itr = this.disk.getParams();
/*     */ 
/*     */ 
/*     */     
/* 591 */     while (itr.hasNext()) {
/*     */       
/* 593 */       String key = itr.next();
/* 594 */       addOrUpdate(key, this.disk.getParam(key).toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void saveChanges() {
/* 603 */     for (int i = 0; i < this.tableParams.getItemCount(); i++) {
/*     */       
/* 605 */       if (!this.tableParams.getItem(i).getText(2).equals(""))
/*     */       {
/* 607 */         MainWin.sendCommand("dw disk set " + this.disk.getDriveNo() + " " + this.tableParams.getItem(i).getText(0) + " " + this.tableParams.getItem(i).getText(2));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents() {
/* 616 */     this.shell = new Shell(getParent(), getStyle());
/* 617 */     this.shell.setSize(522, 487);
/* 618 */     this.shell.setText(getText());
/*     */     
/* 620 */     Button btnOk = new Button((Composite)this.shell, 0);
/* 621 */     btnOk.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 625 */             DiskAdvancedWin.this.saveChanges();
/* 626 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/* 629 */     btnOk.setBounds(270, 425, 75, 25);
/* 630 */     btnOk.setText("Ok");
/*     */     
/* 632 */     Button btnCancel = new Button((Composite)this.shell, 0);
/* 633 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */         {
/*     */           public void widgetSelected(SelectionEvent e)
/*     */           {
/* 637 */             e.display.getActiveShell().close();
/*     */           }
/*     */         });
/* 640 */     btnCancel.setBounds(350, 425, 75, 25);
/* 641 */     btnCancel.setText("Cancel");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void submitEvent(String key, String val) {
/* 649 */     addOrUpdate(key, val);
/* 650 */     applyToggle();
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
/*     */   private void addOrUpdate(String key, String val) {
/* 669 */     if (key.equals("*eject")) {
/*     */       
/* 671 */       this.tableParams.removeAll();
/* 672 */       displayItem((String)null, -1);
/*     */     } 
/*     */     
/* 675 */     if (val != null && !key.startsWith("*")) {
/*     */       int i;
/* 677 */       for (i = 0; i < this.tableParams.getItemCount(); i++) {
/*     */ 
/*     */         
/* 680 */         if (this.tableParams.getItem(i).getText(0).equals(key)) {
/*     */ 
/*     */           
/* 683 */           this.tableParams.getItem(i).setText(1, val);
/* 684 */           doToggle(i, this.tableParams.getItem(i).getText(2));
/*     */ 
/*     */           
/* 687 */           if (i == this.tableParams.getSelectionIndex())
/*     */           {
/* 689 */             displayItem(key, i);
/*     */           }
/*     */           break;
/*     */         } 
/* 693 */         if (this.tableParams.getItem(i).getText(0).compareTo(key) > 0) {
/*     */           
/* 695 */           this.tableParams.setRedraw(false);
/*     */ 
/*     */           
/* 698 */           TableItem item = new TableItem(this.tableParams, 0);
/* 699 */           item.setText(0, key);
/* 700 */           item.setText(1, val);
/* 701 */           item.setText(2, "");
/*     */           
/* 703 */           for (int j = i; j < this.tableParams.getItemCount() - 1; j++) {
/*     */             
/* 705 */             item = new TableItem(this.tableParams, 0);
/* 706 */             item.setText(0, this.tableParams.getItem(i).getText(0));
/* 707 */             item.setText(1, this.tableParams.getItem(i).getText(1));
/* 708 */             item.setText(2, this.tableParams.getItem(i).getText(2));
/*     */             
/* 710 */             this.tableParams.remove(i);
/*     */           } 
/*     */ 
/*     */           
/* 714 */           this.tableParams.setRedraw(true);
/*     */ 
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 723 */       if (i == this.tableParams.getItemCount()) {
/*     */ 
/*     */         
/* 726 */         TableItem item = new TableItem(this.tableParams, 0);
/* 727 */         item.setText(0, key);
/* 728 */         item.setText(1, val);
/* 729 */         item.setText(2, "");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected Spinner getSpinner() {
/* 735 */     return this.spinner;
/*     */   }
/*     */   protected Text getTextIntHex() {
/* 738 */     return this.textIntHex;
/*     */   }
/*     */   protected Label getLblD() {
/* 741 */     return this.lblD;
/*     */   }
/*     */   protected Label getLblH() {
/* 744 */     return this.lblH;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskAdvancedWin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */