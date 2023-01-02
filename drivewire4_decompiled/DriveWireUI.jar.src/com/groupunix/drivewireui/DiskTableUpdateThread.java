/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ 
/*    */ public class DiskTableUpdateThread
/*    */   implements Runnable
/*    */ {
/* 11 */   private LinkedBlockingQueue<DiskTableUpdate> updates = new LinkedBlockingQueue<DiskTableUpdate>();
/* 12 */   private Hashtable<Integer, Hashtable<String, Object>> colval = new Hashtable<Integer, Hashtable<String, Object>>();
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 17 */     Thread.currentThread().setName("dwuiDTUpdater-" + Thread.currentThread().getId());
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 22 */     while (!MainWin.shell.isDisposed()) {
/*    */ 
/*    */       
/*    */       try {
/*    */         
/* 27 */         DiskTableUpdate dtu = this.updates.take();
/*    */ 
/*    */         
/* 30 */         if (!this.colval.containsKey(Integer.valueOf(dtu.getDisk()))) {
/* 31 */           this.colval.put(Integer.valueOf(dtu.getDisk()), new Hashtable<String, Object>());
/*    */         }
/* 33 */         ((Hashtable<String, Object>)this.colval.get(Integer.valueOf(dtu.getDisk()))).put(dtu.getKey(), dtu.getValue());
/*    */ 
/*    */         
/* 36 */         if (this.updates.size() == 0)
/*    */         {
/* 38 */           for (Map.Entry<Integer, Hashtable<String, Object>> diskentry : this.colval.entrySet()) {
/*    */             
/* 40 */             for (Map.Entry<String, Object> param : (Iterable<Map.Entry<String, Object>>)((Hashtable)diskentry.getValue()).entrySet())
/*    */             {
/*    */               
/* 43 */               MainWin.updateDiskTableItem(((Integer)diskentry.getKey()).intValue(), param.getKey(), param.getValue());
/*    */             }
/*    */ 
/*    */             
/* 47 */             ((Hashtable)diskentry.getValue()).clear();
/*    */           } 
/*    */ 
/*    */           
/* 51 */           this.colval.clear();
/*    */         
/*    */         }
/*    */       
/*    */       }
/* 56 */       catch (InterruptedException e) {
/*    */ 
/*    */         
/* 59 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 64 */     System.out.println("dtu die");
/*    */   }
/*    */ 
/*    */   
/*    */   public void addUpdate(int disk, String key, String val) {
/* 69 */     this.updates.add(new DiskTableUpdate(disk, key, val));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addUpdate(int disk, String key, Image val) {
/* 75 */     this.updates.add(new DiskTableUpdate(disk, key, val));
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DiskTableUpdateThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */