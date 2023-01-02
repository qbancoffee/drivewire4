/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ public class DWUINoSuchTaskException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DWUINoSuchTaskException(String msg) {
/* 10 */     super(msg);
/*    */   }
/*    */ 
/*    */   
/*    */   public DWUINoSuchTaskException(byte err) {
/* 15 */     super(MainWin.errorHelpCache.getErrMessage(0xFF & err));
/*    */   }
/*    */ 
/*    */   
/*    */   public DWUINoSuchTaskException(byte err, String msg) {
/* 20 */     super(MainWin.errorHelpCache.getErrMessage(0xFF & err) + ": " + msg);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWUINoSuchTaskException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */