/*    */ package com.groupunix.drivewireserver.dwdisk;
/*    */ 
/*    */ import com.groupunix.drivewireserver.DriveWireServer;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class DWDiskLazyWriter
/*    */   implements Runnable
/*    */ {
/*  9 */   private static final Logger logger = Logger.getLogger("DWServer.DWProtoReader");
/*    */   
/*    */   private boolean wanttodie = false;
/*    */   private boolean inSync = false;
/*    */   
/*    */   public void run() {
/* 15 */     Thread.currentThread().setPriority(1);
/* 16 */     Thread.currentThread().setName("dskwriter-" + Thread.currentThread().getId());
/*    */     
/* 18 */     logger.debug("started, write interval is " + DriveWireServer.serverconfig.getLong("DiskLazyWriteInterval", 15000L));
/*    */     
/* 20 */     while (!this.wanttodie) {
/*    */ 
/*    */       
/*    */       try {
/*    */ 
/*    */         
/* 26 */         Thread.sleep(DriveWireServer.serverconfig.getLong("DiskLazyWriteInterval", 5000L));
/* 27 */         syncDisks();
/*    */       }
/* 29 */       catch (InterruptedException e) {
/*    */         
/* 31 */         logger.debug("interrupted");
/* 32 */         this.wanttodie = true;
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 37 */     logger.debug("exit");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void syncDisks() {
/* 45 */     this.inSync = true;
/* 46 */     for (int h = 0; h < DriveWireServer.getNumHandlers(); h++) {
/*    */ 
/*    */       
/* 49 */       if (DriveWireServer.handlerIsAlive(h))
/*    */       {
/* 51 */         DriveWireServer.getHandler(h).syncStorage();
/*    */       }
/*    */     } 
/*    */     
/* 55 */     this.inSync = false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isInSync() {
/* 62 */     return this.inSync;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwdisk/DWDiskLazyWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */