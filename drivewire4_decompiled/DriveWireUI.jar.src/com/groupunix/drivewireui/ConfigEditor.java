/*      */ package com.groupunix.drivewireui;
/*      */ 
import com.swtdesigner.SWTResourceManager;
/*      */ import java.io.IOException;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.apache.commons.configuration.ConfigurationException;
/*      */ import org.apache.commons.configuration.HierarchicalConfiguration;
/*      */ import org.apache.commons.configuration.XMLConfiguration;
/*      */ import org.apache.commons.configuration.tree.ConfigurationNode;
/*      */ import org.eclipse.swt.events.ControlAdapter;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.ControlListener;
/*      */ import org.eclipse.swt.events.ModifyEvent;
/*      */ import org.eclipse.swt.events.ModifyListener;
/*      */ import org.eclipse.swt.events.MouseAdapter;
/*      */ import org.eclipse.swt.events.MouseEvent;
/*      */ import org.eclipse.swt.events.MouseListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.events.SelectionListener;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.layout.RowData;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Combo;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Spinner;
/*      */ import org.eclipse.swt.widgets.Text;
/*      */ import org.eclipse.swt.widgets.ToolBar;
/*      */ import org.eclipse.swt.widgets.ToolItem;
/*      */ import org.eclipse.swt.widgets.Tree;
/*      */ import org.eclipse.swt.widgets.TreeColumn;
/*      */ import org.eclipse.swt.widgets.TreeItem;
/*      */ 
/*      */ public class ConfigEditor
/*      */   extends Shell {
/*      */   private Tree tree;
/*      */   private Label lblItemTitle;
/*      */   private Composite scrolledComposite;
/*      */   private Composite composite_1;
/*      */   private Label textDescription;
/*      */   private Button btnToggle;
/*      */   private Button btnApply;
/*   55 */   private XMLConfiguration wc = new XMLConfiguration();
/*   56 */   private XMLConfiguration masterc = new XMLConfiguration();
/*      */   
/*      */   private ToolBar toolBar;
/*      */   
/*      */   private ToolItem tltmMidi;
/*      */   
/*      */   private ToolItem tltmLogging;
/*      */   
/*      */   private ToolItem tltmDevice;
/*      */   
/*      */   private ToolItem tltmPrinting;
/*      */   
/*      */   private ToolItem tltmNetworking;
/*      */   
/*      */   private ToolItem tltmAll;
/*      */   
/*      */   private ToolItem tltmCheckAdvanced;
/*      */   
/*      */   private HierarchicalConfiguration.Node selected;
/*      */   
/*      */   private Label lblIntText;
/*      */   
/*      */   private Spinner spinnerInt;
/*      */   
/*      */   private ModifyListener spinnerModifyListener;
/*      */   
/*      */   private Combo comboList;
/*      */   
/*      */   private Label lblList;
/*      */   
/*      */   private ModifyListener comboModifyListener;
/*      */   
/*      */   private Text textString;
/*      */   
/*      */   private Label lblString;
/*      */   
/*      */   private Button btnFileDir;
/*      */   
/*      */   private ToolItem tltmDisk;
/*      */ 
/*      */   
/*      */   protected void checkSubclass() {}
/*      */ 
/*      */   
/*      */   public ConfigEditor(Display display) {
/*  101 */     super(display, 1264);
/*      */     
/*  103 */     createContents();
/*  104 */     loadMaster();
/*  105 */     if (!isDisposed())
/*      */     {
/*  107 */       loadConfig();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void createContents() {
/*  120 */     setText("Configuration Editor");
/*  121 */     setSize(684, 634);
/*      */ 
/*      */ 
/*      */     
/*  125 */     addControlListener((ControlListener)new ControlAdapter()
/*      */         {
/*      */           public void controlResized(ControlEvent e) {
/*  128 */             ConfigEditor.this.setRedraw(false);
/*  129 */             ConfigEditor.this.toolBar.setLayoutData(new RowData((ConfigEditor.this.getSize()).x - 26, -1));
/*  130 */             ConfigEditor.this.scrolledComposite.setLayoutData(new RowData((ConfigEditor.this.getSize()).x - 26, 150));
/*  131 */             ConfigEditor.this.composite_1.setLayoutData(new RowData((ConfigEditor.this.getSize()).x - 26, 36));
/*  132 */             ConfigEditor.this.tree.setLayoutData(new RowData((ConfigEditor.this.getSize()).x - 46, (ConfigEditor.this.getSize()).y - 280));
/*  133 */             ConfigEditor.this.setRedraw(true);
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  138 */     setLayout((Layout)new RowLayout(256));
/*      */     
/*  140 */     this.toolBar = new ToolBar((Composite)this, 8519680);
/*  141 */     this.toolBar.addMouseListener((MouseListener)new MouseAdapter()
/*      */         {
/*      */           public void mouseUp(MouseEvent e)
/*      */           {
/*  145 */             ConfigEditor.this.commitNodes();
/*  146 */             ConfigEditor.this.tree.removeAll();
/*  147 */             ConfigEditor.this.loadConfig((TreeItem)null, ConfigEditor.this.wc.getRootNode().getChildren());
/*      */           }
/*      */         });
/*  150 */     this.toolBar.setLayoutData(new RowData(664, -1));
/*      */     
/*  152 */     this.tltmAll = new ToolItem(this.toolBar, 16);
/*  153 */     this.tltmAll.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/view-list-tree-4.png"));
/*  154 */     this.tltmAll.setSelection(true);
/*  155 */     this.tltmAll.setText("All");
/*      */     
/*  157 */     this.tltmDevice = new ToolItem(this.toolBar, 16);
/*  158 */     this.tltmDevice.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/connect.png"));
/*  159 */     this.tltmDevice.setText("Device");
/*      */     
/*  161 */     this.tltmDisk = new ToolItem(this.toolBar, 16);
/*  162 */     this.tltmDisk.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/disk-insert.png"));
/*  163 */     this.tltmDisk.setText("Disk");
/*      */     
/*  165 */     this.tltmPrinting = new ToolItem(this.toolBar, 16);
/*  166 */     this.tltmPrinting.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/document-print.png"));
/*  167 */     this.tltmPrinting.setText("Printing");
/*      */     
/*  169 */     this.tltmMidi = new ToolItem(this.toolBar, 16);
/*  170 */     this.tltmMidi.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/music.png"));
/*  171 */     this.tltmMidi.setText("MIDI");
/*      */     
/*  173 */     this.tltmNetworking = new ToolItem(this.toolBar, 16);
/*  174 */     this.tltmNetworking.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/preferences-system-network-2.png"));
/*  175 */     this.tltmNetworking.setText("Networking");
/*      */     
/*  177 */     this.tltmLogging = new ToolItem(this.toolBar, 16);
/*  178 */     this.tltmLogging.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/documentation.png"));
/*  179 */     this.tltmLogging.setText("Logging");
/*      */ 
/*      */     
/*  182 */     ToolItem toolItem = new ToolItem(this.toolBar, 2);
/*      */     
/*  184 */     this.tltmCheckAdvanced = new ToolItem(this.toolBar, 32);
/*  185 */     this.tltmCheckAdvanced.setImage(SWTResourceManager.getImage(ConfigEditor.class, "/menu/cog-edit.png"));
/*  186 */     this.tltmCheckAdvanced.setText("Show Advanced Items");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  192 */     this.tree = new Tree((Composite)this, 67584);
/*  193 */     this.tree.setLayoutData(new RowData(663, -1));
/*  194 */     this.tree.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  198 */             ConfigEditor.this.selected = (HierarchicalConfiguration.Node)ConfigEditor.this.tree.getSelection()[0].getData("param");
/*  199 */             ConfigEditor.this.displayParam(ConfigEditor.this.tree.getSelection()[0]);
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  204 */     this.tree.setHeaderVisible(true);
/*      */     
/*  206 */     TreeColumn trclmnItem = new TreeColumn(this.tree, 16384);
/*  207 */     trclmnItem.setWidth(250);
/*  208 */     trclmnItem.setText("Item");
/*  209 */     trclmnItem.addSelectionListener(new SortTreeListener());
/*      */     
/*  211 */     TreeColumn trclmnValue = new TreeColumn(this.tree, 0);
/*  212 */     trclmnValue.setWidth(380);
/*  213 */     trclmnValue.setText("Value");
/*      */     
/*  215 */     this.scrolledComposite = new Composite((Composite)this, 0);
/*      */     
/*  217 */     this.lblItemTitle = new Label(this.scrolledComposite, 0);
/*  218 */     this.lblItemTitle.setBounds(10, 10, 239, 24);
/*  219 */     this.lblItemTitle.setText("");
/*  220 */     this.lblItemTitle.setFont(SWTResourceManager.getBoldFont(getDisplay().getSystemFont()));
/*      */ 
/*      */     
/*  223 */     this.textDescription = new Label(this.scrolledComposite, 64);
/*  224 */     this.textDescription.setBounds(10, 40, 647, 60);
/*  225 */     this.textDescription.setText("");
/*  226 */     this.textDescription.setVisible(true);
/*      */     
/*  228 */     this.btnToggle = new Button(this.scrolledComposite, 32);
/*  229 */     this.btnToggle.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  232 */             ConfigEditor.this.updateBoolean(ConfigEditor.this.selected, ConfigEditor.this.btnToggle.getSelection());
/*      */           }
/*      */         });
/*  235 */     this.btnToggle.setBounds(10, 113, 300, 24);
/*      */     
/*  237 */     this.composite_1 = new Composite((Composite)this, 0);
/*  238 */     FillLayout fl_composite_1 = new FillLayout(256);
/*  239 */     fl_composite_1.spacing = 10;
/*  240 */     fl_composite_1.marginWidth = 5;
/*  241 */     fl_composite_1.marginHeight = 5;
/*  242 */     this.composite_1.setLayout((Layout)fl_composite_1);
/*      */     
/*  244 */     Button btnBackup = new Button(this.composite_1, 0);
/*  245 */     btnBackup.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  248 */             ConfigEditor.this.doBackup();
/*      */           }
/*      */         });
/*  251 */     btnBackup.setText("Save to file...");
/*      */     
/*  253 */     Button btnRestore = new Button(this.composite_1, 0);
/*  254 */     btnRestore.setText("Load from file...");
/*  255 */     btnRestore.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  258 */             ConfigEditor.this.doRestore();
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  263 */     Label lblNewLabel = new Label(this.composite_1, 0);
/*  264 */     lblNewLabel.setText(" ");
/*      */     
/*  266 */     this.btnApply = new Button(this.composite_1, 0);
/*  267 */     this.btnApply.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*  271 */             ConfigEditor.this.applyChanges();
/*      */           }
/*      */         });
/*  274 */     this.btnApply.setText("Write to server");
/*  275 */     this.btnApply.setEnabled(true);
/*      */ 
/*      */     
/*  278 */     Button btnCancel = new Button(this.composite_1, 0);
/*  279 */     btnCancel.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e) {
/*  282 */             ConfigEditor.this.close();
/*      */           }
/*      */         });
/*  285 */     btnCancel.setText("Close");
/*      */     
/*  287 */     this.scrolledComposite.setLayoutData(new RowData(667, 200));
/*      */     
/*  289 */     this.spinnerInt = new Spinner(this.scrolledComposite, 2048);
/*      */     
/*  291 */     this.spinnerInt.setBounds(10, 114, 70, 22);
/*      */ 
/*      */     
/*  294 */     this.spinnerModifyListener = new ModifyListener() {
/*      */         public void modifyText(ModifyEvent e) {
/*  296 */           ConfigEditor.this.updateInt(ConfigEditor.this.selected, ConfigEditor.this.spinnerInt.getSelection());
/*      */         }
/*      */       };
/*      */ 
/*      */     
/*  301 */     this.lblIntText = new Label(this.scrolledComposite, 0);
/*  302 */     this.lblIntText.setBounds(86, 116, 398, 18);
/*      */     
/*  304 */     this.comboList = new Combo(this.scrolledComposite, 8);
/*  305 */     this.comboList.setBounds(10, 114, 128, 23);
/*      */     
/*  307 */     this.comboModifyListener = new ModifyListener() {
/*      */         public void modifyText(ModifyEvent e) {
/*  309 */           ConfigEditor.this.updateString(ConfigEditor.this.selected, ConfigEditor.this.comboList.getText());
/*      */         }
/*      */       };
/*      */     
/*  313 */     this.lblList = new Label(this.scrolledComposite, 0);
/*  314 */     this.lblList.setBounds(150, 116, 357, 18);
/*      */     
/*  316 */     this.textString = new Text(this.scrolledComposite, 2048);
/*  317 */     this.textString.addModifyListener(new ModifyListener() {
/*      */           public void modifyText(ModifyEvent e) {
/*  319 */             ConfigEditor.this.updateString(ConfigEditor.this.selected, ConfigEditor.this.textString.getText());
/*      */           }
/*      */         });
/*  322 */     this.textString.setBounds(20, 116, 383, 21);
/*      */     
/*  324 */     this.btnFileDir = new Button(this.scrolledComposite, 0);
/*  325 */     this.btnFileDir.addSelectionListener((SelectionListener)new SelectionAdapter()
/*      */         {
/*      */           public void widgetSelected(SelectionEvent e)
/*      */           {
/*      */             String res;
/*      */ 
/*      */             
/*  332 */             if (ConfigEditor.this.getAttributeVal(ConfigEditor.this.selected.getAttributes(), "type").equals("directory")) {
/*  333 */               res = MainWin.getFile(false, true, ConfigEditor.this.textString.getText(), "Choose " + ConfigEditor.this.selected.getName(), "Set directory for " + ConfigEditor.this.selected.getName());
/*      */             } else {
/*  335 */               res = MainWin.getFile(false, false, ConfigEditor.this.textString.getText(), "Choose " + ConfigEditor.this.selected.getName(), "Choose file for " + ConfigEditor.this.selected.getName());
/*      */             } 
/*  337 */             if (res != null)
/*  338 */               ConfigEditor.this.textString.setText(res); 
/*      */           }
/*      */         });
/*  341 */     this.btnFileDir.setBounds(409, 113, 98, 25);
/*  342 */     this.btnFileDir.setText("Choose...");
/*      */     
/*  344 */     this.lblString = new Label(this.scrolledComposite, 0);
/*  345 */     this.lblString.setBounds(20, 98, 248, 18);
/*  346 */     this.composite_1.setLayoutData(new RowData((getSize()).x, 100));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doBackup() {
/*  358 */     String path = MainWin.getFile(true, false, "", "Save current config as...", "Save", new String[] { ".xml", "*.*" });
/*      */     
/*  360 */     if (path != null) {
/*      */       
/*  362 */       commitNodes();
/*      */ 
/*      */       
/*      */       try {
/*  366 */         this.wc.save(path);
/*      */       }
/*  368 */       catch (ConfigurationException e) {
/*      */         
/*  370 */         MainWin.showError("Error saving configuration", "A configuration exception occured", e.getMessage(), true);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doRestore() {
/*  383 */     String path = MainWin.getFile(false, false, "", "Load config from file...", "Open", new String[] { ".xml", "*.*" });
/*      */     
/*  385 */     if (path != null)
/*      */     {
/*      */       
/*  388 */       loadConfig(path);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void applyChanges() {
/*  399 */     commitNodes();
/*  400 */     XMLConfiguration temp = new XMLConfiguration();
/*      */ 
/*      */     
/*      */     try {
/*  404 */       StringWriter sw = new StringWriter();
/*      */       
/*  406 */       this.wc.save(sw);
/*      */       
/*  408 */       StringReader sr = new StringReader(sw.getBuffer().toString());
/*      */ 
/*      */       
/*  411 */       temp.load(sr);
/*      */     }
/*  413 */     catch (ConfigurationException e) {
/*      */ 
/*      */       
/*  416 */       e.printStackTrace();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  422 */     final ArrayList<String> cmds = generateConfigCommands("", temp.getRootNode().getChildren());
/*      */     
/*  424 */     cmds.add(0, "ui server conf freeze true");
/*      */     
/*  426 */     for (int i = 0; i < 256; i++) {
/*      */       
/*  428 */       cmds.add("ui server conf set Drive" + i + "Path");
/*  429 */       cmds.add("ui server conf set Drive" + i + "Path[@category] disk,advanced");
/*  430 */       cmds.add("ui server conf set Drive" + i + "Path[@type] file");
/*      */     } 
/*      */ 
/*      */     
/*  434 */     cmds.add("ui server conf freeze false");
/*      */     class Stupid
/*      */     {
/*      */       ConfigEditorTaskWin ctw;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ConfigEditorTaskWin getCtw() {
/*  445 */         return this.ctw;
/*      */       }
/*      */ 
/*      */       
/*      */       public void initCtw(Shell shell) {
/*  450 */         this.ctw = new ConfigEditorTaskWin(shell, 2144, "Writing config to server...", "Please wait while the configuration is written");
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */     
/*  456 */     final Shell shell = this;
/*  457 */     final Stupid stupid = new Stupid();
/*      */     
/*  459 */     Runnable lc = new Runnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*      */         public void run()
/*      */         {
/*  466 */           ConfigEditor.this.getDisplay().syncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 
/*      */                 public void run()
/*      */                 {
/*  472 */                   stupid.initCtw(shell);
/*  473 */                   stupid.getCtw().open();
/*      */                 }
/*      */               });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/*  482 */             stupid.getCtw().setStatus("Generate command list...", 10);
/*      */             
/*  484 */             stupid.getCtw().setStatus("Sending config commands...", 20);
/*      */             
/*  486 */             double slice = 80.0D / Double.valueOf(cmds.size()).doubleValue();
/*  487 */             double i = 0.0D;
/*  488 */             for (String cmd : cmds) {
/*      */ 
/*      */               
/*  491 */               Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*  492 */               conn.Connect();
/*  493 */               conn.loadList(-1, cmd);
/*  494 */               conn.close();
/*      */ 
/*      */ 
/*      */               
/*  498 */               stupid.getCtw().setStatus("Sending config commands...", (int)(20.0D + i * slice));
/*  499 */               i++;
/*      */             } 
/*      */             
/*  502 */             stupid.getCtw().setStatus("Complete", 100);
/*      */             
/*  504 */             stupid.getCtw().closeWin();
/*      */           }
/*  506 */           catch (UnknownHostException e) {
/*      */             
/*  508 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           }
/*  510 */           catch (IOException e) {
/*      */             
/*  512 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           }
/*  514 */           catch (DWUIOperationFailedException e) {
/*      */             
/*  516 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           } 
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */     
/*  523 */     Thread tc = new Thread(lc);
/*  524 */     tc.start();
/*      */   }
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
/*      */   
/*      */   protected ArrayList<String> generateConfigCommands(String key, List<HierarchicalConfiguration.Node> nodes) {
/*  542 */     ArrayList<String> res = new ArrayList<String>();
/*      */     
/*  544 */     List<ConfigItem> items = new ArrayList<ConfigItem>();
/*      */     
/*  546 */     HashMap<String, Integer> count = new HashMap<String, Integer>();
/*      */     
/*  548 */     for (HierarchicalConfiguration.Node t : nodes) {
/*      */ 
/*      */       
/*  551 */       if (count.containsKey(t.getName())) {
/*  552 */         count.put(t.getName(), Integer.valueOf(((Integer)count.get(t.getName())).intValue() + 1));
/*      */       } else {
/*  554 */         count.put(t.getName(), Integer.valueOf(0));
/*      */       } 
/*  556 */       items.add(new ConfigItem(t, ((Integer)count.get(t.getName())).intValue()));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  561 */     Collections.sort(items);
/*      */     
/*  563 */     for (ConfigItem item : items) {
/*      */       
/*  565 */       String cmd = "ui server conf set ";
/*      */       
/*  567 */       String thiskey = item.getNode().getName() + "(" + item.getIndex() + ")";
/*      */       
/*  569 */       if (key != "") {
/*  570 */         thiskey = key + "." + thiskey;
/*      */       }
/*  572 */       cmd = cmd + thiskey;
/*      */       
/*  574 */       if (item.getNode().getValue() != null)
/*      */       {
/*  576 */         cmd = cmd + " " + item.getNode().getValue().toString();
/*      */       }
/*      */       
/*  579 */       res.add(cmd);
/*      */ 
/*      */       
/*  582 */       HashMap<String, String> multi = new HashMap<String, String>();
/*      */ 
/*      */       
/*  585 *//*      *//*      *//*  585 */ for (Iterator it = item.getNode().getAttributes().iterator(); it.hasNext();) {
    HierarchicalConfiguration.Node atn = (HierarchicalConfiguration.Node) it.next();
    /*      */
    /*  587 */         if (item.getNode().getAttributeCount(atn.getName()) > 1) {
        /*      */
        /*      */
        /*  590 */           if (multi.containsKey(thiskey + "[@" + atn.getName() + "]")) {
            /*  591 */             multi.put(thiskey + "[@" + atn.getName() + "]", (String)multi.get(thiskey + "[@" + atn.getName() + "]") + "," + atn.getValue().toString()); continue;
        /*      */           }
        /*  593 */           multi.put(thiskey + "[@" + atn.getName() + "]", atn.getValue().toString());
        /*      */
        /*      */           continue;
    /*      */         }
    /*      */
    /*  598 */         res.add("ui server conf set " + thiskey + "[@" + atn.getName() + "] " + atn.getValue());
    /*      */
    }
 for (Map.Entry<String, String> e : multi.entrySet())
/*      */       {
/*  605 */         res.add("ui server conf set " + (String)e.getKey() + " " + (String)e.getValue());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  610 */       if (item.getNode().hasChildren())
/*      */       {
/*  612 */         res.addAll(generateConfigCommands(thiskey, item.getNode().getChildren()));
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  618 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getTreePath(TreeItem ti) {
/*  627 */     String res = ((HierarchicalConfiguration.Node)ti.getData("param")).getName();
/*      */     
/*  629 */     res = res + "(" + (Integer)ti.getData("index") + ")";
/*      */     
/*  631 */     if (ti.getParentItem() != null) {
/*  632 */       res = getTreePath(ti.getParentItem()) + "." + res;
/*      */     }
/*  634 */     return res;
/*      */   }
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
/*      */   protected void updateBoolean(HierarchicalConfiguration.Node node, boolean selection) {
/*  647 */     node.setValue(Boolean.valueOf(selection));
/*  648 */     this.tree.getSelection()[0].setText(1, selection + "");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateInt(HierarchicalConfiguration.Node node, int selection) {
/*  654 */     node.setValue(Integer.valueOf(selection));
/*  655 */     this.tree.getSelection()[0].setText(1, selection + "");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateString(HierarchicalConfiguration.Node node, String selection) {
/*  661 */     node.setValue(selection);
/*      */     
/*  663 */     if (selection == null) {
/*  664 */       this.tree.getSelection()[0].setText(1, "");
/*      */     } else {
/*  666 */       this.tree.getSelection()[0].setText(1, selection);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getKeyPath(HierarchicalConfiguration.Node node) {
/*  678 */     String res = node.getName();
/*      */ 
/*      */     
/*  681 */     if (node.getParent() != null)
/*      */     {
/*  683 */       res = getKeyPath(node.getParent()) + "." + res;
/*      */     }
/*      */ 
/*      */     
/*  687 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void displayParam(TreeItem ti) {
/*  698 */     HierarchicalConfiguration.Node node = (HierarchicalConfiguration.Node)ti.getData("param");
/*      */     
/*  700 */     if (getAttributeVal(node.getAttributes(), "type") == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  705 */       setDisplayFor((TreeItem)null, "none");
/*      */     }
/*      */     else {
/*      */       
/*  709 */       String type = getAttributeVal(node.getAttributes(), "type").toString();
/*      */       
/*  711 */       setDisplayFor(ti, type);
/*      */     } 
/*      */   }
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
/*      */   private void setDisplayFor(TreeItem ti, String type) {
/*  729 */     setRedraw(false);
/*      */     
/*  731 */     this.lblItemTitle.setVisible(false);
/*  732 */     this.btnToggle.setVisible(false);
/*  733 */     this.textDescription.setVisible(false);
/*  734 */     this.spinnerInt.setVisible(false);
/*  735 */     this.lblIntText.setVisible(false);
/*  736 */     this.comboList.setVisible(false);
/*  737 */     this.lblList.setVisible(false);
/*  738 */     this.lblString.setVisible(false);
/*  739 */     this.btnFileDir.setVisible(false);
/*  740 */     this.textString.setVisible(false);
/*      */     
/*  742 */     if (ti == null) {
/*      */       
/*  744 */       setRedraw(true);
/*      */       
/*      */       return;
/*      */     } 
/*  748 */     HierarchicalConfiguration.Node node = (HierarchicalConfiguration.Node)ti.getData("param");
/*      */     
/*  750 */     if (type.equals("boolean")) {
/*      */       
/*  752 */       this.lblItemTitle.setVisible(true);
/*  753 */       this.lblItemTitle.setText(node.getName());
/*  754 */       this.btnToggle.setVisible(true);
/*      */       
/*  756 */       if (ti.getText(2).equals("")) {
/*  757 */         this.btnToggle.setSelection(Boolean.parseBoolean(node.getValue().toString()));
/*      */       } else {
/*  759 */         this.btnToggle.setSelection(Boolean.parseBoolean(ti.getText(2)));
/*      */       } 
/*  761 */       this.textDescription.setVisible(true);
/*  762 */       this.btnToggle.setText("Enable " + node.getName());
/*  763 */       showHelpFor(node);
/*      */     
/*      */     }
/*  766 */     else if (type.equals("int")) {
/*      */       
/*  768 */       this.lblItemTitle.setVisible(true);
/*  769 */       this.lblItemTitle.setText(node.getName());
/*      */       
/*  771 */       this.textDescription.setVisible(true);
/*  772 */       showHelpFor(node);
/*      */       
/*  774 */       this.spinnerInt.removeModifyListener(this.spinnerModifyListener);
/*      */ 
/*      */       
/*  777 */       if (hasAttribute(node.getAttributes(), "min")) {
/*  778 */         this.spinnerInt.setMinimum(Integer.parseInt(getAttributeVal(node.getAttributes(), "min").toString()));
/*      */       }
/*  780 */       if (hasAttribute(node.getAttributes(), "max")) {
/*  781 */         this.spinnerInt.setMaximum(Integer.parseInt(getAttributeVal(node.getAttributes(), "max").toString()));
/*      */       }
/*      */       
/*  784 */       if (ti.getText(2).equals("")) {
/*      */         
/*  786 */         if (node.getValue() != null) {
/*  787 */           this.spinnerInt.setSelection(Integer.parseInt(node.getValue().toString()));
/*      */         }
/*      */       } else {
/*  790 */         this.spinnerInt.setSelection(Integer.parseInt(ti.getText(2)));
/*      */       } 
/*  792 */       this.spinnerInt.addModifyListener(this.spinnerModifyListener);
/*      */       
/*  794 */       this.spinnerInt.setVisible(true);
/*  795 */       this.lblIntText.setVisible(true);
/*  796 */       this.lblIntText.setText("Choose value for " + node.getName());
/*      */ 
/*      */     
/*      */     }
/*  800 */     else if (type.equals("list")) {
/*      */       
/*  802 */       this.lblItemTitle.setVisible(true);
/*  803 */       this.lblItemTitle.setText(node.getName());
/*  804 */       this.comboList.setVisible(true);
/*      */       
/*  806 */       this.comboList.removeModifyListener(this.comboModifyListener);
/*      */       
/*  808 */       this.comboList.removeAll();
/*      */       
/*  810 */       if (hasAttribute(node.getAttributes(), "list"))
/*      */       {
/*  812 */
    for (Iterator it = getAttributeVals(node.getAttributes(), "list").iterator(); it.hasNext();) {
        String s = (String) it.next();
        /*  814 */           this.comboList.add(s);
        /*      */
    }/*      *//*      */
       }
/*      */ 
/*      */       
/*  819 */       if (ti.getText(2).equals("")) {
/*      */         
/*  821 */         if (node.getValue() != null) {
/*  822 */           this.comboList.select(this.comboList.indexOf(node.getValue().toString()));
/*      */         }
/*      */       } else {
/*  825 */         this.comboList.select(this.comboList.indexOf(ti.getText(2)));
/*      */       } 
/*  827 */       this.comboList.addModifyListener(this.comboModifyListener);
/*      */       
/*  829 */       this.lblList.setVisible(true);
/*  830 */       this.lblList.setText("Select value for " + node.getName());
/*      */       
/*  832 */       this.textDescription.setVisible(true);
/*  833 */       showHelpFor(node);
/*      */     }
/*  835 */     else if (type.equals("section")) {
/*      */       
/*  837 */       this.lblItemTitle.setVisible(true);
/*  838 */       this.lblItemTitle.setText(node.getName());
/*  839 */       this.comboList.setVisible(true);
/*      */       
/*  841 */       this.comboList.removeModifyListener(this.comboModifyListener);
/*      */       
/*  843 */       this.comboList.removeAll();
/*      */       
/*  845 */       if (hasAttribute(node.getAttributes(), "section")) {
/*      */         
/*  847 */         String key = getAttributeVal(node.getAttributes(), "section").toString();
/*      */         
/*  849 */         for (int i = 0; i <= MainWin.dwconfig.getMaxIndex(key); i++) {
/*      */           
/*  851 */           if (MainWin.dwconfig.containsKey(key + "(" + i + ")[@name]")) {
/*  852 */             this.comboList.add(MainWin.dwconfig.getString(key + "(" + i + ")[@name]"));
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  859 */       if (ti.getText(2).equals("")) {
/*  860 */         this.comboList.select(this.comboList.indexOf(node.getValue().toString()));
/*      */       } else {
/*  862 */         this.comboList.select(this.comboList.indexOf(ti.getText(2)));
/*      */       } 
/*      */       
/*  865 */       this.comboList.addModifyListener(this.comboModifyListener);
/*      */       
/*  867 */       this.lblList.setVisible(true);
/*  868 */       this.lblList.setText("Select value for " + node.getName());
/*      */       
/*  870 */       this.textDescription.setVisible(true);
/*  871 */       showHelpFor(node);
/*      */     }
/*  873 */     else if (type.equals("string")) {
/*      */       
/*  875 */       this.lblItemTitle.setVisible(true);
/*  876 */       this.lblItemTitle.setText(node.getName());
/*      */       
/*  878 */       this.textDescription.setVisible(true);
/*  879 */       showHelpFor(node);
/*      */       
/*  881 */       if (ti.getText(2).equals(""))
/*  882 */       { if (node.getValue() != null) {
/*  883 */           this.textString.setText(node.getValue().toString());
/*      */         } else {
/*  885 */           this.textString.setText("");
/*      */         }  }
/*  887 */       else { this.textString.setText(ti.getText(2)); }
/*      */       
/*  889 */       this.textString.setVisible(true);
/*  890 */       this.lblString.setVisible(true);
/*  891 */       this.lblString.setText("Enter value for " + node.getName());
/*      */     
/*      */     }
/*  894 */     else if (type.equals("file") || type.equals("directory")) {
/*      */       
/*  896 */       this.lblItemTitle.setVisible(true);
/*  897 */       this.lblItemTitle.setText(node.getName());
/*      */       
/*  899 */       this.textDescription.setVisible(true);
/*  900 */       showHelpFor(node);
/*      */ 
/*      */       
/*  903 */       if (ti.getText(2).equals(""))
/*  904 */       { if (node.getValue() != null) {
/*  905 */           this.textString.setText(node.getValue().toString());
/*      */         } else {
/*  907 */           this.textString.setText("");
/*      */         }  }
/*  909 */       else { this.textString.setText(ti.getText(2)); }
/*      */       
/*  911 */       this.textString.setVisible(true);
/*  912 */       this.btnFileDir.setVisible(true);
/*      */     } 
/*      */     
/*  915 */     setRedraw(true);
/*      */   }
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
/*      */   private void showHelpFor(final HierarchicalConfiguration.Node node) {
/*  929 */     this.textDescription.setText("Loading help for " + node.getName() + "...");
/*      */ 
/*      */     
/*  932 */     Thread t = new Thread(new Runnable()
/*      */         {
/*      */           public void run()
/*      */           {
/*  936 */             String txt = "";
/*      */ 
/*      */ 
/*      */             
/*      */             try {
/*  941 */               List<String> help = UIUtils.loadList(MainWin.getInstance(), "ui server show help " + ConfigEditor.this.getKeyPath(node));
/*      */ 
/*      */               
/*  944 */               for (String t : help)
/*      */               {
/*  946 */                 txt = txt + t;
/*      */               
/*      */               }
/*      */             
/*      */             }
/*  951 */             catch (IOException e) {
/*      */               
/*  953 */               txt = e.getMessage();
/*      */             }
/*  955 */             catch (DWUIOperationFailedException e) {
/*      */               
/*  957 */               txt = txt + "No help found for " + node.getName();
/*      */             } 
/*      */             
/*  960 */             final String ftxt = txt;
/*  961 */             final String fname = node.getName();
/*      */             
/*  963 */             if (!ConfigEditor.this.isDisposed())
/*  964 */               ConfigEditor.this.getDisplay().asyncExec(new Runnable()
/*      */                   {
/*      */                     public void run() {
/*  967 */                       if (!ConfigEditor.this.textDescription.isDisposed() && !ConfigEditor.this.lblItemTitle.isDisposed() && ConfigEditor.this.lblItemTitle.getText().equals(fname))
/*  968 */                         ConfigEditor.this.textDescription.setText(ftxt); 
/*      */                     }
/*      */                   }); 
/*      */           }
/*      */         });
/*  973 */     t.start();
/*      */   }
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
/*      */   private void loadConfig(TreeItem ti, List<HierarchicalConfiguration.Node> nodes) {
/*  988 */     this.tree.removeAll();
/*  989 */     setDisplayFor((TreeItem)null, "none");
/*  990 */     setRedraw(false);
/*  991 */     loadAllConfig(ti, nodes);
/*  992 */     commitNodes();
/*  993 */     filterConfig((TreeItem)null);
/*  994 */     setRedraw(true);
/*      */     
/*  996 */     if (this.selected != null) {
/*  997 */       tryToSelect(this.selected);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void tryToSelect(HierarchicalConfiguration.Node node) {
/* 1004 */     for (TreeItem t : this.tree.getItems()) {
/*      */       
/* 1006 */       if (t.getData("param").equals(node)) {
/*      */         
/* 1008 */         this.tree.select(t);
/* 1009 */         displayParam(t);
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/* 1016 */     setDisplayFor((TreeItem)null, "none");
/*      */   }
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
/*      */   private void filterConfig(TreeItem ti) {
/*      */     TreeItem[] items;
/* 1031 */     if (ti == null) {
/* 1032 */       items = this.tree.getItems();
/*      */     } else {
/* 1034 */       items = ti.getItems();
/*      */     } 
/* 1036 */     for (TreeItem item : items) {
/*      */       
/* 1038 */       if (item.getItemCount() > 0)
/*      */       {
/* 1040 */         filterConfig(item);
/*      */       }
/*      */       
/* 1043 */       if (item.getItemCount() == 0)
/*      */       {
/* 1045 */         if (!filterItem((HierarchicalConfiguration.Node)item.getData("param")))
/*      */         {
/* 1047 */           item.dispose();
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void commitNodes() {
/* 1056 */     commitNodes(this.tree.getItems());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void commitNodes(TreeItem[] treeitems) {
/* 1062 */     for (TreeItem ti : treeitems) {
/*      */       
/* 1064 */       if (ti.getItemCount() > 0)
/*      */       {
/* 1066 */         commitNodes(ti.getItems());
/*      */       }
/*      */       
/* 1069 */       HierarchicalConfiguration.Node node = (HierarchicalConfiguration.Node)ti.getData("param");
/*      */       
/* 1071 */       String key = getTreePath(ti);
/*      */       
/* 1073 */       this.wc.setProperty(key, node.getValue());
/*      */       
/* 1075 */       HashMap<String, String> multi = new HashMap<String, String>();
/*      */ 
/*      */       
/* 1078 *//*      *//*      *//* 1078 */ for (Iterator it = node.getAttributes().iterator(); it.hasNext();) {
    HierarchicalConfiguration.Node atn = (HierarchicalConfiguration.Node) it.next();
    /*      */
    /* 1080 */         if (node.getAttributeCount(atn.getName()) > 1) {
        /*      */
        /*      */
        /* 1083 */           if (multi.containsKey(key + "[@" + atn.getName() + "]")) {
            /* 1084 */             multi.put(key + "[@" + atn.getName() + "]", (String)multi.get(key + "[@" + atn.getName() + "]") + "," + atn.getValue().toString()); continue;
        /*      */           }
        /* 1086 */           multi.put(key + "[@" + atn.getName() + "]", atn.getValue().toString());
        /*      */
        /*      */           continue;
    /*      */         }
    /*      */
    /* 1091 */         this.wc.setProperty(key + "[@" + atn.getName() + "]", atn.getValue());
    /*      */
    }
 for (Map.Entry<String, String> e : multi.entrySet())
/*      */       {
/* 1098 */         this.wc.setProperty(e.getKey(), e.getValue());
/*      */       }
/*      */     } 
/*      */   }
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
/*      */   private void loadAllConfig(TreeItem ti, List<HierarchicalConfiguration.Node> nodes) {
/* 1113 */     List<ConfigItem> items = new ArrayList<ConfigItem>();
/*      */     
/* 1115 */     HashMap<String, Integer> count = new HashMap<String, Integer>();
/*      */     
/* 1117 */     for (HierarchicalConfiguration.Node t : nodes) {
/*      */ 
/*      */       
/* 1120 */       if (count.containsKey(t.getName())) {
/* 1121 */         count.put(t.getName(), Integer.valueOf(((Integer)count.get(t.getName())).intValue() + 1));
/*      */       } else {
/* 1123 */         count.put(t.getName(), Integer.valueOf(0));
/*      */       } 
/* 1125 */       items.add(new ConfigItem(t, ((Integer)count.get(t.getName())).intValue()));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1130 */     Collections.sort(items);
/*      */     
/* 1132 */     for (ConfigItem item : items) {
/*      */       TreeItem tmp;
/*      */       
/* 1135 */       if (ti == null) {
/*      */         
/* 1137 */         tmp = new TreeItem(this.tree, 0);
/*      */       }
/*      */       else {
/*      */         
/* 1141 */         tmp = new TreeItem(ti, 0);
/*      */       } 
/*      */ 
/*      */       
/* 1145 */       applyMaster(item.node);
/*      */       
/* 1147 */       if (getAttributeVal(item.getNode().getAttributes(), "name") == null) {
/* 1148 */         tmp.setText(0, item.getNode().getName());
/*      */       } else {
/* 1150 */         tmp.setText(0, item.getNode().getName() + ": " + getAttributeVal(item.getNode().getAttributes(), "name").toString());
/*      */       } 
/* 1152 */       tmp.setData("param", item.getNode());
/* 1153 */       tmp.setData("index", Integer.valueOf(item.getIndex()));
/*      */       
/* 1155 */       if (item.getNode().getValue() == null) {
/*      */         
/* 1157 */         if (getAttributeVal(item.getNode().getAttributes(), "desc") != null) {
/* 1158 */           tmp.setText(1, getAttributeVal(item.getNode().getAttributes(), "desc").toString());
/* 1159 */         } else if (hasAttribute(item.getNode().getAttributes(), "dev") && hasAttribute(item.getNode().getAttributes(), "gm")) {
/*      */           
/* 1161 */           tmp.setText(1, "In (native): " + getAttributeVal(item.getNode().getAttributes(), "dev").toString() + " Out (GM): " + getAttributeVal(item.getNode().getAttributes(), "gm").toString());
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1166 */         tmp.setText(1, item.getNode().getValue().toString());
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1172 */       if (item.getNode().hasChildren()) {
/*      */         
/* 1174 */         loadAllConfig(tmp, item.getNode().getChildren());
/*      */         
/* 1176 */         if (item.getNode().getName().equals("instance")) {
/* 1177 */           tmp.setExpanded(true);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean filterItem(HierarchicalConfiguration.Node t) {
/* 1191 */     List<HierarchicalConfiguration.Node> attributes = t.getAttributes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1197 */     if (!this.tltmCheckAdvanced.getSelection() && matchAttributeVal(attributes, "category", "advanced")) {
/* 1198 */       return false;
/*      */     }
/*      */     
/* 1201 */     if (this.tltmAll.getSelection()) {
/* 1202 */       return true;
/*      */     }
/*      */ 
/*      */     
/* 1206 */     if (this.tltmMidi.getSelection() && !matchCategory(t, "midi")) {
/* 1207 */       return false;
/*      */     }
/* 1209 */     if (this.tltmDevice.getSelection() && !matchCategory(t, "device")) {
/* 1210 */       return false;
/*      */     }
/* 1212 */     if (this.tltmPrinting.getSelection() && !matchCategory(t, "printing")) {
/* 1213 */       return false;
/*      */     }
/* 1215 */     if (this.tltmLogging.getSelection() && !matchCategory(t, "logging")) {
/* 1216 */       return false;
/*      */     }
/* 1218 */     if (this.tltmDisk.getSelection() && !matchCategory(t, "disk")) {
/* 1219 */       return false;
/*      */     }
/* 1221 */     if (this.tltmNetworking.getSelection() && !matchCategory(t, "networking")) {
/* 1222 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1229 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean matchCategory(HierarchicalConfiguration.Node node, String category) {
/* 1241 */     if (matchAttributeVal(node.getAttributes(), "category", category)) {
/* 1242 */       return true;
/*      */     }
/* 1244 */     if (node.getParent() != null)
/*      */     {
/* 1246 */       return matchCategory(node.getParent(), category);
/*      */     }
/*      */     
/* 1249 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean matchAttributeVal(List<HierarchicalConfiguration.Node> attributes, String key, String val) {
/* 1258 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1260 */       if (n.getName().equals(key) && n.getValue().equals(val)) {
/* 1261 */         return true;
/*      */       }
/*      */     } 
/* 1264 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object getAttributeVal(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1273 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1275 */       if (n.getName().equals(key))
/*      */       {
/* 1277 */         return n.getValue();
/*      */       }
/*      */     } 
/*      */     
/* 1281 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private List<String> getAttributeVals(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1286 */     List<String> res = new ArrayList<String>();
/*      */     
/* 1288 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1290 */       if (n.getName().equals(key))
/*      */       {
/* 1292 */         res.add(n.getValue().toString());
/*      */       }
/*      */     } 
/*      */     
/* 1296 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasAttribute(List<HierarchicalConfiguration.Node> attributes, String key) {
/* 1303 */     for (HierarchicalConfiguration.Node n : attributes) {
/*      */       
/* 1305 */       if (n.getName().equals(key)) {
/* 1306 */         return true;
/*      */       }
/*      */     } 
/* 1309 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadConfig() {
/*      */     class Stupid
/*      */     {
/*      */       ConfigEditorTaskWin ctw;
/*      */ 
/*      */ 
/*      */       
/*      */       public ConfigEditorTaskWin getCtw() {
/* 1323 */         return this.ctw;
/*      */       }
/*      */ 
/*      */       
/*      */       public void initCtw(Shell shell) {
/* 1328 */         this.ctw = new ConfigEditorTaskWin(shell, 2144, "Loading config from server...", "Please wait while the server's configuration is loaded");
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */     
/* 1334 */     final Shell shell = this;
/* 1335 */     final Stupid stupid = new Stupid();
/*      */     
/* 1337 */     Runnable lc = new Runnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*      */         public void run()
/*      */         {
/* 1344 */           ConfigEditor.this.getDisplay().syncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 
/*      */                 public void run()
/*      */                 {
/* 1350 */                   stupid.initCtw(shell);
/* 1351 */                   stupid.getCtw().open();
/*      */                 }
/*      */               });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1360 */             Connection conn = new Connection(MainWin.getHost(), MainWin.getPort(), MainWin.getInstance());
/*      */             
/* 1362 */             stupid.getCtw().setStatus("Connecting to server...", 10);
/* 1363 */             conn.Connect();
/*      */             
/* 1365 */             stupid.getCtw().setStatus("Sending command...", 20);
/* 1366 */             StringReader sr = conn.loadReader(-1, "ui server config write");
/*      */             
/* 1368 */             stupid.getCtw().setStatus("Received response", 30);
/* 1369 */             conn.close();
/*      */             
/* 1371 */             stupid.getCtw().setStatus("Processing config...", 60);
/* 1372 */             ConfigEditor.this.wc.clear();
/* 1373 */             ConfigEditor.this.wc.load(sr);
/*      */ 
/*      */             
/* 1376 */             stupid.getCtw().setStatus("Processing config...", 85);
/*      */             
/* 1378 */             ConfigEditor.this.getDisplay().syncExec(new Runnable()
/*      */                 {
/*      */                   
/*      */                   public void run()
/*      */                   {
/* 1383 */                     ConfigEditor.this.loadConfig((TreeItem)null, ConfigEditor.this.wc.getRoot().getChildren());
/*      */                   }
/*      */                 });
/*      */ 
/*      */ 
/*      */             
/* 1389 */             stupid.getCtw().setStatus("Complete", 100);
/*      */             
/* 1391 */             stupid.getCtw().closeWin();
/*      */           }
/* 1393 */           catch (UnknownHostException e) {
/*      */             
/* 1395 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           }
/* 1397 */           catch (ConfigurationException e) {
/*      */             
/* 1399 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           }
/* 1401 */           catch (IOException e) {
/*      */             
/* 1403 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           }
/* 1405 */           catch (DWUIOperationFailedException e) {
/*      */             
/* 1407 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           } 
/*      */         }
/*      */       };
/*      */ 
/*      */     
/* 1413 */     if (!isDisposed()) {
/*      */       
/* 1415 */       Thread tc = new Thread(lc);
/* 1416 */       tc.start();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadConfig(final String filepath) {
/*      */     class Stupid
/*      */     {
/*      */       ConfigEditorTaskWin ctw;
/*      */ 
/*      */ 
/*      */       
/*      */       public ConfigEditorTaskWin getCtw() {
/* 1431 */         return this.ctw;
/*      */       }
/*      */ 
/*      */       
/*      */       public void initCtw(Shell shell) {
/* 1436 */         this.ctw = new ConfigEditorTaskWin(shell, 2144, "Loading config from file...", "Please wait while the configuration is loaded");
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */     
/* 1442 */     final Shell shell = this;
/* 1443 */     final Stupid stupid = new Stupid();
/*      */     
/* 1445 */     Runnable lc = new Runnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*      */         public void run()
/*      */         {
/* 1452 */           ConfigEditor.this.getDisplay().syncExec(new Runnable()
/*      */               {
/*      */ 
/*      */                 
/*      */                 public void run()
/*      */                 {
/* 1458 */                   stupid.initCtw(shell);
/* 1459 */                   stupid.getCtw().open();
/*      */                 }
/*      */               });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           try {
/* 1469 */             stupid.getCtw().setStatus("Reading config...", 30);
/* 1470 */             ConfigEditor.this.wc.clear();
/* 1471 */             ConfigEditor.this.wc.load(filepath);
/*      */             
/* 1473 */             stupid.getCtw().setStatus("Processing config...", 60);
/*      */             
/* 1475 */             ConfigEditor.this.getDisplay().syncExec(new Runnable()
/*      */                 {
/*      */                   
/*      */                   public void run()
/*      */                   {
/* 1480 */                     ConfigEditor.this.loadConfig((TreeItem)null, ConfigEditor.this.wc.getRoot().getChildren());
/*      */                   }
/*      */                 });
/*      */ 
/*      */ 
/*      */             
/* 1486 */             stupid.getCtw().setStatus("Complete", 100);
/*      */             
/* 1488 */             stupid.getCtw().closeWin();
/*      */           }
/* 1490 */           catch (Exception e) {
/*      */             
/* 1492 */             stupid.getCtw().setErrorStatus(e.getMessage());
/*      */           } 
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */     
/* 1499 */     Thread tc = new Thread(lc);
/* 1500 */     tc.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadMaster() {
/* 1507 */     this.masterc.clear();
/*      */     
/*      */     try {
/* 1510 */       this.masterc.load("master.xml");
/*      */     }
/* 1512 */     catch (ConfigurationException e) {
/*      */       
/* 1514 */       MainWin.showError("Error loading master config", e.getClass().getSimpleName() + ": " + e.getMessage(), UIUtils.getStackTrace((Throwable)e), false);
/* 1515 */       dispose();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void applyMaster(HierarchicalConfiguration.Node node) {
/* 1522 */     String key = getKeyPath(node);
/* 1523 */     if (key.startsWith("drivewire-config."))
/*      */     {
/* 1525 */       key = key.substring(17);
/*      */     }
/*      */     
/* 1528 */     if (this.masterc.getMaxIndex(key) > -1) {
/*      */       
/* 1530 */       HierarchicalConfiguration.Node mnode = this.masterc.configurationAt(key).getRoot();
/*      */       
/*      */       int i;
/*      */       
/* 1534 */       for (i = 0; i < mnode.getAttributeCount(); i++) {
/*      */         
/* 1536 */         if (node.getAttributeCount(mnode.getAttribute(i).getName()) > 0) {
/* 1537 */           node.removeAttribute(mnode.getAttribute(i).getName());
/*      */         }
/*      */       } 
/*      */       
/* 1541 */       for (i = 0; i < mnode.getAttributeCount(); i++)
/*      */       {
/* 1543 */         node.addAttribute((ConfigurationNode)mnode.getAttribute(i).clone());
/*      */       }
/*      */     } 
/*      */   }
/*      */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/ConfigEditor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */