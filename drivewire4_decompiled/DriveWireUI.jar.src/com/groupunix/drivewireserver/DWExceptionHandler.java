/*    */ package com.groupunix.drivewireserver;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWExceptionHandler
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/*    */   public void uncaughtException(Thread thread, Throwable thrw) {
/* 11 */     DriveWireServer.handleUncaughtException(thread, thrw);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/DWExceptionHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */