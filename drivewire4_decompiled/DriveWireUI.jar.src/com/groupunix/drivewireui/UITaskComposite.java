/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.MenuAdapter;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Decorations;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ 
/*     */ public class UITaskComposite extends Composite {
/*     */   protected Canvas status;
/*  26 */   protected String cmd = ""; protected StyledText details;
/*  27 */   protected String det = "";
/*  28 */   private int activeframe = 0;
/*  29 */   private int taskid = -1;
/*     */   
/*     */   protected int stat;
/*     */   
/*  33 */   public static Image activeFrames = SWTResourceManager.getImage(MainWin.class, "/status/process-working-2.png");
/*     */ 
/*     */   
/*     */   public UITaskComposite(Composite master, int style, int tid) {
/*  37 */     super(master, style | 0x20000000);
/*  38 */     this.taskid = tid;
/*  39 */     createContents(master);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void createContents(Composite master) {
/*  45 */     setBackground(MainWin.colorWhite);
/*     */     
/*  47 */     this.status = new Canvas(this, 536870912);
/*  48 */     this.status.setBounds(3, 3, 37, 37);
/*  49 */     this.status.setBackground(MainWin.colorWhite);
/*     */     
/*  51 */     this.status.addPaintListener(new PaintListener()
/*     */         {
/*     */           public void paintControl(PaintEvent e)
/*     */           {
/*     */             int x, y;
/*  56 */             switch (UITaskComposite.this.stat) {
/*     */               
/*     */               case 0:
/*  59 */                 x = UITaskComposite.this.activeframe % 8 * 32;
/*  60 */                 y = UITaskComposite.this.activeframe / 8 * 32;
/*     */ 
/*     */                 
/*  63 */                 e.gc.drawImage(UITaskComposite.activeFrames, x, y, 32, 32, 0, 0, 32, 32);
/*     */                 return;
/*     */               case 2:
/*  66 */                 e.gc.drawImage(SWTResourceManager.getImage(MainWin.class, "/status/failed.png"), 0, 0);
/*     */                 return;
/*     */               case 1:
/*  69 */                 e.gc.drawImage(SWTResourceManager.getImage(MainWin.class, "/status/completed.png"), 0, 0);
/*     */                 return;
/*     */             } 
/*  72 */             e.gc.drawImage(SWTResourceManager.getImage(MainWin.class, "/status/unknown.png"), 0, 0);
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     this.details = new StyledText(this, 536870986);
/*     */ 
/*     */     
/*  84 */     this.details.setBounds(42, 4, (master.getClientArea()).width - 44, 40);
/*  85 */     this.details.setSize((master.getClientArea()).width - 44, 40);
/*  86 */     this.details.setText("");
/*     */     
/*  88 */     this.details.setFont(UITaskMaster.taskFont());
/*     */     
/*  90 */     this.details.setBackground(MainWin.colorWhite);
/*     */     
/*  92 */     final Menu menu = new Menu((Decorations)getShell(), 8);
/*  93 */     this.details.setMenu(menu);
/*     */ 
/*     */ 
/*     */     
/*  97 */     menu.addMenuListener((MenuListener)new MenuAdapter()
/*     */         {
/*     */           public void menuShown(MenuEvent e)
/*     */           {
/* 101 */             for (MenuItem m : menu.getItems())
/*     */             {
/* 103 */               m.dispose();
/*     */             }
/*     */ 
/*     */             
/* 107 */             if (UITaskComposite.this.details.getSelectionCount() > 0) {
/*     */               
/* 109 */               MenuItem miCopy = new MenuItem(menu, 8);
/* 110 */               miCopy.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                   {
/*     */                     public void widgetSelected(SelectionEvent e)
/*     */                     {
/* 114 */                       if (UITaskComposite.this.details.getSelectionCount() > 0) {
/*     */                         
/* 116 */                         String txt = UITaskComposite.this.details.getSelectionText();
/*     */                         
/* 118 */                         if (txt != null) {
/*     */                           
/* 120 */                           Clipboard clipboard = new Clipboard(MainWin.getDisplay());
/* 121 */                           TextTransfer textTransfer = TextTransfer.getInstance();
/*     */                           
/* 123 */                           Transfer[] transfers = { (Transfer)textTransfer };
/* 124 */                           Object[] data = { txt };
/* 125 */                           clipboard.setContents(data, transfers);
/* 126 */                           clipboard.dispose();
/*     */                         } 
/*     */                       } 
/*     */                     }
/*     */                   });
/*     */               
/* 132 */               miCopy.setText("Copy");
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 137 */             if (UITaskComposite.this.cmd != null && UITaskComposite.this.cmd.startsWith("dw")) {
/*     */               
/* 139 */               MenuItem miRecall = new MenuItem(menu, 8);
/* 140 */               miRecall.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                   {
/*     */                     public void widgetSelected(SelectionEvent e)
/*     */                     {
/* 144 */                       MainWin.setDWCmdText(UITaskComposite.this.cmd);
/*     */                     }
/*     */                   });
/*     */               
/* 148 */               miRecall.setText("Recall command");
/*     */ 
/*     */               
/* 151 */               MenuItem miSend = new MenuItem(menu, 8);
/* 152 */               miSend.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                   {
/*     */                     public void widgetSelected(SelectionEvent e)
/*     */                     {
/* 156 */                       MainWin.sendCommand(UITaskComposite.this.cmd, true);
/*     */                     }
/*     */                   });
/*     */               
/* 160 */               miSend.setText("Resend command");
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 165 */             MenuItem div = new MenuItem(menu, 2);
/*     */             
/* 167 */             if (UITaskComposite.this.getData("refreshinterval") != null)
/*     */             {
/* 169 */               if (UITaskComposite.this.stat == 0) {
/*     */                 
/* 171 */                 MenuItem miStop = new MenuItem(menu, 8);
/* 172 */                 miStop.setText("Stop auto refresh");
/* 173 */                 miStop.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                     {
/*     */                       public void widgetSelected(SelectionEvent e)
/*     */                       {
/* 177 */                         MainWin.taskman.updateTask(UITaskComposite.this.taskid, 1, null);
/*     */                       }
/*     */                     });
/*     */               } 
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 185 */             if (UITaskComposite.this.cmd.startsWith("/") || UITaskComposite.this.cmd.startsWith("dw") || UITaskComposite.this.cmd.startsWith("ui")) {
/*     */               
/* 187 */               div = new MenuItem(menu, 2);
/*     */               
/* 189 */               if (UITaskComposite.this.stat != 0) {
/*     */                 
/* 191 */                 MenuItem miRefresh = new MenuItem(menu, 8);
/* 192 */                 miRefresh.setText("Refresh");
/* 193 */                 miRefresh.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                     {
/*     */                       public void widgetSelected(SelectionEvent e)
/*     */                       {
/* 197 */                         MainWin.sendCommand(UITaskComposite.this.cmd, UITaskComposite.this.taskid, true);
/*     */                       }
/*     */                     });
/*     */ 
/*     */                 
/* 202 */                 MenuItem miStart = new MenuItem(menu, 8);
/* 203 */                 miStart.setText("Start auto refresh");
/* 204 */                 miStart.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                     {
/*     */                       public void widgetSelected(SelectionEvent e)
/*     */                       {
/* 208 */                         if (UITaskComposite.this.getData("refreshinterval") == null)
/*     */                         {
/* 210 */                           UITaskComposite.this.setData("refreshinterval", Integer.valueOf(30));
/*     */                         }
/*     */                         
/* 213 */                         MainWin.taskman.updateTask(UITaskComposite.this.taskid, 0, null);
/*     */                       }
/*     */                     });
/*     */               } 
/*     */             } 
/*     */             
/* 219 */             div = new MenuItem(menu, 2);
/*     */             
/* 221 */             if (UITaskComposite.this.stat != 0) {
/*     */               
/* 223 */               MenuItem miRemove = new MenuItem(menu, 8);
/* 224 */               miRemove.setText("Remove");
/* 225 */               miRemove.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                   {
/*     */                     public void widgetSelected(SelectionEvent e)
/*     */                     {
/* 229 */                       MainWin.taskman.removeTask(UITaskComposite.this.taskid);
/*     */                     }
/*     */                   });
/*     */             } 
/*     */             
/* 234 */             MenuItem miRemoveAll = new MenuItem(menu, 8);
/* 235 */             miRemoveAll.setText("Remove all items");
/* 236 */             miRemoveAll.addSelectionListener((SelectionListener)new SelectionAdapter()
/*     */                 {
/*     */                   public void widgetSelected(SelectionEvent e)
/*     */                   {
/* 240 */                     MainWin.taskman.removeAllTasks();
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(int stat) {
/* 252 */     this.stat = stat;
/*     */     
/* 254 */     this.status.redraw();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCommand(String text) {
/* 262 */     this.cmd = text;
/* 263 */     setDetailText();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDetails(String text) {
/* 269 */     if (text != null) {
/*     */       
/* 271 */       this.det = text;
/*     */     }
/*     */     else {
/*     */       
/* 275 */       this.det = "Null response";
/*     */     } 
/*     */     
/* 278 */     setDetailText();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void setDetailText() {
/* 284 */     this.details.setText(this.cmd.trim());
/* 285 */     int cmdend = this.cmd.trim().length();
/* 286 */     this.details.append(this.details.getLineDelimiter());
/*     */     
/* 288 */     StyleRange styleRange = new StyleRange();
/* 289 */     styleRange.start = 0;
/* 290 */     styleRange.length = cmdend;
/* 291 */     styleRange.foreground = MainWin.colorCmdTxt;
/* 292 */     this.details.setStyleRange(styleRange);
/*     */     
/* 294 */     this.details.append(this.det.trim());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 301 */     return Math.max(40, 8 + (this.details.getTextBounds(0, this.details.getCharCount() - 1)).height);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTop(int y) {
/* 307 */     setBounds(0, y, (getBounds()).width, (getBounds()).height);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBottom(int y) {
/* 313 */     setBounds(0, (getBounds()).y, (getBounds()).width, y - (getBounds()).y);
/*     */     
/* 315 */     this.details.setBounds(42, (this.details.getBounds()).y, (getBounds()).width - 44, (getBounds()).height - (this.details.getBounds()).y);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommand() {
/* 321 */     return this.cmd;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rotateActive() {
/* 327 */     if (this.activeframe < 31) {
/* 328 */       this.activeframe++;
/*     */     } else {
/* 330 */       this.activeframe = 1;
/*     */     } 
/* 332 */     if (this.status != null && !this.status.isDisposed())
/* 333 */       this.status.redraw(); 
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UITaskComposite.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */