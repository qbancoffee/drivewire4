/*    */ package com.groupunix.drivewireserver.dwcommands;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DWCommandResponse
/*    */ {
/*    */   private boolean success;
/*    */   private byte responseCode;
/*    */   private String responseText;
/*    */   private byte[] responseBytes;
/*    */   private boolean usebytes = false;
/*    */   
/*    */   public DWCommandResponse(boolean success, byte responsecode, String responsetext) {
/* 15 */     this.success = success;
/* 16 */     this.responseCode = responsecode;
/* 17 */     this.responseText = responsetext;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse(String responsetext) {
/* 23 */     this.success = true;
/* 24 */     this.responseCode = 0;
/* 25 */     this.responseText = responsetext;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DWCommandResponse(byte[] responsebytes) {
/* 31 */     this.success = true;
/* 32 */     this.responseCode = 0;
/* 33 */     this.responseBytes = responsebytes;
/* 34 */     this.usebytes = true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getSuccess() {
/* 40 */     return this.success;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getResponseCode() {
/* 45 */     return this.responseCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getResponseText() {
/* 50 */     return this.responseText;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getResponseBytes() {
/* 55 */     return this.responseBytes;
/*    */   }
/*    */   
/*    */   public boolean isUsebytes() {
/* 59 */     return this.usebytes;
/*    */   }
/*    */ }


/* Location:              drivewireserver-git/DriveWireUI/DriveWireUI.jar!/com/groupunix/drivewireserver/dwcommands/DWCommandResponse.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */