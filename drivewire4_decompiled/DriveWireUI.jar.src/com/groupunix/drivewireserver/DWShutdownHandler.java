/*   */ package com.groupunix.drivewireserver;
/*   */ 
/*   */ public class DWShutdownHandler
/*   */   extends Thread
/*   */ {
/*   */   public void run() {
/* 7 */     Thread.currentThread().setName("shutdown-" + Thread.currentThread().getId());
/*   */     
/* 9 */     DriveWireServer.serverShutdown();
/*   */   }
/*   */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWShutdownHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */