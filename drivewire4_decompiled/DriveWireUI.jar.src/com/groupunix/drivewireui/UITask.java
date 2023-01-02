/*     */ package com.groupunix.drivewireui;
/*     */ 
/*     */ 
/*     */ public class UITask
/*     */ {
/*   6 */   private int status = 0;
/*     */   private UITaskComposite taskcomp;
/*   8 */   private String text = "";
/*   9 */   private int taskid = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   public UITask(int tid, UITaskComposite tc) {
/*  14 */     this.taskid = tid;
/*  15 */     this.taskcomp = tc;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatus(int status) {
/*  20 */     this.status = status;
/*  21 */     this.taskcomp.setStatus(status);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStatus() {
/*  26 */     return this.status;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setText(String text) {
/*  31 */     this.text = text;
/*  32 */     this.taskcomp.setDetails(text);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getText() {
/*  37 */     return this.text;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTaskcomp(UITaskComposite taskcomp) {
/*  42 */     this.taskcomp = taskcomp;
/*     */   }
/*     */ 
/*     */   
/*     */   public UITaskComposite getTaskcomp() {
/*  47 */     return this.taskcomp;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getHeight() {
/*  52 */     return this.taskcomp.getHeight();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTop(int y) {
/*  57 */     this.taskcomp.setTop(y);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBottom(int y) {
/*  62 */     this.taskcomp.setBottom(y);
/*     */   }
/*     */ 
/*     */   
/*     */   public void rotateActive() {
/*  67 */     if (this.taskcomp != null && !this.taskcomp.isDisposed()) {
/*     */       
/*  69 */       this.taskcomp.rotateActive();
/*     */       
/*  71 */       if (this.taskcomp.getData("refreshinterval") != null) {
/*     */         
/*  73 */         if (this.taskcomp.getData("refreshcount") == null) {
/*  74 */           this.taskcomp.setData("refreshcount", Integer.valueOf(0));
/*     */         }
/*  76 */         int rint = Integer.parseInt(this.taskcomp.getData("refreshinterval").toString());
/*  77 */         int rc = Integer.parseInt(this.taskcomp.getData("refreshcount").toString());
/*     */         
/*  79 */         if (rc < rint) {
/*     */           
/*  81 */           this.taskcomp.setData("refreshcount", Integer.valueOf(rc + 1));
/*     */         }
/*     */         else {
/*     */           
/*  85 */           String cmd = getCommand();
/*     */           
/*  87 */           if (cmd != null) {
/*     */             
/*  89 */             this.taskcomp.setRedraw(false);
/*     */             
/*  91 */             if (cmd.startsWith("/") || cmd.startsWith("dw") || cmd.startsWith("ui"))
/*     */             {
/*  93 */               MainWin.sendCommand(cmd, this.taskid, false);
/*     */             }
/*     */             
/*  96 */             this.taskcomp.setRedraw(true);
/*     */           } 
/*     */ 
/*     */           
/* 100 */           this.taskcomp.setData("refreshcount", Integer.valueOf(0));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCommand(String cmd) {
/* 110 */     if (this.taskcomp != null && !this.taskcomp.isDisposed())
/*     */     {
/* 112 */       this.taskcomp.setCommand(cmd);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCommand() {
/* 118 */     if (this.taskcomp != null && !this.taskcomp.isDisposed())
/*     */     {
/* 120 */       return this.taskcomp.getCommand();
/*     */     }
/*     */     
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTaskID() {
/* 128 */     return this.taskid;
/*     */   }
/*     */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/UITask.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */