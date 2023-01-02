/*      */ package com.groupunix.drivewireui;
/*      */ 
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystem;
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDECBFileSystemDirEntry;
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDisk;
/*      */ import com.groupunix.drivewireserver.dwdisk.DWDiskDrives;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemFileNotFoundException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWDECBFileSystemInvalidFATException;
/*      */ import com.groupunix.drivewireserver.dwexceptions.DWImageFormatException;
/*      */ import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
/*      */ import com.swtdesigner.SWTResourceManager;
/*      */ import java.io.IOException;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.commons.configuration.SubnodeConfiguration;
/*      */ import org.apache.commons.vfs.FileObject;
/*      */ import org.apache.commons.vfs.FileSystemException;
/*      */ import org.apache.commons.vfs.FileType;
/*      */ import org.apache.commons.vfs.VFS;
/*      */ import org.eclipse.swt.custom.CCombo;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.TableCursor;
/*      */ import org.eclipse.swt.events.MenuAdapter;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Table;
/*      */ import org.eclipse.swt.widgets.TableColumn;
/*      */ import org.eclipse.swt.widgets.TableItem;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.eclipse.swt.widgets.ToolBar;
/*      */ import org.eclipse.swt.widgets.ToolItem;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeColumn;
/*      */ import org.eclipse.swt.widgets.TreeItem;
/*      */ import com.swtdesigner.SWTResourceManager;
/*      */ import swing2swt.layout.BorderLayout;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DWLibrary
/*      */   extends Composite
/*      */ {
/*      */   private Tree tree;
/*      */   private Text viewAsciiText;
/*      */   private Table tableHex;
/*      */   private Label lblAddress;
/*      */   private Label lblValue;
/*      */   private CTabFolder tabFolder;
/*      */   private BasicViewer basicViewer;
/*      */   private Text txtSearch;
/*      */   private CCombo comboLine;
/*      */   
/*      */   public DWLibrary(Composite parent, int style) {
/*   77 */     super(parent, style);
/*   78 */     setLayout((Layout)new BorderLayout(0, 4));
/*      */     
/*   80 */     final ToolBar toolBar = new ToolBar(this, 8519680);
/*   81 */     toolBar.setLayoutData("North");
/*      */     
/*   83 */     GradientHelper.applyVerticalGradientBG((Composite)toolBar, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */     
/*   85 */     toolBar.addListener(11, new Listener()
/*      */         {
/*      */           
/*      */           public void handleEvent(Event event)
/*      */           {
/*   90 */             GradientHelper.applyVerticalGradientBG((Composite)toolBar, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   97 */     ToolItem tltmAddLocalSource = new ToolItem(toolBar, 0);
/*   98 */     tltmAddLocalSource.setText("Add local source");
/*   99 */     tltmAddLocalSource.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-new-7.png"));
/*      */     
/*  101 */     SashForm sashForm = new SashForm(this, 0);
/*  102 */     sashForm.setLayoutData("Center");
/*      */ 
/*      */     
/*  105 */     this.tree = new Tree((Composite)sashForm, 0);
/*  106 */     this.tree.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  111 */             if (e.item.getData("browseable") != null && e.item.getData("url") != null)
/*      */             {
/*  113 */               MainWin.addBrowserTab(e.item.getData("url").toString());
/*      */             }
/*      */ 
/*      */             
/*  117 */             if (e.item.getData("viewable") != null && e.item.getData("viewfile") != null && e.item.getData("diskimage") != null)
/*      */             {
/*  119 */               if (((TreeItem)e.item).getParentItem() != null) {
/*      */                 
/*  121 */                 DWDECBFileSystem decbfs = new DWDECBFileSystem((DWDisk)e.item.getData("diskimage"));
/*      */ 
/*      */                 
/*      */                 try {
/*  125 */                   String fn = e.item.getData("viewfile").toString();
/*  126 */                   String fc = decbfs.getFileContents(fn);
/*      */                   
/*  128 */                   DWLibrary.this.viewAsciiText.setText(DWLibrary.this.makeAscii(fc));
/*  129 */                   DWLibrary.this.viewBasic(fc);
/*  130 */                   DWLibrary.this.viewHex(fc);
/*      */                   
/*  132 */                   int ft = decbfs.getDirEntry(fn).getFileType();
/*      */                   
/*  134 */                   if (ft == 3 || (ft > 0 && decbfs.getDirEntry(fn).getFileFlag() == -1))
/*      */                   {
/*  136 */                     DWLibrary.this.tabFolder.setSelection(0);
/*      */                   }
/*  138 */                   else if (ft > 0)
/*      */                   {
/*  140 */                     DWLibrary.this.tabFolder.setSelection(2);
/*      */                   }
/*      */                   else
/*      */                   {
/*  144 */                     DWLibrary.this.tabFolder.setSelection(1);
/*      */                   
/*      */                   }
/*      */                 
/*      */                 }
/*  149 */                 catch (DWDECBFileSystemFileNotFoundException e1) {
/*      */ 
/*      */                   
/*  152 */                   e1.printStackTrace();
/*  153 */                 } catch (DWDECBFileSystemInvalidFATException e1) {
/*      */ 
/*      */                   
/*  156 */                   e1.printStackTrace();
/*  157 */                 } catch (IOException e1) {
/*      */ 
/*      */                   
/*  160 */                   e1.printStackTrace();
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */     
/*  169 */     this.tree.setFont(MainWin.logFont);
/*      */     
/*  171 */     TreeColumn trclmnItem = new TreeColumn(this.tree, 16384);
/*  172 */     trclmnItem.setWidth(270);
/*  173 */     trclmnItem.setText("Item");
/*  174 */     trclmnItem.addSelectionListener(new SortTreeListener());
/*      */     
/*  176 */     TreeColumn trclmnValue = new TreeColumn(this.tree, 0);
/*  177 */     trclmnValue.setWidth(300);
/*  178 */     trclmnValue.setText("Value");
/*      */     
/*  180 */     TreeColumn trclmnPlatforms = new TreeColumn(this.tree, 0);
/*  181 */     trclmnPlatforms.setWidth(100);
/*  182 */     trclmnPlatforms.setText("Platforms");
/*      */ 
/*      */     
/*  185 */     TreeColumn trclmnCategories = new TreeColumn(this.tree, 0);
/*  186 */     trclmnCategories.setWidth(270);
/*  187 */     trclmnCategories.setText("Categories");
/*  188 */     final Menu treemenu = new Menu((Control)this.tree);
/*  189 */     treemenu.addMenuListener((MenuListener)new MenuAdapter()
/*      */         {
/*      */ 
/*      */           
/*      */           public void menuShown(MenuEvent e)
/*      */           {
/*  195 */             while (treemenu.getItemCount() > 0)
/*      */             {
/*  197 */               treemenu.getItem(0).dispose();
/*      */             }
/*  199 */             if (DWLibrary.this.tree.getSelection()[0].getData("ltype") != null) {
/*      */               MenuItem mi;
/*  201 */               int ltype = ((Integer)DWLibrary.this.tree.getSelection()[0].getData("ltype")).intValue();
/*      */ 
/*      */               
/*  204 */               switch (ltype) {
/*      */ 
/*      */                 
/*      */                 case 0:
/*  208 */                   mi = new MenuItem(treemenu, 8);
/*  209 */                   mi.setText("Add new local entry...");
/*  210 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/database-add.png"));
/*      */                   break;
/*      */ 
/*      */ 
/*      */                 
/*      */                 case 2:
/*  216 */                   mi = new MenuItem(treemenu, 8);
/*  217 */                   mi.setText("Open");
/*  218 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-go.png"));
/*      */                   
/*  220 */                   mi = new MenuItem(treemenu, 8);
/*  221 */                   mi.setText("Edit..");
/*  222 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-edit.png"));
/*      */ 
/*      */                   
/*  225 */                   mi = new MenuItem(treemenu, 8);
/*  226 */                   mi.setText("Remove");
/*  227 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-delete.png"));
/*      */                   break;
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*      */                 case 20:
/*  234 */                   mi = new MenuItem(treemenu, 8);
/*  235 */                   mi.setText("Add new cloud entry...");
/*  236 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-add.png"));
/*      */                   break;
/*      */ 
/*      */ 
/*      */                 
/*      */                 case 22:
/*  242 */                   mi = new MenuItem(treemenu, 8);
/*  243 */                   mi.setText("Open ");
/*  244 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-go.png"));
/*      */                   
/*  246 */                   mi = new MenuItem(treemenu, 8);
/*  247 */                   mi.setText("Edit..");
/*  248 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-edit.png"));
/*      */ 
/*      */                   
/*  251 */                   mi = new MenuItem(treemenu, 8);
/*  252 */                   mi.setText("Remove");
/*  253 */                   mi.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/world-delete.png"));
/*      */                   break;
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             } 
/*      */           }
/*      */         });
/*  265 */     this.tree.setMenu(treemenu);
/*      */     
/*  267 */     TreeItem lroot = new TreeItem(this.tree, 0);
/*  268 */     lroot.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/computer-server.png"));
/*  269 */     lroot.setText("Local");
/*  270 */     lroot.setData("ltype", Integer.valueOf(0));
/*  271 */     lroot.setExpanded(true);
/*      */ 
/*      */     
/*  274 */     TreeItem croot = new TreeItem(this.tree, 0);
/*  275 */     croot.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/weather-clouds-2.png"));
/*  276 */     croot.setText("CoCoCloud");
/*  277 */     croot.setData("ltype", Integer.valueOf(20));
/*  278 */     croot.setExpanded(true);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  283 */     if (!MainWin.config.containsKey("Library.Local.updated")) {
/*      */       
/*  285 */       MainWin.config.addProperty("Library.Local.autocreated", Long.valueOf(System.currentTimeMillis()));
/*  286 */       MainWin.config.addProperty("Library.Local.updated", Integer.valueOf(0));
/*      */     } 
/*      */     
/*  289 */     SubnodeConfiguration subnodeConfiguration1 = MainWin.config.configurationAt("Library.Local");
/*      */     
/*  291 */     if (!MainWin.config.containsKey("Library.Cloud.updated")) {
/*      */       
/*  293 */       MainWin.config.addProperty("Library.Cloud.autocreated", Long.valueOf(System.currentTimeMillis()));
/*  294 */       MainWin.config.addProperty("Library.Cloud.updated", Integer.valueOf(0));
/*      */     } 
/*      */     
/*  297 */     SubnodeConfiguration subnodeConfiguration2 = MainWin.config.configurationAt("Library.Cloud");
/*      */     
/*  299 */     buildTree(lroot, subnodeConfiguration1.getRootNode().getChildren());
/*  300 */     lroot.setExpanded(true);
/*      */     
/*  302 */     buildTree(croot, subnodeConfiguration2.getRootNode().getChildren());
/*  303 */     croot.setExpanded(true);
/*      */ 
/*      */     
/*  306 */     this.tabFolder = new CTabFolder((Composite)sashForm, 2048);
/*  307 */     this.tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(35));
/*      */     
/*  309 */     this.tabFolder.setSimple(false);
/*      */     
/*  311 */     this.tabFolder.setSelectionBackground(new Color[] { MainWin.getDisplay().getSystemColor(22), MainWin.getDisplay().getSystemColor(31) }, new int[] { 75 }, true);
/*      */     
/*  313 */     this.tabFolder.setSelectionForeground(MainWin.getDisplay().getSystemColor(30));
/*      */     
/*  315 */     CTabItem tabAscii = new CTabItem(this.tabFolder, 0);
/*  316 */     tabAscii.setText("ASCII  ");
/*  317 */     tabAscii.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/accessories-text-editor-3.png"));
/*      */     
/*  319 */     this.viewAsciiText = new Text((Composite)this.tabFolder, 2826);
/*  320 */     this.viewAsciiText.setBackground(SWTResourceManager.getColor(1));
/*  321 */     this.viewAsciiText.setFont(MainWin.logFont);
/*  322 */     tabAscii.setControl((Control)this.viewAsciiText);
/*      */ 
/*      */     
/*  325 */     CTabItem tabBASIC = new CTabItem(this.tabFolder, 0);
/*  326 */     tabBASIC.setText("BASIC  ");
/*  327 */     tabBASIC.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/basic.png"));
/*      */     
/*  329 */     Composite composite = new Composite((Composite)this.tabFolder, 0);
/*  330 */     tabBASIC.setControl((Control)composite);
/*  331 */     composite.setLayout((Layout)new BorderLayout(0, 0));
/*      */     
/*  333 */     final Composite basicHdr = new Composite(composite, 0);
/*  334 */     basicHdr.setLayoutData("North");
/*      */     
/*  336 */     GradientHelper.applyVerticalGradientBG(basicHdr, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */     
/*  338 */     basicHdr.addListener(11, new Listener()
/*      */         {
/*      */           
/*      */           public void handleEvent(Event event)
/*      */           {
/*  343 */             GradientHelper.applyVerticalGradientBG(basicHdr, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */           }
/*      */         });
/*      */     
/*  347 */     basicHdr.setBackgroundMode(2);
/*      */ 
/*      */     
/*  350 */     int cols = MainWin.config.getInt("BasicViewer_columns", 32);
/*      */     
/*  352 */     this.txtSearch = new Text(basicHdr, 2176);
/*  353 */     this.txtSearch.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  358 */             if (DWLibrary.this.txtSearch.getText() != null && !DWLibrary.this.txtSearch.getText().equals("")) {
/*      */               
/*  360 */               DWLibrary.this.basicViewer.search(DWLibrary.this.txtSearch.getText());
/*      */             }
/*      */             else {
/*      */               
/*  364 */               DWLibrary.this.basicViewer.search(null);
/*      */             } 
/*      */           }
/*      */         });
/*  368 */     this.txtSearch.setBackground(SWTResourceManager.getColor(1));
/*      */     
/*  370 */     this.txtSearch.setBounds(5, 4, 148, 21);
/*      */ 
/*      */ 
/*      */     
/*  374 */     Label btnSearch = new Label(basicHdr, 25165824);
/*  375 */     btnSearch.addMouseListener((MouseListener)new MouseAdapter()
/*      */         {
/*      */           public void mouseDown(MouseEvent e) {
/*  378 */             System.out.println("search");
/*  379 */             if (DWLibrary.this.txtSearch.getText() != null && !DWLibrary.this.txtSearch.getText().equals("")) {
/*      */               
/*  381 */               DWLibrary.this.basicViewer.search(DWLibrary.this.txtSearch.getText());
/*      */             }
/*      */             else {
/*      */               
/*  385 */               DWLibrary.this.basicViewer.search(null);
/*      */             } 
/*      */           }
/*      */         });
/*  389 */     btnSearch.setImage(SWTResourceManager.getImage(DWLibrary.class, "/menu/system-search-4.png"));
/*  390 */     btnSearch.setBounds(152, 3, 25, 23);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  396 */     final ToolBar toolBar_1 = new ToolBar(basicHdr, 8519680);
/*  397 */     toolBar_1.setBounds(298, 2, 137, 27);
/*      */     
/*  399 */     GradientHelper.applyVerticalGradientBG((Composite)toolBar_1, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*  400 */     toolBar_1.addListener(11, new Listener()
/*      */         {
/*      */           
/*      */           public void handleEvent(Event event)
/*      */           {
/*  405 */             GradientHelper.applyVerticalGradientBG((Composite)toolBar_1, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  410 */     toolBar_1.setBackgroundMode(2);
/*      */     
/*  412 */     ToolItem tltmCol = new ToolItem(toolBar_1, 16);
/*  413 */     tltmCol.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  416 */             MainWin.config.setProperty("BasicViewer_columns", Integer.valueOf(32));
/*  417 */             MainWin.config.setProperty("BasicViewer_rows", Integer.valueOf(16));
/*  418 */             DWLibrary.this.basicViewer.updateImg();
/*      */           }
/*      */         });
/*  421 */     tltmCol.setText("32 Col");
/*  422 */     if (cols == 32)
/*      */     {
/*  424 */       tltmCol.setSelection(true);
/*      */     }
/*      */     
/*  427 */     ToolItem tltmCol_1 = new ToolItem(toolBar_1, 16);
/*  428 */     tltmCol_1.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  431 */             MainWin.config.setProperty("BasicViewer_columns", Integer.valueOf(40));
/*  432 */             MainWin.config.setProperty("BasicViewer_rows", Integer.valueOf(24));
/*  433 */             DWLibrary.this.basicViewer.updateImg();
/*      */           }
/*      */         });
/*  436 */     tltmCol_1.setText("40 Col");
/*  437 */     if (cols == 40)
/*      */     {
/*  439 */       tltmCol_1.setSelection(true);
/*      */     }
/*      */     
/*  442 */     ToolItem tltmCol_2 = new ToolItem(toolBar_1, 16);
/*  443 */     tltmCol_2.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  446 */             MainWin.config.setProperty("BasicViewer_columns", Integer.valueOf(80));
/*  447 */             MainWin.config.setProperty("BasicViewer_rows", Integer.valueOf(24));
/*  448 */             DWLibrary.this.basicViewer.updateImg();
/*      */           }
/*      */         });
/*  451 */     tltmCol_2.setText("80 Col");
/*  452 */     if (cols == 80)
/*      */     {
/*  454 */       tltmCol_2.setSelection(true);
/*      */     }
/*      */ 
/*      */     
/*  458 */     ToolBar toolBar_2 = new ToolBar(basicHdr, 8519680);
/*  459 */     toolBar_2.setLocation(448, 2);
/*  460 */     toolBar_2.setSize(100, 27);
/*      */ 
/*      */ 
/*      */     
/*  464 */     final ToolItem tltmAntialias = new ToolItem(toolBar_2, 32);
/*  465 */     tltmAntialias.setText("Anti-Alias");
/*      */     
/*  467 */     tltmAntialias.setSelection(MainWin.config.getBoolean("BasicViewer_antialias", true));
/*      */     
/*  469 */     this.comboLine = new CCombo(basicHdr, 2048);
/*  470 */     this.comboLine.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */             try {
/*  476 */               DWLibrary.this.basicViewer.findLine(Integer.parseInt(DWLibrary.this.comboLine.getItem(DWLibrary.this.comboLine.getSelectionIndex())));
/*      */             }
/*  478 */             catch (NumberFormatException e1) {}
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  483 */     this.comboLine.setBackground(SWTResourceManager.getColor(1));
/*  484 */     this.comboLine.setBounds(192, 4, 88, 21);
/*      */     
/*  486 */     Label label_2 = new Label(basicHdr, 514);
/*  487 */     label_2.setBounds(438, 5, 2, 19);
/*      */     
/*  489 */     Label label = new Label(basicHdr, 514);
/*  490 */     label.setBackground(SWTResourceManager.getColor(23));
/*  491 */     label.setBounds(183, 5, 2, 19);
/*      */     
/*  493 */     Label label_1 = new Label(basicHdr, 514);
/*  494 */     label_1.setBackground(SWTResourceManager.getColor(23));
/*  495 */     label_1.setBounds(288, 5, 2, 19);
/*      */     
/*  497 */     tltmAntialias.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  503 */             MainWin.config.setProperty("BasicViewer_antialias", Boolean.valueOf(tltmAntialias.getSelection()));
/*  504 */             DWLibrary.this.basicViewer.updateImg();
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
/*  518 */     this.basicViewer = new BasicViewer(composite, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  524 */     CTabItem tabHex = new CTabItem(this.tabFolder, 0);
/*  525 */     tabHex.setText("Hex  ");
/*  526 */     tabHex.setImage(SWTResourceManager.getImage(DWBrowser.class, "/menu/hex.png"));
/*      */     
/*  528 */     Composite composite_2 = new Composite((Composite)this.tabFolder, 0);
/*  529 */     tabHex.setControl((Control)composite_2);
/*  530 */     composite_2.setLayout((Layout)new BorderLayout(0, 0));
/*      */ 
/*      */     
/*  533 */     this.tableHex = new Table(composite_2, 2048);
/*  534 */     this.tableHex.setLinesVisible(true);
/*  535 */     this.tableHex.setHeaderVisible(true);
/*  536 */     this.tableHex.setFont(MainWin.logFont);
/*      */ 
/*      */     
/*  539 */     TableColumn tblclmnOffset = new TableColumn(this.tableHex, 0);
/*  540 */     tblclmnOffset.setWidth(70);
/*  541 */     tblclmnOffset.setText("Offset");
/*      */ 
/*      */     
/*  544 */     for (int i = 0; i < 16; i++) {
/*      */       
/*  546 */       TableColumn tableColumn = new TableColumn(this.tableHex, 0);
/*  547 */       tableColumn.setWidth(28);
/*  548 */       tableColumn.setText("0" + intToHexStr(i));
/*      */     } 
/*      */     
/*  551 */     TableColumn tblclmnabcdef = new TableColumn(this.tableHex, 0);
/*  552 */     tblclmnabcdef.setWidth(130);
/*  553 */     tblclmnabcdef.setText("0123456789abcdef");
/*      */     
/*  555 */     final TableCursor tableCursor = new TableCursor(this.tableHex, 0);
/*  556 */     tableCursor.setBackground(Display.getCurrent().getSystemColor(35));
/*  557 */     tableCursor.setFont(MainWin.logFont);
/*  558 */     tableCursor.addSelectionListener(new SelectionListener()
/*      */         {
/*      */ 
/*      */           
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  564 */             if (tableCursor.getColumn() > 0 && tableCursor.getColumn() < 17) {
/*      */ 
/*      */               
/*  567 */               DWLibrary.this.tableHex.setSelection(tableCursor.getRow());
/*      */               
/*  569 */               int off = DWLibrary.this.tableHex.getSelectionIndex() * 16;
/*  570 */               int addr = off + tableCursor.getColumn() - 1;
/*      */ 
/*      */ 
/*      */               
/*      */               try {
/*  575 */                 int val = Integer.parseInt(tableCursor.getRow().getText(tableCursor.getColumn()), 16);
/*      */                 
/*  577 */                 String asc = "";
/*  578 */                 if (val >= 32 && val < 127)
/*      */                 {
/*  580 */                   asc = "'" + (char)val + "'";
/*      */                 }
/*      */                 
/*  583 */                 DWLibrary.this.lblValue.setText(String.format("val:   %02x h   %02d d   " + asc, new Object[] { Integer.valueOf(val), Integer.valueOf(val) }));
/*      */               }
/*  585 */               catch (NumberFormatException e1) {
/*      */                 
/*  587 */                 DWLibrary.this.lblValue.setText("val: " + e1.getMessage());
/*      */               } 
/*      */               
/*  590 */               DWLibrary.this.lblAddress.setText(String.format("addr:   %x h   %d d", new Object[] { Integer.valueOf(addr), Integer.valueOf(addr) }));
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
/*      */           public void widgetDefaultSelected(SelectionEvent e) {}
/*      */         });
/*  603 */     final Composite composite_3 = new Composite(composite_2, 524288);
/*  604 */     composite_3.setLayoutData("South");
/*      */     
/*  606 */     GradientHelper.applyVerticalGradientBG(composite_3, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */     
/*  608 */     composite_3.addListener(11, new Listener()
/*      */         {
/*      */           
/*      */           public void handleEvent(Event event)
/*      */           {
/*  613 */             GradientHelper.applyVerticalGradientBG(composite_3, MainWin.getDisplay().getSystemColor(32), MainWin.getDisplay().getSystemColor(31));
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  618 */     composite_3.setBackgroundMode(2);
/*      */     
/*  620 */     this.lblAddress = new Label(composite_3, 0);
/*  621 */     this.lblAddress.setBounds(10, 3, 230, 16);
/*      */     
/*  623 */     this.lblValue = new Label(composite_3, 0);
/*  624 */     this.lblValue.setBounds(250, 3, 250, 16);
/*      */     
/*  626 */     sashForm.setWeights(new int[] { 221, 226 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void viewBasic(String fc) {
/*  636 */     if (this.basicViewer != null) {
/*      */       
/*  638 */       this.basicViewer.setText(fc.split("\r"));
/*      */       
/*  640 */       this.comboLine.removeAll();
/*      */       
/*  642 */       if (this.basicViewer.getLineRefs() != null)
/*      */       {
/*  644 */         for (int i = 0; i < this.basicViewer.getLineRefs().size(); i++) {
/*      */           
/*  646 */           if (((Integer)this.basicViewer.getLineRefs().get(i)).intValue() != -1)
/*      */           {
/*  648 */             this.comboLine.add("" + this.basicViewer.getLineRefs().get(i));
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void viewHex(String fc) {
/*  659 */     int x = 1;
/*  660 */     int y = 0;
/*  661 */     int off = 0;
/*      */     
/*  663 */     String txt = "";
/*      */     
/*  665 */     this.tableHex.setRedraw(false);
/*      */     
/*  667 */     this.lblAddress.setText("");
/*  668 */     this.lblValue.setText("");
/*  669 */     this.tableHex.removeAll();
/*      */     
/*  671 */     TableItem ti = null;
/*      */     
/*  673 */     byte[] bytes = fc.getBytes();
/*      */     
/*  675 */     for (int i = 0; i < bytes.length; i++) {
/*      */       
/*  677 */       if (x == 1) {
/*      */         
/*  679 */         ti = new TableItem(this.tableHex, 0);
/*  680 */         ti.setText(0, String.format("%8s", new Object[] { intToHexStr(off) }));
/*  681 */         ti.setBackground(0, Display.getCurrent().getSystemColor(35));
/*  682 */         off += 16;
/*  683 */         txt = "";
/*      */       } 
/*      */       
/*  686 */       ti.setText(x, byteToHexStr(bytes[i]));
/*  687 */       txt = txt + fc.charAt(i);
/*      */       
/*  689 */       x++;
/*      */       
/*  691 */       if (x == 17) {
/*      */         
/*  693 */         ti.setText(17, cleanAsciiStr(txt));
/*  694 */         x = 1;
/*  695 */         y++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  700 */     if (ti != null)
/*      */     {
/*  702 */       ti.setText(17, cleanAsciiStr(txt));
/*      */     }
/*      */     
/*  705 */     this.tableHex.setRedraw(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String cleanAsciiStr(String txt) {
/*  714 */     txt = txt.replaceAll("[^\\x20-\\x7E]", ".");
/*      */     
/*  716 */     return txt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String byteToHexStr(byte b) {
/*  723 */     return String.format("%02x", new Object[] { Byte.valueOf(b) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String intToHexStr(int i) {
/*  730 */     return String.format("%x", new Object[] { Integer.valueOf(i) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String makeAscii(String txt) {
/*  737 */     txt = txt.replaceAll("\\x0d", Text.DELIMITER);
/*      */     
/*  739 */     return txt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildTree(TreeItem ti, List<HierarchicalConfiguration.Node> nodes) {
/*  748 */     List<ConfigItem> items = new ArrayList<ConfigItem>();
/*      */     
/*  750 */     HashMap<String, Integer> count = new HashMap<String, Integer>();
/*      */     
/*  752 */     for (HierarchicalConfiguration.Node t : nodes) {
/*      */ 
/*      */ 
/*      */       
/*  756 */       if (t.getName().equals("Directory") || t.getName().equals("URL") || t.getName().equals("Folder")) {
/*      */         
/*  758 */         if (count.containsKey(t.getName())) {
/*  759 */           count.put(t.getName(), Integer.valueOf(((Integer)count.get(t.getName())).intValue() + 1));
/*      */         } else {
/*  761 */           count.put(t.getName(), Integer.valueOf(0));
/*      */         } 
/*  763 */         items.add(new ConfigItem(t, ((Integer)count.get(t.getName())).intValue()));
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  768 */     Collections.sort(items);
/*      */ 
/*      */     
/*  771 */     for (ConfigItem item : items) {
/*      */       TreeItem tmp;
/*      */       
/*  774 */       if (ti == null) {
/*      */         
/*  776 */         tmp = new TreeItem(this.tree, 0);
/*      */       }
/*      */       else {
/*      */         
/*  780 */         tmp = new TreeItem(ti, 0);
/*      */       } 
/*      */ 
/*      */       
/*  784 */       if (getAttributeVal(item.getNode().getAttributes(), "name") == null) {
/*  785 */         tmp.setText(0, item.getNode().getName());
/*      */       } else {
/*  787 */         tmp.setText(0, getAttributeVal(item.getNode().getAttributes(), "name").toString());
/*      */       } 
/*  789 */       if (item.getNode().getName().equals("URL")) {
/*      */         
/*  791 */         tmp.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/world-link.png"));
/*  792 */         tmp.setData("browseable", "yes");
/*  793 */         tmp.setData("url", item.getNode().getValue());
/*      */       }
/*  795 */       else if (item.getNode().getName().equals("Directory")) {
/*  796 */         tmp.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-yellow.png"));
/*  797 */       } else if (item.getNode().getName().equals("Folder")) {
/*      */         
/*  799 */         tmp.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder.png"));
/*  800 */         tmp.setForeground(0, new Color((Device)MainWin.getDisplay(), 128, 128, 128));
/*  801 */         tmp.setForeground(1, new Color((Device)MainWin.getDisplay(), 128, 128, 128));
/*      */       } 
/*      */ 
/*      */       
/*  805 */       tmp.setData("param", item.getNode());
/*  806 */       tmp.setData("index", Integer.valueOf(item.getIndex()));
/*      */       
/*  808 */       if (item.getNode().getValue() == null) {
/*      */         
/*  810 */         if (getAttributeVal(item.getNode().getAttributes(), "desc") != null) {
/*  811 */           tmp.setText(1, getAttributeVal(item.getNode().getAttributes(), "desc").toString());
/*      */         }
/*      */       }
/*      */       else {
/*      */         
/*  816 */         tmp.setText(1, item.getNode().getValue().toString());
/*      */       } 
/*      */       
/*  819 */       if (getAttributeVal(item.getNode().getAttributes(), "platform") != null) {
/*  820 */         tmp.setText(2, getAttributeVals(item.getNode().getAttributes(), "platform").toString());
/*      */       }
/*  822 */       if (getAttributeVal(item.getNode().getAttributes(), "category") != null) {
/*  823 */         tmp.setText(3, getAttributeVals(item.getNode().getAttributes(), "category").toString());
/*      */       }
/*      */       
/*  826 */       if (item.getNode().hasChildren())
/*      */       {
/*  828 */         buildTree(tmp, item.getNode().getChildren());
/*      */       }
/*      */       
/*  831 */       if (item.getNode().getName().equals("Directory")) {
/*      */         
/*  833 */         boolean r = false;
/*      */         
/*  835 */         if (getAttributeVal(item.getNode().getAttributes(), "recursive") != null && 
/*  836 */           Integer.parseInt((String)getAttributeVal(item.getNode().getAttributes(), "recursive")) == 1) {
/*  837 */           r = true;
/*      */         }
/*  839 */         buildFSTree(tmp, item.getNode().getValue().toString(), r);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildFSTree(TreeItem ti, String path, boolean recurse) {
/*      */     try {
/*  851 */       FileObject fo = VFS.getManager().resolveFile(path);
/*  852 */       buildFSTree(ti, fo, recurse);
/*      */     }
/*  854 */     catch (FileSystemException e) {
/*      */       
/*  856 */       ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-important.png"));
/*  857 */       ti.setText(1, e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildFSTree(TreeItem ti, FileObject fo, boolean recurse) {
/*      */     try {
/*  869 */       if (fo.exists() && fo.isReadable())
/*      */       {
/*  871 */         if (fo.getType() == FileType.FOLDER)
/*      */         {
/*  873 */           FileObject[] files = fo.getChildren();
/*      */           
/*  875 */           for (int i = 0; i < files.length; i++)
/*      */           {
/*      */             
/*  878 */             if (files[i].getType() == FileType.FOLDER) {
/*      */               
/*  880 */               TreeItem fi = new TreeItem(ti, 0);
/*  881 */               fi.setText(0, files[i].getName().getBaseName());
/*      */               
/*  883 */               fi.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-grey.png"));
/*      */               
/*  885 */               if (recurse) {
/*  886 */                 buildFSTree(fi, files[i], true);
/*      */               }
/*      */             } else {
/*      */               
/*      */               try
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  897 */                 DWDisk disk = DWDiskDrives.DiskFromFile(files[i]);
/*      */                 
/*  899 */                 TreeItem fi = new TreeItem(ti, 0);
/*  900 */                 fi.setText(0, files[i].getName().getBaseName());
/*      */ 
/*      */ 
/*      */                 
/*  904 */                 fi.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/disk-insert.png"));
/*      */                 
/*  906 */                 fi.setText(1, String.format("%13s", new Object[] { NumberFormat.getIntegerInstance().format(disk.getFileObject().getContent().getSize()) }) + " bytes   " + String.format("%10s", new Object[] { NumberFormat.getIntegerInstance().format(disk.getDiskSectors()) }) + " sectors");
/*      */                 
/*  908 */                 fi.setText(2, DWUtils.prettyFormat(disk.getDiskFormat()));
/*      */                 
/*  910 */                 if (!disk.getParam("_filesystem").toString().equals("unknown")) {
/*  911 */                   fi.setText(3, disk.getParam("_filesystem").toString());
/*      */                 }
/*  913 */                 if (disk.getParam("_filesystem").toString().equals("DECB"))
/*      */                 {
/*  915 */                   buildDECBTree(fi, disk);
/*      */                 }
/*      */               }
/*  918 */               catch (DWImageFormatException e)
/*      */               {
/*      */               
/*      */               }
/*  922 */               catch (IOException e)
/*      */               {
/*  924 */                 TreeItem fi = new TreeItem(ti, 0);
/*  925 */                 fi.setText(0, files[i].getName().getBaseName());
/*  926 */                 fi.setText(1, e.getMessage());
/*  927 */                 fi.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/failed_16.png"));
/*      */               }
/*      */             
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */       else
/*      */       {
/*  939 */         ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-important.png"));
/*  940 */         ti.setText(1, "Unreadable or nonexistent path");
/*      */       }
/*      */     
/*      */     }
/*  944 */     catch (FileSystemException e) {
/*      */       
/*  946 */       ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/folder-important.png"));
/*  947 */       ti.setText(1, e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildDECBTree(TreeItem fi, DWDisk disk) {
/*  958 */     TreeItem ti = null;
/*      */ 
/*      */     
/*      */     try {
/*  962 */       DWDECBFileSystem decbfs = new DWDECBFileSystem(disk);
/*      */       
/*  964 */       for (DWDECBFileSystemDirEntry entry : decbfs.getDirectory()) {
/*      */         
/*  966 */         if (entry.isUsed() && !entry.isKilled())
/*      */         {
/*  968 */           ti = new TreeItem(fi, 0);
/*  969 */           ti.setText(0, String.format("%-8s", new Object[] { entry.getFileName() }) + "." + entry.getFileExt());
/*  970 */           int sectors = decbfs.getFileSectors(entry.getFileName().trim() + "." + entry.getFileExt()).size();
/*  971 */           int bytes = (sectors - 1) * 256 + entry.getBytesInLastSector();
/*  972 */           ti.setText(1, String.format("%13s", new Object[] { NumberFormat.getIntegerInstance().format(bytes) }) + " bytes   " + String.format("%10s", new Object[] { NumberFormat.getIntegerInstance().format(sectors) }) + " sectors");
/*      */           
/*  974 */           ti.setText(2, entry.getPrettyFileFlag());
/*  975 */           ti.setText(3, entry.getPrettyFileType());
/*      */           
/*  977 */           if (entry.getFileType() == 0) {
/*  978 */             ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/basic.png"));
/*  979 */           } else if (entry.getFileType() == 1) {
/*  980 */             ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/applications-office-4.png"));
/*  981 */           } else if (entry.getFileType() == 2) {
/*  982 */             ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/applications-system-2.png"));
/*  983 */           } else if (entry.getFileType() == 3) {
/*  984 */             ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/menu/accessories-text-editor-3.png"));
/*      */           } 
/*  986 */           ti.setData("viewable", Integer.valueOf(1));
/*  987 */           ti.setData("viewfile", entry.getFileName().trim() + "." + entry.getFileExt());
/*  988 */           ti.setData("diskimage", disk);
/*      */         }
/*      */       
/*      */       } 
/*  992 */     } catch (IOException e) {
/*      */       
/*  994 */       if (ti != null)
/*      */       {
/*  996 */         ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/status/failed.png"));
/*  997 */         ti.setText(1, e.getMessage());
/*      */       }
/*      */     
/* 1000 */     } catch (DWDECBFileSystemFileNotFoundException e) {
/*      */       
/* 1002 */       if (ti != null)
/*      */       {
/* 1004 */         ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/status/failed.png"));
/* 1005 */         ti.setText(1, e.getMessage());
/*      */       }
/*      */     
/* 1008 */     } catch (DWDECBFileSystemInvalidFATException e) {
/*      */       
/* 1010 */       if (ti != null) {
/*      */         
/* 1012 */         System.out.println(ti.getText(0) + ": " + e.getMessage());
/* 1013 */         ti.setImage(0, SWTResourceManager.getImage(DWBrowser.class, "/status/failed.png"));
/* 1014 */         ti.setText(1, e.getMessage());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean matchAttributeVal(List<HierarchicalConfiguration.Node> attributes, String key, String val) {
/* 1021 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1023 */       if (n.getName().equals(key) && n.getValue().equals(val)) {
/* 1024 */         return true;
/*      */       }
/*      */     } 
/* 1027 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object getAttributeVal(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1036 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1038 */       if (n.getName().equals(key))
/*      */       {
/* 1040 */         return n.getValue();
/*      */       }
/*      */     } 
/*      */     
/* 1044 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private List<String> getAttributeVals(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1049 */     List<String> res = new ArrayList<String>();
/*      */     
/* 1051 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1053 */       if (n.getName().equals(key))
/*      */       {
/* 1055 */         res.add(n.getValue().toString());
/*      */       }
/*      */     } 
/*      */     
/* 1059 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasAttribute(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1066 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1068 */       if (n.getName().equals(key)) {
/* 1069 */         return true;
/*      */       }
/*      */     } 
/* 1072 */     return false;
/*      */   }
/*      */   
/*      */   protected Text getViewAsciiText() {
/* 1076 */     return this.viewAsciiText;
/*      */   }
/*      */   protected Table getTableHex() {
/* 1079 */     return this.tableHex;
/*      */   }
/*      */   protected CCombo getComboLine() {
/* 1082 */     return this.comboLine;
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWLibrary.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */