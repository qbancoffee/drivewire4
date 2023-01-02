/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import com.swtdesigner.SWTResourceManager;
/*     */ 
/*     */ 
/*     */ public class UITaskMaster
/*     */ {
/*     */   public static final int TASK_STATUS_ACTIVE = 0;
/*     */   public static final int TASK_STATUS_COMPLETE = 1;
/*     */   public static final int TASK_STATUS_FAILED = 2;
/*     */   static Font taskFont;
/*     */   static Font versionFont;
/*  19 */   private int nexttaskno = 0;
/*     */   
/*     */   private Composite master;
/*  22 */   private List<UITask> tasks = new ArrayList<UITask>();
/*     */ 
/*     */   
/*     */   public UITaskMaster(Composite master) {
/*  26 */     this.master = master;
/*     */     
/*  28 */     HashMap<String, Integer> fontmap = new HashMap<String, Integer>();
/*     */     
/*  30 */     fontmap.put("Droid Sans Mono", Integer.valueOf(0));
/*     */     
/*  32 */     taskFont = UIUtils.findFont(master.getDisplay(), fontmap, "4.0.0", 36, 17);
/*     */     
/*  34 */     fontmap.clear();
/*  35 */     fontmap.put("Roboto Cn", Integer.valueOf(0));
/*  36 */     fontmap.put("Roboto", Integer.valueOf(3));
/*  37 */     fontmap.put("Roboto", Integer.valueOf(0));
/*  38 */     versionFont = UIUtils.findFont(master.getDisplay(), fontmap, "4.0.0", 24, 15);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int addTask(final String cmd) {
/*  48 */     this.master.getDisplay().syncExec(new Runnable()
/*     */         {
/*     */ 
/*     */ 
/*     */           
/*     */           public void run()
/*     */           {
/*  55 */             if (UITaskMaster.this.tasks.size() > MainWin.config.getInt("DWOpsHistorySize", 20))
/*     */             {
/*  57 */               UITaskMaster.this.tasks.remove(0);
/*     */             }
/*     */ 
/*     */             
/*  61 */             synchronized (UITaskMaster.this.tasks) {
/*     */               UITaskComposite tc;
/*  63 */               UITaskMaster.this.nexttaskno++;
/*     */               
/*  65 */               if (cmd.equals("/splash")) {
/*     */                 
/*  67 */                 tc = new UITaskCompositeSplash(UITaskMaster.this.master, 536870912, UITaskMaster.this.nexttaskno);
/*     */               
/*     */               }
/*  70 */               else if (cmd.equals("/wizard")) {
/*     */                 
/*  72 */                 tc = new UITaskCompositeWizard(UITaskMaster.this.master, 536870912, UITaskMaster.this.nexttaskno);
/*     */               }
/*     */               else {
/*     */                 
/*  76 */                 UITaskMaster.this.master.setRedraw(false);
/*  77 */                 tc = new UITaskComposite(UITaskMaster.this.master, 536870912, UITaskMaster.this.nexttaskno);
/*     */                 
/*  79 */                 tc.setBounds(0, UITaskMaster.this.tasks.size() * 40, (UITaskMaster.this.master.getClientArea()).width, 40);
/*     */               } 
/*     */ 
/*     */               
/*  83 */               tc.setCommand(cmd);
/*     */               
/*  85 */               UITask task = new UITask(UITaskMaster.this.nexttaskno, tc);
/*  86 */               UITaskMaster.this.tasks.add(task);
/*     */             } 
/*     */             
/*  89 */             UITaskMaster.this.master.setRedraw(true);
/*     */ 
/*     */             
/*  92 */             if (MainWin.tabFolderOutput.getSelectionIndex() != 0)
/*     */             {
/*  94 */               MainWin.tabFolderOutput.getItems()[0].setImage(SWTResourceManager.getImage(MainWin.class, "/logging/info.png"));
/*     */             }
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 101 */     return this.nexttaskno;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void resizeTasks() {
/* 107 */     int y = 0;
/*     */     
/* 109 */     for (UITask t : this.tasks) {
/*     */       
/* 111 */       if (t.getTaskcomp() != null && !t.getTaskcomp().isDisposed()) {
/*     */         
/* 113 */         t.getTaskcomp().setRedraw(false);
/* 114 */         t.setTop(y);
/* 115 */         y += t.getHeight();
/*     */         
/* 117 */         t.setBottom(y);
/*     */       } 
/*     */     } 
/*     */     
/* 121 */     this.master.setBounds(0, 0, (this.master.getBounds()).width, y);
/*     */     
/* 123 */     MainWin.scrolledComposite.setOrigin(0, y);
/*     */     
/* 125 */     for (UITask t : this.tasks) {
/*     */       
/* 127 */       if (t.getTaskcomp() != null && !t.getTaskcomp().isDisposed())
/*     */       {
/* 129 */         t.getTaskcomp().setRedraw(true);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public UITask getTask(int tid) throws DWUINoSuchTaskException {
/* 136 */     for (UITask t : this.tasks) {
/*     */       
/* 138 */       if (t.getTaskID() == tid)
/*     */       {
/* 140 */         return t;
/*     */       }
/*     */     } 
/*     */     
/* 144 */     throw new DWUINoSuchTaskException("No task id " + tid);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateTask(final int tid, final int status, final String txt) {
/* 149 */     this.master.getDisplay().syncExec(new Runnable()
/*     */         {
/*     */           public void run() {
/* 152 */             MainWin.scrolledComposite.setRedraw(false);
/*     */ 
/*     */ 
/*     */             
/*     */             try {
/* 157 */               UITask t = UITaskMaster.this.getTask(tid);
/*     */               
/* 159 */               t.getTaskcomp().setRedraw(false);
/*     */               
/* 161 */               if (txt != null) {
/* 162 */                 t.setText(txt);
/*     */               }
/* 164 */               t.setStatus(status);
/*     */               
/* 166 */               t.getTaskcomp().setRedraw(true);
/*     */             }
/* 168 */             catch (DWUINoSuchTaskException e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 173 */             UITaskMaster.this.resizeTasks();
/* 174 */             MainWin.scrolledComposite.setRedraw(true);
/*     */             
/* 176 */             if (MainWin.tabFolderOutput.getSelectionIndex() != 0) {
/*     */ 
/*     */               
/* 179 */               switch (status) {
/*     */                 
/*     */                 case 0:
/* 182 */                   MainWin.tabFolderOutput.getItems()[0].setImage(SWTResourceManager.getImage(MainWin.class, "/status/active_16.png"));
/*     */                   return;
/*     */                 case 2:
/* 185 */                   MainWin.tabFolderOutput.getItems()[0].setImage(SWTResourceManager.getImage(MainWin.class, "/status/failed_16.png"));
/*     */                   return;
/*     */                 case 1:
/* 188 */                   MainWin.tabFolderOutput.getItems()[0].setImage(SWTResourceManager.getImage(MainWin.class, "/status/completed_16.png"));
/*     */                   return;
/*     */               } 
/* 191 */               MainWin.tabFolderOutput.getItems()[0].setImage(SWTResourceManager.getImage(MainWin.class, "/status/unknown_16.png"));
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumTasks() {
/* 202 */     return this.tasks.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font taskFont() {
/* 209 */     return taskFont;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTask(int tid) {
/* 215 */     MainWin.shell.setRedraw(false);
/*     */     
/* 217 */     int deltid = -1;
/* 218 */     int curtid = 0;
/*     */     
/* 220 */     for (UITask t : this.tasks) {
/*     */ 
/*     */       
/* 223 */       if (t.getTaskID() == tid) {
/*     */         
/* 225 */         deltid = curtid;
/* 226 */         if (t.getTaskcomp() != null && !t.getTaskcomp().isDisposed())
/*     */         {
/* 228 */           t.getTaskcomp().dispose();
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 233 */       curtid++;
/*     */     } 
/*     */     
/* 236 */     if (deltid > -1) {
/*     */       
/* 238 */       this.tasks.remove(deltid);
/* 239 */       resizeTasks();
/*     */     } 
/*     */     
/* 242 */     MainWin.shell.setRedraw(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasTask(int tid) {
/* 248 */     for (UITask t : this.tasks) {
/*     */       
/* 250 */       if (t.getTaskID() == tid)
/*     */       {
/* 252 */         return true;
/*     */       }
/*     */     } 
/* 255 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rotateWaiters() {
/* 261 */     for (UITask t : this.tasks) {
/*     */       
/* 263 */       if (t.getStatus() == 0)
/*     */       {
/* 265 */         t.rotateActive();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Composite getMaster() {
/* 274 */     return this.master;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<UITask> getTasks() {
/* 280 */     return this.tasks;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAllTasks() {
/* 286 */     Vector<Integer> tids = new Vector<Integer>();
/*     */     
/* 288 */     for (UITask t : this.tasks) {
/*     */       
/* 290 */       if (t.getStatus() != 0)
/*     */       {
/* 292 */         tids.add(Integer.valueOf(t.getTaskID()));
/*     */       }
/*     */     } 
/*     */     
/* 296 */     for (Integer tid : tids)
/*     */     {
/* 298 */       removeTask(tid.intValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UITaskMaster.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */