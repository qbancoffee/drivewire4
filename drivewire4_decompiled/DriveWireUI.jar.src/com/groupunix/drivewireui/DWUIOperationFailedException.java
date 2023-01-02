/*    */ package com.groupunix.drivewireui;
/*    */ 
/*    */ 
/*    */ public class DWUIOperationFailedException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DWUIOperationFailedException(String msg) {
/* 10 */     super(msg);
/*    */   }
/*    */ 
/*    */   
/*    */   public DWUIOperationFailedException(byte err) {
/* 15 */     super(MainWin.errorHelpCache.getErrMessage(0xFF & err));
/*    */   }
/*    */ 
/*    */   
/*    */   public DWUIOperationFailedException(byte err, String msg) {
/* 20 */     super(MainWin.errorHelpCache.getErrMessage(0xFF & err) + ": " + msg);
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireui/DWUIOperationFailedException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */